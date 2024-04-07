package com.hassanpial.uber

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class uberpremier : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_uberpremier)
        findViewById<Button>(R.id.bottomButton).setOnClickListener(){
            var intent=Intent(this,MapsActivity3::class.java)
            intent.putExtra("Premiem","YES")
            startActivity(Intent(this,MapsActivity3::class.java))
        }
    }
}