package com.wowdark.nequilistener

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/**
 * Guarda cada pago detectado en SharedPreferences como una cola simple.
 * Para el MVP esto es suficiente; si el volumen crece, migrar a SQLite/Room
 * y agregar la lógica real de sincronización con Supabase (marcar cada
 * registro como sincronizado o pendiente).
 */
object PagoLocalStore {

    private const val PREFS = "nequi_listener_prefs"
    private const val KEY_PAGOS = "pagos_pendientes"

    fun guardar(context: Context, pago: PagoDetectado) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val arrayActual = JSONArray(prefs.getString(KEY_PAGOS, "[]"))

        val obj = JSONObject().apply {
            put("monto", pago.monto)
            put("remitente", pago.remitente)
            put("timestamp", pago.timestamp)
            put("sincronizado", false)
        }
        arrayActual.put(obj)

        prefs.edit().putString(KEY_PAGOS, arrayActual.toString()).apply()
    }

    fun obtenerTodos(context: Context): JSONArray {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return JSONArray(prefs.getString(KEY_PAGOS, "[]"))
    }
}
