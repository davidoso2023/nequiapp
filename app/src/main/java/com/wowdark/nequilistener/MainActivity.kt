package com.wowdark.nequilistener

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var listaTexto: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 80, 40, 40)
        }

        val titulo = TextView(this).apply {
            text = "Nequi Listener - WOWDARK (MVP)"
            textSize = 20f
        }

        val botonPermiso = Button(this).apply {
            text = "1. Activar acceso a notificaciones"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
            }
        }

        val botonRefrescar = Button(this).apply {
            text = "2. Ver pagos detectados (local)"
            setOnClickListener { refrescarHistorial() }
        }

        listaTexto = TextView(this).apply {
            text = "Aún no hay pagos registrados."
            setPadding(0, 40, 0, 0)
        }

        root.addView(titulo)
        root.addView(botonPermiso)
        root.addView(botonRefrescar)
        root.addView(listaTexto)

        setContentView(ScrollView(this).apply { addView(root) })
    }

    private fun refrescarHistorial() {
        val pagos = PagoLocalStore.obtenerTodos(applicationContext)
        if (pagos.length() == 0) {
            listaTexto.text = "Aún no hay pagos registrados."
            return
        }

        val sb = StringBuilder()
        for (i in 0 until pagos.length()) {
            val p = pagos.getJSONObject(i)
            sb.append("• $${p.getString("monto")} de ${p.getString("remitente")} — ${p.getString("timestamp")}\n")
        }
        listaTexto.text = sb.toString()
    }
}
