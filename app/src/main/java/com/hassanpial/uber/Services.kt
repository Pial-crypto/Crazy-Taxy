package com.hassanpial.uber

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class Services : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_services)


        findViewById<CardView>(R.id.item1_card).setOnClickListener(){
            startActivity(Intent(this,MapsActivity::class.java))
        }

        findViewById<CardView>(R.id.item2_card).setOnClickListener(){
            startActivity(Intent(this,MapsActivity2::class.java))
        }
        findViewById<CardView>(R.id.pkg).setOnClickListener(){
            startActivity(Intent(this,packageinstruction::class.java))
        }

        findViewById<CardView>(R.id.rent).setOnClickListener(){
            startActivity(Intent(this,rentalinstructions::class.java))
        }
        findViewById<CardView>(R.id.reserve_card).setOnClickListener(){
            Toast.makeText(this,"This feature is coming soon",Toast.LENGTH_SHORT).show()
        }
        findViewById<CardView>(R.id.back_button).setOnClickListener(){
            startActivity(Intent(this,ApplicationHomePage::class.java))
        }


    }
}