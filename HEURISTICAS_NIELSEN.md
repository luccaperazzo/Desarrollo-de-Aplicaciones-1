# Heurísticas de Nielsen Implementadas en UADE Recipes App

Este documento lista las heurísticas de usabilidad de Jakob Nielsen que están implementadas en la aplicación UADE Recipes App.

---

## 1. Visibilidad del Estado del Sistema ✅

**Principio**: El sistema debe mantener informado al usuario sobre lo que está ocurriendo, mediante retroalimentación apropiada y en un tiempo razonable.

**Se cumple en**:
- Indicadores de carga (ProgressBar) durante login, registro y operaciones asíncronas
- Loading overlay en pantallas de creación y detalle de recetas
- Mensajes Toast para confirmar acciones exitosas ("Logueado!", "Receta creada exitosamente")
- Mensajes de error claros y específicos según el tipo de problema
- Indicación cuando se muestran datos desde caché (modo offline)

**Evidencia**:
|                                                        Indicador de Carga                                                         |                                                     Mensaje Toast                                                      |                                                        Offline                                                         |
|:---------------------------------------------------------------------------------------------------------------------------------:|:----------------------------------------------------------------------------------------------------------------------:|:----------------------------------------------------------------------------------------------------------------------:|
| ![Pantalla principal](C:\Users\lucca\AndroidStudioProjects\Desarrollo-de-Aplicaciones-1\nielsen-evidence\nielsen-evidence1.jpg) | ![asfssf](C:\Users\lucca\AndroidStudioProjects\Desarrollo-de-Aplicaciones-1\nielsen-evidence\nielsen-evidence1-2.jpg) | ![Detalle](C:\Users\lucca\AndroidStudioProjects\Desarrollo-de-Aplicaciones-1\nielsen-evidence\nielsen-evidence1-3.jpg) |



---

## 2. Correspondencia entre el Sistema y el Mundo Real ✅

**Principio**: El sistema debe hablar el lenguaje del usuario, con palabras, frases y conceptos familiares, en lugar de términos orientados al sistema.

**Se cumple en**:
- Iconografía familiar: corazón (❤️) para favoritos, estrella (⭐) para calificaciones, carrito para ingredientes, lápiz (✏️) para editar
- Organización lógica: secciones de receta (Ingredientes y Pasos) que reflejan cómo se cocina en la vida real
- Navegación intuitiva por categorías (Explorar, Mis Recetas, Favoritos)

**Evidencia**:

|                                                   Nombre de items en la barra de navegación                                                   |                                                     Barra Lateral                                                     |                                                      Crear Receta                                                      |
|:-------------------------------------------------------------------------------------------------------------------------------:|:---------------------------------------------------------------------------------------------------------------------:|:----------------------------------------------------------------------------------------------------------------------:|
| ![Pantalla principal](C:\Users\lucca\AndroidStudioProjects\Desarrollo-de-Aplicaciones-1\nielsen-evidence\nielsen-evidence2.jpg) | ![asfssf](C:\Users\lucca\AndroidStudioProjects\Desarrollo-de-Aplicaciones-1\nielsen-evidence\nielsen-evidence2-1.jpg) | ![Detalle](C:\Users\lucca\AndroidStudioProjects\Desarrollo-de-Aplicaciones-1\nielsen-evidence\nielsen-evidence2.3.jpg) |



---

## 3. Control y Libertad del Usuario ✅

**Principio**: Los usuarios a menudo eligen funciones del sistema por error y necesitan una "salida de emergencia" claramente marcada para dejar el estado no deseado.

**Se cumple en**:
- Diálogos de confirmación para acciones destructivas (eliminar receta, cerrar sesión)
- Botones de cancelar en todos los diálogos de confirmación
- Posibilidad de volver sin guardar al crear o editar una receta

**Evidencia**:

|                                                   Confirmación para borrar receta                                              |                                                     Confirmación para desloguarse                                                    
|:-------------------------------------------------------------------------------------------------------------------------------:|:---------------------------------------------------------------------------------------------------------------------:|:-
| ![Pantalla principal](C:\Users\lucca\AndroidStudioProjects\Desarrollo-de-Aplicaciones-1\nielsen-evidence\nielsen-evidence3.jpg) | ![asfssf](C:\Users\lucca\AndroidStudioProjects\Desarrollo-de-Aplicaciones-1\nielsen-evidence\nielsen-evidence3-2.jpg)


