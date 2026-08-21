# Guía de Conexión a la Base de Datos con DBeaver

Esta guía describe el procedimiento para conectar la base de datos MySQL local o en contenedor a **DBeaver Community**.

## 1. Parámetros de Conexión
* **Tipo de Conexión**: MySQL
* **Host**: `localhost` (o `127.0.0.1`)
* **Port**: `3306`
* **Database**: `taskflow_db`
* **Username**: `root`
* **Password**: *(vacío por defecto)*

## 2. Ajuste de Propiedades del Controlador (Driver Properties)
En entornos MySQL 8.0+:
1. En la ventana de configuración de la conexión, pestaña **Propiedades del controlador** (*Driver properties*).
2. Establecer `allowPublicKeyRetrieval` en `true`.
3. Establecer `useSSL` en `false`.

## 3. Generación de Diagrama ER en DBeaver
1. Desplegar la base de datos `taskflow_db`.
2. Hacer doble clic sobre la carpeta **Tablas**.
3. En la parte superior, hacer clic en la pestaña **Diagrama ER**.
4. Clic derecho $\rightarrow$ **Guardar diagrama como imagen**.
