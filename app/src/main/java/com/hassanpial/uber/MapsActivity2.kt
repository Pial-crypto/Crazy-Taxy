package com.hassanpial.uber

import android.Manifest
import android.animation.ValueAnimator
import android.app.ProgressDialog
import android.content.Context
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
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.DatePicker
import android.widget.SearchView
import android.widget.SimpleCursorAdapter
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.common.api.ApiException
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
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.RectangularBounds
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
import java.util.Locale
import java.util.UUID
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt



class MapsActivity2 : AppCompatActivity(), recyclerviewsadapter.OnItemClickListener,OnMapReadyCallback  {
    private val driverMarkers: MutableMap<String, Marker> = HashMap()
    private var pickupMarker: Marker? = null
    private var destinationMarker: Marker? = null
    private lateinit var locationManager: LocationManager
    // Inside your activity or fragment class
    private var marker: Marker? = null
    var selectedDate=" "
    var selectedTime=" "
    private  val SUGGESTION_REQUEST_DELAY_MS = 300L
    private lateinit var pickupButton: Button
    private lateinit var datePicker: DatePicker
    private lateinit var timePicker: TimePicker
    private var selectedYear: Int = 0
    private var selectedMonth: Int = 0
    private var selectedDay: Int = 0
    private var selectedHour: Int = 0
    private var selectedMinute: Int = 0
  //  private lateinit var datePicker:DatePicker
   // private lateinit var timePicker:TimePicker
    var press="None"
    var distance="0"
    var userLocation:String?=null
    private lateinit var nearbyDriversFinder: NearbyDriversFinder
    private lateinit var suggestionAdapter: SimpleCursorAdapter
    var destinationlocation:String?=null
    private lateinit var alertDialog: AlertDialog
    var founddest=false
    var price=0;
    var foundpickpoint=false
    private lateinit var mMap: GoogleMap
    private lateinit var searchViewCurrentLocation: SearchView
    private lateinit var searchViewDestination: SearchView
    private lateinit var placesClient: PlacesClient
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var currentLocation: Location
    private lateinit var destlocation: Location
    private val permissionCode = 101
    var cityName = " "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //var binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_maps2)

        nearbyDriversFinder = NearbyDriversFinder()
        Places.initialize(this, getString(R.string.google_maps_key))
        placesClient = Places.createClient(this)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        var mapFragment = supportFragmentManager.findFragmentById(R.id.map2) as SupportMapFragment
        mapFragment.getMapAsync(this)

