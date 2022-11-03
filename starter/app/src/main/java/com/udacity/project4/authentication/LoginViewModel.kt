package com.udacity.project4.authentication

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.udacity.project4.base.BaseViewModel
import com.udacity.project4.base.NavigationCommand

class LoginViewModel() : ViewModel(){

    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED, INVALID_AUTHENTICATION
    }

    val authenticationState = FirebaseUserLiveData().map { user ->
        if (user != null) {
            AuthenticationState.AUTHENTICATED
        } else {
            AuthenticationState.UNAUTHENTICATED
        }
    }
}
