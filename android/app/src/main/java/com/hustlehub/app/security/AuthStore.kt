package com.hustlehub.app.security

import com.hustlehub.app.model.User

interface AuthStore {
    fun saveAuthData(token: String, user: User)
    fun getToken(): String?
    fun getUser(): User?
    fun clear()
    fun isLoggedIn(): Boolean
}