package com.hassanpial.uber

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PickedUp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picked_up)
        findViewById<Button>(R.id.back_button).setOnClickListener(){
            finish()
        }
var selectedDate=" "
        var selectedTime=" "
        var datePicker=findViewById<DatePicker>(R.id.date_text)
        findViewById<TextView>(R.id.choosedatetextview).setOnClickListener(){
            findViewById<DatePicker>(R.id.date_text).visibility=View.VISIBLE
            findViewById<TimePicker>(R.id.current_time_text).visibility=View.INVISIBLE
            findViewById<TextView>(R.id.text2).visibility=View.INVISIBLE
            findViewById<TextView>(R.id.text1).visibility=View.INVISIBLE
            findViewById<TextView>(R.id.text3).visibility=View.INVISIBLE
            // Set a listener for the DatePicker
            datePicker.init(datePicker.year, datePicker.month, datePicker.dayOfMonth) { _, year, monthOfYear, dayOfMonth ->
           var selectedYear = year
         var   selectedMonth = monthOfYear
           var  selectedDay = dayOfMonth

            selectedDate = "$selectedYear-$selectedMonth-$selectedDay"
        }
        }
var timePicker= findViewById<TimePicker>(R.id.current_time_text)
        findViewById<TextView>(R.id.choosetimetextview).setOnClickListener(){
            findViewById<DatePicker>(R.id.date_text).visibility=View.INVISIBLE
            findViewById<TimePicker>(R.id.current_time_text).visibility=View.VISIBLE
            // Set a listener for the TimePicker
            findViewById<TextView>(R.id.text2).visibility=View.INVISIBLE
            findViewById<TextView>(R.id.text1).visibility=View.INVISIBLE
            findViewById<TextView>(R.id.text3).visibility=View.INVISIBLE
            timePicker.setOnTimeChangedListener(object : TimePicker.OnTimeChangedListener {
                override fun onTimeChanged(view: TimePicker?, hourOfDay: Int, minute: Int) {
                  var  selectedHour = hourOfDay
                    var selectedMinute = minute

                    selectedTime = "$selectedHour:$selectedMinute"

                }
            })
        }

        findViewById<Button>(R.id.fix_time_button).setOnClickListener(){
            findViewById<TextView>(R.id.text2).visibility=View.VISIBLE
            findViewById<TextView>(R.id.text1).visibility=View.VISIBLE
            findViewById<TextView>(R.id.text3).visibility=View.VISIBLE
            datePicker.visibility=View.INVISIBLE
            timePicker.visibility=View.INVISIBLE
            var intent=Intent(this,MapsActivity2::class.java)
            if(selectedTime!=" "&& selectedDate!=" ") {
                intent.putExtra("time", selectedTime)
                intent.putExtra("date", selectedDate)
                startActivity(intent)
            }

            else{
                Toast.makeText(this,"Please set the date and time",Toast.LENGTH_SHORT).show()
            }
        }
    }
}