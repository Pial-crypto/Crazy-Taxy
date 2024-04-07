package com.hassanpial.uber

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.maps.model.LatLng
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AirportDropoffragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class AirportDropoffragment : Fragment() ,recyclerviewsadapter.OnItemClickListener{
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var formattedTime: String? =null
var tocheckisbeforetime: String? =null
    var descriptiondetails:String?=null
    var selectedVehicle="Not selected"
    private lateinit var nearbyDriversFinder: NearbyDriversFinder
    private lateinit var alertDialog: AlertDialog
    var price=0;
    var distance="0";
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
        var view= inflater.inflate(R.layout.fragment_airport_dropoffragment, container, false)
        var time=view.findViewById<TextView>(R.id.time)
        // Set OnClickListener to show the popup


        var pricetextview=view.findViewById<TextView>(R.id.pricetextview)
       var description=view.findViewById<EditText>(R.id.descriptonEditText)



     var pickuptextlocationtextview=view.findViewById<TextView>(R.id.pickUpTextView)
        if(param1!=null && param2!=null) {
            description.setText(param2)
            pickuptextlocationtextview.text = param1
        }
        time.setOnClickListener {
            if(pickuptextlocationtextview.text!="Pickup From" ) {
              //  Toast.makeText(requireContext(),pickuptextlocationtextview.text,Toast.LENGTH_SHORT).show()
                showDateTimePicker(time)
            }
            else{
                Toast.makeText(requireContext(),"First select the location from where you will be picked up",Toast.LENGTH_SHORT).show()
            }
        }
        pickuptextlocationtextview.setOnClickListener {
            var descriptiontext=description.text.toString()
            var intent=Intent(requireContext(),Mapsactivityairport::class.java)
            intent.putExtra("Description",descriptiontext)
            intent.putExtra("Came from","DROPOFF")
            //Toast.makeText(requireContext(),"$descriptiontext+ dasfds",Toast.LENGTH_SHORT).show()
            startActivity(intent)



        }

        val selectVehicleButton = view.findViewById<Button>(R.id.select_vehicle_button)

        selectVehicleButton.setOnClickListener { if(pickuptextlocationtextview.text!="Select destination" ) {
            //val inflater = getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val popupView = inflater.inflate(R.layout.vehicleselectedpopup, null)

            // Calculate the width and height of the PopupWindow
            val widthPixels = resources.displayMetrics.widthPixels
            val heightPixels = resources.displayMetrics.heightPixels / 6  // Adjust height as needed

            val popupWindow = PopupWindow(
                popupView,
                widthPixels,
                heightPixels,
                true // Focusable
            )

            // Inflate the menu
            //val menu = popupView.findViewById<Menu>(R.id.popup_menu)
            //menuInflater.inflate(R.menu.vehicle_menu, menu)

            // Show the popup window at the bottom of the screen
            popupWindow.showAtLocation(
                selectVehicleButton,
                Gravity.BOTTOM,
                0,
                100,
            )

            val button1 = popupView.findViewById<Button>(R.id.bike_button)
            val button2 = popupView.findViewById<Button>(R.id.private_car_button)
            var button3 = popupView.findViewById<Button>(R.id.cng_button)

            button1.setOnClickListener {
                // Button 1 click logic
                selectedVehicle="Bike"
                Toast.makeText(requireContext(),"Bike selected",Toast.LENGTH_SHORT).show()
                selectVehicleButton.text="Bike"
            }

            button2.setOnClickListener {
                // Button 2 click logic
                selectedVehicle="Private car"
                Toast.makeText(requireContext(),"Private car selected",Toast.LENGTH_SHORT).show()
                selectVehicleButton.text="Private car"
            }
            button3.setOnClickListener {
                selectedVehicle="CNG"
                Toast.makeText(requireContext(),"CNG selected",Toast.LENGTH_SHORT).show()
                selectVehicleButton.text="CNG"
            }

        }

            else{
            Toast.makeText(requireContext(),"First select your destination",Toast.LENGTH_SHORT).show()
        }
        }

        val airportLocation = Location("Hazrat Shahjalal International Airport")
        airportLocation.latitude = 23.8486
        airportLocation.longitude = 90.4059
        nearbyDriversFinder = NearbyDriversFinder()
        var confirmbutton=view.findViewById<Button>(R.id.confirm)
        confirmbutton.setOnClickListener {
            if( pickuptextlocationtextview.text!="Select destination" &&tocheckisbeforetime!=null && selectedVehicle!="Not selected") {
                if (!isBefore(tocheckisbeforetime!!)){
                var ProgressDialog = ProgressDialog(requireContext())
                ProgressDialog.setMessage("Searching nearby drivers")

                /**searching driver
                 *
                 */

descriptiondetails=description.text.toString()
                ProgressDialog.show()
                // Create an instance of OnNearbyDriversFoundListener
                val listener = object : NearbyDriversFinder.OnNearbyDriversFoundListener {
                    override fun onNearbyDriversFound(nearbyDrivers: MutableList<Driver>) {
                        if (nearbyDrivers.isNotEmpty()) {
                            // Inflate the custom layout for the dialog
                            val inflater = LayoutInflater.from(requireContext())
                            val dialogView = inflater.inflate(R.layout.customdialogue, null)


                            // Find the ListView in the custom layout
                            val RecyclerView = dialogView.findViewById<RecyclerView>(R.id.listView)
                            RecyclerView.layoutManager = LinearLayoutManager(requireContext())
                            // Set the adapter to the ListView

                            var distancetextview =
                                view.findViewById<TextView>(R.id.distancetextview)
                            var desiredlatlng = getLatLngFromAddress(
                                requireContext(),
                                pickuptextlocationtextview.text.toString()
                            )

                            distance = String.format("%.2f",
                                desiredlatlng?.let { it1 ->
                                    calculateDistanceInKilometers(
                                        airportLocation.latitude, airportLocation.longitude,
                                        it1.lat, desiredlatlng.lng
                                    )
                                })
                            distancetextview.setText(distance + " Km")


                            price = (calculateDistanceInKilometers(
                                airportLocation.latitude,
                                airportLocation.longitude,
                                desiredlatlng!!.lat,
                                desiredlatlng.lng
                            ) * 22.30).toInt()
                            pricetextview.text = price.toString() + " Tk"

                            var adapter = com.hassanpial.uber.recyclerviewsadapter(
                                nearbyDrivers,
                                distance.toString() + " Km",
                                price.toString() + " TK"
                            )
                            RecyclerView.adapter = adapter
                            adapter.setOnItemClickListener(this@AirportDropoffragment)







                            ProgressDialog.hide()



                            Toast.makeText(
                                requireContext(),
                                "Found ${nearbyDrivers.size} drivers",
                                Toast.LENGTH_SHORT
                            ).show()

                            // Create the dialog
                            val dialogBuilder = AlertDialog.Builder(requireContext())
                            dialogBuilder.setView(dialogView)
                                .setTitle("Choose a driver")
                                .setNegativeButton("Cancel") { dialog, _ ->
                                    dialog.dismiss()
                                }

                            // Show the dialog
                            alertDialog = dialogBuilder.create()
                            alertDialog.show()


                        } else {
                            ProgressDialog.hide()

                            Toast.makeText(
                                requireContext(),
                                "No driver found nearby",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                createLocationFromTextView(
                    requireContext(),
                    pickuptextlocationtextview
                )?.let { it1 ->
                    nearbyDriversFinder.findNearbyDrivers(
                        it1, 5000, listener, selectedVehicle
                    )
                }


            }



            }
            else{


                 if(selectedVehicle=="Not selected"){
                    Toast.makeText(
                        requireContext(),
                        "Kindly enter your desired vehicle",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Toast.makeText(requireContext(),"Please provide all the correct informations",Toast.LENGTH_SHORT).show()
            }
        }


        return  view
    }


    fun getLatLngFromAddress(context: Context, address: String): LatLng? {
        val geocoder = Geocoder(context)
        try {
            val addressList: List<Address>? = geocoder.getFromLocationName(address, 1)
            if (addressList != null && addressList.isNotEmpty()) {
                val location = addressList[0]
                return LatLng(location.latitude, location.longitude)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }


    fun createLocationFromTextView(context: Context, locationNameTextView: TextView): Location? {
        val locationName = locationNameTextView.text.toString()
        val geocoder = Geocoder(context)

        try {
            val addressList: List<Address>? = geocoder.getFromLocationName(locationName, 1)
            if (addressList != null && addressList.isNotEmpty()) {
                val address = addressList[0]
                val latitude = address.latitude
                val longitude = address.longitude

                val location = Location(locationName)
                location.latitude = latitude
                location.longitude = longitude

                return location
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }
    fun calculateDistanceInKilometers(
        startLatitude: Double,
        startLongitude: Double,
        endLatitude: Double,
        endLongitude: Double
    ): Double {
        val earthRadius = 6371 // Radius of the Earth in kilometers
        val deltaLatitude = Math.toRadians(endLatitude - startLatitude)
        val deltaLongitude = Math.toRadians(endLongitude - startLongitude)
        val a = sin(deltaLatitude / 2) * sin(deltaLatitude / 2) +
                cos(Math.toRadians(startLatitude)) * cos(Math.toRadians(endLatitude)) *
                sin(deltaLongitude / 2) * sin(deltaLongitude / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }


    private fun showDateTimePicker(timeTextView: TextView) {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, monthOfYear, dayOfMonth ->
                val timePickerDialog = TimePickerDialog(
                    requireContext(),
                    { _, hourOfDay, minute ->
                        val selectedTimeInMillis = Calendar.getInstance().apply {
                            set(year, monthOfYear, dayOfMonth, hourOfDay, minute, 0) // Set seconds to 0
                        }.timeInMillis

                        formattedTime =formatDateTimeFromMillis(selectedTimeInMillis)
                        Toast.makeText(requireContext(), formattedTime, Toast.LENGTH_SHORT).show()

                   timeTextView.text = formattedTime
                        tocheckisbeforetime = "$year-$monthOfYear-$dayOfMonth $hourOfDay:$minute"
                    },
                    currentHour,
                    currentMinute,
                    false
                )
                timePickerDialog.setTitle("Select Time")
                timePickerDialog.show()
            },
            currentYear,
            currentMonth,
            currentDay
        )

        datePickerDialog.setTitle("Select Date")
        datePickerDialog.show()
    }

    fun isBefore(dateTimeString: String): Boolean {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val currentTime = System.currentTimeMillis()

        // Parse the provided date-time string
        val providedDateTime = format.parse(dateTimeString)

        // Check if the current time is before the provided date-time
        return currentTime < providedDateTime.time
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AirportDropoffragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AirportDropoffragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onItemClick(position: Int, driver: Driver) {
       // findViewById<Button>(R.id.confirmpickupbutton).visibility=View.VISIBLE
       // findViewById<TextView>(R.id.pricetextview).visibility=View.VISIBLE



        var tripDatabase = FirebaseDatabase.getInstance().getReference("Trips").child("Long trips")

        // val tripId = tripDatabase.push().key

        // Get current time
        //val currentTime = System.currentTimeMillis()

        // Example data
        val driverName = driver.Name
        val driverUid = driver.UID.toString()

        val passengerUid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val startingTime = formattedTime // Replace with actual starting time
        val status = "not accepted till now" // Status can be "reached" or "unreached"

        // Create a map to hold trip details
        val tripDetails = HashMap<String,Any>()
        var trpdetails2= mapOf(
            "sfasd" to "fafadsfa"
        )
        //  tripDetails["time"] = currentTime.
        tripDetails["driver_name"] = driverName
        tripDetails["driver_uid"] = driverUid

        val passengerNameRef = FirebaseDatabase.getInstance().getReference("Registered users").child(passengerUid).child("Name")

        passengerNameRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val passengerName = dataSnapshot.value.toString()
                // Now you can use the passengerName value
                tripDetails["passengerName"]=passengerName.toString()
                //  println("Passenger Name: $passengerName")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors here
                println("Failed to read passenger name: ${databaseError.message}")
            }
        })
        tripDetails["Vehicle"]=selectedVehicle
     //   tripDetails["additionaldescription"]=description
        tripDetails["additionaldescription"]=descriptiondetails.toString()
        tripDetails["destination"]="Hazrat Shahjalal International Airport"
        tripDetails["pickuplocation"]= param1.toString()
        tripDetails["passenger_uid"] = passengerUid
        tripDetails["starting_time"] = startingTime.toString()
        tripDetails["status"] = status
        tripDetails["driver_phone"]=driver.Phone
        tripDetails["price"]= price.toString()
        tripDetails["triptype"]="Long trips"
        tripDetails["triprating"]="0"
        tripDetails["distance"]=distance.toString()
        tripDetails["ratinggiven"]="false"
        val passengerphoneRef = FirebaseDatabase.getInstance().getReference("Registered users").child(passengerUid).child("Phone")

        passengerphoneRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val passengerPhone = dataSnapshot.value.toString()
                // Now you can use the passengerName value
                tripDetails["passenger_phone"]=passengerPhone.toString()
                //println("Passenger Name: $passengerName")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors here
                println("Failed to read passenger name: ${databaseError.message}")
            }
        })





        /*
        tripDetails["destination"]=destinationlocation.toString()
                tripDetails["pickuplocation"]=userLocation.toString()
                // Write trip details to the database
                tripId?.let {
                   tripDatabase.child(it).setValue(tripDetails)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Trip created successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to create trip", Toast.LENGTH_SHORT).show()
                        }
                }

         */


        tripDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    val tripId = tripDatabase.push().key
                    tripId?.let { id ->
                        tripDetails["trip_id"] = tripId
                        tripDatabase.child(id).setValue(tripDetails).addOnSuccessListener {  }.addOnFailureListener{}
                        FirebaseDatabase.getInstance().getReference("Registered users").child(passengerUid).child("Trips").child(id).setValue(tripDetails).addOnSuccessListener {  }.addOnSuccessListener {  }
                        FirebaseDatabase.getInstance().getReference("Registered drivers").child(driverUid).child("Trips").child("Long trips").child(id).setValue(tripDetails).addOnSuccessListener {  }.addOnFailureListener{}
                        Toast.makeText(requireContext(), "Trip created successfully", Toast.LENGTH_SHORT).show()
                        //  alertDialog.dismiss()
                    }
                } catch (e: IllegalStateException) {
                    // Handle the exception, for example:
                    Log.e("Error", "Task is not yet complete", e)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TripCreation", "Failed to read value.", databaseError.toException())
            }
        })




//FirebaseDatabase.getInstance().getReference("Registered users").child(passengerUid).child("Trips").child(tripId.toString()).setValue(tripDetails)
        //      FirebaseDatabase.getInstance().getReference("Registered drivers").child(driverUid).child("Trips").child("Short trips").child(tripId.toString()).setValue(tripDetails)







        //   sendnoti(driver.Token.toString(),driver.UID.toString(),"ddfasd","adsfasdfasdfsd")
        ///  Toast.makeText(this,"Pressed on something",Toast.LENGTH_SHORT).show()
        if (driver.Token.toString()!=" ")
            sendNotificationToDriver(driver.Token.toString(),"You have received an airport ride request","One passenger has selected you to pickup from $param1 and drop to airport")
        alertDialog.dismiss()
    }
    fun formatDateTimeFromMillis(timeInMillis: Long): String {
        val sdf = android.icu.text.SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val calendar = android.icu.util.Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        return sdf.format(calendar.time)
    }

    fun sendNotificationToDriver(userFcmToken: String, title: String, body: String) {
        var requestQueue: RequestQueue? = null
        val postUrl = "https://fcm.googleapis.com/fcm/send"
        val fcmServerKey =
            "AAAAUcAgUKY:APA91bE13t4V2OcFzM12qczy0Opl54ZbmHJfm04svxUhxH0ZZ1BgEXjNX63YIPpeC5gN8sQS5mcl1MwhygTmWLvNueo68xCg2_n97Ckk8gddLlAEufIq-9hihmzGA127mpE3mpqzIQwo"
        requestQueue = Volley.newRequestQueue(requireContext())
        val mainObj = JSONObject()
        try {
            mainObj.put("to", userFcmToken)
            val notiObject = JSONObject()
            notiObject.put("title", title)
            notiObject.put("body", body)
            // Add custom data to specify target app and activity
            // notiObject.put("target_app", targetApp)
            // notiObject.put("target_activity", targetActivity)
            //   notiObject.put("icon", "icon") // enter icon that exists in drawable only
            mainObj.put("notification", notiObject)

            val request = object : JsonObjectRequest(
                Request.Method.POST, postUrl, mainObj,
                Response.Listener<JSONObject> { response ->
                    // code run if got response
                },
                Response.ErrorListener { error ->
                    // code run if got error
                }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val header = HashMap<String, String>()
                    header["content-type"] = "application/json"
                    header["authorization"] = "key=$fcmServerKey"
                    return header
                }
            }
            requestQueue?.add(request)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}