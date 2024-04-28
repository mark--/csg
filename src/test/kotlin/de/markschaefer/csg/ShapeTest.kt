package de.markschaefer.csg

import de.markschaefer.csg.Line.Companion.lineFromTo
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset.offset
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.math.PI
import kotlin.math.sqrt


fun Vector.isCloseTo(vector: Vector) = this.distance(vector) < 0.01
fun Vector.isCloseTo(x: Double, y: Double) = this.distance(Vector(x, y)) < 0.01


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ShapeTest {

    val square =
        shape {
            loop {
                point(0.0, 0.0)
                point(1.0, 0.0)
                point(1.0, 1.0)
                point(0.0, 1.0)
            }
        }

    val squareHole =
        shape {
            loop {
                point(0.0, 0.0)
                point(1.0, 0.0)
                point(1.0, 1.0)
                point(0.0, 1.0)
            }
            loop {
                point(0.25, 0.25)
                point(0.25, 0.75)
                point(0.75, 0.75)
                point(0.75, 0.25)
            }
        }

    @Test
    fun wind() {
        assertThat(square.windingNumber(Vector(0.01, 0.01))).isCloseTo(2 * PI, offset(0.01))
        assertThat(square.windingNumber(Vector(-0.01, -0.01))).isCloseTo(0.0, offset(0.01))
    }

    @ParameterizedTest
    @MethodSource("squareInside")
    fun `Point inside square`(point: Vector, inside: Boolean) {
        println(square.windingNumber(point) / 2 / PI)
        assertThat(square.contains(point)).isEqualTo(inside);
    }

    private fun squareInside(): Stream<Arguments> = Stream.of(
        arguments(Vector(0.01, 0.01), true),
        arguments(Vector(-0.01, -0.01), false),
        arguments(Vector(0.5, 0.5), true),
    )

    @ParameterizedTest
    @MethodSource("squareHoleInside")
    fun `Point inside squareHole`(point: Vector, inside: Boolean) {
        println(squareHole.windingNumber(point) / 2 / PI)
        assertThat(squareHole.contains(point)).isEqualTo(inside);
    }

    private fun squareHoleInside(): Stream<Arguments> = Stream.of(
        arguments(Vector(0.01, 0.01), true),
        arguments(Vector(-0.01, -0.01), false),
        arguments(Vector(0.5, 0.5), false),
        arguments(Vector(0.2, 0.2), true),
    )

    @Test
    fun scanlines() {

        //    /\  /\
        //   /  \/  \
        //   |      |
        //   --------
        val funkyShape = shape {
            loop {
                point(0.0, 0.0)
                point(4.0, 0.0)
                point(4.0, 1.0)
                point(3.0, 2.0)
                point(2.0, 1.0)
                point(1.0, 2.0)
                point(0.0, 1.0)
            }
        }

        val scanlines = funkyShape.scanlines(lineFromTo(Vector(0.0, 1.5), Vector(4.0, 1.5)))

        assertThat(scanlines).hasSize(2)
        assertThat(scanlines[0].start.isCloseTo(0.5, 1.5)).isTrue()
        assertThat(scanlines[0].end.isCloseTo(1.5, 1.5)).isTrue()
        assertThat(scanlines[1].start.isCloseTo(2.5, 1.5)).isTrue()
        assertThat(scanlines[1].end.isCloseTo(3.5, 1.5)).isTrue()
    }

}
