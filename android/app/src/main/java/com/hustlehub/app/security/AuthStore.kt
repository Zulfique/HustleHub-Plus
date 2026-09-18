package com.hustlehub.app.security

import com.hustlehub.app.model.User
import com.google.gson.Gson

val GSON = Gson()

interface AuthStore {
    fun saveAuthData(token: String, user: User)
    fun getToken(): String?
    fun getUser(): User?
    fun clear()
    fun isLoggedIn(): Boolean
}