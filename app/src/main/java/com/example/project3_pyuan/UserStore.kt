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
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("userInfo")
        private val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
        private val USER_WEIGHT = intPreferencesKey("user_weight")
        private val USER_ACTIVITY_LEVEL = intPreferencesKey("user_activity_level")
        private val USER_WATER_GOAL = intPreferencesKey("user_water_goal")
        private val TODAY_WATER = intPreferencesKey("today_water")
        private val DAY = intPreferencesKey("day")
        private val USER_CURRENT_STREAK = intPreferencesKey("user_current_streak")
        private val USER_BEST_STREAK = intPreferencesKey("user_best_streak")
    }

    suspend fun clearAll() {
        context.dataStore.edit {
            it.clear()
        }
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
        preferences[USER_WATER_GOAL] ?: 0
    }

    val getTodayWater: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[TODAY_WATER] ?: -1
    }

    val getDay: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[DAY] ?: 0
    }

    val getCurrentStreak: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[USER_CURRENT_STREAK] ?: 0
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

    suspend fun setTodayWater(newTodayWater: Int) {
        context.dataStore.edit { preferences ->
            preferences[TODAY_WATER] = newTodayWater
        }
    }

    suspend fun saveDay(day: Int) {
        context.dataStore.edit { preferences ->
            preferences[DAY] = day
        }
    }

    suspend fun saveStreak(newStreak: Int) {
        context.dataStore.edit { preferences ->
            preferences[USER_CURRENT_STREAK] = newStreak
        }
    }
}