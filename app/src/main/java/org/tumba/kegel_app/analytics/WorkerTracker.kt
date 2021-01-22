package org.tumba.kegel_app.analytics

import androidx.core.os.bundleOf
import java.text.DateFormat
import java.util.*
import javax.inject.Inject

class WorkerTracker @Inject constructor(private val analytics: Analytics) : TrackerScope {

    private val dateFormatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM)

    fun trackInitWorker() {
        analytics.track(
            eventName = "worker_init",
            parameters = bundleOf("timestamp" to getTimestamp())
        )
    }

    fun trackScheduled(timestamp: Long) {
        analytics.track(
            eventName = "worker_scheduled",
            parameters = bundleOf("timestamp" to getTimestamp(timestamp))
        )
    }

    fun trackDoWork() {
        analytics.track(
            eventName = "worker_do_work",
            parameters = bundleOf("timestamp" to getTimestamp())
        )
    }

    private fun getTimestamp(timestamp: Long = System.currentTimeMillis()): String {
        return dateFormatter.format(Date(timestamp))
    }

}
