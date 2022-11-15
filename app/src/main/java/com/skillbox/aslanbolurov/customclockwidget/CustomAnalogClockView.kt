package com.skillbox.aslanbolurov.customclockwidget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

class CustomAnalogClockView : View {
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var mHeight: Int = 0
    private var mWidth: Int = 0
    private val mClockHours = listOf<Int>(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
    private var mPadding: Int = 0
    private var mNumeralSpacing: Int = 0
    private var mHandTruncation: Int = 0
    private var mHourHandTruncation: Int = 0
    private var mRadius: Int = 0
    private var mPaint: Paint? = null
    private val mRect: Rect = Rect()
    private var isInit = false
    private var timeList: List<Double> = listOf(0.toDouble(),0.toDouble(),0.toDouble())

    override fun onDraw(canvas: Canvas) {
        initOnce()
        drawClockFace(canvas)
        drawClockHands(canvas)
    }

    private fun initOnce() {
        if (!isInit) {
            mPaint = Paint()
            mHeight = height
            mWidth = width
            mPadding = mNumeralSpacing + 50
            val minAttr = mHeight.coerceAtMost(mWidth)
            mRadius = minAttr / 2 - mPadding
            mHandTruncation = minAttr / 20
            mHourHandTruncation = minAttr / 17
            isInit = true
        }
    }

    private fun drawClockFace(canvas: Canvas) {
        canvas.drawColor(Color.DKGRAY)
        // circle border
        mPaint?.reset()
        mPaint?.color = Color.BLACK
        mPaint?.style = Paint.Style.STROKE
        mPaint?.strokeWidth = 4F
        mPaint?.isAntiAlias = true
        mPaint?.let {
            canvas.drawCircle(
                (mWidth / 2).toFloat(),
                (mHeight / 2).toFloat(),
                (mRadius + mPadding - 10).toFloat(),
                it
            )
        }

        //border of hours
        val fontSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            14F,
            resources.displayMetrics
        )
        mPaint?.textSize = fontSize

        for (hour in mClockHours) {
            val tmp = hour.toString()
            mPaint!!.getTextBounds(tmp, 0, tmp.length, mRect)

            val angle = Math.PI / 6 * (hour - 3)
            val x = (mWidth / 2 + cos(angle) * mRadius - mRect.width() / 2).toFloat()
            val y = (mHeight / 2 + sin(angle) * mRadius + mRect.height() / 2).toFloat()
            canvas.drawText(
                hour.toString(),
                x,
                y,
                mPaint!!
            )
        }
    }

    private fun drawClockHands(canvas: Canvas) {
        fun drawHandLine(canvas: Canvas, moment: Double, isHour: Boolean, isSecond: Boolean) {
            val angle = Math.PI * moment / 30 - Math.PI / 2
            val handRadius =
                if (isHour) mRadius - mHandTruncation - mHourHandTruncation * 2 else mRadius - mHandTruncation
            if (isSecond) mPaint!!.color = Color.RED
            canvas.drawLine(
                (mWidth / 2).toFloat(),
                (mHeight / 2).toFloat(),
                (mWidth / 2 + cos(angle) * handRadius).toFloat(),
                (mHeight / 2 + sin(angle) * handRadius).toFloat(),
                mPaint!!
            )
        }

        drawHandLine(
            canvas,
            ((timeList[0] + timeList[1] / 60) * 5),
            true,
            false
        ); // draw hours
        drawHandLine(
            canvas,
            timeList[1],
            false,
            false
        ); // draw minutes
        drawHandLine(canvas, timeList[2], false, true); // draw seconds

        // clock-center
        mPaint?.style = Paint.Style.FILL
        mPaint?.let {
            canvas.drawCircle(
                (mWidth / 2).toFloat(),
                (mHeight / 2).toFloat(),
                12F,
                it
            )
        }
    }

    fun setTime(timeListFromTimer: List<Double>) {
        timeList = timeListFromTimer
        //invalidate the appearance for next representation of time
        //postInvalidateDelayed(500)
        invalidate()
    }

    companion object {
        const val TAG = CustomTimerView.TAG
    }
}