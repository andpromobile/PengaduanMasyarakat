package com.example.elaporadmin.retrofit

import com.example.elaporadmin.ViewModel.SubmitModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import umbjm.ft.inf.dao.ResponseBidang
import umbjm.ft.inf.dao.ResponseKecamatan
import umbjm.ft.inf.dao.ResponseKelurahan
import umbjm.ft.inf.dao.ResponseLokasi
import umbjm.ft.inf.dao.ResponsePengaduan

interface ApiEndPoint {

    @GET("dtxpengaduan")
    fun getPengaduan(): Call<ResponsePengaduan>

    @GET("dtxpengaduan/getByToken/{token}")
    fun getPengaduanByToken(
        @Path("token") token:String,
    ): Call<ResponsePengaduan>

    @GET("dtxkecamatan")
    fun getKecamatan(): Call<ResponseKecamatan>

    @GET("dtxkelurahan")
    fun getKelurahan(): Call<ResponseKelurahan>

    @GET("dtxlokasi")
    fun getLokasi(): Call<ResponseLokasi>

    @GET("dtxbidang")
    fun getBidang(): Call<ResponseBidang>

    @FormUrlEncoded
    @POST("dtxlokasi/store")
    fun insertLokasi(
        @Field("datalokasi") datalokasi:String,
        @Field("latitude") latitude:Int,
        @Field("longitude") longitude:Int,
        @Field("foto") foto:String,
    ): Call<SubmitModel>

    @FormUrlEncoded
    @POST("dtxlokasi/update/{id}")
    fun updateLokasi(
        @Path("id") id:Int,
        @Field("datalokasi") datalokasi:String,
        @Field("latitude") latitude:Int,
        @Field("longitude") longitude:Int,
        @Field("foto") foto:String,
    ): Call<SubmitModel>

    @DELETE("dtxlokasi/delete/{id}")
    fun deleteLokasi(
        @Path("id") id:Int,
    ): Call<SubmitModel>

    @GET("dtxlokasi/showByBidang/{id}")
    fun getLokasiByBidang(
        @Path("id") id:Int
    ):Call<ResponseLokasi>

    @GET("dtxkelurahan/showByLokasi/{id}")
    fun getKelurahanByLokasi(
        @Path("id") id:Int
    ):Call<ResponseKelurahan>

    @GET("dtxkelurahan/showByKecamatan/{id}")
    fun getKelurahanByKecamatan(
        @Path("id") id:Int
    ):Call<ResponseKelurahan>

    @Multipart
    @POST("dtxpengaduan/upload")
    fun uploadImage(
        @Part foto:MultipartBody.Part
    ):Call<SubmitModel>


}