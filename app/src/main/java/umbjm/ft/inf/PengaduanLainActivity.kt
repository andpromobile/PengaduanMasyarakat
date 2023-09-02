package umbjm.ft.inf

import android.Manifest
import android.R
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.elaporadmin.ViewModel.KelurahanViewModel
import com.example.elaporadmin.ViewModel.PengaduanLainViewModel
import com.example.elaporadmin.ViewModel.PengaduanViewModel
import com.example.elaporadmin.ViewModel.SubmitModel
import com.example.elaporadmin.retrofit.ApiService
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import umbjm.ft.inf.databinding.ActivityPengaduanLainBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class PengaduanLainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPengaduanLainBinding
    private lateinit var frmNama: EditText
    private lateinit var frmNoHp: EditText
    private lateinit var frmJudul: EditText
    private lateinit var frmIsi: EditText
    private lateinit var latitude: EditText
    private lateinit var longitude: EditText
    private lateinit var namalokasi: EditText
    private lateinit var alamat: EditText
    private lateinit var frmKelurahanId: AutoCompleteTextView
    private lateinit var btnImg: ImageButton
    private lateinit var saveImg: ImageButton
    private lateinit var btnPeta:ImageButton
    private lateinit var lapor: AppCompatButton
    private lateinit var progressBar: ProgressBar
    private lateinit var progressBarLapor: ProgressBar
    private lateinit var toolbarPengaduan:androidx.appcompat.widget.Toolbar

    private var kelurahanId:Int=0
    private var selectedImageUri: Uri? = null
    private lateinit var currentPhotoPath:String
    private lateinit var photoFile: File
    private var fileName:String = ""
    private val pengaduanLainViewModel: PengaduanLainViewModel by viewModels()
    private val strFormatDefault = "yyyy-MM-d"//""d MMMM yyyy"
    private val tanggal = SimpleDateFormat(strFormatDefault, Locale.getDefault())
        .format(Calendar.getInstance().time).toString()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPengaduanLainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        toolbarPengaduan = binding.toolbarPengaduanLain
//        setSupportActionBar(toolbarPengaduan)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setDisplayShowHomeEnabled(true)

        setKomponen()
        setPermissions()
        setListener()
        setAutoComplete()
        showLoading(false)
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
            insertPengaduanLain()
        }


        saveImg.setOnClickListener {
            doUpload()
        }

        btnPeta.setOnClickListener {
            startActivityForResult(
                Intent(this, MapsActivity::class.java),
                999
            )
        }
    }

    private fun doUpload() {
            if (selectedImageUri == null){
                return
            }

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
                            Log.d("1: DALAM if response", "DALAM if response")
                        }else{
                            Log.d("2: DALAM else response", "DALAM else response")
                        }
                        Log.d("3: DALAM response", "DALAM response")

                        Log.d("4: DALAM response", "DALAM response")
                        progressBar.progress = 100
                    }

                    override fun onFailure(call: Call<SubmitModel>, t: Throwable) {

                    }

                })

                SweetAlertDialog(this@PengaduanLainActivity, SweetAlertDialog.SUCCESS_TYPE)
                    .setContentText("Berhasil Upload Gambar")
                    .show()

            }
        }

    private fun insertPengaduanLain() {
        if (cekInput()){
            lapor.isEnabled = false
            showLoading(true)
            lifecycleScope.launch{
                pengaduanLainViewModel.insertPengaduanLain(
                    frmJudul.text.toString(), frmNama.text.toString(),
                    frmNoHp.text.toString(), namalokasi.text.toString(),
                    alamat.text.toString(), latitude.text.toString(),
                    longitude.text.toString(), frmIsi.text.toString(),
                    tanggal, fileName, kelurahanId,
                )

            }


            pengaduanLainViewModel.observePesanLiveData().observe(
                this,
            ) {pesan->
                Log.d("DALAM OBSERVE","DALAM OBSERVE")
                SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Sukses")
                    .setContentText("DATA BERHASIL DISIMPAN")
                    .setConfirmButton("IYA") {
                        showLoading(false)
                        lapor.isEnabled = true
                        it.dismissWithAnimation()

                        val intent = Intent(this@PengaduanLainActivity,
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

    private fun cekInput():Boolean {
        var cek = false
        if (
            (frmJudul.text.toString() != "") && (frmNama.text.toString() != "") &&
            (frmNoHp.text.toString() != "") && (frmIsi.text.toString() != "") &&
            (latitude.text.toString() != "") && (longitude.text.toString() != "")
        ) cek = true

        return cek
    }

    private fun setAutoComplete() {
        kelurahan()
    }

    private fun kelurahan() {
        val kelurahanViewModel = ViewModelProvider(this)[KelurahanViewModel::class.java]

        kelurahanViewModel.getKelurahan()

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

    private fun galeri(){
        // Menampilkan layar atau Activity untuk mengambil gambar dari galeri foto di HP
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 456)

    }

    private fun setKomponen() {
        frmNama = binding.frmNamaLain
        frmNoHp = binding.frmNoHPLain
        frmJudul = binding.frmJudulLain
        frmIsi = binding.frmIsiLain
        namalokasi = binding.frmLokasiLain
        alamat = binding.frmAlamatLokasiLain
        latitude = binding.frmLatitude
        longitude = binding.frmLongitude
        frmKelurahanId = binding.frmKelurahanIdLain
        btnImg = binding.uploadImgLain
        saveImg = binding.saveImgLain
        btnPeta = binding.btnPetaLain
        lapor = binding.laporLain
        progressBar = binding.progressBar
        progressBarLapor = binding.progressBarLaporLain
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
            binding.imgLain.setImageBitmap(bmp)
        }else if(requestCode== 456){
            selectedImageUri = data?.data
            binding.imgLain.setImageURI(selectedImageUri)

        }

        // Menampung hasil pemilihan lokasi
        // dari nilai Latitude dan Longitude
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 999) {
                latitude.setText(data?.getStringExtra("latitude"))
                longitude.setText(data?.getStringExtra("longitude"))
            }
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

    private fun showLoading(loading:Boolean){
        when(loading){
            true -> progressBarLapor.visibility = View.VISIBLE
            false -> progressBarLapor.visibility = View.GONE
        }
    }
}