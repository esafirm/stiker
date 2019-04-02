package nolambda.stiker.multitouch

import android.content.Context
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.ViewConfiguration

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
abstract class TwoFingerGestureDetector(context: Context) : BaseGestureDetector(context) {

    private val mEdgeSlop: Float
    protected var mPrevFingerDiffX: Float = 0.toFloat()
    protected var mPrevFingerDiffY: Float = 0.toFloat()
    protected var mCurrFingerDiffX: Float = 0.toFloat()
    protected var mCurrFingerDiffY: Float = 0.toFloat()
    private var mRightSlopEdge: Float = 0.toFloat()
    private var mBottomSlopEdge: Float = 0.toFloat()
    private var mCurrLen: Float = 0.toFloat()
    private var mPrevLen: Float = 0.toFloat()

    /**
     * Return the current distance between the two pointers forming the
     * gesture in progress.
     *
     * @return Distance between pointers in pixels.
     */
    val currentSpan: Float
        get() {
            if (mCurrLen == -1f) {
                val cvx = mCurrFingerDiffX
                val cvy = mCurrFingerDiffY
                mCurrLen = Math.sqrt((cvx * cvx + cvy * cvy).toDouble()).toFloat()
            }
            return mCurrLen
        }

    /**
     * Return the previous distance between the two pointers forming the
     * gesture in progress.
     *
     * @return Previous distance between pointers in pixels.
     */
    val previousSpan: Float
        get() {
            if (mPrevLen == -1f) {
                val pvx = mPrevFingerDiffX
                val pvy = mPrevFingerDiffY
                mPrevLen = Math.sqrt((pvx * pvx + pvy * pvy).toDouble()).toFloat()
            }
            return mPrevLen
        }

    init {

        val config = ViewConfiguration.get(context)
        mEdgeSlop = config.scaledEdgeSlop.toFloat()
    }

    abstract override fun handleStartProgressEvent(actionCode: Int, event: MotionEvent)

    abstract override fun handleInProgressEvent(actionCode: Int, event: MotionEvent)

    override fun updateStateByEvent(curr: MotionEvent) {
        super.updateStateByEvent(curr)

        val prev = mPrevEvent

        mCurrLen = -1f
        mPrevLen = -1f

        // Previous
        val px0 = prev!!.getX(0)
        val py0 = prev.getY(0)
        val px1 = prev.getX(1)
        val py1 = prev.getY(1)
        val pvx = px1 - px0
        val pvy = py1 - py0
        mPrevFingerDiffX = pvx
        mPrevFingerDiffY = pvy

        // Current
        val cx0 = curr.getX(0)
        val cy0 = curr.getY(0)
        val cx1 = curr.getX(1)
        val cy1 = curr.getY(1)
        val cvx = cx1 - cx0
        val cvy = cy1 - cy0
        mCurrFingerDiffX = cvx
        mCurrFingerDiffY = cvy
    }

    /**
     * Check if we have a sloppy gesture. Sloppy gestures can happen if the edge
     * of the user's hand is touching the screen, for example.
     *
     * @param event
     * @return
     */
    protected open fun isSloppyGesture(event: MotionEvent): Boolean {
        // As orientation can change, query the metrics in touch down
        val metrics = mContext.resources.displayMetrics
        mRightSlopEdge = metrics.widthPixels - mEdgeSlop
        mBottomSlopEdge = metrics.heightPixels - mEdgeSlop

        val edgeSlop = mEdgeSlop
        val rightSlop = mRightSlopEdge
        val bottomSlop = mBottomSlopEdge

        val x0 = event.rawX
        val y0 = event.rawY
        val x1 = getRawX(event, 1)
        val y1 = getRawY(event, 1)

        val p0sloppy = (x0 < edgeSlop || y0 < edgeSlop
                || x0 > rightSlop || y0 > bottomSlop)
        val p1sloppy = (x1 < edgeSlop || y1 < edgeSlop
                || x1 > rightSlop || y1 > bottomSlop)

        if (p0sloppy && p1sloppy) {
            return true
        } else if (p0sloppy) {
            return true
        } else if (p1sloppy) {
            return true
        }
        return false
    }

    companion object {

        /**
         * MotionEvent has no getRawX(int) method; simulate it pending future API approval.
         *
         * @param event
         * @param pointerIndex
         * @return
         */
        protected fun getRawX(event: MotionEvent, pointerIndex: Int): Float {
            val offset = event.x - event.rawX
            return if (pointerIndex < event.pointerCount) {
                event.getX(pointerIndex) + offset
            } else 0f
        }

        /**
         * MotionEvent has no getRawY(int) method; simulate it pending future API approval.
         *
         * @param event
         * @param pointerIndex
         * @return
         */
        protected fun getRawY(event: MotionEvent, pointerIndex: Int): Float {
            val offset = event.y - event.rawY
            return if (pointerIndex < event.pointerCount) {
                event.getY(pointerIndex) + offset
            } else 0f
        }
    }

}
