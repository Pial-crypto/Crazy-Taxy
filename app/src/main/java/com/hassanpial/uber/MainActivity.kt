package com.hassanpial.uber

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.hassanpial.uber.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Date

class MainActivity : AppCompatActivity() {  private  lateinit  var auth: FirebaseAuth
  var countrycode:String?=null
    private lateinit var binding: ActivityMainBinding
    lateinit var progressDialog:ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //  setContentView(R.layout.activity_main)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressDialog=ProgressDialog(this)
        progressDialog.setTitle("Account creation")
        progressDialog.setMessage("Creating your account")
auth= Firebase.auth



        binding.logintextview.setOnClickListener(){
            startActivity(Intent(this,SignInActivity::class.java))
        }
        // Register the AuthStateListener
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                // User is signed in
                // Perform actions based on signed-in state (e.g., navigate to main screen)
                startActivity(Intent(this, ApplicationHomePage::class.java))
                finish()
            } else {
                // User is signed out
            }
        }
        binding.signUpButton.setOnClickListener(){






            var email=binding.emailEditText.text.toString()
            var password=binding.passwordEditText.text.toString()
            var phone=binding.phoneEditText.text.toString()
            var name=binding.nameEditText.text.toString()
            var confirm_password=binding.confirmpasswordEditText.text.toString()



                var canwego = true


                ///checking the validity of name
                if (name.isEmpty()) {
                    canwego = false

                    binding.nameEditText.setError("Enter your name");
                    binding.nameEditText.requestFocus();
                    return@setOnClickListener;
                }

                if (name.length < 4)
                {
                    canwego = false
                    binding.nameEditText.setError("Name is too short.Enter at least 6 characters");
                    binding.nameEditText.requestFocus();
                    return@setOnClickListener;
                }



            if(countrycode==null){
                canwego=false
                binding.phoneEditText.setError("Country code is not given")
                binding.phoneEditText.requestFocus()
            }

                //checking the validity of the email
                if (email.isEmpty()) {
                    canwego = false
                    binding.emailEditText.setError("Enter an email address")
                    binding.emailEditText.requestFocus()

                    return@setOnClickListener;
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    canwego = false
                    binding.emailEditText.setError("Enter a valid email address");
                    binding.emailEditText.requestFocus();
                    return@setOnClickListener;
                }


                ///checking the validity of name
                if (phone.isEmpty()) {
                    canwego = false

                    binding.phoneEditText.setError("Enter your phone number");
                    binding.phoneEditText.requestFocus();
                    return@setOnClickListener;
                }

              //  if (gotphone.length != 11 || gotphone[0] != '0' || gotphone[1] != '1') {
                 //   canwego = false
                 //   phone.setError("Enter a valid number");
                 //   phone.requestFocus();
                 //   return@setOnClickListener;
              //  }


                //checking the validity of the password
                if (password.isEmpty()) {
                    canwego = false
                   binding.passwordEditText.setError("Enter a password");
                    binding.passwordEditText.requestFocus();
                    return@setOnClickListener;
                }

                if (password.length < 5) {

                    canwego = false
                    binding.passwordEditText.setError("Password must have at least 6 characters");
                    binding.passwordEditText.requestFocus();
                    return@setOnClickListener;
                }


                ///checking the validity of confirm password


                if (confirm_password.isEmpty()) {
                    canwego = false
                   binding.confirmpasswordEditText.setError("Enter a password");
                    binding.confirmpasswordEditText.requestFocus();
                    return@setOnClickListener;
                }

                if (confirm_password.length < 5) {

                    canwego = false
                    binding.confirmpasswordEditText.setError("Password must have at least 6 characters");
                    binding.confirmpasswordEditText.requestFocus();
                    return@setOnClickListener;
                }







                if (canwego) {







                    if (password != confirm_password) Toast.makeText(
                        this,
                        "Password didnt match",
                        Toast.LENGTH_SHORT
                    ).show();
                    else {


                        if (binding.checkbox.isChecked) {



                            //  rad.putExtra("Phone",gotphone)
                            //  rad.putExtra("Phone",gotphone)

                            ///email: String, password: String,name: String,phone:String
progressDialog.show()
                            createAccount(email,password,name,phone,countrycode.toString())


                        } else {
                            Toast.makeText(
                                this,
                                "Please check if you agree to the terms and conditions",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }





        binding.countryCodeIcon.setOnClickListener(){
            showCountryCodePopupMenu(it)
        }
    }


    private fun showCountryCodePopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.countrycodemenu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.country_code_bangladesh -> {
                        // Update phone number input with the selected country code
                        // For example:
                        // phoneEditText.setText("+1")
                        countrycode=menuItem.title.toString()
                        // Set the text programmatically
                        binding.phoneEditText.setText("+880")

// Enable editing
                        binding.phoneEditText.apply {
                            // Enable focusable and focusable in touch mode
                            isFocusable = true
                            isFocusableInTouchMode = true

                            // Request focus to make the EditText editable
                          requestFocus()
                        }
                      //  Toast.makeText(applicationContext,countrycode,Toast.LENGTH_SHORT).show()
                        true
                    }

                    R.id.country_code_japan -> {
                        // Update phone number input with the selected country code
                        // For example:
                        // phoneEditText.setText("+1")
                        countrycode=menuItem.title.toString()

                        // Set the text programmatically
                        binding.phoneEditText.setText("+81")

// Enable editing
                        binding.phoneEditText.apply {
                            // Enable focusable and focusable in touch mode
                            isFocusable = true
                            isFocusableInTouchMode = true

                            // Request focus to make the EditText editable
                            requestFocus()
                        }
                        true
                    }

                    R.id.country_code_india -> {
                        // Update phone number input with the selected country code
                        // For example:
                        // phoneEditText.setText("+1")
                        countrycode=menuItem.title.toString()
                        // Set the text programmatically
                        binding.phoneEditText.setText("+91")

// Enable editing
                        binding.phoneEditText.apply {
                            // Enable focusable and focusable in touch mode
                            isFocusable = true
                            isFocusableInTouchMode = true

                            // Request focus to make the EditText editable
                            requestFocus()
                        }
                       // Toast.makeText(applicationContext,countrycode,Toast.LENGTH_SHORT).show()

                        true
                    }

                    R.id.country_code_china -> {
                        // Update phone number input with the selected country code
                        // For example:
                        // phoneEditText.setText("+1")
                        countrycode=menuItem.title.toString()
                        // Set the text programmatically
                        binding.phoneEditText.setText("+86")

// Enable editing
                        binding.phoneEditText.apply {
                            // Enable focusable and focusable in touch mode
                            isFocusable = true
                            isFocusableInTouchMode = true

                            // Request focus to make the EditText editable
                            requestFocus()
                        }
                        true
                    }
                    R.id.country_code_usa -> {
                        // Update phone number input with the selected country code
                        // For example:
                        // phoneEditText.setText("+1")
                        countrycode=menuItem.title.toString()
                        // Set the text programmatically
                        binding.phoneEditText.setText("+1")

// Enable editing
                        binding.phoneEditText.apply {
                            // Enable focusable and focusable in touch mode
                            isFocusable = true
                            isFocusableInTouchMode = true

                            // Request focus to make the EditText editable
                            requestFocus()
                        }
                        true
                    }
                    // Add other country code options here
                    else -> false
                }
            }
        })

        popupMenu.show()
    }


    private fun createAccount(email: String, password: String, name: String, phone: String, countrycode: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val uid = user?.uid

                    if (uid != null) {
                        // Send email verification
                        var time = System.currentTimeMillis()
                        // var gap=Long
                        user?.sendEmailVerification()?.addOnCompleteListener { emailTask ->
                            if (emailTask.isSuccessful) {
                                time = System.currentTimeMillis()
                                // Email verification sent successfully
                                // Reload the user's profile to check if email is verified
                                // user?.reload()?.addOnCompleteListener { reloadTask ->
                                //    if (reloadTask.isSuccessful) {
                                //   var gap= (time-System.currentTimeMillis())
                                val user = auth.currentUser
                                if (user != null) {
                                    // User has clicked on the verification link and verified their email
                                    // Proceed with saving user data to database
                                    val userMap = mapOf(
                                        "Name" to name,
                                        "Email" to email,
                                        "Password" to password,
                                        "Phone" to phone,
                                        "UID" to uid,
                                        "Country code" to countrycode,
                                        "Profile picture" to null,
                                        "Token" to FirebaseMessaging.getInstance().token.result.toString()
                                    )

                                    FirebaseDatabase.getInstance().getReference("Registered users")
                                        .child(uid).setValue(userMap)
                                        .addOnSuccessListener {
                                            progressDialog.hide()
                                            // Display success message
                                            Toast.makeText(
                                                baseContext,
                                                "Successfully created account. Verification email sent.",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            startActivity(Intent(this,ApplicationHomePage::class.java));
                                        }
                                        .addOnFailureListener {
                                            // Handle failure to save user data
                                            //    Toast.makeText(
                                            //     baseContext,
                                            //    "Failed to save user data to database",
                                            //    Toast.LENGTH_SHORT
                                            // ).show()
                                            progressDialog.hide()
                                            user.delete()
                                            Toast.makeText(applicationContext,"Failed to save user data to official database",Toast.LENGTH_SHORT).show()
                                        }
                                    //        } else {
                                    // if (user != null) {
                                    //     user.delete()
                                    //  }
                                    // User's email is not verified yet
                                    //  Toast.makeText(
                                    //    baseContext,
                                    //   "Please verify your email address to complete the registration",
                                    //   Toast.LENGTH_SHORT
                                    // ).show()
                                    // }
                                    //  }// else {
                                    //   user.delete()
                                    // Toast.makeText(applicationContext,"")
                                    // Failed to reload user information
                                    // Handle the failure
                                    //  }
                                    // }
                                } else {
                                    progressDialog.hide()
                                    user?.delete()
                                    // Failed to send verification email
                                    Toast.makeText(
                                        baseContext,
                                        "Failed to send verification email",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }?.addOnFailureListener(){
Toast.makeText(this,"User is already registered",Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        progressDialog.hide()
                        // Handle account creation failure
                        Log.e("createAccount", "createUserWithEmail:failure", task.exception)
                        val exception = task.exception
                        if (exception is FirebaseAuthUserCollisionException) {
                            Toast.makeText(
                                this,
                                "This email was already registered",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            progressDialog.hide()
                            Toast.makeText(baseContext, "Authentication failed", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
    }



    private fun signIn() {auth.addAuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        if (user != null) {
          //  startActivity(Intent(this,loggedin_home_page::class.java))
            // User is signed in
            // You can navigate to the main screen or perform other actions
            // based on the signed-in state
        } else {
            // User is signed out
        }
    }

    }


    fun generateChatRoomId(user1Id: String, user2Id: String): String {
        val sortedUserIds = listOf(user1Id, user2Id).sorted().joinToString("_")
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val randomChars = (1..4).map { ('A'..'Z').random() }.joinToString("")
        return "CHATROOM_$sortedUserIds$timestamp$randomChars"
    }

}