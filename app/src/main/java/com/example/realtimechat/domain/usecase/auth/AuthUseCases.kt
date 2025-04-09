package com.example.realtimechat.domain.usecase.auth

import com.example.realtimechat.domain.usecase.GetAllUsersUseCase
import com.example.realtimechat.domain.usecase.GetCurrentUserUseCase

data class AuthUseCases(
    val login: LoginUseCase,
    val register: RegisterUseCase,
    val getCurrentUser: GetCurrentUserUseCase,
    val getAllUsers: GetAllUsersUseCase
)

