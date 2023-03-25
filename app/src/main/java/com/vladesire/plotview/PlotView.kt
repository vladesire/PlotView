package com.vladesire.plotview

import Computable
import Polynomial
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import spline1
import spline2
import kotlin.math.*

private const val TAG = "PlotView"

private fun generatePoints(n: Int, a: Float, b: Float, xOffset: Float, yOffset: Float, function: Computable): FloatArray {
    val points = mutableListOf<Float>()

    val step = (b-a) / (n - 1)

    for (i in 0 until n) {
        (a + i*step).also { points += it + xOffset }.also { points += (yOffset - function(it.toDouble()).toFloat()) }
    }

    return points.toFloatArray()
}

class PlotView(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    var functions: MutableList<(Float) -> Float> =
        mutableListOf(
            { x -> 100f*sin(0.01f*x) },
            { x -> 100f* cos((0.01f*x)) },
            { x -> 0.0004f*(x-100)*(x-200)*(x-300) }
        )

    private val backgroundPaint = Paint().apply {
        color = 0xffc4cdcf.toInt()
    }
    private val coordinateGridPaint = Paint().apply {
        color = 0xff000000.toInt()
    }
    private val pointsPaint = Paint().apply {
        color = 0xffff0000.toInt()
        strokeWidth = 2f
    }
    private val splinePaint = Paint().apply {
        color = 0xff1aa7ec.toInt()
        strokeWidth = 3f
    }

    private var pressPosition: PointF? = null
    private var prevOffset = PointF(0f, 0f)
    private var prevScale = 1f
    private var prevAngle = 0f


    private var offset = PointF(0f, 0f)
    private var scale: Float = 1f
    private var angle: Float = 0f

    private var rotationCenter = PointF(width/2f, height/2f)

    // Two fingers (tf) gesture variables
    private var tfMid: PointF? = null
    private var tfLength: Float? = null
    private var tfVector: PointF? = null

    private var secondFingerId: Int? = null

    fun restore() {
        prevOffset = PointF(0f, 0f)
        offset = PointF(0f, 0f)
        prevScale = 1f
        scale = 1f
        prevAngle = 0f
        angle = 0f
        rotationCenter = PointF(width/2f, height/2f)
        secondFingerId = null
        pressPosition = null

        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawPaint(backgroundPaint)

        // rotation coefficient
        val rot = if (angle != 0f) abs(sin(angle)) else 1f

        // TODO: Fix rotation of coordinate grid


        val coordinateGrid = floatArrayOf(
            (width/2f+offset.x), 0f, width/2f+offset.x, height/rot,
            0f, height/2f+offset.y, height.toFloat(), height/2f + offset.y
        )


        canvas.save()

        canvas.rotate(angle, rotationCenter.x, rotationCenter.y)

        canvas.drawLines(coordinateGrid, coordinateGridPaint)


        // TODO: ADD OFFSETS TO PLOTS

//        canvas.drawLines(generatePoints(200, 0f, 0f), pointsPaint)

        functions.forEach {
            canvas.drawPoints(generatePoints(1000, it), pointsPaint)
        }

        canvas.restore()

        //Log.d("PlotView", "${generatePoints(10000, width/2f+offset.x, height/2f + offset.y).toList()}")
//        val spline1 = spline1(8, -width/4f, width/4f, function)
//        val spline2 = spline2(50, -width/4f, width/4f, function)

//
//        canvas.drawPoints(generatePoints(10000, -width/4f, width/4f, width/2f+offset.x, height/2f+offset.y, spline2), splinePaint)
//        //splinePaint.color = 0xffffe45c.toInt()
//        canvas.drawPoints(generatePoints(10000, -width/4f, width/4f, width/2f, height/2f, spline1), splinePaint)

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        // Finger 1 position
        val pos1 = PointF(event.x, event.y)

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                pressPosition = pos1
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                if (secondFingerId == null) {
                    val index = event.actionIndex
                    secondFingerId = event.getPointerId(index)

                    val pos2 = PointF(event.getX(index), event.getY(index))

                    prevOffset = offset
                    tfVector = pos2 - pos1
                    tfMid = (pos1+pos2)/2f
                    rotationCenter = tfMid!!
                    tfLength = tfVector?.length()
                }
            }

            MotionEvent.ACTION_POINTER_UP -> {
                secondFingerId?.also { id ->
                    val index = event.findPointerIndex(id)
                    if (event.actionIndex == index ||
                        event.actionIndex == event.findPointerIndex(0)) {
                        secondFingerId = null
                        pressPosition = pos1
                        prevOffset = offset
                        prevAngle = angle
                        prevScale = scale

                        pressPosition = if (event.actionIndex == index) {
                            pos1
                        } else {
                            PointF(event.getX(index), event.getY(index))
                        }
                    }
                }
            }

            MotionEvent.ACTION_UP -> {
                prevOffset = offset

                pressPosition = null
                secondFingerId = null
                tfLength = null
                tfMid = null
                tfVector = null
            }
            MotionEvent.ACTION_MOVE -> {

                secondFingerId?.also { id ->

                    val index = event.findPointerIndex(id)
                    val pos2 = PointF(event.getX(index), event.getY(index))

                    val curTfVector = pos2 - pos1
                    val curTfLength = curTfVector.length()
                    val curTfMid = (pos1+pos2)/2f

                    scale = curTfLength / tfLength!! * prevScale
                    angle = abs(getAngle(tfVector!!, curTfVector)) + prevAngle
                    rotationCenter = curTfMid
                    offset = curTfMid - tfMid!! + prevOffset

                    Log.i(TAG, "offset: $offset, scale: $scale, angle: $angle); ${event.findPointerIndex(0)}")

                }

                if (secondFingerId == null) {
                    pressPosition?.let {
                        offset = (pos1 - it + prevOffset).rotate(angle)
                        Log.i(TAG, "offset no rotation: ${(pos1 - it) + prevOffset}")
                        Log.i(TAG, "offset with rotation: $offset")

                    }
                }
                updateView()
            }
        }


        return true
    }

    private fun updateView() {

        // If user touched anything

        invalidate()
    }

    private fun generatePoints(n: Int, function: (Float) -> Float): FloatArray {
        val points = mutableListOf<Float>()

        var start = -width/2f - offset.x
        val end = width/2f - offset.x
        val step = width / n

        while (start < end) {
            points += start + width/2f + offset.x
            try {
                points += height/2f + offset.y - scale*function(start/scale)
            } catch (ex: Exception) {
                points.removeLast()
            }
            start += step
        }

        return points.toFloatArray()
    }
}


fun getAngle(v1: PointF, v2: PointF): Float {
    val cosine = (v1.x*v2.x + v1.y*v2.y) / (v1.length() * v2.length())

    val sign = if (v2.x - v1.x/v1.y*v2.y > 0) -1f else 1f

    return sign * acos(cosine) * 180f / PI.toFloat()
}

operator fun PointF.minus(other: PointF): PointF {
    return PointF(this.x-other.x, this.y-other.y)
}
operator fun PointF.plus(other: PointF): PointF {
    return PointF(this.x+other.x, this.y+other.y)
}
operator fun PointF.div(num: Float): PointF {
    return PointF(this.x/num, this.y/num)
}

fun PointF.rotate(angle: Float): PointF {
    val point = PointF()
    point.x = this.x*cos(angle) - this.y*sin(angle)
    point.y = this.x*sin(angle) + this.y*cos(angle)
    return point
}