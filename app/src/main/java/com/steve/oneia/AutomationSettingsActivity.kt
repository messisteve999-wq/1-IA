package com.steve.oneia

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class AutomationSettingsActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("oneia", MODE_PRIVATE)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 24, 20, 20)
            setBackgroundColor(Color.rgb(12, 10, 22))
        }

        root.addView(TextView(this).apply {
            text = "1-IA · Automatisation"
            textSize = 24f
            setTextColor(Color.rgb(190, 110, 255))
        })

        root.addView(TextView(this).apply {
            text = "Contacts autorisés (un identifiant par ligne)"
            textSize = 16f
            setTextColor(Color.WHITE)
        })

        val contacts = EditText(this).apply {
            hint = "Ex. : identifiant du contact"
            setTextColor(Color.WHITE)
            setHintTextColor(Color.LTGRAY)
            minLines = 4
            gravity = android.view.Gravity.TOP
            setText(prefs.getString("allowed_contacts", ""))
        }
        root.addView(
            contacts,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        root.addView(TextView(this).apply {
            text = "Historique prévu : 24 heures. Cette version initiale ne conserve pas encore de journal de messages."
            textSize = 14f
            setTextColor(Color.LTGRAY)
        })

        root.addView(Button(this).apply {
            text = "Enregistrer les contacts"
            setOnClickListener {
                prefs.edit()
                    .putString("allowed_contacts", contacts.text.toString())
                    .apply()
                Toast.makeText(
                    this@AutomationSettingsActivity,
                    "Liste enregistrée",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        root.addView(Button(this).apply {
            text = "Retour"
            setOnClickListener { finish() }
        })

        setContentView(root)
    }
}
