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
import retrofit2.http.Field
import umbjm.ft.inf.dao.Pengaduan
import umbjm.ft.inf.dao.ResponsePengaduan

class PengaduanLainViewModel:ViewModel() {
    private var pengaduanLiveData = MutableLiveData<List<Pengaduan>>()
    private val pesanLiveData = MutableLiveData<String>()

    fun getPengaduanLain(){
        viewModelScope.launch{
            val response = ApiService.api.getPengaduanLain()
            if (response.isSuccessful){
                withContext(Dispatchers.Main){
                    pengaduanLiveData.value = response.body()!!.data
                }
            }
        }


//        ApiService.api.getPengaduanLain()
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

    fun getPengaduanLainByToken(token:String){

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

    fun insertPengaduanLain(
        judulpengaduan:String,
        nama:String,
        telp:String,
        namalokasi:String,
        alamat:String,
        latitude:String,
        longitude:String,
        isipengaduan:String,
        tanggalpengaduan:String,
        foto:String,
        kelurahan_id:Int,
    ){
        ApiService.api.insertPengaduanLain(
            judulpengaduan,
            nama,
            telp,
            namalokasi,
            alamat,
            latitude,
            longitude,
            isipengaduan,
            tanggalpengaduan,
            foto,
            kelurahan_id,
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