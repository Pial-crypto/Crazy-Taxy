package com.hassanpial.uber

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AccountFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
       var view= inflater.inflate(R.layout.fragment_account, container, false)


        FirebaseDatabase.getInstance().getReference("Registered users").child(FirebaseAuth.getInstance().uid.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Handle the data when it changes
                    if (snapshot.exists()) {
                        // The snapshot contains the data for the specified user
                        val name = snapshot.child("Name").value.toString()
                     //   val email = snapshot.child("Email").getValue(String::class.java)
                        var dp=snapshot.child("Profile Picture").getValue(String::class.java)
                        //val password=snapshot.child("Password").getValue(String::class.java)
                       // val rating=snapshot.child("Rating").getValue(String::class.java)
                        //   var locationname=snapshot.child("Location").getValue(String::class.java)
                    //    findViewById<TextInputEditText>(R.id.passedit)?.setText(password)
                       // findViewById<TextInputEditText>(R.id.confirmpassedit)?.setText(password)
                        //findViewById<TextInputEditText>(R.id.phoneEditText)?.setText(phone)
                        view?.findViewById<TextView>(R.id.name_text)?.setText(name)
                      //  view?.findViewById<TextView>(R.id.rating_text)?.text=rating
                        // editText.text = Editable.Factory.getInstance().newEditable(locationname)
                        //println("Name: $name, Email: $email")
var imageview=view?.findViewById<ShapeableImageView>(R.id.profile_image)


                        if (dp != null) {
                            // Load the image into an ImageView using Glide (or any other method you prefer)
                            Glide.with(requireContext())
                                .load(dp)
                                .into(imageview!!)

                        } else {
                            if (imageview != null) {
                                imageview.setImageResource(R.drawable.blankpp)
                            }

                        }
                    } else {

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle errors, if any
                    println("Error: ${error.message}")
                }
            })

        var activity=view?.findViewById<CardView>(R.id.activity)
        if (activity != null) {
            activity.setOnClickListener(){
                 var intent=Intent(requireContext(),ApplicationHomePage::class.java)
                intent.putExtra("Bal","Cal")
                startActivity(intent)

            }
        }



        var help= view?.findViewById<CardView>(R.id.help)
        if (help != null) {
            help.setOnClickListener(){
                Toast.makeText(requireContext(),"This feature is coming soon",Toast.LENGTH_SHORT).show()
            }
        }

        var payment=view?.findViewById<CardView>(R.id.payment)
        if (payment!= null) {
            payment.setOnClickListener(){
                Toast.makeText(requireContext(),"Online payment system is coming soon",Toast.LENGTH_SHORT).show()
            }
        }
var currentuid=FirebaseAuth.getInstance().uid.toString()
        var accountactivity=view?.findViewById<TextView>(R.id.name_text)
        if (accountactivity != null) {
            accountactivity.setOnClickListener(){
                startActivity(Intent(requireContext(),accountinformationactivity::class.java))
            }
        }
        val firebaseAuth = FirebaseAuth.getInstance()
        var settings=view?.findViewById<Button>(R.id.settings_button)
        var logout=view?.findViewById<Button>(R.id.logout_button)
        var trmandpolicy=view?.findViewById<Button>(R.id.terms_policy_button)
var close=view?.findViewById<ImageButton>(R.id.close_button)
        if (close != null) {
            close.setOnClickListener(){
startActivity(Intent(requireContext(),ApplicationHomePage::class.java))
            }
        }
        if (logout != null) {
          logout.setOnClickListener(){
             // Toast.makeText(requireContext(),"asdfasdfadsfafadfa",Toast.LENGTH_SHORT).show()
              FirebaseDatabase.getInstance().getReference("Registered users").child(currentuid).child("Token").setValue(" ")
                firebaseAuth.signOut()
              val intent = Intent(requireContext(),MainActivity::class.java)
            //  intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
             //Toast.makeText(requireContext(),intent.flags,Toast.LENGTH_SHORT).show()
              startActivity(intent)
              requireActivity().finishAffinity()
            }
           //
        }


        if (settings != null) {
          settings.setOnClickListener(){

              startActivity(Intent(requireContext(),Settings::class.java))
            }
          //
        }

        if (trmandpolicy!= null) {
            trmandpolicy.setOnClickListener(){
                startActivity(Intent(requireContext(),termsandpolicy::class.java))
            }
           //
        }


    return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AccountFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AccountFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}