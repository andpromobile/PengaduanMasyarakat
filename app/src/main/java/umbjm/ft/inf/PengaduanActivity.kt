package umbjm.ft.inf

import android.Manifest
import android.R
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.elaporadmin.ViewModel.KecamatanViewModel
import com.example.elaporadmin.ViewModel.KelurahanViewModel
import com.example.elaporadmin.ViewModel.LokasiViewModel
import com.example.elaporadmin.ViewModel.PengaduanViewModel
import com.example.elaporadmin.ViewModel.SeksiViewModel
import com.example.elaporadmin.ViewModel.SubmitModel
import com.example.elaporadmin.retrofit.ApiService
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import umbjm.ft.inf.databinding.ActivityPengaduanBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import umbjm.ft.inf.dao.CekLokasi
import java.util.UUID

class PengaduanActivity : AppCompatActivity(), UploadRequestBody.UploadCallback {
    private lateinit var binding:ActivityPengaduanBinding
    private lateinit var frmNama: EditText
    private lateinit var frmNoHp: EditText
    private lateinit var frmJudul: EditText
    private lateinit var frmIsi: EditText
//    private lateinit var frmTanggal: EditText
    private lateinit var frmBidangId: AutoCompleteTextView
    private lateinit var frmLokasiId: AutoCompleteTextView
    private lateinit var frmKelurahanId: AutoCompleteTextView
    private lateinit var frmKecamatanId: AutoCompleteTextView
    private lateinit var btnImg:ImageButton
    private lateinit var saveImg:ImageButton
    private lateinit var lapor:AppCompatButton
    private lateinit var progressBar: ProgressBar
    private lateinit var progressBarLapor: ProgressBar
    private lateinit var toolbarPengaduan:androidx.appcompat.widget.Toolbar
    private var seksiId:Int=0
    private var lokasiId:Int=0
    private var kelurahanId:Int=0
    private var kecamatanId:Int=0
    private var selectedImageUri: Uri? = null
    private lateinit var currentPhotoPath:String
    private lateinit var photoFile:File
    private var fileName:String = ""
    private val pengaduanViewModel: PengaduanViewModel by viewModels()
    private val strFormatDefault = "yyyy-MM-d"//""d MMMM yyyy"
    private val tanggal = SimpleDateFormat(strFormatDefault, Locale.getDefault())
        .format(Calendar.getInstance().time).toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPengaduanBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        toolbarPengaduan = binding.toolbarPengaduan!!
//        setSupportActionBar(toolbarPengaduan)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setDisplayShowHomeEnabled(true)


