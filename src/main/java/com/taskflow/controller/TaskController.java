package com.taskflow.controller;

import com.taskflow.dao.ITaskDAO;
import com.taskflow.dao.IUserDAO;
import com.taskflow.model.Priority;
import com.taskflow.model.Task;
import com.taskflow.model.TaskStatus;
import com.taskflow.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador principal de la arquitectura MVC (Model-View-Controller).
 * Se encarga de procesar las acciones de la Vista (UI), aplicar las reglas de negocio
 * y comunicarse con la capa del Modelo (DAOs y Entidades) mediante JDBC.
 */
public class TaskController {
    private final ITaskDAO taskDAO;
    private final IUserDAO userDAO;

    public TaskController(ITaskDAO taskDAO, IUserDAO userDAO) {
        this.taskDAO = taskDAO;
        this.userDAO = userDAO;

        // Cargar datos por defecto si la base de datos está vacía
        if (obtenerUsuarios().isEmpty()) {
            crearDatosDemo();
        }
    }

    /**
     * Inicializa datos predeterminados en la base de datos para la demostración.
     */
    public void crearDatosDemo() {
        User diego = crearUsuario("Diego");
        User guerra = crearUsuario("Guerra");

        if (diego != null && guerra != null) {
            Task t1 = crearTarea("Diseñar Interfaz Swing", "Crear componentes de interfaz clara y legible en Java.", Priority.HIGH, diego.getId());
            if (t1 != null) {
                cambiarEstadoTarea(t1.getId(), TaskStatus.IN_PROGRESS);
            }

            Task t2 = crearTarea("Crear Estructura JSON a MySQL", "Definir formato de guardado de tareas en Base de Datos.", Priority.MEDIUM, guerra.getId());
            if (t2 != null) {
                cambiarEstadoTarea(t2.getId(), TaskStatus.DONE);
            }

            crearTarea("Preparar Presentación", "Revisar el código y flujo para la exposición del lunes.", Priority.HIGH, diego.getId());
        }
    }

    // ==================== OPERACIONES DEL MODELO USUARIO ====================

    public List<User> obtenerUsuarios() {
        return userDAO.getAll();
    }

    public User crearUsuario(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) return null;
        User usuario = new User(nombre.trim());
        if (userDAO.insert(usuario)) {
            return usuario;
        }
        return null;
    }

    public User obtenerUsuarioPorId(String id) {
        if (id == null) return null;
        return userDAO.getById(id);
    }

    // ==================== OPERACIONES DEL MODELO TAREA ====================

    public List<Task> obtenerTareas() {
        return taskDAO.getAll();
    }

    public Task crearTarea(String titulo, String descripcion, Priority prioridad, String usuarioId) {
        if (titulo == null || titulo.trim().isEmpty()) return null;
        Task tarea = new Task(titulo.trim(), descripcion != null ? descripcion.trim() : "", prioridad);
        tarea.setAssignedUserId(usuarioId);
        if (taskDAO.insert(tarea)) {
            return tarea;
        }
        return null;
    }

    public void avanzarEstadoTarea(String tareaId) {
        Task tarea = taskDAO.getById(tareaId);
        if (tarea != null && tarea.getStatus() != null) {
            TaskStatus nextStatus = tarea.getStatus().next();
            taskDAO.updateTaskStatus(tareaId, nextStatus);
        }
    }

    public void cambiarEstadoTarea(String tareaId, TaskStatus nuevoEstado) {
        if (nuevoEstado == null || tareaId == null) return;
        taskDAO.updateTaskStatus(tareaId, nuevoEstado);
    }

    public void eliminarTarea(String tareaId) {
        if (tareaId != null) {
            taskDAO.delete(tareaId);
        }
    }

    public List<Task> obtenerTareasPorEstado(TaskStatus estado) {
        List<Task> resultado = new ArrayList<>();
        for (Task t : obtenerTareas()) {
            if (t.getStatus() == estado) {
                resultado.add(t);
            }
        }
        return resultado;
    }

    public List<Task> obtenerTareasPorUsuarioYPrioridad(String usuarioId, Priority prioridad) {
        List<Task> resultado = new ArrayList<>();
        if (usuarioId == null) return resultado;
        for (Task t : obtenerTareas()) {
            if (usuarioId.equals(t.getAssignedUserId()) && t.getPriority() == prioridad) {
                resultado.add(t);
            }
        }
        return resultado;
    }
}
