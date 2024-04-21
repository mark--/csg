package de.markschaefer.csg

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset.offset
import org.junit.jupiter.api.Test
import kotlin.math.PI

class VectorTest {


    @Test
    fun `Angle`() {
        assertThat(Vector(1.0, 0.0).angle(Vector(0.0, 1.0))).isCloseTo(PI / 2, offset(0.01))
        assertThat(Vector(1.0, 1.0).angle(Vector(0.0, 1.0))).isCloseTo(PI / 4, offset(0.01))
        assertThat(Vector(0.0, -1.0).angle(Vector(0.0, 1.0))).isCloseTo(PI, offset(0.01))
        assertThat(Vector(1.0, 0.0).angle(Vector(-1.0, 1.0))).isCloseTo(0.75 * PI, offset(0.01))

        assertThat(Vector(0.0, 1.0).angle(Vector(1.0, 0.0))).isCloseTo(-PI / 2, offset(0.01))
        assertThat(Vector(0.0, 1.0).angle(Vector(0.0, -1.0))).isCloseTo(-PI, offset(0.01))
    }


}