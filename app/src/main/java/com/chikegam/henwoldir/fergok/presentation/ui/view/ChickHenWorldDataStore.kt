package com.chikegam.henwoldir.fergok.presentation.ui.view

import android.annotation.SuppressLint
import android.widget.FrameLayout
import androidx.lifecycle.ViewModel

class ChickHenWorldDataStore : ViewModel(){
    val chickHenWorldViList: MutableList<ChickHenWorldVi> = mutableListOf()
    var chickHenWorldIsFirstCreate = true
    @SuppressLint("StaticFieldLeak")
    lateinit var chickHenWorldContainerView: FrameLayout
    @SuppressLint("StaticFieldLeak")
    lateinit var chickHenWorldView: ChickHenWorldVi
}