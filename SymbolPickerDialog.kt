package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class SymbolCategory(
    val title: String,
    val symbols: List<String>
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SymbolPickerDialog(
    onDismiss: () -> Unit,
    onSymbolSelected: (symbol: String) -> Unit
) {
    val categories = listOf(
        SymbolCategory(
            title = "প্রশ্ন ও ব্রাকেট",
            symbols = listOf(
                "(ক)", "(খ)", "(গ)", "(ঘ)", "১.", "২.", "৩.", "৪.", "৫.",
                "[১]", "[২]", "[৩]", "১|", "২|", "৩|", "Q1.", "Q2.", "Q3.",
                "( )", "[ ]", "{ }", "⟨ ⟩", "« »", "【 】", "⟦ ⟧", "⌈ ⌉", "⌊ ⌋",
                "_______", "․․․․․․․", "X̄", "▔▔▔▔▔", "‗‗‗‗‗"
            )
        ),
        SymbolCategory(
            title = "গণিত ও ভগ্নাংশ",
            symbols = listOf(
                "½", "⅓", "¼", "¾", "⅕", "⅙", "⅛", "⅔", "⅜", "⅝", "⅞",
                "+", "-", "×", "÷", "=", "≠", "±", "∓", "≈", "≡", "≤", "≥",
                "√", "∛", "∜", "π", "∞", "∑", "∫", "∆", "∇", "∈", "∉", "⊂", "⊃",
                "x²", "x³", "x⁴", "xⁿ", "x₀", "x₁", "x₂", "x₃", "θ", "α", "β", "γ"
            )
        ),
        SymbolCategory(
            title = "চিহ্ন ও তীর",
            symbols = listOf(
                "✓", "✕", "✔", "✖", "★", "☆", "❖", "•", "◦", "▪", "▫",
                "➔", "➜", "➤", "➡", "⬅", "⬆", "⬇", "↔", "↕", "⇒", "⇔",
                "❶", "❷", "❸", "❹", "❺", "❻", "❼", "❽", "❾", "❿",
                "▲", "▼", "◄", "►", "◆", "◇", "■", "□", "¶", "§", "©", "®"
            )
        ),
        SymbolCategory(
            title = "আরবি ও কুরআন",
            symbols = listOf(
                "۝", "۩", "ﷻ", "ﷺ", "٪", "؀", "۞", "ۦ", "ۧ", "ۨ", "۪", "۫", "۬"
            )
        )
    )

    var selectedTabIndex by remember { mutableIntStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "সিম্বল, ব্রাকেট ও প্রশ্ন বিন্যাস",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    edgePadding = 0.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    categories.forEachIndexed { index, category ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(category.title, fontSize = 13.sp) }
                        )
                    }
                }

                val currentCategory = categories[selectedTabIndex]

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(height = 240.dp, width = 300.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            currentCategory.symbols.forEach { sym ->
                                Card(
                                    modifier = Modifier
                                        .clickable {
                                            onSymbolSelected(sym)
                                            onDismiss()
                                        },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                ) {
                                    Box(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = sym,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বন্ধ করুন")
            }
        }
    )
}
