package com.jishindev.android.xkcd.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class VpAdapter(fm: FragmentManager, private val fragmentsMap: Map<String, Fragment>) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int) = fragmentsMap.values.toList()[position]

    override fun getCount() = fragmentsMap.size

    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentsMap.keys.toList()[position]
    }
}