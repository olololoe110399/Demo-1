package com.example.demo_kotlin.Activites

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.demo_kotlin.DAO.UsersDAO
import com.example.demo_kotlin.Fragment.Fragment_Clothes
import com.example.demo_kotlin.Fragment.Fragment_File
import com.example.demo_kotlin.Fragment.Fragment_News
import com.example.demo_kotlin.Fragment.Fragment_User
import com.example.demo_kotlin.Model.User
import com.example.demo_kotlin.R
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONException
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    lateinit var appBarLayout: AppBarLayout
    lateinit var collapsingToolbar: CollapsingToolbarLayout
    lateinit var activity: Activity

    var startingPosition = 0
    private var navigation: BottomNavigationView? = null
    private var toolbar2: Toolbar? = null
    private var mHandler: Handler? = null
    private var floatingActionButton: FloatingActionButton? = null
    var avatar: ImageView? = null
    var imageURL: String? = null
    var name: TextView? = null
    var email: TextView? = null
    var id: String? = null
    var fbname: String? = null
    var fbemail: String? = null
    var token: AccessToken? = null
    var uersDAO: UsersDAO? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        Mainclass.activityMain = this
        activity = this
        setSupportActionBar(toolbar2)
        supportActionBar!!.title = "NEWS"
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        val activity2 = CartActivity()
        if (activity2.count() == 0) {
            tvcount?.setBackgroundResource(0)
            tvcount?.text = ""
        } else {
            tvcount?.setBackgroundResource(
                R.drawable.count
            )
            tvcount?.text = activity2.count().toString()
        }
        token = AccessToken.getCurrentAccessToken()
        if (token == null) {
            val pref =
                getSharedPreferences("USER_FILE", Context.MODE_PRIVATE)
            val nameFromIntent = pref.getString("USERNAME", "")
            if (uersDAO!!.checknull(nameFromIntent!!) > 0) {
                updateinfomation()
                email!!.text = nameFromIntent
            } else {
                val user: User? = uersDAO!!.getuserbyid2(nameFromIntent)
                email?.setText(user?.mUser)
                name?.setText(user?.mName)
                val imgAvatar: ByteArray? = user?.mImage
                val bitmap = imgAvatar?.size?.let {
                    BitmapFactory.decodeByteArray(
                        imgAvatar, 0,
                        it
                    )
                }
                avatar!!.setImageBitmap(bitmap)
            }
        } else {
            if (isOnline(this)) {
                result()
            } else {
                Toast.makeText(
                    activity,
                    R.string.please_connect_to_internet,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        loadFragment(Fragment_News(), 1)
        mHandler = Handler(Looper.getMainLooper())
        mHandler!!.postDelayed({ appBarLayout.setExpanded(false, true) }, 3500)
        floatingActionButton!!.setOnClickListener {
            val intent = Intent(this@MainActivity, CartActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateinfomation() {
        val intent = Intent(this, InfomationActivity::class.java)
        startActivity(intent)
    }

    fun setcount(count: Int?) {
        if (count == 0) {
            tvcount?.setBackgroundResource(0)
            tvcount?.text = ""
        } else {
            tvcount?.setBackgroundResource(
                R.drawable.count
            )
            tvcount?.text = count.toString()
        }
    }
companion object{
    var tvcount: TextView? = null
}
    private fun result() {
        val graphRequest = GraphRequest.newMeRequest(
            AccessToken.getCurrentAccessToken()
        ) { `object`, response ->
            Log.d("JSON", response.jsonObject.toString())
            try {
                id = `object`.getString("id")
                fbname = `object`.getString("name")
                fbemail = `object`.getString("email")
                name!!.text = fbname
                email!!.text = fbemail
                imageURL = "https://graph.facebook.com/$id/picture?type=large"
                Loadimage().execute(imageURL)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        val parameters = Bundle()
        parameters.putString("fields", "id,name,email")
        graphRequest.parameters = parameters
        graphRequest.executeAsync()
    }


    inner class Loadimage : AsyncTask<String?, Void?, Bitmap?>() {
        var bitmaph: Bitmap? = null
        override fun doInBackground(vararg params: String?): Bitmap? {
            try {
                val url = URL(params[0])
                val inputStream = url.openConnection().getInputStream()
                bitmaph = BitmapFactory.decodeStream(inputStream)
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return bitmaph
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
            avatar!!.setImageBitmap(result)
        }

    }

    private fun initView() {
        tvcount = findViewById(
            R.id.tvCount
        )
        uersDAO = UsersDAO(this)
        name = findViewById(R.id.Username)
        email = findViewById(R.id.tvemail)
        avatar = findViewById(R.id.avatartoolbar)
        floatingActionButton = findViewById(R.id.btnfloat)
        appBarLayout = findViewById(
            R.id.app_bar
        )
        collapsingToolbar =
            findViewById<View>(R.id.toolbar_layout) as CollapsingToolbarLayout
        toolbar2 = findViewById(R.id.toolbar2)
        navigation = findViewById(R.id.navigation)
        navigation!!.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
            var newPositon = 0
            val fragment: Fragment
            when (menuItem.itemId) {
                R.id.News -> {
                    supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                    supportActionBar!!.setDisplayShowHomeEnabled(false)
                    appBarLayout.setExpanded(false, true)
                    fragment = Fragment_News()
                    supportActionBar!!.setTitle("NEWS")
                    collapsingToolbar.title = "NEWS"
                    newPositon = 1
                    loadFragment(fragment, newPositon)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.Clothes -> {
                    supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                    supportActionBar!!.setDisplayShowHomeEnabled(false)
                    appBarLayout.setExpanded(false, true)
                    fragment = Fragment_Clothes()
                    supportActionBar!!.setTitle("COSMETICS")
                    collapsingToolbar.title = "COSMETICS"
                    newPositon = 2
                    loadFragment(fragment, newPositon)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.File -> {
                    supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                    supportActionBar!!.setDisplayShowHomeEnabled(false)
                    appBarLayout.setExpanded(false, true)
                    fragment = Fragment_File()
                    supportActionBar!!.setTitle("STORAGE")
                    collapsingToolbar.title = "STORAGE"
                    newPositon = 3
                    loadFragment(fragment, newPositon)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.User -> {
                    supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                    supportActionBar!!.setDisplayShowHomeEnabled(false)
                    appBarLayout.setExpanded(true, true)
                    fragment = Fragment_User()
                    supportActionBar!!.setTitle("PROFILE")
                    collapsingToolbar.title = "PROFILE"
                    newPositon = 4
                    loadFragment(fragment, newPositon)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    private fun loadFragment(
        fragment: Fragment?,
        position: Int
    ): Boolean {
        if (fragment != null) {
            if (startingPosition > position) {
                supportFragmentManager.beginTransaction().setCustomAnimations(
                    R.anim.left_to_right,
                    R.anim.exit_left_to_right,
                    R.anim.right_to_left,
                    R.anim.exit_right_to_left
                ).replace(R.id.frame_container, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            }
            if (startingPosition < position) {
                supportFragmentManager.beginTransaction().setCustomAnimations(
                    R.anim.right_to_left,
                    R.anim.exit_right_to_left,
                    R.anim.left_to_right,
                    R.anim.exit_left_to_right
                ).replace(R.id.frame_container, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            }
            startingPosition = position
            return true
        }
        return false
    }

    override fun onBackPressed() {
        val fragment =
            supportFragmentManager.findFragmentById(R.id.frame_container)
        if (navigation!!.selectedItemId == R.id.News) {
            super.onBackPressed()
        } else if (fragment !is OnBackPressedListner || !(fragment as OnBackPressedListner).onBackPressed()) {
            navigation!!.selectedItemId = R.id.News
        } else {
            navigation!!.selectedItemId = R.id.News
        }
    }

    interface OnBackPressedListner {
        fun onBackPressed(): Boolean
    }


    override fun onSupportNavigateUp(): Boolean {
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        supportActionBar!!.setDisplayShowHomeEnabled(false)
        appBarLayout.setExpanded(false, true)
        val fragment: Fragment = Fragment_News()
        supportFragmentManager.beginTransaction().replace(R.id.frame_container, fragment)
            .commit()
        supportActionBar!!.setTitle("NEWS")
        collapsingToolbar.title = "NEWS"
        return true
    }

    fun isOnline(context: Context): Boolean {
        val cm =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }

    class Mainclass {
        companion object {
            var activityMain: Activity? = null
        }
    }

}