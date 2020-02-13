package com.example.demo_kotlin.Database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.demo_kotlin.DAO.*

class DatabaseHelper(context: Context?) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    VERSION
) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(RssDAO.SQL_NEWS)
        db.execSQL(UsersDAO.SQL_USER)
        db.execSQL(ClothesDAO.SQL_CLOTHES)
        db.execSQL(InvoiceDAO.SQL_INVOICE)
        db.execSQL(InvoiceDetailsDAO.SQL_INVOICE_DETAILS)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        db.execSQL("Drop table if exists " + RssDAO.TABLE_NAME)
        db.execSQL("Drop table if exists " + UsersDAO.TABLE_NAME)
        db.execSQL("Drop table if exists " + ClothesDAO.TABLE_NAME)
        db.execSQL("Drop table if exists " + InvoiceDAO.TABLE_NAME)
        db.execSQL("Drop table if exists " + InvoiceDetailsDAO.TABLE_NAME)
    }

    companion object {
        const val DATABASE_NAME = "dbDemoKotlin"
        const val VERSION = 1
    }
}