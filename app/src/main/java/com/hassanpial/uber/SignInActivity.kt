package com.hassanpial.uber

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging

class SignInActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("515003874919-pcpt9dk5vc6tduk4cvfokc3mhg3k2asj.apps.googleusercontent.com")
            .requestEmail()
            .build()

     //   var sign_in_with_google = findViewById<TextView>(R.id.sign_in_with_google)
       // googleSignInClient = GoogleSignIn.getClient(this, gso)

       // sign_in_with_google.setOnClickListener() {
        //    signInGoogle()
       //}
var progressDialog=ProgressDialog(this)
        progressDialog.setTitle("Loggin in")
        progressDialog.setMessage("Please wait until you are logged into your account")
        var input_email = findViewById<EditText>(R.id.signinemail)
        var input_pass = findViewById<EditText>(R.id.signinpassword)


        var login_button = findViewById<Button>(R.id.logintoprofile)
        login_button .setOnClickListener {


            val email = input_email.text.toString().trim()
            val pass = input_pass.text.toString().trim()
            if (email.isNotEmpty() && pass.isNotEmpty()) {
                progressDialog.show()

                login_button.visibility=View.INVISIBLE
                auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            val uid = user?.uid

                            if (uid != null) {
                                // Get the new token
                                FirebaseMessaging.getInstance().token.addOnSuccessListener { newToken ->
                                    if (newToken != null) {
                                        // Update the user's token in the database
                                        FirebaseDatabase.getInstance()
                                            .getReference("Registered drivers")
                                            .child(uid)
                                            .child("Token")
                                            .setValue(newToken)
                                            .addOnSuccessListener {
                                                // Token updated successfully
                                                // Proceed with your login logic
                                                startActivity(Intent(this,ApplicationHomePage::class.java))
                                                progressDialog.hide()
                                                finish()
                                            }
                                            .addOnFailureListener { e ->
                                                // Failed to update token
                                                // Handle the failure
                                            }
                                    }
                                }
                            }
                        } else {
                            // Login failed
                            // Handle the failure
                        }

                        // Authentication successful, navigate to the next activity
                       // val intent = Intent(this, ApplicationHomePage::class.java)
                        //startActivity(intent)

                    } else {
                        // Authentication failed
progressDialog.hide()
                        login_button.visibility=View.VISIBLE
                        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                            input_email.setError("Enter a valid email address");
                            input_email.requestFocus();

                        } else {
                            val errorCode = (task.exception as? FirebaseAuthException)?.errorCode
                            when (errorCode) {
                                "ERROR_INVALID_EMAIL" -> {
                                    Toast.makeText(
                                        this,
                                        "Invalid email address",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                "ERROR_WRONG_PASSWORD" -> {
                                    Toast.makeText(this, "Wrong password", Toast.LENGTH_SHORT).show()
                                }
                                "ERROR_USER_NOT_FOUND" -> {
                                    Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                                }
                                else -> {
                                    Toast.makeText(this, "User not registered or wrong password", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResults(task)
            } else {
                Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
            }
        }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                Googlelogin(account)
            }
        } else {
            Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun Googlelogin(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        try {
            auth.signInWithCredential(credential).addOnCompleteListener {

                if (it.isSuccessful) {
                    startActivity(Intent(this, ApplicationHomePage::class.java))
                } else {
                    Toast.makeText(this,account.email , Toast.LENGTH_SHORT).show()
                }

            }

        }catch (e:Exception){

            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun login(email: String, pass: String) {
        // calling signInWithEmailAndPassword(email, pass)
        // function using Firebase auth object
        // On successful response Display a Toast
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Successfully Logged In", Toast.LENGTH_SHORT).show()
            } else {
                // If there is an error, you can get the error message using task.exception?.message
                Toast.makeText(this, "Log In failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

}