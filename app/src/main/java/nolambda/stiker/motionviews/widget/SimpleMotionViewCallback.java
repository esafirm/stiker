package nolambda.stiker.motionviews.widget;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import nolambda.stiker.motionviews.widget.entity.MotionEntity;

public abstract class SimpleMotionViewCallback implements MotionViewCallback {
    @Override
    public void onEntityDoubleTap(@NonNull MotionEntity entity) {
    }

    @Override
    public void onEntitySelected(@Nullable MotionEntity entity) {
    }
}
