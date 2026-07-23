package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.model.Note
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object DocumentExportUtil {

    // 1. Export as PDF
    fun exportToPdf(context: Context, note: Note): File? {
        return try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size in points
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            val margin = when (note.marginType) {
                "NARROW" -> 20f
                "WIDE" -> 60f
                "EXAM_BORDER" -> 40f
                else -> 36f
            }

            val paint = Paint().apply {
                isAntiAlias = true
                color = AndroidColor.BLACK
            }

            var currentY = margin + 20f

            // Exam paper border margin line if selected
            if (note.marginType == "EXAM_BORDER") {
                val borderPaint = Paint().apply {
                    color = AndroidColor.RED
                    strokeWidth = 1.5f
                    style = Paint.Style.STROKE
                }
                canvas.drawRect(margin, margin, pageInfo.pageWidth - margin, pageInfo.pageHeight - margin, borderPaint)
                // Red left vertical margin line
                canvas.drawLine(margin + 40f, margin, margin + 40f, pageInfo.pageHeight - margin, borderPaint)
            }

            // Document Header
            val titlePaint = TextPaint().apply {
                isAntiAlias = true
                textSize = (note.fontSizeSp + 6).toFloat()
                isFakeBoldText = true
                color = AndroidColor.parseColor("#1E1B4B")
            }

            val titleLayout = StaticLayout.Builder.obtain(
                note.title.ifEmpty { "শিরোনামহীন নোট" },
                0,
                note.title.ifEmpty { "শিরোনামহীন নোট" }.length,
                titlePaint,
                (pageInfo.pageWidth - (margin * 2)).toInt()
            ).build()

            canvas.save()
            canvas.translate(margin + if (note.marginType == "EXAM_BORDER") 45f else 0f, currentY)
            titleLayout.draw(canvas)
            canvas.restore()

            currentY += titleLayout.height + 15f

            // Divider Line
            val linePaint = Paint().apply {
                color = AndroidColor.LTGRAY
                strokeWidth = 1f
            }
            canvas.drawLine(margin, currentY, pageInfo.pageWidth - margin, currentY, linePaint)
            currentY += 15f

            // Date stamp
            val dateStr = SimpleDateFormat("dd MMMM, yyyy - hh:mm a", Locale.getDefault()).format(Date(note.updatedAt))
            val datePaint = TextPaint().apply {
                isAntiAlias = true
                textSize = 10f
                color = AndroidColor.DKGRAY
            }
            canvas.drawText("তারিখ: $dateStr | ফন্ট: ${note.fontName}", margin + if (note.marginType == "EXAM_BORDER") 45f else 0f, currentY, datePaint)
            currentY += 20f

            // Body Content
            val bodyPaint = TextPaint().apply {
                isAntiAlias = true
                textSize = note.fontSizeSp.toFloat()
                isFakeBoldText = note.isBold
                textSkewX = if (note.isItalic) -0.25f else 0f
                isUnderlineText = note.isUnderline
                isStrikeThruText = note.isStrikethrough
                color = AndroidColor.BLACK
            }

            val alignment = when (note.textAlignment) {
                "CENTER" -> Layout.Alignment.ALIGN_CENTER
                "RIGHT" -> Layout.Alignment.ALIGN_OPPOSITE
                else -> Layout.Alignment.ALIGN_NORMAL
            }

            val bodyLayout = StaticLayout.Builder.obtain(
                note.content,
                0,
                note.content.length,
                bodyPaint,
                (pageInfo.pageWidth - (margin * 2) - if (note.marginType == "EXAM_BORDER") 45f else 0f).toInt()
            ).setAlignment(alignment).build()

            canvas.save()
            canvas.translate(margin + if (note.marginType == "EXAM_BORDER") 45f else 0f, currentY)
            bodyLayout.draw(canvas)
            canvas.restore()

            pdfDocument.finishPage(page)

            val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.cacheDir
            if (!dir.exists()) dir.mkdirs()
            val sanitizeTitle = note.title.replace("[^a-zA-Z0-9-áéíóúÁÉÍÓÚঅ-হ০-৯]".toRegex(), "_").take(20).ifEmpty { "Note" }
            val file = File(dir, "${sanitizeTitle}_${System.currentTimeMillis()}.pdf")

            FileOutputStream(file).use { out ->
                pdfDocument.writeTo(out)
            }
            pdfDocument.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // 2. Export as Word (.docx) ZIP format
    fun exportToDocx(context: Context, note: Note): File? {
        return try {
            val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.cacheDir
            if (!dir.exists()) dir.mkdirs()
            val sanitizeTitle = note.title.replace("[^a-zA-Z0-9-অ-হ০-৯]".toRegex(), "_").take(20).ifEmpty { "Document" }
            val file = File(dir, "${sanitizeTitle}_${System.currentTimeMillis()}.docx")

            val alignXml = when (note.textAlignment) {
                "CENTER" -> "<w:jc w:val=\"center\"/>"
                "RIGHT" -> "<w:jc w:val=\"right\"/>"
                else -> "<w:jc w:val=\"left\"/>"
            }

            val bTag = if (note.isBold) "<w:b/>" else ""
            val iTag = if (note.isItalic) "<w:i/>" else ""
            val uTag = if (note.isUnderline) "<w:u w:val=\"single\"/>" else ""
            val strikeTag = if (note.isStrikethrough) "<w:strike/>" else ""
            val rtlTag = if (note.textDirection == "RTL") "<w:rtl/>" else ""

            val titleEscaped = note.title.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
            val lines = note.content.split("\n")
            val contentParagraphs = StringBuilder()

            for (line in lines) {
                val escapedLine = line.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                contentParagraphs.append("""
                    <w:p>
                        <w:pPr>
                            $alignXml
                            $rtlTag
                        </w:pPr>
                        <w:r>
                            <w:rPr>
                                <w:rFonts w:ascii="${note.fontName}" w:hAnsi="${note.fontName}" w:cs="${note.fontName}"/>
                                <w:sz w:val="${note.fontSizeSp * 2}"/>
                                $bTag $iTag $uTag $strikeTag
                            </w:rPr>
                            <w:t xml:space="preserve">$escapedLine</w:t>
                        </w:r>
                    </w:p>
                """.trimIndent())
            }

            val docXml = """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
                    <w:body>
                        <w:p>
                            <w:pPr>
                                $alignXml
                            </w:pPr>
                            <w:r>
                                <w:rPr>
                                    <w:rFonts w:ascii="${note.fontName}" w:hAnsi="${note.fontName}" w:cs="${note.fontName}"/>
                                    <w:b/>
                                    <w:sz w:val="${(note.fontSizeSp + 8) * 2}"/>
                                    <w:color w:val="1E1B4B"/>
                                </w:rPr>
                                <w:t xml:space="preserve">$titleEscaped</w:t>
                            </w:r>
                        </w:p>
                        $contentParagraphs
                    </w:body>
                </w:document>
            """.trimIndent()

            val contentTypesXml = """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
                    <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
                    <Default Extension="xml" ContentType="application/xml"/>
                    <Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/>
                </Types>
            """.trimIndent()

            val relsXml = """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
                    <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/>
                </Relationships>
            """.trimIndent()

            ZipOutputStream(BufferedOutputStream(FileOutputStream(file))).use { zos ->
                // [Content_Types].xml
                zos.putNextEntry(ZipEntry("[Content_Types].xml"))
                zos.write(contentTypesXml.toByteArray())
                zos.closeEntry()

                // _rels/.rels
                zos.putNextEntry(ZipEntry("_rels/.rels"))
                zos.write(relsXml.toByteArray())
                zos.closeEntry()

                // word/document.xml
                zos.putNextEntry(ZipEntry("word/document.xml"))
                zos.write(docXml.toByteArray())
                zos.closeEntry()
            }

            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // 3. Export as Plain Text (.txt)
    fun exportToTxt(context: Context, note: Note): File? {
        return try {
            val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.cacheDir
            if (!dir.exists()) dir.mkdirs()
            val sanitizeTitle = note.title.replace("[^a-zA-Z0-9-অ-হ০-৯]".toRegex(), "_").take(20).ifEmpty { "Note" }
            val file = File(dir, "${sanitizeTitle}_${System.currentTimeMillis()}.txt")

            val fullText = "${note.title}\n${"=".repeat(note.title.length.coerceAtLeast(10))}\n\n${note.content}"

            OutputStreamWriter(FileOutputStream(file), "UTF-8").use { writer ->
                writer.write(fullText)
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // 4. Share Document File
    fun shareFile(context: Context, file: File, mimeType: String, title: String) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, title)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "$title শেয়ার করুন"))
    }

    // 5. Direct Copy/Share to Google Docs
    fun shareToGoogleDocs(context: Context, note: Note) {
        val file = exportToDocx(context, note)
        if (file != null) {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, note.title)
                setPackage("com.google.android.apps.docs")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                // Fallback to general intent chooser if Google Docs app is not direct target
                shareFile(context, file, "application/vnd.openxmlformats-officedocument.wordprocessingml.document", note.title)
            }
        } else {
            Toast.makeText(context, "ফাইল তৈরি করতে সমস্যা হয়েছে", Toast.LENGTH_SHORT).show()
        }
    }
}
