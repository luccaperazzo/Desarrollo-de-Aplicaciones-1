# Heurísticas de Nielsen Implementadas en Ccookly

Este documento lista las heurísticas de usabilidad de Jakob Nielsen que están implementadas en la aplicación Cookly

---

## 1. Visibilidad del Estado del Sistema

**Principio**: El sistema debe mantener informado al usuario sobre lo que está ocurriendo, mediante retroalimentación apropiada y en un tiempo razonable.

**Se cumple en**:
- Indicadores de carga (ProgressBar) durante login, registro y operaciones asíncronas
- Loading overlay en pantallas de creación y detalle de recetas
- Mensajes Toast para confirmar acciones exitosas ("Logueado!", "Receta creada exitosamente")
- Mensajes de error claros y específicos según el tipo de problema
- Indicación cuando se muestran datos desde caché (modo offline)


|                                                   Indicador de Carga                                                    |                                                           Mensaje Toast                                                            |                                                      Offline                                                      |
|:-------------------------------------------------------------------------------------------------------------------------------:|:---------------------------------------------------------------------------------------------------------------------:|:----------------------------------------------------------------------------------------------------------------------:|
| ![Pantalla principal](C:\Users\lucca\AndroidStudioProjects\Desarrollo-de-Aplicaciones-1\nielsen-evidence\nielsen-evidence1.jpg) | ![asfssf](C:\Users\lucca\AndroidStudioProjects\Desarrollo-de-Aplicaciones-1\nielsen-evidence\nielsen-evidence1-2.jpg) | ![Detalle](C:\Users\lucca\AndroidStudioProjects\Desarrollo-de-Aplicaciones-1\nielsen-evidence\nielsen-evidence1-3.jpg) |

(En las capturas se observa cómo la aplicación informa constantemente al usuario sobre su estado: un **indicador de carga** durante el login, un **mensaje de confirmación (Toast)** tras una acción exitosa, y un **aviso claro** cuando la app opera en modo offline, comunicando el problema y la acción que se está tomando.)


---

## 2. Correspondencia entre el Sistema y el Mundo Real

**Principio**: El sistema debe hablar el lenguaje del usuario, con palabras, frases y conceptos familiares, en lugar de términos orientados al sistema.

**Se cumple en**:
- Iconografía familiar: corazón (❤️) para favoritos, estrella (⭐) para calificaciones, carrito para ingredientes, lápiz (✏️) para editar
- Organización lógica: secciones de receta (Ingredientes y Pasos) que reflejan cómo se cocina en la vida real
- Navegación intuitiva por categorías (Explorar, Mis Recetas, Favoritos)

**Evidencia**:

|                                                   Nombre de items en la barra de navegación                                                   |                                                     Barra Lateral                                                     |                                                      Crear Receta                                                      |
|:-------------------------------------------------------------------------------------------------------------------------------:|:---------------------------------------------------------------------------------------------------------------------:|:----------------------------------------------------------------------------------------------------------------------:|
| ![Pantalla principal](C:\Users\lucca\AndroidStudioProjects\Desarrollo-de-Aplicaciones-1\nielsen-evidence\nielsen-evidence2.jpg) | ![asfssf](C:\Users\lucca\AndroidStudioProjects\Desarrollo-de-Aplicaciones-1\nielsen-evidence\nielsen-evidence2-1.jpg) | ![Detalle](C:\Users\lucca\AndroidStudioProjects\Desarrollo-de-Aplicaciones-1\nielsen-evidence\nielsen-evidence2.3.jpg) |

(Podemos ver como la interfaz habla el idioma del usuario y sigue una lógica que coincide con su experiencia real, evitando tecnicismos o expresiones confusas.)

---

## 3. Control y Libertad del Usuario

**Principio**: Los usuarios a menudo eligen funciones del sistema por error y necesitan una "salida de emergencia" claramente marcada para dejar el estado no deseado.

**Se cumple en**:
- Diálogos de confirmación para acciones destructivas (eliminar receta, cerrar sesión)
- Botones de cancelar en todos los diálogos de confirmación
- Posibilidad de volver sin guardar al crear o editar una receta

**Evidencia**:

