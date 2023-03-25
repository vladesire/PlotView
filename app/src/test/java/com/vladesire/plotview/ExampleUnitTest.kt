package com.vladesire.plotview

import android.graphics.PointF
import org.junit.Test

import org.junit.Assert.*
import kotlin.math.PI

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun rotate_isCorrect() {
        val p1 = PointF(1f, 2f)

        p1.rotate(PI.toFloat())

    }
}