        setPermissions()
        setKomponen()
        setListener()
        setAutoComplete()
        showLoading(false)
    }

    private fun insertPengaduan() {
        if (cekInput()){
            lapor.visibility = View.GONE
            showLoading(true)
            lifecycleScope.launch{
                pengaduanViewModel.insertPengaduan(
                    frmJudul.text.toString(), frmNama.text.toString(),
                    frmNoHp.text.toString(), frmIsi.text.toString(),
                    tanggal, fileName,
                    lokasiId, kelurahanId, seksiId, kecamatanId
                )
                Log.d("HABIS INSERT","HABIS INSERT")
            }


            pengaduanViewModel.observePesanLiveData().observe(
                this,
            ) {pesan->
                Log.d("DALAM OBSERVE","DALAM OBSERVE")
                SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Sukses")
                    .setContentText("DATA BERHASIL DISIMPAN")
                    .setConfirmButton("IYA") {
                        showLoading(false)
                        it.dismissWithAnimation()

                        val intent = Intent(this@PengaduanActivity,
                            MainActivity::class.java,)

                        intent.putExtra("TOKEN", pesan.toString())
                        startActivity(intent)
                    }
                    .show()
            }



       }else{
            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setContentText("Input Tidak Boleh Kosong!!!")
                .show()
        }
    }

    private fun cekInput():Boolean {
        var cek = false
        if (
            (frmJudul.text.toString() != "") && (frmNama.text.toString() != "") &&
            (frmNoHp.text.toString() != "") && (frmIsi.text.toString() != "")
        ) cek = true

        return cek
    }

    private fun setAutoComplete() {
        seksi()
    }

    private fun kecamatan(){

        val kecamatanViewModel = ViewModelProvider(this)[KecamatanViewModel::class.java]

        kecamatanViewModel.getKecamatan()

        kecamatanViewModel.observeKecamatanLiveData().observe(
            this
        ){ kecamatanList ->
            val fp:MutableList<String?> = ArrayList()
            val listId:MutableList<String?> = ArrayList()

            for (i in kecamatanList){
                fp.add(i.namakecamatan)
                listId.add(i.id.toString())
            }

            val arrayAdapter: ArrayAdapter<String?> = ArrayAdapter<String?>(this, R.layout.simple_list_item_1, fp)
            arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)

            frmKecamatanId.setAdapter(arrayAdapter)

            frmKecamatanId.setOnItemClickListener { _, _, position, _ ->
                kecamatanId = listId[position]!!.toInt()
                kelurahan(kecamatanId)
            }


        }
    }
    private fun kelurahan(id:Int) {
        val kelurahanViewModel = ViewModelProvider(this)[KelurahanViewModel::class.java]

        kelurahanViewModel.getKelurahanByKecamatan(id)

        kelurahanViewModel.observeKelurahanLiveData().observe(
            this
        ){ lokasiList ->
            val fp:MutableList<String?> = ArrayList()
            val listId:MutableList<String?> = ArrayList()

            for (i in lokasiList){
                fp.add(i.namakelurahan)
                listId.add(i.id.toString())
            }

            val arrayAdapter: ArrayAdapter<String?> = ArrayAdapter<String?>(this, R.layout.simple_list_item_1, fp)
            arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)

            frmKelurahanId.setAdapter(arrayAdapter)

            frmKelurahanId.setOnItemClickListener { _, _, position, _ ->
                kelurahanId = listId[position]!!.toInt()

            }


        }
    }

    private fun lokasi(id:Int) {
        val lokasiViewModel = ViewModelProvider(this)[LokasiViewModel::class.java]

        lokasiViewModel.getLokasiBySeksi(seksiId)


        lokasiViewModel.observeLokasiLiveData().observe(
            this
        ){ lokasiList ->
            val fp:MutableList<String?> = ArrayList()
            val listId:MutableList<String?> = ArrayList()

            for (i in lokasiList){
                fp.add(i.datalokasi)
                listId.add(i.id.toString())
            }

            val arrayAdapter: ArrayAdapter<String?> = ArrayAdapter<String?>(this, R.layout.simple_list_item_1, fp)
            arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)

            frmLokasiId.setText("")
            frmLokasiId.setAdapter(arrayAdapter)

            frmLokasiId.setOnItemClickListener { _, _, position, _ ->
                lokasiId = listId[position]!!.toInt()
                ApiService.api.cekLokasi(lokasiId).enqueue(object :Callback<CekLokasi>{
                    override fun onResponse(
                        call: Call<CekLokasi>,
                        response: Response<CekLokasi>
                    ) {
                        if (response.isSuccessful){
                            if(response.body()?.message == "0") {
                                lapor.visibility = View.VISIBLE

                                kelurahanId = response.body()?.kelurahan_id!!
                                kecamatanId = response.body()?.kecamatan_id!!
                            }
                            else{
                                lapor.visibility = View.GONE
                                SweetAlertDialog(this@PengaduanActivity, SweetAlertDialog.WARNING_TYPE)
                                    .setContentText("Lokasi sudah dilaporkan dan menunggu tindak lanjut. Silakan Pilih Lokasi lain")
                                    .show()

                            }
                        }
                    }

                    override fun onFailure(call: Call<CekLokasi>, t: Throwable) {
                        Log.d("Masalah Koneksi", "Masalah Koneksi")
                    }

                })
            }


        }
    }

    private fun seksi() {
        val seksiViewModel = ViewModelProvider(this)[SeksiViewModel::class.java]

        seksiViewModel.getSeksi()

        seksiViewModel.observeSeksiLiveData().observe(
            this
        ){ seksiList ->
            val fp:MutableList<String?> = ArrayList()
            val listId:MutableList<String?> = ArrayList()

            for (i in seksiList){
                fp.add(i.namaseksi)
                listId.add(i.id.toString())
            }

            val arrayAdapter: ArrayAdapter<String?> = ArrayAdapter<String?>(this, R.layout.simple_list_item_1, fp)
            arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)

            frmBidangId.setAdapter(arrayAdapter)

            frmBidangId.setOnItemClickListener { _, _, position, _ ->
                seksiId = listId[position]!!.toInt()
                lokasi(seksiId)
            }


        }

    }

    private fun setListener() {
        btnImg.setOnClickListener {
            val pictureDialog = AlertDialog.Builder(this)
            pictureDialog.setTitle("Pilih Aksi")

            val pictureDialogItem:Array<String> = arrayOf("Pilih dari Galeri")

            pictureDialog.setItems(pictureDialogItem){
                    dialog, which ->
                when(which){
                    0 -> galeri()
//                    1 -> kamera()
                }
            }

            pictureDialog.show()
        }

        lapor.setOnClickListener {
            insertPengaduan()
        }

//        frmTanggal.setOnClickListener {
//            val tanggalLapor: Calendar = Calendar.getInstance()
//            val date =
//                DatePickerDialog.OnDateSetListener {
//                        view1: DatePicker?,
//                        year: Int, monthOfYear: Int,
//                        dayOfMonth: Int ->
//                    tanggalLapor.set(Calendar.YEAR, year)
//                    tanggalLapor.set(Calendar.MONTH, monthOfYear)
//                    tanggalLapor.set(Calendar.DAY_OF_MONTH, dayOfMonth)
//                    val strFormatDefault = "yyyy-MM-d"//""d MMMM yyyy"
//                    val simpleDateFormat =
//                        SimpleDateFormat(strFormatDefault, Locale.getDefault())
//                    frmTanggal.setText(simpleDateFormat.format(tanggalLapor.time))
//                }
//            DatePickerDialog(
//                this@PengaduanActivity, date,
//                tanggalLapor.get(Calendar.YEAR),
//                tanggalLapor.get(Calendar.MONTH),
//                tanggalLapor.get(Calendar.DAY_OF_MONTH)
//            ).show()
//        }

        saveImg.setOnClickListener {
            doUpload()
        }

    }

    private fun doUpload() {
        if (selectedImageUri == null){
            return
        }

//        val strFormatDefault = "yyyy-MM-d"//""d MMMM yyyy"
//        val simpleDateFormat =
//            SimpleDateFormat(strFormatDefault, Locale.getDefault())

        val rndm = UUID.randomUUID().toString().substring(0,10) +"_"+tanggal+".jpg"

        val parcelFileDescriptor = contentResolver.openFileDescriptor(selectedImageUri!!,
        "r",
        null)?:return
        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val file = File(cacheDir, contentResolver.getFileName(selectedImageUri!!))
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)

        Log.d("Cek nama file", file.name)
        Log.d("Cek nama file", file.absolutePath)
        val renamedFile = File(cacheDir, rndm)

        file.renameTo(renamedFile)

        Log.d("Cek nama file",renamedFile.name)
        Log.d("Cek nama file", renamedFile.absolutePath)

