package nolambda.stiker.motionviews.utils;

public class MotionViewUtils {
    public static float[] getMatrixValues(android.graphics.Matrix matrix) {
        final float[] matrixVal = new float[9];
        matrix.getValues(matrixVal);
        return matrixVal;
    }
}
