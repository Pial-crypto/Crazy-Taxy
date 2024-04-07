package com.hassanpial.uber

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class EditProfile : AppCompatActivity() {   private var selectedImageUri: Uri? =null
    // Declare variables at the class level
    private lateinit  var editprofileimg: ImageView
    var userok=true
    private  final  var gallery_request_code=1
    var currentUID= FirebaseAuth.getInstance().uid.toString()
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)





        // Declare and initialize views with var and findViewById
        val passedit = findViewById<TextInputEditText>(R.id.passedit)
        val confirm_passedit = findViewById<TextInputEditText>(R.id.confirmpassedit)
        val edit_phone = findViewById<TextInputEditText>(R.id.phoneEditText)
        val editname =findViewById<TextInputEditText>(R.id.nameedit)
         editprofileimg = findViewById(R.id.profileImageView)
        val editprofilebtn = findViewById<Button>(R.id.editPictureButton)
        val save = findViewById<Button>(R.id.saveButton)
        val editbuttonpass = findViewById<ImageButton>(R.id.editButtonpassword)
        val editButtonconfirmpass =findViewById<ImageButton>(R.id.editButtonconfirmpass)
        val editButtonphone = findViewById<ImageButton>(R.id.editButtonphone)
        val editButtonname = findViewById<ImageButton>(R.id.editButtonname)
