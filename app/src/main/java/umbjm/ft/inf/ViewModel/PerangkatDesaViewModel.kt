package com.example.elaporadmin.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.elaporadmin.retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import umbjm.ft.inf.dao.Perangkatdesa

class PerangkatDesaViewModel:ViewModel() {
//    private var perangkatDesaLiveData = MutableLiveData<List<Perangkatdesa>>()
//    private val pesanLiveData = MutableLiveData<String>()
//
//    fun getPerangkatDesa(){
//        ApiService.endPoint.getPerangkatDesa()
//            .enqueue(object:Callback<ResponsePerangkatdesa>{
//                override fun onResponse(
//                    call: Call<ResponsePerangkatdesa>,
//                    response: Response<ResponsePerangkatdesa>
//                ) {
//                    perangkatDesaLiveData.value = response.body()!!.data
//                }
//
//                override fun onFailure(call: Call<ResponsePerangkatdesa>, t: Throwable) {
//                   Log.d("TAG", t.message.toString())
//                }
//
//            })
//    }
//
//    fun insertPerangkatDesa(nik:String, namapd:String, kelurahan_id:Int,email: String, password: String){
//        ApiService.endPoint.insertPerangkatDesa(
//            nik, namapd, kelurahan_id, email, password
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
//    fun updatePerangkatDesa(nik:String, namapd:String, kelurahan_id:Int,email: String, password: String){
//        ApiService.endPoint.updatePerangkatDesa(
//            nik, namapd, kelurahan_id, email, password
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
//    fun deletePerangkatDesa(nik: String){
//
//        ApiService.endPoint.deletePerangkatDesa(
//            nik
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
//
//    fun observePerangkatDesaLiveData():LiveData<List<Perangkatdesa>>{
//        return perangkatDesaLiveData
//    }
//
//    fun observePesanLiveData():LiveData<String>{
//        return pesanLiveData
//    }
}