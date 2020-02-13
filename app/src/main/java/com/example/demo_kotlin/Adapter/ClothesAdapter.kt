package com.example.demo_kotlin.Adapter

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.view.View.OnLongClickListener
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.demo_kotlin.Activites.CartActivity
import com.example.demo_kotlin.Adapter.ClothesAdapter.ClothesViewHolder
import com.example.demo_kotlin.DAO.ClothesDAO
import com.example.demo_kotlin.Fragment.Fragment_Clothes
import com.example.demo_kotlin.Model.Cart
import com.example.demo_kotlin.Model.Clothes
import com.example.demo_kotlin.R
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class ClothesAdapter(var clothesList: MutableList<Clothes>?, var context: Context) :
    RecyclerView.Adapter<ClothesViewHolder>() {
    var clothesDAO: ClothesDAO? = null

    init {
        clothesDAO = ClothesDAO(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClothesViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_clothes, null)
        return ClothesViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClothesViewHolder, position: Int) {
        val img = intArrayOf(
            R.drawable.picture1,
            R.drawable.picture2,
            R.drawable.picture3,
            R.drawable.picture4
        )
        val ran = Random()
        val formatter: NumberFormat = DecimalFormat("#,###")
        val images = img[ran.nextInt(img.size)]
        holder.img.setBackgroundResource(images)
        holder.name.text = clothesList!!.get(position).nameclothes
        holder.price.text = formatter.format(clothesList!!.get(position).price) + " VND"
        holder.cardView.setOnLongClickListener(object : OnLongClickListener {
            override fun onLongClick(v: View): Boolean {
                val builder =
                    AlertDialog.Builder(context)
                val change =
                    arrayOf("Edit", "Delete", "Cancel")
                builder.setItems(change, object : DialogInterface.OnClickListener {
                    override fun onClick(
                        dialog: DialogInterface,
                        which: Int
                    ) {
                        when (which) {
                            0 -> {
                                val dialog1 = Dialog(
                                    (context)!!,
                                    android.R.style.Theme_Translucent_NoTitleBar
                                )
                                dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE)
                                dialog1.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                dialog1.setContentView(R.layout.dialog_addcomestic)
                                dialog1.window!!.attributes.windowAnimations =
                                    R.style.DialogAnimation_2
                                val edtid_clothes: EditText
                                val edtnameclothes: EditText
                                val edtprice: EditText
                                val edtamount: EditText
                                val add: Button
                                val back: RelativeLayout
                                back = dialog1.findViewById(R.id.back)
                                back.setOnClickListener(View.OnClickListener { dialog1.dismiss() })
                                edtid_clothes = dialog1.findViewById(R.id.edtidcosmetic)
                                edtnameclothes =
                                    dialog1.findViewById(R.id.edtnamecosmetics)
                                edtprice = dialog1.findViewById(R.id.edtprice)
                                edtamount = dialog1.findViewById(R.id.edtamount)
                                add = dialog1.findViewById(R.id.addcosmetics)
                                edtid_clothes.setText(clothesList!![position].idclothes)
                                edtnameclothes.setText(clothesList!![position].nameclothes)
                                edtprice.setText(clothesList!![position].price.toString())
                                edtamount.setText(clothesList!![position].amount.toString())
                                add.setOnClickListener(object : View.OnClickListener {
                                    override fun onClick(v: View) {
                                        if ((edtid_clothes.text.toString().trim { it <= ' ' } == "") || (edtprice.text.toString().trim { it <= ' ' } == "") || (edtnameclothes.text.toString().trim { it <= ' ' } == "") || (edtamount.text.toString().trim { it <= ' ' } == "")) {
                                            Toast.makeText(
                                                context,
                                                "Do not empty!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            val clothes = Clothes()
                                            clothes.idclothes = edtid_clothes.text.toString()
                                                .trim { it <= ' ' }
                                            clothes.nameclothes =
                                                edtnameclothes.text.toString()
                                                    .trim { it <= ' ' }
                                            clothes.price = edtprice.text.toString().toDouble()
                                            clothes.amount = edtamount.text.toString().toInt()
                                            if (clothesDAO!!.updateClothes(clothes) > 0) {
                                                Toast.makeText(
                                                    context,
                                                    R.string.successfully,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                clothesList!![position] = clothes
                                                notifyItemRangeChanged(position, clothesList!!.size)
                                                notifyItemChanged(position)
                                                dialog1.dismiss()
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    R.string.error,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }
                                })
                                dialog1.show()
                            }
                            1 -> {
                                val builder =
                                    AlertDialog.Builder(context)
                                builder.setTitle("Warning")
                                builder.setMessage("Are you sure you delete?")
                                builder.setCancelable(false)
                                builder.setPositiveButton(
                                    "Cancel",
                                    object : DialogInterface.OnClickListener {
                                        override fun onClick(
                                            dialogInterface: DialogInterface,
                                            i: Int
                                        ) {
                                            dialogInterface.dismiss()
                                        }
                                    })
                                builder.setNegativeButton(
                                    "Yes",
                                    object : DialogInterface.OnClickListener {
                                        override fun onClick(
                                            dialogInterface: DialogInterface,
                                            i: Int
                                        ) {
                                            if (clothesDAO!!.deleteClothesByID(
                                                    (clothesList!![position].idclothes)!!
                                                ) > 0
                                            ) {
                                                clothesList!!.removeAt(position)
                                                notifyItemRangeChanged(position, clothesList!!.size)
                                                notifyItemRemoved(position)
                                                notifyItemChanged(position)
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    R.string.error,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    })
                                val alertDialog = builder.create()
                                alertDialog.show()
                            }
                            2 -> dialog.dismiss()
                            else -> throw IllegalStateException("Unexpected value: $which")
                        }
                    }
                })
                val dialog = builder.create()
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                val wmlp = dialog.window!!.attributes
                wmlp.gravity = Gravity.BOTTOM
                wmlp.flags = wmlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
                dialog.window!!.attributes = wmlp
                dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation_2
                dialog.show()
                return false
            }
        })
        holder.cardView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                val dialog2 = Dialog(
                    (context)!!,
                    android.R.style.Theme_Translucent_NoTitleBar
                )
                dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog2.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog2.setContentView(R.layout.dialog_addcart)
                dialog2.window!!.attributes.windowAnimations = R.style.DialogAnimation_2
                val idclothes: TextView
                val nameclothes: TextView
                val priceclothes: TextView
                val amountclothes: TextView
                val add: Button
                idclothes = dialog2.findViewById(R.id.tvidcosmetic)
                nameclothes = dialog2.findViewById(R.id.tvnamecosmetics)
                priceclothes = dialog2.findViewById(R.id.tvprice)
                amountclothes = dialog2.findViewById(R.id.tvamout)
                val back: RelativeLayout
                back = dialog2.findViewById(R.id.back)
                back.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View) {
                        dialog2.dismiss()
                    }
                })
                val formatter: NumberFormat = DecimalFormat("#,###")
                idclothes.text = clothesList!!.get(position).idclothes
                nameclothes.text = clothesList!!.get(position).nameclothes
                priceclothes.text = (formatter.format(clothesList!!.get(position).price) + " VND")
                amountclothes.text = (clothesList!![position].amount.toString())
                add = dialog2.findViewById(R.id.addcart)
                back.setOnClickListener {
                    dialog2.dismiss()
                }
                add.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View) {
                        if ((amountclothes.text.toString().trim { it <= ' ' } == "")) {
                            Toast.makeText(
                                context,
                                "Enter Amount",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            if (clothesList!![position].amount == 0) {
                                Toast.makeText(
                                    context,
                                    "Sorry! This book is out",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                if (amountclothes.text.toString().trim { it <= ' ' }.toInt() == 0) {
                                    Toast.makeText(
                                        context,
                                        "Value can't be less than 1",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    if (amountclothes.text.toString().trim { it <= ' ' }.toInt() > clothesList!![position].amount
                                    ) {
                                        Toast.makeText(
                                            context,
                                            "The number is only " + clothesList!![position].amount + " Please enter again!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        val aaaa = CartActivity()
                                        val cart = Cart()
                                        cart.idcosmetics = clothesList!![position].idclothes
                                        cart.namecosmetics = clothesList!![position].nameclothes
                                        cart.amount =
                                            amountclothes.text.toString().trim { it <= ' ' }
                                                .toInt()
                                        cart.price = clothesList!![position].price
                                        aaaa.additem(cart)
                                        dialog2.dismiss()
                                    }
                                }
                            }
                        }
                    }
                })
                dialog2.show()
            }
        })
    }

    override fun getItemCount(): Int {
        return clothesList!!.size
    }

    inner class ClothesViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val name: TextView
        val price: TextView
        val img: ImageView
        val cardView: CardView

        init {
            cardView = itemView.findViewById(R.id.cardViewclothes)
            img = itemView.findViewById(R.id.imgclothes)
            name = itemView.findViewById(R.id.tvnameclothes)
            price = itemView.findViewById(R.id.tvprice)
        }
    }

    fun refresh(ls: MutableList<Clothes>?) {
        val a = Fragment_Clothes()
        a.refresh(context, ls)
    }

}