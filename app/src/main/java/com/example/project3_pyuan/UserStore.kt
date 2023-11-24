package com.example.project3_pyuan

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserStore(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("userToken")
        private val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
        private val USER_WEIGHT = intPreferencesKey("user_weight")
        private val USER_ACTIVITY_LEVEL = intPreferencesKey("user_activity_level")
        private val USER_WATER_GOAL = intPreferencesKey("user_water_goal")
    }

    val getOnboardingStatus: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[ONBOARDING_DONE] ?: false
    }

    val getUserWeight: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[USER_WEIGHT] ?: -1
    }

    val getUserActivityLevel: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[USER_ACTIVITY_LEVEL] ?: -1
    }

    val getUserWaterGoal: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[USER_WATER_GOAL] ?: -1
    }

    suspend fun saveOnboardingStatus(status: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_DONE] = status
        }
    }

    suspend fun saveWeight(weight: Int) {
        context.dataStore.edit { preferences ->
            preferences[USER_WEIGHT] = weight
        }
    }

    suspend fun saveActivityLevel(activityLevel: Int) {
        context.dataStore.edit { preferences ->
            preferences[USER_ACTIVITY_LEVEL] = activityLevel
        }
    }

    suspend fun saveUserWaterGoal(waterGoal: Int) {
        context.dataStore.edit { preferences ->
            preferences[USER_WATER_GOAL] = waterGoal
        }
    }
}