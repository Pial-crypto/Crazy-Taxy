package com.hassanpial.uber


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class recyclerviewsadapter(private val nearestdriverlist: MutableList<Driver>,var distance:String,var Price:String) : RecyclerView.Adapter<recyclerviewsadapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.listofdneardriverlayout, parent, false)
        return ViewHolder(itemView)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val drivername: TextView = itemView.findViewById(R.id.textViewName)
        val driverphone: TextView = itemView.findViewById(R.id.textViewPhoneNumber)
        var price:TextView=itemView.findViewById(R.id.textViewPrice)
        var distance:TextView=itemView.findViewById(R.id.textViewDistance)
        var rating:TextView=itemView.findViewById(R.id.textRating)


    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentdriver =nearestdriverlist[position]


holder.drivername.text=currentdriver.Name
        holder.driverphone.text=currentdriver.Phone
        holder.rating.text= currentdriver.Rating.toString()

holder.price.text=Price.toString()
        holder.distance.text=distance

        // Set click listener for the entire item view
        holder.itemView.setOnClickListener {
           itemClickListener?.onItemClick(position,currentdriver)
       }
    }

    override fun getItemCount(): Int {
        return nearestdriverlist.size
    }




    interface OnItemClickListener {
        fun onItemClick(position: Int, driver:Driver)
    }

    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.itemClickListener = listener
    }

}