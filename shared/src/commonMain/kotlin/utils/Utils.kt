package utils

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object Utils {
    fun MillisSinceEpoch.generateCurrentMonthId(): CurrentMonthID {
        val instant = Instant.fromEpochMilliseconds(this)
        val dateTime: LocalDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val id = "${dateTime.year}${dateTime.monthNumber}${dateTime.dayOfMonth}"
        return id.toLong()
    }

    fun MillisSinceEpoch.formatDate(): String {
        val instant = Instant.fromEpochMilliseconds(this)
        val dateTime: LocalDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        return "${dateTime.dayOfMonth}/${dateTime.monthNumber}/${dateTime.year}"
    }

}