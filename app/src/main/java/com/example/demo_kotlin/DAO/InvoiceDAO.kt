package com.example.demo_kotlin.DAO

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.demo_kotlin.Database.DatabaseHelper
import com.example.demo_kotlin.Model.Invoice
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class InvoiceDAO(context: Context?) {
    private val db: SQLiteDatabase
    private val dbHelper: DatabaseHelper = DatabaseHelper(context)
    var sdf = SimpleDateFormat("yyyy-MM-dd")
    //insert
    fun inserInvoice(hd: Invoice): Int {
        val values = ContentValues()
        values.put("id_invoice", hd.id_invoice)
        values.put("date", sdf.format(hd.date))
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
    @get:Throws(ParseException::class)
    val allInvoice: MutableList<Invoice>
        get() {
            val dsHoaDon: MutableList<Invoice> = mutableListOf()
            val c =
                db.query(TABLE_NAME, null, null, null, null, null, null)
            c.moveToFirst()
            while (!c.isAfterLast) {
                val ee = Invoice()
                ee.id_invoice=(c.getString(0))
                ee.date=(sdf.parse(c.getString(1)))
                dsHoaDon.add(ee)
                Log.d("//=====", ee.toString())
                c.moveToNext()
            }
            c.close()
            return dsHoaDon
        }

    //delete
    fun deleteInvoiceByID(mahoadon: String): Int {
        val result = db.delete(
            TABLE_NAME,
            "id_invoice=?",
            arrayOf(mahoadon)
        )
        return if (result == 0) -1 else 1
    }

    companion object {
        const val TABLE_NAME = "Invoice"
        const val SQL_INVOICE =
            "CREATE TABLE Invoice (id_invoice text primary key, date date);"
        const val TAG = "HoaDonDAO"
    }

    init {
        db = dbHelper.getWritableDatabase()
    }
}