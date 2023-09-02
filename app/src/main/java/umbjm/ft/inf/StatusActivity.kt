package umbjm.ft.inf

import android.content.Intent
import android.graphics.Color
import android.graphics.ColorSpace.Rgb
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.example.elaporadmin.ViewModel.LokasiViewModel
import com.example.elaporadmin.ViewModel.PengaduanViewModel
import umbjm.ft.inf.databinding.ActivityStatusBinding

class StatusActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStatusBinding
    private lateinit var toolbarStatus:androidx.appcompat.widget.Toolbar
    private lateinit var progressBar:ProgressBar
    private lateinit var iv:ImageView
    private lateinit var cv:CardView
    private val pengaduanViewModel: PengaduanViewModel by viewModels()
    private val lokasiViewModel: LokasiViewModel by viewModels()
    private var urlGambar = ""

    companion object{
        private val url ="https://pupr.hstkab.go.id/elapor/elapor/public/foto/"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inisialisasi()
        setKomponen()
    }

    private fun inisialisasi() {
        progressBar = binding.progressBar
        cv = binding.cv
        iv = binding.iv

        if (!intent.extras?.isEmpty!!) {
            val token = intent.getStringExtra("TOKEN").toString()
            pengaduanViewModel.getPengaduanByToken(token)

            pengaduanViewModel.observePengaduanLiveData().observe(
                this
            ) {

                lokasiViewModel.showLokasi(it[0].lokasi_id)

                lokasiViewModel.observeLokasiLiveData().observe(
                    this
                ){listLokasi->

                    binding.judul.text = it[0].judulpengaduan.toString()
                    binding.isi.text = it[0].isipengaduan.toString()
                    binding.lokasi.text = listLokasi[0].datalokasi

                    urlGambar = url+it[0].foto

                    Glide
                        .with(this)
                        .load(urlGambar)
                        .centerCrop()
                        .into(iv)

                    when(it[0].status){
                        "0" -> {
                            "PENDING".also { binding.status.text = it }
                            binding.status.setBackgroundColor(Color.parseColor("#900C3F"))
                        }
                        "1"-> {
                            "Diteruskan Ke Kepala Bidang".also { binding.status.text = it }
                            binding.status.setBackgroundColor(Color.parseColor("#F94C10"))
                        }
                        "2"-> {
                            "Telah Diverifikasi Kepala Bidang".also { binding.status.text = it }
                            binding.status.setBackgroundColor(Color.parseColor("#1A5D1A"))
                        }
                        "3"-> {
                            "Telah Selesai".also { binding.status.text = it }
                            binding.status.setBackgroundColor(Color.parseColor("#1A5D1A"))
                        }
                        "4"-> {
                            "Ditolak".also { binding.status.text = it }
                            binding.status.setBackgroundColor(Color.parseColor("#FF0000"))
                        }
                    }

                    cv.visibility = View.VISIBLE
                    showLoading(false)
                }
            }

        }
    }

    private fun setKomponen() {
        toolbarStatus = binding.toolbarStatus
        setSupportActionBar(toolbarStatus)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        iv = binding.iv
        iv.setOnClickListener {
            val intent = Intent(this@StatusActivity,
                FullscreenImageActivity::class.java,)
            intent.putExtra("URLGAMBAR", urlGambar)
            startActivity(intent)
        }
    }

    private fun showLoading(loading:Boolean){
        when(loading){
            true -> progressBar.visibility = View.VISIBLE
            false -> progressBar.visibility = View.GONE
        }
    }

}