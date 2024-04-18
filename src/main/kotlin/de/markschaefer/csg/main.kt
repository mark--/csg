package de.markschaefer.csg

import de.markschaefer.csg.Matrix.Companion.rotate
import de.markschaefer.csg.Matrix.Companion.translate
import java.lang.Math.PI

fun main() {
    val square =
        shape {
            loop {
                point(0.0, 0.0)
                point(0.0, 1.0)
                point(1.0, 1.0)
                point(1.0, 0.0)
            }
            loop {
                point(0.25, 0.25)
                point(0.75, 0.25)
                point(0.75, 0.75)
                point(0.25, 0.75)
            }
        }

    println(square)
    println(rotate(10))
//    val translate = Matrix.translate(2.0, 3.0)
//    val scale = Matrix.scale(2.0)
//    val vector = Vector(1.0, 2.0)
//    println(scale * translate)
//    println(translate * vector)
//    println(scale * translate * vector)
//    println(translate * scale * vector)

    val mainWindow = MainWindow()
    mainWindow.shapeRenderer.addShape(rotate(30) * square)
    mainWindow.shapeRenderer.addShape(translate(-2.0, -1.0) * square)
    mainWindow.shapeRenderer.addShape(rotate(30) * translate(-2.0, -1.0) * square)
}