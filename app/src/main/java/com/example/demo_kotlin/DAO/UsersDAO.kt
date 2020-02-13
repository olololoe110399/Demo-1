package com.example.demo_kotlin.DAO

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.demo_kotlin.Database.DatabaseHelper
import com.example.demo_kotlin.Model.User
import java.util.*

class UsersDAO(context: Context?) {
    private val db: SQLiteDatabase
    private val dbHelper: DatabaseHelper
    //insert
    fun inserUser(nd: User): Int {
        val values = ContentValues()
        values.put("username", nd.mUser)
        values.put("password", nd.mPassword)
        values.put("image", nd.mImage)
        try {
            if (db.insert(TABLE_NAME, null, values) == -1L) {
                return -1
            }
        } catch (ex: Exception) {
            Log.e(TAG, ex.toString())
        }
        return 1
    }

    //getAll
    val allUser: MutableList<User>
        get() {
            val dsNguoiDung: MutableList<User> =
                ArrayList()
            val c =
                db.query(TABLE_NAME, null, null, null, null, null, null)
            c.moveToFirst()
            while (c.isAfterLast == false) {
                val ee = User()
                ee.mUser=c.getString(0)
                ee.mPassword=c.getString(1)
                ee.mPhone=c.getString(2)
                ee.mName=c.getString(3)
                ee.mAddress=(c.getString(4))
                ee.mImage=c.getBlob(5)
                dsNguoiDung.add(ee)
                Log.d("//=====", ee.toString())
                c.moveToNext()
            }
            c.close()
            return dsNguoiDung
        }

    fun checknull(user: String): Int {
        var id = -1
        val c = db.rawQuery(
            "SELECT * FROM $TABLE_NAME WHERE username= '$user'",
            null
        )
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    if (c.isNull(3)) {
                        id = 1
                        Log.e("DUY", "null")
                    }
                } while (c.moveToNext())
            }
            c.close()
        }
        return id
    }

    fun getuserbyid2(user: String): User? {
        var ee: User? = null
        val c = db.rawQuery(
            "SELECT * FROM $TABLE_NAME WHERE username = '$user'",
            null
        )
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    ee = User()
                    ee.mUser=(c.getString(0))
                    ee.mPassword=(c.getString(1))
                    ee.mPhone=(c.getString(2))
                    ee.mName=(c.getString(3))
                    ee.mAddress=(c.getString(4))
                    ee.mImage=(c.getBlob(5))
                } while (c.moveToNext())
            }
            c.close()
        }
        return ee
    }

    fun changePasswordUser(nd: User): Int {
        val values = ContentValues()
        values.put("password", nd.mPassword)
        val result = db.update(
            TABLE_NAME,
            values,
            "username=?",
            arrayOf(nd.mUser)
        )
        return if (result == 0) {
            -1
        } else 1
    }

    fun updateInfoUser(
        username: String,
        phone: String?,
        name: String?,
        address: String?
    ): Int {
        val values = ContentValues()
        values.put("phone", phone)
        values.put("fullname", name)
        values.put("address", address)
        val result = db.update(
            TABLE_NAME,
            values,
            "username=?",
            arrayOf(username)
        )
        return if (result == 0) {
            -1
        } else 1
    }

    fun updateUser(student: User): Int {
        val values = ContentValues()
        values.put("phone", student.mPhone)
        values.put("fullname", student.mName)
        values.put("address", student.mAddress)
        values.put("image", student.mImage)
        val result = db.update(
            TABLE_NAME,
            values,
            "username=?",
            arrayOf(student.mUser)
        )
        return if (result == 0) {
            -1
        } else 1
    }

    fun addimage(username: String, hinh: ByteArray?): Int {
        val values = ContentValues()
        values.put("image", hinh)
        val result = db.update(
            TABLE_NAME,
            values,
            "username=?",
            arrayOf(username)
        )
        return if (result == 0) {
            -1
        } else 1
    }

    //delete
    fun deleteUserByID(username: String): Int {
        val result =
            db.delete(TABLE_NAME, "username=?", arrayOf(username))
        return if (result == 0) -1 else 1
    }

    //check login
    fun checkLogin(username: String, password: String): Int {
        var id = -1
        val cursor = db.rawQuery(
            "SELECT username FROM User WHERE username=? AND password=?",
            arrayOf(username, password)
        )
        if (cursor.count > 0) {
            cursor.moveToFirst()
            id = 1
            cursor.close()
        }
        return id
    }

    fun checkEmail(username: String): Int {
        var id = -1
        val cursor = db.rawQuery(
            "SELECT username FROM User WHERE username=?",
            arrayOf(username)
        )
        if (cursor.count > 0) {
            cursor.moveToFirst()
            id = 1
            cursor.close()
        }
        return id
    }

    fun checkPhone(phone: String): Int {
        var id = -1
        val cursor =
            db.rawQuery("SELECT phone FROM User WHERE phone=?", arrayOf(phone))
        if (cursor.count > 0) {
            cursor.moveToFirst()
            id = 1
            cursor.close()
        }
        return id
    }

    companion object {
        const val TABLE_NAME = "User"
        const val SQL_USER =
            "CREATE TABLE User (username text primary key, password text, phone text, fullname text,address text,image BLOB);"
        const val TAG = "NguoiDungDAO"
    }

    init {
        dbHelper = DatabaseHelper(context)
        db = dbHelper.writableDatabase
    }
}