package com.hassanpial.uber

import android.content.Context

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener



class recyclerviewsadapter2(private val context:Context, private val mypastdriverslist: MutableList<requestsclass>) : RecyclerView.Adapter<recyclerviewsadapter2.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var itemView = LayoutInflater.from(parent.context).inflate(R.layout.requestrecyclerviw, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var currentDriver = mypastdriverslist[position]

        var reqobj=mypastdriverslist[position]
        holder.drivernametextview.text="Driver's name : "+reqobj.name
        holder.destinationtxtviwe.text="Destination : "+reqobj.dest
        holder.phonetextview.text="Driver's phone : "+reqobj.phn
        holder.senttime.text="Trip request timer : "+reqobj.tm.toString()
        holder.pickuplocationtxtview.text="Pickup location : "+reqobj.pkloc
        holder.id.text="Trip id : "+reqobj.id
        holder.reachornottextview.text="Trip status : "+reqobj.rchornt
        holder.textviewprice.text="Trip investment : "+reqobj.prc+" BDT"

      var ratingString = currentDriver.trprating
        var rating = ratingString.toFloatOrNull() ?: 0.0f // Convert string to float, defaulting to 0.0f if conversion fails

        // Bind the rating value to the RatingBar
        holder.ratingBar.rating = rating


if(reqobj.rtnggvn=="false") {
    holder.ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
        // Update the rating in Firebase
        val driverId = currentDriver.drvruid // Assuming you have a unique driver ID

        updateRatingInFirebase(driverId, rating, reqobj.trptp, reqobj.id)

    }
}
        else if(reqobj.rtnggvn=="true"){
          // holder.ratingBar.isIndicator=true
    holder.ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
        // Update the rating in Firebase
       Toast.makeText(context,"You cannot change the review again.If you change it will not be saved",Toast.LENGTH_SHORT).show()


    }


}

        // Set click listener for the entire item view
        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(position, currentDriver)
        }
    }


    private fun updateRatingInFirebase(driverId: String, rating: Float,trptype:String,trpid:String) {

        val database = FirebaseDatabase.getInstance()
        val driverRef = database.getReference("Registered drivers").child(driverId)
        driverRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val tripsCount = snapshot.child("TripsCount").getValue(Int::class.java) ?: 0

                    // Get the current rating value as a string
                    val currentRatingString = snapshot.child("Rating").getValue(String::class.java)

                    // Convert the current rating value to a float
                    val currentRating = currentRatingString?.toFloatOrNull() ?: 0.0f

                    // Update the rating value by adding the new rating
                    val updatedRating = currentRating + rating

                    // Convert the updated rating value to a string
                    val updatedRatingString = String.format("%.1f", updatedRating)
                    driverRef.child("Rating").setValue(updatedRatingString)
                        .addOnSuccessListener {
                            Log.d("Firebase", "Trip count updated successfully for driver $driverId")

                        }
                        .addOnFailureListener {
                        }
                } else {
                    Log.e("Firebase", "Driver $driverId not found")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Database error: ${error.message}")
            }
        })
FirebaseDatabase.getInstance()
        FirebaseDatabase.getInstance().getReference("Trips").child(trptype).child("triprating").setValue("%.1f".format(rating))
        FirebaseDatabase.getInstance()
        FirebaseDatabase.getInstance().getReference("Trips").child(trptype).child("ratinggiven").setValue("true")
        driverRef.child("Trips").child(trptype).child(trpid).child("triprating").setValue("%.1f".format(rating))
        driverRef.child("Trips").child(trptype).child(trpid).child("ratinggiven").setValue("true")
        FirebaseDatabase.getInstance().getReference("Registered users").child(FirebaseAuth.getInstance().currentUser?.uid.toString()).child("Trips").child(trpid).child("triprating").setValue("%.1f".format(rating))
        FirebaseDatabase.getInstance().getReference("Registered users").child(FirebaseAuth.getInstance().currentUser?.uid.toString()).child("Trips").child(trpid).child("ratinggiven").setValue("true")
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var drivernametextview: TextView = itemView.findViewById(R.id.textdrivername)
        val id: TextView = itemView.findViewById(R.id.textViewRequestId)
        var senttime:TextView=itemView.findViewById(R.id.textViewTimestamp)
        var pickuplocationtxtview=itemView.findViewById<TextView>(R.id.textViewpickuplocation)
        var destinationtxtviwe=itemView.findViewById<TextView>(R.id.textViewdestination)
        var phonetextview=itemView.findViewById<TextView>(R.id.textViewPhone)
        var reachornottextview=itemView.findViewById<TextView>(R.id.reachedornot)
var textviewprice=itemView.findViewById<TextView>(R.id.textViewprice)
        var ratingBar: RatingBar = itemView.findViewById(R.id.Ratingbar)
    }

    override fun getItemCount(): Int {
        return mypastdriverslist.size
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, driver: requestsclass)
    }

    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.itemClickListener = listener
    }


}
