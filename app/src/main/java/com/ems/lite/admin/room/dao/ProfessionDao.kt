package com.ems.lite.admin.room.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ems.lite.admin.model.table.Profession


@Dao
abstract class ProfessionDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(resLogin: List<Profession>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(resLogin: Profession)

    @Query("SELECT * FROM Profession")
    abstract fun getAll(): List<Profession>?

    @Query("SELECT * FROM Profession where professionNo=:id")
    abstract fun get(id: Int): Profession

   @Query("DELETE FROM Profession")
   abstract fun clear(): Int

    @Query("SELECT * FROM `Profession` where professionNameEng=:name OR professionName=:name ")
    abstract fun getProfessionId(name: String): Profession
}