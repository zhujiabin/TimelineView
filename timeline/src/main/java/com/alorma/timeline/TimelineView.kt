package com.alorma.timeline

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup

class TimelineView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : ViewGroup(context, attrs, defStyleAttr) {

    companion object {
        private const val CIRCLE_SIZE = 30

        private const val V_ALIGN_TOP: Int = 0
        private const val V_ALIGN_CENTER: Int = 1
        private const val V_ALIGN_BOTTOM: Int = 2

        private const val V_ALIGN_DEFAULT: Int = V_ALIGN_CENTER

        private const val H_ALIGN_START: Int = 0
        private const val H_ALIGN_CENTER: Int = 1
        private const val H_ALIGN_END: Int = 2

        private const val H_ALIGN_DEFAULT: Int = H_ALIGN_CENTER
    }

    private val rect = Rect()

    private val paint = Paint().apply {
        isAntiAlias = true
        color = Color.BLUE
    }

    private val vAlign: Int
    private val hAlign: Int

    private var internalView: View? = null

    private val canvasDispatcherList: List<CanvasDispatcher>

    init {
        canvasDispatcherList = listOf(LinesCanvasDispatcher())

        val typedArray: TypedArray = getTypedArray(context, attrs, defStyleAttr)

        vAlign = typedArray.getInt(R.styleable.TimelineView_vAlign, V_ALIGN_DEFAULT)
        hAlign = typedArray.getInt(R.styleable.TimelineView_hAlign, H_ALIGN_DEFAULT)

        canvasDispatcherList.forEach {
            it.init()
        }
    }

    private fun getTypedArray(context: Context, attrs: AttributeSet?, defStyle: Int): TypedArray {
        return context.theme
                .obtainStyledAttributes(attrs, R.styleable.TimelineView, defStyle, 0)
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)

        canvas.getClipBounds(rect)

        val centerX = rect.centerX()
        val centerY = rect.centerY()

        val x = when (hAlign) {
            H_ALIGN_START -> ((centerY.toFloat()) / 3) + if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) paddingStart else paddingLeft
            H_ALIGN_END -> centerY + ((centerY.toFloat()) / 3) * 2 - if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) paddingEnd else paddingRight
            else -> centerX.toFloat()
        }
        val y = when (vAlign) {
            V_ALIGN_TOP -> ((centerX.toFloat()) / 3) + paddingTop
            V_ALIGN_BOTTOM -> centerX + ((centerX.toFloat()) / 3) * 2 - paddingBottom
            else -> centerY.toFloat()
        }.toFloat()

        canvas.drawCircle(x, y, (CIRCLE_SIZE / 2).toFloat(), paint)

        canvasDispatcherList.forEach {
            it.drawCanvas(canvas, this)
        }
    }


    override fun addView(child: View?, params: LayoutParams?) {
        if (internalView == null) {
            super.addView(child, params)
            internalView = child
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

    }
}