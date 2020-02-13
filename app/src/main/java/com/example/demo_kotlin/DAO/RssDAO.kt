package com.example.demo_kotlin.DAO

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.demo_kotlin.Database.DatabaseHelper
import com.example.demo_kotlin.Model.RSS
import java.util.*

class RssDAO(context: Context?) {
    private val db: SQLiteDatabase
    private val dbHelper: DatabaseHelper
    fun addRss(rss: RSS): Int {
        val values = ContentValues()
        values.put("tittle", rss.tittle)
        values.put("link", rss.link)
        values.put("pubdate", rss.pubdate)
        try {
            if (db.insert("News", null, values) < 0) {
                return -1
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
        return 1
    }

    val allRss: MutableList<RSS>
        get() {
            val rssList: MutableList<RSS> = ArrayList()
            val sql = " SELECT * FROM News"
            val cursor = db.rawQuery(sql, null)
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                val rss = RSS()
                rss.id = cursor.getString(0).toInt()
                rss.tittle = cursor.getString(1)
                rss.link = cursor.getString(2)
                rss.pubdate = cursor.getString(3)
                rssList.add(rss)
                cursor.moveToNext()
            }
            cursor.close()
            return rssList
        }

    fun checkTitile(tittle: String): Boolean {
        val cursor =
            db.rawQuery(" SELECT tittle FROM News WHERE tittle =? ", arrayOf(tittle))
        return if (cursor.count > 0) true else false
    }

    companion object {
        const val TABLE_NAME = "News"
        const val SQL_NEWS =
            "CREATE TABLE News (idnews INTEGER PRIMARY KEY AUTOINCREMENT,tittle text,link text,pubdate text);"
        const val TAG = "NewsRSS"
    }

    init {
        dbHelper = DatabaseHelper(context)
        db = dbHelper.writableDatabase
    }
}