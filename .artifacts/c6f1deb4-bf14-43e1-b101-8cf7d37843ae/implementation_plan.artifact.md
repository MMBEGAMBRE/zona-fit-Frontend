# Plan de Optimización de Rendimiento (Alineado con GA1-GA4)

El usuario reporta lentitud en la aplicación. Este plan propone mejoras técnicas que respetan la estructura de las guías del profesor (GA1, GA2, GA3, GA4) pero optimizan su ejecución para una presentación fluida.

## Alineación con las Guías del Profesor

*   **GA1 (Networking):** Mantenemos el uso de **Retrofit** y **Gson**, pero optimizamos los interceptores para que no saturen el Logcat.
*   **GA2 (UI & UX):** Mejoramos la respuesta táctil de los componentes para que la navegación se sienta "premium".
*   **GA3 (Listas & Roles):** Optimizamos el filtrado de las listas de socios y membresías, evitando recálculos innecesarios en el hilo principal.
*   **GA4 (Práctica Final):** Implementamos un manejo de estado avanzado que demuestra un nivel superior de competencia en Android.

## Diagnóstico del Problema

1.  **Backend "Cold Start" (Inicio en Frío):** El servidor en Render tarda en responder tras inactividad.
2.  **Falta de Caché:** La app descarga todo en cada entrada a pantalla.
3.  **Procesamiento en UI:** El filtrado de listas se recalcula erróneamente en cada recomposición.
4.  **Logging Excesivo:** El interceptor `HttpLoggingInterceptor.Level.BODY` consume CPU innecesaria.
5.  **Corrutinas Redundantes:** Uso incorrecto de `scope.launch` dentro de `LaunchedEffect`.

## User Review Required

> [!IMPORTANT]
> **Servidor Render:** La causa principal de la lentitud extrema al abrir la app por primera vez es que el backend de Render se "duerme". Esto no se puede arreglar solo en el código del frontend, pero optimizaremos el manejo para que una vez despierto, todo sea más fluido.

## Proposed Changes

### 1. Optimización de Red (Retrofit)
Reduciremos el nivel de logs y ajustaremos los tiempos de espera para que la app no parezca "congelada" por tanto tiempo.

#### [MODIFY] [RetrofitClient.kt](file:///C:/Users/maile/OneDrive/Desktop/front-end/fronet/zona-fit-Frontend/app/src/main/java/com/example/proyecto_movil/data/RetrofitClient.kt)
- Cambiar `Level.BODY` a `Level.HEADERS` o `Level.BASIC` para evitar procesar JSONs gigantes en los logs.
- Ajustar los timeouts a valores más razonables (ej. 15-30s).

### 2. Optimización de Pantallas (UI & Compose)
Mejoraremos la eficiencia del filtrado y eliminaremos llamadas redundantes.

#### [MODIFY] [ClientsScreen.kt](file:///C:/Users/maile/OneDrive/Desktop/front-end/fronet/zona-fit-Frontend/app/src/main/java/com/example/proyecto_movil/ui/main/ClientsScreen.kt)
- Usar `remember(searchQuery, clients)` para que el filtrado solo ocurra cuando la lista o el texto cambian.
- Corregir el uso de `LaunchedEffect` eliminando el `scope.launch` interno redundante.

#### [MODIFY] [MembresiasScreen.kt](file:///C:/Users/maile/OneDrive/Desktop/front-end/fronet/zona-fit-Frontend/app/src/main/java/com/example/proyecto_movil/ui/main/MembresiasScreen.kt)
- Aplicar las mismas optimizaciones de `remember` y `LaunchedEffect`.

### 3. Caché en Memoria (Opcional pero Recomendado)
Implementar una persistencia básica en el objeto `Session` o un Repositorio simple para evitar recargas constantes al navegar atrás/adelante.

## Verification Plan

### Manual Verification
- Medir el tiempo de respuesta tras el primer "despertar" del servidor.
- Verificar que el filtrado de búsqueda sea instantáneo y no cause lag en el teclado.
- Observar los logs de Android Studio para confirmar que no se imprimen bloques masivos de texto.
