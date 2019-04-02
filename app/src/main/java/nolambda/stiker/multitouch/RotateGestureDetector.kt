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
class RotateGestureDetector(context: Context, private val mListener: OnRotateGestureListener) :
    TwoFingerGestureDetector(context) {
    private var mSloppyGesture: Boolean = false

    /**
     * Return the rotation difference from the previous rotate event to the current
     * event.
     *
     * @return The current rotation //difference in degrees.
     */
    val rotationDegreesDelta: Float
        get() {
            val diffRadians = Math.atan2(
                mPrevFingerDiffY.toDouble(),
                mPrevFingerDiffX.toDouble()
            ) - Math.atan2(mCurrFingerDiffY.toDouble(), mCurrFingerDiffX.toDouble())
            return (diffRadians * 180 / Math.PI).toFloat()
        }

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
                    isInProgress = mListener.onRotateBegin(this)
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
                    isInProgress = mListener.onRotateBegin(this)
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
                    mListener.onRotateEnd(this)
                }

                resetState()
            }

            MotionEvent.ACTION_CANCEL -> {
                if (!mSloppyGesture) {
                    mListener.onRotateEnd(this)
                }

                resetState()
            }

            MotionEvent.ACTION_MOVE -> {
                updateStateByEvent(event)

                // Only accept the event if our relative pressure is within
                // a certain limit. This can help filter shaky data as a
                // finger is lifted.
                if (mCurrPressure / mPrevPressure > BaseGestureDetector.PRESSURE_THRESHOLD) {
                    val updatePrevious = mListener.onRotate(this)
                    if (updatePrevious) {
                        mPrevEvent!!.recycle()
                        mPrevEvent = MotionEvent.obtain(event)
                    }
                }
            }
        }
    }

    override fun resetState() {
        super.resetState()
        mSloppyGesture = false
    }

    /**
     * Listener which must be implemented which is used by RotateGestureDetector
     * to perform callbacks to any implementing class which is registered to a
     * RotateGestureDetector via the constructor.
     *
     * @see RotateGestureDetector.SimpleOnRotateGestureListener
     */
    interface OnRotateGestureListener {
        fun onRotate(detector: RotateGestureDetector): Boolean

        fun onRotateBegin(detector: RotateGestureDetector): Boolean

        fun onRotateEnd(detector: RotateGestureDetector)
    }

    /**
     * Helper class which may be extended and where the methods may be
     * implemented. This way it is not necessary to implement all methods
     * of OnRotateGestureListener.
     */
    open class SimpleOnRotateGestureListener : OnRotateGestureListener {
        override fun onRotate(detector: RotateGestureDetector): Boolean {
            return false
        }

        override fun onRotateBegin(detector: RotateGestureDetector): Boolean {
            return true
        }

        override fun onRotateEnd(detector: RotateGestureDetector) {
            // Do nothing, overridden implementation may be used
        }
    }
}
