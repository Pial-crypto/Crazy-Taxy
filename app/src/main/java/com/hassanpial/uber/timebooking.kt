package com.hassanpial.uber

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class timebooking : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timebooking)

        findViewById<Button>(R.id.bottomButton).setOnClickListener(){
            startActivity(Intent(this,MapsActivity3::class.java))
        }
    }
}