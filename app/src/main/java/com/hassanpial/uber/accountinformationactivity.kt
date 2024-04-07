package com.hassanpial.uber

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class accountinformationactivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accountinformationactivity)
        SetupExistingDatas()
        findViewById<ImageButton>(R.id.back_button).setOnClickListener(){
            finish()
        }
   //     findViewById<Button>(R.id.editprofilebutton).setOnClickListener(){
         //   startActivity(Intent(this,EditProfile::class.java))
     //   }

        var bottomNavigationView=findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId=R.id.ai
        bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.privacyanddata->{
                    findViewById<ShapeableImageView>(R.id.profile_picture).visibility=View.INVISIBLE
                    findViewById<TextView>(R.id.editTextEmail).visibility=View.INVISIBLE
                    findViewById<TextView>(R.id.editTextPhone).visibility=View.INVISIBLE
                    findViewById<TextView>(R.id.editTextName).visibility=View.INVISIBLE
                    supportFragmentManager.beginTransaction().replace(R.id.container,PrivacyandDatafragment()).commit()
                    //var fragment=supportFragmentManager.findFragmentById(R.id.container)
                    //fragment.let {
                      //  if (it != null) {
                        //    supportFragmentManager.beginTransaction().remove(it).commit()
                        //}
                    //}
                //    Toast.makeText(this,"This feature is coming soon to keep")
                    true
                }

                R.id.ai->{findViewById<ShapeableImageView>(R.id.profile_picture).visibility=View.VISIBLE
                    findViewById<TextView>(R.id.editTextEmail).visibility=View.VISIBLE
                    findViewById<TextView>(R.id.editTextPhone).visibility=View.VISIBLE
                    findViewById<TextView>(R.id.editTextName).visibility=View.VISIBLE
                    val fragment = supportFragmentManager.findFragmentById(R.id.container)
                    fragment?.let {
                        supportFragmentManager.beginTransaction().remove(it).commit()
                    }
                  //  supportFragmentManager.beginTransaction().replace(R.id.container,Accountinfofragmentinsideaccountactivity()).commit()
                    true
                }

                else -> {
                    false
                }
            }
        }
    }


    fun SetupExistingDatas(){
        FirebaseDatabase.getInstance().getReference("Registered users").child(FirebaseAuth.getInstance().uid.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Handle the data when it changes
                    if (snapshot.exists()) {
                        // The snapshot contains the data for the specified user
                        val name = snapshot.child("Name").value.toString()
                        val email = snapshot.child("Email").getValue(String::class.java)
                        var dp=snapshot.child("Profile Picture").getValue(String::class.java)
                       // val password=snapshot.child("Password").getValue(String::class.java)
                        val phone=snapshot.child("Phone").getValue(String::class.java)
                        //   var locationname=snapshot.child("Location").getValue(String::class.java)
                        findViewById<TextView>(R.id.editTextEmail).text = email
                        findViewById<TextView>(R.id.editTextName).text = name
                        findViewById<TextView>(R.id.editTextPhone).text = phone

                        var imageview=findViewById<ShapeableImageView>(R.id.profile_picture)


                        if (dp != null) {
                            // Load the image into an ImageView using Glide (or any other method you prefer)
                            Glide.with(applicationContext)
                                .load(dp)
                                .into(imageview)

                        } else {
                          imageview.setImageResource(R.drawable.blankpp)

                            // Handle the case where the "Profile Picture" field is null or doesn't exist
                            // You might want to use a default image or show an error placeholder
                            // imageView.setImageResource(R.drawable.default_profile_image)
                            // or
                            // handle the error appropriately8/
                        }
                    } else {
                        //  userok=false
                        // Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
                        // Handle the case when the user data doesn't exist
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle errors, if any
                    println("Error: ${error.message}")
                }
            })


    }



}