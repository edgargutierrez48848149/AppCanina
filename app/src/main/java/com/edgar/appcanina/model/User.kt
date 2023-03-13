package com.edgar.appcanina.model

import android.app.Activity
import android.content.Context

data class User(
    val id:Long,
    val email:String,
    val autenticationToken:String
){
    companion object{
        private const val AUTH_PREFS = "auth_prefs"
        private const val ID_KEY = "id"
        private const val EMAIL_KEY = "email"
        private const val AUTH_TOKEN_ID= "token"

        fun setLoggedUser(activity: Activity,user:User) {
            activity.getSharedPreferences(AUTH_PREFS,
            Context.MODE_PRIVATE).also {
                it.edit()
                    .putLong(ID_KEY, user.id)
                    .putString(EMAIL_KEY, user.email)
                    .putString(AUTH_TOKEN_ID,user.autenticationToken)
                    .apply()
            }
        }

        fun getLoggedUser(activity: Activity): User? {
            val prefs =
                activity.getSharedPreferences(AUTH_PREFS, Context.MODE_PRIVATE) ?: return null

            val userId = prefs.getLong(ID_KEY, 0)

            if (userId == 0L) {
                return null
            }

            return User(
                userId,
                prefs.getString(EMAIL_KEY, "") ?: "",
                prefs.getString(AUTH_TOKEN_ID, "") ?: ""
            )
        }

        fun logout(activity: Activity){
            activity.getSharedPreferences(AUTH_PREFS,
                Context.MODE_PRIVATE).also {
                    it.edit().clear().apply()
            }
        }
    }
}
