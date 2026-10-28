package com.steve.oneia

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import java.util.concurrent.Executors

class MainActivity : Activity() {
    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var apiKey: EditText
    private lateinit var prompt: EditText
    private lateinit var result: TextView
    private val prefs by lazy { getSharedPreferences("oneia", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(12, 10, 22)
        window.navigationBarColor = Color.BLACK

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 24, 20, 20)
            setBackgroundColor(Color.rgb(12, 10, 22))
        }

        fun text(value: String, size: Float, color: Int = Color.WHITE) =
            TextView(this).apply {
                text = value
                textSize = size
                setTextColor(color)
            }

        val scroll = ScrollView(this)
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 0, 0, 16)
        }

        content.addView(text("1-IA", 30f, Color.rgb(190, 110, 255)))
        content.addView(text("Assistant flottant intelligent", 15f))
        content.addView(text("Configure Gemini puis demande une suggestion.", 14f))

        apiKey = EditText(this).apply {
            hint = "Clé API Gemini"
            setSingleLine(true)
            setTextColor(Color.WHITE)
            setHintTextColor(Color.LTGRAY)
            setBackgroundColor(Color.rgb(35, 29, 48))
            setPadding(12, 8, 12, 8)
            inputType = 129
            setText(prefs.getString("gemini_key", ""))
        }
        content.addView(apiKey, fieldParams())

        prompt = EditText(this).apply {
            hint = "Ex. : propose 5 messages naturels pour relancer un ami"
            setTextColor(Color.WHITE)
            setHintTextColor(Color.LTGRAY)
            setBackgroundColor(Color.rgb(35, 29, 48))
            setPadding(12, 8, 12, 8)
            minLines = 3
            gravity = Gravity.TOP
        }
        content.addView(prompt, fieldParams())

        val generate = Button(this).apply {
            text = "Générer avec Gemini"
            setOnClickListener { generateAnswer() }
        }
        content.addView(generate, fieldParams())

        content.addView(Button(this).apply {
            text = "Activer la bulle flottante"
            setOnClickListener {
                if (!Settings.canDrawOverlays(this@MainActivity)) {
                    startActivity(
                        Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:$packageName")
                        )
                    )
                    Toast.makeText(
                        this@MainActivity,
                        "Autorise l'affichage par-dessus les autres applications, puis réessaie.",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    startService(
                        Intent(this@MainActivity, FloatingAssistantService::class.java)
                    )
                    Toast.makeText(this@MainActivity, "Bulle activée", Toast.LENGTH_SHORT).show()
                }
            }
        }, fieldParams())

        content.addView(Button(this).apply {
            text = "Autoriser l'accès aux notifications"
            setOnClickListener {
                startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
            }
        }, fieldParams())

        content.addView(Button(this).apply {
            text = "Paramètres d'automatisation"
            setOnClickListener {
                startActivity(Intent(this@MainActivity, AutomationSettingsActivity::class.java))
            }
        }, fieldParams())

        content.addView(text("Résultat", 20f, Color.rgb(190, 110, 255)))
        result = text("Les suggestions apparaîtront ici.", 16f)
        content.addView(result)

        scroll.addView(content)
        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
            )
        )
        setContentView(root)
    }

    private fun fieldParams() = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    ).apply {
        setMargins(0, 12, 0, 12)
    }

    private fun generateAnswer() {
        val key = apiKey.text.toString().trim()
        val question = prompt.text.toString().trim()

        if (key.isEmpty() || question.isEmpty()) {
            Toast.makeText(this, "Entre la clé API et une demande.", Toast.LENGTH_SHORT).show()
            return
        }

        prefs.edit().putString("gemini_key", key).apply()
        result.text = "Génération en cours…"

        executor.execute {
            try {
                val answer = GeminiClient.generate(key, question)
                runOnUiThread { result.text = answer }
            } catch (e: Exception) {
                runOnUiThread {
                    result.text = "Erreur : ${e.message ?: "connexion impossible"}"
                }
            }
        }
    }

    override fun onDestroy() {
        executor.shutdownNow()
        super.onDestroy()
    }
}
