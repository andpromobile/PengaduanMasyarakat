package com.example.elaporadmin.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.elaporadmin.retrofit.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import umbjm.ft.inf.dao.Pengaduan
import umbjm.ft.inf.dao.ResponsePengaduan

class PengaduanViewModel:ViewModel() {
    private var pengaduanLiveData = MutableLiveData<List<Pengaduan>>()
    private val pesanLiveData = MutableLiveData<String>()

    fun getPengaduan(){

        ApiService.api.getPengaduan()
            .enqueue(object :Callback<ResponsePengaduan>{
                override fun onResponse(
                    call: Call<ResponsePengaduan>,
                    response: Response<ResponsePengaduan>
                ) {
                    pengaduanLiveData.value = response.body()!!.data
                }

                override fun onFailure(call: Call<ResponsePengaduan>, t: Throwable) {
                    Log.d("TAG", t.message.toString())
                }

            })
    }

    fun getPengaduanByToken(token:String){

        viewModelScope.launch{
             val response = ApiService.api.getPengaduanByToken(token)
            if (response.isSuccessful){
                withContext(Dispatchers.Main){
                    pengaduanLiveData.value = response.body()!!.data
                }
            }
        }

//        ApiService.api.getPengaduanByToken(token)
//            .enqueue(object :Callback<ResponsePengaduan>{
//                override fun onResponse(
//                    call: Call<ResponsePengaduan>,
//                    response: Response<ResponsePengaduan>
//                ) {
//                    pengaduanLiveData.value = response.body()!!.data
//                }
//
//                override fun onFailure(call: Call<ResponsePengaduan>, t: Throwable) {
//                    Log.d("TAG", t.message.toString())
//                }
//
//            })
    }

    fun insertPengaduan(
        judulpengaduan:String,
        nama:String,
        telp:String,
        isipengaduan:String,
        tanggalpengaduan:String,
        foto:String,
        lokasi_id:Int,
        kelurahan_id:Int,
        bidang_id:Int,
        kecamatan_id:Int,
    ){
        ApiService.api.insertPengaduan(
            judulpengaduan,
            nama,
            telp,
            isipengaduan,
            tanggalpengaduan,
            foto,
            lokasi_id,
            kelurahan_id,
            bidang_id,
            kecamatan_id,
        ).enqueue(object:Callback<SubmitModel>{
            override fun onResponse(call: Call<SubmitModel>, response: Response<SubmitModel>) {
                pesanLiveData.value = response.body()?.message
            }

            override fun onFailure(call: Call<SubmitModel>, t: Throwable) {
                Log.d("TAG", t.message.toString())
            }

        })
    }

    fun observePengaduanLiveData():LiveData<List<Pengaduan>>{
        return pengaduanLiveData
    }

    fun observePesanLiveData():LiveData<String>{
        return pesanLiveData
    }

}