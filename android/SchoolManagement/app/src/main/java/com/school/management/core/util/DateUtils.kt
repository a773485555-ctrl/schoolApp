package com.school.management.core.util

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale

object DateUtils {

    private val isoFormatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_ISO)
    private val displayDateFormatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_DISPLAY)
    private val apiDateFormatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_API)
    private val displayTimeFormatter = DateTimeFormatter.ofPattern(Constants.TIME_FORMAT_DISPLAY)

    fun parseIsoDateTime(isoString: String): LocalDateTime {
        return try {
            LocalDateTime.parse(isoString, isoFormatter)
        } catch (e: Exception) {
            try {
                LocalDateTime.parse(isoString, DateTimeFormatter.ISO_DATE_TIME)
            } catch (e2: Exception) {
                LocalDateTime.now()
            }
        }
    }

    fun parseApiDate(dateString: String): LocalDate {
        return try {
            LocalDate.parse(dateString, apiDateFormatter)
        } catch (e: Exception) {
            LocalDate.now()
        }
    }

    fun formatToDisplay(dateTime: LocalDateTime): String {
        return dateTime.format(displayDateFormatter)
    }

    fun formatDateToDisplay(date: LocalDate): String {
        return date.format(displayDateFormatter)
    }

    fun formatToApi(date: LocalDate): String {
        return date.format(apiDateFormatter)
    }

    fun formatToIso(dateTime: LocalDateTime): String {
        return dateTime.format(isoFormatter)
    }

    fun formatTimeToDisplay(dateTime: LocalDateTime): String {
        return dateTime.format(displayTimeFormatter)
    }

    fun toEpochMillis(date: LocalDate): Long {
        return date.toEpochDay() * 86400000L
    }

    fun fromEpochMillis(millis: Long): LocalDate {
        return LocalDate.ofEpochDay(millis / 86400000L)
    }

    fun daysBetween(start: LocalDate, end: LocalDate): Long {
        return ChronoUnit.DAYS.between(start, end)
    }

    fun isToday(date: LocalDate): Boolean {
        return date == LocalDate.now()
    }

    fun isPast(date: LocalDate): Boolean {
        return date.isBefore(LocalDate.now())
    }

    fun isFuture(date: LocalDate): Boolean {
        return date.isAfter(LocalDate.now())
    }

    fun getMonthRange(year: Int, month: Int): Pair<LocalDate, LocalDate> {
        val yearMonth = YearMonth.of(year, month)
        return Pair(yearMonth.atDay(1), yearMonth.atEndOfMonth())
    }

    fun getCurrentAcademicYear(): String {
        val now = LocalDate.now()
        val year = if (now.monthValue >= 9) now.year else now.year - 1
        return "$year-${year + 1}"
    }

    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    fun timestampToDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }

    fun formatRelativeTime(dateTime: LocalDateTime): String {
        val now = LocalDateTime.now()
        val minutes = ChronoUnit.MINUTES.between(dateTime, now)
        val hours = ChronoUnit.HOURS.between(dateTime, now)
        val days = ChronoUnit.DAYS.between(dateTime, now)

        return when {
            minutes < 1 -> "Just now"
            minutes < 60 -> "${minutes}m ago"
            hours < 24 -> "${hours}h ago"
            days < 7 -> "${days}d ago"
            days < 30 -> "${days / 7}w ago"
            else -> formatToDisplay(dateTime)
        }
    }
}
