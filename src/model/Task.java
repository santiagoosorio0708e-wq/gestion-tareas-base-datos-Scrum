package model;

public class Task {
    private static int nextId = 1;

    private int id;
    private String title;
    private String description;
    private Priority priority;
    private Status status;
    private User assignedUser;

    // Constructor: Inicializa una tarea nueva. Toda tarea nace por defecto en estado 'POR_REALIZAR'.
    public Task(String title, String description, Priority priority, User assignedUser) {
        this.id = nextId++;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = Status.POR_REALIZAR;
        this.assignedUser = assignedUser;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public User getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(User assignedUser) {
        this.assignedUser = assignedUser;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s (Prioridad: %s) - Estado: %s - Asignado a: %s",
                id, title, priority, status, assignedUser != null ? assignedUser.getName() : "Sin asignar");
    }
}
