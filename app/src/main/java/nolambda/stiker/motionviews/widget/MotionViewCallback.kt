package nolambda.stiker.motionviews.widget

import nolambda.stiker.motionviews.widget.entity.MotionEntity

interface MotionViewCallback {
    fun onEntitySelected(entity: MotionEntity?)
    fun onEntityDoubleTap(entity: MotionEntity)
}
