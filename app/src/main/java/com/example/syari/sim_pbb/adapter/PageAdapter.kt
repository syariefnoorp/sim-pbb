package com.example.syari.sim_pbb.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class PageAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {

    var mFragmentItems:ArrayList<Fragment> = ArrayList()

    //function to add fragment
    fun addFragment(fragmentItem: Fragment){
        mFragmentItems.add(fragmentItem)
    }

    override fun getItem(position: Int): Fragment {
        return mFragmentItems[position]
    }

    override fun getCount(): Int {
        return mFragmentItems.size
    }

}