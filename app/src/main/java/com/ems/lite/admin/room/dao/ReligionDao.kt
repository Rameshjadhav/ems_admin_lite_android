package com.ems.lite.admin.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ems.lite.admin.model.table.Religion

@Dao
abstract class ReligionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(resLogin: List<Religion>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(resLogin: Religion)

    @Query("SELECT * FROM Religion")
    abstract fun getAll(): List<Religion>?

    @Query("DELETE FROM Religion")
    abstract fun clear(): Int
}