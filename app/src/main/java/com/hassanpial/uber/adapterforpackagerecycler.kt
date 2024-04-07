package com.hassanpial.uber

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class adapterforpackagerecycler : RecyclerView.Adapter<adapterforpackagerecycler.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.packagerecyclerviewadapter, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
when(position){
    0->{
        holder.title.text="Easy delivery"
        holder.desccrpt.text="Get the best services from us for any types of urgent delivey"
        holder.imageview.setImageResource(R.drawable.sendany)
    }

    1->{
        holder.title.text="Give a surprize"
        holder.desccrpt.text="Is today your someone special's birthday?Do you want to surprize for an anniversary?"
        holder.imageview.setImageResource(R.drawable.surprize)
    }

    2->{
        holder.title.text="Reach with your customers"
        holder.desccrpt.text="Are your customers waiting?Deliver your products by us and get the best review"
        holder.imageview.setImageResource(R.drawable.delightcustomers)
    }

}
        holder.itemView.setOnClickListener {
           itemClickListener?.onItemClick(position)

        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
var title:TextView=itemView.findViewById(R.id.firstTextView)
        var desccrpt=itemView.findViewById<TextView>(R.id.secondTextView)
    var imageview=itemView.findViewById<ImageView>(R.id.itemImageView)
    }

    override fun getItemCount(): Int {
        return  3
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.itemClickListener = listener
    }
}