     /*   FirebaseDatabase.getInstance().getReference("Registered drivers").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (driversuid in snapshot.children) {
                        var uid=driversuid.key.toString()
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
                                valueAnimator.duration = 2000 // Duration of animation in milliseconds

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
                                val originalIcon = BitmapFactory.decodeResource(resources, R.drawable.driverscar)
                                val resizedIcon = Bitmap.createScaledBitmap(originalIcon, 60, 60, false)
                                val descriptor = BitmapDescriptorFactory.fromBitmap(resizedIcon)

                                val newMarker = mMap.addMarker(MarkerOptions().position(LatLng(lat, long)).title(name).icon(descriptor))
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
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getCurrentLocationUser()
      //  startLocationUpdates()


        datePicker = findViewById(R.id.datePicker)
        timePicker = findViewById(R.id.timePicker)

pickupButton=findViewById(R.id.pickupbutton)
        FirebaseDatabase.getInstance().getReference("Registered drivers").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (driversuid in snapshot.children) {
                        var uid=driversuid.key.toString()
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
                                valueAnimator.duration = 2000 // Duration of animation in milliseconds

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
                                val originalIcon = BitmapFactory.decodeResource(resources, R.drawable.driverscar)
                                val resizedIcon = Bitmap.createScaledBitmap(originalIcon, 60, 60, false)
                                val descriptor = BitmapDescriptorFactory.fromBitmap(resizedIcon)

                                val newMarker = mMap.addMarker(MarkerOptions().position(LatLng(lat, long)).title(name).icon(descriptor))
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

        if(intent.getStringExtra("date")!=null && intent.getStringExtra("time")!=null){
            pickupButton.visibility=View.GONE
            datePicker.visibility=View.GONE
            timePicker.visibility=View.GONE
            selectedDate=intent.getStringExtra("date").toString()
            selectedTime=intent.getStringExtra("time").toString()
            Toast.makeText(this,"Selected date:$selectedDate - selected time:$selectedTime",Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(this,"Please select date and time",Toast.LENGTH_SHORT).show()
        }

        pickupButton.setOnClickListener(){
            mapFragment.view?.visibility = View.INVISIBLE
            showDateTimePickers()
        }


        var ProgressDialog=ProgressDialog(this)
        ProgressDialog.setMessage("Searching nearby drivers")
        // Set up search views
        searchViewCurrentLocation = findViewById(R.id.searchViewCurrentLocation)
        searchViewDestination = findViewById(R.id.searchViewDestination)
        setUpSearchView(searchViewCurrentLocation)
        setUpSearchView(searchViewDestination)
        // Set up listeners for search views
        findViewById<Button>(R.id.confirmpickupbutton).setOnClickListener {  // binding.pb.visibility=View.VISIBLE
        //    findViewById<Button>(R.id.confirmpickupbutton).visibility=View.INVISIBLE
            //binding.findingyourridetextview.visibility=View.VISIBLE
           // Toast.makeText(this,"Please select date and time",Toast.LENGTH_SHORT).show()
datePicker.visibility=View.INVISIBLE
            timePicker.visibility=View.INVISIBLE
            mapFragment.view?.visibility = View.VISIBLE
         //   Toast.makeText(this,"$founddest $foundpickpoint",Toast.LENGTH_SHORT).show()
            if ( founddest==true && foundpickpoint==true && selectedDate!=" " && selectedTime!=" " ) {
                if (!isBefore("$selectedDate $selectedTime")) {
                  //  findViewById<Button>(R.id.confirmpickupbutton).visibility = View.INVISIBLE
                    ProgressDialog.show()
                    //  binding.pb.visibility=View.VISIBLE
                    // Create an instance of OnNearbyDriversFoundListener
                    val listener = object : NearbyDriversFinder.OnNearbyDriversFoundListener {
                        override fun onNearbyDriversFound(nearbyDrivers: MutableList<Driver>) {
                            if (nearbyDrivers.isNotEmpty()) {
                                // Inflate the custom layout for the dialog
                                val inflater = LayoutInflater.from(this@MapsActivity2)
                                val dialogView = inflater.inflate(R.layout.customdialogue, null)


                                // Find the ListView in the custom layout
                                val RecyclerView =
                                    dialogView.findViewById<RecyclerView>(R.id.listView)
                                RecyclerView.layoutManager = LinearLayoutManager(applicationContext)
                                // Set the adapter to the ListView




                                distance = String.format("%.2f", calculateDistanceInKilometers(currentLocation.latitude, currentLocation.longitude, destlocation.latitude, destlocation.longitude))


                                price= (calculateDistanceInKilometers(currentLocation.latitude,currentLocation.longitude,destlocation.latitude,destlocation.longitude)*22.30).toInt()
                                findViewById<TextView>(R.id.pricetextview).text = price.toString()+" Tk"

                                var adapter=recyclerviewsadapter(nearbyDrivers,distance.toString()+"Km",price.toString()+"TK")
                                RecyclerView.adapter = adapter
                                adapter.setOnItemClickListener(this@MapsActivity2)



                                ProgressDialog.hide()







                                Toast.makeText(
                                    applicationContext,
                                    "Found ${nearbyDrivers.size} drivers",
                                    Toast.LENGTH_SHORT
                                ).show()
                                //     binding.pb.visibility=View.INVISIBLE
                                // Create the dialog
                                val dialogBuilder = AlertDialog.Builder(this@MapsActivity2)
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
                                    applicationContext,
                                    "No driver found nearby",
                                    Toast.LENGTH_SHORT
                                ).show()

                                findViewById<Button>(R.id.confirmpickupbutton).visibility =
                                    View.VISIBLE
                                // binding.findingyourridetextview.visibility=View.INVISIBLE
                            }
                        }
                    }
                    nearbyDriversFinder.findNearbyDrivers(currentLocation, 5000, listener)
                }
            }

            else {
                findViewById<Button>(R.id.confirmpickupbutton).visibility = View.VISIBLE
                if(selectedDate!=" " && selectedTime!=" "){
                    if (isBefore("$selectedDate $selectedTime")) {
                        Toast.makeText(
                            this,
                            "Current time can not be greater than pick up time",
                            Toast.LENGTH_SHORT
                        ).show()
                    }}
                else {
                    Toast.makeText(
                        this,
                        "Enter valid locations or check if you have provided the date and time",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }


        // Find nearby drivers within a certain radius
*/
    }




    // Find nearby drivers within a certain radius
    //  nearbyDriversFinder.findNearbyDrivers(Location(userLocation), 5000, listen
    // Handle the list of nearby drivers
    // You can update UI or perform other actions based on the nearby drivers found








    fun sendNotificationToDriver(userFcmToken: String,title:String,body:String) {
        var requestQueue: RequestQueue? = null
        val postUrl = "https://fcm.googleapis.com/fcm/send"
        val fcmServerKey = "AAAAUcAgUKY:APA91bE13t4V2OcFzM12qczy0Opl54ZbmHJfm04svxUhxH0ZZ1BgEXjNX63YIPpeC5gN8sQS5mcl1MwhygTmWLvNueo68xCg2_n97Ckk8gddLlAEufIq-9hihmzGA127mpE3mpqzIQwo"
        requestQueue = Volley.newRequestQueue(this)
        val mainObj = JSONObject()
        try {
            mainObj.put("to", userFcmToken)
            val notiObject = JSONObject()
            notiObject.put("title", title)
            notiObject.put("body",body )
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






    private fun getCurrentLocationUser() {
        /*   fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
               if (location != null) {
                   currentLocation = location
                   val geocoder = Geocoder(this, Locale.getDefault())
                   val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                   cityName = addresses?.get(0)?.locality.toString()
                   Toast.makeText(applicationContext, "Current city: $cityName", Toast.LENGTH_SHORT)
                       .show()
   // Now you have the user's current city in the cityName variable
                   // You can use it as needed, for example, starting a new activity

                   // Now you have the user's current city in the cityName variable
                   // You can use it as needed
               }
           }

         */

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
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location
foundpickpoint=true
                val geocoder = Geocoder(this, Locale.getDefault())
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                cityName = addresses?.get(0)?.locality.toString()
                userLocation=cityName
                Toast.makeText(applicationContext, "Current city: $cityName", Toast.LENGTH_SHORT)
                    .show()
                val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
                searchViewCurrentLocation.setQuery(cityName,false)
                addMarkerAndMoveCamera(latLng)


            }
            else{
                //      Toast.makeText(this,"no markar",Toast.lengh)
            }
        }
    }

    private fun addMarkerAndMoveCamera(latLng: LatLng) {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map2) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            val originalIcon = BitmapFactory.decodeResource(resources, R.drawable.car)
            val resizedIcon = Bitmap.createScaledBitmap(originalIcon, 32, 32, false)
            val descriptor = BitmapDescriptorFactory.fromBitmap(resizedIcon)

            val markerOptions = MarkerOptions().position(latLng).title("Current Location").icon(descriptor)
            marker=
                googleMap.addMarker(markerOptions)
            googleMap.isTrafficEnabled=true

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
        }
    }
