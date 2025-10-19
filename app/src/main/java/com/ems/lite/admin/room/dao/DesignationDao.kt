package com.ems.lite.admin.room.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ems.lite.admin.model.table.Designation


@Dao
abstract class DesignationDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(resLogin: List<Designation>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(resLogin: Designation)

    @Query("SELECT * FROM `Designation`")
    abstract fun getAll(): List<Designation>?

//    @Query("SELECT * FROM `Designation` where docID=:id")
//    abstract fun get(id: Int): Designation

   @Query("DELETE FROM `Designation`")
   abstract fun clear(): Int

//   @Query("SELECT * FROM `Designation` where castname=:name ")
//    abstract fun getCastId(name: String): Designation
}