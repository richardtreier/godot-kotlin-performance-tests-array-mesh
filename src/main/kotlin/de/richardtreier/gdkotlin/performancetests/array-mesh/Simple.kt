package de.richardtreier.gdkotlin.performancetests.array-mesh

import godot.Spatial
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.global.GD

@RegisterClass
class Simple: Spatial() {

    @RegisterFunction
    override fun _ready() {
        GD.print("Hello world!")
    }
}
