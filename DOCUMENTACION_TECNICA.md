# Documentación Técnica - UADE Recipes App

## Índice

1. [Introducción](#introducción)
2. [Arquitectura](#arquitectura)
3. [Estructura del Proyecto](#estructura-del-proyecto)
4. [Tecnologías y Dependencias](#tecnologías-y-dependencias)
5. [Configuración del Proyecto](#configuración-del-proyecto)
6. [Componentes Principales](#componentes-principales)
7. [Patrones de Diseño](#patrones-de-diseño)
8. [Base de Datos](#base-de-datos)
9. [API y Servicios](#api-y-servicios)
10. [Seguridad](#seguridad)
11. [Firebase Analytics](#firebase-analytics)
12. [Build y Deployment](#build-y-deployment)

---

## Introducción

**UADE Recipes App** es una aplicación móvil Android desarrollada en Java que permite a los usuarios crear, explorar, compartir y gestionar recetas culinarias. La aplicación sigue los principios de arquitectura MVVM y Repository Pattern para mantener el código organizado y mantenible.

### Características Principales

- Autenticación de usuarios (login/registro)
- Exploración de recetas públicas
- Creación y edición de recetas propias
- Sistema de favoritos
- Carrito de compras con ingredientes
- Sistema de calificaciones (ratings)
- Búsqueda de recetas
- Soporte para modo claro/oscuro
- Internacionalización (Español/Inglés)
- Accesibilidad (contentDescription)
- Fuentes escalables

---

## Arquitectura

La aplicación sigue el patrón **MVVM (Model-View-ViewModel)** junto con el **Repository Pattern**:

```
┌─────────────┐
│   Activity   │  ← View (UI)
│  / Fragment  │
└──────┬──────┘
       │ observa
       ▼
┌─────────────┐
│  ViewModel   │  ← Lógica de negocio
└──────┬──────┘
       │ usa
       ▼
┌─────────────┐
│  Repository  │  ← Abstracción de datos
└──────┬──────┘
       │
       ├─► Retrofit (API)
       └─► Room (Base de datos local)
```

### Flujo de Datos

1. **View (Activity/Fragment)** → Observa `LiveData` del ViewModel
2. **ViewModel** → Expone `LiveData` y maneja lógica de negocio
3. **Repository** → Abstrae fuentes de datos (API y base de datos local)
4. **Service** → Retrofit para llamadas HTTP
5. **Database** → Room para persistencia local

---

## Estructura del Proyecto

```
app/src/main/java/ar/edu/uade/recipes/
├── adapter/                    # Adapters para RecyclerViews
│   ├── CartAdapter.java
│   └── RecipesAdapter.java
├── database/                  # Room Database
│   ├── AppDatabase.java
│   ├── CartDao.java
│   └── RecipeDao.java
├── fragment/                  # Fragments
│   ├── BaseRecipeListFragment.java
│   ├── ExploreFragment.java
│   ├── FavoritesFragment.java
│   ├── MyRecipesFragment.java
│   └── RecipeListFragment.java
├── model/                     # Modelos de datos
│   ├── CartItem.java
│   ├── CreateRecipeRequest.java
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   ├── Recipe.java
│   ├── RecipeDetail.java
│   ├── RecipeIngredient.java
│   ├── RecipeStep.java
│   ├── RegisterRequest.java
│   ├── RegisterResponse.java
│   ├── UpdateUserRequest.java
│   └── User.java
├── repository/                # Repository Pattern
│   └── RecipeRepository.java
├── service/                   # Servicios de red
│   ├── AuthService.java
│   ├── RecipeService.java
│   └── RetrofitClient.java
├── util/                      # Utilidades
│   ├── AnalyticsHelper.java
│   ├── ImageHelper.java
│   └── UserManager.java
├── viewmodel/                 # ViewModels
│   ├── HomeViewModel.java
│   └── RecipeDetailViewModel.java
└── [Activities]               # Activities principales
    ├── SplashActivity.java
    ├── LoginActivity.java
    ├── RegisterActivity.java
    ├── HomeActivity.java
    ├── RecipeDetailActivity.java
    ├── CreateRecipeActivity.java
    ├── ProfileActivity.java
    └── CartActivity.java
```

---

## Tecnologías y Dependencias

### Core Android
- **Android SDK**: Compile SDK 36, Min SDK 24, Target SDK 36
- **Java**: 11
- **Gradle**: 8.12.3

### UI/UX
- **Material Design 3**: Componentes de Material Design
- **Glide**: Carga y caché de imágenes
- **EdgeToEdge**: Soporte para edge-to-edge display

### Arquitectura
- **Lifecycle Components**: ViewModel y LiveData
- **MVVM Pattern**: Separación de lógica y UI

### Networking
- **Retrofit 2.9.0**: Cliente HTTP type-safe
- **OkHttp 4.12.0**: Cliente HTTP
- **Gson 2.10.1**: Serialización JSON

### Persistencia
- **Room 2.6.1**: Base de datos local SQLite

### Analytics
- **Firebase Analytics**: Tracking de eventos de usuario

### Utilidades
- **ExifInterface**: Corrección de orientación de imágenes

---

## Configuración del Proyecto

### Requisitos Previos

- Android Studio Hedgehog o superior
- JDK 11 o superior
- Android SDK 36
- Gradle 8.12.3

### Configuración Inicial

1. **Clonar el repositorio**
   ```bash
   git clone <repository-url>
   cd tp
   ```

2. **Agregar google-services.json**
   - Obtener el archivo desde Firebase Console
   - Colocarlo en `app/google-services.json`

3. **Configurar variables de entorno** (si es necesario)
   - Editar `local.properties` si hay configuraciones específicas

4. **Sincronizar proyecto**
   ```bash
   ./gradlew build
   ```

### Estructura de Build

**Build Types:**
- **Debug**: Modo desarrollo con logging habilitado
- **Release**: ProGuard habilitado, código ofuscado, sin logs

**Gradle Files:**
- `build.gradle.kts` (root): Configuración del proyecto
- `app/build.gradle.kts`: Configuración del módulo app
- `gradle/libs.versions.toml`: Gestión de versiones de dependencias

---

## Componentes Principales

### Activities

#### SplashActivity
- **Propósito**: Pantalla inicial que redirige según estado de autenticación
- **Flujo**: Verifica si hay usuario logueado → HomeActivity o LoginActivity

#### LoginActivity
- **Propósito**: Autenticación de usuarios existentes
- **Funcionalidad**:
  - Validación de credenciales
  - Guardado de token y datos de usuario
  - Redirección a HomeActivity

#### RegisterActivity
- **Propósito**: Registro de nuevos usuarios
- **Funcionalidad**:
  - Validación de formulario
  - Carga de imagen de perfil (cámara/galería)
  - Validación de contraseña (8+ caracteres, mayúscula, minúscula, número)

#### HomeActivity
- **Propósito**: Pantalla principal con navegación
- **Componentes**:
  - Bottom Navigation (Explore, Mis Recetas, Favoritos)
  - Drawer Navigation (Perfil, Carrito, Logout, Tema)
  - Barra de búsqueda con debounce (500ms)
  - FAB para crear nueva receta

#### RecipeDetailActivity
- **Propósito**: Visualización detallada de una receta
- **Funcionalidad**:
  - Ver detalles completos
  - Agregar/quitar de favoritos
  - Calificar receta (1-5 estrellas)
  - Agregar ingredientes al carrito
  - Editar/eliminar (solo si es owner)

#### CreateRecipeActivity
- **Propósito**: Crear o editar recetas
- **Funcionalidad**:
  - Campos: título, descripción, imagen
  - Ingredientes dinámicos (nombre, cantidad, unidad)
  - Pasos dinámicos
  - Validación de campos requeridos

#### ProfileActivity
- **Propósito**: Gestión de perfil de usuario
- **Funcionalidad**: Editar nombre completo, email, imagen de perfil

#### CartActivity
- **Propósito**: Ver y gestionar carrito de compras
- **Funcionalidad**: Lista de ingredientes agregados desde recetas

### ViewModels

#### HomeViewModel
- **Responsabilidad**: Gestionar listas de recetas (públicas, propias, favoritas)
- **LiveData**:
  - `publicRecipes`, `myRecipes`, `favoriteRecipes`
  - `isLoading`, `errorMessage`, `hasMoreData`
- **Métodos**:
  - `loadPublicRecipes()`, `loadMyRecipes()`, `loadFavoriteRecipes()`
  - Soporte para búsqueda y paginación

#### RecipeDetailViewModel
- **Responsabilidad**: Gestionar detalles de una receta
- **LiveData**:
  - `recipeDetail`, `isLoading`, `errorMessage`
  - `favoriteUpdated`, `favoriteMessage`
  - `ratingUpdated`, `ratingMessage`
- **Métodos**:
  - `loadRecipeDetail()`, `toggleFavorite()`, `updateRating()`, `deleteRecipe()`

### Fragments

#### BaseRecipeListFragment
- **Propósito**: Fragment base abstracto para listas de recetas
- **Funcionalidad**:
  - Configuración de RecyclerView
  - Scroll infinito (paginación)
  - Manejo de estados (loading, error, empty)
  - Búsqueda con debounce

#### ExploreFragment, MyRecipesFragment, FavoritesFragment
- **Propósito**: Implementaciones específicas que extienden `BaseRecipeListFragment`
- **Diferencia**: Cada uno usa un método diferente del `HomeViewModel`

### Repository

#### RecipeRepository
- **Propósito**: Abstraer acceso a datos
- **Funcionalidad**:
  - Caché local con Room
  - Sincronización con API
  - Manejo de conectividad de red
  - Fallback a caché cuando no hay internet

---

## Patrones de Diseño

### MVVM (Model-View-ViewModel)
- **View**: Activities y Fragments (observan LiveData)
- **ViewModel**: Lógica de negocio y estado
- **Model**: Entidades de datos

### Repository Pattern
- Abstracción sobre fuentes de datos múltiples
- API y base de datos local

### Observer Pattern
- LiveData para comunicación reactiva
- ViewModel → View mediante observadores

### Singleton Pattern
- `RetrofitClient`: Instancia única de Retrofit
- `AppDatabase`: Instancia única de base de datos
- `UserManager`: Gestión de sesión

---

## Base de Datos

### Room Database

**AppDatabase**
- Versión: 2
- Entidades: `Recipe`, `CartItem`

**RecipeDao**
- `insertRecipes()`: Insertar/actualizar recetas
- `getRecipesByType()`: Obtener por tipo (public, my_recipe, favorite)
- `searchRecipesByType()`: Búsqueda por tipo
- `deleteRecipesByType()`: Eliminar por tipo

**CartDao**
- `insert()`: Agregar ingrediente al carrito
- `getAll()`: Obtener todos los items
- `delete()`: Eliminar item
- `clear()`: Limpiar carrito

### Estrategia de Caché

1. **Primera carga**: Desde API → Guardar en Room
2. **Sin internet**: Cargar desde Room (marcado como `fromCache = true`)
3. **Refresco**: Actualizar desde API y sincronizar con Room
4. **Paginación**: Acumular resultados en Room

---

## API y Servicios

### Base URL
```
https://tpo-desappi.vercel.app/
```

### Servicios

#### AuthService
- `POST /api/auth/login` - Login
- `POST /api/auth/register` - Registro
- `PUT /api/auth/profile` - Actualizar perfil

#### RecipeService
- `GET /api/recipes/public` - Recetas públicas
- `GET /api/recipes/my-recipes` - Mis recetas
- `GET /api/recipes/favorites` - Favoritos
- `GET /api/recipes/{id}` - Detalle de receta
- `POST /api/recipes` - Crear receta
- `PUT /api/recipes/{id}` - Editar receta
- `DELETE /api/recipes/{id}` - Eliminar receta
- `POST /api/recipes/{id}/favorite` - Agregar favorito
- `DELETE /api/recipes/{id}/favorite` - Remover favorito
- `POST /api/recipes/{id}/rating` - Agregar rating
- `PUT /api/recipes/{id}/rating` - Actualizar rating

### Autenticación

- **Bearer Token**: Se envía en header `Authorization: Bearer <token>`
- **Interceptor**: Automático en `RetrofitClient`
- **Storage**: Token guardado en `UserManager`

### Manejo de Errores

- **Network Error**: Muestra mensaje de "Sin conexión"
- **Server Error (4xx/5xx)**: Muestra mensaje del servidor
- **Timeout**: Manejo de timeouts de OkHttp

---

## Seguridad

### Controles OWASP Mobile Top 10 Implementados

#### M1: Improper Platform Usage
- ✅ `allowBackup="false"` deshabilitado

#### M7: Client Code Quality
- ✅ ProGuard habilitado en release
- ✅ Código ofuscado
- ✅ Shrink resources

Ver documentación completa en `OWASP_SECURITY.md`

---

## Firebase Analytics

### Inicialización

Firebase se inicializa en `RecipesApplication.onCreate()`

### Eventos Trackeados

- `login` - Login exitoso
- `sign_up` - Registro exitoso
- `view_item` - Visualización de receta
- `add_to_favorites` / `remove_from_favorites` - Gestión de favoritos
- `rate_recipe` - Calificación de receta
- `create_recipe` / `edit_recipe` / `delete_recipe` - Gestión de recetas
- `search` - Búsqueda de recetas
- `navigate_tab` - Navegación entre tabs
- `add_to_cart` - Agregar ingredientes al carrito
- `theme_change` - Cambio de tema

### Helper Class

`AnalyticsHelper` centraliza el logging de eventos con métodos helper para cada tipo de evento.

---

## Build y Deployment

### Compilar APK de Debug
```bash
./gradlew assembleDebug
```

### Compilar APK de Release
```bash
./gradlew assembleRelease
```

### Generar Bundle para Play Store
```bash
./gradlew bundleRelease
```

### Configuración de ProGuard

- **Reglas**: `app/proguard-rules.pro`
- **Habilitado en**: Release builds
- **Ofuscación**: Activada
- **Shrink Resources**: Habilitado

---

## Internacionalización

### Idiomas Soportados

- **Español** (default): `values/strings.xml`
- **Inglés**: `values-en/strings.xml`

### Uso de String Resources

Todos los textos de la UI deben usar strings.xml:
```xml
<TextView android:text="@string/app_name" />
```

### Accesibilidad

- `contentDescription` para elementos de imagen
- `textAppearance` para fuentes escalables

---

## Referencias

- [Android Developer Documentation](https://developer.android.com/)
- [Material Design 3](https://m3.material.io/)
- [Retrofit Documentation](https://square.github.io/retrofit/)
- [Room Persistence Library](https://developer.android.com/training/data-storage/room)
- [MVVM Architecture](https://developer.android.com/topic/libraries/architecture/viewmodel)
- [OWASP Mobile Top 10](https://owasp.org/www-project-mobile-top-10/)

