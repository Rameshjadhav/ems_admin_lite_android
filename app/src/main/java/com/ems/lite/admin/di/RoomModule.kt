package com.ems.lite.admin.di

import android.content.Context
import androidx.room.Room
import com.ems.lite.admin.room.ElectionDatabase
import com.ems.lite.admin.room.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object RoomModule {

    @Singleton
    @Provides
    fun provideElectionDatabase(@ApplicationContext context: Context): ElectionDatabase {
        return Room
            .databaseBuilder(
                context,
                ElectionDatabase::class.java,
                ElectionDatabase.DATABASE_NAME
            )
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }


    @Singleton
    @Provides
    fun provideUserDetailsDao(db: ElectionDatabase): VoterDao {
        return db.voterDao()
    }

    @Singleton
    @Provides
    fun provideProfessionDao(db: ElectionDatabase): ProfessionDao {
        return db.ProfessionDao()
    }

    @Singleton
    @Provides
    fun provideCastDao(db: ElectionDatabase): CastDao {
        return db.CastDao()
    }

    @Singleton
    @Provides
    fun provideVillageDao(db: ElectionDatabase): VillageDao {
        return db.villageDao()
    }

    @Singleton
    @Provides
    fun provideBoothDao(db: ElectionDatabase): BoothDao {
        return db.BoothDao()
    }
}