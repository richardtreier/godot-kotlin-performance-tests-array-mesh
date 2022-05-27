package de.richardtreier.gdkotlin.performancetests.arraymesh.utils

import godot.Node
import godot.Resource
import godot.ResourceLoader
import godot.core.NodePath
import godot.global.GD

inline fun <reified T> Node.getNodeOrThrow(path: String): T {
  val node = this.getNode(NodePath(path)) ?: {
    GD.print("Could not find Node with path ${path}.")
    this.getTree()?.quit(404)
    error("Could not find Node with path ${path}.")
  }
  return node.let {
    check(it is T) { "Node $path is not ${T::class.java} but ${it::class.java}." }
    it
  }
}


inline fun <reified T : Resource> loadResourceOrThrow(fullPath: String): T {
  val node = ResourceLoader.load(fullPath) ?: {
    GD.print("Could not find Node with path ${fullPath}.")
    error("Could not find Node with path ${fullPath}.")
  }
  return node.let {
    check(it is T) { "Node $fullPath is not ${T::class.java} but ${it::class.java}." }
    it
  }
}

inline fun <reified T : Number> Node.getNumber(propertyName: String, mapper: (number: Number) -> T): T {
  return (get(propertyName) as Number).let(mapper)
}
