package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatAlignLeft
import androidx.compose.material.icons.automirrored.filled.FormatAlignRight
import androidx.compose.material.icons.automirrored.filled.FormatIndentDecrease
import androidx.compose.material.icons.automirrored.filled.FormatIndentIncrease
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddChart
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FontDownload
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material.icons.filled.FormatAlignJustify
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatColorText
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatOverline
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.FormatStrikethrough
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Margin
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.ViewColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Note
import com.example.util.FontProvider

@Composable
fun RichTextEditorToolbar(
    note: Note,
    onNoteChange: (Note) -> Unit,
    onOpenMathDialog: () -> Unit,
    onOpenSymbolDialog: () -> Unit,
    onOpenDrawingDialog: () -> Unit
) {
    var showFontMenu by remember { mutableStateOf(false) }
    var showMarginMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        // Row 1: Direction Tabs (LTR / RTL), Font Size, Bold/Italic/Underline/Strikethrough
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // LTR / RTL Direction Switcher Tabs
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.padding(end = 4.dp)
            ) {
                Row(modifier = Modifier.padding(2.dp)) {
                    FilterChip(
                        selected = note.textDirection == "LTR",
                        onClick = { onNoteChange(note.copy(textDirection = "LTR", textAlignment = "LEFT")) },
                        label = { Text("LTR ➔", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    FilterChip(
                        selected = note.textDirection == "RTL",
                        onClick = { onNoteChange(note.copy(textDirection = "RTL", textAlignment = "RIGHT")) },
                        label = { Text("⮘ RTL (Urdu/Arabic)", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            VerticalDivider(modifier = Modifier.height(24.dp))

            // Font Size Controls
            IconButton(
                onClick = {
                    if (note.fontSizeSp > 10) onNoteChange(note.copy(fontSizeSp = note.fontSizeSp - 2))
                }
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease Font Size")
            }
            Text(
                text = "${note.fontSizeSp} sp",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(
                onClick = {
                    if (note.fontSizeSp < 48) onNoteChange(note.copy(fontSizeSp = note.fontSizeSp + 2))
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Increase Font Size")
            }

            VerticalDivider(modifier = Modifier.height(24.dp))

            // Bold
            IconButton(
                onClick = { onNoteChange(note.copy(isBold = !note.isBold)) },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (note.isBold) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                )
            ) {
                Icon(Icons.Default.FormatBold, contentDescription = "Bold")
            }

            // Italic
            IconButton(
                onClick = { onNoteChange(note.copy(isItalic = !note.isItalic)) },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (note.isItalic) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                )
            ) {
                Icon(Icons.Default.FormatItalic, contentDescription = "Italic")
            }

            // Underline
            IconButton(
                onClick = { onNoteChange(note.copy(isUnderline = !note.isUnderline)) },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (note.isUnderline) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                )
            ) {
                Icon(Icons.Default.FormatUnderlined, contentDescription = "Underline")
            }

            // Strikethrough
            IconButton(
                onClick = { onNoteChange(note.copy(isStrikethrough = !note.isStrikethrough)) },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (note.isStrikethrough) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                )
            ) {
                Icon(Icons.Default.FormatStrikethrough, contentDescription = "Strikethrough")
            }

            // Overline (line above word for math/exam questions)
            IconButton(
                onClick = { onNoteChange(note.copy(isOverline = !note.isOverline)) },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (note.isOverline) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                )
            ) {
                Icon(Icons.Default.FormatOverline, contentDescription = "Overline (Exam line)")
            }
        }

        // Row 2: Font Family Dropdown, Columns, Alignment, Tab Indent, Math, Symbols, Drawing, Margins
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Font Family Selector Dropdown
            Box {
                FilterChip(
                    selected = true,
                    onClick = { showFontMenu = true },
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FontDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(note.fontName, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                )

                DropdownMenu(
                    expanded = showFontMenu,
                    onDismissRequest = { showFontMenu = false }
                ) {
                    val grouped = FontProvider.fontOptions.groupBy { it.languageGroup }
                    grouped.forEach { (group, fontList) ->
                        DropdownMenuItem(
                            text = { Text("--- $group ---", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                            onClick = {},
                            enabled = false
                        )
                        fontList.forEach { fontOption ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(fontOption.displayNameBengali, fontWeight = FontWeight.Medium)
                                        Text(fontOption.sampleText, fontSize = 11.sp, color = Color.Gray, fontFamily = fontOption.fontFamily)
                                    }
                                },
                                onClick = {
                                    onNoteChange(note.copy(fontName = fontOption.name))
                                    showFontMenu = false
                                }
                            )
                        }
                    }
                }
            }

            // Tab Indent Button (inserts 4-5 spaces for tab indent)
            IconButton(
                onClick = {
                    val tabSpaces = "    " // 4 spaces tab equivalent
                    onNoteChange(note.copy(content = note.content + tabSpaces))
                }
            ) {
                Icon(Icons.AutoMirrored.Filled.FormatIndentIncrease, contentDescription = "Tab Indent (4 Spaces)")
            }

            // Tab Outdent Button
            IconButton(
                onClick = {
                    if (note.content.endsWith("    ")) {
                        onNoteChange(note.copy(content = note.content.dropLast(4)))
                    }
                }
            ) {
                Icon(Icons.AutoMirrored.Filled.FormatIndentDecrease, contentDescription = "Outdent")
            }

            VerticalDivider(modifier = Modifier.height(24.dp))

            // Column Layout Switcher (1 Col / 2 Cols)
            IconButton(
                onClick = {
                    val nextCols = if (note.columnCount == 1) 2 else 1
                    onNoteChange(note.copy(columnCount = nextCols))
                },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (note.columnCount == 2) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                )
            ) {
                Icon(Icons.Default.ViewColumn, contentDescription = "2 Columns Layout")
            }

            // Align Left, Center, Right, Justify
            IconButton(
                onClick = { onNoteChange(note.copy(textAlignment = "LEFT")) }
            ) {
                Icon(Icons.AutoMirrored.Filled.FormatAlignLeft, contentDescription = "Align Left")
            }
            IconButton(
                onClick = { onNoteChange(note.copy(textAlignment = "CENTER")) }
            ) {
                Icon(Icons.Default.FormatAlignCenter, contentDescription = "Align Center")
            }
            IconButton(
                onClick = { onNoteChange(note.copy(textAlignment = "RIGHT")) }
            ) {
                Icon(Icons.AutoMirrored.Filled.FormatAlignRight, contentDescription = "Align Right")
            }

            VerticalDivider(modifier = Modifier.height(24.dp))

            // Math Vertical Calculation Button
            FilterChip(
                selected = false,
                onClick = onOpenMathDialog,
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("উপরে-নিচে অংক", fontSize = 11.sp)
                    }
                }
            )

            // Symbols & Exam Brackets Button
            FilterChip(
                selected = false,
                onClick = onOpenSymbolDialog,
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Functions, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("সিম্বল ও ব্রাকেট", fontSize = 11.sp)
                    }
                }
            )

            // Drawing Button
            FilterChip(
                selected = !note.drawingDataJson.isNullOrEmpty(),
                onClick = onOpenDrawingDialog,
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Gesture, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ড্রইং", fontSize = 11.sp)
                    }
                }
            )

            // Margin Selector
            Box {
                FilterChip(
                    selected = note.marginType != "NORMAL",
                    onClick = { showMarginMenu = true },
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Margin, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("মার্জিন", fontSize = 11.sp)
                        }
                    }
                )

                DropdownMenu(
                    expanded = showMarginMenu,
                    onDismissRequest = { showMarginMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("স্বাভাবিক মার্জিন (Normal)") },
                        onClick = {
                            onNoteChange(note.copy(marginType = "NORMAL"))
                            showMarginMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("সংকীর্ণ মার্জিন (Narrow)") },
                        onClick = {
                            onNoteChange(note.copy(marginType = "NARROW"))
                            showMarginMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("প্রশস্ত মার্জিন (Wide)") },
                        onClick = {
                            onNoteChange(note.copy(marginType = "WIDE"))
                            showMarginMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("পরীক্ষার খাতা লাল মার্জিন (Exam Red Margin)") },
                        onClick = {
                            onNoteChange(note.copy(marginType = "EXAM_BORDER"))
                            showMarginMenu = false
                        }
                    )
                }
            }
        }
    }
}

