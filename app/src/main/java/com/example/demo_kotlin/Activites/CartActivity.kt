package com.example.demo_kotlin.Activites


import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.example.demo_kotlin.Adapter.CartAdpater
import com.example.demo_kotlin.Adapter.ClothesAdapter
import com.example.demo_kotlin.Adapter.InvoiceAdapter
import com.example.demo_kotlin.Adapter.UsersAdapter
import com.example.demo_kotlin.DAO.ClothesDAO
import com.example.demo_kotlin.DAO.InvoiceDAO
import com.example.demo_kotlin.DAO.InvoiceDetailsDAO
import com.example.demo_kotlin.DAO.UsersDAO
import com.example.demo_kotlin.Helper.SwipeController
import com.example.demo_kotlin.Helper.SwipeControllerActions
import com.example.demo_kotlin.Model.Cart
import com.example.demo_kotlin.Model.Invoice
import com.example.demo_kotlin.R
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class CartActivity : AppCompatActivity() {

    var sumtotal = 0.0
    private var toolbar: Toolbar? = null
    private var recyclerView: RecyclerView? = null
    private var adapter: CartAdpater? = null
    private var controller: SwipeController? = null
    private var simpleDateFormat: SimpleDateFormat? = null
    private lateinit var managerInvoice: InvoiceDAO
    private var edtidinvoice: EditText? = null
    private var edtdate: EditText? = null
    private var managerClothes: ClothesDAO? = null
    private var managerUsers: UsersDAO? = null
    private var managerInvoiceDetails: InvoiceDetailsDAO? = null
    var tvtotal: TextView? = null
    var formatter: NumberFormat? = null
    private var invoice: Invoice? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        initView()
        edtdate!!.setOnClickListener {
            val calendar = Calendar.getInstance()
            val idate = calendar[Calendar.DATE]
            val imonth = calendar[Calendar.MONTH]
            val iyear = calendar[Calendar.YEAR]
            val datePickerDialog =
                DatePickerDialog(
                    this@CartActivity,
                    OnDateSetListener { view, year, month, dayOfMonth ->
                        calendar[year, month] = dayOfMonth
                        edtdate!!.setText(simpleDateFormat!!.format(calendar.time))
                    },
                    iyear,
                    imonth,
                    idate
                )
            datePickerDialog.show()
        }
    }
    companion object{
        var cartList: MutableList<Cart>? = mutableListOf()
    }

    private fun initView() {

        edtdate = findViewById(R.id.edtdate)
        edtidinvoice = findViewById(R.id.edtidinvoice)
        managerInvoiceDetails = InvoiceDetailsDAO(this)
        managerUsers = UsersDAO(this)
        managerClothes = ClothesDAO(this)
        managerInvoice = InvoiceDAO(this)
        simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        invoice = Invoice()
        formatter = DecimalFormat("#,###")
        recyclerView = findViewById(R.id.lvCard)
        toolbar = findViewById(R.id.toolbarcart)
        tvtotal = findViewById(R.id.tvprice)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        total()
        recyclerView?.hasFixedSize()
        recyclerView?.layoutManager = LinearLayoutManager(this)
        adapter = CartAdpater(getDATA(), this@CartActivity)
        recyclerView?.adapter = adapter
        recyclerView?.isNestedScrollingEnabled = false
        controller = SwipeController(object : SwipeControllerActions() {
            override fun onLeftClicked(position: Int) {
                super.onLeftClicked(position)
                Toast.makeText(this@CartActivity, "not", Toast.LENGTH_SHORT).show()
            }

            override fun onRightClicked(position: Int) {
                super.onRightClicked(position)
                retotal(position)
                adapter?.cartList?.removeAt(position)
                adapter?.notifyItemRemoved(position)
                adapter?.notifyItemRangeChanged(position, adapter!!.getItemCount())
                val mainActivity = MainActivity()
                cartList?.size?.let { mainActivity.setcount(it) }
            }
        })

        val itemTouchhelper = ItemTouchHelper(controller!!)
        itemTouchhelper.attachToRecyclerView(recyclerView)
        recyclerView?.addItemDecoration(object : ItemDecoration() {
            override fun onDraw(
                c: Canvas,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                controller?.onDraw(c)
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        sumtotal = 0.0
        finish()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.clear()
        menuInflater.inflate(R.menu.mncart, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mnnew -> {
                cartList?.clear()
                adapter?.notifyDataSetChanged()
                tvtotal!!.text = ("0 VND")
                sumtotal = 0.0
                val mainActivity = MainActivity()
                cartList?.size?.let { mainActivity.setcount(it) }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun total() {
        try {
            for (cart in cartList!!) {
                sumtotal += (cart.amount * cart.price)
            }
            if (sumtotal > 0) {
                tvtotal!!.text = (formatter!!.format(sumtotal) + " VND")
            } else {
                tvtotal!!.text = ("0 VND")
            }
        } catch (e: Exception) {
            Log.e("Error", e.toString())
        }
    }

    private fun retotal(position: Int) {
        try {
            sumtotal -= ((cartList?.get(position)?.amount)?.times((cartList?.get(position)?.price!!))!!)
            if (sumtotal > 0) {
                tvtotal!!.text = (formatter!!.format(sumtotal) + " VND")
            } else {
                tvtotal!!.text = ("0 VND")
            }
        } catch (e: Exception) {
            Log.e("Error", e.toString())
        }
    }

    fun additem(cart: Cart) {
        var pos = -1
        for (i in cartList?.indices!!) {

            val hd: Cart = cartList !![i]
            if (hd.idcosmetics.equals(cart.idcosmetics)) {
                Log.i("cart", "1")
                pos = i
                break
            }
        }
        if (pos >= 0) {
            Log.i("cart", "2")

            cartList!![pos] = cart
        } else {
            Log.i("cart", "3")

            cartList?.add(cart)
            Log.i("cart",cart.toString())
        }
        val mainActivity = MainActivity()
        cartList?.size?.let { mainActivity.setcount(it) }
    }

    fun count(): Int? {
        return cartList?.size
    }



    private fun Sumprice() {
        var newamount: Int
        try {
            for (cart in getDATA()!!) {
                invoice?.id_invoice = (edtidinvoice!!.text.toString().trim { it <= ' ' })
                cart.idinvoice = (edtidinvoice!!.text.toString().trim { it <= ' ' })
                managerInvoiceDetails?.insertInvoiceDetails(cart)
                val oldamount: Int = cart.amount
                newamount = oldamount - cart.amount
                cart.idcosmetics?.let { managerClothes?.updateAmounClothes(newamount, it) }
            }
            edtdate!!.text = null
            edtidinvoice!!.text = null
            cartList?.clear()
            adapter?.notifyDataSetChanged()
            recyclerView!!.adapter = adapter
            val mainActivity = MainActivity()
            cartList?.size?.let { mainActivity.setcount(it) }
            val a: InvoiceAdapter? = null
            a?.refresh(managerInvoice.allInvoice)
            arletdialogSum()
            val ab: UsersAdapter? = null
            ab?.refresh(managerUsers?.allUser)
            val ad: ClothesAdapter? = null
            ad?.refresh(managerClothes?.allClothes)
        } catch (e: Exception) {
            Log.e("Error", e.toString())
        }
    }

    private fun arletdialogSum() {
        val formatter: NumberFormat = DecimalFormat("#,###")
        AlertDialog.Builder(this)
            .setTitle("TOTAL BILL:")
            .setCancelable(false)
            .setMessage("Total amount of the bill is " + formatter.format(sumtotal) + " VND")
            .setPositiveButton("OK") { dialog, which ->
                sumtotal = 0.0
                tvtotal!!.text = ("0 VND")
                dialog.dismiss()
            }
            .show()
    }
    fun getDATA(): MutableList<Cart>? {
        return cartList
    }

    fun addinvoice(view: View) {
        try {
            if (edtidinvoice!!.text.toString().trim { it <= ' ' } == "") {
                Toast.makeText(this, "Enter ID Invoice", Toast.LENGTH_SHORT).show()
            } else {
                if (edtdate!!.text.toString() == "") {
                    Toast.makeText(this, "Enter Date", Toast.LENGTH_SHORT).show()
                } else {
                    invoice?.id_invoice = (edtidinvoice!!.text.toString().trim { it <= ' ' })
                    invoice?.date = (simpleDateFormat!!.parse(edtdate!!.text.toString()))
                    if (invoice?.let { managerInvoice?.inserInvoice(it) }!! > 0) {
                        if (cartList?.size!! > 0) {
                            Sumprice()
                        } else {
                            Toast.makeText(this, "Please add Book !", Toast.LENGTH_SHORT).show()
                        }
                        Toast.makeText(this, "Add Successful!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "ID Invoice Already Exists!", Toast.LENGTH_SHORT)
                            .show()
                        edtidinvoice!!.text = null
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("Error", e.toString())
        }
    }
}