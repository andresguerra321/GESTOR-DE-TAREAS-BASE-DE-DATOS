package com.taskflow;

import com.taskflow.controller.TaskController;
import com.taskflow.dao.ITaskDAO;
import com.taskflow.dao.IUserDAO;

/**
 * Gestor de tareas que hereda de TaskController para mantener compatibilidad
 * con la arquitectura MVC del proyecto.
 */
public class TaskManager extends TaskController {

    public TaskManager(ITaskDAO taskDAO, IUserDAO userDAO) {
        super(taskDAO, userDAO);
    }
}
