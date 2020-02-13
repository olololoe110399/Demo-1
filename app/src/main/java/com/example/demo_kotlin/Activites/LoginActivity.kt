package com.example.demo_kotlin.Activites

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.demo_kotlin.Fragment.Fragment_Signin
import com.example.demo_kotlin.Fragment.Fragment_Signup
import com.example.demo_kotlin.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val fragment_signin = Fragment_Signin()
        loadFragment(fragment_signin)
        initView()

    }

    private fun initView() {
        btnin = findViewById(
            R.id.btnSignin
        )
        btnout = findViewById(
            R.id.btnSingnup
        )
    }

    fun onClick(view: View) {
        val fragment: Fragment
        when (view.id) {
            R.id.btnSignin -> {
                btnin!!.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.colorWhite
                    )
                )
                btnin!!.setBackgroundResource(
                    R.drawable.btnsginin1
                )
                btnout!!.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.color
                    )
                )
                btnout!!.setBackgroundResource(
                    R.drawable.btnsignup2
                )
                fragment = Fragment_Signin()
                loadFragment(fragment)
            }
            R.id.btnSingnup -> {
                btnin!!.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.color
                    )
                )
                btnin!!.setBackgroundResource(
                    R.drawable.btnsignin2
                )
                btnout!!.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.colorWhite
                    )
                )
                btnout!!.setBackgroundResource(
                    R.drawable.btnsignup1
                )
                fragment = Fragment_Signup()
                loadFragment(fragment)
            }
        }
    }

    fun backsignin() {
        btnin!!.setTextColor(
            ContextCompat.getColor(
                applicationContext,
                R.color.colorWhite
            )
        )
        btnin!!.setBackgroundResource(
            R.drawable.btnsginin1
        )
        btnout!!.setTextColor(
            ContextCompat.getColor(
                applicationContext,
                R.color.color
            )
        )
        btnout!!.setBackgroundResource(
            R.drawable.btnsignup2
        )
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        val fragment =
            supportFragmentManager.findFragmentById(R.id.frame_container2)
        fragment!!.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun loadFragment(fragment: Fragment) { // load fragment
        val transaction =
            supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_container2, fragment)
        transaction.commit()
    }

//    private fun gethashkey() {
//        val info: PackageInfo
//        try {
//            info = packageManager.getPackageInfo(
//                "com.example.demo_kotlin",
//                PackageManager.GET_SIGNATURES
//            )
//            for (signature in info.signatures) {
//                var md: MessageDigest
//                md = MessageDigest.getInstance("SHA")
//                md.update(signature.toByteArray())
//                val something = String(Base64.encode(md.digest(), 0))
//                //String something = new String(Base64.encodeBytes(md.digest()));
//                Log.e("hash key", something)
//            }
//        } catch (e1: PackageManager.NameNotFoundException) {
//            Log.e("name not found", e1.toString())
//        } catch (e: NoSuchAlgorithmException) {
//            Log.e("no such an algorithm", e.toString())
//        } catch (e: Exception) {
//            Log.e("exception", e.toString())
//        }
//    }

    companion object {
        var btnin: Button? = null
        var btnout: Button? = null
    }
}