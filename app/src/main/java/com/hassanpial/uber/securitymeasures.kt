package com.hassanpial.uber

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class securitymeasures : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_securitymeasures)
        var back=findViewById<ImageButton>(R.id.backButton)
        back.setOnClickListener(){
            finish()
        }
    }
}