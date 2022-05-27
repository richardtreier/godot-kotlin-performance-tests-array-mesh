package de.richardtreier.gdkotlin.performancetests.arraymesh.utils


data class TestResult(val name: String, val totalMs: Int, val insertsMs: Int) {
  fun pretty(): String {
    return "$name - total ${totalMs}ms, PoolVectorArray filling: ${insertsMs}ms, rest ${totalMs - insertsMs}ms"
  }
}
