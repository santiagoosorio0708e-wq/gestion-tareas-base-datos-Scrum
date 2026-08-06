package service;

import model.Priority;
import model.Status;
import model.Task;
import model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskManager {
    private List<User> users;
    private List<Task> tasks;

    public TaskManager() {
        this.users = new ArrayList<>();
        this.tasks = new ArrayList<>();
    }

    public void addUser(User user) {
        if (!users.contains(user)) {
            users.add(user);
        }
    }

    public List<User> getUsers() {
        return users;
    }

    public User getUserByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public Task getTaskById(int id) {
        for (Task task : tasks) {
            if (task.getId() == id) {
                return task;
            }
        }
        return null;
    }

    public List<Task> getTasksByStatus(Status status) {
        return tasks.stream()
                .filter(task -> task.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<Task> getTasksByUserOrderedByPriority(User user) {
        return tasks.stream()
                .filter(task -> task.getAssignedUser() != null && task.getAssignedUser().equals(user))
                .sorted((t1, t2) -> t1.getPriority().compareTo(t2.getPriority()))
                .collect(Collectors.toList());
    }
}
