package com.individual_project3.kodegame.ui.authentication

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.individual_project3.kodegame.data.repository.AuthRepository
import com.individual_project3.kodegame.data.DataStore
import com.individual_project3.kodegame.data.model.Parent
import com.individual_project3.kodegame.data.model.Child
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

//ViewModel uses the repository and DataStore(session manager)

class AuthViewModel(
    private val repo: AuthRepository,
    private val sessionManager: DataStore
) : ViewModel(){
    //loading and error state exposed as StateFlow() for Compose or UI observe
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    //register a parent and a child. on success, session is saved

}
