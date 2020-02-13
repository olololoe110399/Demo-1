package com.example.demo_kotlin.Activites

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.demo_kotlin.DAO.UsersDAO
import com.example.demo_kotlin.Model.User
import com.example.demo_kotlin.R
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException

class InfomationActivity : AppCompatActivity() {
    var image: ByteArray? = null
    var camera: RelativeLayout? = null
    var back: TextView? = null
    var name: EditText? = null
    var address: EditText? = null
    var phone: EditText? = null
    var email: TextView? = null
    var update: Button? = null
    var userDAO: UsersDAO? = null
    var avataer: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_infomation)
        camera = findViewById(R.id.camera)
        back = findViewById(R.id.back)
        name = findViewById(R.id.edtnameemployees)
        address = findViewById(R.id.edtaddress)
        phone = findViewById(R.id.edtphone)
        email = findViewById(R.id.email)
        avataer = findViewById(R.id.avatar)
        userDAO = UsersDAO(this)
        val pref =
            getSharedPreferences("USER_FILE", Context.MODE_PRIVATE)
        val nameFromIntent = pref.getString("USERNAME", "")
        if (nameFromIntent?.let { userDAO?.checknull(it) }!! > 0) {
            email?.setText(nameFromIntent)
        } else {
            val user2: User? = userDAO!!.getuserbyid2(nameFromIntent)
            email?.setText(user2?.mUser)
            name?.setText(user2?.mName)
            address?.setText(user2?.mAddress)
            phone?.setText(user2?.mPhone)
            val imgavatar: ByteArray? = user2?.mImage
            val bitmap = imgavatar?.size?.let { BitmapFactory.decodeByteArray(imgavatar, 0, it) }
            avataer?.setImageBitmap(bitmap)
        }
        back?.setOnClickListener(View.OnClickListener { dialogback() })
        update = findViewById(R.id.updateinformation)
        update?.setOnClickListener(View.OnClickListener { updateinformation() })
        camera?.setOnClickListener(View.OnClickListener { selectImage() })
    }

    private fun updateinformation() {
        if (name!!.text.toString() == "" || address!!.text.toString() == "" || phone!!.text.toString() == "") {
            Toast.makeText(this, "Do not empty!", Toast.LENGTH_SHORT).show()
        } else {
            if (10 > phone!!.text.toString().trim { it <= ' ' }.length && phone!!.text.toString().trim { it <= ' ' }.length < 12) {
                Toast.makeText(this,
                    R.string.error_valid_phone, Toast.LENGTH_SHORT).show()
            } else {
                val user = User()
                if (image == null) {
                    val res = resources
                    val drawable = res.getDrawable(R.drawable.avatar)
                    val bitmap = (drawable as BitmapDrawable).bitmap
                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    image = stream.toByteArray()
                }
                user.mUser=(email!!.text.toString())
                user.mImage=(image)
                user.mAddress=(address!!.text.toString())
                user.mName=(name!!.text.toString())
                user.mPhone=(phone!!.text.toString())
                if (userDAO?.updateUser(user)!! > 0) {
                    MainActivity.Mainclass.activityMain?.finish()
                    Toast.makeText(this,
                        R.string.successfully, Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun dialogback() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Update information!")
        builder.setMessage("Updating information helps you more convenient in the process of using! Are you sure you do not want to update information? ")
        builder.setCancelable(false)
        builder.setPositiveButton("No") { dialogInterface, i -> dialogInterface.dismiss() }
        builder.setNegativeButton("Yes") { dialogInterface, i -> finish() }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun selectImage() {
        val builder = AlertDialog.Builder(this)
        val animals =
            arrayOf("Take photo", "Choose from Library")
        builder.setItems(animals) { dialog, which ->
            when (which) {
                0 -> takephoto()
                1 -> chooselibrary()
                else -> throw IllegalStateException("Unexpected value: $which")
            }
        }
        // create and show the alert dialog
        val dialog = builder.create()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val wmlp = dialog.window!!.attributes
        wmlp.gravity = Gravity.BOTTOM
        wmlp.flags = wmlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
        dialog.window!!.attributes = wmlp
        dialog.window!!.attributes.windowAnimations =
            R.style.DialogAnimation_2
        dialog.show()
    }

    private fun takephoto() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                REQUEST_CODE_CAMERA
            )
        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent,
                REQUEST_CODE_CAMERA
            )
        }
    }

    private fun chooselibrary() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                REQUEST_CODE_LIBRARY
            )
        } else {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,
                REQUEST_CODE_LIBRARY
            )
        }
    }

    public override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK && data != null) {
            val bitmap = data.extras!!["data"] as Bitmap?
            avataer!!.setImageBitmap(bitmap)
            val byteArray = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, byteArray)
            image = byteArray.toByteArray()
        }
        if (requestCode == REQUEST_CODE_LIBRARY && resultCode == Activity.RESULT_OK && data != null) {
            val uri = data.data
            try {
                val inputStream =
                    this.contentResolver.openInputStream(uri!!)
                val bitmap = BitmapFactory.decodeStream(inputStream) as Bitmap
                avataer!!.setImageBitmap(bitmap)
                val byteArray = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArray)
                image = byteArray.toByteArray()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_CAMERA -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent,
                    REQUEST_CODE_CAMERA
                )
            } else {
                Toast.makeText(this, "Do not allow to open the CAMERA", Toast.LENGTH_SHORT).show()
            }
            REQUEST_CODE_LIBRARY -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent,
                    REQUEST_CODE_LIBRARY
                )
            } else {
                Toast.makeText(this, "Do not allow to open the LIBRARY", Toast.LENGTH_SHORT).show()
            }
            else -> throw IllegalStateException("Unexpected value: $requestCode")
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        private const val REQUEST_CODE_CAMERA = 123
        private const val REQUEST_CODE_LIBRARY = 456
    }
}