package com.example.demo_kotlin.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.demo_kotlin.Activites.MainActivity
import com.example.demo_kotlin.Fragment.Fragment_News
import com.example.demo_kotlin.Fragment.Fragment_weds
import com.example.demo_kotlin.Model.RSS
import com.example.demo_kotlin.R

class RssAdapter(
    var rssList: MutableList<RSS>,
    var context: Context?
) : RecyclerView.Adapter<RssAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_rss, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val rss = rssList[position]
        holder.tvTitlte.text = rss.tittle
        holder.tvPubDate.text = rss.pubdate
        holder.layout.setOnClickListener {
            val fragment_weds = Fragment_weds()
            val fragment_news = Fragment_News()
            Fragment_weds.link = rssList[position].link.toString()
            (context as AppCompatActivity).supportActionBar!!.setTitle("WED")
            val activity = context as MainActivity
            activity.appBarLayout.setExpanded(false, true)
            activity.collapsingToolbar.title = "WED"
            (context as AppCompatActivity).supportActionBar
                ?.setDisplayShowTitleEnabled(true)
            (context as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            (context as AppCompatActivity).supportActionBar!!.setDisplayShowHomeEnabled(true)
            fragment_news.loadFragment(context as AppCompatActivity, fragment_weds)
        }
    }

    override fun getItemCount(): Int {
        return rssList.size
    }

    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var tvTitlte: TextView
        var tvPubDate: TextView
        var layout: LinearLayout

        init {
            layout = itemView.findViewById(R.id.onclick)
            tvTitlte = itemView.findViewById(R.id.titleEntry)
            tvPubDate = itemView.findViewById(R.id.description)
        }
    }

}