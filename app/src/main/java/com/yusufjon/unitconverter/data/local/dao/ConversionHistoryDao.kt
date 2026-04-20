package com.yusufjon.unitconverter.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yusufjon.unitconverter.data.local.entity.ConversionHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversionHistoryDao {
    @Query(
        """
        SELECT * FROM conversion_history
        ORDER BY createdAtMillis DESC
        LIMIT :limit
        """,
    )
    fun observeRecent(limit: Int): Flow<List<ConversionHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ConversionHistoryEntity)

    @Query(
        """
        DELETE FROM conversion_history
        WHERE id NOT IN (
            SELECT id FROM conversion_history
            ORDER BY createdAtMillis DESC
            LIMIT :keepCount
        )
        """,
    )
    suspend fun trimTo(keepCount: Int)

    @Query("DELETE FROM conversion_history")
    suspend fun clear()
}
