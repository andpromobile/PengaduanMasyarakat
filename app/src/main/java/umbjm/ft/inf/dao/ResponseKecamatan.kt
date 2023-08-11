package umbjm.ft.inf.dao

import com.google.gson.annotations.SerializedName

class ResponseKecamatan (
    @SerializedName("status"  ) var status  : Boolean?        = null,
    @SerializedName("message" ) var message : String?         = null,
    @SerializedName("data"    ) var data    : ArrayList<Kecamatan> = arrayListOf()
)