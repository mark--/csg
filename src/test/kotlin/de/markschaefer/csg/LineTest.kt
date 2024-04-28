package de.markschaefer.csg

import de.markschaefer.csg.IntersectionResult.NoIntersection
import de.markschaefer.csg.IntersectionResult.Point
import de.markschaefer.csg.Line.Companion.lineFromTo
import de.markschaefer.csg.Vector.Companion.ORIGIN
import de.markschaefer.csg.Vector.Companion.X
import de.markschaefer.csg.Vector.Companion.Y
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LineTest {

    fun Vector.closeTo(other: Vector) = this.distance(other) < 0.01

    @Test
    fun `Parallel lines with no intersection`() {
        assertThat(lineFromTo(ORIGIN, X).intersection(lineFromTo(Y, X + Y))).isEqualTo(NoIntersection)
    }

    @Test
    fun `Lines with no intersection`() {
        assertThat(lineFromTo(ORIGIN, X).intersection(lineFromTo(Y, 2 * Y))).isEqualTo(NoIntersection)
    }

    @Test
    fun `Lines with intersection 1`() {
        val intersection = lineFromTo(-X, X).intersection(lineFromTo(-Y, Y))
        assertThat(intersection).isInstanceOf(Point::class.java)
        val point = intersection as Point
        assertThat(point.value.closeTo(ORIGIN))
    }

    @Test
    fun `Lines with intersection 2`() {
        val intersection = lineFromTo(-X + 0.2 * Y, X + 0.2 * Y).intersection(lineFromTo(-Y + 0.2 * X, Y + 0.2 * X))
        assertThat(intersection).isInstanceOf(Point::class.java)
        val point = intersection as Point
        assertThat(point.value.closeTo(Vector(0.2, 0.2)))
    }


}