package nolambda.stiker

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import nolambda.stiker.motionviews.utils.FontProvider
import nolambda.stiker.motionviews.widget.MotionView
import nolambda.stiker.motionviews.widget.entity.TextEntity

class MainActivity : AppCompatActivity() {

    private val motionView by lazy { createMotionView() }
    private val fontProvider by lazy { FontProvider(resources) }
    private val factory by lazy { EntityFactory(fontProvider) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            addView(motionView)
            addView(createButton().apply {
                text = "Add Image"
                setOnClickListener { onAddImage() }
            })
            addView(createButton().apply {
                text = "Add Text"
                setOnClickListener { onAddText() }
            })
        })
    }

    private fun onAddText() {
        motionView.addEntity(
            factory.createTextEntity(
                motionView, TextInputResult(
                    text = "Stringgg",
                    typeface = fontProvider.fontNames.first(),
                    color = Color.BLACK
                )
            )
        )
    }

    private fun onAddImage() {
        motionView.addEntity(
            factory.createImageEntity(motionView, R.mipmap.ic_launcher)
        )
    }

    private fun createButton() =
        Button(this).apply {
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        }

    private fun createMotionView() =
        MotionView(this).apply {
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                weight = 1F
            }
        }
}