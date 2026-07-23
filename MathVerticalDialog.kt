package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MathVerticalDialog(
    onDismiss: () -> Unit,
    onInsertMathBlock: (formattedBlock: String) -> Unit
) {
    var num1 by remember { mutableStateOf("৭৫০") }
    var num2 by remember { mutableStateOf("২৫০") }
    var operator by remember { mutableStateOf("+") }

    // Auto calculate if valid digits
    val calculatedResult: String = remember(num1, num2, operator) {
        val n1 = convertBengaliToEnglishDigits(num1).toDoubleOrNull()
        val n2 = convertBengaliToEnglishDigits(num2).toDoubleOrNull()
        if (n1 != null && n2 != null) {
            val res = when (operator) {
                "+" -> n1 + n2
                "-" -> n1 - n2
                "×" -> n1 * n2
                "÷" -> if (n2 != 0.0) n1 / n2 else null
                else -> null
            }
            if (res != null) {
                val formatted = if (res % 1.0 == 0.0) res.toLong().toString() else String.format("%.2f", res)
                convertEnglishToBengaliDigits(formatted)
            } else ""
        } else ""
    }

    var resultInput by remember { mutableStateOf(calculatedResult) }

    val formattedPreview = remember(num1, num2, operator, calculatedResult) {
        val maxLen = maxOf(num1.length, num2.length + 2, calculatedResult.length)
        val p1 = num1.padStart(maxLen)
        val p2 = "$operator ${num2.padStart(maxLen - 2)}"
        val line = "-".repeat(maxLen + 2)
        val res = (calculatedResult.ifEmpty { "____" }).padStart(maxLen)

        "$p1\n$p2\n$line\n$res\n"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "উপরে-নিচে অংক লিখুন (Vertical Math)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(text = "চিহ্ন নির্বাচন করুন:", style = MaterialTheme.typography.labelLarge)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("+" to "যোগ (+)", "-" to "বিয়োগ (-)", "×" to "গুণ (×)", "÷" to "ভাগ (÷)").forEach { (op, label) ->
                        FilterChip(
                            selected = operator == op,
                            onClick = { operator = op },
                            label = { Text(op, fontSize = 18.sp, fontWeight = FontWeight.Bold) }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = num1,
                        onValueChange = { num1 = it },
                        label = { Text("১ম সংখ্যা (উপরে)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = num2,
                        onValueChange = { num2 = it },
                        label = { Text("২য় সংখ্যা (নিচে)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Text(
                    text = "ফলাফল প্রিভিউ (Preview):",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = formattedPreview,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End,
                        lineHeight = 24.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onInsertMathBlock(formattedPreview)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("যুক্ত করুন")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল")
            }
        }
    )
}

private fun convertBengaliToEnglishDigits(input: String): String {
    val ben = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
    val eng = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
    var result = input
    for (i in 0..9) {
        result = result.replace(ben[i], eng[i])
    }
    return result
}

private fun convertEnglishToBengaliDigits(input: String): String {
    val eng = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
    val ben = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
    var result = input
    for (i in 0..9) {
        result = result.replace(eng[i], ben[i])
    }
    return result
}
