package de.markschaefer.csg

import de.markschaefer.csg.Matrix.Companion.identity
import de.markschaefer.csg.Matrix.Companion.mirrorY
import de.markschaefer.csg.Matrix.Companion.rotateDegrees
import de.markschaefer.csg.Matrix.Companion.scale
import de.markschaefer.csg.Matrix.Companion.translate
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import kotlin.math.max
import kotlin.math.min


class MainWindow : JFrame("CSG") {
    val shapeRenderer = ShapeRenderer()

    private var zoom = 75
    private var angle = 0

    init {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE)
        isVisible = true
        layout = BorderLayout(10, 10)

        SwingUtilities.invokeLater {
            val rotSlider = JSlider(-360, 360, 0)
            rotSlider.addChangeListener { updateRotation(rotSlider.value) }
            add(rotSlider, BorderLayout.NORTH)

            val logText = JTextArea(5, 100)

            shapeRenderer.addMouseMotionListener(object : MouseAdapter() {
                override fun mouseMoved(e: MouseEvent) {

                    val i = (shapeRenderer.matrix * mirrorY()).inverse()
                    val model = i * Vector(e.x.toDouble(), e.y.toDouble())

                    logText.text = """
                    Viewport coordinates: (${e.x}, ${e.y})
                    Model coordinates: (${model.x.format(3)}, ${model.y.format(3)})
                """.trimIndent()
                }
            })

            add(shapeRenderer, BorderLayout.CENTER)

            addMouseWheelListener { event -> updateZoom(zoom + event.wheelRotation) }
            updateZoom(zoom)


            logText.isEditable = false
            add(logText, BorderLayout.SOUTH)

            pack()
            setLocationRelativeTo(null)
        }
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
        shapes[shape] = color
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        if (grid) drawGrid(g)

        shapes.forEach { draw(g, it.component1(), it.component2()) }
    }


    private fun draw(g: Graphics, shape: Shape, color: Color) {
        g.color = color
        val res = matrix * mirrorY() * shape
        res.loops.forEach {
            for (i in 0..<it.points.size) {
                val start = it.points[i]
                val end = it.points[(i + 1) % it.points.size]
                g.drawLine(start.x.toInt(), start.y.toInt(), end.x.toInt(), end.y.toInt())
            }
        }

//        res.boundingBox().grid(11.0, 11.0)
//            .filter { res.contains(it) }
//            .forEach {
//                g.fillRect(it.x.toInt() - 5, it.y.toInt() - 5, 11, 11)
//            }

        res.boundingBox().scanlines().flatMap { res.scanlines(it) }.forEach {
            g.drawLine(it.start.x.toInt(), it.start.y.toInt(), it.end.x.toInt(), it.end.y.toInt())
        }

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
            g.drawLine(unitStart.x.toInt(), unitStart.y.toInt(), unitEnd.x.toInt(), unitEnd.y.toInt())
        }
        for (y in -100..100) {
            val unitStart = res * Vector(0.0, y.toDouble())
            val unitEnd = res * Vector(0.1, y.toDouble())
            g.drawLine(unitStart.x.toInt(), unitStart.y.toInt(), unitEnd.x.toInt(), unitEnd.y.toInt())
        }
    }

    override fun getPreferredSize(): Dimension {
        return Dimension(1200, 800)
    }

}