package nolambda.stiker.multitouch

import android.content.Context
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
abstract class BaseGestureDetector(protected val mContext: Context) {
    /**
     * Returns `true` if a gesture is currently in progress.
     *
     * @return `true` if a gesture is currently in progress, `false` otherwise.
     */
    var isInProgress: Boolean = false
        protected set
    protected var mPrevEvent: MotionEvent? = null
    protected var mCurrEvent: MotionEvent? = null
    protected var mCurrPressure: Float = 0.toFloat()
    protected var mPrevPressure: Float = 0.toFloat()
    /**
     * Return the time difference in milliseconds between the previous accepted
     * GestureDetector event and the current GestureDetector event.
     *
     * @return Time difference since the last move event in milliseconds.
     */
    var timeDelta: Long = 0
        protected set

    /**
     * Return the event time of the current GestureDetector event being
     * processed.
     *
     * @return Current GestureDetector event time in milliseconds.
     */
    val eventTime: Long
        get() = mCurrEvent!!.eventTime

    /**
     * All gesture detectors need to be called through this method to be able to
     * detect gestures. This method delegates work to handler methods
     * (handleStartProgressEvent, handleInProgressEvent) implemented in
     * extending classes.
     *
     * @param event
     * @return
     */
    fun onTouchEvent(event: MotionEvent): Boolean {
        val actionCode = event.action and MotionEvent.ACTION_MASK
        if (!isInProgress) {
            handleStartProgressEvent(actionCode, event)
        } else {
            handleInProgressEvent(actionCode, event)
        }
        return true
    }

    /**
     * Called when the current event occurred when NO gesture is in progress
     * yet. The handling in this implementation may set the gesture in progress
     * (via mGestureInProgress) or out of progress
     *
     * @param actionCode
     * @param event
     */
    protected abstract fun handleStartProgressEvent(actionCode: Int, event: MotionEvent)

    /**
     * Called when the current event occurred when a gesture IS in progress. The
     * handling in this implementation may set the gesture out of progress (via
     * mGestureInProgress).
     *
     * @param actionCode
     * @param event
     */
    protected abstract fun handleInProgressEvent(actionCode: Int, event: MotionEvent)


    protected open fun updateStateByEvent(curr: MotionEvent) {
        val prev = mPrevEvent

        // Reset mCurrEvent
        if (mCurrEvent != null) {
            mCurrEvent!!.recycle()
            mCurrEvent = null
        }
        mCurrEvent = MotionEvent.obtain(curr)


        // Delta time
        timeDelta = curr.eventTime - prev!!.eventTime

        // Pressure
        mCurrPressure = curr.getPressure(curr.actionIndex)
        mPrevPressure = prev.getPressure(prev.actionIndex)
    }

    protected open fun resetState() {
        if (mPrevEvent != null) {
            mPrevEvent!!.recycle()
            mPrevEvent = null
        }
        if (mCurrEvent != null) {
            mCurrEvent!!.recycle()
            mCurrEvent = null
        }
        isInProgress = false
    }

    companion object {
        /**
         * This value is the threshold ratio between the previous combined pressure
         * and the current combined pressure. When pressure decreases rapidly
         * between events the position values can often be imprecise, as it usually
         * indicates that the user is in the process of lifting a pointer off of the
         * device. This value was tuned experimentally.
         */
        const val PRESSURE_THRESHOLD = 0.67f
    }

}
