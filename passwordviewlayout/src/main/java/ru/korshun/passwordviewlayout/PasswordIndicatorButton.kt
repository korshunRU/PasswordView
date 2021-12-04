package ru.korshun.passwordviewlayout

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatButton
import ru.korshun.passwordviewlayout.utils.dpToPx

class PasswordIndicatorButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatButton(context, attrs) {

    private val size = 20

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val initSize = resolveDefaultSize()
        setMeasuredDimension(initSize, initSize)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

    }

    private fun resolveDefaultSize() : Int {
        return when(MeasureSpec.getMode(size)) {
            MeasureSpec.UNSPECIFIED -> context.dpToPx(size)
            MeasureSpec.AT_MOST -> MeasureSpec.getSize(size)
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(size)
            else -> MeasureSpec.getSize(size)
        }
    }

}