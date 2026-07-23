package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String = "",
    val content: String = "",
    val colorHex: String = "#FFFFFF",
    val fontName: String = "Roboto",
    val fontSizeSp: Int = 16,
    val isBold: Boolean = false,
    val isItalic: Boolean = false,
    val isUnderline: Boolean = false,
    val isStrikethrough: Boolean = false,
    val isOverline: Boolean = false,
    val textDirection: String = "LTR", // "LTR" or "RTL"
    val textAlignment: String = "LEFT", // "LEFT", "CENTER", "RIGHT", "JUSTIFY"
    val columnCount: Int = 1, // 1 or 2
    val marginType: String = "NORMAL", // "NONE", "NORMAL", "EXAM_BORDER", "NOTEBOOK_LINE"
    val isPinned: Boolean = false,
    val tag: String = "সাধারণ", // "সাধারণ", "পরীক্ষার প্রশ্ন", "গণিত", "উর্দু", "বাংলা", "আরবি"
    val drawingDataJson: String? = null, // JSON representation of drawing strokes
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
