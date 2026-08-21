# Modelo Relacional y Notación DBML

## 1. Esquema Relacional Formal

* **USERS** (<u>id</u>, name)
* **TASK_STATUSES** (<u>id</u>, label, color_hex)
* **PRIORITIES** (<u>id</u>, label, level)
* **TASKS** (<u>id</u>, title, description, *priority_id*, *status_id*, *assigned_user_id*)
  * *FK `priority_id`* $\rightarrow$ **PRIORITIES**(`id`)
  * *FK `status_id`* $\rightarrow$ **TASK_STATUSES**(`id`)
  * *FK `assigned_user_id`* $\rightarrow$ **USERS**(`id`)

---

## 2. Esquema en DBML (dbdiagram.io)

```dbml
Table users {
  id varchar(36) [pk, not null]
  name varchar(100) [not null]
}

Table task_statuses {
  id varchar(50) [pk, not null]
  label varchar(50) [not null]
  color_hex varchar(10) [not null]
}

Table priorities {
  id varchar(50) [pk, not null]
  label varchar(50) [not null]
  level int [not null]
}

Table tasks {
  id varchar(36) [pk, not null]
  title varchar(255) [not null]
  description text
  priority_id varchar(50) [ref: > priorities.id]
  status_id varchar(50) [ref: > task_statuses.id]
  assigned_user_id varchar(36) [ref: > users.id]
}
```
