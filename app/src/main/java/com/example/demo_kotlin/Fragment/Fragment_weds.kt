package com.example.demo_kotlin.Fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.example.demo_kotlin.Activites.MainActivity.OnBackPressedListner
import com.example.demo_kotlin.R

@Suppress("DEPRECATION")
class Fragment_weds : Fragment(), OnBackPressedListner {
    var dialog: ProgressDialog? = null

    var webView: WebView? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view: View? = inflater.inflate(R.layout.fragment_wed, container, false)
        initView()
        webView!!.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                if (dialog!!.isShowing) {
                    dialog!!.dismiss()
                }
            }
        }
        dialog!!.setMessage("Loading...Please wait.")
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.show()
        webView!!.loadUrl(link)
        val webSettings = webView!!.settings
        webSettings.javaScriptEnabled = true
        return view
    }

    private fun initView() {
        dialog = ProgressDialog(activity)
        webView = view!!.findViewById(R.id.wedtintiuc)
    }

    override fun onBackPressed(): Boolean {
        return true
    }

    companion object {
        var link = ""
    }
}