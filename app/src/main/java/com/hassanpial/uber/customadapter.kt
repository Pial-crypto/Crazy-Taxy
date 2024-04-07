package com.hassanpial.uber

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class customadapter(private val context: Context) : RecyclerView.Adapter<customadapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view:View?=null
        try {
            val view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false)

        } catch (e:Exception){

        }
        return ViewHolder(view!!)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            // Bind your data to the views here
            if (position == 0) {
                holder.imageView.setImageResource(R.drawable.ridereq)
                holder.titleTextView.text = "Request ride"
                //holder.descriptionTextView.text = "Find your comfortable rider"
            } else if (position == 1) {
                holder.imageView.setImageResource(R.drawable.premiumrider)
                holder.titleTextView.text = "Request premier"
                holder.descriptionTextView.text = "Get the best riders"
            }
        }catch (e:Exception){

        }

    }

    override fun getItemCount(): Int {
        try {
           // return 2 // Assuming you have only two items
        } catch (e:Exception){

        }
        return  2

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //val cardView: CardView = itemView.findViewById(R.id.cardView)
        val imageView: ImageView = itemView.findViewById(R.id.rvimg)
        val titleTextView: TextView = itemView.findViewById(R.id.rvtxt1)
        val descriptionTextView: TextView = itemView.findViewById(R.id.rvtxt2)
    }
}