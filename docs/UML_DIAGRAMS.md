# Diagramas UML del Sistema TaskFlow (Arquitectura MVC)

## 1. Diagrama de Clases (MVC + DAO Pattern)

Este diagrama representa la estructura de clases del sistema bajo la arquitectura **Modelo - Vista - Controlador (MVC)**, modelando las entidades de dominio, el controlador y el desacoplamiento mediante interfaces DAO.

```mermaid
classDiagram
    %% VISTA
    class MainFrame {
        -TaskController controller
        +actualizarTodo()
        +accionNuevaTarea()
        +accionNuevoUsuario()
    }

    %% CONTROLADOR
    class TaskController {
        -ITaskDAO taskDAO
        -IUserDAO userDAO
        +TaskController(taskDAO: ITaskDAO, userDAO: IUserDAO)
        +obtenerTareas() List~Task~
        +crearTarea(titulo, desc, prioridad, usuarioId) Task
        +cambiarEstadoTarea(tareaId, nuevoEstado)
        +eliminarTarea(tareaId)
        +obtenerUsuarios() List~User~
        +crearUsuario(nombre) User
    }

    %% MODELO - PERSISTENCIA (DAOs)
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

    %% MODELO - ENTIDADES
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

    %% RELACIONES MVC
    MainFrame --> TaskController : "View -> Controller"
    TaskController --> ITaskDAO : "Controller -> Model"
    TaskController --> IUserDAO : "Controller -> Model"

    IGenericDAO <|-- ITaskDAO
    IGenericDAO <|-- IUserDAO
    ITaskDAO <|.. TaskDAOImpl
    IUserDAO <|.. UserDAOImpl

    TaskDAOImpl --> Task
    UserDAOImpl --> User

    Task --> Priority
    Task --> TaskStatus
    Task --> User
```
