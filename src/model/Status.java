package model;

public enum Status {
    POR_REALIZAR("Por realizar"),
    EN_PROCESO("En proceso"),
    FINALIZADO("Finalizado");

    private String description;

    Status(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
