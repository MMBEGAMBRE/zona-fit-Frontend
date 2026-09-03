# Documentación Detallada del Proyecto: Zona Fit Evolution (Fase 1)

Este documento explica todos los cambios realizados en el proyecto Android para conectar con el backend de Flask, siguiendo las guías **GA1 y GA2** del SENA.

## 1. Configuración de Base y Seguridad (GA1)

### [AndroidManifest.xml](file:///C:/Users/maile/AndroidStudioProjects/proyectomovil/app/src/main/AndroidManifest.xml)
- **Cambio**: Se agregó `<uses-permission android:name="android.permission.INTERNET" />`.
- **Explicación**: Sin este permiso, el sistema operativo Android bloquea cualquier intento de la app de salir a la red.
- **Cambio**: Se agregó `android:networkSecurityConfig="@xml/network_security_config"`.
- **Explicación**: Desde Android 9, se prohíbe el tráfico HTTP (sin S). Como el servidor local usa HTTP, esta configuración le dice a Android que confíe específicamente en tu PC.

### [network_security_config.xml](file:///C:/Users/maile/AndroidStudioProjects/proyectomovil/app/src/main/res/xml/network_security_config.xml)
- **Contenido**: Lista de dominios permitidos para tráfico "cleartext" (HTTP).
- **Dirección 192.168.0.102**: Es la IP de tu PC. Es necesaria para que tu celular físico pueda "ver" el servidor que corre en tu computadora.

---

## 2. Capa de Datos y Red (El "Traductor")

### [LoginRequest.kt](file:///C:/Users/maile/AndroidStudioProjects/proyectomovil/app/src/main/java/com/example/proyecto_movil/data/model/LoginRequest.kt)
- **Código**: `data class LoginRequest(val Email: String, val PasswoRDkey: String)`
- **Explicación**: Es el "molde" de lo que enviamos. Los nombres coinciden exactamente con lo que espera tu Flask (`Email` y `PasswoRDkey`). La anotación `@Serializable` permite convertir este objeto a un texto JSON automáticamente.

### [ApiService.kt](file:///C:/Users/maile/AndroidStudioProjects/proyectomovil/app/src/main/java/com/example/proyecto_movil/data/network/ApiService.kt)
- **@POST("api/auth/login")**: Define que usaremos el método POST para enviar datos de login.
- **suspend fun**: Palabra clave de Kotlin. Significa que la función puede pausarse mientras espera la respuesta del servidor sin que se trabe la pantalla del celular.

### [NetworkModule.kt](file:///C:/Users/maile/AndroidStudioProjects/proyectomovil/app/src/main/java/com/example/proyecto_movil/data/network/NetworkModule.kt)
- **Retrofit**: Es la librería encargada de hacer las llamadas. Aquí se configura la `BASE_URL` (la dirección de tu PC).
- **OkHttpClient**: Se configuró un "Logging Interceptor", que es como un espía que imprime en la consola de Android Studio todo lo que la app envía y recibe.

### [Session.kt](file:///C:/Users/maile/AndroidStudioProjects/proyectomovil/app/src/main/java/com/example/proyecto_movil/data/session/Session.kt)
- **Objeto Singleton**: Solo existe una copia en toda la app. Guarda el **Token JWT**. El token es como una "llave" que el servidor nos da para que no tengamos que mandar la contraseña en cada pantalla.

---

## 3. Interfaz de Usuario (Jetpack Compose)

### [Theme.kt](file:///C:/Users/maile/AndroidStudioProjects/proyectomovil/app/src/main/java/com/example/proyecto_movil/ui/theme/Theme.kt) y [Color.kt](file:///C:/Users/maile/AndroidStudioProjects/proyectomovil/app/src/main/java/com/example/proyecto_movil/ui/theme/Color.kt)
- Se definieron los colores **ZonaFitYellow** (Amarillo institucional) y **ZonaFitDark** (Negro de fondo).
- Esto hace que todos los botones y textos se vean con la identidad de tu marca automáticamente.

### [LoginScreen.kt](file:///C:/Users/maile/AndroidStudioProjects/proyectomovil/app/src/main/java/com/example/proyecto_movil/ui/login/LoginScreen.kt)
- **@Composable**: Marca una función que dibuja algo en pantalla.
- **remember { mutableStateOf(...) }**: Es la memoria de la pantalla. Si el usuario escribe una letra, esta variable cambia y la pantalla se redibuja sola para mostrarla.
- **IconButton con Visibilidad**: Implementé el ojito para mostrar/ocultar la contraseña.

### [LoginViewModel.kt](file:///C:/Users/maile/AndroidStudioProjects/proyectomovil/app/src/main/java/com/example/proyecto_movil/ui/login/LoginViewModel.kt)
- Es el "cerebro" de la pantalla de login. Aquí vive la lógica de qué pasa cuando haces clic en el botón.
- Separa la parte visual de la parte lógica (Arquitectura MVVM).

---

## 4. Navegación (El Mapa de la App)

### [MainActivity.kt](file:///C:/Users/maile/AndroidStudioProjects/proyectomovil/app/src/main/java/com/example/proyecto_movil/MainActivity.kt)
- **NavHost**: Es el contenedor principal. Define que la app tiene dos rutas: `"login"` y `"home"`.
- **startDestination = "login"**: Indica que siempre que abras la app, empiece por el login.
- **popUpTo("login") { inclusive = true }**: Código vital que hace que, al entrar al menú, el usuario no pueda darle "atrás" para volver al login (sería un fallo de seguridad).

---

## Conceptos Clave para Estudiar:
1. **JSON**: Formato de texto para hablar con el servidor.
2. **Retrofit**: Librería de red.
3. **Jetpack Compose**: Forma moderna de hacer diseños (sin XML).
4. **JWT (Token)**: La llave de seguridad que guarda el objeto `Session`.
