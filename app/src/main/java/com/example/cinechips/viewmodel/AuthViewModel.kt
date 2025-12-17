package com.example.cinechips.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cinechips.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel : ViewModel() {

    private val repo = FirebaseRepository()

    private val _loginResult = MutableLiveData<Pair<Boolean, String?>>()
    val loginResult: LiveData<Pair<Boolean, String?>> = _loginResult

    private val _isAdmin = MutableLiveData<Boolean>()
    val isAdmin: LiveData<Boolean> = _isAdmin

    fun login(email: String, password: String) {
        repo.loginUser(email, password) { success, error ->
            if (success) {
                val uid = FirebaseAuth.getInstance().currentUser!!.uid

                // USE YOUR FUNCTION HERE
                repo.isAdmin(uid) { adminStatus ->
                    _isAdmin.value = adminStatus
                    _loginResult.value = Pair(true, null)
                }

            } else {
                _loginResult.value = Pair(false, error)
            }
        }
    }
}