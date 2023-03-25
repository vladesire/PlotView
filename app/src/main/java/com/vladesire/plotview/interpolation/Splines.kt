import org.ejml.simple.SimpleMatrix

fun spline1(pointsNumber: Int): Spline {
    val points = partition(pointsNumber, A, B)
    val listY = mutableListOf<Double>()

    for (i in points) {
        listY += foo(i)
        listY += foo(i)
    }

    listY.removeFirst()
    listY.removeLast()

    val vectorY = SimpleMatrix(arrayOf(listY.toDoubleArray())).transpose()
    val matrixA = SimpleMatrix(2 * pointsNumber - 2, 2 * pointsNumber - 2)

    for (i in 0..pointsNumber - 2) {
        val block = SimpleMatrix(
            arrayOf(
                doubleArrayOf(points[i], 1.0),
                doubleArrayOf(points[i + 1], 1.0)
            )
        )

        matrixA.insertIntoThis(2 * i, 2 * i, block)
    }

    val coeffs = matrixA.solve(vectorY)
    val polynomials = mutableListOf<Polynomial>()

    for (i in 0 until coeffs.numRows() / 2) {
        polynomials += Polynomial(coeffs[2 * i], coeffs[2 * i + 1])
    }

    return Spline(polynomials, points)
}
fun spline2(pointsNumber: Int): Spline {
    val points = partition(pointsNumber, A, B)
    val listY = mutableListOf<Double>()

    for (i in 0..points.size-2) {
        listY += foo(points[i])
        listY += foo(points[i+1])
        listY += 0.0
    }

    val vectorY = SimpleMatrix(arrayOf(listY.toDoubleArray())).transpose()
    val matrixA = SimpleMatrix(3 * (pointsNumber - 1), 3 * (pointsNumber - 1))

    for (i in 0..pointsNumber - 2) {
        val block1 = SimpleMatrix(
            arrayOf(
                doubleArrayOf(points[i]*points[i], points[i], 1.0),
                doubleArrayOf(points[i+1]*points[i+1], points[i+1], 1.0)
            )
        )

        val block2 = SimpleMatrix(
            arrayOf(
                doubleArrayOf(2*points[i+1], 1.0, 0.0, -2.0*points[i+1], -1.0)
            )
        )

        if (i == pointsNumber - 2) {
            block2.reshape(1, 3)
        }

        matrixA.insertIntoThis(3 * i, 3 * i, block1)
        matrixA.insertIntoThis(3 * i + 2, 3 * i, block2)
    }

    val coeffs = matrixA.solve(vectorY)
    val polynomials = mutableListOf<Polynomial>()

    for (i in 0 until coeffs.numRows() / 3) {
        polynomials += Polynomial(coeffs[3*i], coeffs[3*i+1], coeffs[3*i+2])
    }

    return Spline(polynomials, points)
}
fun spline3(pointsNumber: Int): Spline {
    val points = partition(pointsNumber, A, B)
    val y = points.map { foo(it) }
    val h = mutableListOf<Double>()

    for (i in 0..pointsNumber-2) {
        h += points[i+1] - points[i]
    }

    val matrixH = SimpleMatrix(pointsNumber - 2, pointsNumber - 2)

    for (i in 0..pointsNumber-4) {
        matrixH[i, i] = 2 * (h[i] + h[i+1])
        matrixH[i+1, i] = h[i+1]
        matrixH[i, i+1] = h[i+1]
    }

    matrixH[pointsNumber-3, pointsNumber-3] = 2 * (h[pointsNumber-3] + h[pointsNumber-2])

    val g = mutableListOf<Double>()

    for (i in 1..pointsNumber-2) {
        g += 6 * ( (y[i+1] - y[i]) / h[i] - (y[i] - y[i-1]) / h[i-1])
    }

    val vectorYdd = matrixH.solve(
        SimpleMatrix(
            arrayOf(g.toDoubleArray())
        ).transpose()
    )

    val ydd = mutableListOf(0.0)
    for (i in 0 until vectorYdd.numRows()) {
        ydd += vectorYdd[i]
    }
    ydd += 0.0

    val yd = mutableListOf<Double>()

    for (i in 0..pointsNumber-2) {
        yd += (y[i+1]-y[i]) / h[i] - ydd[i+1]*h[i]/6.0 - ydd[i]*h[i]/3.0
    }

    val polynomials = mutableListOf<Polynomial>()

    for (i in 0..pointsNumber-2) {
        polynomials +=
                Polynomial(y[i]) +
                Polynomial(1.0, -points[i])*yd[i] +
                Polynomial(1.0, -points[i]).pow(2)*(ydd[i]/2.0) +
                Polynomial(1.0, -points[i]).pow(3)*( (ydd[i+1] - ydd[i]) / (6.0 * h[i]) )
    }

    return Spline(polynomials, points)
}


