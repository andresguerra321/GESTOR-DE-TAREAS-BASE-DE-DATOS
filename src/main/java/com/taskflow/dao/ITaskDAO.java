package com.taskflow.dao;

import com.taskflow.model.Task;
import com.taskflow.model.TaskStatus;

public interface ITaskDAO extends IGenericDAO<Task, String> {
    boolean updateTaskStatus(String taskId, TaskStatus newStatus);
}
