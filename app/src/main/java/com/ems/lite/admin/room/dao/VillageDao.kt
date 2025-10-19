package com.ems.lite.admin.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ems.lite.admin.model.table.Village

@Dao
abstract class VillageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(resLogin: List<Village>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(resLogin: Village)

    @Query("SELECT * FROM Village")
    abstract fun getAll(): List<Village>?

    @Query("SELECT * FROM Village WHERE (:name = '' OR villageName LIKE '%' || :name  || '%' OR villageNameEng LIKE '%' || :name  || '%') ")
    abstract fun getAll(name: String?): List<Village>?

    @Query("SELECT * FROM Village where villageNo=:id")
    abstract fun get(id: Long?): Village?

    @Query("DELETE FROM Village")
    abstract fun clear(): Int
}