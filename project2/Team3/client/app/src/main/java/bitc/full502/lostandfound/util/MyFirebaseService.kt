package bitc.full502.lostandfound.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import bitc.full502.lostandfound.R
import bitc.full502.lostandfound.storage.TokenManager
import bitc.full502.lostandfound.ui.ChatActivity
import bitc.full502.lostandfound.ui.ChatRoomActivity
import bitc.full502.lostandfound.ui.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if ((application as MyApplication).isSocketConnected()) return

        Log.d("FCM_MESSAGE", "From: ${remoteMessage.from}")
        Log.d("FCM_MESSAGE", "Data: ${remoteMessage.data}")

        val data = remoteMessage.data
        Log.d("**fullstack502**", "메시지 받음: ${data["boardIdx"]}, ${data["title"]}")
        // 타입이 chat이면 알림 생성
        if (data["type"] == "chat") {
            val boardIdx = data["boardIdx"]?.toLong()
            val senderId = data["senderId"]
            val title = data["title"] ?: "새 메시지"
            val body = data["body"] ?: ""

            if (boardIdx != null && senderId != null) {
                sendChatNotification(boardIdx, senderId, title, body)
            }
        }

        refreshChatroom()
    }

    private fun refreshChatroom() {
        val curActivity = (application as MyApplication).getCurrentActivity()
        if(curActivity is ChatRoomActivity){
            curActivity.runOnUiThread {
                curActivity.fetchUserInfo()
            }
        }
    }

    private fun sendChatNotification(boardIdx: Long, senderId: String, title: String, body: String) {
        // ChatActivity Intent
        val chatIntent = Intent(this, ChatActivity::class.java).apply {
            putExtra("boardIdx", boardIdx)
            putExtra("otherUserId", senderId)
        }

        // MainActivity Intent (부모 스택)
        val mainIntent = Intent(this, MainActivity::class.java)

        // TaskStackBuilder로 스택 구성
        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntent(mainIntent)   // 부모 액티비티
            addNextIntent(chatIntent)   // 알림 클릭 시 열릴 액티비티
            getPendingIntent(
                System.currentTimeMillis().toInt(),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        val channelId = "chat_channel_id"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // 채널 생성
        val channel = NotificationChannel(
            channelId,
            "Chat Messages",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Chat message notifications"
            enableLights(true)
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 250, 100, 250)
        }
        notificationManager.createNotificationChannel(channel)

        // Notification
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }


    override fun onNewToken(token: String) {
        Log.d("FCM_TOKEN", "New token: $token")
        val tokenManager = TokenManager(this)

        // 로그인 상태에서만 서버에 FCM 토큰 저장
        tokenManager.getToken()?.let {
            (application as MyApplication).saveFcmToken(it)
        }
    }
}
