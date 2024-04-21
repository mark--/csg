package de.markschaefer.csg

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

}