---

## 4. Consistencia y Estándares ✅

**Principio**: Los usuarios no deben tener que preguntarse si palabras, situaciones o acciones diferentes significan lo mismo.

**Se cumple en**:
- Uso consistente de Material Design en toda la aplicación
- Patrones de navegación estándar de Android (Bottom Navigation, Drawer Navigation)
- Formato consistente de mensajes de error en toda la app
- Iconografía consistente: mismos iconos para las mismas acciones en toda la aplicación

---

## 5. Prevención de Errores ✅

**Principio**: Mejor que un buen mensaje de error es un diseño cuidadoso que previene que ocurra el problema.

**Se cumple en**:
- Validación en tiempo real de campos mientras el usuario escribe
- Validación de formularios antes de enviar (campos requeridos, formato de email, etc.)
- Botones deshabilitados hasta que los campos sean válidos (login, registro)
- Confirmaciones para acciones destructivas (eliminar, logout)

---

## 6. Reconocimiento antes que Recuerdo ✅

**Principio**: Hacer visibles objetos, acciones y opciones. El usuario no debe tener que recordar información de una parte del diálogo a otra.

**Se cumple en**:
- Botones con iconos reconocibles (FAB + para crear, corazón para favoritos, estrella para calificar)
- Estado visual de favoritos (corazón lleno/vacío según el estado)
- Calificación actual visible si el usuario ya calificó
- Títulos claros en cada pantalla
- Navegación inferior que muestra claramente dónde está el usuario
- Placeholders y labels descriptivos en todos los campos de entrada

---

## 7. Flexibilidad y Eficiencia de Uso ✅

**Principio**: Los aceleradores - invisibles para el usuario novato - pueden acelerar la interacción para el usuario experto.

**Se cumple en**:
- Búsqueda en tiempo real con debounce (búsqueda automática mientras se escribe)
- Scroll automático al tope y recarga al tocar la pestaña activa
- deslizar para eliminar items del carrito

---

## 8. Diseño Estético y Minimalista ✅

**Principio**: Los diálogos no deben contener información que sea irrelevante o raramente necesaria.

**Se cumple en**:
- Diseño minimalista siguiendo Material Design
- Espaciado adecuado y consistente en toda la aplicación
- Tipografía clara y legible
- Cada pantalla muestra solo la información esencial
- Presentación clara y organizada con cards para recetas
- Jerarquía visual que destaca la información importante
- Navegación simplificada (tres pestañas principales, opciones secundarias en drawer)
- Feedback no intrusivo (toasts breves, loading overlays discretos)

---

## 9. Ayuda a los Usuarios a Reconocer, Diagnosticar y Recuperarse de Errores ✅

**Principio**: Los mensajes de error deben expresarse en lenguaje claro, indicar el problema y sugerir una solución constructiva.

**Se cumple en**:
- Mensajes de error específicos y descriptivos ("Credenciales inválidas" en lugar de "Error 401")
- Mensajes que sugieren acciones ("Error de red. Verifique su conexión")
- Indicadores visuales de error (campos marcados en rojo cuando son inválidos)
- Diferentes mensajes según el tipo de error (red, servidor, sin conexión)
- Fallback automático a caché cuando falla la conexión
- Posibilidad de reintentar acciones después de un error
- Diálogos de error informativos con opción de volver

---

## 10. Ayuda y Documentación ✅

**Principio**: Aunque es mejor si el sistema se puede usar sin documentación, puede ser necesario proporcionar ayuda y documentación.

**Se cumple en**:
- Se encuentra pendiente la realización de un tutorial al momento de abrir por primera vez la app

---

## Resumen

| Heurística | Estado |
|------------|--------|
| 1. Visibilidad del Estado del Sistema | ✅ Implementada |
| 2. Correspondencia Sistema-Mundo Real | ✅ Implementada |
| 3. Control y Libertad del Usuario | ✅ Implementada |
| 4. Consistencia y Estándares | ✅ Implementada |
| 5. Prevención de Errores | ✅ Implementada |
| 6. Reconocimiento antes que Recuerdo | ✅ Implementada |
| 7. Flexibilidad y Eficiencia de Uso | ✅ Implementada |
| 8. Diseño Estético y Minimalista | ✅ Implementada |
| 9. Recuperación de Errores | ✅ Implementada |
| 10. Ayuda y Documentación | ❌ Penidente |


