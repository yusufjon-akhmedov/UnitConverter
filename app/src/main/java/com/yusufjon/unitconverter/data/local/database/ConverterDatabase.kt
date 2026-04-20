package com.yusufjon.unitconverter.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yusufjon.unitconverter.data.local.dao.ConversionHistoryDao
import com.yusufjon.unitconverter.data.local.dao.FavoriteConversionDao
import com.yusufjon.unitconverter.data.local.entity.ConversionHistoryEntity
import com.yusufjon.unitconverter.data.local.entity.FavoriteConversionEntity

@Database(
    entities = [
        ConversionHistoryEntity::class,
        FavoriteConversionEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class ConverterDatabase : RoomDatabase() {
    abstract fun conversionHistoryDao(): ConversionHistoryDao

    abstract fun favoriteConversionDao(): FavoriteConversionDao
}
