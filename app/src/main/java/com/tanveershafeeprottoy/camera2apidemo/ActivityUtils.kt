package com.tanveershafeeprottoy.camera2apidemo

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

object ActivityUtils {
    private var fragmentTransaction: FragmentTransaction? = null

    fun addFragmentOnActivity(fragmentManager: FragmentManager?, fragment: Fragment, frameId: Int) {
        fragmentTransaction = fragmentManager?.beginTransaction()
        fragmentTransaction?.add(frameId, fragment)
        fragmentTransaction?.commit()
    }

    fun replaceFragmentOnActivity(
        fragmentManager: FragmentManager?, fragment: Fragment, frameId: Int,
        name: String = ""
    ) {
        fragmentTransaction = fragmentManager?.beginTransaction()
        fragmentTransaction?.replace(frameId, fragment)
        if(name != "") {
            fragmentTransaction?.addToBackStack(name)
        }
        fragmentTransaction?.commit()
    }

    fun setToolbarTitle(appCompatActivity: AppCompatActivity?, title: String) {
        if(appCompatActivity?.supportActionBar?.title != title) {
            appCompatActivity?.supportActionBar?.title = title
        }
    }

    fun setToolbarTitle(fragmentActivity: FragmentActivity?, title: String) {
        if((fragmentActivity as AppCompatActivity).supportActionBar?.title != title) {
            fragmentActivity.supportActionBar?.title = title
        }
    }
}