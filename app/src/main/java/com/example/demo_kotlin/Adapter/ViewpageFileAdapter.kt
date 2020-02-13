package com.example.demo_kotlin.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.demo_kotlin.Fragment.Fragment_Invoice
import com.example.demo_kotlin.Fragment.Fragment_Users

@Suppress("DEPRECATION")
class ViewpageFileAdapter(fragmentManager: FragmentManager?) :
    FragmentStatePagerAdapter(fragmentManager!!) {

    var TAG = "duy"
    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = Fragment_Users.newInstance()
            1 -> fragment = Fragment_Invoice.newInstance()
        }
        return fragment!!
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var title = "PINK"
        when (position) {
            0 -> title = "EMPLOYEES"
            1 -> title = "INVOICE"
        }
        return title
    }
}