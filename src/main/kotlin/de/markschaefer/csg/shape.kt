package de.markschaefer.csg


data class Loop(val points: List<Vector>) {
    override fun toString() = "$points"
}

data class Shape(val loops: List<Loop>) {
    override fun toString() = "$loops"
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