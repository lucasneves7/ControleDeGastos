package com.lucasneves.financecontrol.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.WeekDayPosition
import com.lucasneves.financecontrol.data.model.DayMarkType
import com.lucasneves.financecontrol.ui.theme.ExpenseRed
import com.lucasneves.financecontrol.ui.theme.IncomeGreen
import com.lucasneves.financecontrol.ui.theme.TransferBlue
import kotlinx.datetime.LocalDate
import java.time.DayOfWeek
import java.time.YearMonth

@Composable
fun CalendarCard(
    selectedMonth: LocalDate,
    dayMarks: Map<LocalDate, DayMarkType> = emptyMap(),
    onDayClick: ((LocalDate) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val today = java.time.LocalDate.now()
    val currentMonth = YearMonth.of(selectedMonth.year, selectedMonth.monthNumber)

    Card(
        onClick = { expanded = !expanded },
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            AnimatedContent(targetState = expanded, label = "calendar") { isExpanded ->
                if (isExpanded) {
                    HorizontalCalendar(
                        state = rememberCalendarState(
                            startMonth = currentMonth.minusMonths(12),
                            endMonth = currentMonth.plusMonths(12),
                            firstVisibleMonth = currentMonth,
                            firstDayOfWeek = DayOfWeek.SUNDAY
                        ),
                        monthHeader = { DaysOfWeekHeader() },
                        dayContent = { day ->
                            val kxDate = runCatching {
                                LocalDate(day.date.year, day.date.monthValue, day.date.dayOfMonth)
                            }.getOrNull()
                            DayCell(
                                day = day.date,
                                isCurrentMonth = day.position == DayPosition.MonthDate,
                                isToday = day.date == today,
                                markType = kxDate?.let { dayMarks[it] } ?: DayMarkType.NONE,
                                onClick = if (onDayClick != null && kxDate != null) {
                                    { onDayClick(kxDate) }
                                } else null
                            )
                        }
                    )
                } else {
                    WeekCalendar(
                        state = rememberWeekCalendarState(
                            startDate = today.minusWeeks(52),
                            endDate = today.plusWeeks(52),
                            firstVisibleWeekDate = today,
                            firstDayOfWeek = DayOfWeek.SUNDAY
                        ),
                        dayContent = { day ->
                            val kxDate = runCatching {
                                LocalDate(day.date.year, day.date.monthValue, day.date.dayOfMonth)
                            }.getOrNull()
                            DayCell(
                                day = day.date,
                                isCurrentMonth = day.position == WeekDayPosition.RangeDate,
                                isToday = day.date == today,
                                markType = kxDate?.let { dayMarks[it] } ?: DayMarkType.NONE,
                                onClick = if (onDayClick != null && kxDate != null) {
                                    { onDayClick(kxDate) }
                                } else null
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DaysOfWeekHeader() {
    val dayLabels = listOf("D", "S", "T", "Q", "Q", "S", "S")
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        dayLabels.forEach { label ->
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DayCell(
    day: java.time.LocalDate,
    isCurrentMonth: Boolean,
    isToday: Boolean,
    markType: DayMarkType,
    onClick: (() -> Unit)? = null
) {
    val dotColor = when (markType) {
        DayMarkType.INCOME_ONLY -> IncomeGreen
        DayMarkType.EXPENSE_ONLY -> ExpenseRed
        DayMarkType.BOTH -> TransferBlue
        DayMarkType.NONE -> Color.Transparent
    }
    Column(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(if (isToday) MaterialTheme.colorScheme.primary else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = day.dayOfMonth.toString(),
                fontSize = 12.sp,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                color = when {
                    isToday -> MaterialTheme.colorScheme.onPrimary
                    !isCurrentMonth -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
        }
        Box(
            modifier = Modifier
                .size(4.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
    }
}
