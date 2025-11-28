package com.individual_project3.kodegame.data

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.first

//extension property to create a Preferences DataStore named "app_prefs"
private val Context.dataStore by preferencesDataStore(name = "app_prefs")

//key used to persist the currently logged-in parent id as a string
private val KEY_CURRENT_PARENT_ID = stringPreferencesKey("current_parent_id")

//DataStore session manager provides session persistence (store current parent id)
class DataStore(private val context: Context) {
    //save current parent id as string in DataStore
    suspend fun setCurrentParentId(id: Long) = withContext(Dispatchers.IO){
        context.dataStore.edit{prefs -> prefs[KEY_CURRENT_PARENT_ID] = id.toString()}
    }

    //read current parent id -> returns null if not set or invalid
    suspend fun getCurrentParentId(): Long? = withContext(Dispatchers.IO){
        context.dataStore.data.first()[KEY_CURRENT_PARENT_ID]?.toLongOrNull()
    }

    //clear saved session
    suspend fun clearSession() = withContext(Dispatchers.IO){
        context.dataStore.edit{prefs -> prefs.remove(KEY_CURRENT_PARENT_ID)}
    }
}