|                                                   Confirmación para borrar receta                                              |                                                     Confirmación para desloguarse                                                    
|:-------------------------------------------------------------------------------------------------------------------------------:|:---------------------------------------------------------------------------------------------------------------------:|:- 
| ![Pantalla principal](C:\Users\lucca\AndroidStudioProjects\Desarrollo-de-Aplicaciones-1\nielsen-evidence\nielsen-evidence3.jpg) | ![asfssf](C:\Users\lucca\AndroidStudioProjects\Desarrollo-de-Aplicaciones-1\nielsen-evidence\nielsen-evidence3-2.jpg)

(Como se ve en la evidencia, la aplicación presenta diálogos de confirmación antes de ejecutar acciones destructivas como eliminar una receta o cerrar sesión. Esto le da al usuario el **control** total y la **libertad** de arrepentirse y cancelar la operación, evitando errores irreversibles.)

---

## 4. Consistencia y Estándares

**Principio**: Los usuarios no deben tener que preguntarse si palabras, situaciones o acciones diferentes significan lo mismo.

**Se cumple en**:
- Uso consistente de Material Design en toda la aplicación
- Patrones de navegación estándar de Android (Bottom Navigation, Drawer Navigation)
- Formato consistente de mensajes de error en toda la app
- Iconografía consistente: mismos iconos para las mismas acciones en toda la aplicación

**Evidencia**:

|                                                            HomePage                                                             |                                                     Barra Lateral                                                     |                                                      Crear Receta                                                      |
|:-------------------------------------------------------------------------------------------------------------------------------:|:---------------------------------------------------------------------------------------------------------------------:|:----------------------------------------------------------------------------------------------------------------------:|
| ![Pantalla principal](C:\Users\lucca\AndroidStudioProjects\Desarrollo-de-Aplicaciones-1\nielsen-evidence\nielsen-evidence2.jpg) | ![asfssf](C:\Users\lucca\AndroidStudioProjects\Desarrollo-de-Aplicaciones-1\nielsen-evidence\nielsen-evidence2-1.jpg) | ![Detalle](C:\Users\lucca\AndroidStudioProjects\Desarrollo-de-Aplicaciones-1\nielsen-evidence\nielsen-evidence2.3.jpg) |

(Vemos como la app mantiene un diseño uniforme, familiar y predecible. Esto reduce la carga cognitiva del usuario, mejora la fluidez de navegación y disminuye los errores de uso.)



---

## 5. Prevención de Errores

**Principio**: Mejor que un buen mensaje de error es un diseño cuidadoso que previene que ocurra el problema.

**Se cumple en**:
- Validación en tiempo real de campos mientras el usuario escribe
- Validación de formularios antes de enviar (campos requeridos, formato de email, etc.)
- Botones deshabilitados hasta que los campos sean válidos (login, registro)
- Confirmaciones para acciones destructivas (eliminar, logout)

**Evidencia**:

|                                                   Validación de formularios antes de enviar                                                  |                                                     Botones deshabilitados hasta que los campos sean válidos                                                   |
|:-------------------------------------------------------------------------------------------------------------------------------:|:---------------------------------------------------------------------------------------------------------------------:|:---------------------------------------------------------------------------------------------------------------------:|
| ![Pantalla principal](C:\Users\lucca\AndroidStudioProjects\Desarrollo-de-Aplicaciones-1\nielsen-evidence\nielsen-evidence4.jpg) | ![asfssf](C:\Users\lucca\AndroidStudioProjects\Desarrollo-de-Aplicaciones-1\nielsen-evidence\nielsen-evidence5.jpg) |

(Se evidencia la prevención de errores mediante la validación en tiempo real de los formularios y deshabilitando el botón de acción hasta que el usuario corrija la entrada, evitando así envíos de datos incorrectos desde el origen.)


---

## 6. Reconocimiento antes que Recuerdo

**Principio**: Hacer visibles objetos, acciones y opciones. El usuario no debe tener que recordar información de una parte del diálogo a otra.

**Se cumple en**:
- Botones con iconos reconocibles (FAB + para crear, corazón para favoritos, estrella para calificar)
- Estado visual de favoritos (corazón lleno/vacío según el estado)
- Calificación actual visible si el usuario ya calificó
- Títulos claros en cada pantalla
- Navegación inferior que muestra claramente dónde está el usuario
- Placeholders y labels descriptivos en todos los campos de entrada


