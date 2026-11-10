package com.cse489.medialab

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.RectF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.min

/** Internet image view with two-finger pinch, panning and double-tap to reset. */
class ZoomableImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {
    private val transform = Matrix()
    private val values = FloatArray(9)
    private var minFitScale = 1f
    private var lastX = 0f
    private var lastY = 0f

    private val scaleDetector = ScaleGestureDetector(context,
        object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                if (drawable == null) return false
                transform.getValues(values)
                val current = values[Matrix.MSCALE_X].coerceAtLeast(0.001f)
                val target = (current * detector.scaleFactor)
                    .coerceIn(minFitScale, minFitScale * 5f)
                transform.postScale(target / current, target / current, detector.focusX, detector.focusY)
                keepInBounds()
                imageMatrix = transform
                return true
            }
        }
    )

    private val gestures = GestureDetector(context,
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent): Boolean = true
            override fun onDoubleTap(e: MotionEvent): Boolean {
                transform.getValues(values)
                val zoom = values[Matrix.MSCALE_X] / minFitScale
                if (zoom > 1.05f) resetZoom() else {
                    transform.postScale(2.4f, 2.4f, e.x, e.y)
                    keepInBounds()
                    imageMatrix = transform
                }
                return true
            }
        }
    )

    init { scaleType = ScaleType.MATRIX }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        if (width > 0 && height > 0) resetZoom() else post { resetZoom() }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0) resetZoom()
    }

    fun resetZoom() {
        val image = drawable ?: return
        if (width <= 0 || height <= 0 || image.intrinsicWidth <= 0 || image.intrinsicHeight <= 0) return
        minFitScale = min(
            width.toFloat() / image.intrinsicWidth,
            height.toFloat() / image.intrinsicHeight
        )
        transform.reset()
        transform.postScale(minFitScale, minFitScale)
        transform.postTranslate(
            (width - image.intrinsicWidth * minFitScale) / 2f,
            (height - image.intrinsicHeight * minFitScale) / 2f
        )
        imageMatrix = transform
    }

    private fun keepInBounds() {
        val image = drawable ?: return
        val box = RectF(0f, 0f, image.intrinsicWidth.toFloat(), image.intrinsicHeight.toFloat())
        transform.mapRect(box)
        val deltaX = if (box.width() <= width) {
            width / 2f - box.centerX()
        } else when {
            box.left > 0f -> -box.left
            box.right < width -> width - box.right
            else -> 0f
        }
        val deltaY = if (box.height() <= height) {
            height / 2f - box.centerY()
        } else when {
            box.top > 0f -> -box.top
            box.bottom < height -> height - box.bottom
            else -> 0f
        }
        transform.postTranslate(deltaX, deltaY)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (drawable == null) return super.onTouchEvent(event)
        parent?.requestDisallowInterceptTouchEvent(true)
        scaleDetector.onTouchEvent(event)
        gestures.onTouchEvent(event)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                lastX = event.x
                lastY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                if (event.pointerCount == 1 && !scaleDetector.isInProgress) {
                    transform.postTranslate(event.x - lastX, event.y - lastY)
                    keepInBounds()
                    imageMatrix = transform
                }
                lastX = event.x
                lastY = event.y
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                performClick()
                parent?.requestDisallowInterceptTouchEvent(false)
            }
        }
        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
}
