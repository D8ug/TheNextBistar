package com.personalproject.thenextbistar

import android.provider.Settings
import java.util.*

fun calcResult(item: LiveShowItem): String {
    val seconds = Calendar.getInstance().timeInMillis / 1000
    if (seconds < item.timestamp) {
        val timeLeft = item.timestamp - seconds
        val minsLeft = timeLeft / 60
        val secsLeft = timeLeft % 60

        return String.format("%02d:%02d", minsLeft, secsLeft)
    }

    return "${(calcPercent(item) * 100).toInt()}%"
}

fun calcPercent(item: LiveShowItem) : Float {
    if(GlobalState.numUsers == 0) {
        return 0f
    }

    return (item.votes / GlobalState.numUsers.toFloat())
}