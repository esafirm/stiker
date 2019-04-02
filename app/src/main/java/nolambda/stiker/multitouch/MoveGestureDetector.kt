package nolambda.stiker.multitouch

import android.content.Context
import android.graphics.PointF
import android.view.MotionEvent

/**
 * @author Almer Thie (code.almeros.com)
 * Copyright (c) 2013, Almer Thie (code.almeros.com)
 *
 *
 * All rights reserved.
 *
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the distribution.
 *
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
class MoveGestureDetector(context: Context, private val mListener: OnMoveGestureListener) :
    BaseGestureDetector(context) {
    private var mCurrFocusInternal: PointF? = null
    private var mPrevFocusInternal: PointF? = null
    private val mFocusExternal = PointF()
    var focusDelta = PointF()
        private set

    val focusX: Float
        get() = mFocusExternal.x

    val focusY: Float
        get() = mFocusExternal.y

    override fun handleStartProgressEvent(actionCode: Int, event: MotionEvent) {
        when (actionCode) {
            MotionEvent.ACTION_DOWN -> {
                resetState() // In case we missed an UP/CANCEL event

                mPrevEvent = MotionEvent.obtain(event)
                timeDelta = 0

                updateStateByEvent(event)
            }

            MotionEvent.ACTION_MOVE -> isInProgress = mListener.onMoveBegin(this)
        }
    }

    override fun handleInProgressEvent(actionCode: Int, event: MotionEvent) {
        when (actionCode) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mListener.onMoveEnd(this)
                resetState()
            }

            MotionEvent.ACTION_MOVE -> {
                updateStateByEvent(event)

                // Only accept the event if our relative pressure is within
                // a certain limit. This can help filter shaky data as a
                // finger is lifted.
                if (mCurrPressure / mPrevPressure > BaseGestureDetector.PRESSURE_THRESHOLD) {
                    val updatePrevious = mListener.onMove(this)
                    if (updatePrevious) {
                        mPrevEvent!!.recycle()
                        mPrevEvent = MotionEvent.obtain(event)
                    }
                }
            }
        }
    }

    override fun updateStateByEvent(curr: MotionEvent) {
        super.updateStateByEvent(curr)

        val prev = mPrevEvent

        // Focus intenal
        mCurrFocusInternal = determineFocalPoint(curr)
        mPrevFocusInternal = determineFocalPoint(prev!!)

        // Focus external
        // - Prevent skipping of focus delta when a finger is added or removed
        val mSkipNextMoveEvent = prev.pointerCount != curr.pointerCount
        focusDelta = if (mSkipNextMoveEvent) FOCUS_DELTA_ZERO else PointF(
            mCurrFocusInternal!!.x - mPrevFocusInternal!!.x,
            mCurrFocusInternal!!.y - mPrevFocusInternal!!.y
        )

        // - Don't directly use mFocusInternal (or skipping will occur). Add
        // 	 unskipped delta values to mFocusExternal instead.
        mFocusExternal.x += focusDelta.x
        mFocusExternal.y += focusDelta.y
    }

    /**
     * Determine (multi)finger focal point (a.k.a. center point between all
     * fingers)
     *
     * @param MotionEvent e
     * @return PointF focal point
     */
    private fun determineFocalPoint(e: MotionEvent): PointF {
        // Number of fingers on screen
        val pCount = e.pointerCount
        var x = 0f
        var y = 0f

        for (i in 0 until pCount) {
            x += e.getX(i)
            y += e.getY(i)
        }

        return PointF(x / pCount, y / pCount)
    }

    /**
     * Listener which must be implemented which is used by MoveGestureDetector
     * to perform callbacks to any implementing class which is registered to a
     * MoveGestureDetector via the constructor.
     *
     * @see MoveGestureDetector.SimpleOnMoveGestureListener
     */
    interface OnMoveGestureListener {
        fun onMove(detector: MoveGestureDetector): Boolean

        fun onMoveBegin(detector: MoveGestureDetector): Boolean

        fun onMoveEnd(detector: MoveGestureDetector)
    }

    /**
     * Helper class which may be extended and where the methods may be
     * implemented. This way it is not necessary to implement all methods
     * of OnMoveGestureListener.
     */
    open class SimpleOnMoveGestureListener : OnMoveGestureListener {
        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        override fun onMoveBegin(detector: MoveGestureDetector): Boolean {
            return true
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {
            // Do nothing, overridden implementation may be used
        }
    }

    companion object {

        private val FOCUS_DELTA_ZERO = PointF()
    }

}
