package com.lucasneves.financecontrol.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyUtils {
    private val brFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    fun format(value: Double): String = brFormat.format(value)

    fun parse(text: String): Double {
        val cleaned = text.replace(Regex("[^\\d,.]"), "").replace(",", ".")
        return cleaned.toDoubleOrNull() ?: 0.0
    }
}
