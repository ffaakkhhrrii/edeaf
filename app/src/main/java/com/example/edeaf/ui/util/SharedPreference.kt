package com.example.edeaf.ui.util

import android.app.Activity
import android.content.Context

object SharedPreference {
    private val shared_key = "MyPrefs"

    fun saveString(activity: Activity, key:String,value:String){
        val shared = activity.getSharedPreferences(shared_key, Context.MODE_PRIVATE)
        with(shared.edit()){
            putString(key,value)
            apply()
        }
    }

    fun getString(activity: Activity,key:String) :String {
        val shared = activity.getSharedPreferences(shared_key,Context.MODE_PRIVATE)
        return shared.getString(key,"") ?: ""
    }
}