package de.jbamberger.irremote

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.component1
import androidx.core.graphics.component2
import androidx.core.graphics.component3
import androidx.core.graphics.component4
import androidx.core.math.MathUtils.clamp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
public class RemoteBaseView(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
) : View(context, attrs, defStyleAttr, defStyleRes) {

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0, 0)
    constructor(context: Context) : this(context, null, 0, 0)

    companion object {
        const val BORDER_SIZE = 20F
    }


    private val buttons: MutableList<RectF> = ArrayList(128)
    private val groups: MutableList<ButtonGroup> = ArrayList(64)
    private val paint: Paint = Paint()
    private val selectedPaint = Paint()

    private val originalPos: RectF = RectF()
    private var selected: RectF? = null
    private var pointerDx = 0F
    private var pointerDy = 0F

    init {
        paint.color = Color.GREEN
        selectedPaint.color = Color.MAGENTA
        selectedPaint.alpha = 127
    }


    fun addBtn(sizeX: Float, sizeY: Float) {
        buttons.add(RectF(0F, 0F, 100F, 100F))
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val e = event ?: return super.onTouchEvent(event)
        return when (e.action) {
            MotionEvent.ACTION_DOWN -> handleActionDown(e)
            MotionEvent.ACTION_UP -> handleActionUp(e)
            MotionEvent.ACTION_CANCEL -> handleActionCancel(e)
            MotionEvent.ACTION_MOVE -> handleActionMove(e)
            else -> super.onTouchEvent(event)
        }
    }

    private fun handleActionDown(event: MotionEvent): Boolean {
        fun handleSelect(rect: RectF) {
            selected = rect
            originalPos.set(selected as RectF)
            pointerDx = event.x - rect.left
            pointerDy = event.y - rect.top
            invalidate()
        }

        if (selected != null) {
            return false
        }

        for (rect in buttons) {
            if (rect.contains(event.x, event.y)) {
                handleSelect(rect)
                return true
            }
        }
        return false
    }

    private fun handleActionUp(event: MotionEvent): Boolean {
        val s = selected ?: return false
        if (!trySnap(s)) {
            s.offsetTo(round(s.left / 100) * 100, round(s.top / 100) * 100)
        }
        originalPos.setEmpty()
        selected = null
        invalidate()
        return true
    }

    private fun handleActionCancel(event: MotionEvent): Boolean {
        if (selected != null) {
            selected!!.set(originalPos)
            originalPos.setEmpty()
            selected = null
            invalidate()
            return true
        }
        return false
    }

    private fun handleActionMove(event: MotionEvent): Boolean {
        val selected = this.selected ?: return false
        selected.offsetTo(clamp(event.x, 0f, width.toFloat()) - pointerDy, clamp(event.y, 0f, height.toFloat()) - pointerDy)
        invalidate()
        return true
    }

    private fun trySnap(selected: RectF): Boolean {
        for (button in buttons) {
            if (button.intersect(selected) && button !== selected) {
                snap(button, selected)
                return true
            }
        }
        return false
    }

    private fun snap(rect1: RectF, rect2: RectF) {
        buttons.remove(rect1)
        buttons.remove(rect2)
        groups.add(ButtonGroup(rect1, rect2))

    }

    override fun onDraw(canvas: Canvas?) {
        val surface = canvas ?: return super.onDraw(canvas)
        surface.drawColor(Color.BLUE)
        for (button in buttons) {
            surface.drawRect(button, paint)
        }
        for (group in groups) {
            group.draw(surface)
        }
        if (selected != null) {
            val (l, t, r, b) = selected!!
            surface.drawRect(l - BORDER_SIZE, t - BORDER_SIZE, r + BORDER_SIZE, b + BORDER_SIZE, selectedPaint)
        }

        super.onDraw(canvas)
    }

    class ButtonGroup(val b1: RectF, val b2: RectF) : RectF() {

        val outer = Paint()
        val inner = Paint()

        init {
            left = min(b1.left, b2.left) - BORDER_SIZE
            top = min(b1.top, b2.top) - BORDER_SIZE
            right = max(b1.right, b2.right) + BORDER_SIZE
            bottom = max(b1.bottom, b2.bottom) + BORDER_SIZE

            outer.color = Color.MAGENTA
            inner.color = Color.CYAN
            inner.alpha = 128
        }

        fun draw(canvas: Canvas) {
            canvas.drawRect(this, outer)
            canvas.drawRect(b1, inner)
            canvas.drawRect(b2, inner)
        }
    }

}