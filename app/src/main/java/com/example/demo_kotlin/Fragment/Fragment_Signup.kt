package com.example.demo_kotlin.Fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.demo_kotlin.Activites.LoginActivity
import com.example.demo_kotlin.Model.User
import com.example.demo_kotlin.R
import com.example.demo_kotlin.DAO.UsersDAO
import java.io.ByteArrayOutputStream


class Fragment_Signup : Fragment() {
    var btnreister: Button? = null
    var edtuser: EditText? = null
    var edtpass: EditText? = null
    var edtrepass: EditText? = null
    var tvsingn: TextView? = null
    var usersDAO: UsersDAO? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_signup, container, false)
        initView(view)
        btnreister!!.setOnClickListener { register() }
        tvsingn!!.setOnClickListener {
            (activity as LoginActivity?)!!.backsignin()
            loadFragment(Fragment_Signin())
        }
        return view
    }

     fun initView(view: View) {

        btnreister = view.findViewById(R.id.btnregisger)
        edtuser = view.findViewById(R.id.edtuser)
        edtpass = view.findViewById(R.id.edtpassword)
        edtrepass = view.findViewById(R.id.edtrepassword)
        usersDAO = UsersDAO(activity)
        tvsingn = view.findViewById(R.id.tvsignin)
    }

//    fun initVieẉ(var view1: View){
//
//    }

    private fun loadFragment(fragment: Fragment) { // load fragment
        val transaction =
            activity!!.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_container2, fragment)
        transaction.commit()
    }

    fun register() {
        if (edtuser!!.text.toString() == "" || edtpass!!.text.toString() == "" || edtrepass!!.text.toString() == "") {
            Toast.makeText(activity, "Do not empty!", Toast.LENGTH_SHORT).show()
        } else {
            if (!isValidEmail(edtuser!!.text.toString())) {
                Toast.makeText(activity, R.string.error_valid_email, Toast.LENGTH_SHORT).show()
            } else {
                if (edtpass!!.text.toString() != edtrepass!!.text.toString()) {
                    Toast.makeText(activity, R.string.error_password_match, Toast.LENGTH_SHORT)
                        .show()
                } else {
                    val bitmap= BitmapFactory.decodeResource(resources,R.drawable.avatar)
                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    val bitMapData = stream.toByteArray()
                    val user = User()
                    user.mUser=edtuser!!.text.toString()
                    user.mPassword=edtpass!!.text.toString()
                    user.mImage=bitMapData
                    if (usersDAO!!.inserUser(user) > 0) {
                        edtuser!!.setText(null)
                        edtpass!!.setText(null)
                        edtrepass!!.setText(null)
                        Toast.makeText(activity, R.string.success_message, Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        edtuser!!.setText(null)
                        Toast.makeText(
                            activity,
                            R.string.error_email_exists,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    companion object {
        fun isValidEmail(target: CharSequence?): Boolean {
            return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }
}