package com.example.demo_kotlin.DAO

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.demo_kotlin.Database.DatabaseHelper
import com.example.demo_kotlin.Model.Clothes
import java.util.*

class ClothesDAO(context: Context?) {
    private val db: SQLiteDatabase
    private val dbHelper: DatabaseHelper = DatabaseHelper(context)
    fun inserClothes(s: Clothes): Int {
        val values = ContentValues()
        values.put("id_clothes", s.idclothes)
        values.put("name_clothes", s.nameclothes)
        values.put("price", s.price)
        values.put("amount", s.amount)
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
    val allClothes: MutableList<Clothes>
        get() {
            val dsClothes: MutableList<Clothes> = ArrayList()
            val c =
                db.query(TABLE_NAME, null, null, null, null, null, null)
            c.moveToFirst()
            while (!c.isAfterLast) {
                val s = Clothes()
                s.idclothes = c.getString(0)
                s.nameclothes = c.getString(1)
                s.price = c.getDouble(2)
                s.amount = c.getInt(3)
                dsClothes.add(s)
                Log.d("//=====", s.toString())
                c.moveToNext()
            }
            c.close()
            return dsClothes
        }

    //update
    fun updateClothes(s: Clothes): Int {
        val values = ContentValues()
        values.put("id_clothes", s.idclothes)
        values.put("name_clothes", s.nameclothes)
        values.put("price", s.price)
        values.put("amount", s.amount)
        val result = db.update(
            TABLE_NAME,
            values,
            "id_clothes=?",
            arrayOf(s.idclothes)
        )
        return if (result == 0) {
            -1
        } else 1
    }

    fun updateAmounClothes(amount: Int, id: String): Int {
        val values = ContentValues()
        values.put("amount", amount)
        val result = db.update(
            TABLE_NAME,
            values,
            "id_clothes=?",
            arrayOf(id)
        )
        return if (result == 0) {
            -1
        } else 1
    }

    //delete
    fun deleteClothesByID(maSach: String): Int {
        val result = db.delete(
            TABLE_NAME,
            "id_clothes=?",
            arrayOf(maSach)
        )
        return if (result == 0) -1 else 1
    }

    //getAll
    fun getClothesByID(idclothes: String): Clothes? { //WHERE clause
        var s: Clothes? = null
        val selection = "id_clothes=?"
        //WHERE clause arguments
        val selectionArgs = arrayOf(idclothes)
        val c = db.query(
            TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        Log.d("getClothesByID", "===>" + c.count)
        c.moveToFirst()
        while (c.isAfterLast == false) {
            s = Clothes()
            s.idclothes = c.getString(0)
            s.nameclothes = c.getString(1)
            s.price = c.getDouble(2)
            s.amount = c.getInt(3)
            break
        }
        c.close()
        return s
    }

    fun getAmountByID(idbook: String): Int { //WHERE clause
        var amount = 0
        val selection = "id_clothes=?"
        //WHERE clause arguments
        val selectionArgs = arrayOf(idbook)
        val c = db.query(
            TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        Log.d("getAmountByID", "===>" + c.count)
        c.moveToFirst()
        while (!c.isAfterLast) {
            amount = c.getInt(c.getColumnIndex("amount"))
            break
        }
        c.close()
        return amount
    }

    companion object {
        const val TABLE_NAME = "Clothes"
        const val SQL_CLOTHES =
            "CREATE TABLE Clothes (id_clothes text primary key,name_clothes text, price double, amount int);"
        const val TAG = "Clothes"
    }

    init {
        db = dbHelper.writableDatabase
    }
}