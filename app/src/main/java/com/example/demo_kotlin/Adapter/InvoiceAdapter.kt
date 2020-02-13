package com.example.demo_kotlin.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.demo_kotlin.Activites.InvoiceDetailsActivity
import com.example.demo_kotlin.Adapter.InvoiceAdapter.InvoiceViewHolder
import com.example.demo_kotlin.Fragment.Fragment_Invoice
import com.example.demo_kotlin.Model.Invoice
import com.example.demo_kotlin.R
import java.text.SimpleDateFormat

class InvoiceAdapter(var invoiceList: MutableList<Invoice>?,var context: Context) :
    RecyclerView.Adapter<InvoiceViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceViewHolder {
        val inflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.item_invoice, parent, false)
        return InvoiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: InvoiceViewHolder, position: Int) {
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        holder.tvid?.text = (invoiceList!![position].id_invoice)
        holder.tvdate?.text = simpleDateFormat.format(invoiceList!![position].date)
        holder.cardView?.setOnClickListener {
            val intent =
                Intent(context, InvoiceDetailsActivity::class.java)
            intent.putExtra("id", invoiceList!![position].id_invoice)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return invoiceList!!.size
    }

    inner class InvoiceViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var cardView: CardView? = null
        var tvid: TextView? = null
        var tvdate: TextView? = null

        init {
            cardView = itemView.findViewById(R.id.iteminvoice)
            tvid = itemView.findViewById(R.id.tvidinvoice)
            tvdate = itemView.findViewById(R.id.tvdate)
        }

    }

    fun refresh(ls: MutableList<Invoice>) {
        val a = Fragment_Invoice()
        a.refresh(context, ls)
    }

}