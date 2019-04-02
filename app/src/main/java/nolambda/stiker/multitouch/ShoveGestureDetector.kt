package nolambda.stiker.multitouch

import android.content.Context
import android.view.MotionEvent

/**
 * @author Robert Nordan (robert.nordan@norkart.no)
 *
 *
 * Copyright (c) 2013, Norkart AS
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
class ShoveGestureDetector(context: Context, private val mListener: OnShoveGestureListener) :
    TwoFingerGestureDetector(context) {
    private var mPrevAverageY: Float = 0.toFloat()
    private var mCurrAverageY: Float = 0.toFloat()
    private var mSloppyGesture: Boolean = false

    /**
     * Return the distance in pixels from the previous shove event to the current
     * event.
     *
     * @return The current distance in pixels.
     */
    val shovePixelsDelta: Float
        get() = mCurrAverageY - mPrevAverageY

    override fun handleStartProgressEvent(actionCode: Int, event: MotionEvent) {
        when (actionCode) {
            MotionEvent.ACTION_POINTER_DOWN -> {
                // At least the second finger is on screen now

                resetState() // In case we missed an UP/CANCEL event
                mPrevEvent = MotionEvent.obtain(event)
                timeDelta = 0

                updateStateByEvent(event)

                // See if we have a sloppy gesture
                mSloppyGesture = isSloppyGesture(event)
                if (!mSloppyGesture) {
                    // No, start gesture now
                    isInProgress = mListener.onShoveBegin(this)
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (!mSloppyGesture) {
                    return
                }

                // See if we still have a sloppy gesture
                mSloppyGesture = isSloppyGesture(event)
                if (!mSloppyGesture) {
                    // No, start normal gesture now
                    isInProgress = mListener.onShoveBegin(this)
                }
            }

            MotionEvent.ACTION_POINTER_UP -> if (!mSloppyGesture) {
                return
            }
        }
    }

    override fun handleInProgressEvent(actionCode: Int, event: MotionEvent) {
        when (actionCode) {
            MotionEvent.ACTION_POINTER_UP -> {
                // Gesture ended but
                updateStateByEvent(event)

                if (!mSloppyGesture) {
                    mListener.onShoveEnd(this)
                }

                resetState()
            }

            MotionEvent.ACTION_CANCEL -> {
                if (!mSloppyGesture) {
                    mListener.onShoveEnd(this)
                }

                resetState()
            }

            MotionEvent.ACTION_MOVE -> {
                updateStateByEvent(event)

                // Only accept the event if our relative pressure is within
                // a certain limit. This can help filter shaky data as a
                // finger is lifted. Also check that shove is meaningful.
                if (mCurrPressure / mPrevPressure > BaseGestureDetector.PRESSURE_THRESHOLD && Math.abs(shovePixelsDelta) > 0.5f) {
                    val updatePrevious = mListener.onShove(this)
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
        val py0 = prev!!.getY(0)
        val py1 = prev.getY(1)
        mPrevAverageY = (py0 + py1) / 2.0f

        val cy0 = curr.getY(0)
        val cy1 = curr.getY(1)
        mCurrAverageY = (cy0 + cy1) / 2.0f
    }

    override fun isSloppyGesture(event: MotionEvent): Boolean {
        val sloppy = super.isSloppyGesture(event)
        if (sloppy)
            return true

        // If it's not traditionally sloppy, we check if the angle between fingers
        // is acceptable.
        val angle = Math.abs(Math.atan2(mCurrFingerDiffY.toDouble(), mCurrFingerDiffX.toDouble()))
        //about 20 degrees, left or right
        return !(0.0f < angle && angle < 0.35f || 2.79f < angle && angle < Math.PI)
    }

    override fun resetState() {
        super.resetState()
        mSloppyGesture = false
        mPrevAverageY = 0.0f
        mCurrAverageY = 0.0f
    }

    /**
     * Listener which must be implemented which is used by ShoveGestureDetector
     * to perform callbacks to any implementing class which is registered to a
     * ShoveGestureDetector via the constructor.
     *
     * @see ShoveGestureDetector.SimpleOnShoveGestureListener
     */
    interface OnShoveGestureListener {
        fun onShove(detector: ShoveGestureDetector): Boolean

        fun onShoveBegin(detector: ShoveGestureDetector): Boolean

        fun onShoveEnd(detector: ShoveGestureDetector)
    }

    /**
     * Helper class which may be extended and where the methods may be
     * implemented. This way it is not necessary to implement all methods
     * of OnShoveGestureListener.
     */
    class SimpleOnShoveGestureListener : OnShoveGestureListener {
        override fun onShove(detector: ShoveGestureDetector): Boolean {
            return false
        }

        override fun onShoveBegin(detector: ShoveGestureDetector): Boolean {
            return true
        }

        override fun onShoveEnd(detector: ShoveGestureDetector) {
            // Do nothing, overridden implementation may be used
        }
    }
}
