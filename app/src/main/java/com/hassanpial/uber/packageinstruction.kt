package com.hassanpial.uber

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hassanpial.uber.databinding.ActivityMainBinding

private lateinit  var binding:ActivityMainBinding

class packageinstruction : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //  binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_packageinstruction)
        // Initialize your RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.horizontalRecyclerViewpackage)

        // Set layout manager
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        var adapter=adapterforpackagerecycler()
recyclerView.adapter=adapter
        findViewById<ImageButton>(R.id.backButton).setOnClickListener(){
            finish()
        }
        adapter.setOnItemClickListener(object : adapterforpackagerecycler.OnItemClickListener {


            override fun onItemClick(position: Int) {
                // Handle item click here
                when (position) {
                    0 -> {
                        var intent=Intent(this@packageinstruction,MapsActivitypackages::class.java)
                        intent.putExtra("packagetype","I want to send a product")
                        startActivity(intent)
                       // startActivity(Intent(this@packageinstruction,MapsActivitypackages::class.java))
                    }
                    1 -> {
                        var intent=Intent(this@packageinstruction,MapsActivitypackages::class.java)
                        intent.putExtra("packagetype","I need to give a surprize gift")
                        startActivity(intent)
                        // Handle click for item 1
                        //startActivity(Intent(this@packageinstruction,MapsActivitypackages::class.java))
                    }
                    2 -> {
                        var intent=Intent(this@packageinstruction,MapsActivitypackages::class.java)
                        intent.putExtra("packagetype","Deliver a product to my customer")
                        startActivity(intent)
                        // Handle click for item 2
                  //      startActivity(Intent(this@packageinstruction,MapsActivitypackages::class.java))
                    }
                }
            }

        })


      //  findViewById<RecyclerView>(R.id.horizontalRecyclerViewpackage).adapter=adapter
        findViewById<CardView>(R.id.cornerRadiusButtonCardView).setOnClickListener(){
            var intent=Intent(this,MapsActivitypackages::class.java)
            intent.putExtra("packagetype","I want to send a product")
startActivity(intent)
        }
        findViewById<CardView>(R.id.receivesomethingcardview).setOnClickListener(){
            var intent=Intent(this,MapsActivitypackages::class.java)
            intent.putExtra("packagetype","Receive a product for me")
            startActivity(intent)
        }


    }
}