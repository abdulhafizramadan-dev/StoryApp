package com.ahr.storyapp.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "auth")

@Singleton
class AuthPreferences @Inject constructor(private val datastore: DataStore<Preferences>) {

    val authToken: Flow<String> = datastore.data.map { preferences ->
        preferences[AUTH_TOKEN] ?: ""
    }

    suspend fun updateAuthToken(token: String) {
        datastore.edit { settings ->
            settings[AUTH_TOKEN] = token
        }
    }

    suspend fun removeAuthToken() {
        datastore.edit { settings ->
            settings[AUTH_TOKEN] = ""
        }
    }

    companion object {
        val AUTH_TOKEN = stringPreferencesKey("token")
    }
}
