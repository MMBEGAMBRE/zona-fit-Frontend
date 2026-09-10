# Resumen de Cambios: Solución de Conexión de Red

He aplicado los cambios necesarios para permitir que la aplicación se conecte correctamente al backend desde tu dispositivo físico.

## Cambios Realizados

### Configuración de Red

#### [network_security_config.xml](file:///C:/Users/panes/AndroidStudioProjects/zona-fit-Frontend/app/src/main/res/xml/network_security_config.xml)
- Se añadió la IP del servidor local `192.168.1.65` a la lista de dominios con permiso para tráfico `cleartext` (HTTP). Esto evita que Android bloquee la conexión por razones de seguridad.

### Cliente Retrofit

#### [RetrofitClient.kt](file:///C:/Users/panes/AndroidStudioProjects/zona-fit-Frontend/app/src/main/java/com/example/proyecto_movil/data/RetrofitClient.kt)
- Se actualizó la `BASE_URL` para usar el puerto `5050`, que es el puerto configurado en el servidor backend según la documentación del proyecto.

## Verificación Realizada
- Se ejecutó `gradle sync` y el proyecto se sincronizó correctamente.

> [!IMPORTANT]
> **Pasos Finales para el Usuario**:
> 1. Asegúrate de que tu servidor backend esté corriendo en la PC.
> 2. Asegúrate de que el celular y la PC estén conectados a la **misma red Wi-Fi**.
> 3. Ejecuta la aplicación de nuevo en tu teléfono.
