package umbjm.ft.inf.dao

class Pengaduan(
    val id:Int,
    val token:String?,
    val nama:String?,
    val telp:String?,
    val judulpengaduan:String?,
    val isipengaduan:String?,
    val tanggalpengaduan:String?,
    val foto:String?,
    val status:String?,
    val lokasi_id:Int,
    val kelurahan_id:Int,
    val bidang_id:Int,
    val created_at:String?,
    val updated_at:String?
) {
}