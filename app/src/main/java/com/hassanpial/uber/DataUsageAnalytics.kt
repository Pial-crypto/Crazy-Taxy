package com.hassanpial.uber

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class DataUsageAnalytics : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_usage_analytics)

        var back=findViewById<ImageButton>(R.id.backButton)
        back.setOnClickListener(){
            finish()
        }
    }
}