// Set up OnMapClickListener


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
        mMap.setOnMapClickListener(object : GoogleMap.OnMapClickListener {
            override fun onMapClick(latLng: LatLng,) {
                // Convert LatLng to address
                val geocoder = Geocoder(this@MapsActivity2, Locale.getDefault())
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
              //  addMarkerAndMoveCamera(LatLng(latLng.latitude, latLng.longitude))
                // Check if addresses are available
                if (addresses != null && addresses.isNotEmpty()) {
                    val address = addresses[0]
                    val locationName = address.getAddressLine(0) // Get the address line
                    Toast.makeText(applicationContext, locationName, Toast.LENGTH_SHORT).show()


                    if (press == "Dest") {
                        addMarkerAndMoveCamerafordestination(LatLng(latLng.latitude,latLng.longitude))
                        searchViewDestination.setQuery(locationName, true)
founddest=true
                        destinationlocation = searchViewDestination.query.toString()
                    }
                    if (press == "Curr") { addMarkerAndMoveCameraforpickuplocation(LatLng(latLng.latitude, latLng.longitude))
                        foundpickpoint=true
                        userLocation = locationName
                        searchViewCurrentLocation.setQuery(
                            locationName,
                            true
                        ) // Set the location name as the query in the search view
                        userLocation=searchViewCurrentLocation.query.toString()
                    }
                }
            }
        })

        // Initialize search views
        // searchViewCurrentLocation = findViewById(R.id.searchViewCurrentLocation)
        // searchViewDestination = findViewById(R.id.searchViewDestination)

        // Set up listeners for search views
        //setUpSearchView(searchViewCurrentLocation)
        //  setUpSearchView(searchViewDestination)
    }


    private fun performAutoComplete(query: String) {
        // Get the current location's latitude and longitude
        val currentLocation = LatLng(currentLocation.latitude, currentLocation.longitude)

        // Define a bias region around the current location
        val bounds: RectangularBounds = RectangularBounds.newInstance(
            LatLng(currentLocation.latitude - 0.01, currentLocation.longitude - 0.01),
            LatLng(currentLocation.latitude + 0.01, currentLocation.longitude + 0.01)
        )

        // Create a FindAutocompletePredictionsRequest object with the query and location bias
        val request: FindAutocompletePredictionsRequest = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .setLocationBias(bounds)
            .build()

        // Fetch autocomplete predictions
        placesClient.findAutocompletePredictions(request).addOnSuccessListener { response ->
            val predictions: List<AutocompletePrediction> = response.autocompletePredictions
            // Handle predictions
        }.addOnFailureListener { exception ->
            if (exception is ApiException) {
                Toast.makeText(
                    baseContext,
                    "Autocomplete failed: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    private fun addMarkerAndMoveCameraforpickuplocation(latLng: LatLng) {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            googleMap.isTrafficEnabled=true
// Construct the request URL


            if (pickupMarker == null) {
                pickupMarker = googleMap.addMarker(MarkerOptions().position(latLng).title("Pickup point"))
            } else {
                pickupMarker?.position = latLng
            }
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
        }
    }


    private fun addMarkerAndMoveCamerafordestination(latLng: LatLng) {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            googleMap.isTrafficEnabled=true
            if (destinationMarker == null) {
                destinationMarker = googleMap.addMarker(MarkerOptions().position(latLng).title("Destination"))
            } else {
                destinationMarker?.position = latLng
            }
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
        }
    }

    private fun handleSearchViewFocus(searchView: SearchView) {
        if (searchView == searchViewCurrentLocation) {
            press = "Curr"
        }
        if (searchView == searchViewDestination) {
            press = "Dest"
        }
    }
    private fun setUpSearchView(searchView: SearchView) {

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {datePicker.visibility=View.INVISIBLE
                timePicker.visibility=View.INVISIBLE
                supportFragmentManager.findFragmentById(R.id.map2)!!.view?.visibility=View.VISIBLE
                if(searchView==searchViewCurrentLocation){
                    press="Curr"
                }
                if(searchView==searchViewDestination){
                    press="Dest"
                }
                // Handle search query submission
                if (!query.isNullOrEmpty()) {

                    performSearch(query,searchView) // Perform search when the user submits the query
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                datePicker.visibility=View.INVISIBLE
                timePicker.visibility=View.INVISIBLE
                supportFragmentManager.findFragmentById(R.id.map2)!!.view?.visibility=View.VISIBLE
                if(searchView==searchViewCurrentLocation){
                    press="Curr"
                }
                if(searchView==searchViewDestination){
                    press="Dest"
                }
                // Handle search query text changes
                if (!newText.isNullOrEmpty()) {
                    fetchLocationSuggestions(newText, searchView) // Fetch location suggestions based on the new text
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
                val suggestion = searchView.suggestionsAdapter.getItem(position) as String // Get the clicked suggestion
                searchView.setQuery(suggestion, true) // Set the suggestion as the query in the search view
                return true
            }
        })

        searchView.setOnQueryTextFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                // Handle focus change of the search view
                if (hasFocus) {
                    if (searchView == searchViewCurrentLocation) {
                        press = "Curr"
                    }
                    if (searchView == searchViewDestination) {
                        press = "Dest"
                    }
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
                val suggestionList = predictions.map { prediction -> prediction.getFullText(null).toString() }
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

    private fun performSearch(query: String,Searchview:SearchView) {
        findViewById<Button>(R.id.confirmpickupbutton).visibility=View.VISIBLE
        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocationName(query, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val location = addresses?.get(0)?.let { LatLng(it.latitude, addresses[0].longitude) }

                    if (location != null) {
                        addMarkerAndMoveCamera(location)

                        val Location = Location("")
                        Location.latitude = location.latitude
                        Location.longitude = location.longitude

                        if(Searchview==searchViewDestination){
                            addMarkerAndMoveCamerafordestination(location)
                           // destinationlocation=addresses[0].locality
                            destinationlocation=Searchview.query.toString()
                            founddest=true
                            destlocation=Location
                            Toast.makeText(this,"Found location",Toast.LENGTH_SHORT).show()

                        }
                        if(Searchview==searchViewCurrentLocation){
                            addMarkerAndMoveCameraforpickuplocation(location)
                            //userLocation=addresses[0].locality
                            userLocation=Searchview.query.toString()
                            foundpickpoint=true
                            currentLocation=Location
                            Toast.makeText(this,"Found location",Toast.LENGTH_SHORT).show()
                        }

                    }
                } else {
                    if(Searchview.id==R.id.searchViewDestination){
                        founddest=false
                    }

                    if(Searchview.id==R.id.searchViewDestination){
                        foundpickpoint=false
                    }
                    Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: IOException) {
            // Handle IOException
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onItemClick(position: Int, driver: Driver) {
        findViewById<Button>(R.id.confirmpickupbutton).visibility=View.VISIBLE
        findViewById<TextView>(R.id.pricetextview).visibility=View.VISIBLE



        var tripDatabase = FirebaseDatabase.getInstance().getReference("Trips").child("Long trips")

        val tripId = tripDatabase.push().key

        // Get current time
        val currentTime =selectedDate+" "+selectedTime

        // Example data
        val driverName = driver.Name
        val driverUid = driver.UID.toString()

        val passengerUid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val startingTime = currentTime // Replace with actual starting time
        val status = "not accepted till now" // Status can be "reached" or "unreached"

        // Create a map to hold trip details
        val tripDetails = HashMap<String, Any>()
     //   tripDetails["time"] = currentTime
        tripDetails["driver_name"] = driverName
        tripDetails["driver_uid"] = driverUid
//tripDetails["Accepting status"]=status
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
        tripDetails["ratinggiven"]="false"
tripDetails["triptype"]="Long trips"
        tripDetails["triprating"]="0"
        tripDetails["passenger_uid"] = passengerUid
        tripDetails["starting_time"] = startingTime
        tripDetails["status"] = status
        tripDetails["driver_phone"]=driver.Phone
        tripDetails["price"]=price.toString()
        tripDetails["distance"]=distance.toString()
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

        tripDetails["destination"]=destinationlocation.toString()
        tripDetails["pickuplocation"]=userLocation.toString()
        // Write trip details to the database
        tripDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    val tripId = tripDatabase.push().key
                    tripId?.let { id ->
                        tripDetails["trip_id"] = tripId
                        tripDatabase.child(id).setValue(tripDetails).addOnSuccessListener {  }.addOnFailureListener{}
                        FirebaseDatabase.getInstance().getReference("Registered users").child(passengerUid).child("Trips").child(id).setValue(tripDetails).addOnSuccessListener {  }.addOnSuccessListener {  }
                        FirebaseDatabase.getInstance().getReference("Registered drivers").child(driverUid).child("Trips").child("Long trips").child(id).setValue(tripDetails).addOnSuccessListener {  }.addOnFailureListener{}
                        Toast.makeText(this@MapsActivity2, "Trip created successfully", Toast.LENGTH_SHORT).show()
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






        //   sendnoti(driver.Token.toString(),driver.UID.toString(),"ddfasd","adsfasdfasdfsd")
        ///  Toast.makeText(this,"Pressed on something",Toast.LENGTH_SHORT).show()
        if (driver.Token.toString()!=" ")
        sendNotificationToDriver(driver.Token.toString(),"You have received a new ride request","One passenger selected you to pickup from $userLocation")
        alertDialog.dismiss()
    }



    private fun sendnoti(recipientToken:String,uid:String,notificationTitle:String,notificationBody:String){
        val message = RemoteMessage.Builder(recipientToken)
            .setMessageId(UUID.randomUUID().toString())
            .setData(mapOf("title" to notificationTitle, "body" to notificationBody))
            .build()

        FirebaseMessaging.getInstance().send(message)

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

    // Method to show Date and Time Pickers when Pickup Button is clicked
    fun showDateTimePickers() {
        datePicker.visibility = View.VISIBLE
        timePicker.visibility = View.VISIBLE

        // Show Date Picker
        datePicker.init(datePicker.year, datePicker.month, datePicker.dayOfMonth) { _, year, monthOfYear, dayOfMonth ->
            selectedYear = year
            selectedMonth = monthOfYear
            selectedDay = dayOfMonth

            selectedDate = "$selectedYear-$selectedMonth-$selectedDay"
        }

        // Show Time Picker
        timePicker.setOnTimeChangedListener(object : TimePicker.OnTimeChangedListener {
            override fun onTimeChanged(view: TimePicker?, hourOfDay: Int, minute: Int) {
                selectedHour = hourOfDay
                selectedMinute = minute

                selectedTime = "$selectedHour:$selectedMinute"

            }
        })

        // Do something with the selected date and time
        // For example, display it in a toast message
        val message = "Selected Date: $selectedDate, Selected Time: $selectedTime"
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }



    // Method to handle the button click
    fun onConfirmPickupClicked(view: View) {

    }

    fun isBefore(dateTimeString: String): Boolean {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val currentTime = System.currentTimeMillis()

        // Parse the provided date-time string
        val providedDateTime = format.parse(dateTimeString)

        // Check if the current time is before the provided date-time
        return currentTime < providedDateTime.time
    }

    private fun startLocationUpdates() {
        // Check if permission is granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
            val resizedIcon = Bitmap.createScaledBitmap(originalIcon, 32,32, false)
            val descriptor = BitmapDescriptorFactory.fromBitmap(resizedIcon)
            marker = mMap.addMarker(MarkerOptions().position(newPosition).title("Current Location").icon(descriptor))
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
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.addUpdateListener { animation ->
            val fraction = animation.animatedFraction
            val lat = (toPosition.latitude - fromPosition.latitude) * fraction + fromPosition.latitude
            val lng = (toPosition.longitude - fromPosition.longitude) * fraction + fromPosition.longitude
            val newPosition = LatLng(lat, lng)
            marker!!.position = newPosition
        }
        valueAnimator.start()
    }


}