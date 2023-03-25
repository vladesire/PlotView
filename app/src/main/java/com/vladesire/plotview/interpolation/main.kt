import kotlin.math.*

fun foo(x: Double) = sin(ln(x))
const val A = 0.04
const val B = 2.0

// 11. f(x) = cot(x) + x^2
//fun foo(x: Double) = 1.0 / tan(x) + x*x
//const val A = 0.1
//const val B = 3.1

fun analyzeErrors(nodes: Int, points: Int) {
    val testPoints = partition(points, A, B)
    val cases = mutableMapOf<Double, String>()

    cases += maxError(lagrange(nodes, false), testPoints) to "L   "
    cases += maxError(lagrange(nodes, true), testPoints) to "LOpt"
    cases += maxError(newton(nodes, false), testPoints)  to "N   "
    cases += maxError(newton(nodes, true), testPoints) to "NOpt"
    cases += maxError(spline1(nodes), testPoints) to "S1  "
    cases += maxError(spline2(nodes), testPoints) to "S2  "
    cases += maxError(spline3(nodes), testPoints) to "S3  "

    println("Error ($nodes, $points):")
    cases.forEach { (k, v) ->  println("\t$v = $k") }
}
fun maxError(function: Computable, points: List<Double>): Double {
    var max = 0.0
    for (i in points) {
        (abs(function(i) - foo(i))).let { if (it > max) max = it }
    }
    return max
}

fun main() {
    for (i in 3..100) {
//          analyzeErrors(i, 500)
    }

    var points = partition(5, 1.0, 3.0)
    println(points)
    println(dividedDifferences(points))
}