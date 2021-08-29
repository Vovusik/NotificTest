package com.example.notifictest

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

const val KEY_ITEM_TEXT = "com.example.notifictest.KEY_ITEM_TEXT"

class PageFragment : Fragment() {

    private val TAG = "PageFragment"
    private var index = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.item_mutable_collection, container, false)

        val buttonNotification: Button = view.findViewById(R.id.buttonNotification)
        buttonNotification.setText(R.string.create_new_notification)
        index = arguments?.getString(KEY_ITEM_TEXT) ?: throw IllegalStateException()

        buttonNotification.setOnClickListener {

//            val toast = Toast.makeText(
//                activity,
//                "Notification $index", Toast.LENGTH_SHORT
//            )
//            toast.show()

            Log.i(TAG, "Сповіщення $index")

            notificationReminder()
        }
        return view
    }

    companion object {
        const val NOTIFICATION_ID = 101
        const val CHANNEL_ID = "channelID"

        fun newInstance(itemText: String) =
            PageFragment().apply {
                arguments = Bundle(1).apply {
                    putString(KEY_ITEM_TEXT, itemText)
                }
            }
    }

    private val largeIcon = BitmapFactory.decodeResource(
        context?.resources,
        R.drawable.outline_person_black_36
    )

    @SuppressLint("UnspecifiedImmutableFlag", "WrongConstant")
    fun notificationReminder() {

        // Створюю явний намір для діяльності у своєму додатку
//        val intent = Intent(context, MutableCollectionFragmentActivity::class.java)
//        val pendingIntent =
//            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//
//        val openIntent = Intent(context, PageFragment::class.java)
//        val pendingOpenIntent =
//        PendingIntent.getActivity(context, 1, openIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        //  Встановлюю вміст і канал сповіщення
        val builder =
            context?.let {
                NotificationCompat.Builder(it, CHANNEL_ID)
                    .setLargeIcon(largeIcon)
                    .setSmallIcon(R.drawable.ic_messenger)
                    .setContentTitle("Chat heads active")
                    .setContentText("Notification $index")
                    .setAutoCancel(true)
                 // .addAction(0, "Action", pendingOpenIntent)
                    .setColor(ContextCompat.getColor(requireContext(), R.color.messenger))
                    .setStyle(NotificationCompat.BigTextStyle())// Стиль сповіщення
                    .setSound(defaultSoundUri) // Встановити звук
                 // .setContentIntent(pendingIntent)
            }

        // Надіслати сповіщення
        val notificationManager = context?.let { NotificationManagerCompat.from(it) }
        builder?.build()?.let { notificationManager?.notify(NOTIFICATION_ID, it) }

        // Зареєструвати в системі канал сповіщень свого додатка,
        // для надісилання сповіщень на Android 8.0 та новіших версіях
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Канал 1",
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            notificationManager?.createNotificationChannel(channel)
        }
    }
}