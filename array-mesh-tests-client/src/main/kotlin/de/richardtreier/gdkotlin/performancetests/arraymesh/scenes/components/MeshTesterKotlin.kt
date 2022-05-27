package de.richardtreier.gdkotlin.performancetests.arraymesh.scenes.components

import de.richardtreier.gdkotlin.performancetests.arraymesh.utils.StopWatch2
import de.richardtreier.gdkotlin.performancetests.arraymesh.utils.generateCubeFace3s
import de.richardtreier.gdkotlin.performancetests.arraymesh.utils.loadResourceOrThrow
import de.richardtreier.gdkotlin.performancetests.arraymesh.utils.setMeshFromFace3s
import godot.ArrayMesh
import godot.Material
import godot.MeshInstance
import godot.Node
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.core.Vector3

@RegisterClass
class MeshTesterKotlin : MeshInstance() {
  private class Nodes(parent: Node) {
    val material: Material = loadResourceOrThrow("res://assets/materials/metal_rusted.tres")
  }

  private lateinit var nodes: Nodes

  @RegisterProperty
  var numBlocks = 0

  @RegisterProperty
  var totalElapsedMs = 0

  @RegisterProperty
  var fillArraysElapsedMs = 0

  @RegisterFunction
  override fun _ready() {
    nodes = Nodes(this)
  }

  @RegisterFunction
  fun runTests() {
    // Run Test
    val totalStopWatch = StopWatch2()
    val fillArraysStopWatch = StopWatch2()
    totalStopWatch.measure {
      val faces = generateCubesFace3s()
      setMeshFromFace3s(faces, nodes.material, fillArraysStopWatch)
    }

    // Wire results
    totalElapsedMs = totalStopWatch.getMillis()
    fillArraysElapsedMs = fillArraysStopWatch.getMillis()

    // Clean up
    (mesh as ArrayMesh).clearSurfaces()
  }

  private fun generateCubesFace3s() = (0..(numBlocks / 100)).flatMap { x ->
    (0..10).flatMap { y ->
      (0..10).flatMap { z ->
        generateCubeFace3s(Vector3(x, y, z), Vector3(1.0, 1.0, 1.0))
      }
    }
  }
}
