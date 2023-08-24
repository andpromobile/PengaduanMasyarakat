package com.example.elaporadmin.retrofit

import com.example.elaporadmin.ViewModel.SubmitModel
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
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

    @FormUrlEncoded
    @POST("dtxpengaduan/store")
    fun insertPengaduan(
        @Field("judulpengaduan") judulpengaduan:String,
        @Field("nama") nama:String,
        @Field("telp") telp:String,
        @Field("isipengaduan") isipengaduan:String,
        @Field("tanggalpengaduan") tanggalpengaduan:String,
        @Field("foto") foto:String,
        @Field("lokasi_id") lokasi_id:Int,
        @Field("kelurahan_id") kelurahan_id:Int,
        @Field("bidang_id") bidang_id:Int,
        @Field("kecamatan_id") kecamatan_id:Int,
    ): Call<SubmitModel>


    @GET("dtxpengaduan/getByToken/{token}")
    suspend fun getPengaduanByToken(
        @Path("token") token:String,
    ): Response<ResponsePengaduan>

    @GET("dtxkecamatan")
    suspend fun getKecamatan(): Response<ResponseKecamatan>

    @GET("dtxkelurahan")
    fun getKelurahan(): Call<ResponseKelurahan>

    @GET("dtxlokasi")
    fun getLokasi(): Call<ResponseLokasi>

    @GET("dtxlokasi/show/{id}")
    suspend fun getLokasiById(
        @Path("id") id: Int
    ):Response<ResponseLokasi>

    @GET("dtxbidang")
    suspend fun getBidang(): Response<ResponseBidang>

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
    suspend fun getLokasiByBidang(
        @Path("id") id:Int
    ):Response<ResponseLokasi>

    @GET("dtxkelurahan/showByLokasi/{id}")
    suspend fun getKelurahanByLokasi(
        @Path("id") id:Int
    ):Response<ResponseKelurahan>

    @GET("dtxkelurahan/showByKecamatan/{id}")
    suspend fun getKelurahanByKecamatan(
        @Path("id") id:Int
    ):Response<ResponseKelurahan>

    @Multipart
    @POST("dtxpengaduan/upload")
    fun uploadImage(
        @Part foto:MultipartBody.Part
    ):Call<SubmitModel>


}