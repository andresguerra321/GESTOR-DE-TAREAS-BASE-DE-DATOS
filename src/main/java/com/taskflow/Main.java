package com.taskflow;

import com.taskflow.controller.TaskController;
import com.taskflow.dao.ITaskDAO;
import com.taskflow.dao.IUserDAO;
import com.taskflow.dao.TaskDAOImpl;
import com.taskflow.dao.UserDAOImpl;
import com.taskflow.ui.MainFrame;
import javax.swing.SwingUtilities;

/**
 * Clase principal y punto de entrada de la aplicación TaskFlow.
 * Inicializa los componentes de la arquitectura MVC e inyecta las dependencias.
 */
public class Main {
    public static void main(String[] args) {
        // 1. MODEL: Inicializar la capa de acceso a datos (DAOs con JDBC)
        ITaskDAO taskDAO = new TaskDAOImpl();
        IUserDAO userDAO = new UserDAOImpl();

        // 2. CONTROLLER: Inicializar el controlador inyectando las dependencias del Modelo
        TaskController controller = new TaskController(taskDAO, userDAO);

        // 3. VIEW: Lanzar la interfaz gráfica en el hilo de Swing pasando el Controlador
        SwingUtilities.invokeLater(() -> {
            MainFrame ventana = new MainFrame(controller);
            ventana.setVisible(true);
        });
    }
}
