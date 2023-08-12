package umbjm.ft.inf

import android.Manifest
import android.R
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.elaporadmin.ViewModel.BidangViewModel
import com.example.elaporadmin.ViewModel.KecamatanViewModel
import com.example.elaporadmin.ViewModel.KelurahanViewModel
import com.example.elaporadmin.ViewModel.LokasiViewModel
import com.example.elaporadmin.ViewModel.PengaduanViewModel
import com.example.elaporadmin.ViewModel.SubmitModel
import com.example.elaporadmin.retrofit.ApiService
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import umbjm.ft.inf.databinding.ActivityPengaduanBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class PengaduanActivity : AppCompatActivity(), UploadRequestBody.UploadCallback {
    private lateinit var binding:ActivityPengaduanBinding
    private lateinit var frmNama: EditText
    private lateinit var frmNoHp: EditText
    private lateinit var frmJudul: EditText
    private lateinit var frmIsi: EditText
    private lateinit var frmTanggal: EditText
    private lateinit var frmBidangId: AutoCompleteTextView
    private lateinit var frmLokasiId: AutoCompleteTextView
    private lateinit var frmKelurahanId: AutoCompleteTextView
    private lateinit var frmKecamatanId: AutoCompleteTextView
    private lateinit var btnImg:ImageButton
    private lateinit var saveImg:ImageButton
    private lateinit var lapor:AppCompatButton
    private lateinit var progressBar: ProgressBar
    private var bidangId:Int=0
    private var lokasiId:Int=0
    private var kelurahanId:Int=0
    private var kecamatanId:Int=0
    private var selectedImageUri: Uri? = null
    private lateinit var currentPhotoPath:String
    private lateinit var photoFile:File
    private var fileName:String = "foto.png"
    private val pengaduanViewModel: PengaduanViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPengaduanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setPermissions()
        setKomponen()
        setListener()
        setAutoComplete()

    }

    private fun setLog() {
//        'judulpengaduan' => 'required',
//        'nama'  => 'required',
//        'telp'  => 'required',
//        'isipengaduan'  => 'required',
//        'tanggalpengaduan'  => 'required',
//        'foto'  => 'required',
//        'lokasi_id'  => 'required',
//        'kelurahan_id'=>'required',
//        'bidang_id'=>'required',
//        'kecamatan_id'=>'required',
//        CoroutineScope(Dispatchers.IO).launch {
//            Log.d("FORM", frmJudul.text.toString())
//            Log.d("FORM", frmNama.text.toString())
//            Log.d("FORM", frmNoHp.text.toString())
//            Log.d("FORM", frmIsi.text.toString())
//            Log.d("FORM", frmTanggal.text.toString())
//            Log.d("FORM", fileName)
//            Log.d("FORM", lokasiId.toString())
//            Log.d("FORM", kelurahanId.toString())
//            Log.d("FORM", bidangId.toString())
//            Log.d("FORM", kecamatanId.toString())
//        }

        if (cekInput()){
            pengaduanViewModel.insertPengaduan(
                frmJudul.text.toString(),
                frmNama.text.toString(),
                frmNoHp.text.toString(),
                frmIsi.text.toString(),
                frmTanggal.text.toString(),
                fileName,
                lokasiId,
                kelurahanId,
                bidangId,
                kecamatanId
            )

            pengaduanViewModel.observePesanLiveData().observe(
                this,
            ) {pesan->
                SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Sukses")
                    .setContentText("DATA BERHASIL DISIMPAN")
                    .setConfirmButton("IYA") {
                        it.dismissWithAnimation()

                        val intent = Intent(
                            this@PengaduanActivity,
                            MainActivity::class.java,
                        )



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
            (frmJudul.text.toString() != "") &&
            (frmNama.text.toString() != "") &&
            (frmNoHp.text.toString() != "") &&
            (frmIsi.text.toString() != "") &&
            (frmTanggal.text.toString() != "")
        ) cek = true

        return cek
    }

    private fun setAutoComplete() {
        bidang()

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

        lokasiViewModel.getLokasiByBidang(bidangId)
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

            frmLokasiId.setAdapter(arrayAdapter)

            frmLokasiId.setOnItemClickListener { _, _, position, _ ->
                lokasiId = listId[position]!!.toInt()
                kecamatan()
            }


        }
    }

    private fun bidang() {
        val bidangViewModel = ViewModelProvider(this)[BidangViewModel::class.java]

        bidangViewModel.getBidang()
        bidangViewModel.observeBidangLiveData().observe(
            this
        ){ bidangList ->
            val fp:MutableList<String?> = ArrayList()
            val listId:MutableList<String?> = ArrayList()

            for (i in bidangList){
                fp.add(i.namabidang+" - "+i.seksi)
                listId.add(i.id.toString())
            }

            val arrayAdapter: ArrayAdapter<String?> = ArrayAdapter<String?>(this, R.layout.simple_list_item_1, fp)
            arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)

            frmBidangId.setAdapter(arrayAdapter)

            frmBidangId.setOnItemClickListener { _, _, position, _ ->
                bidangId = listId[position]!!.toInt()
                lokasi(bidangId)
            }


        }

    }

    private fun setListener() {
        btnImg.setOnClickListener {
            val pictureDialog = AlertDialog.Builder(this)
            pictureDialog.setTitle("Pilih Aksi")

            val pictureDialogItem:Array<String> = arrayOf("Pilih dari Galeri",
                "Ambil Gambar")

            pictureDialog.setItems(pictureDialogItem){
                    dialog, which ->
                when(which){
                    0 -> galeri()
                    1 -> kamera()
                }
            }

            pictureDialog.show()
        }

        lapor.setOnClickListener {
            setLog()
        }

        frmTanggal.setOnClickListener {
            val tanggalLapor: Calendar = Calendar.getInstance()
            val date =
                DatePickerDialog.OnDateSetListener {
                        view1: DatePicker?,
                        year: Int, monthOfYear: Int,
                        dayOfMonth: Int ->
                    tanggalLapor.set(Calendar.YEAR, year)
                    tanggalLapor.set(Calendar.MONTH, monthOfYear)
                    tanggalLapor.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val strFormatDefault = "yyyy-MM-d"//""d MMMM yyyy"
                    val simpleDateFormat =
                        SimpleDateFormat(strFormatDefault, Locale.getDefault())
                    frmTanggal.setText(simpleDateFormat.format(tanggalLapor.time))
                }
            DatePickerDialog(
                this@PengaduanActivity, date,
                tanggalLapor.get(Calendar.YEAR),
                tanggalLapor.get(Calendar.MONTH),
                tanggalLapor.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        saveImg.setOnClickListener {
            doUpload()
        }

    }

    private fun doUpload() {
        if (selectedImageUri == null){
            return
        }

        val parcelFileDescriptor = contentResolver.openFileDescriptor(selectedImageUri!!,
        "r",
        null)?:return
        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val file = File(cacheDir, contentResolver.getFileName(selectedImageUri!!))
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)

        fileName = file.name

        progressBar.progress = 0
        val foto = UploadRequestBody(file, "image",this)
        ApiService.endPoint.uploadImage(
            MultipartBody.Part.createFormData("foto", file.name, foto)
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
    }

    private fun setKomponen() {
        frmNama = binding.frmNama!!
        frmNoHp = binding.frmNoHP!!
        frmJudul = binding.frmJudul!!
        frmIsi = binding.frmIsi!!
        frmTanggal = binding.frmTanggal!!
        frmBidangId = binding.frmBidangId!!
        frmLokasiId = binding.frmLokasiId!!
        frmKelurahanId = binding.frmKelurahanId!!
        frmKecamatanId = binding.frmKecamatanId!!
        btnImg = binding.uploadImg
        saveImg = binding.saveImg!!
        lapor = binding.lapor as AppCompatButton
        progressBar = binding.progressBar!!
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
}


