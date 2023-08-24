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
import umbjm.ft.inf.dao.Kelurahan
import umbjm.ft.inf.dao.ResponseKelurahan

class KelurahanViewModel:ViewModel() {
    private var kelurahanLiveData = MutableLiveData<List<Kelurahan>>()
    private val pesanLiveData = MutableLiveData<String>()

    fun getKelurahan() {
        ApiService.api.getKelurahan()
            .enqueue(object  : Callback<ResponseKelurahan> {
                override fun onResponse(
                    call: Call<ResponseKelurahan>,
                    response: Response<ResponseKelurahan>,
                ) {
                    if (response.body()!=null){
                        kelurahanLiveData.value = response.body()!!.data
                    }
                    else{
                        return
                    }
                }
                override fun onFailure(call: Call<ResponseKelurahan>, t: Throwable) {
                    Log.d("TAG",t.message.toString())
                }
            })
    }

    fun getKelurahanByLokasi(id:Int) {
        GlobalScope.launch (Dispatchers.IO){
            val response = ApiService.api.getKelurahanByLokasi(id)
            if (response.isSuccessful){
                withContext(Dispatchers.Main){
                    kelurahanLiveData.value = response.body()!!.data
                }
            }
        }

//        ApiService.api.getKelurahanByLokasi(id)
//            .enqueue(object  : Callback<ResponseKelurahan> {
//                override fun onResponse(
//                    call: Call<ResponseKelurahan>,
//                    response: Response<ResponseKelurahan>,
//                ) {
//                    if (response.body()!=null){
//                        kelurahanLiveData.value = response.body()!!.data
//                    }
//                    else{
//                        return
//                    }
//                }
//                override fun onFailure(call: Call<ResponseKelurahan>, t: Throwable) {
//                    Log.d("TAG",t.message.toString())
//                }
//            })
    }

    fun getKelurahanByKecamatan(id:Int) {

        GlobalScope.launch (Dispatchers.IO){
            val response = ApiService.api.getKelurahanByKecamatan(id)
            if (response.isSuccessful){
                withContext(Dispatchers.Main){
                    kelurahanLiveData.value = response.body()!!.data
                }
            }
        }

//        ApiService.api.getKelurahanByKecamatan(id)
//            .enqueue(object  : Callback<ResponseKelurahan> {
//                override fun onResponse(
//                    call: Call<ResponseKelurahan>,
//                    response: Response<ResponseKelurahan>,
//                ) {
//                    if (response.body()!=null){
//                        kelurahanLiveData.value = response.body()!!.data
//                    }
//                    else{
//                        return
//                    }
//                }
//                override fun onFailure(call: Call<ResponseKelurahan>, t: Throwable) {
//                    Log.d("TAG",t.message.toString())
//                }
//            })
    }

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

    fun observeKelurahanLiveData() : LiveData<List<Kelurahan>> {
        return kelurahanLiveData
    }

    fun observePesanLiveData():LiveData<String>{
        return pesanLiveData
    }
}