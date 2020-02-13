package com.example.demo_kotlin.Activites

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.demo_kotlin.R
import com.facebook.AccessToken
import com.facebook.login.LoginManager

class SplashScreenActivity : AppCompatActivity() {
    var strUserName: String? = null
    var strPassword: String? = null
    var token: AccessToken? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        token = AccessToken.getCurrentAccessToken()
        Handler().postDelayed({
            if (checkLoginShap() < 0) {
                val intent =
                    Intent(this@SplashScreenActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                if (isOnline(this@SplashScreenActivity)) {
                    val intent =
                        Intent(this@SplashScreenActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    if (token == null) {
                        val intent =
                            Intent(this@SplashScreenActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        logout()
                        LoginManager.getInstance().logOut()
                        val intent =
                            Intent(this@SplashScreenActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }, TIMEOUT_MILLIS.toLong())
    }

    fun checkLoginShap(): Int {
        val pref =
            getSharedPreferences("USER_FILE", Context.MODE_PRIVATE)
        val chk = pref.getBoolean("REMEMBER", false)
        if (chk) {
            strUserName = pref.getString("USERNAME", "")
            strPassword = pref.getString("PASSWORD", "")
            return 1
        }
        return -1
    }

    fun isOnline(context: Context): Boolean {
        val cm =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }

    private fun logout() {
        val pref =
            getSharedPreferences("USER_FILE", Context.MODE_PRIVATE)
        val edit = pref.edit()
        edit.clear()
        edit.commit()
    }

    companion object {
        var TIMEOUT_MILLIS = 4000
    }
}