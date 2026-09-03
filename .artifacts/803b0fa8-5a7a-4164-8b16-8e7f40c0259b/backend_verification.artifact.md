# Guía de Verificación: Backend "Zona Fit Evolution" en Thunder Client

Antes de programar en Android, vamos a asegurarnos de que tu backend responda correctamente. Sigue estos pasos en **Thunder Client** dentro de VS Code.

## 1. Petición de Estado (Ping)
Esta prueba confirma que el servidor está escuchando en el puerto 5050.

- **Método**: `GET`
- **URL**: `http://localhost:5050/`
- **Resultado esperado**:
  ```json
  { "message": "Zona Fit Evolution API is running" }
  ```

---

## 2. Prueba de Login (Autenticación)
Tu backend usa JWT. Necesitamos este token para las demás peticiones.

- **Método**: `POST`
- **URL**: `http://localhost:5050/api/auth/login`
- **Pestaña "Body"**: Selecciona **JSON** y pega lo siguiente:
  ```json
  {
    "Email": "admin@zonafit.com",
    "PasswoRDkey": "admin123"
  }
  ```
- **Resultado esperado**: Deberías recibir un código `200 OK` y un JSON que contenga un campo `"token": "ey..."`.

> [!IMPORTANT]
> Copia el valor del **token** que recibas, lo necesitaremos para el siguiente paso.

---

## 3. Obtener Lista de Clientes
Esta prueba verifica la conexión con tu base de datos MySQL.

- **Método**: `GET`
- **URL**: `http://localhost:5050/api/clientes/`
- **Pestaña "Auth"**:
  - Si tienes implementada seguridad en esta ruta, deberás agregar el token en el Header:
  - **Header Name**: `Authorization` (o como lo espere tu middleware).
  - **Header Value**: `Bearer TU_TOKEN_AQUI`.
- **Resultado esperado**: Una lista con los clientes registrados en tu DB.

---

## Solución de Problemas Comunes

- **Error 401 (Unauthorized)**: Las credenciales son incorrectas o el token expiró.
- **Error 500 (Server Error)**: Revisa la terminal de VS Code; probablemente MySQL no esté encendido o el nombre de la base de datos en el archivo `.env` sea incorrecto.
- **Connection Refused**: El backend no está corriendo. Ejecuta `.\venv\Scripts\python.exe app.py`.
