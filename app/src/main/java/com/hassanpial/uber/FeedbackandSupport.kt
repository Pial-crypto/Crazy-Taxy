package com.hassanpial.uber

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class FeedbackandSupport : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedbackand_support)
        var back=findViewById<ImageButton>(R.id.backButton)
        back.setOnClickListener(){
            finish()
        }
    }
}