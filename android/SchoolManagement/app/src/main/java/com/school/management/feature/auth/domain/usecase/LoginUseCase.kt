package com.school.management.feature.auth.domain.usecase

import android.util.Patterns
import com.school.management.core.model.domain.User
import com.school.management.core.util.Resource
import com.school.management.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String, role: String): Flow<Resource<User>> {
        val trimmedEmail = email.trim()

        if (trimmedEmail.isBlank()) {
            return flowOf(Resource.Error("Email address is required"))
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            return flowOf(Resource.Error("Please enter a valid email address"))
        }

        if (password.isBlank()) {
            return flowOf(Resource.Error("Password is required"))
        }

        if (password.length < 6) {
            return flowOf(Resource.Error("Password must be at least 6 characters"))
        }

        if (role.isBlank()) {
            return flowOf(Resource.Error("Please select a role"))
        }

        return authRepository.login(trimmedEmail, password, role)
    }
}
