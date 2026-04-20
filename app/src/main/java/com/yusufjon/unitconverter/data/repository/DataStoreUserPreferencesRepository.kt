package com.yusufjon.unitconverter.data.repository

import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.yusufjon.unitconverter.data.local.preferences.UserPreferencesDataStore
import com.yusufjon.unitconverter.domain.model.ThemeMode
import com.yusufjon.unitconverter.domain.model.UnitCategory
import com.yusufjon.unitconverter.domain.model.UserPreferences
import com.yusufjon.unitconverter.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class DataStoreUserPreferencesRepository(
    private val userPreferencesDataStore: UserPreferencesDataStore,
) : UserPreferencesRepository {

    override fun observe(): Flow<UserPreferences> {
        return userPreferencesDataStore.dataStore.data
            .catch { throwable ->
                if (throwable is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw throwable
                }
            }
            .map { preferences ->
                UserPreferences(
                    themeMode = ThemeMode.fromName(preferences[Keys.THEME_MODE]),
                    lastSelectedCategory = UnitCategory.fromName(preferences[Keys.LAST_SELECTED_CATEGORY]),
                )
            }
    }

    override suspend fun setThemeMode(themeMode: ThemeMode) {
        userPreferencesDataStore.dataStore.edit { preferences ->
            preferences[Keys.THEME_MODE] = themeMode.name
        }
    }

    override suspend fun setLastSelectedCategory(category: UnitCategory) {
        userPreferencesDataStore.dataStore.edit { preferences ->
            preferences[Keys.LAST_SELECTED_CATEGORY] = category.name
        }
    }

    private object Keys {
        val THEME_MODE: Preferences.Key<String> = stringPreferencesKey("theme_mode")
        val LAST_SELECTED_CATEGORY: Preferences.Key<String> =
            stringPreferencesKey("last_selected_category")
    }
}
