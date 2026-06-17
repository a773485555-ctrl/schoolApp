package com.school.management.core.network

import android.util.Log
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    companion object {
        private const val TAG = "AuthInterceptor"
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
    }

    @Volatile
    private var isRefreshing = false

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Skip auth for login and refresh endpoints
        if (isAuthEndpoint(originalRequest)) {
            return chain.proceed(originalRequest)
        }

        val accessToken = tokenManager.getAccessToken()
        val authenticatedRequest = if (accessToken != null) {
            originalRequest.newBuilder()
                .header(HEADER_AUTHORIZATION, "$BEARER_PREFIX$accessToken")
                .build()
        } else {
            originalRequest
        }

        val response = chain.proceed(authenticatedRequest)

        // If we get a 401, attempt token refresh
        if (response.code == 401 && !isRefreshing) {
            synchronized(this) {
                if (isRefreshing) {
                    // Another thread is already refreshing, retry with whatever token is current
                    response.close()
                    val currentToken = tokenManager.getAccessToken()
                    val retryRequest = originalRequest.newBuilder()
                        .header(HEADER_AUTHORIZATION, "$BEARER_PREFIX${currentToken.orEmpty()}")
                        .build()
                    return chain.proceed(retryRequest)
                }

                isRefreshing = true

                val newToken = runBlocking {
                    try {
                        tokenManager.refreshToken()
                    } catch (e: Exception) {
                        Log.e(TAG, "Token refresh failed in interceptor", e)
                        null
                    }
                }

                isRefreshing = false

                return if (newToken != null) {
                    response.close()
                    val newRequest = originalRequest.newBuilder()
                        .header(HEADER_AUTHORIZATION, "$BEARER_PREFIX$newToken")
                        .build()
                    chain.proceed(newRequest)
                } else {
                    tokenManager.clearTokens()
                    response
                }
            }
        }

        return response
    }

    private fun isAuthEndpoint(request: Request): Boolean {
        val path = request.url.encodedPath
        return path.contains("/auth/login") || path.contains("/auth/refresh")
    }
}
