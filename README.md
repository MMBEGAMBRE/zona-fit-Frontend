# Zona Fit Evolution — App Android

Aplicación móvil nativa para **Android** (Kotlin + Jetpack Compose) que administra un gimnasio: clientes, membresías, pagos y personal. Consume el backend REST [zona-fit-backend](../zona-fit-backend) (Flask + MySQL) construido en paralelo a este proyecto.

## El problema que resuelve

Un gimnasio pequeño hoy lleva el control de sus socios, membresías y pagos en cuadernos o en una hoja de Excel que solo tiene el dueño en su computador — nadie más puede consultarla ni actualizarla desde el mostrador. Zona Fit Evolution le permite al dueño y a su personal **consultar y gestionar clientes, membresías y pagos desde el celular**, en el momento en que ocurre cada inscripción o cobro, sin depender de una sola persona ni de un archivo local.

**Alcance:** *"Zona Fit Evolution permite al dueño y a su personal de un gimnasio consultar y gestionar clientes, membresías y pagos desde el celular, para que el registro de socios y cobros no dependa de un cuaderno o una hoja de cálculo aislada."*

## Roles y permisos

| Acción | Empleado | Administrador |
|---|:---:|:---:|
| Iniciar sesión | ✅ | ✅ |
| Editar mi perfil / cambiar mi contraseña | ✅ | ✅ |
| Ver y registrar Membresías | ✅ | ✅ |
| Ver y registrar Pagos | ✅ | ✅ |
| Gestionar Clientes (crear/editar/listar) | ❌ | ✅ |
| Crear cuentas de personal | ❌ | ✅ |
| Ver historial de auditoría (Control de Accesos) | ❌ | ✅ |

La app **oculta** con `if (Session.isAdmin)` las opciones que un rol no puede usar — pero eso es solo la primera capa, y no basta por sí sola: cualquiera con la URL del backend podría llamar a esos endpoints directamente sin pasar por la app. Por eso el **servidor vuelve a validar el rol en cada endpoint sensible** y responde:
- `401 Unauthorized` si no se envía un token válido
- `403 Forbidden` si el token es válido pero el rol no alcanza

Cada intento bloqueado queda además registrado en el historial de auditoría del backend.

## Usuarios de prueba

| Rol | Email | Contraseña |
|---|---|---|
| ADMINISTRADOR | `admin@zonafit.com` | `admin123` |
| EMPLEADO | `juan.empleado@zonafit.com` | `clave123` |

## Requisitos previos

- Android Studio (Ladybug o superior) con SDK de Android instalado
- Un dispositivo físico o emulador con **Android 7.0 (API 24)** o superior
- El backend [zona-fit-backend](../zona-fit-backend) corriendo (`python app.py`, ver su propio README) y una base MySQL con el esquema de `database/create-zonafit.sql`

## Configurar la URL base

El cliente Retrofit apunta al backend en [`data/RetrofitClient.kt`](app/src/main/java/com/example/proyecto_movil/data/RetrofitClient.kt):

```kotlin
// Opción A: emulador de Android Studio (IP especial que apunta a tu PC)
private const val BASE_URL = "http://10.0.2.2:5050/"

// Opción B: celular físico en la misma red Wi-Fi que tu PC
private const val BASE_URL = "http://192.168.1.65:5050/"
```

Descomenta la que corresponda a tu forma de probar y comenta la otra. Si usas un celular físico, reemplaza la IP por la de tu propio computador (`ipconfig` en Windows) — y agrégala también en [`res/xml/network_security_config.xml`](app/src/main/res/xml/network_security_config.xml), que es lo que le permite a la app hacer peticiones HTTP en texto claro (sin HTTPS) solo hacia esas IPs de desarrollo.

## Ejecutar el proyecto

1. Abre la carpeta en Android Studio y espera a que Gradle sincronice.
2. Confirma la `BASE_URL` (paso anterior) y que el backend esté corriendo y accesible desde el emulador/celular.
3. Ejecuta (▶) sobre un emulador o un dispositivo físico con depuración USB activada.
4. Inicia sesión con alguno de los usuarios de prueba de arriba.

