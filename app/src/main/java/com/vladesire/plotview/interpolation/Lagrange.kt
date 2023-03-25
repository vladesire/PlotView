fun lagrange(n: Int, optimal: Boolean = false): Polynomial {
    val points = if (optimal) partitionOptimal(n, A, B) else partition(n, A, B)
    val result = Polynomial(0.0)

    for (i in 0 until  n) {
        val l = Polynomial(1.0)
        var coeff = 1.0

        for (k in 0 until n) {
            if (i != k) {
                l *= Polynomial(1.0, -points[k])
                coeff *= (points[i] - points[k])
            }
        }

        l *= ( foo(points[i]) / coeff )

        result += l
    }

    return result
}