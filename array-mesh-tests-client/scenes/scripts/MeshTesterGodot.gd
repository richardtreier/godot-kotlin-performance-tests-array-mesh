extends MeshInstance

onready var mat: Material = load("res://assets/materials/metal_rusted.tres")

signal report_mesh_tester_godot_result(total_elapsed_ms, fill_arrays_elapsed_ms)

var num_blocks = 0

# Track total mesh generation time
var total_elapsed_ms: int = 0
var total_start_us: int = 0

# Track PoolVectorArray.append calls only
var fill_arrays_elapsed_ms: int = 0
var fill_arrays_elapsed_us: int = 0
var fill_arrays_start_us: int = 0

var verts = PoolVector3Array()
var uvs = PoolVector2Array()

func _on_TestScene_run_mesh_tester_godot(p_num_blocks):
  num_blocks = p_num_blocks
  run_tests()
  emit_signal("report_mesh_tester_godot_result", total_elapsed_ms, fill_arrays_elapsed_ms)

func run_tests():
  # Start time measurment
  total_start_us = OS.get_ticks_usec()
  fill_arrays_elapsed_us = 0

  # Add Mesh
  var arrays = []
  arrays.resize(Mesh.ARRAY_MAX)

  verts = PoolVector3Array()
  uvs = PoolVector2Array()
  
  for x in range(num_blocks / 100):
    for y in range(10):
      for z in range(10):
        add_block(Vector3(x, y, z), Vector3(1, 1, 1))

  arrays[Mesh.ARRAY_VERTEX] = verts
  arrays[Mesh.ARRAY_TEX_UV] = uvs
  
  var newMesh = ArrayMesh.new()
  newMesh.add_surface_from_arrays(Mesh.PRIMITIVE_TRIANGLES, arrays)
  newMesh.surface_set_material(0, mat)
  mesh = newMesh

  # Print elapsed time
  fill_arrays_elapsed_ms = fill_arrays_elapsed_us / 1000
  total_elapsed_ms = (OS.get_ticks_usec() - total_start_us) / 1000
  
  # Reset
  newMesh.clear_surfaces()
  mesh = newMesh

func add_block(
  offset: Vector3, 
  dimensions: Vector3
):
  # Direction variable names are not following godot conventions
  # forgive me, old code, copy-pasted, makes cubes nonetheless
  
  var xLeft = offset.x
  var xRight = offset.x + dimensions.x
  var yBackward = offset.y
  var yForward = offset.y + dimensions.y
  var zDown = offset.z
  var zUp = offset.z + dimensions.z
  
  var leftForwardDown = Vector3(xLeft, yForward, zDown)
  var leftForwardUp = Vector3(xLeft, yForward, zUp)
  var leftBackwardDown = Vector3(xLeft, yBackward, zDown)
  var leftBackwardUp = Vector3(xLeft, yBackward, zUp)
  var rightForwardDown = Vector3(xRight, yForward, zDown)
  var rightForwardUp = Vector3(xRight, yForward, zUp)
  var rightBackwardDown = Vector3(xRight, yBackward, zDown)
  var rightBackwardUp = Vector3(xRight, yBackward, zUp)

  add_quad_face(
    leftForwardDown,
    leftForwardUp,
    leftBackwardUp,
    leftBackwardDown,
    Vector2(dimensions.z, dimensions.y)
  )
  add_quad_face(
    rightBackwardDown,
    rightBackwardUp,
    rightForwardUp,
    rightForwardDown,
    Vector2(dimensions.z, dimensions.y)
  )
  add_quad_face(
    leftBackwardDown,
    leftBackwardUp,
    rightBackwardUp,
    rightBackwardDown,
    Vector2(dimensions.z, dimensions.x)
  )
  add_quad_face(
    rightForwardDown,
    rightForwardUp,
    leftForwardUp,
    leftForwardDown,
    Vector2(dimensions.z, dimensions.x)
  )
  add_quad_face(
    leftBackwardUp,
    leftForwardUp,
    rightForwardUp,
    rightBackwardUp,
    Vector2(dimensions.y, dimensions.x)
  )
  add_quad_face(
    leftForwardDown,
    leftBackwardDown,
    rightBackwardDown,
    rightForwardDown,
    Vector2(dimensions.y, dimensions.x)
  )
   
func add_quad_face(
  a: Vector3, 
  b: Vector3, 
  c: Vector3, 
  d: Vector3, 
  uvDimensions: Vector2
):
  add_tri_face(a, b, c, Vector2(0.0, 0.0), Vector2(uvDimensions.x, 0.0), uvDimensions)
  add_tri_face(a, c, d, Vector2(0.0, 0.0), uvDimensions, Vector2(0.0, uvDimensions.y))

func add_tri_face(
  a: Vector3, b: Vector3, c: Vector3,
  uva: Vector2, uvb: Vector2, uvc: Vector2
):
  fill_arrays_start_us = OS.get_ticks_usec()
  verts.append(a)
  verts.append(b)
  verts.append(c)
  uvs.append(uva)
  uvs.append(uvb)
  uvs.append(uvc)
  fill_arrays_elapsed_us += OS.get_ticks_usec() - fill_arrays_start_us

