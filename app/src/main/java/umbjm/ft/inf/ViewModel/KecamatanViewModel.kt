package com.example.elaporadmin.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.elaporadmin.retrofit.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import umbjm.ft.inf.dao.Kecamatan
import umbjm.ft.inf.dao.Kelurahan
import umbjm.ft.inf.dao.ResponseKecamatan
import umbjm.ft.inf.dao.ResponseKelurahan

class KecamatanViewModel:ViewModel() {
    private var kecamatanLiveData = MutableLiveData<List<Kecamatan>>()
    private val pesanLiveData = MutableLiveData<String>()

    fun getKecamatan() {
        GlobalScope.launch(Dispatchers.IO){
            val response = ApiService.api.getKecamatan()
            if (response.isSuccessful){
                withContext(Dispatchers.Main){
                    kecamatanLiveData.value = response.body()!!.data
                }
            }
        }
//        ApiService.api.getKecamatan()
//            .enqueue(object  : Callback<ResponseKecamatan> {
//                override fun onResponse(
//                    call: Call<ResponseKecamatan>,
//                    response: Response<ResponseKecamatan>,
//                ) {
//                    if (response.body()!=null){
//                        kecamatanLiveData.value = response.body()!!.data
//                    }
//                    else{
//                        return
//                    }
//                }
//                override fun onFailure(call: Call<ResponseKecamatan>, t: Throwable) {
//                    Log.d("TAG",t.message.toString())
//                }
//            })
    }

//    fun getKelurahanByLokasi(id:Int) {
//        ApiService.endPoint.getKelurahanByLokasi(id)
//            .enqueue(object  : Callback<ResponseKecamatan> {
//                override fun onResponse(
//                    call: Call<ResponseKecamatan>,
//                    response: Response<ResponseKecamatan>,
//                ) {
//                    if (response.body()!=null){
//                        kecamatanLiveData.value = response.body()!!.data
//                    }
//                    else{
//                        return
//                    }
//                }
//                override fun onFailure(call: Call<ResponseKecamatan>, t: Throwable) {
//                    Log.d("TAG",t.message.toString())
//                }
//            })
//    }

//    fun insertKelurahan(namakelurahan:String, namakecamatan:String){
//        ApiService.endPoint.insertKelurahan(
//            namakelurahan, namakecamatan
//        )
//            .enqueue(object: Callback<SubmitModel>{
//                override fun onResponse(
//                    call: Call<SubmitModel>,
//                    response: Response<SubmitModel>
//                ) {
//                    if (response.isSuccessful){
//                        pesanLiveData.value = response.body()!!.message
//                    }
//                }
//
//                override fun onFailure(
//                    call: Call<SubmitModel>,
//                    t: Throwable
//                ) {
//                    pesanLiveData.value = t.toString()
//                }
//            })
//    }
//
//    fun updateKelurahan(id:Int, namakelurahan:String, namakecamatan: String){
//        ApiService.endPoint.updateKelurahan(
//            id, namakelurahan, namakecamatan
//        )
//            .enqueue(object: Callback<SubmitModel>{
//                override fun onResponse(
//                    call: Call<SubmitModel>,
//                    response: Response<SubmitModel>
//                ) {
//                    if (response.isSuccessful){
//                        pesanLiveData.value = response.body()!!.message
//                    }
//                }
//
//                override fun onFailure(
//                    call: Call<SubmitModel>,
//                    t: Throwable
//                ) {
//                    pesanLiveData.value = t.toString()
//                }
//            })
//    }
//
//    fun deleteKelurahan(id:Int){
//
//        ApiService.endPoint.deleteKelurahan(
//            id
//        ).enqueue(object:Callback<SubmitModel>{
//            override fun onResponse(
//                call: Call<SubmitModel>,
//                response: Response<SubmitModel>
//            ) {
//                if (response.isSuccessful){
//                    pesanLiveData.value = response.body()!!.message
//                }
//            }
//
//            override fun onFailure(call: Call<SubmitModel>, t: Throwable) {
//                pesanLiveData.value = t.toString()
//            }
//
//        })
//    }

    fun observeKecamatanLiveData() : LiveData<List<Kecamatan>> {
        return kecamatanLiveData
    }

    fun observePesanLiveData():LiveData<String>{
        return pesanLiveData
    }
}