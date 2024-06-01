package it.unipd.dei.pseaiproject.ui.main

import android.graphics.PorterDuff
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import it.unipd.dei.pseaiproject.BottomSheetFragment
import it.unipd.dei.pseaiproject.R
import it.unipd.dei.pseaiproject.databinding.ActivityModelBinding
import it.unipd.dei.pseaiproject.viewmodels.CameraViewModel


class ModelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityModelBinding
    private var bottomSheetFragment: BottomSheetFragment? = null
    private val cameraViewModel: CameraViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.backButtonToolbar.setOnClickListener {
            finish()
        }

        val logoImage = binding.logoImage
        logoImage.setColorFilter(ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.SRC_IN)

        bottomSheetFragment = BottomSheetFragment()
        cameraViewModel.objectDetectorHelper.observe(this) { helper ->
            helper?.let {
                bottomSheetFragment?.setDetectorObject(it)
            }
        }

        binding.modalButton.setOnClickListener {
            bottomSheetFragment?.show(supportFragmentManager, "BottomSheetDialog")
        }
    }
}
