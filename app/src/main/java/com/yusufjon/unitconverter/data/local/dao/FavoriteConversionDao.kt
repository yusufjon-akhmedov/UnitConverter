package com.yusufjon.unitconverter.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yusufjon.unitconverter.data.local.entity.FavoriteConversionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteConversionDao {
    @Query(
        """
        SELECT * FROM favorite_conversions
        ORDER BY createdAtMillis DESC
        """,
    )
    fun observeAll(): Flow<List<FavoriteConversionEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: FavoriteConversionEntity)

    @Query("DELETE FROM favorite_conversions WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query(
        """
        DELETE FROM favorite_conversions
        WHERE categoryName = :categoryName
        AND fromUnitId = :fromUnitId
        AND toUnitId = :toUnitId
        """,
    )
    suspend fun removeByUnits(
        categoryName: String,
        fromUnitId: String,
        toUnitId: String,
    )
}
