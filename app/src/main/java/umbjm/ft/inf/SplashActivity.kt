package umbjm.ft.inf

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.elaporadmin.ViewModel.BidangViewModel
import com.example.elaporadmin.ViewModel.PengaduanViewModel
import umbjm.ft.inf.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private lateinit var btnPengaduan:androidx.appcompat.widget.AppCompatButton
    private lateinit var btnProses:androidx.appcompat.widget.AppCompatButton
    private lateinit var txtToken:EditText
    private var sizeList:Int = 0;
    private lateinit var progressBar:ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setKomponen()
        setAlert()
        toPengaduan()
    }

    private fun toPengaduan() {
        btnPengaduan.setOnClickListener{
            val bottomSheet = BottomSheetPengaduan()
            bottomSheet.show(supportFragmentManager, "Bottom Sheet Pengaduan")
        }
    }

    private fun setAlert() {
        btnProses.setOnClickListener {
            if (!txtToken.text.toString().isEmpty()){
                showLoading(true)
                val pengaduanViewModel = ViewModelProvider(this)[PengaduanViewModel::class.java]

                pengaduanViewModel.getPengaduanByToken(txtToken.text.toString())
                pengaduanViewModel.observePengaduanLiveData().observe(
                    this
                ){listPengaduan->
                    sizeList = listPengaduan.count()
                    if (sizeList > 0 ){
                        val intent = Intent(this@SplashActivity, StatusActivity::class.java)

                        showLoading(false)

                    intent.putExtra("TOKEN",txtToken.text.toString())
                    startActivity(intent)
                }else{
                    SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setContentText("Belum ada pengaduan yang anda ajukan!!!")
                        .show()
                }
                }

                }else{
                SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setContentText("Isikan Token Pengaduan Anda Terlebih Dahulu")
                    .show()
            }
        }
    }

    private fun setKomponen() {
        btnPengaduan = binding.btnPengaduan
        btnProses = binding.btnProses
        txtToken = binding.txtToken
        progressBar = binding.progressBar

        showLoading(false)
    }

    private fun showLoading(loading:Boolean){
        when(loading){
            true -> progressBar.visibility = View.VISIBLE
            false -> progressBar.visibility = View.GONE
        }
    }
}