package com.hassanpial.uber

import android.location.Location
import android.os.Handler
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NearbyDriversFinder {
    private val driversRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Registered drivers")

    fun findNearbyDrivers(
        userLocation: Location,
        radius: Int,
        listener: OnNearbyDriversFoundListener,
        selectedVehicle: String
    ) {
             val geoHashQueryBounds = GeoFireUtils.getGeoHashQueryBounds(GeoLocation(userLocation.latitude, userLocation.longitude),
            radius.toDouble()
        )
      //  for (queryBounds in geoHashQueryBounds) {
          //  val query: Query = driversRef
            //    .orderByChild("geohash")
              //  .startAt(queryBounds.startHash)
                //.endAt(queryBounds.endHash)

            driversRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val nearbyDrivers = ArrayList<Driver>()
                    for (snapshot in dataSnapshot.children) {
                        //val driver = snapshot.getValue(Driver::class.java)

                        if (snapshot.exists()) {
                            // Calculate distance between user and driver
                            val driverLocation = Location("")
                            val UID = snapshot.child("UID").getValue(String::class.java)
                            val latitude = snapshot.child("Latitude").getValue(Double::class.java) ?: 0.0
                            val longitude = snapshot.child("Longitude").getValue(Double::class.java) ?: 0.0
                            val name = snapshot.child("Name").getValue(String::class.java) ?: ""
                            val email = snapshot.child("Email").getValue(String::class.java) ?: ""
                            val password = snapshot.child("Password").getValue(String::class.java) ?: ""
                            val phone = snapshot.child("Phone").getValue(String::class.java) ?: ""
                            val rating = snapshot.child("Rating").getValue(String::class.java) ?: 0.0
                            val availability = snapshot.child("Availability").getValue(String::class.java) ?: ""
                            val token = snapshot.child("Token").getValue(String::class.java) ?: ""
var vehicle= snapshot.child("Vehicle").getValue(String::class.java) ?: ""
                            val driver = Driver(UID, latitude, longitude, name, email, password, phone, rating.toString(), availability, token)
driverLocation.latitude=driver.Latitude
                            driverLocation.longitude=driver.Longitude

                            val distance = userLocation.distanceTo(driverLocation)
                            // Add driver to the list if within radius

                            if (distance <= radius.toFloat() && driver.Availability=="available" && selectedVehicle==vehicle) {
                                nearbyDrivers.add(driver)
                            }
                        }
                    }
Handler().postDelayed(
                    Runnable {  listener.onNearbyDriversFound(nearbyDrivers.toMutableList())},2000)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors
                }
            })
        }
    //}

    interface OnNearbyDriversFoundListener {
        fun onNearbyDriversFound(nearbyDrivers: MutableList<Driver>)
    }
}
