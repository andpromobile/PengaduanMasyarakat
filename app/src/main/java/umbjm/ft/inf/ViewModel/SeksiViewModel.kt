package com.example.elaporadmin.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.elaporadmin.retrofit.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import umbjm.ft.inf.dao.Bidang
import umbjm.ft.inf.dao.ResponseBidang
import umbjm.ft.inf.dao.Seksi

class SeksiViewModel: ViewModel() {
    private var seksiLiveData = MutableLiveData<List<Seksi>>()
    private val pesanLiveData = MutableLiveData<String>()

    fun getSeksi() {
        viewModelScope.launch {
            val response = ApiService.api.getSeksi()
            if (response.isSuccessful) {
                seksiLiveData.value = response.body()!!.data
            }
        }
    }

    fun observeSeksiLiveData() : LiveData<List<Seksi>> {
        return seksiLiveData
    }

    fun observePesanLiveData():LiveData<String>{
        return pesanLiveData
    }
}