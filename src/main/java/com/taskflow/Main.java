package com.taskflow;

import com.taskflow.dao.ITaskDAO;
import com.taskflow.dao.IUserDAO;
import com.taskflow.dao.TaskDAOImpl;
import com.taskflow.dao.UserDAOImpl;
import com.taskflow.ui.MainFrame;
import javax.swing.SwingUtilities;

/**
 * Clase principal y punto de entrada de la aplicación TaskFlow.
 * Sencilla y fácil de explicar en la exposición.
 */
public class Main {
    public static void main(String[] args) {
        // Inicializar el gestor de tareas inyectando las dependencias DAO
        ITaskDAO taskDAO = new TaskDAOImpl();
        IUserDAO userDAO = new UserDAOImpl();
        TaskManager taskManager = new TaskManager(taskDAO, userDAO);

        // Lanzar la ventana gráfica principal en el hilo de Swing
        SwingUtilities.invokeLater(() -> {
            MainFrame ventana = new MainFrame(taskManager);
            ventana.setVisible(true);
        });
    }
}
