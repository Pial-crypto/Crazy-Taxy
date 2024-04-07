package com.hassanpial.uber
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ScrollView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.hassanpial.uber.databinding.ActivityMainBinding

class ApplicationHomePage : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    //lateinit var progressDialog: ProgressDialog

    private val REQUEST_PERMISSIONS_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        var binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_application_home_page)

        checkAndRequestPermissions()

                val searchView = findViewById<SearchView>(R.id.search_view)
                searchView.queryHint = "Where do you want to go"


                      var shorttrip=findViewById<CardView>(R.id.shorttrip)
              shorttrip.setOnClickListener(){
                 //startActivity(Intent(this,MapsActivity::class.java))
              }

        findViewById<CardView>(R.id.longtrip).setOnClickListener(){
           startActivity(Intent(this,MapsActivity3::class.java))
        }

        findViewById<CardView>(R.id.packg).setOnClickListener(){
            startActivity(Intent(this,packageinstruction::class.java))
        }


        findViewById<CardView>(R.id.rent).setOnClickListener(){
           startActivity(Intent(this,rentalinstructions::class.java))
        }





        var token= FirebaseMessaging.getInstance().token.result.toString()
        FirebaseDatabase.getInstance().getReference("Registered users").child(
            FirebaseAuth.getInstance().currentUser?.uid.toString()).child("Token").setValue(token)

                                    // Disable search functionality
                                            //searchView.setOnQueryTextListener(null)
        var srchview=findViewById<SearchView>(R.id.search_view)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                                                    // Handle the text submission here
                query?.let {
                                                        // Perform any action with the submitted query
                                                        // For example, you can start a search or navigate to another activity
                   // val intent = Intent(this@ApplicationHomePage, MapsActivity::class.java)
                   // intent.putExtra("destination", it) // Pass the query to the next activity if needed
                   // startActivity(intent)
                }
                return true // Return true to indicate that the query has been handled
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                                                    // Handle text changes here (optional)
                return false // Return false to indicate that the query has not been handled
            }
        })

        findViewById<SearchView>(R.id.search_view).setOnClickListener(){
           // startActivity(Intent(this,MapsActivity::class.java))
        }
        var btmnavview=findViewById<BottomNavigationView>(R.id.btmnavview)

        // Select the "Home" menu item by default
                                            //
        findViewById<TextView>(R.id.see_all_button).setOnClickListener(){
            // startActivity(Intent(this,Services::class.java))
            findViewById<ScrollView>(R.id.scrollbar).visibility = View.INVISIBLE
            findViewById<TextView>(R.id.app_name).visibility = View.INVISIBLE
            // Navigate to the HomeFragment
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, servicefragment2())
                .commit()
            btmnavview.selectedItemId = R.id.service

        }

        findViewById<Button>(R.id.bookforlaterbutton).setOnClickListener(){
            startActivity(Intent(this,MapsActivity3::class.java))
        }

        findViewById<CardView>(R.id.shortridegride).setOnClickListener(){
//           startActivity(Intent(this,MapsActivity::class.java))

        }

        findViewById<CardView>(R.id.premiergrid).setOnClickListener(){
            startActivity(Intent(this,uberpremier::class.java))

        }

        findViewById<CardView>(R.id.grouptravel).setOnClickListener(){
            startActivity(Intent(this,Grouptravel::class.java))

        }
        findViewById<CardView>(R.id.plantrip).setOnClickListener(){
          startActivity(Intent(this,timebooking::class.java))

        }
        findViewById<CardView>(R.id.joinus).setOnClickListener(){
          //  startActivity(Intent(this,MapsActivity::class.java))
        }


        if(intent.getStringExtra("Bal")=="Cal"){
            findViewById<ScrollView>(R.id.scrollbar).visibility = View.INVISIBLE
            findViewById<TextView>(R.id.app_name).visibility = View.INVISIBLE
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, history())
                .commit()
            btmnavview.selectedItemId=R.id.activity
        }

        // Select the "Home" menu item by default
        // btmnavview.selectedItemId = R.id.home

        btmnavview.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    findViewById<ScrollView>(R.id.scrollbar).visibility = View.VISIBLE
                    findViewById<TextView>(R.id.app_name).visibility = View.VISIBLE
                    // Remove any existing fragment from the container (if any)
                    val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
                    fragment?.let {
                        supportFragmentManager.beginTransaction().remove(it).commit()
                    }
                    // Optionally, if you have a specific layout for the main page, you can inflate it here
                    // Otherwise, you can simply update your UI or perform any other necessary actions
                    true
                }
                R.id.service -> {
                    findViewById<ScrollView>(R.id.scrollbar).visibility = View.INVISIBLE
                    findViewById<TextView>(R.id.app_name).visibility = View.INVISIBLE
                    // Navigate to the HomeFragment
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, servicefragment2())
                       .commit()
                    true
                }
                R.id.activity -> {
                    findViewById<ScrollView>(R.id.scrollbar).visibility = View.INVISIBLE
                    findViewById<TextView>(R.id.app_name).visibility = View.INVISIBLE
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, history())
                        .commit()
                    true
                }
                R.id.profile -> {
                    findViewById<ScrollView>(R.id.scrollbar).visibility = View.INVISIBLE
                    findViewById<TextView>(R.id.app_name).visibility = View.INVISIBLE
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, AccountFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }





    }





    private fun checkAndRequestPermissions() {
        val permissionsToRequest = mutableListOf<String>()



        // Location permission
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        // Notification permission
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_NOTIFICATION_POLICY
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.ACCESS_NOTIFICATION_POLICY)
        }

        // Request permissions
        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSIONS_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            val permissionsDenied = mutableListOf<String>()

            for ((index, result) in grantResults.withIndex()) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    permissionsDenied.add(permissions[index])
                }
            }

            if (permissionsDenied.isNotEmpty()) {
                // Permission denied
                Toast.makeText(
                    this,
                    "Permission denied for: ${permissionsDenied.joinToString()}",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // All permissions granted
               Toast.makeText(this, "All permissions granted", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

