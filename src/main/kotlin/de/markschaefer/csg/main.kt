package de.markschaefer.csg

import de.markschaefer.csg.Matrix.Companion.rotateDegrees
import de.markschaefer.csg.Matrix.Companion.translate

fun main() {
    val myShape =
        shape {
            loop {
                point(0.0, 0.0)
                point(2.0, 0.0)
                point(2.0, 2.0)
                point(0.0, 2.0)
            }

            loop {
                point(-1.0, -1.0)
                point(3.0, -1.0)
                point(3.0, 3.0)
                point(-1.0, 3.0)
            }

            loop {
                point(-0.5, 2.5)
                point(2.5, 2.5)
                point(2.5, -0.5)
                point(-0.5, -0.5)
            }

            for (x in 0..1) {
                for (y in 0..1) {
                    loop {
                        point(x + 0.25, y + 0.25)
                        point(x + 0.25, y + 0.75)
                        point(x + 0.75, y + 0.75)
                        point(x + 0.75, y + 0.25)
                    }

                }
            }
        }

    println(myShape)
    println(rotateDegrees(10))

    val mainWindow = MainWindow()
    val shape = rotateDegrees(30) * translate(-2.0, -1.0) * myShape
    mainWindow.shapeRenderer.addShape(myShape)
//    mainWindow.shapeRenderer.addShape(shape.boundingBox().toShape(), Color.green)
}