package com.ems.lite.admin.room.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ems.lite.admin.model.table.Cast


@Dao
abstract class CastDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(resLogin: List<Cast>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(resLogin: Cast)

    @Query("SELECT * FROM `Cast`")
    abstract fun getAll(): List<Cast>?

    @Query("SELECT * FROM `Cast` where castNo=:id")
    abstract fun get(id: Int): Cast

   @Query("DELETE FROM `Cast`")
   abstract fun clear(): Int

   @Query("SELECT * FROM `Cast` where castNameEng=:name OR castName=:name ")
    abstract fun getCastId(name: String): Cast
}