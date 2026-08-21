package com.taskflow.ui;

import com.taskflow.controller.TaskController;
import com.taskflow.model.Priority;
import com.taskflow.model.Task;
import com.taskflow.model.TaskStatus;
import com.taskflow.model.User;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Ventana Principal de TaskFlow (Capa Vista / View en MVC).
 * Diseñada en Swing con paleta clara de alto contraste, texto 100% legible y fácil de explicar.
 */
public class MainFrame extends JFrame {
    private final TaskController taskManager;

    private JTabbedPane tabbedPane;
    private JPanel kanbanPanel;
    private JPanel userViewPanel;
    private JComboBox<UserItem> userComboBox;
    private JPanel userTasksContainer;

    // Paneles para las 3 columnas Kanban
    private JPanel todoColumn;
    private JPanel inProgressColumn;
    private JPanel doneColumn;

    // Filtro por Prioridad (Botones rápidos)
    private Priority filtroPrioridadActual = null; // null = Todas
    private JButton btnFiltroTodas;
    private JButton btnFiltroAlta;
    private JButton btnFiltroMedia;
    private JButton btnFiltroBaja;

    // Paleta de Colores de Alto Contraste (Claro + Encabezado Oscuro Elegante)
    private static final Color BG_MAIN = new Color(243, 244, 246);       // Fondo principal claro #F3F4F6
    private static final Color BG_HEADER = new Color(31, 41, 55);        // Encabezado oscuro slate #1F2937
    private static final Color BG_COLUMN = new Color(229, 231, 235);     // Fondo de columna #E5E7EB
    private static final Color BG_CARD = new Color(255, 255, 255);       // Tarjeta blanca pura #FFFFFF
    private static final Color BORDER_COLOR = new Color(209, 213, 219);  // Borde fino #D1D5DB

    private static final Color TEXT_DARK = new Color(17, 24, 39);        // Texto principal oscuro #111827
    private static final Color TEXT_MUTED = new Color(75, 85, 99);       // Texto secundario #4B5563
    private static final Color TEXT_WHITE = new Color(255, 255, 255);     // Texto blanco puro

    private static final Color BTN_BLUE = new Color(37, 99, 235);        // Azul primario #2563EB
    private static final Color BTN_GREEN = new Color(16, 185, 129);      // Verde primario #10B981
    private static final Color BTN_RED = new Color(239, 68, 68);         // Rojo eliminar #EF4444

