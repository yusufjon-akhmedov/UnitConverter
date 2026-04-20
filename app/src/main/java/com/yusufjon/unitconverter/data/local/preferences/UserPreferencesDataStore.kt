package com.yusufjon.unitconverter.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile

class UserPreferencesDataStore(
    context: Context,
) {
    val dataStore: DataStore<Preferences> = PreferenceDataStoreFactory.create(
        produceFile = {
            context.preferencesDataStoreFile(PREFERENCES_FILE_NAME)
        },
    )

    private companion object {
        const val PREFERENCES_FILE_NAME = "user_preferences.preferences_pb"
    }
}
