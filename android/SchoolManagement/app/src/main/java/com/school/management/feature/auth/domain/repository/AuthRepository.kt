package com.school.management.feature.auth.domain.repository

import com.school.management.core.model.domain.User
import com.school.management.core.util.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String, role: String): Flow<Resource<User>>
    suspend fun refreshToken(): Resource<Unit>
    suspend fun logout()
    fun isLoggedIn(): Boolean
    fun getCurrentUser(): User?
}
