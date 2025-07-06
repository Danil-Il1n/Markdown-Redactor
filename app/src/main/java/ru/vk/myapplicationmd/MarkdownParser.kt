package ru.vk.myapplicationmd

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import java.net.URL

object MarkdownParser {
    fun parse(text: String, context: Context): List<View> {
        val views = mutableListOf<View>()
        val lines = text.lines()
        var i = 0

        while (i < lines.size) {
            val line = lines[i]
            when {
                line.startsWith("# ") -> createHeader(line, 1, views, context)
                line.startsWith("## ") -> createHeader(line, 2, views, context)
                line.startsWith("### ") -> createHeader(line, 3, views, context)
                line.startsWith("#### ") -> createHeader(line, 4, views, context)
                line.startsWith("##### ") -> createHeader(line, 5, views, context)
                line.startsWith("###### ") -> createHeader(line, 6, views, context)

                line.startsWith("|") -> {
                    val tableData = mutableListOf<String>()
                    while (i < lines.size && lines[i].startsWith("|")) {
                        tableData.add(lines[i])
                        i++
                    }
                    i--
                    createTable(tableData, views, context)
                }

                line.startsWith("![") -> {
                    createImage(line, views, context)
                }

                else -> createFormattedText(line, views, context)
            }
            i++
        }
        return views
    }

    private fun createHeader(line: String, level: Int, views: MutableList<View>, context: Context) {
        val tv = TextView(context).apply {
            text = line.substring(level + 1).trim()
            textSize = (24 - level * 2).toFloat()
            setTypeface(null, Typeface.BOLD)
        }
        views.add(tv)
    }

    private fun createTable(rows: List<String>, views: MutableList<View>, context: Context) {
        val table = TableLayout(context)
        table.setPadding(0, 16, 0, 16)

        var isFirstRow = true

        for (row in rows) {
            if (row.matches(Regex("\\|\\s*[-:]+\\s*(\\|\\s*[-:]+\\s*)+\\|?"))) continue

            val tr = TableRow(context)
            val cells = row.trim().split("|").filter { it.isNotBlank() }

            for (cell in cells) {
                val tv = TextView(context).apply {
                    text = parseInlineMarkdown(cell.trim())
                    setPadding(16, 8, 16, 8)
                    setTextColor(Color.GRAY)

                }
                tr.addView(tv)
            }

            table.addView(tr)

            if (isFirstRow) {
                isFirstRow = false
            }
        }

        views.add(table)
    }

    private fun createImage(line: String, views: MutableList<View>, context: Context) {
        val match = Regex("!\\[(.*?)]\\((.*?)\\)").find(line)
        match?.let {
            val alt = it.groupValues[1]
            val url = it.groupValues[2]
            val iv = ImageView(context)

            Thread {
                try {
                    val bmp = BitmapFactory.decodeStream(URL(url).openStream())
                    ImageCache.put(url, bmp)
                    (context as Activity).runOnUiThread { iv.setImageBitmap(bmp) }
                } catch (e: Exception) {
                    (context as Activity).runOnUiThread {
                        iv.setImageResource(R.drawable.ic_error)
                        iv.contentDescription = "Ошибка загрузки: $alt"
                    }
                }
            }.start()

            views.add(iv)
        }
    }

    private fun createFormattedText(line: String, views: MutableList<View>, context: Context) {
        if (line.isBlank()) return

        val tv = TextView(context)
        val spanned = parseInlineMarkdown(line)
        tv.text = spanned
        views.add(tv)
    }

    private fun parseInlineMarkdown(text: String): Spanned {
        val sb = SpannableStringBuilder()
        val tagStack = ArrayDeque<Pair<String, Int>>()
        var i = 0

        while (i < text.length) {
            when {
                text.startsWith("***", i) -> {
                    val tag = "***"
                    handleTag(sb, tagStack, tag, i, StyleSpan(Typeface.BOLD_ITALIC))
                    i += 3
                }

                text.startsWith("**", i) -> {
                    val tag = "**"
                    handleTag(sb, tagStack, tag, i, StyleSpan(Typeface.BOLD))
                    i += 2
                }

                text.startsWith("*", i) -> {
                    val tag = "*"
                    handleTag(sb, tagStack, tag, i, StyleSpan(Typeface.ITALIC))
                    i += 1
                }

                text.startsWith("~~", i) -> {
                    val tag = "~~"
                    handleTag(sb, tagStack, tag, i, StrikethroughSpan())
                    i += 2
                }

                else -> {
                    sb.append(text[i])
                    i++
                }
            }
        }

        return sb
    }

    private fun handleTag(
        sb: SpannableStringBuilder,
        tagStack: MutableList<Pair<String, Int>>,
        tag: String,
        position: Int,
        span: Any
    ) {
        val reverseIndex = tagStack.indexOfLast { it.first == tag }
        if (reverseIndex != -1) {
            val (t, start) = tagStack.removeAt(reverseIndex)
            val end = sb.length
            sb.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        } else {
            tagStack.add(tag to sb.length)
        }
    }
}