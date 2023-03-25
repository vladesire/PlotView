import kotlin.math.pow

private fun MutableList<Double>.initZero(size: Int): MutableList<Double> {
    this.clear()
    for (i in 1..size)
        this += 0.0
    return this
}

class Polynomial (
    private var coeffs: List<Double>
) : Computable {

    init {
        // Coefficients are stored in reversed order to simplify calculations
        coeffs = coeffs.reversed()
    }

    constructor(vararg coefficients: Double) : this(coefficients.toList())

    override fun toString(): String {
        return coeffs.reversed().toString()
    }

    fun toDesmosString(): String {
        var str = ""
        coeffs.forEachIndexed { index, d -> str += String.format("%.15f*x^{$index} + ", d)}
        return str.removeSuffix(" + ")
    }

    fun pow(n: Int): Polynomial {
        val res = Polynomial(1.0)
        repeat(n) { res *= this }
        return res
    }

    operator fun times(num: Double): Polynomial {
        return Polynomial(coeffs.map { it*num }.reversed())
    }
    operator fun timesAssign(num: Double) {
        coeffs = this.times(num).coeffs
    }
    operator fun unaryMinus(): Polynomial {
        return this.times(-1.0)
    }
    operator fun div(num: Double): Polynomial {
        return this.times(1.0/num)
    }
    operator fun divAssign(num: Double) {
        coeffs = this.div(num).coeffs
    }
    operator fun plus(other: Polynomial): Polynomial {
        val newCoeffs: MutableList<Double>

        if (coeffs.size > other.coeffs.size) {
            newCoeffs = coeffs.toMutableList()
            other.coeffs.forEachIndexed { index, d -> newCoeffs[index] += d }
        } else {
            newCoeffs = other.coeffs.toMutableList()
            coeffs.forEachIndexed { index, d -> newCoeffs[index] += d }
        }

        return Polynomial(newCoeffs.reversed())
    }
    operator fun plusAssign(other: Polynomial) {
        coeffs = this.plus(other).coeffs
    }
    operator fun minus(other: Polynomial): Polynomial {
        return this.plus(-other)
    }
    operator fun times(other: Polynomial): Polynomial {
        val c1 = coeffs
        val c2 = other.coeffs
        val newCoeffs = mutableListOf<Double>().initZero(c1.size + c2.size - 1)

        c1.forEachIndexed { index1, d1 ->
            c2.forEachIndexed { index2, d2 ->
                newCoeffs[index1+index2] += d1*d2
            }
        }

        return Polynomial(newCoeffs.reversed())
    }
    operator fun timesAssign(other: Polynomial) {
        coeffs = this.times(other).coeffs
    }

    override operator fun invoke(x: Double) =
        coeffs.foldIndexed(0.0) {ind, sum, elem -> sum + (elem * x.pow(ind))}

}
