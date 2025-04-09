package com.example.realtimechat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.realtimechat.data.model.User
import com.example.realtimechat.domain.usecase.GetAllUsersUseCase
import com.example.realtimechat.domain.usecase.GetCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val getAllUsersUseCase: GetAllUsersUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users = _users.asStateFlow()

    init {
        loadUsers()
    }

    private fun loadUsers() {
        viewModelScope.launch {
            val currentUser = getCurrentUserUseCase()
            currentUser?.uid?.let { uid ->
                getAllUsersUseCase(uid).collect { otherUsers ->
                    _users.value = otherUsers
                }
            }
        }
    }
}