fun spline1(pointsNumber: Int, start: Float, end: Float, function: (Float) -> Float): Spline {
    val points = partition(pointsNumber, start.toDouble(), end.toDouble())
    val listY = mutableListOf<Double>()

    for (i in points) {
        listY += function(i.toFloat()).toDouble()
        listY += function(i.toFloat()).toDouble()
    }

    listY.removeFirst()
    listY.removeLast()

    val vectorY = SimpleMatrix(arrayOf(listY.toDoubleArray())).transpose()
    val matrixA = SimpleMatrix(2 * pointsNumber - 2, 2 * pointsNumber - 2)

    for (i in 0..pointsNumber - 2) {
        val block = SimpleMatrix(
            arrayOf(
                doubleArrayOf(points[i], 1.0),
                doubleArrayOf(points[i + 1], 1.0)
            )
        )

        matrixA.insertIntoThis(2 * i, 2 * i, block)
    }

    val coeffs = matrixA.solve(vectorY)
    val polynomials = mutableListOf<Polynomial>()

    for (i in 0 until coeffs.numRows() / 2) {
        polynomials += Polynomial(coeffs[2 * i], coeffs[2 * i + 1])
    }

    return Spline(polynomials, points)
}

fun spline2(pointsNumber: Int, start: Float, end: Float, function: (Float) -> Float): Spline {
    val points = partition(pointsNumber, start.toDouble(), end.toDouble())
    val listY = mutableListOf<Double>()

    for (i in 0..points.size-2) {
        listY += function(points[i].toFloat()).toDouble()
        listY += function(points[i+1].toFloat()).toDouble()
        listY += 0.0
    }

    val vectorY = SimpleMatrix(arrayOf(listY.toDoubleArray())).transpose()
    val matrixA = SimpleMatrix(3 * (pointsNumber - 1), 3 * (pointsNumber - 1))

    for (i in 0..pointsNumber - 2) {
        val block1 = SimpleMatrix(
            arrayOf(
                doubleArrayOf(points[i]*points[i], points[i], 1.0),
                doubleArrayOf(points[i+1]*points[i+1], points[i+1], 1.0)
            )
        )

        val block2 = SimpleMatrix(
            arrayOf(
                doubleArrayOf(2*points[i+1], 1.0, 0.0, -2.0*points[i+1], -1.0)
            )
        )

        if (i == pointsNumber - 2) {
            block2.reshape(1, 3)
        }

        matrixA.insertIntoThis(3 * i, 3 * i, block1)
        matrixA.insertIntoThis(3 * i + 2, 3 * i, block2)
    }

    val coeffs = matrixA.solve(vectorY)
    val polynomials = mutableListOf<Polynomial>()

    for (i in 0 until coeffs.numRows() / 3) {
        polynomials += Polynomial(coeffs[3*i], coeffs[3*i+1], coeffs[3*i+2])
    }

    return Spline(polynomials, points)
}
