package com.hassanpial.uber

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMservice : FirebaseMessagingService() {


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Extract custom data from the message payload
        val targetApp = remoteMessage.data["target_app"]
        val targetActivity = remoteMessage.data["target_activity"]

        // Based on the target app and activity received, open the corresponding activity
        if (targetApp == "rider_app" && targetActivity == "RiderActivity") {
            // Start RiderActivity
          //  startActivity(Intent(this, RiderActivity::class.java))
        } else if (targetApp == "driver_app" && targetActivity == "DriverActivity") {
            // Start DriverActivity
           // startActivity(Intent(this, DriverActivity::class.java))
        }
    }

    override fun onNewToken(token: String) {
        // Handle token refresh
        super.onNewToken(token)
    }
}