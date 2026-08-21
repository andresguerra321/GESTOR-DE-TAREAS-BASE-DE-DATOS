CREATE DATABASE IF NOT EXISTS taskflow_db;
USE taskflow_db;

-- Tabla de Usuarios
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

-- Tabla de Estados de Tareas
CREATE TABLE IF NOT EXISTS task_statuses (
    id VARCHAR(50) PRIMARY KEY,
    label VARCHAR(50) NOT NULL,
    color_hex VARCHAR(10) NOT NULL
);

-- Insertar estados por defecto
INSERT IGNORE INTO task_statuses (id, label, color_hex) VALUES 
('TODO', 'Por Hacer', '#6B7280'),
('IN_PROGRESS', 'En Proceso', '#2563EB'),
('DONE', 'Finalizado', '#10B981');

-- Tabla de Prioridades
CREATE TABLE IF NOT EXISTS priorities (
    id VARCHAR(50) PRIMARY KEY,
    label VARCHAR(50) NOT NULL,
    level INT NOT NULL
);

-- Insertar prioridades por defecto
INSERT IGNORE INTO priorities (id, label, level) VALUES 
('LOW', 'Baja', 1),
('MEDIUM', 'Media', 2),
('HIGH', 'Alta', 3);

-- Tabla de Tareas
CREATE TABLE IF NOT EXISTS tasks (
    id VARCHAR(36) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    priority_id VARCHAR(50),
    status_id VARCHAR(50),
    assigned_user_id VARCHAR(36),
    FOREIGN KEY (priority_id) REFERENCES priorities(id),
    FOREIGN KEY (status_id) REFERENCES task_statuses(id),
    FOREIGN KEY (assigned_user_id) REFERENCES users(id) ON DELETE SET NULL
);
