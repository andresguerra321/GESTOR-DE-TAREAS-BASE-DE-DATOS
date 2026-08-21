package com.taskflow;

import com.taskflow.dao.ITaskDAO;
import com.taskflow.dao.IUserDAO;
import com.taskflow.model.Priority;
import com.taskflow.model.Task;
import com.taskflow.model.TaskStatus;
import com.taskflow.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestor único de tareas y usuarios.
 * Ahora utiliza el patrón DAO para conectarse a MySQL en lugar de JSON.
 */
public class TaskManager {
    private final ITaskDAO taskDAO;
    private final IUserDAO userDAO;

    // Inyección de dependencias
    public TaskManager(ITaskDAO taskDAO, IUserDAO userDAO) {
        this.taskDAO = taskDAO;
        this.userDAO = userDAO;

        // Si el sistema no tiene usuarios, crear los usuarios por defecto Diego y Guerra
        if (obtenerUsuarios().isEmpty()) {
            crearDatosDemo();
        }
    }

    /**
     * Crea los datos iniciales predeterminados para la demo con Diego y Guerra.
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

    // ==================== OPERACIONES DE USUARIOS ====================

    public List<User> obtenerUsuarios() {
        return userDAO.getAll();
    }

    public User crearUsuario(String nombre) {
        User usuario = new User(nombre);
        if (userDAO.insert(usuario)) {
            return usuario;
        }
        return null;
    }

    public User obtenerUsuarioPorId(String id) {
        if (id == null) return null;
        return userDAO.getById(id);
    }

    // ==================== OPERACIONES DE TAREAS ====================

    public List<Task> obtenerTareas() {
        return taskDAO.getAll();
    }

    public Task crearTarea(String titulo, String descripcion, Priority prioridad, String usuarioId) {
        Task tarea = new Task(titulo, descripcion, prioridad);
        tarea.setAssignedUserId(usuarioId);
        if (taskDAO.insert(tarea)) {
            return tarea;
        }
        return null;
    }

    public void avanzarEstadoTarea(String tareaId) {
        Task tarea = taskDAO.getById(tareaId);
        if (tarea != null) {
            TaskStatus nextStatus = tarea.getStatus().next();
            taskDAO.updateTaskStatus(tareaId, nextStatus);
        }
    }

    public void cambiarEstadoTarea(String tareaId, TaskStatus nuevoEstado) {
        if (nuevoEstado == null) return;
        taskDAO.updateTaskStatus(tareaId, nuevoEstado);
    }

    public void eliminarTarea(String tareaId) {
        taskDAO.delete(tareaId);
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
