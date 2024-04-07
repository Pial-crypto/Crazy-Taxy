package com.hassanpial.uber

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Grouptravel : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grouptravel)
        findViewById<Button>(R.id.bottomButton).setOnClickListener(){
            startActivity(Intent(this,PickedUp::class.java))
        }
    }
}