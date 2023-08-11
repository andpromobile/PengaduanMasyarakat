package com.example.elaporadmin.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.elaporadmin.retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import umbjm.ft.inf.dao.Lokasi
import umbjm.ft.inf.dao.ResponseLokasi

class LokasiViewModel: ViewModel() {
    private var lokasiLiveData = MutableLiveData<List<Lokasi>>()
    private val pesanLiveData = MutableLiveData<String>()

    fun getLokasi(){

        ApiService.endPoint.getLokasi()
            .enqueue(object: Callback<ResponseLokasi> {
                override fun onResponse(
                    call: Call<ResponseLokasi>,
                    response: Response<ResponseLokasi>
                ) {

                    if (response.body()!=null){
                        lokasiLiveData.value = response.body()!!.data

                        Log.d("HASIL LOKASI",lokasiLiveData.value.toString())
                    }
                }

                override fun onFailure(call: Call<ResponseLokasi>, t: Throwable) {
                    Log.d("TAG ON Failure",t.message.toString())
                }

            })
    }

    fun getLokasiByBidang(id:Int){

        ApiService.endPoint.getLokasiByBidang(id)
            .enqueue(object: Callback<ResponseLokasi> {
                override fun onResponse(
                    call: Call<ResponseLokasi>,
                    response: Response<ResponseLokasi>
                ) {

                    if (response.body()!=null){
                        lokasiLiveData.value = response.body()!!.data

                        Log.d("HASIL LOKASI",lokasiLiveData.value.toString())
                    }
                }

                override fun onFailure(call: Call<ResponseLokasi>, t: Throwable) {
                    Log.d("TAG ON Failure",t.message.toString())
                }

            })
    }

    fun insertLokasi(
        datalokasi:String,
        latitude:Int,
        longitude:Int,
        foto: String    ){
        ApiService.endPoint.insertLokasi(
            datalokasi,
            latitude,
            longitude,
            foto
        )
            .enqueue(object: Callback<SubmitModel>{
                override fun onResponse(
                    call: Call<SubmitModel>,
                    response: Response<SubmitModel>
                ) {
                    if (response.isSuccessful){
                        pesanLiveData.value = response.body()!!.message
                    }
                }

                override fun onFailure(
                    call: Call<SubmitModel>,
                    t: Throwable
                ) {
                    pesanLiveData.value = t.toString()
                }
            })
    }

    fun updateLokasi(id:Int,
                      datalokasi:String,
                      latitude:Int,
                      longitude:Int,
                      foto: String){

        ApiService.endPoint.updateLokasi(
            id,
            datalokasi,
            latitude,
            longitude,
            foto,
        )
            .enqueue(object: Callback<SubmitModel>{
                override fun onResponse(
                    call: Call<SubmitModel>,
                    response: Response<SubmitModel>
                ) {
                    if (response.isSuccessful){
                        pesanLiveData.value = response.body()!!.message
                    }
                }

                override fun onFailure(
                    call: Call<SubmitModel>,
                    t: Throwable
                ) {
                    pesanLiveData.value = t.toString()
                }
            })
    }

    fun deleteLokasi(id:Int){

        ApiService.endPoint.deleteLokasi(
            id
        ).enqueue(object:Callback<SubmitModel>{
            override fun onResponse(
                call: Call<SubmitModel>,
                response: Response<SubmitModel>
            ) {
                if (response.isSuccessful){
                    pesanLiveData.value = response.body()!!.message
                }
            }

            override fun onFailure(call: Call<SubmitModel>, t: Throwable) {
                pesanLiveData.value = t.toString()
            }

        })
    }
    fun observeLokasiLiveData():LiveData<List<Lokasi>>{
        return lokasiLiveData
    }

    fun observePesanLiveData():LiveData<String>{
        return pesanLiveData
    }
}