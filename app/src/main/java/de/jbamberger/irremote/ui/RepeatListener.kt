package de.jbamberger.irremote.ui

import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener


/**
 * This listener emulates a repeated clicking of a button. The listener is set as the
 * onTouchListener and calls the onClickListener of the view for every repetition.
 *
 * Based on https://stackoverflow.com/a/12795551/3103067

 * @param initialDelay Time delay of the first repetition following the initial click
 * @param repetitionDelay time delta between repetitions non-positive values skip this special
 * treatment of the first repeated click
 */
class RepeatListener(repetitionDelay: Int, initialDelay: Int = -1) : OnTouchListener {
    private val handler = Handler(Looper.getMainLooper())
    private val initialInterval: Int
    private val normalInterval: Int
    private var touchedView: View? = null
    private val handlerRunnable: Runnable = object : Runnable {
        override fun run() {
            if (touchedView!!.isPressed && touchedView!!.isEnabled) {
                handler.postDelayed(this, repetitionDelay.toLong())
                touchedView!!.performClick()
            } else {
                // if the view was disabled by the clickListener, remove the callback
                handler.removeCallbacks(this)
                touchedView?.isPressed = false
                touchedView = null
            }
        }
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                handler.removeCallbacks(handlerRunnable)
                handler.postDelayed(handlerRunnable, initialInterval.toLong())
                touchedView = view
                touchedView!!.isPressed = true
                view.performClick()
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                handler.removeCallbacks(handlerRunnable)
                touchedView?.isPressed = false
                touchedView = null
                return true
            }
        }
        return false
    }

    init {
        require(repetitionDelay > 0) { "Repetition delay must be positive." }

        this.initialInterval = if (initialDelay <= 0) repetitionDelay else initialDelay
        this.normalInterval = repetitionDelay
    }
}