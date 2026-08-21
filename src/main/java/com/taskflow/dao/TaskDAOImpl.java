package com.taskflow.dao;

import com.taskflow.db.DatabaseConnection;
import com.taskflow.model.Priority;
import com.taskflow.model.Task;
import com.taskflow.model.TaskStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskDAOImpl implements ITaskDAO {

    @Override
    public List<Task> getAll() {
        List<Task> tasks = new ArrayList<>();
        String query = "SELECT id, title, description, priority_id, status_id, assigned_user_id FROM tasks";
        
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            System.err.println("Advertencia: No hay conexión a MySQL para obtener tareas.");
            return tasks;
        }

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
             
            while (rs.next()) {
                tasks.add(mapResultSetToTask(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    @Override
    public Task getById(String id) {
        String query = "SELECT id, title, description, priority_id, status_id, assigned_user_id FROM tasks WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return null;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTask(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean insert(Task task) {
        String query = "INSERT INTO tasks (id, title, description, priority_id, status_id, assigned_user_id) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, task.getId());
            pstmt.setString(2, task.getTitle());
            pstmt.setString(3, task.getDescription());
            pstmt.setString(4, task.getPriority() != null ? task.getPriority().name() : null);
            pstmt.setString(5, task.getStatus() != null ? task.getStatus().name() : null);
            pstmt.setString(6, task.getAssignedUserId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Task task) {
        String query = "UPDATE tasks SET title = ?, description = ?, priority_id = ?, status_id = ?, assigned_user_id = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            pstmt.setString(3, task.getPriority() != null ? task.getPriority().name() : null);
            pstmt.setString(4, task.getStatus() != null ? task.getStatus().name() : null);
            pstmt.setString(5, task.getAssignedUserId());
            pstmt.setString(6, task.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String taskId) {
        String query = "DELETE FROM tasks WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, taskId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateTaskStatus(String taskId, TaskStatus newStatus) {
        String query = "UPDATE tasks SET status_id = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, newStatus != null ? newStatus.name() : null);
            pstmt.setString(2, taskId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Task mapResultSetToTask(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setId(rs.getString("id"));
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));
        
        String priorityId = rs.getString("priority_id");
        if (priorityId != null) {
            try {
                task.setPriority(Priority.valueOf(priorityId));
            } catch (IllegalArgumentException ignored) {}
        }
        
        String statusId = rs.getString("status_id");
        if (statusId != null) {
            try {
                task.setStatus(TaskStatus.valueOf(statusId));
            } catch (IllegalArgumentException ignored) {}
        }
        
        task.setAssignedUserId(rs.getString("assigned_user_id"));
        return task;
    }
}
