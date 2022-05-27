package de.richardtreier.gdkotlin.performancetests.arraymesh.scenes

import godot.Node
import godot.Spatial
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction

@RegisterClass
class TestScene : Spatial() {
  private class Nodes(val parent: Node) {

  }

  private lateinit var nodes: Nodes

  @RegisterFunction
  override fun _ready() {
    nodes = Nodes(this)
  }
}
