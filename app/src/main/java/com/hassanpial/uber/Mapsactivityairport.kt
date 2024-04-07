package com.hassanpial.uber

import android.Manifest
import android.animation.ValueAnimator
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.MatrixCursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.SearchView
import android.widget.SimpleCursorAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.hassanpial.uber.databinding.ActivityMapsBinding
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class Mapsactivityairport : AppCompatActivity(),recyclerviewsadapter.OnItemClickListener,OnMapReadyCallback  {
    private val driverMarkers: MutableMap<String, Marker> = HashMap()

    private var desiredmarkar: Marker? = null
    private lateinit var locationManager: LocationManager

    // Inside your activity or fragment class
    private var marker: Marker? = null
    var selectedVehicle="Not selected"
    var press = "None"
    var userLocation: String? = null
    private lateinit var nearbyDriversFinder: NearbyDriversFinder
    private lateinit var suggestionAdapter: SimpleCursorAdapter
    var desiredlocation: String? = null
    private lateinit var alertDialog: AlertDialog
    var found =true
    var price = 0;
    var distance = "0";
    var foundpickpoint = false
    private lateinit var mMap: GoogleMap
    private lateinit var searchView: SearchView

    private lateinit var placesClient: PlacesClient
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var currentLocation: Location
    private lateinit var destlocation: Location
    private val permissionCode = 101
    var cityName = " "
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_mapsactivityairport)
        nearbyDriversFinder = NearbyDriversFinder()
        // Initialize location manager
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // Initialize Places API
        //context?.let {
        Places.initialize(this, getString(R.string.google_maps_key))
        placesClient = Places.createClient(this)
        //}
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapairport) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getCurrentLocationUser()
        startLocationUpdates()
