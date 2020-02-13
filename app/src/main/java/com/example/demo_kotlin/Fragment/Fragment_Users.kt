package com.example.demo_kotlin.Fragment


import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.example.demo_kotlin.Activites.LoginActivity
import com.example.demo_kotlin.Adapter.UsersAdapter
import com.example.demo_kotlin.DAO.UsersDAO
import com.example.demo_kotlin.Helper.SwipeController
import com.example.demo_kotlin.Helper.SwipeControllerActions
import com.example.demo_kotlin.Model.User
import com.example.demo_kotlin.R

class Fragment_Users : Fragment() {
    private var studentList: MutableList<User>? = null
    private var controller: SwipeController? = null
    private var progressBar: ProgressBar? = null
    var managerUsers: UsersDAO? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view: View?  = inflater.inflate(R.layout.fragment_users, container, false)
        setHasOptionsMenu(true)
        initView(view)
        return view
    }

    private fun initView(view:View?) {
        managerUsers = UsersDAO(activity)
        progressBar = view!!.findViewById(R.id.progress_bar)
        recyclerView = view!!.findViewById(R.id.lvcstudent)
        studentList = managerUsers!!.allUser
        adapter = activity?.let { UsersAdapter(it, studentList) }
        recyclerView?.setHasFixedSize(true)
        recyclerView?.setNestedScrollingEnabled(false)
        val layoutManager = LinearLayoutManager(activity)
        recyclerView?.setLayoutManager(layoutManager)
        recyclerView?.setAdapter(adapter)
        controller= SwipeController(object : SwipeControllerActions(){
            override fun onLeftClicked(position: Int) {
                super.onLeftClicked(position)
                Toast.makeText(
                    activity,
                    "You cannot edit information other users!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onRightClicked(position: Int) {
                super.onRightClicked(position)
                val pref = activity!!.getSharedPreferences(
                    "USER_FILE",
                    Context.MODE_PRIVATE
                )
                val strUserName = pref.getString("USERNAME", "")
                if (studentList!![position].mUser?.let { managerUsers!!.deleteUserByID(it) }!! > 0) {
                    if (studentList!![position].mUser.equals(strUserName)) {
                        logout()
                        val intent2 = Intent(activity, LoginActivity::class.java)
                        startActivity(intent2)
                        activity!!.finish()
                    }
                    adapter?.studentList?.removeAt(position)
                    adapter?.notifyItemRemoved(position)
                    adapter?.getItemCount()?.let {
                        adapter?.notifyItemRangeChanged(
                            position,
                            it
                        )
                    }
                } else {
                    Toast.makeText(activity, R.string.error, Toast.LENGTH_SHORT).show()
                }
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
        inflater.inflate(R.menu.mnstudent, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.addstudent -> {
                dialogstudent()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logout() {
        val pref =
            activity!!.getSharedPreferences("USER_FILE", Context.MODE_PRIVATE)
        val edit = pref.edit()
        edit.clear()
        edit.commit()
    }

    private fun dialogstudent() {
        val dialog =
            Dialog(activity!!, android.R.style.Theme_Translucent_NoTitleBar)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_addusers)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation_2
        val btncancel: RelativeLayout
        btncancel = dialog.findViewById(R.id.back)
        btncancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }


    fun refresh(
        context: Context?,
        ls: MutableList<User>?
    ) {
        if (context != null) {
            studentList=ls
            adapter = UsersAdapter(context, studentList)
            recyclerView!!.adapter = adapter
            adapter?.notifyDataSetChanged()
        }
    }

    companion object {
        var recyclerView: RecyclerView? = null
        var adapter: UsersAdapter? = null
        fun newInstance(): Fragment_Users {
            val args = Bundle()
            val fragment_student = Fragment_Users()
            fragment_student.setArguments(args)
            return fragment_student
        }
    }
}