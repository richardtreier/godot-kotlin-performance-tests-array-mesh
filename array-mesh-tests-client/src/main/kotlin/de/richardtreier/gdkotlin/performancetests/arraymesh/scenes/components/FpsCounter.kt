package de.richardtreier.gdkotlin.performancetests.arraymesh.scenes.components

import godot.Engine
import godot.Label
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction

@RegisterClass
class FpsCounter : Label() {

  @RegisterFunction
  override fun _process(delta: Double) {
    val fps = Engine.getFramesPerSecond()
    text = String.format("fps: %.0f", fps)
  }
}
