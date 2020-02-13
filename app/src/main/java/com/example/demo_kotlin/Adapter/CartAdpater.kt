package com.example.demo_kotlin.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.demo_kotlin.Adapter.CartAdpater.CartViewHolder
import com.example.demo_kotlin.Model.Cart
import com.example.demo_kotlin.R
import java.text.DecimalFormat
import java.text.NumberFormat

class CartAdpater(var cartList: MutableList<Cart>?, var context: Context?) :
    RecyclerView.Adapter<CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val inflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val formatter: NumberFormat = DecimalFormat("#,###")
        holder.tvname.text = cartList!![position].namecosmetics
        holder.tvprice.text = (formatter.format(cartList!![position].price).toString() + " VND")
        holder.tvquantity.text = (cartList!![position].amount.toString() + "")
    }

    override fun getItemCount(): Int {
        return cartList!!.size
    }

    inner class CartViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var tvname: TextView = itemView.findViewById(R.id.tvnamecosmetics)
        var tvprice: TextView = itemView.findViewById(R.id.tvprice)
        var tvquantity: TextView = itemView.findViewById(R.id.tvquantity)

    }
}