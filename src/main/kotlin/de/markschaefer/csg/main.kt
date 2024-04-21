package de.markschaefer.csg

import de.markschaefer.csg.Matrix.Companion.rotateDegrees
import de.markschaefer.csg.Matrix.Companion.translate
import java.awt.Color

fun main() {
    val square =
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

    println(square)
    println(rotateDegrees(10))

    val mainWindow = MainWindow()
    val shape = rotateDegrees(30) * translate(-2.0, -1.0) * square
    mainWindow.shapeRenderer.addShape(square)
//    mainWindow.shapeRenderer.addShape(shape.boundingBox().toShape(), Color.green)
}