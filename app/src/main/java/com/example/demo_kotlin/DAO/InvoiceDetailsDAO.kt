package com.example.demo_kotlin.DAO

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.demo_kotlin.Database.DatabaseHelper
import com.example.demo_kotlin.Model.Cart
import java.text.SimpleDateFormat
import java.util.*

class InvoiceDetailsDAO(context: Context?) {
    private val db: SQLiteDatabase
    private val dbHelper: DatabaseHelper
    var sdf = SimpleDateFormat("yyyy-MM-dd")
    //insert
    fun insertInvoiceDetails(hd: Cart): Int {
        val values = ContentValues()
        values.put("id_invoice", hd.idinvoice)
        values.put("id_cosmetics", hd.idcosmetics)
        values.put("name_cosmetics", hd.namecosmetics)
        values.put("price", hd.price)
        values.put("amount", hd.amount)
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
    fun getAllInvoiceDetailsByID1(maHoaDon: String): MutableList<Cart>? { //WHERE clause
        val ls: MutableList<Cart> ?= null
        val sql =
            "SELECT * FROM InvoiceDetails  where InvoiceDetails.id_invoice='$maHoaDon'"
        val c = db.rawQuery(sql, null)
        Log.d("getBillDetailsByID", "===>" + c.count)
        c.moveToFirst()
        try {
            while (c.isAfterLast == false) {
                val s = Cart()
                s.idcart=(c.getInt(0))
                s.idcosmetics=(c.getString(1))
                s.namecosmetics=(c.getString(3))
                s.price=(c.getDouble(4))
                s.amount=(c.getInt(5))
                ls?.add(s)
                c.moveToNext()
            }
            c.close()
        } catch (e: Exception) {
            Log.d(TAG, e.toString())
        }
        return ls
    }

    //delete
    fun deleteInvoiceDetailsByID(maHDCT: Int): Int {
        val result = db.delete(
            TABLE_NAME,
            "id_invoice_dt=?",
            arrayOf(maHDCT.toString())
        )
        return if (result == 0) -1 else 1
    }

    //check
    fun checkInvoice(maHoaDon: String): Boolean { //SELECT
        val columns = arrayOf("id_invoice")
        //WHERE clause
        val selection = "id_invoice=?"
        //WHERE clause arguments
        val selectionArgs = arrayOf(maHoaDon)
        var c: Cursor? = null
        return try {
            c = db.query(
                TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
            )
            c.moveToFirst()
            val i = c.count
            c.close()
            if (i <= 0) {
                false
            } else true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    } //    public List<Cart> getAllInvoiceDetails() {

    //        List<Cart> dsHoaDonChiTiet = new ArrayList<>();
//        String sSQL = "SELECT id_invoice_dt, Invoice.id_invoice,Invoice.date, " +
//                "Book.id_book, Book.id_category, Book.name_book, Book.author, Book.nxb, Book.cover_price, " +
//                "Book.amount,InvoiceDetails.sell_number FROM InvoiceDetails INNER JOIN Invoice " +
//                "on InvoiceDetails.id_invoice = Invoice.id_invoice INNER JOIN Book on Book.id_book = InvoiceDetails.id_book";
//        Cursor c = db.rawQuery(sSQL,    null);
//        c.moveToFirst();
//        try {
//            while (c.isAfterLast()==false){
//                Cart ee = new Cart();
//                ee.setIdinvoicedatails(c.getInt(0));
//                ee.setInvoice(new Invoice(c.getString(1),sdf.parse(c.getString(2))));
//                ee.setBook(new Books(c.getString(3),c.getString(4),c.getString(5),c.getString(6),c.getString(7),c.getInt(8),c.getInt(9)));
//                ee.setAmount(c.getInt(10));
//                dsHoaDonChiTiet.add(ee);
//                Log.d("//=====",ee.toString());
//                c.moveToNext();
//            }
//            c.close();
//        } catch (Exception e) {
//            Log.d(TAG, e.toString());
//        }
//        return dsHoaDonChiTiet;
//    }
    companion object {
        const val TABLE_NAME = "InvoiceDetails"
        const val SQL_INVOICE_DETAILS =
            "CREATE TABLE InvoiceDetails(id_invoice_dt INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "id_cosmetics text NOT NULL, id_invoice text NOT NULL,name_cosmetics text,price double, amount INTEGER);"
        const val TAG = "HoaDonChiTiet"
    }

    init {
        dbHelper = DatabaseHelper(context)
        db = dbHelper.getWritableDatabase()
    }
}