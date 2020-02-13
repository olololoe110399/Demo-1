package com.example.demo_kotlin.Adapter


import android.app.Dialog
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.demo_kotlin.Adapter.UsersAdapter.StudentViewHolder
import com.example.demo_kotlin.Fragment.Fragment_Users
import com.example.demo_kotlin.Model.User
import com.example.demo_kotlin.R

class UsersAdapter : RecyclerView.Adapter<StudentViewHolder> {
    var studentList: MutableList<User>? = null
    var context: Context? = null

    constructor(context: Context, studentList: MutableList<User>?) {
        this.context = context
        this.studentList = studentList
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val inflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.item_user, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val hinh: ByteArray? = studentList!![position].mImage
        val bitmap = hinh?.size?.let { BitmapFactory.decodeByteArray(hinh, 0, it) }
        holder.imgstudent.setImageBitmap(bitmap)
        holder.tvphone.setText(studentList!![position].mPhone)
        holder.name.setText(studentList!![position].mName)
        holder.cardView.setOnClickListener {
            val dialog = Dialog(context!!)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window
                ?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.dialog_details_users)
            dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation_2
            val phone: TextView
            val name: TextView
            val user: TextView
            val btncancel: Button
            val imgavatar: ImageView
            user = dialog.findViewById(R.id.user)
            phone = dialog.findViewById(R.id.textPhone)
            name = dialog.findViewById(R.id.textfullname)
            btncancel = dialog.findViewById(R.id.appCompatButtonReturn)
            imgavatar = dialog.findViewById(R.id.imgavatar)
            user.setText(studentList!![position].mUser)
            phone.setText(studentList!![position].mPhone)
            name.setText(studentList!![position].mName)
            if (studentList!![position].mImage != null) {
                val hinh: ByteArray? = studentList!![position].mImage
                val bitmap = hinh?.size?.let { it1 -> BitmapFactory.decodeByteArray(hinh, 0, it1) }
                imgavatar.setImageBitmap(bitmap)
            } else {
                imgavatar.setImageResource(R.drawable.avatar)
            }
            btncancel.setOnClickListener { dialog.dismiss() }
            dialog.show()
        }
    }

    override fun getItemCount(): Int {
        return studentList!!.size
    }

    inner class StudentViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var tvphone: TextView
        var name: TextView
        var cardView: CardView
        var imgstudent: ImageView

        init {
            imgstudent = itemView.findViewById(R.id.imgstudent)
            cardView = itemView.findViewById(R.id.itemstudent)
            tvphone = itemView.findViewById(R.id.tvphone)
            name = itemView.findViewById(R.id.tvname)
        }
    }

    fun refresh(ls: MutableList<User>?) {
        val a = Fragment_Users()
        a.refresh(context, ls)
    }


}