package com.lucasneves.financecontrol.util

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn

object DateUtils {

    fun today(): String = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()

    fun todayDate(): LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())

    fun currentMonth(): LocalDate {
        val d = Clock.System.todayIn(TimeZone.currentSystemDefault())
        return LocalDate(d.year, d.month, 1)
    }

    fun parseIso(date: String): LocalDate? = runCatching { LocalDate.parse(date) }.getOrNull()

    fun toDisplayDate(isoDate: String): String {
        val date = parseIso(isoDate) ?: return isoDate
        return "${date.dayOfMonth.toString().padStart(2, '0')}/" +
                "${date.monthNumber.toString().padStart(2, '0')}/${date.year}"
    }

    fun monthLabel(firstOfMonth: LocalDate): String =
        "${ptMonthName(firstOfMonth.month)} ${firstOfMonth.year}"

    fun shortMonthLabel(firstOfMonth: LocalDate): String {
        val name = ptMonthName(firstOfMonth.month)
        return "${name.take(3)}/${firstOfMonth.year.toString().takeLast(2)}"
    }

    fun isInMonth(isoDate: String, firstOfMonth: LocalDate): Boolean {
        val date = parseIso(isoDate) ?: return false
        return date.year == firstOfMonth.year && date.month == firstOfMonth.month
    }

    fun previousMonth(firstOfMonth: LocalDate): LocalDate =
        firstOfMonth.minus(1, DateTimeUnit.MONTH)

    fun nextMonth(firstOfMonth: LocalDate): LocalDate =
        firstOfMonth.plus(1, DateTimeUnit.MONTH)

    private fun ptMonthName(month: Month): String = when (month) {
        Month.JANUARY -> "Janeiro"
        Month.FEBRUARY -> "Fevereiro"
        Month.MARCH -> "Março"
        Month.APRIL -> "Abril"
        Month.MAY -> "Maio"
        Month.JUNE -> "Junho"
        Month.JULY -> "Julho"
        Month.AUGUST -> "Agosto"
        Month.SEPTEMBER -> "Setembro"
        Month.OCTOBER -> "Outubro"
        Month.NOVEMBER -> "Novembro"
        Month.DECEMBER -> "Dezembro"
    }
}