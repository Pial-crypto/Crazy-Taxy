package com.hassanpial.uber

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hassanpial.uber.databinding.ActivityAirportBinding

class AirportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        var binding=ActivityAirportBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_airport)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.btmnavviewairport)
     //  val menu = bottomNavigationView.menu
     //   val inflater: MenuInflater = menuInflater
      //  inflater.inflate(R.menu.airportpicupordropmenu, menu)
findViewById<ImageButton>(R.id.backbutton).setOnClickListener {
    startActivity(Intent(this,ApplicationHomePage::class.java))
}
        if(intent.getStringExtra("Came from")!=null){
            Toast.makeText(this,intent.getStringExtra("Came from"),Toast.LENGTH_SHORT).show()
var desriptiontext=""
            var desiredlocation: String? =null
            if(intent.getStringExtra("Description")!=null){
                desriptiontext= intent.getStringExtra("Description")!!
            }
            if (intent.getStringExtra("Desired location")!==null){
                desiredlocation= intent.getStringExtra("Desired location")!!
            }
            if (intent.getStringExtra("Came from") == "DROPOFF") {
                val dropOffFragment = AirportDropoffragment.newInstance(desiredlocation!!,desriptiontext)

              //  dropOffFragment.setDescription(desriptiontext)
                //if (desiredlocation != null) {
                  //  dropOffFragment.setdesiredlocation(desiredlocation)
               // }
                bottomNavigationView.selectedItemId=R.id.menu_airport_drop_off
                supportFragmentManager.beginTransaction().replace(R.id.fragment_containerforairport, dropOffFragment).commit()
            }

                if (intent.getStringExtra("Came from") == "PICKUP") {
                    val pickUpFragment = airportpickupfragment.newInstance(desiredlocation!!,desriptiontext)
                   // pickUpFragment.setDescription(desriptiontext)
               //     if (desiredlocation != null) {
                        //pickUpFragment.setdesiredlocation(desiredlocation)
                   // }
                    bottomNavigationView.selectedItemId=R.id.pickUpTextView
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_containerforairport, pickUpFragment).commit()
                }
        }
        else{
            supportFragmentManager.beginTransaction().replace(R.id.fragment_containerforairport,airportpickupfragment()).commit()
        }

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            // Handle item selection
            when (item.itemId) {
                R.id.menu_airport_pick_up -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_containerforairport,airportpickupfragment()).commit()
                    // Handle pick-up selection
                  //  item.setIcon(R.drawable.ic_pick_up_selected)
                  //  item.setTitleColorResource(R.color.selected_text_color)
                }
                R.id.menu_airport_drop_off -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_containerforairport,AirportDropoffragment()).commit()
                    // Handle drop-off selection
                   // item.setIcon(R.drawable.ic_drop_off_selected)
                    //item.setTitleColorResource(R.color.selected_text_color)
                }
            }
            true
        }





    }
}