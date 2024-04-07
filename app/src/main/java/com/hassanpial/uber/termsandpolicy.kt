package com.hassanpial.uber

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class termsandpolicy : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_termsandpolicy)
        findViewById<ImageButton>(R.id.back_button).setOnClickListener(){
            finish()
        }
    }
}