package com.example.demo_kotlin.Fragment

import android.content.Context
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.demo_kotlin.Adapter.RssAdapter
import com.example.demo_kotlin.DAO.RssDAO
import com.example.demo_kotlin.Helper.XMLDOMParser
import com.example.demo_kotlin.Model.RSS
import com.example.demo_kotlin.R
import org.w3c.dom.Element
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL
import kotlin.math.log


@Suppress("DEPRECATION")
class Fragment_News : Fragment() {
    var recyclerView: RecyclerView? = null
    var rssList: MutableList<RSS>? = null
    var adapterRss: RssAdapter? = null
    var rssDAO: RssDAO? = null
    var edSearch: SwipeRefreshLayout? = null
    var btnSearch: ProgressBar? = null
    var TAG: String? = "AAA"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_news, container, false)

        rssDAO = RssDAO(activity)
        recyclerView = view.findViewById(R.id.recycleviewRss)
        edSearch = view.findViewById(R.id.load)
        btnSearch = view.findViewById(R.id.progressbar)
        val layoutManager =
            LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recyclerView?.setLayoutManager(layoutManager)
        val dividerItemDecoration =
            DividerItemDecoration(recyclerView?.getContext(), DividerItemDecoration.VERTICAL)

        recyclerView?.addItemDecoration(dividerItemDecoration)

        recyclerView?.setItemAnimator(DefaultItemAnimator())
        checkInternetLoad()
        edSearch?.setOnRefreshListener {
            edSearch?.setRefreshing(true)
            checkInternetLoad()

        }
        return view
    }

    fun loadAdapter() {
        Log.i(TAG, "Load Apdater")
        rssList = rssDAO!!.allRss
        adapterRss = RssAdapter(this.rssList!!, activity)
        recyclerView!!.adapter = adapterRss
        adapterRss!!.notifyDataSetChanged()
        edSearch?.setRefreshing(false)
    }

    inner class ReadRSS :
        AsyncTask<String?, Void?, String>() {

        override fun onPostExecute(s: String) {
            val parser = XMLDOMParser()
            val document = parser.getDocument(s)
            val nodeList = document!!.getElementsByTagName("item")
            var tieuDe = ""
            var link = ""
            var pubDate = ""
            for (i in 0 until nodeList.length) {
                val element = nodeList.item(i) as Element
                tieuDe = parser.getValue(element, "title")
                link = parser.getValue(element, "link")
                pubDate = parser.getValue(element, "pubDate")
                val checkTittle = rssDAO!!.checkTitile(tieuDe)
                Log.i(TAG, "have internet")
                if (!checkTittle) {
                    Log.i(TAG, "not SQlite")
                    val rss = RSS()
                    rss.tittle = tieuDe
                    rss.link = link
                    rss.pubdate = pubDate
                    rssDAO!!.addRss(rss)
                    loadAdapter()
                    btnSearch!!.visibility = View.GONE
                } else {
                    Log.i(TAG, "have SQlite")
                    loadAdapter()
                    btnSearch!!.visibility = View.GONE

                }
            }

            super.onPostExecute(s)
        }

        override fun doInBackground(vararg params: String?): String {
            val content = StringBuilder()
            try {
                val url = URL(params[0])
                val inputStreamReader =
                    InputStreamReader(url.openConnection().getInputStream())
                val bufferedReader =
                    BufferedReader(inputStreamReader)
                var line: String? = ""
                while (bufferedReader.readLine().also { line = it } != null) {
                    content.append(line)
                }
                bufferedReader.close()
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return content.toString()
        }
    }


    fun loadFragment(
        activity: Context,
        fragment: Fragment?
    ) { // load fragment
        val transaction =
            (activity as AppCompatActivity).supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_container, fragment!!)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun checkInternetLoad() {
        if (!isNetworkAvailable) {
            Toast.makeText(context, "Not Internet", Toast.LENGTH_SHORT).show()
            loadAdapter()
            btnSearch!!.visibility = View.GONE
            Log.i(TAG, "not internet")
        } else {
            ReadRSS().execute("https://cosmetics.einnews.com/rss/K1p5-5RY9nuff5IS")
        }
    }

    private val isNetworkAvailable: Boolean
        private get() {
            val connectivityManager =
                context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
}