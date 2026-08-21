# Justificación de Normalización hasta la Cuarta Forma Normal (4NF)

Este documento detalla el proceso riguroso de normalización aplicado a la base de datos `taskflow_db`.

## 1. Primera Forma Normal (1NF)
* **Requisito**: Todos los atributos deben contener valores atómicos e indivisibles, y cada tabla debe contar con una clave primaria única.
* **Cumplimiento**: 
  - En la tabla `tasks`, `title` y `description` almacenan valores atómicos simples.
  - No existen listas de cadenas separadas por comas ni grupos repetitivos.

## 2. Segunda Forma Normal (2NF)
* **Requisito**: Cumplir 1NF y asegurar que todos los atributos no clave dependan totalmente de la clave primaria (sin dependencias parciales).
* **Cumplimiento**:
  - Todas las tablas (`users`, `tasks`, `task_statuses`, `priorities`) utilizan claves primarias simples de una sola columna (`id`).
  - Al no existir claves compuestas, no hay posibilidad de dependencias funcionales parciales.

## 3. Tercera Forma Normal (3NF)
* **Requisito**: Cumplir 2NF y eliminar cualquier dependencia transitiva (ningún atributo no clave debe depender de otro atributo no clave).
* **Cumplimiento**:
  - La información de los estados (como `label` y `color_hex`) se extrajo a la tabla `task_statuses`.
  - La información de prioridades (como `level`) se extrajo a la tabla `priorities`.
  - `tasks` solo almacena las claves foráneas `status_id` y `priority_id`.

## 4. Cuarta Forma Normal (4NF)
* **Requisito**: Cumplir con la Forma Normal de Boyce-Codd (BCNF) y no contener dependencias multivaluadas independientes ($X \twoheadrightarrow Y$) dentro de una misma entidad.
* **Cumplimiento**:
  - En `tasks`, la asignación de usuario (`assigned_user_id`), el estado (`status_id`) y la prioridad (`priority_id`) son relaciones monovalentes (1 a 1 por tarea).
  - No existen tablas que mezclen relaciones multivaluadas no relacionadas (por ejemplo, múltiples etiquetas y múltiples usuarios asignados en la misma tabla).
