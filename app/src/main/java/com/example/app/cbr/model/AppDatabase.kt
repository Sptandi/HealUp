package com.example.app.cbr.model

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import com.example.app.cbr.Converters
import com.example.app.cbr.databaseDAO.*


@Database(entities = [
    User::class,
    Konsultasi::class,
    Gejala::class,
    Solusi::class,
    Kasus::class,
    Komponen::class,
    KomponenGejala::class,
    GejalaSolusi::class]
    , version = 73, exportSchema = false)
@TypeConverters(Converters::class)

public abstract class AppDatabase : RoomDatabase() {
    public abstract fun UserDao(): UserDao
    public abstract fun KonsultasiDao(): KonsultasiDao
    public abstract fun KasusDao(): KasusDao
    public abstract fun GejalaDao(): GejalaDao
    public abstract fun KomponenGejalaDao(): KomponenGejalaDao
    public abstract fun KomponenDao(): KomponenDao
    public abstract fun SolusiDao(): SolusiDao
    public abstract fun GejalaSolusiDao(): GejalaSolusiDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getAppDataBase(context: Context): AppDatabase? {
            if (INSTANCE == null){
                synchronized(AppDatabase::class){
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        AppDatabase::class.java, "db").fallbackToDestructiveMigration().allowMainThreadQueries().build()
                }
            }
            return INSTANCE
        }

        fun destroyDataBase(){
            INSTANCE = null
        }
    }
}