package com.example.demo_kotlin.Fragment


import android.app.AlertDialog
import android.content.Context
import android.graphics.Canvas
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.example.demo_kotlin.Adapter.ClothesAdapter
import com.example.demo_kotlin.Adapter.InvoiceAdapter
import com.example.demo_kotlin.DAO.InvoiceDAO
import com.example.demo_kotlin.DAO.InvoiceDetailsDAO
import com.example.demo_kotlin.Helper.SwipeController
import com.example.demo_kotlin.Helper.SwipeControllerActions
import com.example.demo_kotlin.Model.Clothes
import com.example.demo_kotlin.Model.Invoice
import com.example.demo_kotlin.R
import java.text.ParseException

class Fragment_Invoice : Fragment() {
    var invoiceList: MutableList<Invoice> = mutableListOf()
    var controller: SwipeController? = null
    lateinit var managerInvoice: InvoiceDAO
    var managerInvoiceDetails: InvoiceDetailsDAO? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view: View? = inflater.inflate(R.layout.fragment_invoice, container, false)
        setHasOptionsMenu(true)
        initView(view)
        return view
    }

    inner class Loading : AsyncTask<Void?, Void?, Void?>() {


        override fun onPostExecute(result: Void?) {
            adapter = InvoiceAdapter(invoiceList, activity!!)
            recyclerView?.setHasFixedSize(true)
            recyclerView?.isNestedScrollingEnabled = false
            recyclerView?.layoutManager = LinearLayoutManager(activity)
            recyclerView?.adapter = adapter
            adapter!!.notifyDataSetChanged()
            super.onPostExecute(result)
        }
        override fun doInBackground(vararg params: Void?): Void? {
            invoiceList.addAll(managerInvoice.allInvoice)
            return null
        }
    }


    fun initView(view: View?) {
        managerInvoiceDetails = InvoiceDetailsDAO(activity)
        managerInvoice = InvoiceDAO(activity)
        recyclerView = view?.findViewById(R.id.lvinvoice)

        Loading().execute()



        controller = SwipeController(object : SwipeControllerActions() {
            override fun onLeftClicked(position: Int) {
                super.onLeftClicked(position)
                Toast.makeText(activity, "alo", Toast.LENGTH_SHORT).show()
            }

            override fun onRightClicked(position: Int) {
                super.onRightClicked(position)
                val builder =
                    AlertDialog.Builder(activity)
                builder.setTitle("Warning")
                builder.setMessage("Are you sure you delete?")
                builder.setCancelable(false)
                builder.setPositiveButton(
                    "Cancel"
                ) { dialogInterface, i -> dialogInterface.dismiss() }
                builder.setNegativeButton(
                    "Yes"
                ) { dialogInterface, i ->
                    if (invoiceList!![position].id_invoice?.let {
                            managerInvoiceDetails?.checkInvoice(
                                it
                            )
                        }!!) {
                        Toast.makeText(
                            activity,
                            "You must delete detailed invoices before deleting this invoice",
                            Toast.LENGTH_SHORT
                        ).show()
                        dialogInterface.dismiss()
                    } else {
                        if (invoiceList!![position].id_invoice?.let {
                                managerInvoice?.deleteInvoiceByID(
                                    it
                                )
                            }!! > 0) {
                            adapter?.invoiceList?.removeAt(position)
                            adapter?.notifyItemRemoved(position)
                        } else {
                            Toast.makeText(
                                activity,
                                R.string.error,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
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

    override fun onCreateOptionsMenu(
        menu: Menu,
        inflater: MenuInflater
    ) {
        menu.clear()
        inflater.inflate(R.menu.mninvoice, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    fun refresh(
        context: Context?,
        ls: MutableList<Invoice>
    ) {
        if (context != null) {
            invoiceList = ls
            adapter = InvoiceAdapter(invoiceList, context)
            recyclerView!!.adapter = adapter
            adapter?.notifyDataSetChanged() // notify adapter
        }
    }

    companion object {
        var recyclerView: RecyclerView? = null
        var adapter: InvoiceAdapter? = null
        fun newInstance(): Fragment_Invoice {
            val args = Bundle()
            val invoice = Fragment_Invoice()
            invoice.setArguments(args)
            return invoice
        }
    }
}