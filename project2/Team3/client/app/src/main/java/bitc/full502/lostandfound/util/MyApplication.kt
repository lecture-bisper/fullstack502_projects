package bitc.full502.lostandfound.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.provider.Settings
import android.util.Log
import bitc.full502.lostandfound.data.api.ApiClient
import bitc.full502.lostandfound.data.api.FcmService
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyApplication : Application() {

    private var socketConnected: Boolean = false
    private var currentActivity: Activity? = null

    override fun onCreate() {
        super.onCreate()
        // Firebase 초기화
        FirebaseApp.initializeApp(this)
    }

    fun getCurrentActivity(): Activity? {
        return currentActivity
    }

    fun setCurrentActivity(activity: Activity?) {
        currentActivity = activity
    }

    fun isSocketConnected(): Boolean {
        return socketConnected
    }

    fun setSocketConnected(value: Boolean) {
        socketConnected = value
    }

    @SuppressLint("HardwareIds")
    fun saveFcmToken(userToken: String) {
        // FCM 토큰 받기
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM_TOKEN", token)
                val deviceId: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID);

                ApiClient.createScalarService(Constants.BASE_URL, FcmService::class.java)
                    .saveFcmToken("Bearer $userToken", token, deviceId)
                    .enqueue(object : Callback<String> {
                        override fun onResponse(call: Call<String?>, response: Response<String?>) {
                            if (response.isSuccessful) {
                                Log.d("FCM_TOKEN", "토큰 저장 성공!")
                            } else {
                                Log.d("FCM_TOKEN", "토큰 저장 실패: ${response.code()}")
                            }
                        }

                        override fun onFailure(call: Call<String?>, t: Throwable) {
                            Log.d("FCM_TOKEN", "토큰 저장 네크워크 통신 실패: ${t.message}")
                        }
                    })

            } else {
                Log.e("FCM_TOKEN", "토큰 가져오기 실패", task.exception)
            }
        }
    }
}