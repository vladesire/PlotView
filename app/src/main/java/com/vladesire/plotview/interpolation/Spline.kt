class Spline(
    private val polynomials: List<Polynomial>,
    private val points: List<Double>
) : Computable {

    override fun toString(): String {
        var str = ""
        polynomials.forEachIndexed { ind, pol ->
            str += "[${points[ind]}, ${points[ind+1]}] = $pol\n"
        }
        return str
    }
    fun toDesmosString(): String {
        var str = ""
        polynomials.forEachIndexed { ind, pol ->
            str += "${pol.toDesmosString()} \\left\\{${points[ind]} \\le x \\le ${points[ind+1]}\\right\\}\n"
        }
        return str
    }

    override operator fun invoke(x: Double): Double {
        val index = points.indexOfLast { it < x }.let {
            when (it) {
                -1 -> 0
                points.size-1 -> it-2
                else -> it
            }
        }
        return polynomials[index](x)
    }
}