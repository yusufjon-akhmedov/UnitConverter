package com.yusufjon.unitconverter.util

import com.yusufjon.unitconverter.domain.model.ConversionHistoryItem
import com.yusufjon.unitconverter.domain.model.ConversionSnapshot
import com.yusufjon.unitconverter.domain.model.FavoriteConversion
import com.yusufjon.unitconverter.domain.model.ThemeMode
import com.yusufjon.unitconverter.domain.model.UnitCategory
import com.yusufjon.unitconverter.domain.model.UserPreferences
import com.yusufjon.unitconverter.domain.repository.ConversionHistoryRepository
import com.yusufjon.unitconverter.domain.repository.FavoriteConversionRepository
import com.yusufjon.unitconverter.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeConversionHistoryRepository : ConversionHistoryRepository {
    private val historyItems = MutableStateFlow<List<ConversionHistoryItem>>(emptyList())
    private var nextId = 1L

    override fun observeRecent(limit: Int): Flow<List<ConversionHistoryItem>> {
        return historyItems.asStateFlow()
    }

    override suspend fun record(snapshot: ConversionSnapshot) {
        historyItems.value = buildList {
            add(
                ConversionHistoryItem(
                    id = nextId++,
                    category = snapshot.category,
                    fromUnitId = snapshot.fromUnit.id,
                    toUnitId = snapshot.toUnit.id,
                    inputValue = snapshot.inputValue,
                    outputValue = snapshot.outputValue,
                    createdAtMillis = nextId,
                ),
            )
            addAll(historyItems.value)
        }.take(ConversionHistoryRepository.DEFAULT_HISTORY_LIMIT)
    }

    override suspend fun clear() {
        historyItems.value = emptyList()
    }

    fun items(): List<ConversionHistoryItem> = historyItems.value
}

class FakeFavoriteConversionRepository : FavoriteConversionRepository {
    private val favorites = MutableStateFlow<List<FavoriteConversion>>(emptyList())
    private var nextId = 1L

    override fun observeAll(): Flow<List<FavoriteConversion>> = favorites.asStateFlow()

    override suspend fun add(
        category: UnitCategory,
        fromUnitId: String,
        toUnitId: String,
    ) {
        if (favorites.value.any { it.category == category && it.fromUnitId == fromUnitId && it.toUnitId == toUnitId }) {
            return
        }

        favorites.value = listOf(
            FavoriteConversion(
                id = nextId++,
                category = category,
                fromUnitId = fromUnitId,
                toUnitId = toUnitId,
                createdAtMillis = nextId,
            ),
        ) + favorites.value
    }

    override suspend fun remove(id: Long) {
        favorites.value = favorites.value.filterNot { it.id == id }
    }

    override suspend fun remove(
        category: UnitCategory,
        fromUnitId: String,
        toUnitId: String,
    ) {
        favorites.value = favorites.value.filterNot {
            it.category == category && it.fromUnitId == fromUnitId && it.toUnitId == toUnitId
        }
    }

    fun items(): List<FavoriteConversion> = favorites.value
}

class FakeUserPreferencesRepository(
    initialPreferences: UserPreferences = UserPreferences(),
) : UserPreferencesRepository {
    private val preferences = MutableStateFlow(initialPreferences)

    override fun observe(): Flow<UserPreferences> = preferences.asStateFlow()

    override suspend fun setThemeMode(themeMode: ThemeMode) {
        preferences.value = preferences.value.copy(themeMode = themeMode)
    }

    override suspend fun setLastSelectedCategory(category: UnitCategory) {
        preferences.value = preferences.value.copy(lastSelectedCategory = category)
    }

    fun currentPreferences(): UserPreferences = preferences.value
}
