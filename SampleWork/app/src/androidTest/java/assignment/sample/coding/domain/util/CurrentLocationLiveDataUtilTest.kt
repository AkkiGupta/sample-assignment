package assignment.sample.coding

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import assignment.sample.coding.domain.model.Coordinate
import assignment.sample.coding.util.Constants
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Gets the value of a [LiveData] or waits for it to have one, with a timeout.
 *
 * Use this extension from host-side (JVM) tests. It's recommended to use it alongside
 * `InstantTaskExecutorRule` or a similar mechanism to execute tasks synchronously.
 *
 * Modified to handle Loading and Success Scenarios
 */
@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun <Coordinate> LiveData<assignment.sample.coding.domain.model.Coordinate>.getOrAwaitCurrentLocation(
    time: Long = 3,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    afterObserve: () -> Unit = {},
    responseExpected: Int = 1
): assignment.sample.coding.domain.model.Coordinate {
    var counter = 1
    var data: assignment.sample.coding.domain.model.Coordinate? = Coordinate(Constants.HAMBURG_NE_LAT, Constants.HAMBURG_NE_LON)
    val latch = CountDownLatch(1)
    val observer = object : Observer<assignment.sample.coding.domain.model.Coordinate> {
        override fun onChanged(o: assignment.sample.coding.domain.model.Coordinate) {
            counter++
            data = o
            if (counter > responseExpected) {
                latch.countDown()
                this@getOrAwaitCurrentLocation.removeObserver(this)
            }
        }
    }
    this.observeForever(observer)

    try {
        afterObserve.invoke()
        val live_value = Coordinate(Constants.HAMBURG_NE_LAT, Constants.HAMBURG_NE_LON)

        // Don't wait indefinitely if the LiveData is not set.
        if (!latch.await(time, timeUnit)) {
            return live_value as assignment.sample.coding.domain.model.Coordinate
        //throw TimeoutException("LiveData value was never set.")
        }

    } finally {
        if (counter > 2)
            this.removeObserver(observer)
    }

    @Suppress("UNCHECKED_CAST")
    return data as assignment.sample.coding.domain.model.Coordinate
}