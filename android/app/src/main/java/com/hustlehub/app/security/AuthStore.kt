package com.hustlehub.app.security

import com.hustlehub.app.model.User

interface AuthStore {
    fun saveAuthData(token: String, userJson: String)
    fun getToken(): String?
    fun getUserJson(): String?
    fun clear()
    fun isLoggedIn(): Boolean
}