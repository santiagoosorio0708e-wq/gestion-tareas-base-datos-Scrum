package model;

public class Task {
    private int id;
    private String title;
    private String description;
    private Priority priority;
    private Status status;
    private Person assignedPerson;

    // Constructor completo (al cargar de BD)
    public Task(int id, String title, String description, Priority priority, Status status, Person assignedPerson) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.assignedPerson = assignedPerson;
    }

    // Constructor para tareas nuevas
    public Task(String title, String description, Priority priority, Person assignedPerson) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = Status.POR_REALIZAR;
        this.assignedPerson = assignedPerson;
    }

    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
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

    public Person getAssignedPerson() {
        return assignedPerson;
    }

    public void setAssignedPerson(Person assignedPerson) {
        this.assignedPerson = assignedPerson;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s (Prioridad: %s) - Estado: %s - Asignado a: %s",
                id, title, priority, status, assignedPerson != null ? assignedPerson.getName() : "Sin asignar");
    }
}