var backimgbtn=findViewById<ImageView>(R.id.backimgbtn)
        backimgbtn.setOnClickListener(){
            finish()
        }

        SetupExistingDatas()

        if (editprofilebtn != null) {
            editprofilebtn.setOnClickListener(){
                val gallery= Intent(Intent.ACTION_PICK)
                gallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(gallery,gallery_request_code)

                //startActivityForResult(gallery_request_code,gallery)
            }
        }


        if(!userok)
            Toast.makeText(this, "User data empty", Toast.LENGTH_SHORT).show()



        if (editButtonname != null) {
            editButtonname.setOnClickListener(){
                editname?.let {
                    it.isClickable = true
                    it.isFocusable = true
                    it.isFocusableInTouchMode = true
                    it.requestFocus()
                }

                passedit?.let {
                    it.isClickable = false
                    it.isFocusable = false
                    it.isFocusableInTouchMode = false
                }

                edit_phone?.let {
                    it.isClickable = false
                    it.isFocusable = false
                    it.isFocusableInTouchMode = false
                }

                confirm_passedit?.let {
                    it.isClickable = false
                    it.isFocusable = false
                    it.isFocusableInTouchMode = false
                }
            }
        }

        if (editbuttonpass != null) {
            editbuttonpass.setOnClickListener(){
                passedit?.let {
                    it.isClickable = true
                    it.isFocusable = true
                    it.isFocusableInTouchMode = true
                    it.requestFocus()
                }

                editname?.let {
                    it.isClickable = false
                    it.isFocusable = false
                    it.isFocusableInTouchMode = false
                }

                edit_phone?.let {
                    it.isClickable = false
                    it.isFocusable = false
                    it.isFocusableInTouchMode = false
                }

                confirm_passedit?.let {
                    it.isClickable = false
                    it.isFocusable = false
                    it.isFocusableInTouchMode = false
                }
            }
        }

        if (editButtonconfirmpass != null) {
            editButtonconfirmpass.setOnClickListener(){
                confirm_passedit?.let {
                    it.isClickable = true
                    it.isFocusable = true
                    it.isFocusableInTouchMode = true
                    it.requestFocus()
                }

                editname?.let {
                    it.isClickable = false
                    it.isFocusable = false
                    it.isFocusableInTouchMode = false
                }

                edit_phone?.let {
                    it.isClickable = false
                    it.isFocusable = false
                    it.isFocusableInTouchMode = false
                }

                passedit?.let {
                    it.isClickable = false
                    it.isFocusable = false
                    it.isFocusableInTouchMode = false
                }
            }
        }

        if (editButtonphone != null) {
            editButtonphone.setOnClickListener(){
                edit_phone?.let {
                    it.isClickable = true
                    it.isFocusable = true
                    it.isFocusableInTouchMode = true
                    it.requestFocus()
                }

                editname?.let {
                    it.isClickable = false
                    it.isFocusable = false
                    it.isFocusableInTouchMode = false
                }

                passedit?.let {
                    it.isClickable = false
                    it.isFocusable = false
                    it.isFocusableInTouchMode = false
                }

                confirm_passedit?.let {
                    it.isClickable = false
                    it.isFocusable = false
                    it.isFocusableInTouchMode = false
                }
            }
        }

        findViewById<Button>(R.id.removePictureButton)!!.setOnClickListener(){
            if (editprofileimg != null) {
                editprofileimg.setImageResource(R.drawable.blankpp)
            }
        }

        var ProgressDialog= ProgressDialog(this)
        ProgressDialog.setTitle("Saving")
        ProgressDialog.setMessage("Updating your informations")


        if (save != null) {
            save.setOnClickListener(){



                val storageRef = FirebaseStorage.getInstance().reference.child("profile_images")
                val imageRef = storageRef.child("$currentUID.jpg")



                var name= editname?.text.toString()
                var password= passedit?.text.toString()
                var phone= edit_phone?.text.toString()
                var confirm_password= confirm_passedit?.text.toString()




                var canwego = true


                ///checking the validity of name
                if (name.isEmpty()) {
                    canwego = false

                    if (editname != null) {
                        editname.setError("Enter your name")
                    };
                    if (editname != null) {
                        editname.requestFocus()
                    };
                    return@setOnClickListener;
                }

                if (name.length < 4)
                {
                    canwego = false
                    if (editname != null) {
                        editname.setError("Name is too short.Enter at least 6 characters")
                    };
                    if (editname != null) {
                        editname.requestFocus()
                    };
                    return@setOnClickListener;
                }




                ///checking the validity of phone
                if (phone.isEmpty()) {
                    canwego = false

                    if (edit_phone != null) {
                        edit_phone.setError("Enter your phone number")
                    };
                    if (edit_phone != null) {
                        edit_phone.requestFocus()
                    };
                    return@setOnClickListener;
                }








                //checking the validity of the password
                if (password.isEmpty()) {
                    canwego = false
                    if (passedit != null) {
                        passedit.setError("Enter a password")
                    };
                    if (passedit != null) {
                        passedit.requestFocus()
                    };
                    return@setOnClickListener;
                }

                if (password.length < 5) {

                    canwego = false
                    if (passedit != null) {
                        passedit.setError("Password must have at least 6 characters")
                    };
                    if (passedit != null) {
                        passedit.requestFocus()
                    };
                    return@setOnClickListener;

                }






                ///checking the validity of confirm password


                if (confirm_password.isEmpty()) {
                    canwego = false
                    if (confirm_passedit != null) {
                        confirm_passedit.setError("Enter a password")
                    };
                    if (confirm_passedit != null) {
                        confirm_passedit.requestFocus()
                    };
                    return@setOnClickListener;
                }

                if (confirm_password.length < 5) {

                    canwego = false
                    if (confirm_passedit != null) {
                        confirm_passedit.setError("Password must have at least 6 characters")
                    };
                    if (confirm_passedit != null) {
                        confirm_passedit.requestFocus()
                    };
                    return@setOnClickListener;
                }


                if (password != confirm_password) {

                    Toast.makeText(
                        this,
                        "Password didnt match",
                        Toast.LENGTH_SHORT
                    ).show();}

                else {

                    // Check if an image is selected

                    if (selectedImageUri != null) {
                        ProgressDialog.show()
                        // Upload the image to Firebase Storage
                        imageRef.putFile(selectedImageUri!!)
                            .addOnSuccessListener { taskSnapshot ->
                                // Get the download URL
                                imageRef.downloadUrl
                                    .addOnSuccessListener { uri ->
                                        val imageUrl = uri.toString()

                                        // Update the userdata map with the image URL
                                        val userdata = mapOf(
                                            "Profile Picture" to imageUrl,
                                            "Name" to name,
                                            "Password" to password,
                                            "Phone" to phone,

                                            )

                                        // Update the user data in the Realtime Database
                                        FirebaseDatabase.getInstance().getReference("Registered users")
                                            .child(currentUID)
                                            .updateChildren(userdata)
                                            .addOnSuccessListener {
                                                Toast.makeText(
                                                   this,
                                                    "Saved Successfully",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                ProgressDialog.hide()

                                            }
                                            .addOnFailureListener {

                                                Toast.makeText(
                                                    this,
                                                    "Failed to save ",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                ProgressDialog.hide()
                                            }
                                    }
                                    .addOnFailureListener { exception ->
ProgressDialog.hide()
                                        // Handle the failure to get download URL
                                        Toast.makeText(
                                            this,
                                            "Failed to get image URL",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                            }

                    }



                    else {     ProgressDialog.show()

                        val userdata = mapOf(
                            "Profile Picture" to null,
                            "Name" to name,
                            //"adfasd" to "bafasd",
                            //  "Email" to email,
                            "Password" to password,
                            "Phone" to phone,


                            //"UID" to uid
                        )

                        FirebaseDatabase.getInstance().getReference("Registered users")
                            .child(currentUID)
                            .updateChildren(userdata).addOnSuccessListener {
                                Toast.makeText(applicationContext, "Saved Successfully", Toast.LENGTH_LONG).show()
                                ProgressDialog.hide()
                            }.addOnFailureListener() {

                                Toast.makeText(this, "Failed to save ", Toast.LENGTH_LONG).show()
                                ProgressDialog.hide()
                            }



                    }
                }
            }
        }

    }



    fun SetupExistingDatas(){
        FirebaseDatabase.getInstance().getReference("Registered users").child(currentUID)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Handle the data when it changes
                    if (snapshot.exists()) {
                        // The snapshot contains the data for the specified user
                        val name = snapshot.child("Name").value.toString()
                        val email = snapshot.child("Email").getValue(String::class.java)
                        var dp=snapshot.child("Profile Picture").getValue(String::class.java)
                        val password=snapshot.child("Password").getValue(String::class.java)
                        val phone=snapshot.child("Phone").getValue(String::class.java)
                        //   var locationname=snapshot.child("Location").getValue(String::class.java)
                        findViewById<TextInputEditText>(R.id.passedit)?.setText(password)
                            findViewById<TextInputEditText>(R.id.confirmpassedit)?.setText(password)
                        findViewById<TextInputEditText>(R.id.phoneEditText)?.setText(phone)
                        findViewById<TextInputEditText>(R.id.nameedit)?.setText(name)

                        // editText.text = Editable.Factory.getInstance().newEditable(locationname)
                        //println("Name: $name, Email: $email")

                        if (dp != null) {
                            // Load the image into an ImageView using Glide (or any other method you prefer)
                            Glide.with(applicationContext)
                                .load(dp)
                                .into(editprofileimg)

                        } else {
                            editprofileimg.setImageResource(R.drawable.blankpp)

                        }
                    } else {

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle errors, if any
                    println("Error: ${error.message}")
                }
            })


    }





    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == gallery_request_code && resultCode == RESULT_OK && data != null) {
            // selectedImageUri = data.data
            // You can set the selected image to your ImageView
            editprofileimg.setImageURI(data.data)
            selectedImageUri=data.data

        }
    }





}