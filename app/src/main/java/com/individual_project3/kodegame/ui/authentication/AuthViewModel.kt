package com.individual_project3.kodegame.ui.authentication


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
    fun registerParent(parentFirst: String,
                       parentLast: String,
                       parentDob: String,
                       email: String,
                       password: String,
                       childFirst: String,
                       childLast: String,
                       childDob: String,
                       onComplete: (Long?) -> Unit){
        viewModelScope.launch{
            _isLoading.value = true
            _error.value = null
            try{
                val id = repo.registerParent(
                    firstName = parentFirst,
                    lastName = parentLast,
                    dob = parentDob,
                    email = email,
                    passwordPlain = password
                )
                sessionManager.setCurrentParentId(id) //persist session
                onComplete(id)
            }catch(e: Exception){
                _error.value = e.message
                onComplete(null)
            }finally {
                _isLoading.value = false
            }
        }
    }

    //parent login flow: verify credentials and persist session on success
    fun loginParent(email: String, password: String, onComplete: (Long?) -> Unit){
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val parent = repo.loginParent(email, password)
                if(parent != null){
                    sessionManager.setCurrentParentId(parent.id)
                    onComplete(parent.id)
                }else{
                    _error.value = "Invalid credentials"
                    onComplete(null)
                }
            }catch (e: Exception){
                _error.value = e.message
                onComplete(null)
            }finally {
                _isLoading.value = false
            }
        }
    }

    //child lookup: find parents by child name and dob
    fun lookupParentsForChild(first: String, last: String, dob: String, onComplete: (List<Parent>) -> Unit){
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try{
                val parents = repo.lookupParentsForChild(first,last,dob)
                onComplete(parents)
            }catch(e:Exception){
                _error.value = e.message
                onComplete(emptyList())
            }finally {
                _isLoading.value = false
            }
        }
    }

    //child registration after selecting parent from dropdown
    fun registerChild(
        parentId: Long,
        first: String,
        last: String,
        dob: String,
        username: String,
        password: String,
        onComplete: (Long?) -> Unit
    ){
        viewModelScope.launch{
            _isLoading.value = true
            _error.value = null
            try{
                val childId = repo.registerChild(
                    parentId, first, last, dob, username, password
                )
                onComplete(childId)
            }catch(e: Exception){
                _error.value = e.message
                onComplete(null)
            }finally{
                _isLoading.value = false
            }
        }
    }
    //child login: find child and save the associated parent id as session
    fun loginChild(username: String, password: String, onComplete: (Child?) -> Unit){
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val child = repo.loginChild(username, password)
                if(child != null){
                    sessionManager.setCurrentParentId(child.parentId)
                    sessionManager.setCurrentChildId(child.id)
                    onComplete(child)
                }else{
                    _error.value = "Invalid credentials"
                    onComplete(null)
                }
            }catch (e:Exception){
                _error.value = e.message
                onComplete(null)
            }finally {
                _isLoading.value = false
            }
        }
    }

    //logout clears the session
    fun logout(){
        viewModelScope.launch { sessionManager.clearSession() }
    }
}
