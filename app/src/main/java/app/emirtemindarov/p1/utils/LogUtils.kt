package app.emirtemindarov.p1.utils

import android.util.Log

/**
 * Работает - не трожь!
 */
object LogUtils {

    fun logLong(tag: String, message: String) {
        val maxLogSize = 2000
        for (i in 0..message.length / maxLogSize) {
            val start = i * maxLogSize
            val end = (start + maxLogSize).coerceAtMost(message.length)
            if (start < end) {
                Log.i(tag, message.substring(start, end))
            }
        }
    }

}