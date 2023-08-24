package umbjm.ft.inf

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetPengaduan: BottomSheetDialogFragment() {
    private lateinit var pengaduan:AppCompatButton
    private lateinit var pengaduanLain:AppCompatButton
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_pengaduan, container, false)

        pengaduan = view.findViewById(R.id.btnPilihPengaduan)
        pengaduanLain = view.findViewById(R.id.btnPilihPengaduanLain)

        pengaduan.setOnClickListener {
            val intent = Intent(
                context,
                PengaduanActivity::class.java)

            startActivity(intent)
        }

        pengaduanLain.setOnClickListener {
            val intent = Intent(context, PengaduanLainActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}