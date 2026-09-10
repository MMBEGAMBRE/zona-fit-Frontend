# Plan de Implementación - Expansión de Funcionalidades Frontend

Este plan detalla la implementación de nuevas pantallas (Perfil, Contraseña, Membresías, Pagos y Edición de Clientes), la integración de validaciones, manejo centralizado de errores y ajustes en la navegación.

## User Review Required

> [!IMPORTANT]
> **URL Base**: He notado que el plan sugiere cambiar la `BASE_URL` en `RetrofitClient.kt`. Actualmente está en `http://192.168.1.65:5050/`. Se propone cambiarla a `10.0.2.2` para emuladores. ¿Confirmas qué entorno prefieres mantener por defecto?

> [!NOTE]
> **Reutilización de Componentes**: `CustomOutlinedTextField` se encuentra definido en `AddClientScreen.kt`. Se mantendrá allí para ser reutilizado por las nuevas pantallas del paquete `ui.main`.

## Proposed Changes

### [Componente] Data & Models

#### [MODIFY] [Models.kt](file:///C:/Users/panes/AndroidStudioProjects/zona-fit-Frontend/app/src/main/java/com/example/proyecto_movil/data/Models.kt)
- Agregar `MembresiaResponse`, `PagoResponse` y `UpdateClientRequest`.
- Asegurar que `ProfileResponse`, `UpdateProfileRequest`, `ChangePasswordRequest` y `SimpleMessageResponse` coincidan con lo requerido.

#### [MODIFY] [ApiService.kt](file:///C:/Users/panes/AndroidStudioProjects/zona-fit-Frontend/app/src/main/java/com/example/proyecto_movil/data/ApiService.kt)
- Agregar endpoints para perfil, contraseña, membresías, pagos y actualización/obtención de cliente individual.
- Importar `PUT` y `Path`.

#### [NEW] [ErrorHandler.kt](file:///C:/Users/panes/AndroidStudioProjects/zona-fit-Frontend/app/src/main/java/com/example/proyecto_movil/data/ErrorHandler.kt)
- Implementar `manejarError` para centralizar la traducción de códigos HTTP y el cierre de sesión en caso de error 401.

#### [NEW] [Validaciones.kt](file:///C:/Users/panes/AndroidStudioProjects/zona-fit-Frontend/app/src/main/java/com/example/proyecto_movil/data/Validaciones.kt)
- Implementar funciones para validar email, teléfono y formato de fecha.

#### [MODIFY] [RetrofitClient.kt](file:///C:/Users/panes/AndroidStudioProjects/zona-fit-Frontend/app/src/main/java/com/example/proyecto_movil/data/RetrofitClient.kt)
- Ajustar la `BASE_URL` según la preferencia (emulador o físico).

---

### [Componente] UI & Navigation

#### [MODIFY] [MainActivity.kt](file:///C:/Users/panes/AndroidStudioProjects/zona-fit-Frontend/app/src/main/MainActivity.kt)
- Registrar las nuevas rutas: `editProfile`, `changePassword`, `membresias`, `pagos` y `edit_cliente/{id}`.

#### [MODIFY] [HomeScreen.kt](file:///C:/Users/panes/AndroidStudioProjects/zona-fit-Frontend/app/src/main/java/com/example/proyecto_movil/ui/main/HomeScreen.kt)
- Agregar tarjetas para "Editar mis datos" y "Cambiar contraseña".
- Conectar todas las tarjetas a sus respectivas rutas de navegación.

#### [MODIFY] [ClientsScreen.kt](file:///C:/Users/panes/AndroidStudioProjects/zona-fit-Frontend/app/src/main/java/com/example/proyecto_movil/ui/main/ClientsScreen.kt)
- Hacer que los elementos de la lista sean clicables para navegar a la edición del cliente.
- Usar `manejarError` para los mensajes de error.

#### [MODIFY] [AddClientScreen.kt](file:///C:/Users/panes/AndroidStudioProjects/zona-fit-Frontend/app/src/main/java/com/example/proyecto_movil/ui/main/AddClientScreen.kt)
- Integrar las funciones de `Validaciones.kt`.

#### [MODIFY] [LoginScreen.kt](file:///C:/Users/panes/AndroidStudioProjects/zona-fit-Frontend/app/src/main/java/com/example/proyecto_movil/ui/login/LoginScreen.kt)
- Quitar las credenciales precargadas.

#### [NEW] [EditProfileScreen.kt](file:///C:/Users/panes/AndroidStudioProjects/zona-fit-Frontend/app/src/main/java/com/example/proyecto_movil/ui/main/EditProfileScreen.kt)
- Implementar pantalla de edición de perfil.

#### [NEW] [ChangePasswordScreen.kt](file:///C:/Users/panes/AndroidStudioProjects/zona-fit-Frontend/app/src/main/java/com/example/proyecto_movil/ui/main/ChangePasswordScreen.kt)
- Implementar pantalla de cambio de contraseña.

#### [NEW] [MembresiasScreen.kt](file:///C:/Users/panes/AndroidStudioProjects/zona-fit-Frontend/app/src/main/java/com/example/proyecto_movil/ui/main/MembresiasScreen.kt)
- Implementar lista de membresías.

#### [NEW] [PagosScreen.kt](file:///C:/Users/panes/AndroidStudioProjects/zona-fit-Frontend/app/src/main/java/com/example/proyecto_movil/ui/main/PagosScreen.kt)
- Implementar lista de pagos.

#### [NEW] [EditClientScreen.kt](file:///C:/Users/panes/AndroidStudioProjects/zona-fit-Frontend/app/src/main/java/com/example/proyecto_movil/ui/main/EditClientScreen.kt)
- Implementar pantalla de edición de socios existentes.

## Verification Plan

### Automated Tests
- Ejecutar `gradlew assembleDebug` para verificar que no hay errores de compilación.

### Manual Verification
1. Iniciar sesión con credenciales válidas.
2. Navegar a cada una de las nuevas secciones desde el Home.
3. Probar la validación de formularios en la creación y edición de socios.
4. Verificar que al editar el perfil o contraseña los cambios se reflejen (o den el error esperado).
5. Comprobar que la navegación hacia atrás (`popBackStack`) funcione en todas las pantallas nuevas.
