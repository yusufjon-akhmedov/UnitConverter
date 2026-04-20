package com.yusufjon.unitconverter.presentation.util

import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun Long.toRelativeTime(clock: Clock = Clock.systemDefaultZone()): String {
    val createdAt = Instant.ofEpochMilli(this)
    val duration = Duration.between(createdAt, Instant.now(clock))

    return when {
        duration.isNegative -> "Just now"
        duration < Duration.ofMinutes(1) -> "Just now"
        duration < Duration.ofHours(1) -> "${duration.toMinutes()}m ago"
        duration < Duration.ofDays(1) -> "${duration.toHours()}h ago"
        duration < Duration.ofDays(7) -> "${duration.toDays()}d ago"
        else -> createdAt.toDateLabel(clock.zone)
    }
}

private fun Instant.toDateLabel(zoneId: ZoneId): String {
    return DateTimeFormatter.ofPattern("MMM d", Locale.US)
        .withZone(zoneId)
        .format(this)
}
