package com.wowdark.nequilistener

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

/**
 * Escucha TODAS las notificaciones del sistema (con permiso del usuario)
 * y filtra únicamente las que vienen del paquete de Nequi.
 *
 * IMPORTANTE (validar antes de probar en tu negocio):
 * - El paquete de Nequi normalmente es "com.nequi.MobileApp", pero confírmalo
 *   yendo a Ajustes > Apps > Nequi > y viendo el nombre del paquete, o con
 *   `adb shell pm list packages | grep nequi` conectando el celular por USB.
 * - El texto exacto de la notificación puede variar. Este código incluye
 *   varios patrones comunes, pero debes ajustarlos viendo notificaciones
 *   REALES en tu negocio (revisa el Logcat, ahí quedará impreso el texto
 *   crudo de cada notificación de Nequi que llegue, para que ajustes el regex).
 */
class NequiNotificationListener : NotificationListenerService() {

    companion object {
        private const val TAG = "NequiListener"

        // Ajusta esto al paquete real de Nequi en tu celular de pruebas
        private const val NEQUI_PACKAGE = "com.nequi.MobileApp"

        // Patrones de ejemplo — deben ajustarse con el texto real de las notificaciones.
        // Cubren variaciones típicas tipo "Recibiste $50.000 de Juan Pérez"
        private val MONTO_PATTERN = Pattern.compile(
            "\\$\\s?([0-9]{1,3}(?:[.,][0-9]{3})*)"
        )

        private val REMITENTE_PATTERN = Pattern.compile(
            "de\\s+([A-Za-zÁÉÍÓÚáéíóúÑñ ]+?)(?:\\.|$)"
        )
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)

        if (sbn.packageName != NEQUI_PACKAGE) return

        val extras = sbn.notification.extras
        val titulo = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: ""
        val texto = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""
        val cuerpoCompleto = "$titulo $texto"

        // Deja este log mientras pruebas: te muestra el texto crudo de cada
        // notificación de Nequi para que ajustes los patrones si hace falta.
        Log.d(TAG, "Notificación de Nequi recibida -> título: '$titulo' | texto: '$texto'")

        val montoMatcher = MONTO_PATTERN.matcher(cuerpoCompleto)
        val remitenteMatcher = REMITENTE_PATTERN.matcher(cuerpoCompleto)

        val monto = if (montoMatcher.find()) montoMatcher.group(1) else null
        val remitente = if (remitenteMatcher.find()) remitenteMatcher.group(1)?.trim() else null

        if (monto != null) {
            val evento = PagoDetectado(
                monto = monto,
                remitente = remitente ?: "Desconocido",
                timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    .format(Date(sbn.postTime))
            )
            procesarPago(evento)
        } else {
            Log.w(TAG, "Notificación de Nequi detectada pero no se pudo extraer el monto. Revisa el patrón.")
        }
    }

    private fun procesarPago(pago: PagoDetectado) {
        Log.i(TAG, "PAGO DETECTADO -> $pago")

        // Guarda localmente primero (para no perder el dato si no hay internet)
        PagoLocalStore.guardar(applicationContext, pago)

        // TODO: aquí se dispara el resto del flujo del MVP:
        // 1. Sonido/aviso en pantalla
        // 2. Intento de sincronización con Supabase si hay internet
        // 3. Actualización de la pantalla de historial dentro de la app
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.i(TAG, "Listener conectado correctamente. Escuchando notificaciones.")
    }
}

data class PagoDetectado(
    val monto: String,
    val remitente: String,
    val timestamp: String
)
