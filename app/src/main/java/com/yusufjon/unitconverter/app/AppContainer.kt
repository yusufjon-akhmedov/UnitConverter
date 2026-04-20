package com.yusufjon.unitconverter.app

import android.content.Context
import androidx.room.Room
import com.yusufjon.unitconverter.data.local.database.ConverterDatabase
import com.yusufjon.unitconverter.data.local.preferences.UserPreferencesDataStore
import com.yusufjon.unitconverter.data.repository.DataStoreUserPreferencesRepository
import com.yusufjon.unitconverter.data.repository.RoomConversionHistoryRepository
import com.yusufjon.unitconverter.data.repository.RoomFavoriteConversionRepository
import com.yusufjon.unitconverter.domain.converter.NumericInputParser
import com.yusufjon.unitconverter.domain.converter.UnitCatalog
import com.yusufjon.unitconverter.domain.converter.ValueFormatter
import com.yusufjon.unitconverter.domain.usecase.ClearConversionHistoryUseCase
import com.yusufjon.unitconverter.domain.usecase.ConvertValueUseCase
import com.yusufjon.unitconverter.domain.usecase.ObserveConversionHistoryUseCase
import com.yusufjon.unitconverter.domain.usecase.ObserveFavoriteConversionsUseCase
import com.yusufjon.unitconverter.domain.usecase.ObserveUserPreferencesUseCase
import com.yusufjon.unitconverter.domain.usecase.RecordConversionUseCase
import com.yusufjon.unitconverter.domain.usecase.RemoveFavoriteConversionUseCase
import com.yusufjon.unitconverter.domain.usecase.ToggleFavoriteConversionUseCase
import com.yusufjon.unitconverter.domain.usecase.UpdateLastSelectedCategoryUseCase
import com.yusufjon.unitconverter.domain.usecase.UpdateThemeModeUseCase
import com.yusufjon.unitconverter.presentation.viewmodel.ConverterViewModel

class AppContainer(
    context: Context,
) {
    private val applicationContext = context.applicationContext
    private val database = Room.databaseBuilder(
        applicationContext,
        ConverterDatabase::class.java,
        DATABASE_NAME,
    ).build()

    private val userPreferencesDataStore = UserPreferencesDataStore(applicationContext)

    private val conversionHistoryRepository = RoomConversionHistoryRepository(
        dao = database.conversionHistoryDao(),
    )
    private val favoriteConversionRepository = RoomFavoriteConversionRepository(
        dao = database.favoriteConversionDao(),
    )
    private val userPreferencesRepository = DataStoreUserPreferencesRepository(
        userPreferencesDataStore = userPreferencesDataStore,
    )

    private val numericInputParser = NumericInputParser()
    private val valueFormatter = ValueFormatter()
    private val unitCatalog = UnitCatalog()

    val converterViewModelDependencies = ConverterViewModel.Dependencies(
        unitCatalog = unitCatalog,
        valueFormatter = valueFormatter,
        convertValueUseCase = ConvertValueUseCase(
            unitCatalog = unitCatalog,
            inputParser = numericInputParser,
            valueFormatter = valueFormatter,
        ),
        observeConversionHistoryUseCase = ObserveConversionHistoryUseCase(
            repository = conversionHistoryRepository,
        ),
        observeFavoriteConversionsUseCase = ObserveFavoriteConversionsUseCase(
            repository = favoriteConversionRepository,
        ),
        observeUserPreferencesUseCase = ObserveUserPreferencesUseCase(
            repository = userPreferencesRepository,
        ),
        recordConversionUseCase = RecordConversionUseCase(
            repository = conversionHistoryRepository,
        ),
        clearConversionHistoryUseCase = ClearConversionHistoryUseCase(
            repository = conversionHistoryRepository,
        ),
        toggleFavoriteConversionUseCase = ToggleFavoriteConversionUseCase(
            repository = favoriteConversionRepository,
        ),
        removeFavoriteConversionUseCase = RemoveFavoriteConversionUseCase(
            repository = favoriteConversionRepository,
        ),
        updateLastSelectedCategoryUseCase = UpdateLastSelectedCategoryUseCase(
            repository = userPreferencesRepository,
        ),
        updateThemeModeUseCase = UpdateThemeModeUseCase(
            repository = userPreferencesRepository,
        ),
    )

    private companion object {
        const val DATABASE_NAME = "unit_converter.db"
    }
}
