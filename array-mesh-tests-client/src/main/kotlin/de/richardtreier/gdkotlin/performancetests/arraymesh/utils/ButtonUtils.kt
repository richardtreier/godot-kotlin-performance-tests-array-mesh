package de.richardtreier.gdkotlin.performancetests.arraymesh.utils

import godot.BaseButton

fun <T> BaseButton.disableWhile(action: () -> T): T {
  return try {
    disabled = true
    action()
  } finally {
    disabled = false
  }
}
