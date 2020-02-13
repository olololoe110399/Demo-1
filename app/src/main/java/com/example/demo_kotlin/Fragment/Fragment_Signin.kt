package com.example.demo_kotlin.Fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.demo_kotlin.R
import com.example.demo_kotlin.DAO.UsersDAO
import com.example.demo_kotlin.Activites.MainActivity
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import org.json.JSONException

class Fragment_Signin : Fragment() {
    var callbackManager: CallbackManager? = null

    var loginButton: LoginButton? = null
    var iduser: String? = null
    var name: String? = null
    var email: String? = null
    var btnsubmit: Button? = null
    var edtuser: EditText? = null
    var edtpass: EditText? = null
    var checkBox: CheckBox? = null
    var usersDAO: UsersDAO? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_signin, container, false)
        initView(view)
        if (isNetworkAvailable) { // If using in a fragment
            loginButton!!.fragment = this
            // Callback registration
            set_Loginbtn()
        } else {
            Toast.makeText(activity, "Disconnected. Check the network", Toast.LENGTH_SHORT)
                .show()
        }
        btnsubmit!!.setOnClickListener {
            if (edtpass!!.text.toString() == "" || edtuser!!.text.toString() == "") {
                Toast.makeText(activity, "Do not empty!", Toast.LENGTH_SHORT).show()
            } else {
                if (!isValidEmail(edtuser!!.text.toString())) {
                    Toast.makeText(
                        activity,
                        R.string.error_valid_email,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    if (usersDAO!!.checkLogin(
                            edtuser!!.text.toString(),
                            edtpass!!.text.toString()
                        ) > 0
                    ) {
                        Toast.makeText(activity, R.string.successfully, Toast.LENGTH_SHORT)
                            .show()
                        rememberUser(
                            edtuser!!.text.toString(),
                            edtpass!!.text.toString(),
                            checkBox!!.isChecked
                        )
                        val intent = Intent(activity, MainActivity::class.java)
                        startActivity(intent)
                        activity!!.finish()
                    } else {
                        edtuser!!.setText(null)
                        edtpass!!.setText(null)
                        Toast.makeText(
                            activity,
                            R.string.error_valid_email_password,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        return view
    }

    private fun set_Loginbtn() {
        loginButton!!.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {
                dialogremembermefb()
            }

            override fun onCancel() { // App code
            }

            override fun onError(exception: FacebookException) { // App code
            }
        })
    }

    private fun result(a: Boolean) {
        val graphRequest = GraphRequest.newMeRequest(
            AccessToken.getCurrentAccessToken()
        ) { `object`, response ->
            Log.d("JSON", response.jsonObject.toString())
            try {
                iduser = `object`.getString("id")
                name = `object`.getString("name")
                email = `object`.getString("email")
                rememberUser(email, "", a)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        val parameters = Bundle()
        parameters.putString("fields", "id,name,email")
        graphRequest.parameters = parameters
        graphRequest.executeAsync()
    }

    private fun dialogremembermefb() {
        val builder =
            AlertDialog.Builder(activity)
        builder.setTitle("Hello")
        builder.setMessage("I have read all information?")
        builder.setCancelable(false)
        builder.setPositiveButton("No") { dialogInterface, i ->
            dialogInterface.dismiss()
            result(false)
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity!!.finish()
        }
        builder.setNegativeButton("Yes") { dialogInterface, i ->
            result(true)
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity!!.finish()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun initView(view:View) {
        loginButton = view.findViewById(R.id.login_button)
        callbackManager = CallbackManager.Factory.create()
        edtuser = view.findViewById(R.id.edtuser)
        edtpass = view.findViewById(R.id.edtpassword)
        btnsubmit = view.findViewById(R.id.btnsubmit)
        checkBox = view.findViewById(R.id.checkbox)
        usersDAO = UsersDAO(activity)
    }

    fun rememberUser(u: String?, p: String?, status: Boolean) {
        val pref =
            activity!!.getSharedPreferences("USER_FILE", Context.MODE_PRIVATE)
        val edit = pref.edit()
        if (!status) {
            edit.clear()
            edit.putString("USERNAME", u)
        } else {
            edit.putString("USERNAME", u)
            edit.putString("PASSWORD", p)
            edit.putBoolean("REMEMBER", status)
        }
        edit.commit()
    }

    override fun onStart() {
        LoginManager.getInstance().logOut()
        super.onStart()
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
    private val isNetworkAvailable: Boolean
         get() {
            val connectivityManager =
                activity!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }

    companion object {
        fun isValidEmail(target: CharSequence?): Boolean {
            return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }
}