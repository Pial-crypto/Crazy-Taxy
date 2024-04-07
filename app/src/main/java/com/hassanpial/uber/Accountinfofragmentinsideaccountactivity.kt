package com.hassanpial.uber

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.gms.maps.GoogleMap
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Accountinfofragmentinsideaccountactivity.newInstance] factory method to
 * create an instance of this fragment.
 */
class Accountinfofragmentinsideaccountactivity : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var selectedImageUri: Uri? =null
    // Declare variables at the class level
    private lateinit  var editprofileimg:ImageView
    var userok=true
    private  final  var gallery_request_code=1
    var currentUID=FirebaseAuth.getInstance().uid.toString()
    private lateinit var auth: FirebaseAuth


    //private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var map: GoogleMap
    //private lateinit var placesClient: PlacesClient
    private lateinit var searchEditText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
       var view= inflater.inflate(R.layout.fragment_accountinfofragmentinsideaccountactivity, container, false)

















        // Declare and initialize views with var and findViewById
        val passedit = view?.findViewById<TextInputEditText>(R.id.passedit)
        val confirm_passedit = view?.findViewById<TextInputEditText>(R.id.confirmpassedit)
        val edit_phone = view?.findViewById<TextInputEditText>(R.id.phoneEditText)
        val editname = view?.findViewById<TextInputEditText>(R.id.nameedit)
     editprofileimg = view?.findViewById<ImageView>(R.id.profileImageView)!!
        val editprofilebtn = view?.findViewById<Button>(R.id.editPictureButton)
        val save = view?.findViewById<Button>(R.id.saveButton)
        val editbuttonpass = view?.findViewById<ImageButton>(R.id.editButtonpassword)
        val editButtonconfirmpass = view.findViewById<ImageButton>(R.id.editButtonconfirmpass)
        val editButtonphone = view?.findViewById<ImageButton>(R.id.editButtonphone)
        val editButtonname = view?.findViewById<ImageButton>(R.id.editButtonname)


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
            Toast.makeText(requireContext(), "User data empty", Toast.LENGTH_SHORT).show()



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

        view?.findViewById<Button>(R.id.removePictureButton)!!.setOnClickListener(){
            if (editprofileimg != null) {
                editprofileimg.setImageResource(R.drawable.blankpp)
            }
        }

var ProgressDialog=ProgressDialog(requireContext())
        ProgressDialog.setTitle("Saving")
        ProgressDialog.setMessage("Updating your informations")


        if (save != null) {
            save.setOnClickListener(){
                ProgressDialog.show()


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

                if (phone.length != 11 || phone[0] != '0' || phone[1] != '1') {
                    canwego = false
                    if (edit_phone != null) {
                        edit_phone.setError("Enter a valid number")
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
                        requireContext(),
                        "Password didnt match",
                        Toast.LENGTH_SHORT
                    ).show();}

                else {
    // Check if an image is selected
                    if (selectedImageUri != null) {
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
                                                    requireContext(),
                                                    "Saved Successfully",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                ProgressDialog.hide()

                                            }
                                            .addOnFailureListener {

                                                Toast.makeText(
                                                    requireContext(),
                                                    "Failed to save ",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                ProgressDialog.hide()
                                            }
                                    }
                                    .addOnFailureListener { exception ->

                                        // Handle the failure to get download URL
                                        Toast.makeText(
                                            requireContext(),
                                            "Failed to get image URL",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                            }

                    }



                    else {

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
                                Toast.makeText(requireContext(), "Saved Successfully", Toast.LENGTH_LONG).show()
                                ProgressDialog.hide()
                            }.addOnFailureListener() {
                                ProgressDialog.hide()
                                Toast.makeText(requireContext(), "Failed to save ", Toast.LENGTH_LONG).show()
                            }



                    }
                }
            }
        }


return view

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
                        view?.findViewById<TextInputEditText>(R.id.passedit)?.setText(password)
                        view?.findViewById<TextInputEditText>(R.id.confirmpassedit)?.setText(password)
                        view?.findViewById<TextInputEditText>(R.id.phoneEditText)?.setText(phone)
                        view?.findViewById<TextInputEditText>(R.id.nameedit)?.setText(name)

                        // editText.text = Editable.Factory.getInstance().newEditable(locationname)
                        //println("Name: $name, Email: $email")

                        if (dp != null) {
                            // Load the image into an ImageView using Glide (or any other method you prefer)
                            Glide.with(requireContext())
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













    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Accountinfofragmentinsideaccountactivity.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic fun newInstance(param1: String, param2: String) =
                Accountinfofragmentinsideaccountactivity().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}