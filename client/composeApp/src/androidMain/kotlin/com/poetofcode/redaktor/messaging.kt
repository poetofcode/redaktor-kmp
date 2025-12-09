package com.poetofcode.redaktor

import android.content.Context
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

/*
    Инструкция:
    https://firebase.google.com/docs/cloud-messaging/android/first-message?hl=ru
    https://medium.com/@ravisharma23523/sending-notifications-to-mobile-devices-with-firebase-cloud-messaging-fcm-in-node-js-8fe3faead58b
 */

fun Context.retrieveFirebasePushToken(onSuccess: (String) -> Unit) {
    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
        if (!task.isSuccessful) {
            task.exception?.printStackTrace()
            return@OnCompleteListener
        }

        // Get new FCM registration token
        val token = task.result
        onSuccess(token)
    })
}
