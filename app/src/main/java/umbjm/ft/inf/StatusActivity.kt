package umbjm.ft.inf

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import umbjm.ft.inf.databinding.ActivitySplashBinding
import umbjm.ft.inf.databinding.ActivityStatusBinding

class StatusActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStatusBinding
    private lateinit var toolbarStatus:androidx.appcompat.widget.Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setKomponen()
    }

    private fun setKomponen() {
        toolbarStatus = binding.toolbarStatus
        setSupportActionBar(toolbarStatus)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }
}