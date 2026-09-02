# Nequi Listener - WOWDARK (MVP)

Prototipo inicial: escucha notificaciones de Nequi en el celular, extrae el
monto y el remitente, y los guarda localmente.

## Antes de compilar

1. Abre el proyecto en Android Studio (File > Open > selecciona esta carpeta).
2. Confirma el paquete real de Nequi en tu celular:
   - Ajustes > Apps > Nequi > "Nombre del paquete" (o vía `adb shell dumpsys package com.nequi.MobileApp`)
   - Si es distinto, actualízalo en `NequiNotificationListener.kt` (constante `NEQUI_PACKAGE`)
3. Instala la app en tu celular de pruebas (el que tiene Nequi del negocio).
4. Abre la app y toca "1. Activar acceso a notificaciones" -> actívalo para
   "Nequi Listener - WOWDARK" en la pantalla de Android que se abre.
5. Provoca un pago de prueba por Nequi (pídele a alguien que te envíe $1.000, por ejemplo).
6. Revisa el Logcat filtrando por "NequiListener" — ahí verás el texto EXACTO
   de la notificación real. Con eso ajustas los patrones (regex) en
   `MONTO_PATTERN` y `REMITENTE_PATTERN` si no detectó bien el monto.
7. Toca "2. Ver pagos detectados (local)" en la app para confirmar que quedó guardado.

## Siguientes pasos (fuera de este MVP)
- Conectar `PagoLocalStore` con Supabase para sincronizar en la nube.
- Agregar sonido/TTS cuando se detecte un pago.
- Foreground service + reinicio automático para que Android no mate el listener.
- Soporte multi-dispositivo (ver notas de la conversación con WOWDARK).
