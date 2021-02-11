package com.rafay.gallery

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_PixabayGallery)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
