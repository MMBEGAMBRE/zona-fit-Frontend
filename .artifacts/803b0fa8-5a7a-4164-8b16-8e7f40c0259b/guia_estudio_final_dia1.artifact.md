# Guía de Estudio Completa: Zona Fit Evolution - Día 1

Este documento es tu guía definitiva de todo lo realizado hoy. Está estructurado para que entiendas cada decisión técnica, cada archivo y cómo se conecta todo, alineado 100% con las guías **GA1, GA2 y GA3 del SENA**.

---

## 1. El Backend (Python + Flask)
Antes de tocar Android, el servidor debe estar listo.
- **Puerto**: `5050` (Configurado en el archivo `.env` del backend).
- **Dirección Local**: `http://localhost:5050`.
- **IP de Red**: `192.168.0.102`. Esta es la que usa el celular para "ver" tu PC a través del Wi-Fi.
- **Prueba en Thunder Client**: Verificamos el endpoint `POST /api/auth/login` enviando `Email` y `PasswoRDkey`. Recibimos un **Token JWT**.

---

## 2. Configuración de Base en Android (GA1)

### [build.gradle.kts (Dependencias)](file:///C:/Users/maile/AndroidStudioProjects/proyectomovil/app/build.gradle.kts)
Hemos añadido las "herramientas" necesarias:
1. **Retrofit**: El encargado de hacer las llamadas al servidor.
2. **Gson**: El traductor que convierte el JSON del servidor en objetos de Kotlin.
3. **Navigation Compose**: El sistema para movernos entre pantallas (`login` -> `home`).
4. **Material Icons**: Para el icono de la pesa y el ojito de la contraseña.

### [AndroidManifest.xml (Permisos)](file:///C:/Users/maile/AndroidStudioProjects/proyectomovil/app/src/main/AndroidManifest.xml)
- **`INTERNET`**: Permiso obligatorio para que la app salga a la red.
- **`usesCleartextTraffic="true"`**: Permite usar `http` en lugar de `https` (necesario para servidores locales).
- **`networkSecurityConfig`**: Apunta a un archivo que le dice a Android que confíe en la IP de tu PC.

---

## 3. La Capa de Datos (El Cerebro)

### [Models.kt](file:///C:/Users/maile/AndroidStudioProjects/proyectomovil/app/src/main/java/com/example/proyecto_movil/data/Models.kt)
Aquí definimos el "molde" de los datos:
- **`LoginRequest`**: Tiene `Email` y `PasswoRDkey`. Coincide exacto con Flask.
- **`LoginResponse`**: Recibe el `message`, el `token` y el objeto `user`.
- **`ClienteResponse`**: Estructura para cuando pidamos la lista de socios.

### [ApiService.kt](file:///C:/Users/maile/AndroidStudioProjects/proyectomovil/app/src/main/java/com/example/proyecto_movil/data/ApiService.kt)
Es una **Interfaz**. Solo declara qué podemos pedir:
- `@POST("api/auth/login")`: Envía datos para entrar.
- `@GET("api/clientes/")`: Pide la lista de clientes (usa `@Header` para enviar el Token).

### [RetrofitClient.kt](file:///C:/Users/maile/AndroidStudioProjects/proyectomovil/app/src/main/java/com/example/proyecto_movil/data/RetrofitClient.kt)
Es un **Singleton** (solo hay uno).
- Configura la `BASE_URL` con tu IP.
- Añade el `HttpLoggingInterceptor`: permite que veas en el **Logcat** todo lo que pasa entre la app y el servidor.

---

## 4. La Interfaz de Usuario (Jetpack Compose)

### [LoginScreen.kt](file:///C:/Users/maile/AndroidStudioProjects/proyectomovil/app/src/main/java/com/example/proyecto_movil/ui/login/LoginScreen.kt)
Es la pantalla que ves en tu celular.
- **Estado (`remember`)**: Usamos variables como `email` y `password` que se actualizan mientras escribes.
- **Lógica del Botón**:
    1. Valida que los campos no estén vacíos.
    2. Usa `scope.launch` (Corrutina) para llamar al servidor sin congelar la app.
    3. Si el servidor dice "OK", guarda el Token en `Session.kt` y llama a `onLoginSuccess()`.
    4. Si falla, muestra el error en letras rojas debajo del botón.

### [Session.kt](file:///C:/Users/maile/AndroidStudioProjects/proyectomovil/app/src/main/java/com/example/proyecto_movil/data/Session.kt)
Un lugar seguro en memoria donde guardamos:
- El **Token**.
- El **Nombre** del usuario.
- Si es **Admin** (para la GA3).

---

## 5. Navegación y Estilo

### [MainActivity.kt](file:///C:/Users/maile/AndroidStudioProjects/proyectomovil/app/src/main/java/com/example/proyecto_movil/MainActivity.kt)
- Contiene el **`NavHost`**.
- Es el "mapa" que dice: "Si el login es exitoso, navega a la ruta `home`".

### [Theme.kt](file:///C:/Users/maile/AndroidStudioProjects/proyectomovil/app/src/main/java/com/example/proyecto_movil/ui/theme/Theme.kt)
- Configuramos los colores **Amarillo Zona Fit** y **Negro**.
- Ahora todos los botones y textos heredan este estilo automáticamente.

---

## Tips para Mañana (Sustentación/Estudio)
1. **¿Qué es una Corrutina?**: Es el `scope.launch`. Sirve para que la app no se bloquee mientras espera que el servidor responda.
2. **¿Por qué usamos 192.168.0.102?**: Porque el celular físico y tu PC están en la misma red Wi-Fi y esa es la "dirección de tu casa" en internet local.
3. **¿Qué hace Gson?**: Toma el texto JSON que manda Python y lo convierte en un objeto de Kotlin que podemos usar fácilmente.

> [!TIP]
> Si la app no abre, revisa siempre el **Logcat** en Android Studio. Es el "diario" de la app donde dice exactamente por qué ocurrió un error.

**¡Todo listo para descansar! Mañana el proyecto está en un estado perfecto para continuar con la lista de clientes.**
