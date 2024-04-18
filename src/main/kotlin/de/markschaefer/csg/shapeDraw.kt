package de.markschaefer.csg

import de.markschaefer.csg.Matrix.Companion.identity
import de.markschaefer.csg.Matrix.Companion.mirrorY
import de.markschaefer.csg.Matrix.Companion.scale
import de.markschaefer.csg.Matrix.Companion.translate
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JSlider
import javax.swing.SwingUtilities
import kotlin.math.max
import kotlin.math.min


class MainWindow : JFrame("CSG") {
    val shapeRenderer = ShapeRenderer()

    private var zoom = 10;

    init {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE)
        setSize(800, 600)
        setLocationRelativeTo(null)
        isVisible = true


        layout = BorderLayout(10, 10)

        val menu = JPanel()
        add(menu, BorderLayout.NORTH)


        addMouseWheelListener { event -> updateZoom(zoom + event.wheelRotation) }

        updateZoom(zoom)

        add(shapeRenderer, BorderLayout.CENTER)
        pack()
    }

    private fun updateZoom(zoom: Int) {
        this.zoom = max(min(zoom, 100), 5);
        shapeRenderer.matrix = translate(400.0, 300.0) * scale(this.zoom.toDouble())
        shapeRenderer.repaint()
    }
}

class ShapeRenderer : JPanel() {
    private val shapes = mutableListOf<Shape>()

    var matrix = identity()
    var grid = true


    init {
        background = Color.WHITE
    }

    fun addShape(shape: Shape) {
        shapes.add(shape)
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        if (grid) drawGrid(g)

        g.color = Color.RED
        shapes.forEach { draw(g, it) }
    }

    private fun drawGrid(g: Graphics) {
        g.color = Color.BLACK

        val res = matrix * mirrorY()
        val gridVS = res * Vector(0.0, -100.0)
        val gridVE = res * Vector(0.0, +100.0)
        val gridHS = res * Vector(-100.0, 0.0)
        val gridHE = res * Vector(+100.0, 0.0)

        g.drawLine(gridVS.x.toInt(), gridVS.y.toInt(), gridVE.x.toInt(), gridVE.y.toInt())
        g.drawLine(gridHS.x.toInt(), gridHS.y.toInt(), gridHE.x.toInt(), gridHE.y.toInt())

        for (x in -100..100) {
            val unitStart = res * Vector(x.toDouble(), 0.0)
            val unitEnd = res * Vector(x.toDouble(), 0.1)
            g.drawLine(unitStart.x.toInt(), unitStart.y.toInt(), unitEnd.x.toInt(), unitEnd.y.toInt());
        }
        for (y in -100..100) {
            val unitStart = res * Vector(0.0, y.toDouble())
            val unitEnd = res * Vector(0.1, y.toDouble())
            g.drawLine(unitStart.x.toInt(), unitStart.y.toInt(), unitEnd.x.toInt(), unitEnd.y.toInt());
        }
    }

    private fun draw(g: Graphics, shape: Shape) {
        val res = matrix * mirrorY()
        shape.loops.forEach { loop ->
            for (i in 0..<loop.points.size) {
                val start = res * loop.points[i]
                val end = res * loop.points[(i + 1) % loop.points.size]
                g.drawLine(start.x.toInt(), start.y.toInt(), end.x.toInt(), end.y.toInt())
            }
        }
    }


    override fun getPreferredSize(): Dimension {
        return Dimension(800, 600)
    }


}