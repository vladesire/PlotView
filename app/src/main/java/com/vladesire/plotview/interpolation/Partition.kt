import kotlin.math.PI
import kotlin.math.cos

fun partition(n: Int, a: Double, b: Double): List<Double> {
    val points = mutableListOf<Double>()

    val step = (b-a) / (n - 1)

    for (i in 0 until n) {
        points += ( a + i*step )
    }

    return points
}

fun partitionOptimal(n: Int, a: Double, b: Double): List<Double> {
    val points = mutableListOf<Double>()

    // Not 2(n+1) in denominator as I exclude n

    for (i in 0 until n) {
        points += ( 0.5 * ((b-a) * cos((2.0*i+1) / (2.0*n) * PI) + (b+a)) )
    }

    return points
}
