package de.markschaefer.csg

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

data class Vector(val x: Double, val y: Double) {
    override fun toString(): String = "($x, $y)"
    operator fun times(other: Vector) = dotProduct(arrayOf(x, y), arrayOf(other.x, other.y))
}

class Matrix(private vararg val entries: Double) {
    init {
        if (entries.size != 9) throw IllegalArgumentException("Matrix must contain exactly 9 entries (rowwise)")
    }

    operator fun times(other: Matrix): Matrix {
        val a = this
        val b = other

        return Matrix(
            dotProduct(a.row(0), b.column(0)), dotProduct(a.row(0), b.column(1)), dotProduct(a.row(0), b.column(2)),
            dotProduct(a.row(1), b.column(0)), dotProduct(a.row(1), b.column(1)), dotProduct(a.row(1), b.column(2)),
            dotProduct(a.row(2), b.column(0)), dotProduct(a.row(2), b.column(1)), dotProduct(a.row(2), b.column(2)),
        )
    }

    operator fun times(other: Vector): Vector {
        val a = this
        val b = arrayOf(other.x, other.y, 1.0)

        val z = dotProduct(a.row(3), b)

        val x = dotProduct(a.row(0), b) / z
        val y = dotProduct(a.row(1), b) / z

        return Vector(x, y)
    }

    override fun toString(): String {
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
        fun rotate(angle: Int) = rotate(angle * 2 * PI / 360)
    }

    private fun row(n: Int) = arrayOf(entries[3 * n + 0], entries[3 * n + 1], entries[3 * n + 2])
    private fun column(n: Int) = arrayOf(entries[n + 0], entries[n + 3], entries[n + 6])
}


fun dotProduct(a: Array<Double>, b: Array<Double>) =
    if (a.size != b.size) throw IllegalArgumentException("Dot product needs an even number of parameters")
    else a.zip(b).sumOf { it.first * it.second }