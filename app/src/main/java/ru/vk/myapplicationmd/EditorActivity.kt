package ru.vk.myapplicationmd

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class EditorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        val editor = findViewById<EditText>(R.id.etMarkdown)
        editor.setText(intent.getStringExtra("markdown") ?: "")

        findViewById<Button>(R.id.btnSave).setOnClickListener {
            val intent = Intent(this, ViewerActivity::class.java)
            intent.putExtra("markdown", editor.text.toString())
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnBold).setOnClickListener {
            insertSyntax(editor, "**")
        }
        findViewById<Button>(R.id.btnItalic).setOnClickListener {
            insertSyntax(editor, "*")
        }
        findViewById<Button>(R.id.btnStrike).setOnClickListener {
            insertSyntax(editor, "~~")
        }
        findViewById<Button>(R.id.btnTable).setOnClickListener {
            val sample =
                "| Заголовок1 | Заголовок2 |\n|-----------|-----------|\n| Значение1 | Значение2 |\n"
            editor.append("\n$sample")
        }
    }

    private fun insertSyntax(editText: EditText, syntax: String) {
        val start = editText.selectionStart
        val end = editText.selectionEnd
        val text = editText.text
        text.replace(start, end, "$syntax${text.subSequence(start, end)}$syntax")
    }
}