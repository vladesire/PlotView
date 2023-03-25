fun dividedDifferences(points: List<Double>): List<Double> {
    val n = points.size
    val divDiffs = mutableListOf<MutableList<Double>>()

    divDiffs += points.map { foo(it) }.toMutableList()

    for (i in 1 until n){
        divDiffs += mutableListOf<Double>()
        for (j in 0 until n-i) {
            divDiffs[i] += ( (divDiffs[i-1][j+1] - divDiffs[i-1][j]) / (points[j+i] - points[j]) )
        }
    }

    divDiffs.forEach { println(it) }

    return divDiffs.map { it[0] }
}

fun newton(n: Int, optimal: Boolean = false): Polynomial {
    val points = if (optimal) partitionOptimal(n, A, B) else partition(n, A, B)
    val divDiffs = dividedDifferences(points)

    val result = Polynomial(divDiffs[0])
    val pol = Polynomial(1.0)

    for (i in 0 until n-1) {
        pol *= Polynomial(1.0, -points[i])
        result += pol * divDiffs[i+1]
    }

    return result
}