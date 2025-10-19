package com.ems.lite.admin.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ems.lite.admin.model.table.Booth
import com.ems.lite.admin.model.table.Cast
import com.ems.lite.admin.model.table.Designation
import com.ems.lite.admin.model.table.Profession
import com.ems.lite.admin.model.table.Religion
import com.ems.lite.admin.model.table.Village
import com.ems.lite.admin.model.table.Voter
import com.ems.lite.admin.room.dao.BoothDao
import com.ems.lite.admin.room.dao.CastDao
import com.ems.lite.admin.room.dao.DesignationDao
import com.ems.lite.admin.room.dao.ProfessionDao
import com.ems.lite.admin.room.dao.ReligionDao
import com.ems.lite.admin.room.dao.VillageDao
import com.ems.lite.admin.room.dao.VoterDao

@Database(
    entities = [Voter::class, Profession::class, Cast::class,
        Village::class, Booth::class,
        Designation::class, Religion::class],
    version = 1, exportSchema = false
)

abstract class ElectionDatabase : RoomDatabase() {

    abstract fun voterDao(): VoterDao
    abstract fun ProfessionDao(): ProfessionDao
    abstract fun CastDao(): CastDao
    abstract fun villageDao(): VillageDao
    abstract fun BoothDao(): BoothDao
    abstract fun designationDao(): DesignationDao
    abstract fun religionDao(): ReligionDao

    companion object {
        val DATABASE_NAME: String = "ElectionDB"
        private var INSTANCE: ElectionDatabase? = null

        fun getDatabase(context: Context?): ElectionDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context!!, ElectionDatabase::class.java, DATABASE_NAME)
                    .allowMainThreadQueries()
//                    .addMigrations(Migration12/*, Migration23, Migration34*/)
                    .build()
            }
            return INSTANCE!!
        }
    }

}