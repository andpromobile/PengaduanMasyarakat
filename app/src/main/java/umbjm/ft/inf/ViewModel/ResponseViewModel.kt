package com.example.elaporadmin.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import umbjm.ft.inf.dao.Bidang
import umbjm.ft.inf.dao.ResponseBidang
import com.example.elaporadmin.retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResponseViewModel: ViewModel() {
    private var bidangLiveData = MutableLiveData<List<Bidang>>()
    private val pesanLiveData = MutableLiveData<String>()

    fun getBidang() {
        ApiService.endPoint.getBidang()
            .enqueue(object  : Callback<ResponseBidang> {
            override fun onResponse(
                call: Call<ResponseBidang>,
                response: Response<ResponseBidang>,
            ) {

                if (response.body()!=null){
                    bidangLiveData.value = response.body()!!.data

                    Log.d("HASIL BIDANG",bidangLiveData.value.toString())
                }
                else{
//                    Log.d("HASIL BIDANG",bidangLiveData.value.toString())
//                    return
                }

//                Log.d("HASIL BIDANG","TES AJA")
            }
            override fun onFailure(call: Call<ResponseBidang>, t: Throwable) {
                Log.d("TAG",t.message.toString())
            }
        })
    }

//    fun insertBidang(namabidang:String, seksi:String){
//        ApiService.endPoint.insertBidang(
//            namabidang, seksi
//        )
//            .enqueue(object: Callback<SubmitModel>{
//                override fun onResponse(
//                    call: Call<SubmitModel>,
//                    response: Response<SubmitModel>
//                ) {
//                   if (response.isSuccessful){
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
//    fun updateBidang(id:Int, namabidang:String, seksi: String){
//        ApiService.endPoint.updateBidang(
//            id, namabidang, seksi
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
//    fun deleteBidang(id:Int){
//
//        ApiService.endPoint.deleteBidang(
//            id
//        ).enqueue(object:Callback<SubmitModel>{
//            override fun onResponse(
//                call: Call<SubmitModel>,
//                response: Response<SubmitModel>
//            ) {
//                Log.d("ERROR", "ERROR JER")
//                if (response.isSuccessful){
//                    pesanLiveData.value = response.body()!!.message
//                }
//            }
//
//            override fun onFailure(call: Call<SubmitModel>, t: Throwable) {
//                pesanLiveData.value = t.toString()
//
//                Log.d("error", t.toString())
//            }
//
//        })
//    }

    fun observeBidangLiveData() : LiveData<List<Bidang>> {
        return bidangLiveData
    }

    fun observePesanLiveData():LiveData<String>{
        return pesanLiveData
    }
}