// Editor Canvas layout wrapping the document text field with margins, columns, and fonts
@Composable
fun NoteEditorCanvas(
    note: Note,
    onNoteChange: (Note) -> Unit,
    modifier: Modifier = Modifier
) {
    val fontFamily = FontProvider.getFontFamily(note.fontName)

    val paddingValues = when (note.marginType) {
        "NARROW" -> 12.dp
        "WIDE" -> 32.dp
        "EXAM_BORDER" -> 20.dp
        else -> 16.dp
    }

    val textAlign = when (note.textAlignment) {
        "CENTER" -> TextAlign.Center
        "RIGHT" -> TextAlign.Right
        "JUSTIFY" -> TextAlign.Justify
        else -> TextAlign.Left
    }

    val textDirection = if (note.textDirection == "RTL") TextDirection.Rtl else TextDirection.Ltr

    val textDecoration = when {
        note.isUnderline && note.isStrikethrough -> TextDecoration.combine(listOf(TextDecoration.Underline, TextDecoration.LineThrough))
        note.isUnderline -> TextDecoration.Underline
        note.isStrikethrough -> TextDecoration.LineThrough
        else -> TextDecoration.None
    }

    val textStyle = TextStyle(
        fontFamily = fontFamily,
        fontSize = note.fontSizeSp.sp,
        fontWeight = if (note.isBold) FontWeight.Bold else FontWeight.Normal,
        fontStyle = if (note.isItalic) FontStyle.Italic else FontStyle.Normal,
        textAlign = textAlign,
        textDirection = textDirection,
        textDecoration = textDecoration,
        color = MaterialTheme.colorScheme.onSurface,
        lineHeight = (note.fontSizeSp * 1.5).sp
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                try {
                    Color(android.graphics.Color.parseColor(note.colorHex))
                } catch (e: Exception) {
                    MaterialTheme.colorScheme.surface
                }
            )
            .border(
                width = if (note.marginType == "EXAM_BORDER") 2.dp else 1.dp,
                color = if (note.marginType == "EXAM_BORDER") Color.Red else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
    ) {
        // Exam red margin vertical rule
        if (note.marginType == "EXAM_BORDER") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "পরীক্ষার খাতা / প্রশ্নপত্র মার্জিন",
                    fontSize = 11.sp,
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "তারিখ: ${java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date())}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }

        // Title Input
        BasicTextField(
            value = note.title,
            onValueChange = { onNoteChange(note.copy(title = it)) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = textStyle.copy(
                fontSize = (note.fontSizeSp + 6).sp,
                fontWeight = FontWeight.Bold
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            decorationBox = { innerTextField ->
                Box {
                    if (note.title.isEmpty()) {
                        Text(
                            text = "নোটের শিরোনাম লিখুন...",
                            style = textStyle.copy(
                                fontSize = (note.fontSizeSp + 6).sp,
                                color = Color.Gray
                            )
                        )
                    }
                    innerTextField()
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Content Input (1 Column vs 2 Columns)
        if (note.columnCount == 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Column 1
                Box(modifier = Modifier.weight(1f)) {
                    BasicTextField(
                        value = note.content,
                        onValueChange = { onNoteChange(note.copy(content = it)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 350.dp),
                        textStyle = textStyle,
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        decorationBox = { innerTextField ->
                            Box {
                                if (note.content.isEmpty()) {
                                    Text(
                                        text = "কলাম ১ লিখুন...",
                                        style = textStyle.copy(color = Color.Gray)
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }

                VerticalDivider(modifier = Modifier.height(350.dp))

                // Column 2 Guidance / Text
                Box(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "[কলাম ২ লঘুরূপ প্রিভিউ - টেক্সট কলাম ১ থেকে প্রবাহিত হবে]",
                        style = textStyle.copy(fontSize = 12.sp, color = Color.Gray)
                    )
                }
            }
        } else {
            // Standard 1 Column
            BasicTextField(
                value = note.content,
                onValueChange = { onNoteChange(note.copy(content = it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 400.dp),
                textStyle = textStyle,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                decorationBox = { innerTextField ->
                    Box {
                        if (note.content.isEmpty()) {
                            Text(
                                text = "এখানে আপনার বিশদ নোট, প্রশ্নের উত্তর বা নথির বিবরণ লিখুন...",
                                style = textStyle.copy(color = Color.Gray)
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }

        // Render drawing canvas overlay thumbnail if present
        if (!note.drawingDataJson.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "সংযুক্ত ড্রইং / স্কেচ:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(top = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
            ) {
                DrawingDisplayView(
                    jsonData = note.drawingDataJson ?: "",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
