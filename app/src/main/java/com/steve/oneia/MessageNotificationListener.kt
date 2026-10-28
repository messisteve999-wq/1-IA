package com.steve.oneia

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class MessageNotificationListener : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val extras = sbn.notification.extras
        val title = extras.getCharSequence("android.title")?.toString().orEmpty()
        val message = extras.getCharSequence("android.text")?.toString().orEmpty()

        if (title.isNotBlank() || message.isNotBlank()) {
            // V1 : réception uniquement. Aucun message n'est envoyé automatiquement.
            Log.d("1-IA", "Notification reçue de ${sbn.packageName}")
        }
    }
}
