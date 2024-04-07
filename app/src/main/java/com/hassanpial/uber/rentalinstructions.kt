package com.hassanpial.uber

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class rentalinstructions : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rentalinstructions)
        findViewById<ImageButton>(R.id.backButton).setOnClickListener(){
            finish()
        }

        findViewById<Button>(R.id.getStartedButton).setOnClickListener(){
            startActivity(Intent(this,MapsActivityrental::class.java))
        }

    }
}