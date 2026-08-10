import model.Priority;
import model.Status;
import model.Task;
import model.User;
import service.TaskManager;

import javax.swing.*;
import java.util.List;

public class App {
    private static TaskManager taskManager = new TaskManager();

    public static void main(String[] args) {
        try {
            // Set System L&F for better looks
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Ignore
        }

        while (true) {
            String[] options = {
                    "Gestionar Usuarios",
                    "Gestionar Tareas",
                    "Tablero Kanban (Estado)",
                    "Mis Tareas",
                    "Cargar Datos de Prueba",
                    "Salir"
            };

            int choice = JOptionPane.showOptionDialog(null, "Gestor de Tareas (Jira/Trello Style)",
                    "Menú Principal", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, options, options[0]);

            // Menú principal: Usamos un switch para manejar las diferentes opciones seleccionadas por el usuario.
            switch (choice) {
                case 0:
                    manageUsers();
                    break;
                case 1:
                    manageTasks();
                    break;
                case 2:
                    showKanbanBoard();
                    break;
                case 3:
                    showUserTasks();
                    break;
                case 4:
                    loadTestData();
                    break;
                case 5:
                case JOptionPane.CLOSED_OPTION:
                    System.exit(0);
                    break;
            }
        }
    }

    // Módulo de Usuarios: Permite registrar nuevos usuarios o listar los ya existentes.
    private static void manageUsers() {
        String[] options = {"Crear Usuario", "Listar Usuarios", "Volver"};
        int choice = JOptionPane.showOptionDialog(null, "Gestión de Usuarios",
                "Usuarios", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        if (choice == 0) {
            String username = JOptionPane.showInputDialog("Ingrese el username:");
            if (username == null || username.trim().isEmpty()) return;
            String name = JOptionPane.showInputDialog("Ingrese el nombre completo:");
            if (name == null || name.trim().isEmpty()) return;
            
            taskManager.addUser(new User(username, name));
            JOptionPane.showMessageDialog(null, "Usuario creado exitosamente.");
        } else if (choice == 1) {
            List<User> users = taskManager.getUsers();
            if (users.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No hay usuarios registrados.");
            } else {
                StringBuilder sb = new StringBuilder("Usuarios:\n");
                for (User u : users) {
                    sb.append("- ").append(u.toString()).append("\n");
                }
                JOptionPane.showMessageDialog(null, sb.toString());
            }
        }
    }

    // Módulo de Tareas: Lógica para registrar nuevas tareas asignadas a usuarios y actualizar su estado.
    private static void manageTasks() {
        String[] options = {"Crear Tarea", "Cambiar Estado de Tarea", "Volver"};
        int choice = JOptionPane.showOptionDialog(null, "Gestión de Tareas",
                "Tareas", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        if (choice == 0) {
            if (taskManager.getUsers().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Debe crear al menos un usuario primero.");
                return;
            }

            String title = JOptionPane.showInputDialog("Título de la tarea:");
            if (title == null || title.trim().isEmpty()) return;

            String description = JOptionPane.showInputDialog("Descripción de la tarea:");
            if (description == null || description.trim().isEmpty()) return;

            Priority[] priorities = Priority.values();
            Priority priority = (Priority) JOptionPane.showInputDialog(null, "Seleccione la prioridad:",
                    "Prioridad", JOptionPane.QUESTION_MESSAGE, null, priorities, priorities[0]);
            if (priority == null) return;

            List<User> users = taskManager.getUsers();
            User user = (User) JOptionPane.showInputDialog(null, "Asignar a usuario:",
                    "Asignación", JOptionPane.QUESTION_MESSAGE, null, users.toArray(), users.get(0));
            if (user == null) return;

            Task task = new Task(title, description, priority, user);
            taskManager.addTask(task);
            JOptionPane.showMessageDialog(null, "Tarea creada exitosamente. ID: " + task.getId());
            
        } else if (choice == 1) {
            List<Task> tasks = taskManager.getTasks();
            if (tasks.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No hay tareas registradas.");
                return;
            }

            Task task = (Task) JOptionPane.showInputDialog(null, "Seleccione la tarea a modificar:",
                    "Cambiar Estado", JOptionPane.QUESTION_MESSAGE, null, tasks.toArray(), tasks.get(0));
            if (task == null) return;

            Status[] statuses = Status.values();
            Status newStatus = (Status) JOptionPane.showInputDialog(null, "Seleccione el nuevo estado:",
                    "Nuevo Estado", JOptionPane.QUESTION_MESSAGE, null, statuses, task.getStatus());
            if (newStatus != null) {
                task.setStatus(newStatus);
                JOptionPane.showMessageDialog(null, "Estado actualizado exitosamente.");
            }
        }
    }

    // Tablero Kanban: Recorre todos los estados posibles y muestra las tareas agrupadas por estado.
    private static void showKanbanBoard() {
        StringBuilder sb = new StringBuilder();
        for (Status status : Status.values()) {
            sb.append("--- ").append(status.getDescription().toUpperCase()).append(" ---\n");
            List<Task> tasksInStatus = taskManager.getTasksByStatus(status);
            if (tasksInStatus.isEmpty()) {
                sb.append("  (Vacío)\n");
            } else {
                for (Task t : tasksInStatus) {
                    sb.append("  - [").append(t.getPriority()).append("] ").append(t.getTitle())
                            .append(" (").append(t.getAssignedUser().getUsername()).append(")\n");
                }
            }
            sb.append("\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString(), "Tablero Kanban", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void showUserTasks() {
        List<User> users = taskManager.getUsers();
        if (users.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay usuarios registrados.");
            return;
        }

        User user = (User) JOptionPane.showInputDialog(null, "Seleccione un usuario:",
                "Mis Tareas", JOptionPane.QUESTION_MESSAGE, null, users.toArray(), users.get(0));
        
        if (user != null) {
            List<Task> userTasks = taskManager.getTasksByUserOrderedByPriority(user);
            if (userTasks.isEmpty()) {
                JOptionPane.showMessageDialog(null, "El usuario no tiene tareas asignadas.");
            } else {
                StringBuilder sb = new StringBuilder("Tareas de " + user.getName() + " (Ordenadas por Prioridad Alta->Baja):\n\n");
                for (Task t : userTasks) {
                    sb.append(t.toString()).append("\n");
                }
                JOptionPane.showMessageDialog(null, sb.toString(), "Tareas Asignadas", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private static void loadTestData() {
        User u1 = new User("dev1", "Alice Smith");
        User u2 = new User("dev2", "Bob Johnson");
        taskManager.addUser(u1);
        taskManager.addUser(u2);

        Task t1 = new Task("Configurar DB", "Instalar PostgreSQL y crear schemas", Priority.ALTA, u1);
        Task t2 = new Task("Crear vistas", "Vistas SQL de ventas", Priority.MEDIA, u1);
        Task t3 = new Task("Diseñar Login", "UI de login con Swing", Priority.ALTA, u2);
        Task t4 = new Task("Documentación", "Escribir README", Priority.BAJA, u2);
        
        t1.setStatus(Status.EN_PROCESO);
        t2.setStatus(Status.POR_REALIZAR);
        t3.setStatus(Status.FINALIZADO);

        taskManager.addTask(t1);
        taskManager.addTask(t2);
        taskManager.addTask(t3);
        taskManager.addTask(t4);

        JOptionPane.showMessageDialog(null, "Datos de prueba cargados.");
    }
}