## Dependencias principales

- **Jetpack Compose** + Material 3 — UI declarativa
- **Navigation Compose** — navegación entre pantallas
- **Retrofit** + **Gson** — cliente HTTP y parseo JSON
- **OkHttp** con `HttpLoggingInterceptor` — log de cada petición/respuesta en Logcat
- Corrutinas de Kotlin — llamadas de red asíncronas (`suspend fun`)

Ver [`app/build.gradle.kts`](app/build.gradle.kts) para las versiones exactas.

## Arquitectura del proyecto

```
app/src/main/java/com/example/proyecto_movil/
  data/
    Models.kt          # data class: requests y responses de la API
    ApiService.kt       # interfaz Retrofit (todas las llamadas HTTP)
    RetrofitClient.kt    # URL base + cliente OkHttp/Retrofit
    Session.kt           # token, nombre y rol del usuario, en memoria
    ErrorHandler.kt       # traduce códigos HTTP (401/403/404/409) a mensajes
    Validaciones.kt        # validadores de formularios (email, teléfono, fecha)
  ui/
    login/LoginScreen.kt
    main/                  # todas las pantallas post-login
  MainActivity.kt            # NavHost: registra cada pantalla como una ruta
```

**Cómo viaja un dato, de punta a punta:** una pantalla (ej. `ClientsScreen.kt`) llama a `RetrofitClient.api.getClientes(Session.bearer())` dentro de un `LaunchedEffect` → Retrofit arma la petición HTTP con el header `Authorization` → el backend responde JSON → Gson lo convierte al `data class` de `Models.kt` → el `Composable` recompone la pantalla con los datos nuevos.

## Mapa de navegación

`startDestination = "login"`. Todas las rutas están registradas en [`MainActivity.kt`](app/src/main/java/com/example/proyecto_movil/MainActivity.kt):

| Ruta | Pantalla | Acceso | Argumento |
|---|---|---|---|
| `login` | `LoginScreen` | Público | — |
| `home` | `HomeScreen` | Autenticado | — |
| `clientes` | `ClientsScreen` | Admin | — |
| `add_cliente` | `AddClientScreen` | Admin | — |
| `edit_cliente/{id}` | `EditClientScreen` | Admin | `id: Int` |
| `membresias` | `MembresiasScreen` | Autenticado | — |
| `add_membresia` | `AddMembresiaScreen` | Autenticado | — |
| `pagos` | `PagosScreen` | Autenticado | — |
| `add_pago` | `AddPagoScreen` | Autenticado | — |
| `editProfile` | `EditProfileScreen` | Autenticado | — |
| `changePassword` | `ChangePasswordScreen` | Autenticado | — |
| `add_empleado` | `AddEmployeeScreen` | Admin | — |
| `registros` | `RegistrosScreen` | Admin | — |

Al iniciar sesión con éxito se navega a `home` con `popUpTo("login") { inclusive = true }` (borra el login del historial, el botón Atrás no regresa a él). Al cerrar sesión (con confirmación por `AlertDialog`) se usa `popUpTo(0)` para limpiar todo el historial de navegación.

## Tabla de endpoints consumidos

Todas las rutas cuelgan de la `BASE_URL` configurada arriba. El token se envía siempre por el encabezado `Authorization: Bearer <token>`, nunca en la URL.

