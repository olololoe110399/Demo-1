package com.example.demo_kotlin.Fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.demo_kotlin.Activites.LoginActivity
import com.example.demo_kotlin.DAO.UsersDAO
import com.example.demo_kotlin.Activites.InfomationActivity
import com.example.demo_kotlin.Activites.MapActivity
import com.example.demo_kotlin.Model.User
import com.example.demo_kotlin.R
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.GraphRequest.GraphJSONObjectCallback
import com.facebook.GraphResponse
import com.facebook.login.LoginManager
import org.json.JSONException
import org.json.JSONObject

class Fragment_User() : Fragment() {
    var map: RelativeLayout? = null
    var tvloction: TextView? = null
    var tvName: TextView? = null
    var tvphone: TextView? = null
    var tvemail: TextView? = null
    var a: String? = null
    var fbname: String? = null
    var fbemail: String? = null
    var mHandler: Handler? = null
    var token: AccessToken? = null
    var usersDAO: UsersDAO? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_user, container, false)
        setHasOptionsMenu(true)
        map = view.findViewById(R.id.map)
        usersDAO = UsersDAO(activity)
        tvloction = view.findViewById(R.id.location)
        tvemail = view.findViewById(R.id.useremail)
        tvName = view.findViewById(R.id.username)
        tvphone = view.findViewById(R.id.userphone)
        a = tvloction?.getText().toString().trim { it <= ' ' }
        token = AccessToken.getCurrentAccessToken()
        if (token == null) {
            val pref = activity!!.getSharedPreferences(
                "USER_FILE",
                Context.MODE_PRIVATE
            )
            val nameFromIntent = pref.getString("USERNAME", "")
            if (usersDAO!!.checknull((nameFromIntent)!!) > 0) {
                tvemail?.setText(nameFromIntent)
            } else {
                val user2 =
                    usersDAO!!.getuserbyid2((nameFromIntent))
                tvemail?.setText(user2?.mUser)
                tvName?.setText(user2?.mName)
                tvloction?.setText(user2?.mAddress)
                tvphone?.setText(user2?.mPhone)
            }
        } else {
            if (isOnline(activity)) {
                result()
            } else {
                Toast.makeText(
                    activity,
                    R.string.please_connect_to_internet,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        mHandler = Handler(Looper.getMainLooper())
        map?.setOnClickListener(View.OnClickListener {
            if (isOnline(activity)) {
                val intent = Intent(activity, MapActivity::class.java)
                intent.putExtra("address", tvloction?.getText().toString())
                startActivity(intent)
            } else {
                Toast.makeText(
                    activity,
                    R.string.please_connect_to_internet,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        return view
    }

    fun isOnline(context: Context?): Boolean {
        val cm =
            context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }

    override fun onCreateOptionsMenu(
        menu: Menu,
        inflater: MenuInflater
    ) {
        menu.clear()
        inflater.inflate(R.menu.mnuser, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.meunu -> {
                menu()
                return (true)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun menu() {
        val builder =
            AlertDialog.Builder(activity)
        val animals =
            arrayOf("Update information", "Change Password", "Log Out", "Exit")
        builder.setItems(animals, object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {
                when (which) {
                    0 -> if (token == null) {
                        val intent = Intent(activity, InfomationActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            activity,
                            "Do not Update Facebook in here!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    1 -> dialogchangpassword()
                    2 -> {
                        if (token == null) {
                        } else {
                            LoginManager.getInstance().logOut()
                        }
                        logout()
                        val intent2 = Intent(activity, LoginActivity::class.java)
                        startActivity(intent2)
                        activity!!.finish()
                    }
                    3 -> activity!!.finish()
                    else -> throw IllegalStateException("Unexpected value: $which")
                }
            }
        })
        // create and show the alert dialog
        val dialog = builder.create()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val wmlp = dialog.window!!.attributes
        wmlp.gravity = Gravity.BOTTOM
        wmlp.flags = wmlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
        dialog.window!!.attributes = wmlp
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation_2
        dialog.show()
    }

    private fun logout() {
        val pref =
            activity!!.getSharedPreferences("USER_FILE", Context.MODE_PRIVATE)
        val edit = pref.edit()
        edit.clear()
        edit.commit()
    }

    private fun result() {
        val graphRequest = GraphRequest.newMeRequest(
            AccessToken.getCurrentAccessToken(),
            object : GraphJSONObjectCallback {
                override fun onCompleted(
                    `object`: JSONObject,
                    response: GraphResponse
                ) {
                    Log.d("JSON", response.jsonObject.toString())
                    try {
                        fbname = `object`.getString("name")
                        fbemail = `object`.getString("email")
                        tvName!!.text = fbname
                        tvemail!!.text = fbemail
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            })
        val parameters = Bundle()
        parameters.putString("fields", "id,name,email")
        graphRequest.parameters = parameters
        graphRequest.executeAsync()
    }

    private fun dialogchangpassword() {
        val pref =
            activity!!.getSharedPreferences("USER_FILE", Context.MODE_PRIVATE)
        val strUserName = pref.getString("USERNAME", "")
        val user = usersDAO!!.getuserbyid2((strUserName)!!)
        val dialog2 = Dialog((activity)!!)
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog2.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog2.setContentView(R.layout.dialogoutpass)
        dialog2.window!!.attributes.windowAnimations = R.style.DialogAnimation
        val oldpass: EditText
        val btn: Button
        btn = dialog2.findViewById(R.id.appCompatButton)
        oldpass = dialog2.findViewById(R.id.textInputEditTextOldPassword)
        btn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                if ((oldpass.text.toString().trim { it <= ' ' } == "")) {
                    Toast.makeText(
                        activity,
                        R.string.error_message_password,
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                if ((oldpass.text.toString().trim { it <= ' ' } == user!!.mPassword)) {
                    dialog2.dismiss()
                    val dialog1 = Dialog((activity)!!)
                    dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog1.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    dialog1.setContentView(R.layout.dialogchangepass)
                    dialog1.window!!.attributes.windowAnimations = R.style.DialogAnimation
                    val newpass: EditText
                    val conrfimnewpass: EditText
                    val btnreset: Button
                    newpass = dialog1.findViewById(R.id.EditPassword)
                    conrfimnewpass = dialog1.findViewById(R.id.EditConfirmPassword)
                    btnreset =
                        dialog1.findViewById(R.id.appCompatButtonReset)
                    btnreset.setOnClickListener(object : View.OnClickListener {
                        override fun onClick(v: View) {
                            if ((newpass.text.toString().trim { it <= ' ' } == "")) {
                                Toast.makeText(
                                    activity,
                                    R.string.error_message_password,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                if ((newpass.text.toString() == conrfimnewpass.text.toString()) && newpass.text.toString().length == conrfimnewpass.text.toString().length) {
                                    val pref =
                                        activity!!.getSharedPreferences(
                                            "USER_FILE",
                                            Context.MODE_PRIVATE
                                        )
                                    val strUserName = pref.getString("USERNAME", "")
                                    val users =
                                        User()
                                    users.mUser=(strUserName)
                                    users.mPassword=(newpass.text.toString().trim { it <= ' ' })
                                    if (usersDAO!!.changePasswordUser(users) > 0) {
                                        Toast.makeText(
                                            activity, "Change password successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        newpass.setText(null)
                                        conrfimnewpass.setText(null)
                                        dialog1.dismiss()
                                    } else {
                                        Toast.makeText(
                                            activity, "Password change failed",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        newpass.setText(null)
                                        conrfimnewpass.setText(null)
                                    }
                                } else {
                                    Toast.makeText(
                                        activity,
                                        R.string.error_password_match,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    })
                    dialog1.show()
                } else {
                    oldpass.setText(null)
                    Toast.makeText(activity, "Wrong Password", Toast.LENGTH_SHORT).show()
                }
            }
        })
        dialog2.show()
    }
}