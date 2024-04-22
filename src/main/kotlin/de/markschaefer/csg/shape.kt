package de.markschaefer.csg

import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

data class BoundingBox(val xMin: Double, val yMin: Double, val xMax: Double, val yMax: Double) {
    fun with(point: Vector) =
        BoundingBox(min(xMin, point.x), min(yMin, point.y), max(xMax, point.x), max(yMax, point.y))

    fun with(other: BoundingBox) =
        BoundingBox(min(xMin, other.xMin), min(yMin, other.yMin), max(xMax, other.xMax), max(yMax, other.yMax))

    fun toShape() = shape {
        loop {
            point(xMin, yMin)
            point(xMax, yMin)
            point(xMax, yMax)
            point(xMin, yMax)
        }
    }

    fun grid(xResolution: Double = 1.0, yResolution: Double = 1.0): List<Vector> {
        val result = mutableListOf<Vector>()
        var x = xMin
        while (x <= xMax) {
            var y = yMin
            while (y <= yMax) {
                result.add(Vector(x, y))
                y += yResolution
            }
            x += xResolution
        }

        return result
    }

    companion object {
        fun from(point: Vector) = BoundingBox(point.x, point.y, point.x, point.y)
    }
}

data class Loop(val points: List<Vector>) {
    override fun toString() = "$points"

    fun windingNumber(point: Vector): Double {
        val diffs = points.map { it - point }.toMutableList()
        diffs.add(diffs[0])
        return diffs.zipWithNext().sumOf { it.first.angle(it.second) }
    }

    fun boundingBox() = points.drop(1).fold(BoundingBox.from(points[0]), BoundingBox::with)
}

data class Shape(val loops: List<Loop>) {
    override fun toString() = "$loops"

    fun contains(point: Vector): Boolean {
        return windingNumber(point).absoluteValue > 0.001;
    }

    fun windingNumber(point: Vector): Double {
        return loops.sumOf { it.windingNumber(point) };
    }

    fun boundingBox() = loops.drop(1).fold(loops[0].boundingBox()) { a, b -> a.with(b.boundingBox()) }

}

fun shape(init: ShapeBuilder.() -> Unit): Shape {
    val shapeBuilder = ShapeBuilder()
    shapeBuilder.init()
    return Shape(shapeBuilder.loops)
}

fun ShapeBuilder.loop(init: LoopBuilder.() -> Unit) {
    val loopBuilder = LoopBuilder()
    loopBuilder.init()
    loops.add(Loop(loopBuilder.vectors))
}

fun LoopBuilder.point(x: Double, y: Double) {
    vectors.add(Vector(x, y))
}

class LoopBuilder {
    val vectors: MutableList<Vector> = mutableListOf()
}

class ShapeBuilder {
    val loops: MutableList<Loop> = mutableListOf()
}

operator fun Matrix.times(shape: Shape) = Shape(shape.loops.map { this * it })

operator fun Matrix.times(loop: Loop): Loop = Loop(loop.points.map { this * it })