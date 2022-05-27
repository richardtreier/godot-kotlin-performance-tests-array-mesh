package de.richardtreier.gdkotlin.performancetests.arraymesh.utils

import org.apache.commons.lang3.time.StopWatch
import java.util.concurrent.TimeUnit

class StopWatch2 {
  private val stopWatch = StopWatch()

  fun <T> measure(run: () -> T): T {
    if (stopWatch.isSuspended) {
      stopWatch.resume()
    } else if (stopWatch.isStopped) {
      stopWatch.start()
    }
    return try {
      run()
    } finally {
      stopWatch.suspend()
    }
  }

  fun getMillis(): Int {
    return stopWatch.getTime(TimeUnit.MILLISECONDS).toInt()
  }
}
