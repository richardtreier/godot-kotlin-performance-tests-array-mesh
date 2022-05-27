package de.richardtreier.gdkotlin.performancetests.arraymesh.scenes

import de.richardtreier.gdkotlin.performancetests.arraymesh.scenes.components.MeshTesterKotlin
import de.richardtreier.gdkotlin.performancetests.arraymesh.utils.TestResult
import de.richardtreier.gdkotlin.performancetests.arraymesh.utils.disableWhile
import de.richardtreier.gdkotlin.performancetests.arraymesh.utils.getNodeOrThrow
import godot.Button
import godot.Label
import godot.LineEdit
import godot.MeshInstance
import godot.Node
import godot.Spatial
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterSignal
import godot.signals.signal

@RegisterClass
class TestScene : Spatial() {
  private class Nodes(parent: Node) {
    val meshTesterKotlin: MeshTesterKotlin = parent.getNodeOrThrow("MeshTesterKotlin")
    val meshTesterGodot: MeshInstance = parent.getNodeOrThrow("MeshTesterGodot")

    val button: Button = parent.getNodeOrThrow("UI/C/Button")
    val input: LineEdit = parent.getNodeOrThrow("UI/C/Input")
    val result: Label = parent.getNodeOrThrow("UI/C/Result")
  }

  private lateinit var nodes: Nodes
  private var numBlocks = 0
  private var lastKotlinResult: TestResult? = null
  private var lastGodotNoResizeArraysResult: TestResult? = null
  private var lastGodotResizeArraysResult: TestResult? = null

  @RegisterSignal
  val signalRunMeshTesterGodot by signal<Int, Boolean>("numBlocks", "resizeArrays")

  @RegisterFunction
  override fun _ready() {
    nodes = Nodes(this)
    nodes.button.connect("pressed", this, "on_run_tests_button_click")
    nodes.meshTesterGodot.connect("report_mesh_tester_godot_result", this, "on_report_mesh_tester_godot_result")
  }

  @RegisterFunction
  fun onRunTestsButtonClick() {
    nodes.button.disableWhile {
      updateNumBlocks()
      lastKotlinResult = runKotlinTest()
      signalRunMeshTesterGodot.emit(numBlocks, false)
    }
  }

  @RegisterFunction
  fun onReportMeshTesterGodotResult(numTotalMs: Int, numInsertsMs: Int, resizeArrays: Boolean) {
    val result = TestResult(
      "GDScript ${if (resizeArrays) "with resize and set" else "with append"}",
      numTotalMs,
      numInsertsMs
    )
    if (!resizeArrays) {
      lastGodotNoResizeArraysResult = result
      signalRunMeshTesterGodot.emit(numBlocks, true)
    } else {
      lastGodotResizeArraysResult = result

      val resultMessage = "---\nGenerating $numBlocks cubes into a single ArrayMesh:" + listOfNotNull(
        lastKotlinResult,
        lastGodotNoResizeArraysResult,
        lastGodotResizeArraysResult
      ).joinToString("") { "\n  - " + it.pretty() }
      println(resultMessage)
      nodes.result.text = resultMessage
    }
  }

  private fun runKotlinTest(): TestResult {
    nodes.meshTesterKotlin.numBlocks = numBlocks
    nodes.meshTesterKotlin.runTests()
    return TestResult(
      "Kotlin",
      nodes.meshTesterKotlin.totalElapsedMs,
      nodes.meshTesterKotlin.fillArraysElapsedMs
    )
  }

  private fun updateNumBlocks() {
    fun Int.floorToMultipleOf(multiple: Int) = this - this % multiple
    numBlocks = nodes.input.text.toIntOrNull()
      ?.floorToMultipleOf(100)
      ?.coerceAtLeast(100)
      ?: 100
    nodes.input.text = numBlocks.toString()
  }
}
