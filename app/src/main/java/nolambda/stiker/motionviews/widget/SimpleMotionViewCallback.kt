package nolambda.stiker.motionviews.widget

import nolambda.stiker.motionviews.widget.entity.MotionEntity

abstract class SimpleMotionViewCallback : MotionViewCallback {
    override fun onEntityDoubleTap(entity: MotionEntity) {}

    override fun onEntitySelected(entity: MotionEntity?) {}
}
