package nolambda.stiker.motionviews.widget;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import nolambda.stiker.motionviews.widget.entity.MotionEntity;

public interface MotionViewCallback {
    void onEntitySelected(@Nullable MotionEntity entity);

    void onEntityDoubleTap(@NonNull MotionEntity entity);
}
