package com.example.demo_kotlin.Fragment

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.example.demo_kotlin.Adapter.ClothesAdapter
import com.example.demo_kotlin.DAO.ClothesDAO
import com.example.demo_kotlin.Model.Clothes
import com.example.demo_kotlin.R
import java.util.*

class Fragment_Clothes : Fragment() {

    var clothesList: MutableList<Clothes>? = null
    var swipeRefreshLayout: SwipeRefreshLayout? = null
    var myProgress: ProgressBar? = null
    var managerClothes: ClothesDAO? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view: View = inflater.inflate(R.layout.fragment_clothes, container, false)
        setHasOptionsMenu(true)
        initView(view)
        return view
    }

    override fun onCreateOptionsMenu(
        menu: Menu,
        inflater: MenuInflater
    ) {
        menu.clear()
        inflater.inflate(R.menu.mncosmetics, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mnaddcosmetics -> {
                dialogaddcosmetics()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun initView(view: View) {
        managerClothes = ClothesDAO(activity)
        myProgress = view.findViewById(R.id.progress_bar)
        swipeRefreshLayout = view.findViewById(R.id.swipe)
        swipeRefreshLayout?.setOnRefreshListener(OnRefreshListener { refreshRecyclerView() })
        recyclerView = view.findViewById(R.id.lvClothes)
        clothesList = mutableListOf()
        Loading().execute()
        recyclerView?.isNestedScrollingEnabled = false
    }

    private fun refreshRecyclerView() {
        Loading().execute()
    }

    private fun dialogaddcosmetics() {
        val dialog1 =
            Dialog(activity!!, android.R.style.Theme_Translucent_NoTitleBar)
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog1.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog1.setContentView(R.layout.dialog_addcomestic)
        dialog1.window!!.attributes.windowAnimations = R.style.DialogAnimation_2
        val edtid_clothes: EditText
        val edtnameclothes: EditText
        val edtprice: EditText
        val edtamount: EditText
        val add: Button
        val back: RelativeLayout
        back = dialog1.findViewById(R.id.back)
        back.setOnClickListener { dialog1.dismiss() }
        edtid_clothes = dialog1.findViewById(R.id.edtidcosmetic)
        edtnameclothes = dialog1.findViewById(R.id.edtnamecosmetics)
        edtprice = dialog1.findViewById(R.id.edtprice)
        edtamount = dialog1.findViewById(R.id.edtamount)
        add = dialog1.findViewById(R.id.addcosmetics)
        add.setOnClickListener {
            if (edtid_clothes.text.toString().trim { it <= ' ' } == "" || edtprice.text.toString().trim { it <= ' ' } == "" || edtnameclothes.text.toString().trim { it <= ' ' } == "" || edtamount.text.toString().trim { it <= ' ' } == "") {
                Toast.makeText(activity, "Do not empty!", Toast.LENGTH_SHORT).show()
            } else {
                val clothes = Clothes()
                clothes.idclothes = edtid_clothes.text.toString().trim { it <= ' ' }
                clothes.nameclothes = edtnameclothes.text.toString().trim { it <= ' ' }
                clothes.price = edtprice.text.toString().toDouble()
                clothes.amount = edtamount.text.toString().toInt()
                if (managerClothes!!.inserClothes(clothes) > 0) {
                    Toast.makeText(activity, R.string.successfully, Toast.LENGTH_SHORT)
                        .show()
                    load()
                    dialog1.dismiss()
                } else {
                    Toast.makeText(activity, R.string.error_id_exists, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        dialog1.show()
    }

    fun load() {
        Loading().execute()
    }

    inner class Loading :
        AsyncTask<Void?, Void?, MutableList<Clothes>>() {
        override fun onPreExecute() {
            myProgress!!.visibility = View.VISIBLE
            super.onPreExecute()
        }

        override fun onPostExecute(result: MutableList<Clothes>?) {
            adapter = ClothesAdapter(clothesList, activity!!)
            recyclerView!!.setHasFixedSize(true)
            recyclerView!!.layoutManager = GridLayoutManager(
                activity,
                2
            )
            recyclerView!!.adapter = adapter
            adapter!!.notifyItemRangeChanged(
                0,
                clothesList!!.size
            )
            adapter!!.notifyItemInserted(0)
            adapter!!.notifyItemChanged(0)
            adapter!!.notifyDataSetChanged()
            myProgress!!.visibility = View.GONE
            swipeRefreshLayout!!.isRefreshing = false // set swipe refreshing
            super.onPostExecute(clothesList)
        }


        override fun doInBackground(vararg params: Void?): MutableList<Clothes> {
            clothesList = managerClothes?.allClothes
            Log.i("data",clothesList.toString())
            return clothesList!!
        }
    }

    fun refresh(
        context: Context?,
        ls: MutableList<Clothes>?
    ) {
        if (context != null) {
            clothesList = ls
            adapter = ClothesAdapter(clothesList, context)
            recyclerView!!.adapter = adapter
            adapter!!.notifyDataSetChanged()
        }
    }

    companion object {
        var recyclerView: RecyclerView? = null
        var adapter: ClothesAdapter? = null
    }
}