package com.skillbox.aslanbolurov.customclockwidget


import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class CustomTimerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val format = SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
    private val btnPlayStop: Button
    private val btnReset: Button
    private val tvDigitalTime: TextView
    private val customAnalogClockView: CustomAnalogClockView


     var timerJob: Job? = null
    private var isPlayed: Boolean = false
    private var isAfterConfigChange:Boolean=false

    private var counterListeners = mutableSetOf<(TimeState) -> Unit>()
    var time = currentTime()
        set(value) {
            if (value == field)
                return
            field = value
            counterListeners.forEach { it(TimeState(time, isPlayed)) }
        }
    private var timeState: TimeState = TimeState(time, false)

    init {

        isAfterConfigChange=true

        val root = inflate(context, R.layout.custom_constraint, this)
        btnPlayStop = root.findViewById(R.id.btnStartStop)
        btnReset = root.findViewById(R.id.btnReset)
        tvDigitalTime = root.findViewById(R.id.tvDigitalTime)
        customAnalogClockView = root.findViewById(R.id.clock_widget)

        format.timeZone = TimeZone.getTimeZone("GMT+00")



        btnPlayStop.setOnClickListener {
            execute()
        }

        btnReset.setOnClickListener {
            reset()
        }

        changeText(time)
        addUpdateListener { timeState ->
            changeText(timeState.time)
            changeClockDraw(timeState.time)
        }

    }
    private fun start() {
        Log.d(TAG, "start: in $isAfterConfigChange ${format.format(timeState.time)}")
        btnPlayStop.text = resources.getText(R.string.button_stop)
        isPlayed = true
        timerJob = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                delay(999)
                if (isAfterConfigChange) {
                    Log.d(TAG, "start: last time: ${format.format(timeState.time)}")
                    time=timeState.time
                    timeState= TimeState(time, isPlayed)
                    isAfterConfigChange=false
                    Log.d(TAG, "start: true $time")
                } else{
                    time += 1000L
                    timeState = TimeState(time, isPlayed)

                }
//                Log.d(TAG, "start: false $time")
//                Log.d(TAG, "Time updated, new time is ${format.format(time)}.")

            }
        }
    }

    private fun stop(fromEach: Boolean = false) {
        btnPlayStop.text = resources.getText(R.string.button_start)
        isPlayed = false
        timerJob?.cancel()
        if (!fromEach) {
            timeState = TimeState(time, isPlayed)
            Log.d(TAG, "StopButton was clicked, last time is ${format.format(time)}.")
        }
    }

    private fun reset() {
        stop(true)
        time = 0
        timeState = TimeState(time, isPlayed)
        Log.d(TAG, "ResetButton was clicked, new time is ${format.format(time)}.")
    }
    fun execute(){

        Log.d(TAG, "execute: in")
//        if (btnPlayStop.text == resources.getText(R.string.button_start)) {
        if (!timeState.isPlayed) {
            Log.d(TAG, "execute: in , start in")
            start()
        } else {
            Log.d(TAG, "execute: in , stop in")
            stop()

        }


    }
    private fun currentTime(): Long {
        return System.currentTimeMillis()
    }

    private fun addUpdateListener(listener: (TimeState) -> Unit) {
        counterListeners.add(listener)
        listener(TimeState(time, isPlayed))
    }

    /*private fun removeUpdateListener(listener: (TimeState) -> Unit) {
        counterListeners.remove(listener)
    }*/

    private fun changeText(timeForSet: Long) {
        tvDigitalTime.text = format.format(timeForSet)
    }


    private fun changeClockDraw(time: Long) {
        val timeStr = format.format(time)
        val hours = timeStr.substring(0,2).toDouble()
        val minutes = timeStr.substring(3,5).toDouble()
        val seconds = timeStr.substring(6).toDouble()
        customAnalogClockView.setTime(listOf(hours,minutes,seconds))
    }

    companion object {
        const val TAG = "CustomTimerView"
        const val DATE_TIME_FORMAT = "HH:mm:ss"
    }
}







