package com.skillbox.aslanbolurov.customclockwidget

import androidx.lifecycle.ViewModel
import java.util.*

class ClockFragmentViewModel:ViewModel() {

    var time=Calendar.getInstance().timeInMillis


}