| Funcionalidad | Método y ruta | ¿Token? | ¿Rol admin? | Cuerpo enviado | Respuesta esperada | Código de error |
|---|---|---|---|---|---|---|
| Iniciar sesión | `POST /api/auth/login` | No | No | `{Email, PasswoRDkey}` | `200` `{token, user}` | `400` / `401` |
| Crear cuenta de personal | `POST /api/auth/register` | Sí | Sí | `{nombre, email, password, rol}` | `201` `{message, id}` | `400` / `401` / `403` / `409` |
| Ver mi perfil | `GET /api/cuentas/profile` | Sí | No | — | `200` `{user}` | `401` / `404` |
| Editar mi perfil | `PUT /api/cuentas/profile` | Sí | No | `{nombre, email}` | `200` `{user}` | `400` / `401` / `409` |
| Cambiar mi contraseña | `PUT /api/cuentas/change-password` | Sí | No | `{current_password, new_password}` | `200` `{message}` | `400` / `401` |
| Listar clientes | `GET /api/clientes/` | Sí | Sí | — | `200` `[cliente]` | `401` / `403` |
| Ver un cliente | `GET /api/clientes/{id}` | Sí | Sí | — | `200` `{cliente}` | `401` / `403` / `404` |
| Crear cliente | `POST /api/clientes/` | Sí | Sí | `{nombre, apellido, documento, email, telefono, fecha_nacimiento}` | `201` `{id}` | `400` / `401` / `403` |
| Crear cliente + membresía + pago (un solo paso) | `POST /api/clientes/con-membresia` | Sí | Sí | `{...cliente, tipo, fecha_inicio?, metodo_pago, monto}` | `201` `{cliente, membresia, pago}` | `400` / `401` / `403` |
| Editar cliente | `PUT /api/clientes/{id}` | Sí | Sí | `{...cliente, estado}` | `200` `{message}` | `400` / `401` / `403` |
| Listar membresías | `GET /api/membresias/` | Sí | No | — | `200` `[membresía]` | `401` |
| Ver una membresía | `GET /api/membresias/{id}` | Sí | No | — | `200` `{membresía}` | `401` / `404` |
| Crear membresía | `POST /api/membresias/` | Sí | No | `{cliente_id, tipo, fecha_inicio, fecha_vencimiento}` | `201` `{id}` | `401` |
| Cambiar estado de membresía | `PUT /api/membresias/{id}` | Sí | No | `{estado}` | `200` `{message}` | `401` |
| Listar pagos | `GET /api/pagos/` | Sí | No | — | `200` `[pago]` | `401` |
| Registrar pago | `POST /api/pagos/` | Sí | No | `{cliente_id, membresia_id, monto, metodo_pago}` | `201` `{id}` | `400` / `401` |
| Ver historial de auditoría | `GET /api/registros/` | Sí | Sí | — | `200` `[registro]` | `401` / `403` |
| Crear un registro manual | `POST /api/registros/` | Sí | No | `{usuario_id, accion, descripcion}` | `201` `{message}` | `401` |

Detalle completo de cada endpoint (validaciones exactas, formato de cada campo) en el [README del backend](../zona-fit-backend/README.MD).

## Manejo de errores

Cada pantalla que llama al backend sigue el mismo patrón: `try/catch` alrededor de la llamada, revisión de `isSuccessful`, y traducción del código HTTP a un mensaje en español (`ErrorHandler.kt::manejarError`):

| Escenario | Mensaje al usuario |
|---|---|
| Sin conexión / servidor caído | *"📡 Sin conexión al servidor"* |
| `401` — sesión vencida | *"⚠️ Tu sesión expiró, vuelve a iniciar sesión"* (limpia la sesión y regresa al login) |
| `403` — sin permisos | *"🚫 No tienes permisos para esta acción"* |
| `404` — no existe | *"❌ No se encontró el recurso solicitado"* |

Ninguna pantalla cierra la app ante un error de red o de servidor.

## Capturas de pantalla

*(pendiente — ejecuta la app con los usuarios de prueba y agrega aquí capturas de: login, menú como admin, menú como empleado, listado de clientes, "Nuevo Socio" con membresía y pago, listado de membresías, listado de pagos, y un caso de error manejado, por ejemplo un 403 al forzar una llamada sin permisos)*

## Generar el APK de depuración

```bash
./gradlew assembleDebug
```
El APK queda en `app/build/outputs/apk/debug/app-debug.apk` — no se sube al repositorio (excluido por `.gitignore`); publícalo como *release* de GitHub para la entrega.