var camefrom=intent.getStringExtra("Came from")
        var description=intent.getStringExtra("Description")
        //Toast.makeText(this,"dferfe a$description",Toast.LENGTH_LONG).show()

        FirebaseDatabase.getInstance().getReference("Registered drivers")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (driversuid in snapshot.children) {
                            var uid = driversuid.key.toString()
                            val name = driversuid.child("Name").getValue(String::class.java)
                            val lat = driversuid.child("Latitude").getValue(Double::class.java)
                            val long = driversuid.child("Longitude").getValue(Double::class.java)

                            if (lat != null && long != null) {
                                // Get the marker for the driver if it already exists
                                val existingMarker = driverMarkers[name]

                                if (existingMarker != null) {
                                    // Animate the existing marker to the new position
                                    val startPosition = existingMarker.position
                                    val endPosition = LatLng(lat, long)

                                    val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
                                    valueAnimator.duration =
                                        2000 // Duration of animation in milliseconds

                                    valueAnimator.addUpdateListener { animator ->
                                        val fraction = animator.animatedFraction
                                        val newPosition = LatLng(
                                            startPosition.latitude + (endPosition.latitude - startPosition.latitude) * fraction,
                                            startPosition.longitude + (endPosition.longitude - startPosition.longitude) * fraction
                                        )
                                        existingMarker.position = newPosition
                                    }

                                    valueAnimator.start()
                                } else {
                                    // Add a new marker for the driver
                                    val originalIcon = BitmapFactory.decodeResource(
                                        resources,
                                        R.drawable.driverscar
                                    )
                                    val resizedIcon =
                                        Bitmap.createScaledBitmap(originalIcon, 60, 60, false)
                                    val descriptor = BitmapDescriptorFactory.fromBitmap(resizedIcon)

                                    val newMarker = mMap.addMarker(
                                        MarkerOptions().position(LatLng(lat, long)).title(name)
                                            .icon(descriptor)
                                    )
                                    driverMarkers[uid.toString()] = newMarker!!

                                }
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle error
                }
            })

        // Set up search views
        searchView = findViewById(R.id.searchView)
        if(intent.getStringExtra("Came from")=="DROPOFF"){
            searchView.queryHint="Pickup from"
        }

        setUpSearchView(searchView)

        var ProgressDialog = ProgressDialog(this)
        ProgressDialog.setMessage("Searching nearby drivers")
        // Set up listeners for search views
        findViewById<Button>(R.id.confirmpickupbutton).setOnClickListener {

            if (found == true ) {
                // binding.confirmpickupbutton.visibility=View.INVISIBLE
if(desiredlocation!=null){
    var intent=Intent(this,AirportActivity::class.java)
    intent.putExtra("Desired location",desiredlocation)

    if (description != null) {
        if (description.isNotEmpty()){
            intent.putExtra("Description",description)
        }
    }
    //if(intent.getStringExtra("Came from")=="DROPOFF"){
        //searchView.queryHint="Pickup from"
     //   Toast.makeText(this,"intent.getStringEadfd",Toast.LENGTH_SHORT).show()
     //   intent.putExtra("Came from","DROPOFF")
   // }

  //  if(intent.getStringExtra("Came from")=="PICKUP"){
        //searchView.queryHint="Pickup from"
       // Toast.makeText(this,"\"intent.getStringEadfd\"",Toast.LENGTH_SHORT).show()
        intent.putExtra("Came from",camefrom)
   // }


    startActivity(intent)
}else{
    Toast.makeText(this, "Enter valid locations", Toast.LENGTH_SHORT).show()
}
            } else {
                binding.confirmpickupbutton.visibility = View.VISIBLE
                //   ProgressDialog.hide()
                Toast.makeText(this, "Enter valid locations", Toast.LENGTH_SHORT).show()
            }
        }

    }


    // Find nearby drivers within a certain radius
    //  nearbyDriversFinder.findNearbyDrivers(Location(userLocation), 5000, listen
    // Handle the list of nearby drivers
    // You can update UI or perform other actions based on the nearby drivers found


    fun sendNotificationToDriver(userFcmToken: String, title: String, body: String) {
        var requestQueue: RequestQueue? = null
        val postUrl = "https://fcm.googleapis.com/fcm/send"
        val fcmServerKey =
            "AAAAUcAgUKY:APA91bE13t4V2OcFzM12qczy0Opl54ZbmHJfm04svxUhxH0ZZ1BgEXjNX63YIPpeC5gN8sQS5mcl1MwhygTmWLvNueo68xCg2_n97Ckk8gddLlAEufIq-9hihmzGA127mpE3mpqzIQwo"
        requestQueue = Volley.newRequestQueue(this)
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


    private fun makeDriverUnavailable(driver: Driver) {
        // Get a reference to the drivers node in the database
        val driversRef = FirebaseDatabase.getInstance().getReference("Registered drivers")

        // Query the drivers node to find the driver with the specified UID
        driversRef.orderByChild("UID").equalTo(driver.UID).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Loop through the dataSnapshot to find the driver node
                //  for (snapshot in dataSnapshot.children) {
                // Get the key (driver ID) of the driver node
                // val driverKey = snapshot.key

                // Update the availability status of the driver to "unavailable"
                driversRef.child(driver.UID ?: "").child("Availability").setValue("unavailable")
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Driver availability status updated successfully
                            // You can perform any additional actions here
                        } else {
                            // Failed to update driver availability status
                            // Handle the error
                        }
                    }
                // }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
            }
        })
    }


    private fun getCurrentLocationUser() {


        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        try {


            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    currentLocation = location
                   found = true
                    val geocoder = Geocoder(this, Locale.getDefault())
                    val addresses =
                        geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    cityName = addresses?.get(0)?.locality.toString()
                    userLocation = cityName
                    desiredlocation=cityName
                    Toast.makeText(
                        applicationContext,
                        "Current city: $cityName",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
                    searchView.setQuery(cityName, false)
                    addMarkerAndMoveCamera(latLng)


                } else {
                    //      Toast.makeText(this,"no markar",Toast.lengh)
                }
            }
        } catch (e: Exception) {

        }
    }

    private fun addMarkerAndMoveCamera(latLng: LatLng) {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapairport) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            val originalIcon = BitmapFactory.decodeResource(resources, R.drawable.car)
            val resizedIcon = Bitmap.createScaledBitmap(originalIcon, 32, 32, false)
            val descriptor = BitmapDescriptorFactory.fromBitmap(resizedIcon)

            val markerOptions =
                MarkerOptions().position(latLng).title("Current location").icon(descriptor)
            marker =
                googleMap.addMarker(markerOptions)
            googleMap.isTrafficEnabled = true

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
        }
    }




    private fun addMarkerAndMoveCamerafordesired(latLng: LatLng) {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapairport) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            googleMap.isTrafficEnabled = true
            if (desiredmarkar == null) {
                desiredmarkar =
                    googleMap.addMarker(MarkerOptions().position(latLng).title("Desired"))
            } else {
                desiredmarkar?.position = latLng
            }
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocationUser()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {


        mMap = googleMap
        mMap.isTrafficEnabled = true
        mMap.setOnMapClickListener(object : GoogleMap.OnMapClickListener {
            override fun onMapClick(latLng: LatLng, ) {
                // Convert LatLng to address
                try {


                    val geocoder = Geocoder(this@Mapsactivityairport, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

                    // Check if addresses are available
                    if (addresses != null && addresses.isNotEmpty()) {
                        val address = addresses[0]
                        val locationName = address.getAddressLine(0) // Get the address line
                        Toast.makeText(applicationContext, locationName, Toast.LENGTH_SHORT).show()


                       // if (press == "Dest") {
                            addMarkerAndMoveCamerafordesired(
                                LatLng(
                                    latLng.latitude,
                                    latLng.longitude
                                )
                            )
                            searchView.setQuery(locationName, true)
                            found = true
                            desiredlocation = searchView.query.toString()

                       // }
                        // Set the location name as the query in the search view
                    }

                } catch (e: Exception) {

                }
            }
        })


    }






    private fun setUpSearchView(searchView: SearchView) {

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                supportFragmentManager.findFragmentById(R.id.mapairport)!!.view?.visibility = View.VISIBLE

                // Handle search query submission
                if (!query.isNullOrEmpty()) {

                    performSearch(
                        query,
                        searchView
                    ) // Perform search when the user submits the query
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                supportFragmentManager.findFragmentById(R.id.mapairport)!!.view?.visibility = View.VISIBLE

                // Handle search query text changes
                if (!newText.isNullOrEmpty()) {
                    fetchLocationSuggestions(
                        newText,
                        searchView
                    ) // Fetch location suggestions based on the new text
                }
                return true
            }
        })

        searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(position: Int): Boolean {
                // Handle when a suggestion is selected
                return true
            }

            override fun onSuggestionClick(position: Int): Boolean {
                // Handle when a suggestion is clicked
                val suggestion =
                    searchView.suggestionsAdapter.getItem(position) as String // Get the clicked suggestion
                searchView.setQuery(
                    suggestion,
                    true
                ) // Set the suggestion as the query in the search view
                return true
            }
        })

        searchView.setOnQueryTextFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                // Handle focus change of the search view
                if (hasFocus) {

                }
            }
        })

    }

    private fun fetchLocationSuggestions(query: String, searchView: SearchView) {
        // Use the Places API to fetch location predictions based on the query
        val placesClient = Places.createClient(searchView.context)
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .build()

        placesClient.findAutocompletePredictions(request).addOnSuccessListener { response ->
            val predictions = response.autocompletePredictions
            if (predictions.isNotEmpty()) {
                val suggestionList =
                    predictions.map { prediction -> prediction.getFullText(null).toString() }
                updateSuggestions(suggestionList, searchView)
            } else {
                // No suggestions found
                showToast("No matching locations found")
            }
        }.addOnFailureListener {
            // Handle failure
            // showToast("${exception.message}")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }


    private fun updateSuggestions(suggestions: List<String>, searchView: SearchView) {
        val cursor = MatrixCursor(arrayOf("_id", "location_name"))
        suggestions.forEachIndexed { index, suggestion ->
            cursor.addRow(arrayOf(index, suggestion))
        }

        val suggestionAdapter = searchView.suggestionsAdapter as SimpleCursorAdapter
        suggestionAdapter.changeCursor(cursor)
    }

    private fun performSearch(query: String, Searchview: SearchView) {
        findViewById<Button>(R.id.confirmpickupbutton).visibility = View.VISIBLE
        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocationName(query, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val location =
                        addresses?.get(0)?.let { LatLng(it.latitude, addresses[0].longitude) }

                    if (location != null) {

                        val Location = Location("")
                        Location.latitude = location.latitude
                          Location.longitude = location.longitude
                        // addMarkerAndMoveCamera(location)


                            addMarkerAndMoveCamerafordesired(location)
                            //  destinationlocation=addresses[0].locality
                            desiredlocation = Searchview.query.toString()
                           found= true
                            destlocation = Location
                            Toast.makeText(this, "Found location", Toast.LENGTH_SHORT).show()
                            //  addMarkerAndMoveCamera(Location)



                    }
                } else {



                      found = false

                    Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: IOException) {
            // Handle IOException
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onItemClick(position: Int, driver: Driver) {
        findViewById<Button>(R.id.confirmpickupbutton).visibility = View.VISIBLE
        findViewById<TextView>(R.id.pricetextview).visibility = View.VISIBLE


        var tripDatabase = FirebaseDatabase.getInstance().getReference("Trips").child("Short trips")

        // val tripId = tripDatabase.push().key

        // Get current time
        val currentTime = System.currentTimeMillis()

        // Example data
        val driverName = driver.Name
        val driverUid = driver.UID.toString()

        val passengerUid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val startingTime =
            formatDateTimeFromMillis(currentTime) // Replace with actual starting time
        val status = "not accepted till now" // Status can be "reached" or "unreached"

        // Create a map to hold trip details
        val tripDetails = HashMap<String, Any>()
        var trpdetails2 = mapOf(
            "sfasd" to "fafadsfa"
        )
        //  tripDetails["time"] = currentTime.
        tripDetails["driver_name"] = driverName
        tripDetails["driver_uid"] = driverUid

        val passengerNameRef =
            FirebaseDatabase.getInstance().getReference("Registered users").child(passengerUid)
                .child("Name")

        passengerNameRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val passengerName = dataSnapshot.value.toString()
                // Now you can use the passengerName value
                tripDetails["passengerName"] = passengerName.toString()
                //  println("Passenger Name: $passengerName")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors here
                println("Failed to read passenger name: ${databaseError.message}")
            }
        })
       // tripDetails["destination"] = destinationlocation.toString()
        tripDetails["pickuplocation"] = userLocation.toString()
        tripDetails["passenger_uid"] = passengerUid
        tripDetails["starting_time"] = startingTime
        tripDetails["status"] = status
        tripDetails["driver_phone"] = driver.Phone
        tripDetails["price"] = price.toString()
        tripDetails["triptype"] = "Short trips"
        tripDetails["triprating"] = "0"
        tripDetails["distance"] = distance.toString()
        tripDetails["ratinggiven"] = "false"
        val passengerphoneRef =
            FirebaseDatabase.getInstance().getReference("Registered users").child(passengerUid)
                .child("Phone")

        passengerphoneRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val passengerPhone = dataSnapshot.value.toString()
                // Now you can use the passengerName value
                tripDetails["passenger_phone"] = passengerPhone.toString()
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
                        tripDatabase.child(id).setValue(tripDetails).addOnSuccessListener { }
                            .addOnFailureListener {}
                        FirebaseDatabase.getInstance().getReference("Registered users")
                            .child(passengerUid).child("Trips").child(id).setValue(tripDetails)
                            .addOnSuccessListener { }.addOnSuccessListener { }
                        FirebaseDatabase.getInstance().getReference("Registered drivers")
                            .child(driverUid).child("Trips").child("Short trips").child(id)
                            .setValue(tripDetails).addOnSuccessListener { }.addOnFailureListener {}
                        Toast.makeText(
                            this@Mapsactivityairport,
                            "Trip created successfully",
                            Toast.LENGTH_SHORT
                        ).show()
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
        if (driver.Token.toString() != " ")
            sendNotificationToDriver(
                driver.Token.toString(),
                "You have received a new ride request",
                "One passenger selected you to pickup from $userLocation"
            )
        alertDialog.dismiss()
    }


    private fun sendnoti(
        recipientToken: String,
        uid: String,
        notificationTitle: String,
        notificationBody: String
    ) {
        val message = RemoteMessage.Builder(recipientToken)
            .setMessageId(UUID.randomUUID().toString())
            .setData(mapOf("title" to notificationTitle, "body" to notificationBody))
            .build()

        FirebaseMessaging.getInstance().send(message)

    }



    fun formatDateTimeFromMillis(timeInMillis: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        return sdf.format(calendar.time)
    }

    private fun startLocationUpdates() {
        // Check if permission is granted
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Request location updates with the desired parameters
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000,  // Update interval in milliseconds
                10f,   // Minimum distance in meters between location updates
                locationListener
            )
        } else {
            // Handle permission not granted
        }
    }


    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val newLatLng = LatLng(location.latitude, location.longitude)
            moveMarker(newLatLng)
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            // Handle status changes if needed
        }

        override fun onProviderEnabled(provider: String) {
            // Handle provider enabled if needed
        }

        override fun onProviderDisabled(provider: String) {
            // Handle provider disabled if needed
        }
    }

    private fun moveMarker(newPosition: LatLng) {
        if (marker == null) {
            // Create marker if it doesn't exist
            val originalIcon = BitmapFactory.decodeResource(resources, R.drawable.car)
            val resizedIcon = Bitmap.createScaledBitmap(originalIcon, 32, 32, false)
            val descriptor = BitmapDescriptorFactory.fromBitmap(resizedIcon)
            // marker = mMap.addMarker(MarkerOptions().position(newPosition).title("Current Location").icon(descriptor))
            // mMap.addMarker(MarkerOptions().position(newPosition).title("Current Location").icon(descriptor))
        } else {
            // Animate marker movement
            val previousPosition = marker!!.position
            marker!!.position = newPosition
            animateMarker(previousPosition, newPosition)
        }
    }

    private fun animateMarker(fromPosition: LatLng, toPosition: LatLng) {
        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.duration = 1000 // Duration of animation in milliseconds
        valueAnimator.interpolator =
            LinearInterpolator()
        valueAnimator.addUpdateListener { animation ->
            val fraction = animation.animatedFraction
            val lat =
                (toPosition.latitude - fromPosition.latitude) * fraction + fromPosition.latitude
            val lng =
                (toPosition.longitude - fromPosition.longitude) * fraction + fromPosition.longitude
            val newPosition = LatLng(lat, lng)
            marker!!.position = newPosition
        }
        valueAnimator.start()
    }
}