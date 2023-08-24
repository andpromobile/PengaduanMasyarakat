package umbjm.ft.inf

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.elaporadmin.ViewModel.PengaduanViewModel
import umbjm.ft.inf.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var judul:EditText
    private lateinit var txtToken: TextView
    private val pengaduanViewModel: PengaduanViewModel by viewModels()
    private var sizeList:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        judul = binding.judul
        txtToken = binding.token

        if (!intent.extras?.isEmpty!!){
            val token = intent.getStringExtra("TOKEN").toString()
            pengaduanViewModel.getPengaduanByToken(token)

            pengaduanViewModel.observePengaduanLiveData().observe(
                this
            ){
                judul.setText(it[0].judulpengaduan)

                txtToken.setText(it[0].token)
            }

        }

        binding.lapor.setOnClickListener{
            if (!txtToken.text.toString().isEmpty()){


                val pengaduanViewModel = ViewModelProvider(this)[PengaduanViewModel::class.java]

                pengaduanViewModel.getPengaduanByToken(txtToken.text.toString())
                pengaduanViewModel.observePengaduanLiveData().observe(
                    this
                ){listPengaduan->
                    sizeList = listPengaduan.count()
                    if (sizeList > 0 ){
                        val intent = Intent(
                            this@MainActivity,
                            StatusActivity::class.java)

                        intent.putExtra("TOKEN",txtToken.text.toString())
                        startActivity(intent)
                    }
                }
            }else{
                SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setContentText("Isikan Token Pengaduan Anda Terlebih Dahulu")
                    .show()
            }
        }
    }
}