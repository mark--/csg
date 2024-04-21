package de.markschaefer.csg

import de.markschaefer.csg.Matrix.Companion.identity
import de.markschaefer.csg.Matrix.Companion.mirrorY
import de.markschaefer.csg.Matrix.Companion.rotate
import de.markschaefer.csg.Matrix.Companion.rotateDegrees
import de.markschaefer.csg.Matrix.Companion.scale
import de.markschaefer.csg.Matrix.Companion.translate
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JSlider
import kotlin.math.max
import kotlin.math.min


class MainWindow : JFrame("CSG") {
    val shapeRenderer = ShapeRenderer()

    private var zoom = 75;
    private var angle = 0;

    init {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE)
        isVisible = true
        layout = BorderLayout(10, 10)

        val rotSlider = JSlider(-360, 360, 0)
        rotSlider.addChangeListener { updateRotation(rotSlider.value) }
        add(rotSlider, BorderLayout.NORTH)

        add(shapeRenderer, BorderLayout.CENTER)
        pack()

        addMouseWheelListener { event -> updateZoom(zoom + event.wheelRotation) }
        updateZoom(zoom)

        setLocationRelativeTo(null)
    }

    private fun updateZoom(zoom: Int) {
        this.zoom = max(min(zoom, 200), 5);
        update()

    }

    private fun updateRotation(angle: Int) {
        this.angle = angle
        update()
    }

    private fun update() {
        shapeRenderer.matrix = translate(600.0, 400.0) * scale(this.zoom.toDouble()) * rotateDegrees(this.angle)
        shapeRenderer.repaint()
    }

}

class ShapeRenderer : JPanel() {
    private val shapes = mutableMapOf<Shape, Color>()

    var matrix = identity()
    var grid = true

    init {
        background = Color.WHITE
    }

    fun addShape(shape: Shape, color: Color = Color.RED) {
        shapes[shape] = color;
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        if (grid) drawGrid(g)

        shapes.forEach { draw(g, it.component1(), it.component2()) }
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

    private fun draw(g: Graphics, shape: Shape, color: Color) {
        val res = matrix * mirrorY()
        g.color = color
        shape.loops.forEach { loop ->
            for (i in 0..<loop.points.size) {
                val start = res * loop.points[i]
                val end = res * loop.points[(i + 1) % loop.points.size]
                g.drawLine(start.x.toInt(), start.y.toInt(), end.x.toInt(), end.y.toInt())
            }
        }

        shape.boundingBox().grid(0.02, 0.02)
            .filter { shape.contains(it) }
            .forEach {
                val r = res * it
                g.drawRect(r.x.toInt(), r.y.toInt(), 1, 1)
            }

    }

    override fun getPreferredSize(): Dimension {
        return Dimension(1200, 800)
    }

}