# Plan de Implementación: Módulo de Membresías (GA3 - Gestión de Listas)

Este plan detalla los pasos para implementar la gestión de membresías, permitiendo al personal del gimnasio ver quién tiene el plan activo y cuándo se vence, siguiendo el diseño institucional de **Zona Fit Evolution**.

## User Review Required

> [!IMPORTANT]
> **Formato de Fecha**: El backend espera fechas en formato `YYYY-MM-DD`.
> **Carga de Datos**: Al igual que en Clientes, usaremos el Token JWT para autorizar la petición.

## Proposed Changes

### 1. Capa de Datos (Data Layer)

#### [MODIFY] [Models.kt](file:///C:/Users/maile/AndroidStudioProjects/proyectomovil/app/src/main/java/com/example/proyecto_movil/data/Models.kt)
- Agregar `MembresiaResponse`: `id`, `cliente_id`, `tipo` (Mensual, Trimestral, Anual), `fecha_inicio`, `fecha_vencimiento`, `estado`, `cliente_nombre`, `cliente_apellido`.

#### [MODIFY] [ApiService.kt](file:///C:/Users/maile/AndroidStudioProjects/proyectomovil/app/src/main/java/com/example/proyecto_movil/data/ApiService.kt)
- Agregar `@GET("api/membresias/")` para obtener la lista de todas las membresías.

### 2. Interfaz de Usuario (UI)

#### [NEW] [MembresiasScreen.kt](file:///C:/Users/maile/AndroidStudioProjects/proyectomovil/app/src/main/java/com/example/proyecto_movil/ui/main/MembresiasScreen.kt)
- Pantalla que lista las membresías con el diseño de círculos e iniciales.
- Mostrará el rango de fechas (Inicio - Vencimiento) y el estado en colores (**ACTIVA**, **VENCIDA**, **CANCELADA**).
- Fondo oscuro y tarjetas estilo Premium.

#### [MODIFY] [HomeScreen.kt](file:///C:/Users/maile/AndroidStudioProjects/proyectomovil/app/src/main/java/com/example/proyecto_movil/ui/main/HomeScreen.kt)
- Habilitar la navegación en la tarjeta de **MEMBRESÍAS**.

#### [MODIFY] [MainActivity.kt](file:///C:/Users/maile/AndroidStudioProjects/proyectomovil/app/src/main/java/com/example/proyecto_movil/MainActivity.kt)
- Registrar la ruta `"membresias"`.

## Verification Plan

### Manual Verification
- Iniciar sesión y navegar a **Membresías**.
- Confirmar que se listan las membresías existentes en la base de datos MySQL.
- Verificar que el color de la etiqueta cambie según el estado (Verde para Activa, Rojo para Vencida).
