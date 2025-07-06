package ru.vk.myapplicationmd

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ViewerActivity : AppCompatActivity() {
    private lateinit var markdown: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewer)

        markdown = intent.getStringExtra("markdown") ?: ""

        val container = findViewById<LinearLayout>(R.id.mdContainer)
        val views = MarkdownParser.parse(markdown, this)
        views.forEach { container.addView(it) }

        findViewById<Button>(R.id.btnEdit).setOnClickListener {
            val intent = Intent(this, EditorActivity::class.java)
            intent.putExtra("markdown", markdown)
            startActivity(intent)
        }
    }
}