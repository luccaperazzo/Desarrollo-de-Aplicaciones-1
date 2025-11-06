# Implementación de Controles OWASP Mobile Top 10

Este documento describe los controles de seguridad implementados según el OWASP Mobile Top 10.

## Controles Implementados

### ✅ M1: Improper Platform Usage

**Problema identificado:**
- `allowBackup="true"` permite que los datos de la app se respalden automáticamente
- Los backups pueden contener datos sensibles (tokens, información de usuario)
- Los backups pueden ser accedidos por otras apps o mediante ADB sin root

**Solución implementada:**
- ✅ `allowBackup="false"` deshabilitado en AndroidManifest.xml
- ✅ Previene que datos sensibles se incluyan en backups automáticos
- ✅ Protege contra extracción de datos mediante ADB backup

**Archivos modificados:**
- `app/src/main/AndroidManifest.xml`

---

### ✅ M7: Client Code Quality

**Problema identificado:**
- ProGuard deshabilitado en builds de release
- Código no ofuscado, fácil de hacer ingeniería inversa

**Solución implementada:**
- ✅ ProGuard habilitado en release (`isMinifyEnabled = true`)
- ✅ Shrink resources habilitado (`isShrinkResources = true`)
- ✅ Reglas de ProGuard configuradas para mantener clases necesarias
- ✅ Ofuscación de nombres de clases y métodos

**Archivos modificados:**
- `app/build.gradle.kts`
- `app/proguard-rules.pro`

**Reglas ProGuard:**
- Mantiene clases de Retrofit, Gson, Room, Firebase
- Mantiene modelos de datos necesarios
- Ofusca el resto del código

---

## Referencias

- [OWASP Mobile Top 10](https://owasp.org/www-project-mobile-top-10/)
- [Android Security Best Practices](https://developer.android.com/training/best-security)

