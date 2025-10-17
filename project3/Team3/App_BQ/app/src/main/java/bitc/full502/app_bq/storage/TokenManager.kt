package bitc.full502.lostandfound.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey


class TokenManager(context: Context) {

    // 마스터 키 생성 (AES256_GCM)
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    // 암호화된 SharedPreferences 생성
    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_prefs", // 파일 이름
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    /** 토큰 저장 */
    fun saveToken(token: String) {
        prefs.edit().putString("accessToken", token).apply()
    }

    /** 토큰 불러오기 */
    fun getToken(): String? {
        return prefs.getString("accessToken", null)
    }

    /** 토큰 삭제 */
    fun clearToken() {
        prefs.edit().remove("accessToken").apply()
    }

    // TokenManager 수정
    fun saveEmpCode(userId: String) {
        prefs.edit().putString("userId", userId).apply()
    }

    fun getUserId(): String? {
        return prefs.getString("userId", null)
    }

    fun isAutoLogin(): Boolean = prefs.getBoolean("autoLogin", false)
}