**Evidencia**:

|                                               Estado de Favoritos y Calificación                                               |
|:------------------------------------------------------------------------------------------------------------------------------:|:------------------------------------------------------------------------------------------------------------------------:|
| ![Detalle de Receta](C:\Users\lucca\AndroidStudioProjects\Desarrollo-de-Aplicaciones-1\nielsen-evidence\nielsen-evidence6.jpg) 

(Se puede observar cómo el estado de una acción, como "favorito" o la calificación, se muestra visualmente para que el usuario no tenga que recordarlo. Asimismo, las opciones de navegación y los placeholders en los campos de texto están siempre visibles.)


---

## 7. Flexibilidad y Eficiencia de Uso

**Principio**: Los aceleradores - invisibles para el usuario novato - pueden acelerar la interacción para el usuario experto.

**Se cumple en**:
- Búsqueda en tiempo real con debounce (búsqueda automática mientras se escribe)
- Scroll automático al tope y recarga al tocar la pestaña activa
- deslizar para eliminar items del carrito

**Evidencia**:

|                                                    Búsqueda en Tiempo Real                                                    |                                                          Boton "Saltear"                                                           |
|:-----------------------------------------------------------------------------------------------------------------------------:|:--------------------------------------------------------------------------------------------------------------------------------:|
| ![Búsqueda en Home](C:\Users\lucca\AndroidStudioProjects\Desarrollo-de-Aplicaciones-1\nielsen-evidence\nielsen-evidence8.jpg) | ![Deslizar en Carrito](C:\Users\lucca\AndroidStudioProjects\Desarrollo-de-Aplicaciones-1\nielsen-evidence\nielsen-evidence7.jpg) |

(La búsqueda en tiempo real ofrece resultados inmediatos, actuando como un acelerador para encontrar recetas rápidamente. Los gestos, como deslizar para eliminar, o los botones como "Saltear" son atajos eficientes para usuarios que ya conocen la funcionalidad.)

---

## 8. Diseño Estético y Minimalista

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


**Evidencia**:

|                                                       Detalle de Receta                                                       |                                                               Home                                                               |
|:-----------------------------------------------------------------------------------------------------------------------------:|:--------------------------------------------------------------------------------------------------------------------------------:|
| ![Búsqueda en Home](C:\Users\lucca\AndroidStudioProjects\Desarrollo-de-Aplicaciones-1\nielsen-evidence\nielsen-evidence6.jpg) | ![Deslizar en Carrito](C:\Users\lucca\AndroidStudioProjects\Desarrollo-de-Aplicaciones-1\nielsen-evidence\nielsen-evidence2.jpg) |

(La pantalla de Detalle de Receta organiza la información en secciones claras y con una fuerte jerarquía visual, evitando cualquier elemento que no sea esencial para comprender la receta. La pantalla principal (Home) utiliza el patrón de `Cards` para presentar cada receta de forma limpia y minimalista, mostrando solo la información necesaria para que el usuario la reconozca y decida si quiere ver más.)

---

---

## 9. Ayuda a los Usuarios a Reconocer, Diagnosticar y Recuperarse de Errores

**Principio**: Los mensajes de error deben expresarse en lenguaje claro, indicar el problema y sugerir una solución constructiva.

**Se cumple en**:
- Mensajes de error específicos y descriptivos ("Credenciales inválidas" en lugar de "Error 401")
- Mensajes que sugieren acciones ("Error de red. Verifique su conexión")
- Indicadores visuales de error (campos marcados en rojo cuando son inválidos)
- Diferentes mensajes según el tipo de error (red, servidor, sin conexión)
- Fallback automático a caché cuando falla la conexión
- Posibilidad de reintentar acciones después de un error
- Diálogos de error informativos con opción de volver


**Evidencia**:

|                                                       Error de Red con Opción de Recuperación                                                      |                                                                 Error de Formulario en Contexto                                                                 |
|:-----------------------------------------------------------------------------------------------------------------------------:|:------------------------------------------------------------------------------------------------------------------------------------:|
| ![Búsqueda en Home](C:\Users\lucca\AndroidStudioProjects\Desarrollo-de-Aplicaciones-1\nielsen-evidence\nielsen-evidence9.jpg) | ![Deslizar en Carrito](C:\Users\lucca\AndroidStudioProjects\Desarrollo-de-Aplicaciones-1\nielsen-evidence\nielsen-evidence10jpg.jpg) |

