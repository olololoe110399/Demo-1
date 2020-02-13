package com.example.demo_kotlin.Activites


import android.app.AlertDialog
import android.graphics.Canvas
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.example.demo_kotlin.Adapter.CartAdpater
import com.example.demo_kotlin.DAO.InvoiceDetailsDAO
import com.example.demo_kotlin.Helper.SwipeController
import com.example.demo_kotlin.Helper.SwipeControllerActions
import com.example.demo_kotlin.Model.Cart
import com.example.demo_kotlin.R
import java.util.*

class InvoiceDetailsActivity : AppCompatActivity() {
    private var toolbar: Toolbar? = null
    private var managerInvoiceDetails: InvoiceDetailsDAO? = null
    private var billdetailsList: MutableList<Cart>? = null
    private var recyclerView: RecyclerView? = null
    private var adapter: CartAdpater? = null
    private var controller: SwipeController? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice_details)
        initView()
    }

    private fun initView() {
        val intent = intent
        val idinvoice = intent.getStringExtra("id")
        toolbar = findViewById(R.id.toolbardetails)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setTitle("INVOICE DETAILS")
        managerInvoiceDetails = InvoiceDetailsDAO(this)
        billdetailsList = ArrayList<Cart>()
        billdetailsList = managerInvoiceDetails?.getAllInvoiceDetailsByID1(idinvoice)
        adapter = CartAdpater(billdetailsList, this)
        recyclerView = findViewById(R.id.lvdetails)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.setLayoutManager(LinearLayoutManager(this))
        recyclerView?.setAdapter(adapter)
        recyclerView?.setNestedScrollingEnabled(false)
        controller = SwipeController(object : SwipeControllerActions() {
            override fun onLeftClicked(position: Int) {
                super.onLeftClicked(position)
                Toast.makeText(this@InvoiceDetailsActivity, "not", Toast.LENGTH_SHORT).show()

            }

            override fun onRightClicked(position: Int) {
                super.onRightClicked(position)
                val builder =
                    AlertDialog.Builder(this@InvoiceDetailsActivity)
                builder.setTitle("Warning")
                builder.setMessage("Are you sure you delete?")
                builder.setCancelable(false)
                builder.setPositiveButton(
                    "Cancel"
                ) { dialogInterface, i -> dialogInterface.dismiss() }
                builder.setNegativeButton(
                    "Yes"
                ) { dialogInterface, i ->
                    if (managerInvoiceDetails?.deleteInvoiceDetailsByID(
                            billdetailsList!![position].idcart
                        )!! > 0
                    ) {
                        adapter?.cartList?.removeAt(position)
                        adapter?.notifyItemRemoved(position)
                        adapter?.getItemCount()?.let {
                            adapter?.notifyItemRangeChanged(
                                position,
                                it
                            )
                        }
                    } else {
                        Toast.makeText(
                            this@InvoiceDetailsActivity,
                            R.string.error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                val alertDialog = builder.create()
                alertDialog.show()
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
        finish()
        return true
    }
}