//        var compressedImageFile = renamedFile
        lifecycleScope.launch {
            val compressedImageFile = Compressor.compress(applicationContext, renamedFile){
                resolution(800,600)
                quality(80)
            }

            fileName = compressedImageFile.name

            Log.d("Cek nama file",compressedImageFile.name)
            Log.d("Cek nama file", compressedImageFile.absolutePath)

            progressBar.progress = 0
//            val foto = UploadRequestBody(compressedImageFile, "image",this)
            val foto = compressedImageFile.asRequestBody("image/*".toMediaTypeOrNull())

            ApiService.api.uploadImage(
                MultipartBody.Part.createFormData("foto", compressedImageFile.name, foto)
            ).enqueue(object: Callback<SubmitModel> {
                override fun onResponse(call: Call<SubmitModel>, response: Response<SubmitModel>) {
                    if(response.isSuccessful){

                    }else{

                    }

                    progressBar.progress = 100
                }

                override fun onFailure(call: Call<SubmitModel>, t: Throwable) {

                }

            })

            SweetAlertDialog(this@PengaduanActivity, SweetAlertDialog.SUCCESS_TYPE)
                .setContentText("Berhasil Upload Gambar")
                .show()

        }
    }

    private fun setKomponen() {
        frmNama = binding.frmNama!!
        frmNoHp = binding.frmNoHP!!
        frmJudul = binding.frmJudul!!
        frmIsi = binding.frmIsi!!
//        frmTanggal = binding.frmTanggal!!
        frmBidangId = binding.frmBidangId!!
        frmLokasiId = binding.frmLokasiId!!
//        frmKelurahanId = binding.frmKelurahanId!!
//        frmKecamatanId = binding.frmKecamatanId!!
        btnImg = binding.uploadImg
        saveImg = binding.saveImg!!
        lapor = binding.lapor as AppCompatButton
        progressBar = binding.progressBar!!
        progressBarLapor = binding.progressBarLapor!!
    }

    private fun setPermissions() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
        }
    }

    private fun kamera(){
        // Menampilkan layar atau Activity untuk mengambil gambar menggunakan kamera HP
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, 123)

    }

    private fun galeri(){
        // Menampilkan layar atau Activity untuk mengambil gambar dari galeri foto di HP
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 456)

    }

    override fun onActivityResult(requestCode: Int,
                                  resultCode: Int,
                                  data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        // Menampung hasil ambil gambar
        // kode = 123 untuk gambar dari penyimpanan lokal
        // kode = 456 untuk gambar dari kamera
        if(requestCode == 123){
            val bmp: Bitmap = data?.extras?.get("data") as Bitmap
            binding.img.setImageBitmap(bmp)
        }else if(requestCode== 456){
            selectedImageUri = data?.data
            binding.img.setImageURI(selectedImageUri)

        }

    }

    private fun ContentResolver.getFileName(fileUri: Uri): String {
       var name = ""
       val returnCursor = this.query(fileUri, null, null, null, null)
        if (returnCursor != null){
            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor.moveToFirst()
            name = returnCursor.getString(nameIndex)
            returnCursor.close()
        }
        return name
    }

    override fun onProgressUpdate(percentage: Int) {
        progressBar.progress = percentage
    }

    @Throws(IOException::class)
    private fun createImage():File{
        val timeStamp:String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg"
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun showLoading(loading:Boolean){
        when(loading){
            true -> progressBarLapor.visibility = View.VISIBLE
            false -> progressBarLapor.visibility = View.GONE
        }
    }


}


