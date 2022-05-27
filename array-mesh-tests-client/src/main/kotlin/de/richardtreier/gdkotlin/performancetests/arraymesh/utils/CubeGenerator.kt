package de.richardtreier.gdkotlin.performancetests.arraymesh.utils

import de.richardtreier.gdkotlin.performancetests.arraymesh.utils.StopWatch2
import godot.ArrayMesh
import godot.Material
import godot.Mesh
import godot.MeshInstance
import godot.core.PoolVector2Array
import godot.core.PoolVector3Array
import godot.core.VariantArray
import godot.core.Vector2
import godot.core.Vector3

data class Face3(
  val a: Vector3,
  val b: Vector3,
  val c: Vector3,
  val uva: Vector2,
  val uvb: Vector2,
  val uvc: Vector2
) {
  fun vertices() = listOf(a, b, c)
  fun uvs() = listOf(uva, uvb, uvc)
}

fun generateCubeFace3s(offset: Vector3, dimensions: Vector3): List<Face3> {
  val faces = mutableListOf<Face3>()

  // Clock-Wise
  fun addQuad(a: Vector3, b: Vector3, c: Vector3, d: Vector3, uvDimensions: Vector2) {
    faces.add(Face3(a, b, c, Vector2.ZERO, Vector2(uvDimensions.x, 0.0), uvDimensions))
    faces.add(Face3(a, c, d, Vector2.ZERO, uvDimensions, Vector2(0.0, uvDimensions.y)))
  }

  // Direction variable names are not following godot conventions
  // forgive me, old code, copy-pasted, makes cubes nonetheless

  val xLeft = offset.x
  val xRight = offset.x + dimensions.x
  val yBackward = offset.y
  val yForward = offset.y + dimensions.y
  val zDown = offset.z
  val zUp = offset.z + dimensions.z

  val leftForwardDown = Vector3(xLeft, yForward, zDown)
  val leftForwardUp = Vector3(xLeft, yForward, zUp)
  val leftBackwardDown = Vector3(xLeft, yBackward, zDown)
  val leftBackwardUp = Vector3(xLeft, yBackward, zUp)
  val rightForwardDown = Vector3(xRight, yForward, zDown)
  val rightForwardUp = Vector3(xRight, yForward, zUp)
  val rightBackwardDown = Vector3(xRight, yBackward, zDown)
  val rightBackwardUp = Vector3(xRight, yBackward, zUp)

  addQuad(
    leftForwardDown,
    leftForwardUp,
    leftBackwardUp,
    leftBackwardDown,
    Vector2(dimensions.z, dimensions.y)
  )

  addQuad(
    rightBackwardDown,
    rightBackwardUp,
    rightForwardUp,
    rightForwardDown,
    Vector2(dimensions.z, dimensions.y)
  )

  addQuad(
    leftBackwardDown,
    leftBackwardUp,
    rightBackwardUp,
    rightBackwardDown,
    Vector2(dimensions.z, dimensions.x)
  )

  addQuad(
    rightForwardDown,
    rightForwardUp,
    leftForwardUp,
    leftForwardDown,
    Vector2(dimensions.z, dimensions.x)
  )

  addQuad(
    leftBackwardUp,
    leftForwardUp,
    rightForwardUp,
    rightBackwardUp,
    Vector2(dimensions.y, dimensions.x)
  )

  addQuad(
    leftForwardDown,
    leftBackwardDown,
    rightBackwardDown,
    rightForwardDown,
    Vector2(dimensions.y, dimensions.x)
  )

  return faces
}

fun MeshInstance.setMeshFromFace3s(mesh: List<Face3>, material: Material, poolArrayInsertionStopWatch: StopWatch2) {
  fun List<Face3>.toVertexArray(): PoolVector3Array {
    val array = PoolVector3Array()
    poolArrayInsertionStopWatch.measure {
      flatMap { face -> face.vertices() }.forEach { array.append(it) }
    }
    return array
  }

  fun List<Face3>.toUvArray(): PoolVector2Array {
    val array = PoolVector2Array()
    poolArrayInsertionStopWatch.measure {
      flatMap { face -> face.uvs() }.forEach {
        array.append(it)
      }
    }
    return array
  }

  fun ArrayMesh.addSurface(mesh: List<Face3>, material: Material) {
    val arrays = VariantArray<Any?>()
    arrays.resize(ArrayMesh.ARRAY_MAX.toInt())
    arrays[ArrayMesh.ArrayType.ARRAY_VERTEX.id.toInt()] = mesh.toVertexArray()
    arrays[ArrayMesh.ArrayType.ARRAY_TEX_UV.id.toInt()] = mesh.toUvArray()
    this.addSurfaceFromArrays(Mesh.PRIMITIVE_TRIANGLES, arrays)
    this.surfaceSetMaterial(getSurfaceCount() - 1, material)
  }

  val arrayMesh = ArrayMesh()
  arrayMesh.addSurface(mesh, material)
  this.mesh = arrayMesh
}
