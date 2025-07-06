package ru.vk.myapplicationmd


import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import android.content.Intent
import android.widget.Button
import android.widget.EditText
import java.net.URL

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnLoadFromFile).setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "text/*"
            startActivityForResult(intent, 1)
        }

        findViewById<Button>(R.id.btnLoadFromUrl).setOnClickListener {
            val url = findViewById<EditText>(R.id.etUrl).text.toString()
            Thread {
                try {
                    val content = URL(url).readText()
                    runOnUiThread { openViewer(content) }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(this, "Ошибка загрузки", Toast.LENGTH_SHORT).show()
                    }
                }
            }.start()
        }
    }

    private fun openViewer(content: String) {
        val intent = Intent(this, ViewerActivity::class.java)
        intent.putExtra("markdown", content)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            val uri = data.data ?: return
            val text = contentResolver.openInputStream(uri)?.bufferedReader()?.readText() ?: ""
            openViewer(text)
        }
    }
}