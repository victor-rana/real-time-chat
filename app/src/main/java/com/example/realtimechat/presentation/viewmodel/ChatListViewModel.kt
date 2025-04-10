package com.example.realtimechat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.realtimechat.data.model.Conversation
import com.example.realtimechat.data.model.User
import com.example.realtimechat.domain.usecase.GetAllUsersUseCase
import com.example.realtimechat.domain.usecase.message.GetRecentConversationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val getAllUsersUseCase: GetAllUsersUseCase,
    private val getRecentConversationsUseCase: GetRecentConversationsUseCase
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _otherUsers = MutableStateFlow<List<User>>(emptyList())
    val otherUsers: StateFlow<List<User>> = _otherUsers.asStateFlow()

    private val _recentConversations = MutableStateFlow<List<Conversation>>(emptyList())
    val recentConversations: StateFlow<List<Conversation>> = _recentConversations.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        fetchCurrentUserAndData()
    }

    private fun fetchCurrentUserAndData() {
        viewModelScope.launch {
            try {
                getAllUsersUseCase().collect { (currentUser, otherUsers) ->
                    _currentUser.value = currentUser
                    _otherUsers.value = otherUsers

                    // Fetch recent conversations once we have current user
                    currentUser?.let { user ->
                        getRecentConversationsUseCase(user.uid).collect { conversations ->
                            _recentConversations.value = conversations
                            _isLoading.value = false
                        }
                    } ?: run {
                        _errorMessage.value = "Current user not found"
                        _isLoading.value = false
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
                _isLoading.value = false
            }
        }
    }
}
