package com.ems.lite.admin.room.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ems.lite.admin.model.table.*


@Dao
abstract class BoothDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(resLogin: List<Booth>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(resLogin: Booth)

    @Query("SELECT * FROM `Booth`")
    abstract fun getAll(): List<Booth>


    @Query("SELECT * FROM `Booth` where boothNo=:id")
    abstract fun get(id: Long?): Booth?

    @Query("SELECT * FROM `Booth` where boothNo=:id")
    abstract fun getBoothListByWard(id: Int): Booth

    @Query("DELETE FROM `Booth`")
    abstract fun clear(): Int

    @Query("SELECT * FROM `Booth` where villageNo=:villageNo ")
    abstract fun getAllByVillageNo(villageNo: Long?): List<Booth>?

    @Query("SELECT * FROM `Booth` where (:villageNo = 0 OR villageNo=:villageNo) AND (:boothName = '' OR LOWER(boothNameEng) LIKE '%' ||  :boothName || '%' OR LOWER(boothName) LIKE '%' || :boothName || '%') ")
    abstract fun getAllByVillageNo(villageNo: Long?, boothName: String?): List<Booth>?

    @Query("SELECT COUNT(*) FROM `Booth` where (:villageNo ==0 OR villageNo == :villageNo)")
    abstract fun getBoothTotalByWard(villageNo: Long?): Long

    @Query("SELECT * FROM `Booth` where boothName=:bName OR boothNameEng=:bName")
    abstract fun getBoothId(bName: String): Booth
}