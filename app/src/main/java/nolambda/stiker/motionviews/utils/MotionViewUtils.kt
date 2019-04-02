package nolambda.stiker.motionviews.utils

object MotionViewUtils {
    fun getMatrixValues(matrix: android.graphics.Matrix): FloatArray {
        val matrixVal = FloatArray(9)
        matrix.getValues(matrixVal)
        return matrixVal
    }
}
