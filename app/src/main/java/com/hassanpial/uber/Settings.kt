package com.hassanpial.uber

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Settings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
     var   currentuid=FirebaseAuth.getInstance().uid.toString()
        var imageview=findViewById<ShapeableImageView>(R.id.profile_image)
        var backbtn=findViewById<ImageButton>(R.id.back_button)
        var nameview=findViewById<TextView>(R.id.name_edittext)
        var numberview=findViewById<TextView>(R.id.number_edittext)
        var emailview=findViewById<TextView>(R.id.email_edittext)
        var mode=findViewById<Switch>(R.id.dark_mode_switch)
        backbtn.setOnClickListener(){
            finish()
        }
       // var addhome=findViewById<Button>(R.id.add_home_button)
        //var addwork=findViewById<Button>(R.id.add_work_button)

        FirebaseDatabase.getInstance().getReference("Registered users").child(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Handle the data when it changes
                    if (snapshot.exists()) {
                        // The snapshot contains the data for the specified user
                        val name = snapshot.child("Name").value.toString()
                        val email = snapshot.child("Email").getValue(String::class.java)
                        var dp=snapshot.child("Profile Picture").getValue(String::class.java)
                        //  val password=snapshot.child("Password").getValue(String::class.java)
                        val phone=snapshot.child("Phone").getValue(String::class.java)
                        //   var locationname=snapshot.child("Location").getValue(String::class.java)
                        //findViewById<TextInputEditText>(R.id.passedit)?.setText(password)

                        nameview.setText(name)
                       numberview.setText(phone)
                        emailview.setText(email)

                        // editText.text = Editable.Factory.getInstance().newEditable(locationname)
                        //println("Name: $name, Email: $email")

                        if (dp != null) {
                            // Load the image into an ImageView using Glide (or any other method you prefer)
                            Glide.with(applicationContext)
                                .load(dp)
                                .into(imageview)

                        } else {
                           imageview.setImageResource(R.drawable.blankpp)

                        }
                    } else {

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle errors, if any
                    println("Error: ${error.message}")
                }
    })

findViewById<TextView>(R.id.signout_text).setOnClickListener(){
    FirebaseDatabase.getInstance().getReference("Registered users").child(currentuid).child("Token").setValue(" ")
    FirebaseAuth.getInstance().signOut()

    val intent = Intent(this, MainActivity::class.java)
    //  intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    //Toast.makeText(requireContext(),intent.flags,Toast.LENGTH_SHORT).show()
    startActivity(intent)
   finishAffinity()
}


       mode.setOnCheckedChangeListener { _, isChecked ->
           if (isChecked) {
               // Dark mode is enabled
               AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
           } else {
               // Dark mode is disabled
               AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
           }
       }

   // fun SetupExistingDatas(){

         //   })


    }
}