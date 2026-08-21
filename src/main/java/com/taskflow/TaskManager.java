package com.taskflow;

import com.taskflow.dao.TaskDAO;
import com.taskflow.dao.UserDAO;
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
    private final TaskDAO taskDAO;
    private final UserDAO userDAO;

    public TaskManager() {
        this.taskDAO = new TaskDAO();
        this.userDAO = new UserDAO();

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
        return userDAO.getAllUsers();
    }

    public User crearUsuario(String nombre) {
        User usuario = new User(nombre);
        if (userDAO.insertUser(usuario)) {
            return usuario;
        }
        return null;
    }

    public User obtenerUsuarioPorId(String id) {
        if (id == null) return null;
        return userDAO.getUserById(id);
    }

    // ==================== OPERACIONES DE TAREAS ====================

    public List<Task> obtenerTareas() {
        return taskDAO.getAllTasks();
    }

    public Task crearTarea(String titulo, String descripcion, Priority prioridad, String usuarioId) {
        Task tarea = new Task(titulo, descripcion, prioridad);
        tarea.setAssignedUserId(usuarioId);
        if (taskDAO.insertTask(tarea)) {
            return tarea;
        }
        return null;
    }

    public void avanzarEstadoTarea(String tareaId) {
        Task tarea = taskDAO.getTaskById(tareaId);
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
        taskDAO.deleteTask(tareaId);
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
