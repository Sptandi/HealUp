package com.example.app.cbr.databaseDAO

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.example.app.cbr.model.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User)

    @Query("SELECT * FROM user ")
    fun listUser(): List<User>

    @Query("Select idUser from user where emailUser = :email")
    fun userIdSession(email: String?): Int

    @Query("SELECT * FROM user WHERE emailUser == :email and passwordUser == :password and nama = :nama")
    fun checkIsUserExist(email: String, password: String, nama: String): Boolean

    @Query("SELECT * FROM user WHERE emailUser == :email and passwordUser == :password")
    fun checkUser(email: String, password: String): Boolean

    @Query("select * from user where idUser = :id")
    fun getUserData(id: Int?) :User

    @Query("Update user set nama = :nama ,emailUser = :email where idUser = :id")
    fun updateUser(nama: String, email: String, id: Int?)
}