package com.example.demo_kotlin.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.demo_kotlin.Adapter.ViewpageFileAdapter
import com.example.demo_kotlin.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.google.android.material.tabs.TabLayout.ViewPagerOnTabSelectedListener

class Fragment_File : Fragment() {
    private var pager: ViewPager? = null
    private var tabLayout: TabLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_file, container, false)
        addControl(view)
        return view
    }

    //TabLayout - ViewPager
    fun addControl(view: View) {
        pager = view.findViewById<View>(R.id.view_pager) as ViewPager
        tabLayout = view.findViewById<View>(R.id.tab_layout) as TabLayout
        val manager = childFragmentManager
        val adapter = ViewpageFileAdapter(manager)
        pager!!.adapter = adapter
        tabLayout!!.setupWithViewPager(pager)
        pager!!.addOnPageChangeListener(TabLayoutOnPageChangeListener(tabLayout))
        tabLayout!!.addOnTabSelectedListener(ViewPagerOnTabSelectedListener(pager))
        val tabIcons = intArrayOf(
            R.drawable.ic_users,
            R.drawable.ic_invoice
        )
        tabLayout!!.getTabAt(0)!!.setIcon(tabIcons[0])
        tabLayout!!.getTabAt(1)!!.setIcon(tabIcons[1])
    }
}