(La aplicación muestra errores de validación directamente en el campo correspondiente, ayudando al usuario a **diagnosticar** el problema rápidamente. Ante un error de sistema como la pérdida de conexión, no solo se **reconoce** el problema, sino que se ofrece una vía de **recuperación** clara con un botón de "Reintentar".)

---

## 10. Ayuda y Documentación

**Principio**: Aunque es mejor si el sistema se puede usar sin documentación, puede ser necesario proporcionar ayuda y documentación.

**Se cumple en**:
- Se encuentra pendiente la realización de un tutorial al momento de abrir por primera vez la app


|                                                           Onboarding 1                                                           |                                                      Onboarding 2                                                       |                                                       Onboarding 3                                                       |
|:--------------------------------------------------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------------------------------------:|:------------------------------------------------------------------------------------------------------------------------:|
| ![Pantalla principal](C:\Users\lucca\AndroidStudioProjects\Desarrollo-de-Aplicaciones-1\nielsen-evidence\nielsen-evidence13.jpg) | ![asfssf](C:\Users\lucca\AndroidStudioProjects\Desarrollo-de-Aplicaciones-1\nielsen-evidence\nielsen-evidence12jpg.jpg) | ![Detalle](C:\Users\lucca\AndroidStudioProjects\Desarrollo-de-Aplicaciones-1\nielsen-evidence\nielsen-evidence11jpg.jpg) |


(El onboarding funciona como la principal herramienta de **ayuda y documentación**, presentando las características clave de la aplicación de una manera fácil de digerir para un nuevo usuario, sin que este tenga que buscar activamente un menú de ayuda.)


---

## Resumen

| Heurística | Estado |
|---|---|
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


---

# Anexo: Investigación de Usuario (UI/UX)

Esta sección documenta el proceso de investigación y diseño centrado en el usuario, cumpliendo con el requisito #4 de UI/UX y CX.

## 1. Personas (Arquetipos de Usuario)

Se definieron dos arquetipos de usuario para representar a los segmentos clave de nuestra audiencia.

### Persona 1: Martín, el Estudiante Ocupado

- **Bio**: Tiene 21 años y estudia arquitectura. Vive solo y tiene un presupuesto ajustado. No tiene mucho tiempo para cocinar pero está cansado de la comida rápida.
- **Metas**:
    - Encontrar recetas rápidas, fáciles y económicas.
    - Aprender a cocinar platos básicos pero sabrosos.
    - Organizar su lista de compras semanal para no gastar de más.
- **Frustraciones**:
    - Las recetas online a menudo usan ingredientes caros o difíciles de encontrar.
    - Se siente abrumado por los videos largos de cocina.
    - A menudo olvida ingredientes clave cuando va al supermercado.

### Persona 2: Sofía, la Chef Creativa

- **Bio**: Tiene 28 años y es diseñadora gráfica. Cocinar es su principal hobby y una forma de relajarse. Le encanta experimentar y crear sus propias recetas.
- **Metas**:
    - Descubrir recetas nuevas e inspiradoras.
    - Guardar y organizar sus propias creaciones y variaciones.
    - Compartir sus recetas con una comunidad y recibir feedback.
    - Ser reconocida por sus habilidades culinarias.
- **Frustraciones**:
    - Pierde sus notas y recetas guardadas en un caos de bookmarks y capturas de pantalla.
    - Las apps de recetas existentes no le permiten modificar o "remixar" recetas fácilmente.
    - Le gustaría tener un solo lugar para centralizar todo su "mundo culinario".

---

## 2. Guion de Entrevista / Encuesta

Se diseñó el siguiente guion para validar las hipótesis sobre las necesidades de los usuarios.

**Objetivo**: Entender los hábitos de cocina, el uso de tecnología para recetas y los principales puntos de dolor.

