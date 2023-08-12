package umbjm.ft.inf

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import com.example.elaporadmin.ViewModel.PengaduanViewModel
import umbjm.ft.inf.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var judul:EditText
    private lateinit var txtToken: TextView
    private val pengaduanViewModel: PengaduanViewModel by viewModels()

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
    }
}