    public MainFrame(TaskController taskManager) {
        this.taskManager = taskManager;

        setTitle("TaskFlow — Gestor de Tareas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 720);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_MAIN);
        setLayout(new BorderLayout());

        crearBarraSuperior();
        crearPestañasPrincipales();

        actualizarTodo();
    }

    private void crearBarraSuperior() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_HEADER);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));

        // Subpanel Izquierdo (Título + Botones de Filtro por Prioridad)
        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftHeader.setOpaque(false);

        JLabel titleLabel = new JLabel("TaskFlow");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_WHITE);
        leftHeader.add(titleLabel);

        JLabel filterLabel = new JLabel("Prioridad:");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        filterLabel.setForeground(new Color(209, 213, 219));
        leftHeader.add(filterLabel);

        btnFiltroTodas = crearBotonFiltro("Todas", null);
        btnFiltroAlta = crearBotonFiltro("Alta", Priority.HIGH);
        btnFiltroMedia = crearBotonFiltro("Media", Priority.MEDIUM);
        btnFiltroBaja = crearBotonFiltro("Baja", Priority.LOW);

        leftHeader.add(btnFiltroTodas);
        leftHeader.add(btnFiltroAlta);
        leftHeader.add(btnFiltroMedia);
        leftHeader.add(btnFiltroBaja);

        actualizarEstilosBotonesFiltro();
        headerPanel.add(leftHeader, BorderLayout.WEST);

        // Botones de Acción Principales (Derecha)
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setOpaque(false);

        JButton btnNuevoUsuario = crearBoton("+ Nuevo Usuario", BTN_GREEN);
        btnNuevoUsuario.addActionListener(e -> accionNuevoUsuario());

        JButton btnNuevaTarea = crearBoton("+ Nueva Tarea", BTN_BLUE);
        btnNuevaTarea.addActionListener(e -> accionNuevaTarea());

        buttonsPanel.add(btnNuevoUsuario);
        buttonsPanel.add(btnNuevaTarea);

        headerPanel.add(buttonsPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
    }

    private void crearPestañasPrincipales() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setBackground(BG_MAIN);
        tabbedPane.setForeground(TEXT_DARK);

        // Pestaña 1: Tablero Kanban
        kanbanPanel = crearPanelKanban();
        tabbedPane.addTab("Tablero Kanban", kanbanPanel);

        // Pestaña 2: Vista por Usuario
        userViewPanel = crearPanelVistaUsuario();
        tabbedPane.addTab("Vista por Usuario", userViewPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    // ==================== PESTAÑA 1: TABLERO KANBAN ====================

    private JPanel crearPanelKanban() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 14, 0));
        panel.setBackground(BG_MAIN);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 16, 16, 16));

        todoColumn = crearColumnaKanban("POR HACER", TaskStatus.TODO.getColor());
        inProgressColumn = crearColumnaKanban("EN PROCESO", TaskStatus.IN_PROGRESS.getColor());
        doneColumn = crearColumnaKanban("FINALIZADO", TaskStatus.DONE.getColor());

        panel.add(todoColumn);
        panel.add(inProgressColumn);
        panel.add(doneColumn);

        return panel;
    }

    private JPanel crearColumnaKanban(String titulo, Color colorEncabezado) {
        JPanel columna = new JPanel(new BorderLayout(0, 10));
        columna.setBackground(BG_COLUMN);
        columna.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Encabezado
        JLabel labelTitulo = new JLabel(titulo, SwingConstants.LEFT);
        labelTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelTitulo.setForeground(colorEncabezado);
        labelTitulo.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, colorEncabezado));
        columna.add(labelTitulo, BorderLayout.NORTH);

        // Contenedor de tarjetas
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(BG_COLUMN);

        JScrollPane scroll = new JScrollPane(container);
        scroll.setBorder(null);
        scroll.setBackground(BG_COLUMN);
        scroll.getViewport().setBackground(BG_COLUMN);
        scroll.getVerticalScrollBar().setUnitIncrement(14);

        columna.add(scroll, BorderLayout.CENTER);
        return columna;
    }

    // ==================== PESTAÑA 2: VISTA POR USUARIO ====================

    private JPanel crearPanelVistaUsuario() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(BG_MAIN);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 16, 16, 16));

        // Selector de Usuario
        JPanel topUserSelector = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topUserSelector.setOpaque(false);

        JLabel labelSelect = new JLabel("Seleccionar Usuario:");
        labelSelect.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labelSelect.setForeground(TEXT_DARK);

        userComboBox = new JComboBox<>();
        userComboBox.setPreferredSize(new Dimension(230, 32));
        userComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userComboBox.setBackground(BG_CARD);
        userComboBox.setForeground(TEXT_DARK);
        userComboBox.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        userComboBox.addActionListener(e -> actualizarVistaUsuario());

        topUserSelector.add(labelSelect);
        topUserSelector.add(userComboBox);
        panel.add(topUserSelector, BorderLayout.NORTH);

        // Contenedor de Tareas Agrupadas por Prioridad
        userTasksContainer = new JPanel();
        userTasksContainer.setLayout(new BoxLayout(userTasksContainer, BoxLayout.Y_AXIS));
        userTasksContainer.setBackground(BG_MAIN);

        JScrollPane scroll = new JScrollPane(userTasksContainer);
        scroll.setBorder(null);
        scroll.setBackground(BG_MAIN);
        scroll.getViewport().setBackground(BG_MAIN);

        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // ==================== MÉTODOS DE ACTUALIZACIÓN ====================

    public void actualizarTodo() {
        actualizarTableroKanban();
        actualizarComboUsuarios();
        actualizarVistaUsuario();
    }

    private void actualizarTableroKanban() {
        actualizarColumnaEspecifica(todoColumn, TaskStatus.TODO);
        actualizarColumnaEspecifica(inProgressColumn, TaskStatus.IN_PROGRESS);
        actualizarColumnaEspecifica(doneColumn, TaskStatus.DONE);
    }

    private void actualizarColumnaEspecifica(JPanel columnaPanel, TaskStatus estado) {
        JScrollPane scroll = (JScrollPane) columnaPanel.getComponent(1);
        JPanel container = (JPanel) scroll.getViewport().getView();
        container.removeAll();

        List<Task> tareasEstado = taskManager.obtenerTareasPorEstado(estado);
        int tareasMostradas = 0;

        for (Task t : tareasEstado) {
            if (filtroPrioridadActual != null && t.getPriority() != filtroPrioridadActual) {
                continue;
            }
            container.add(crearTarjetaTareaPanel(t));
            container.add(Box.createRigidArea(new Dimension(0, 8)));
            tareasMostradas++;
        }

        if (tareasMostradas == 0) {
            String textoVacio = filtroPrioridadActual != null
                ? "Sin tareas " + filtroPrioridadActual.getLabel().toLowerCase()
                : "Sin tareas";
            JLabel emptyLabel = new JLabel(textoVacio, SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            emptyLabel.setForeground(TEXT_MUTED);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            container.add(Box.createRigidArea(new Dimension(0, 15)));
            container.add(emptyLabel);
        }

        container.revalidate();
        container.repaint();
    }

    private JPanel crearTarjetaTareaPanel(Task task) {
        JPanel tarjeta = new JPanel(new BorderLayout(0, 6));
        tarjeta.setBackground(BG_CARD);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        // Encabezado Tarjeta (ID + Badge de Prioridad)
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel idLabel = new JLabel("TSK-" + task.getId().toUpperCase());
        idLabel.setFont(new Font("Consolas", Font.BOLD, 12));
        idLabel.setForeground(TEXT_MUTED);

        JLabel priorityBadge = new JLabel(" Prioridad " + task.getPriority().getLabel() + " ");
        priorityBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        priorityBadge.setForeground(TEXT_WHITE);
        priorityBadge.setBackground(task.getPriority().getColor());
        priorityBadge.setOpaque(true);
        priorityBadge.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));

        top.add(idLabel, BorderLayout.WEST);
        top.add(priorityBadge, BorderLayout.EAST);
        tarjeta.add(top, BorderLayout.NORTH);

        // Centro (Título + Descripción)
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        JLabel titleLabel = new JLabel(task.getTitle());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLabel.setForeground(TEXT_DARK);
        center.add(titleLabel);

        if (task.getDescription() != null && !task.getDescription().trim().isEmpty()) {
            String desc = task.getDescription().trim();
            if (desc.length() > 45) desc = desc.substring(0, 42) + "...";
            JLabel descLabel = new JLabel(desc);
            descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            descLabel.setForeground(TEXT_MUTED);
            center.add(descLabel);
        }

        tarjeta.add(center, BorderLayout.CENTER);

        // Pie (Usuario + Botones)
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);

        User usuario = taskManager.obtenerUsuarioPorId(task.getAssignedUserId());
        String nombreUsuario = usuario != null ? usuario.getName() : "Sin asignar";
        JLabel userLabel = new JLabel("Asignado a: " + nombreUsuario);
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        userLabel.setForeground(TEXT_MUTED);
        bottom.add(userLabel, BorderLayout.WEST);

        // Botones de acción
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        actions.setOpaque(false);

        JComboBox<TaskStatus> comboEstado = new JComboBox<>(TaskStatus.values());
        comboEstado.setSelectedItem(task.getStatus());
        comboEstado.setFont(new Font("Segoe UI", Font.BOLD, 11));
        comboEstado.setBackground(BG_CARD);
        comboEstado.setForeground(task.getStatus().getColor());
        comboEstado.setFocusable(false);
        comboEstado.setToolTipText("Mover tarea a otro estado");
        comboEstado.addActionListener(e -> {
            TaskStatus nuevoEstado = (TaskStatus) comboEstado.getSelectedItem();
            if (nuevoEstado != null && nuevoEstado != task.getStatus()) {
                taskManager.cambiarEstadoTarea(task.getId(), nuevoEstado);
                actualizarTodo();
            }
        });
        actions.add(comboEstado);

        JButton btnEliminar = new JButton("✕");
        btnEliminar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnEliminar.setBackground(BTN_RED);
        btnEliminar.setForeground(TEXT_WHITE);
        btnEliminar.setFocusPainted(false);
        btnEliminar.setMargin(new Insets(2, 6, 2, 6));
        btnEliminar.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this, "¿Eliminar la tarea \"" + task.getTitle() + "\"?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                taskManager.eliminarTarea(task.getId());
                actualizarTodo();
            }
        });
        actions.add(btnEliminar);

        bottom.add(actions, BorderLayout.EAST);
        tarjeta.add(bottom, BorderLayout.SOUTH);

        return tarjeta;
    }

    private void actualizarComboUsuarios() {
        userComboBox.removeAllItems();
        userComboBox.addItem(new UserItem(null, "-- Seleccionar Usuario --"));
        for (User u : taskManager.obtenerUsuarios()) {
            userComboBox.addItem(new UserItem(u.getId(), u.getName()));
        }
    }

    private void actualizarVistaUsuario() {
        if (userTasksContainer == null) return;
        userTasksContainer.removeAll();

        UserItem selectedUser = (UserItem) userComboBox.getSelectedItem();
        if (selectedUser == null || selectedUser.getId() == null) {
            JLabel labelInfo = new JLabel("Selecciona un usuario en el desplegable superior para ver sus tareas asignadas.");
            labelInfo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            labelInfo.setForeground(TEXT_MUTED);
            labelInfo.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
            userTasksContainer.add(labelInfo);
        } else {
            int gruposMostrados = 0;
            for (Priority priority : Priority.values()) {
                if (filtroPrioridadActual != null && priority != filtroPrioridadActual) {
                    continue;
                }
                List<Task> tareasPrioridad = taskManager.obtenerTareasPorUsuarioYPrioridad(selectedUser.getId(), priority);

                if (!tareasPrioridad.isEmpty()) {
                    gruposMostrados++;
                    JPanel grupo = new JPanel();
                    grupo.setLayout(new BoxLayout(grupo, BoxLayout.Y_AXIS));
                    grupo.setOpaque(false);
                    grupo.setAlignmentX(Component.LEFT_ALIGNMENT);

                    JLabel labelGrupo = new JLabel("PRIORIDAD " + priority.getLabel().toUpperCase() + " (" + tareasPrioridad.size() + ")");
                    labelGrupo.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    labelGrupo.setForeground(priority.getColor());
                    labelGrupo.setAlignmentX(Component.LEFT_ALIGNMENT);
                    grupo.add(labelGrupo);
                    grupo.add(Box.createRigidArea(new Dimension(0, 6)));

                    for (Task t : tareasPrioridad) {
                        JPanel fila = new JPanel(new BorderLayout(10, 0));
                        fila.setBackground(BG_CARD);
                        fila.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(BORDER_COLOR, 1),
                            BorderFactory.createEmptyBorder(8, 12, 8, 12)
                        ));
                        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
                        fila.setAlignmentX(Component.LEFT_ALIGNMENT);

                        JLabel idLabel = new JLabel("TSK-" + t.getId().toUpperCase());
                        idLabel.setFont(new Font("Consolas", Font.BOLD, 12));
                        idLabel.setForeground(TEXT_MUTED);

                        JLabel titleLabel = new JLabel(t.getTitle());
                        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
                        titleLabel.setForeground(TEXT_DARK);

                        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
                        left.setOpaque(false);
                        left.add(idLabel);
                        left.add(titleLabel);

                        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
                        right.setOpaque(false);

                        JLabel labelEstado = new JLabel("Estado:");
                        labelEstado.setFont(new Font("Segoe UI", Font.BOLD, 12));
                        labelEstado.setForeground(TEXT_MUTED);
                        right.add(labelEstado);

                        JComboBox<TaskStatus> comboStatusUser = new JComboBox<>(TaskStatus.values());
                        comboStatusUser.setSelectedItem(t.getStatus());
                        comboStatusUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
                        comboStatusUser.setBackground(BG_CARD);
                        comboStatusUser.setForeground(t.getStatus().getColor());
                        comboStatusUser.setFocusable(false);

                        final Task currentTask = t;
                        comboStatusUser.addActionListener(e -> {
                            TaskStatus nuevoEstado = (TaskStatus) comboStatusUser.getSelectedItem();
                            if (nuevoEstado != null && nuevoEstado != currentTask.getStatus()) {
                                taskManager.cambiarEstadoTarea(currentTask.getId(), nuevoEstado);
                                actualizarTodo();
                            }
                        });
                        right.add(comboStatusUser);

                        fila.add(left, BorderLayout.WEST);
                        fila.add(right, BorderLayout.EAST);

                        grupo.add(fila);
                        grupo.add(Box.createRigidArea(new Dimension(0, 4)));
                    }

                    userTasksContainer.add(grupo);
                    userTasksContainer.add(Box.createRigidArea(new Dimension(0, 14)));
                }
            }

            if (gruposMostrados == 0) {
                String msg = filtroPrioridadActual != null
                    ? "El usuario \"" + selectedUser.getName() + "\" no tiene tareas asignadas con prioridad " + filtroPrioridadActual.getLabel() + "."
                    : "El usuario \"" + selectedUser.getName() + "\" no tiene tareas asignadas.";
                JLabel labelEmptyUser = new JLabel(msg);
                labelEmptyUser.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                labelEmptyUser.setForeground(TEXT_MUTED);
                labelEmptyUser.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
                userTasksContainer.add(labelEmptyUser);
            }
        }

        userTasksContainer.revalidate();
        userTasksContainer.repaint();
    }

    // ==================== ACCIONES CON JOPTIONPANE ====================

    private void accionNuevoUsuario() {
        String nombre = JOptionPane.showInputDialog(this, "Ingrese el nombre del nuevo usuario:", "Nuevo Usuario", JOptionPane.PLAIN_MESSAGE);
        if (nombre != null && !nombre.trim().isEmpty()) {
            taskManager.crearUsuario(nombre.trim());
            actualizarTodo();
            JOptionPane.showMessageDialog(this, "Usuario \"" + nombre.trim() + "\" creado exitosamente.");
        }
    }

    private void accionNuevaTarea() {
        List<User> usuarios = taskManager.obtenerUsuarios();
        if (usuarios.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Primero debe crear al menos un usuario.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String titulo = JOptionPane.showInputDialog(this, "Título de la tarea:", "Nueva Tarea", JOptionPane.PLAIN_MESSAGE);
        if (titulo == null || titulo.trim().isEmpty()) return;

        String descripcion = JOptionPane.showInputDialog(this, "Descripción (opcional):", "Nueva Tarea", JOptionPane.PLAIN_MESSAGE);

        Priority[] prioridades = Priority.values();
        Priority prioridad = (Priority) JOptionPane.showInputDialog(
            this, "Seleccione la prioridad:", "Nueva Tarea",
            JOptionPane.QUESTION_MESSAGE, null, prioridades, prioridades[0]
        );
        if (prioridad == null) return;

        User usuario = (User) JOptionPane.showInputDialog(
            this, "Seleccione el usuario asignado:", "Nueva Tarea",
            JOptionPane.QUESTION_MESSAGE, null, usuarios.toArray(), usuarios.get(0)
        );
        if (usuario == null) return;

        taskManager.crearTarea(titulo.trim(), descripcion != null ? descripcion.trim() : "", prioridad, usuario.getId());
        actualizarTodo();
        JOptionPane.showMessageDialog(this, "Tarea creada exitosamente.");
    }

    private JButton crearBotonFiltro(String texto, Priority priority) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> {
            filtroPrioridadActual = priority;
            actualizarEstilosBotonesFiltro();
            actualizarTodo();
        });
        return btn;
    }

    private void actualizarEstilosBotonesFiltro() {
        aplicarEstiloFiltro(btnFiltroTodas, filtroPrioridadActual == null, BG_HEADER, TEXT_WHITE);
        aplicarEstiloFiltro(btnFiltroAlta, filtroPrioridadActual == Priority.HIGH, Priority.HIGH.getColor(), TEXT_WHITE);
        aplicarEstiloFiltro(btnFiltroMedia, filtroPrioridadActual == Priority.MEDIUM, Priority.MEDIUM.getColor(), TEXT_WHITE);
        aplicarEstiloFiltro(btnFiltroBaja, filtroPrioridadActual == Priority.LOW, Priority.LOW.getColor(), TEXT_WHITE);
    }

    private void aplicarEstiloFiltro(JButton btn, boolean seleccionado, Color colorBase, Color colorTexto) {
        if (btn == null) return;
        if (seleccionado) {
            btn.setBackground(colorBase);
            btn.setForeground(colorTexto);
            btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TEXT_WHITE, 2),
                BorderFactory.createEmptyBorder(3, 10, 3, 10)
            ));
        } else {
            btn.setBackground(new Color(55, 65, 81)); // Slate oscuro #374151
            btn.setForeground(new Color(209, 213, 219)); // Texto gris suave
            btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(4, 11, 4, 11)
            ));
        }
    }

    private JButton crearBoton(String texto, Color bg) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(TEXT_WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private static class UserItem {
        private final String id;
        private final String name;

        UserItem(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() { return id; }
        public String getName() { return name; }

        @Override
        public String toString() { return name; }
    }
}