1.  **Demografía**: ¿Qué edad tienes y a qué te dedicas?
2.  **Hábitos**: ¿Con qué frecuencia cocinas? ¿Qué tipo de platos sueles preparar?
3.  **Búsqueda**: Cuando buscas una receta, ¿dónde sueles buscar primero? (Google, Instagram, YouTube, apps, libros, etc.)
4.  **Uso de Tecnología**: ¿Usas tu teléfono o tablet mientras cocinas? ¿Para qué?
5.  **Problemas**: ¿Cuál es tu mayor frustración al buscar o seguir una receta online?
6.  **Organización**: ¿Cómo guardas las recetas que te gustan? ¿Y cómo organizas tu lista de compras?
7.  **Creación**: ¿Alguna vez creas tus propias recetas o modificas las existentes? Si es así, ¿cómo guardas esos cambios?
8.  **Social**: ¿Te interesaría compartir tus propias recetas con otros o ver las creaciones de tus amigos?

---

## 3. User Journey Map: Sofía publica una nueva receta

- **Objetivo del Usuario**: Guardar y compartir una nueva creación culinaria para que otros la vean y califiquen.

| Fase | Acciones del Usuario | Pensamientos y Sentimientos                                                                         | Oportunidades para Cookly |
| :--- | :--- |:----------------------------------------------------------------------------------------------------| :--- |
| **Inspiración** | Acaba de crear un postre increíble. Le saca una buena foto. | "¡Esto me quedó genial! Tengo que guardarlo en algún sitio para no olvidarme. Debería compartirlo." | Ser la app de referencia para guardar creaciones personales. |
| **Creación** | Abre Cookly, va a "Mis Recetas" y toca el botón "+". | "Ok, vamos a ver si es fácil."                                                                      | Un botón de "Crear" (FAB) prominente y de fácil acceso. |
| **Completar Formulario** | 1. Sube la foto. <br> 2. Escribe el nombre. <br> 3. Escribe la descripción. <br> 4. Añade los ingredientes uno por uno. <br> 5. Añade los pasos. | "Uf, son muchos ingredientes. Escribir todo esto es un poco lento."                               | **Implementar la entrada de ingredientes por voz (IA)**. Ofrecer autocompletado para unidades e ingredientes comunes. |
| **Publicación** | Revisa que todo esté bien y presiona "Guardar y Publicar". | "Espero que se vea tan bien como en la foto."                                                       | Mostrar un preview de cómo se verá la receta. Dar feedback instantáneo de que se guardó correctamente. |
| **Validación Social** | Entra a su perfil para ver su nueva receta. Comparte el enlace. Vuelve más tarde para ver comentarios. | "¡Me encanta cómo se ve! A ver si a alguien le gusta. ¡Ya tengo un like!"                           | Permitir compartir fácilmente en redes sociales. Implementar un sistema de notificaciones para likes y comentarios. |


---

## 4. Análisis y Conclusiones

La investigación de usuarios validó varias de nuestras hipótesis iniciales y generó insights que impactaron directamente en el diseño y las funcionalidades de **Cookly**:

1.  **Necesidad de Organización**: Tanto Martín como Sofía tienen problemas para organizar sus recetas y listas de compras. Esto validó la importancia de funcionalidades clave como **Favoritos**, **Mis Recetas** y el **Carrito de Compras**.

2.  **Dos Perfiles, Dos Necesidades**: La app debía servir tanto para el consumo pasivo de recetas (Martín) como para la creación activa (Sofía). Esto justificó la separación entre las pestañas **Explorar** y **Mis Recetas**, y la importancia del flujo de **Crear Receta**.

3.  **Dolor en la Entrada de Datos**: El User Journey de Sofía reveló que la entrada manual de muchos ingredientes es un punto de fricción. Esto generó y priorizó la idea de implementar **una funcionalidad de transcripción de ingredientes por voz (IA)** como un factor diferencial y un acelerador de uso (Heurística #7).

4.  **Componente Social**: El deseo de Sofía de compartir y recibir feedback validó la necesidad de un **sistema de perfiles públicos, calificaciones (estrellas) y comentarios**, convirtiendo a Cookly en más que un simple recetario.

5.  **Acceso Offline**: La frustración de perder una receta por mala conexión (un problema común en la cocina) confirmó que una **funcionalidad offline robusta con caché de datos** era un requisito no funcional crítico para una buena experiencia de usuario.
