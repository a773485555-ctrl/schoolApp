package com.school.management.core.network

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.school.management.core.model.dto.AuthResponse
import com.school.management.core.model.dto.RefreshRequest
import com.school.management.core.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences by lazy {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        EncryptedSharedPreferences.create(
            Constants.ENCRYPTED_PREFS_NAME,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private var apiService: ApiService? = null

    fun setApiService(apiService: ApiService) {
        this.apiService = apiService
    }

    fun saveTokens(accessToken: String, refreshToken: String) {
        prefs.edit()
            .putString(Constants.KEY_ACCESS_TOKEN, accessToken)
            .putString(Constants.KEY_REFRESH_TOKEN, refreshToken)
            .apply()
    }

    fun getAccessToken(): String? {
        return prefs.getString(Constants.KEY_ACCESS_TOKEN, null)
    }

    fun getRefreshToken(): String? {
        return prefs.getString(Constants.KEY_REFRESH_TOKEN, null)
    }

    fun clearTokens() {
        prefs.edit()
            .remove(Constants.KEY_ACCESS_TOKEN)
            .remove(Constants.KEY_REFRESH_TOKEN)
            .apply()
    }

    fun isLoggedIn(): Boolean {
        return getAccessToken() != null
    }

    suspend fun refreshToken(): String? {
        val refreshToken = getRefreshToken() ?: return null
        val service = apiService ?: return null

        return try {
            val response: Response<AuthResponse> = service.refreshToken(
                RefreshRequest(refreshToken)
            )
            if (response.isSuccessful) {
                val authResponse = response.body()
                if (authResponse != null) {
                    saveTokens(authResponse.accessToken, authResponse.refreshToken)
                    authResponse.accessToken
                } else {
                    clearTokens()
                    null
                }
            } else {
                clearTokens()
                null
            }
        } catch (e: Exception) {
            Log.e("TokenManager", "Token refresh failed", e)
            clearTokens()
            null
        }
    }

    fun getUserRole(): String? {
        return getClaimFromToken("role")
    }

    fun getUserId(): Int? {
        return getClaimFromToken("sub")?.toIntOrNull()
    }

    fun getSchoolId(): Int? {
        return getClaimFromToken("school_id")?.toIntOrNull()
    }

    fun getClassName(): String? {
        return getClaimFromToken("class_name")
    }

    fun getSection(): String? {
        return getClaimFromToken("section")
    }

    private fun getClaimFromToken(claim: String): String? {
        val token = getAccessToken() ?: return null
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return null

            val payload = parts[1]
            val paddedPayload = when (payload.length % 4) {
                2 -> "$payload=="
                3 -> "$payload="
                else -> payload
            }
            val decodedBytes = Base64.decode(paddedPayload, Base64.URL_SAFE or Base64.NO_WRAP)
            val decodedString = String(decodedBytes, Charsets.UTF_8)
            val jsonObject = JSONObject(decodedString)

            if (jsonObject.has(claim)) {
                jsonObject.get(claim).toString()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("TokenManager", "Failed to parse JWT claim: $claim", e)
            null
        }
    }
}
