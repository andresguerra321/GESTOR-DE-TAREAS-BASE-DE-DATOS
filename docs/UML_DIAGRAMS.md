# Diagramas UML del Sistema TaskFlow

## 1. Diagrama de Clases

Este diagrama representa la estructura de clases del sistema, modelando las entidades de dominio y el desacoplamiento mediante interfaces DAO.

```mermaid
classDiagram
    class IGenericDAO~T, ID~ {
        <<interface>>
        +getAll() List~T~
        +getById(id: ID) T
        +insert(entity: T) boolean
        +update(entity: T) boolean
        +delete(id: ID) boolean
    }

    class ITaskDAO {
        <<interface>>
        +updateTaskStatus(taskId: String, newStatus: TaskStatus) boolean
    }

    class IUserDAO {
        <<interface>>
    }

    class TaskDAOImpl {
        +getAll() List~Task~
        +getById(id: String) Task
        +insert(task: Task) boolean
        +update(task: Task) boolean
        +delete(id: String) boolean
        +updateTaskStatus(taskId: String, newStatus: TaskStatus) boolean
    }

    class UserDAOImpl {
        +getAll() List~User~
        +getById(id: String) User
        +insert(user: User) boolean
        +update(user: User) boolean
        +delete(id: String) boolean
    }

    class TaskManager {
        -taskDAO: ITaskDAO
        -userDAO: IUserDAO
        +TaskManager(taskDAO: ITaskDAO, userDAO: IUserDAO)
        +obtenerTareas() List~Task~
        +crearTarea(titulo, desc, prioridad, usuarioId) Task
        +cambiarEstadoTarea(tareaId, nuevoEstado)
        +eliminarTarea(tareaId)
        +obtenerUsuarios() List~User~
        +crearUsuario(nombre) User
    }

    class Task {
        -String id
        -String title
        -String description
        -Priority priority
        -TaskStatus status
        -String assignedUserId
    }

    class User {
        -String id
        -String name
    }

    class Priority {
        <<enumeration>>
        LOW
        MEDIUM
        HIGH
    }

    class TaskStatus {
        <<enumeration>>
        TODO
        IN_PROGRESS
        DONE
    }

    IGenericDAO <|-- ITaskDAO
    IGenericDAO <|-- IUserDAO
    ITaskDAO <|.. TaskDAOImpl
    IUserDAO <|.. UserDAOImpl
    TaskManager --> ITaskDAO
    TaskManager --> IUserDAO
    Task --> Priority
    Task --> TaskStatus
    Task --> User
```
