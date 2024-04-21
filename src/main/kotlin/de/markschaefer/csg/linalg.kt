package de.markschaefer.csg

import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class Vector(val x: Double, val y: Double) {
    constructor(x: Double, y: Double, z: Double) : this(x / z, y / z)

    override fun toString(): String = "($x, $y)"
    operator fun times(other: Vector) = dotProduct(arrayOf(x, y), arrayOf(other.x, other.y))
    operator fun times(scalar: Double) = Vector(x * scalar, y * scalar)
    operator fun plus(other: Vector) = Vector(x + other.x, y + other.y)
    operator fun minus(other: Vector) = Vector(x - other.x, y - other.y)

    fun crossProduct(other: Vector) = this.x * other.y - this.y * other.x

    fun normalised() = Vector(x, y, sqrt(x * x + y * y))

    fun angle(other: Vector) = atan2(this.crossProduct(other), this * other)
}

class Matrix(private vararg val entries: Double) {
    init {
        if (entries.size != 9) throw IllegalArgumentException("Matrix must contain exactly 9 entries (rowwise)")
    }

    operator fun times(other: Matrix): Matrix = Matrix(
        dotProduct(row(0), other.column(0)), dotProduct(row(0), other.column(1)), dotProduct(row(0), other.column(2)),
        dotProduct(row(1), other.column(0)), dotProduct(row(1), other.column(1)), dotProduct(row(1), other.column(2)),
        dotProduct(row(2), other.column(0)), dotProduct(row(2), other.column(1)), dotProduct(row(2), other.column(2)),
    )

    operator fun times(other: Vector): Vector {
        val b = arrayOf(other.x, other.y, 1.0)
        return Vector(dotProduct(row(0), b), dotProduct(row(1), b), dotProduct(row(2), b))
    }

    fun determinate() =
        entries[0] * (entries[4] * entries[8] - entries[5] * entries[7]) -
                entries[1] * (entries[3] * entries[8] - entries[5] * entries[6]) +
                entries[2] * (entries[3] * entries[7] - entries[4] * entries[6]);

    fun inverse(): Matrix {
        val determinate = determinate();
        if (determinate == 0.0) throw IllegalStateException("Cannot calculate inverse of matrix with determinate 0")

        val invDet = 1.0 / determinate

        return Matrix(
            (entries[4] * entries[8] - entries[5] * entries[7]) * invDet,  // Entry 0,0
            -(entries[1] * entries[8] - entries[2] * entries[7]) * invDet, // Entry 0,1
            (entries[1] * entries[5] - entries[2] * entries[4]) * invDet,  // Entry 0,2
            -(entries[3] * entries[8] - entries[5] * entries[6]) * invDet, // Entry 1,0
            (entries[0] * entries[8] - entries[2] * entries[6]) * invDet,  // Entry 1,1
            -(entries[0] * entries[5] - entries[2] * entries[3]) * invDet, // Entry 1,2
            (entries[3] * entries[7] - entries[4] * entries[6]) * invDet,  // Entry 2,0
            -(entries[0] * entries[7] - entries[1] * entries[6]) * invDet, // Entry 2,1
            (entries[0] * entries[4] - entries[1] * entries[3]) * invDet   // Entry 2,2
        )
    }

    override

    fun toString(): String {
        fun s(s: Double) = (if (s == 0.0) "" else s.toString()).padStart(5)

        return """
            / ${s(entries[0])} ${s(entries[1])} ${s(entries[2])} \  
            | ${s(entries[3])} ${s(entries[4])} ${s(entries[5])} |  
            \ ${s(entries[6])} ${s(entries[7])} ${s(entries[8])} /  
        """.trimIndent()
    }


    companion object {
        fun identity() = Matrix(1.0, 0.0, 0.0,/**/ 0.0, 1.0, 0.0,/**/ 0.0, 0.0, 1.0)
        fun mirrorX() = Matrix(-1.0, 0.0, 0.0,/**/ 0.0, 1.0, 0.0,/**/ 0.0, 0.0, 1.0)
        fun mirrorY() = Matrix(1.0, 0.0, 0.0,/**/ 0.0, -1.0, 0.0,/**/ 0.0, 0.0, 1.0)
        fun scale(scale: Double) = Matrix(scale, 0.0, 0.0,/**/ 0.0, scale, 0.0,/**/ 0.0, 0.0, 1.0)
        fun translate(x: Double, y: Double) = Matrix(1.0, 0.0, x,/**/ 0.0, 1.0, y,/**/ 0.0, 0.0, 1.0)
        fun rotate(angle: Double) = Matrix(cos(angle), sin(angle), 0.0,/**/ -sin(angle), cos(angle), 0.0,/**/ 0.0, 0.0, 1.0)
        fun rotateDegrees(angle: Int) = rotate(angle * 2 * PI / 360)
    }

    private fun row(n: Int) = arrayOf(entries[3 * n + 0], entries[3 * n + 1], entries[3 * n + 2])
    private fun column(n: Int) = arrayOf(entries[n + 0], entries[n + 3], entries[n + 6])

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Matrix

        return entries.contentEquals(other.entries)
    }

    override fun hashCode(): Int {
        return entries.contentHashCode()
    }
}


fun dotProduct(a: Array<Double>, b: Array<Double>) =
    if (a.size != b.size) throw IllegalArgumentException("Dot product needs an even number of parameters")
    else a.zip(b).sumOf { it.first * it.second }