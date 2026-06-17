package com.school.management.feature.auth.data

import com.school.management.core.model.domain.User
import com.school.management.core.model.dto.LoginRequest
import com.school.management.core.model.dto.RefreshTokenRequest
import com.school.management.core.network.ApiService
import com.school.management.core.network.TokenManager
import com.school.management.core.util.Resource
import com.school.management.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(email: String, password: String, role: String): Flow<Resource<User>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.login(LoginRequest(email = email, password = password, role = role))
            if (response.isSuccessful) {
                response.body()?.let { loginResponse ->
                    tokenManager.saveAccessToken(loginResponse.accessToken)
                    tokenManager.saveRefreshToken(loginResponse.refreshToken)
                    tokenManager.saveUserId(loginResponse.user.id)
                    tokenManager.saveUserRole(loginResponse.user.role)
                    loginResponse.user.schoolId?.let { tokenManager.saveSchoolId(it) }

                    val user = User(
                        id = loginResponse.user.id,
                        email = loginResponse.user.email,
                        name = loginResponse.user.name,
                        role = loginResponse.user.role,
                        schoolId = loginResponse.user.schoolId,
                        avatarUrl = loginResponse.user.avatarUrl
                    )
                    emit(Resource.Success(user))
                } ?: emit(Resource.Error("Empty response body"))
            } else {
                val errorMsg = when (response.code()) {
                    401 -> "Invalid email or password"
                    403 -> "Account not authorized for this role"
                    404 -> "Account not found"
                    429 -> "Too many attempts. Please try again later"
                    else -> "Login failed: ${response.message()}"
                }
                emit(Resource.Error(errorMsg))
            }
        } catch (e: java.net.UnknownHostException) {
            emit(Resource.Error("No internet connection. Please check your network."))
        } catch (e: java.net.SocketTimeoutException) {
            emit(Resource.Error("Connection timed out. Please try again."))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }

    override suspend fun refreshToken(): Resource<Unit> {
        return try {
            val currentRefreshToken = tokenManager.getRefreshToken()
                ?: return Resource.Error("No refresh token available")
            val response = apiService.refreshToken(RefreshTokenRequest(refreshToken = currentRefreshToken))
            if (response.isSuccessful) {
                response.body()?.let { tokenResponse ->
                    tokenManager.saveAccessToken(tokenResponse.accessToken)
                    tokenManager.saveRefreshToken(tokenResponse.refreshToken)
                    Resource.Success(Unit)
                } ?: Resource.Error("Empty response")
            } else {
                Resource.Error("Token refresh failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Token refresh failed")
        }
    }

    override suspend fun logout() {
        try {
            apiService.logout()
        } catch (_: Exception) {
            // Best-effort logout on server
        } finally {
            tokenManager.clearAll()
        }
    }

    override fun isLoggedIn(): Boolean {
        return tokenManager.getAccessToken() != null
    }

    override fun getCurrentUser(): User? {
        val userId = tokenManager.getUserId() ?: return null
        val role = tokenManager.getUserRole() ?: return null
        return User(
            id = userId,
            email = "",
            name = "",
            role = role,
            schoolId = tokenManager.getSchoolId(),
            avatarUrl = null
        )
    }
}
