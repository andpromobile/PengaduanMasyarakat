package umbjm.ft.inf.dao

import com.google.gson.annotations.SerializedName

class ResponseBidang (
//    @SerializedName("status") var status  : Boolean?        = null,
//    @SerializedName("message") var message : String?         = null,
    @SerializedName("data") var data    : ArrayList<Bidang> = arrayListOf()
)