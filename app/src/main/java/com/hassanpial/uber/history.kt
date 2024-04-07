package com.hassanpial.uber

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hassanpial.uber.databinding.FragmentHistoryBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [history.newInstance] factory method to
 * create an instance of this fragment.
 */
class history : Fragment() {

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentHistoryBinding? = null
private lateinit var auth:FirebaseAuth
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
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        auth=FirebaseAuth.getInstance()
        val passengerUid = FirebaseAuth.getInstance().currentUser?.uid.toString()

// Reference to the "Trips" node for the passenger
        val tripsRef = FirebaseDatabase.getInstance().getReference("Registered users").child(passengerUid).child("Trips")

// Query to retrieve trips sorted by timesnap
        val query = tripsRef.orderByChild("starting_time")
        var myhistory: MutableList<requestsclass> = mutableListOf()

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Iterate through each trip
                for (tripSnapshot in dataSnapshot.children) {
                    var triprating=tripSnapshot.child("triprating").getValue(String::class.java)
                    var id=tripSnapshot.key
                    // Extract trip details
                    var triptype=tripSnapshot.child("triptype").getValue(String::class.java)
                    //ratinggiven
                    var ratinggiven=tripSnapshot.child("ratinggiven").getValue(String::class.java)
                    //val time = tripSnapshot.child("time").getValue(Long::class.java)
                    val driverName = tripSnapshot.child("driver_name").getValue(String::class.java)
                    val driverUid = tripSnapshot.child("driver_uid").getValue(String::class.java)
                    //   val acceptingStatus = tripSnapshot.child("Accepting status").getValue(String::class.java)
                    val passengerUid = tripSnapshot.child("passenger_uid").getValue(String::class.java)
                    val startingTime = tripSnapshot.child("starting_time").getValue(String::class.java)
                    var status = tripSnapshot.child("status").getValue(String::class.java)
                    if(status=="unreached") status="On progress"

                    else if(status=="not accepted till now") status="Waiting for the drivers response"
                    else if(status=="reached")status="You were successfully delivered"
                    val phone = tripSnapshot.child("driver_phone").getValue(String::class.java)
                    var destination=tripSnapshot.child("destination").getValue(String::class.java)
                    var pickuplocation=tripSnapshot.child(      "pickuplocation").getValue(String::class.java)
                    var price=tripSnapshot.child("price").getValue(String::class.java)
                    if (status=="cancelled") status="Driver cancelled your request"
                    myhistory.add(requestsclass(ratinggiven.toString(),triprating.toString(),triptype.toString(),driverName.toString(),id.toString(),phone.toString(),startingTime.toString(),price.toString(),destination.toString(),pickuplocation.toString(),status.toString(),driverUid.toString()))


                    // Do something with the trip details
                    // println("Time: $time, Driver Name: $driverName, Driver UID: $driverUid, Accepting Status: $acceptingStatus, Passenger UID: $passengerUid, Starting Time: $startingTime, Status: $status")
                }
                myhistory = myhistory.sortedByDescending { it.tm }.toMutableList()
                if(myhistory.isNotEmpty()) {
                    var adapter = recyclerviewsadapter2(requireContext(),myhistory)
                    _binding!!.recyclerViewhistories.layoutManager=LinearLayoutManager(requireContext())
                    _binding!!.recyclerViewhistories.adapter = adapter
                    _binding!!.ifhistoryexists.text="Heres your activites"




                }
                else{
                    _binding!!.ifhistoryexists.text ="You dont have any activity now"
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
                println("Error: ${databaseError.message}")
            }
        })


        return _binding!!.root
        //return inflater.inflate(R.layout.fragment_history, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment history.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            history().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}