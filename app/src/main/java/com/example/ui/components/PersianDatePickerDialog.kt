package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.LightBlueAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.JalaliDate
import com.example.util.PersianDateHelper
import com.example.util.toPersianDigits

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PersianDatePickerDialog(
    initialDate: JalaliDate? = null,
    onDismiss: () -> Unit,
    onDateSelected: (JalaliDate) -> Unit
) {
    val defaultDate = remember {
        initialDate ?: PersianDateHelper.gregorianToJalali(PersianDateHelper.getEffectiveToday())
    }

    var selectedYear by remember { mutableIntStateOf(defaultDate.year) }
    var selectedMonth by remember { mutableIntStateOf(defaultDate.month) }
    var selectedDay by remember { mutableIntStateOf(defaultDate.day) }

    val daysInCurrentMonth = when {
        selectedMonth <= 6 -> 31
        selectedMonth <= 11 -> 30
        else -> 29 // Esfand
    }

    if (selectedDay > daysInCurrentMonth) {
        selectedDay = daysInCurrentMonth
    }

    val availableYears = listOf(1402, 1403, 1404, 1405, 1406)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurfaceElevated,
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ElectricBlue.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "تقویم",
                        tint = ElectricBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = "انتخاب تاریخ شمسی",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Selected Date Preview Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.background)
                        .border(1.dp, ElectricBlue, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${selectedDay.toPersianDigits()} ${PersianDateHelper.monthNames[selectedMonth - 1]} ${selectedYear.toPersianDigits()}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = LightBlueAccent,
                        fontSize = 15.sp
                    )
                }

                // 1. Year selector
                Text(
                    text = "سال:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    availableYears.forEach { yr ->
                        val isSel = (selectedYear == yr)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) ElectricBlue else MaterialTheme.colorScheme.background)
                                .border(1.dp, if (isSel) ElectricBlue else DarkCardBorder, RoundedCornerShape(8.dp))
                                .clickable { selectedYear = yr }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = yr.toPersianDigits(),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSel) MaterialTheme.colorScheme.background else TextPrimary
                            )
                        }
                    }
                }

                // 2. Month selector
                Text(
                    text = "ماه:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PersianDateHelper.monthNames.forEachIndexed { idx, mName ->
                        val mNum = idx + 1
                        val isSel = (selectedMonth == mNum)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) ElectricBlue else MaterialTheme.colorScheme.background)
                                .border(1.dp, if (isSel) ElectricBlue else DarkCardBorder, RoundedCornerShape(8.dp))
                                .clickable { selectedMonth = mNum }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = mName,
                                fontSize = 11.sp,
                                color = if (isSel) MaterialTheme.colorScheme.background else TextPrimary,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                // 3. Day selector (1 to daysInMonth)
                Text(
                    text = "روز:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (d in 1..daysInCurrentMonth) {
                        val isSel = (selectedDay == d)
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) ElectricBlue else MaterialTheme.colorScheme.background)
                                .border(1.dp, if (isSel) ElectricBlue else DarkCardBorder, RoundedCornerShape(6.dp))
                                .clickable { selectedDay = d },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = d.toPersianDigits(),
                                fontSize = 11.sp,
                                color = if (isSel) MaterialTheme.colorScheme.background else TextSecondary,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onDateSelected(JalaliDate(selectedYear, selectedMonth, selectedDay))
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
            ) {
                Text("تأیید تاریخ", color = MaterialTheme.colorScheme.background, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("انصراف", color = TextSecondary)
            }
        }
    )
}
