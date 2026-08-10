import model.Priority;
import model.Status;
import model.Task;
import model.User;
import service.TaskManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class App extends JFrame {
    private TaskManager taskManager = new TaskManager();
    private JTabbedPane tabbedPane;
    
    // Models for tables to refresh data
    private DefaultTableModel userTableModel;
    private DefaultTableModel taskTableModel;
    
    // Panels for Kanban
    private JPanel kanbanTodoPanel;
    private JPanel kanbanInProgressPanel;
    private JPanel kanbanDonePanel;
    
    public App() {
        setTitle("Gestor de Tareas (Jira/Trello Style)");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar en pantalla
        
        // Cargar datos de prueba por defecto
        loadTestData();
        
        tabbedPane = new JTabbedPane();
        
        tabbedPane.addTab("Usuarios", createUserPanel());
        tabbedPane.addTab("Tareas", createTaskPanel());
        tabbedPane.addTab("Tablero Kanban", createKanbanPanel());
        tabbedPane.addTab("Mis Tareas", createMyTasksPanel());
        
        // Listener para refrescar el Kanban y otras vistas al cambiar de pestaña
        tabbedPane.addChangeListener(e -> {
            refreshKanbanBoard();
        });
        
        add(tabbedPane);
    }
    
    private void loadTestData() {
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
    }
    
    private JPanel createUserPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Barra superior con botones
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAddUser = new JButton("Crear Usuario");
        topPanel.add(btnAddUser);
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Tabla de usuarios
        String[] columns = {"Username", "Nombre Completo"};
        userTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable userTable = new JTable(userTableModel);
        userTable.setRowHeight(25);
        panel.add(new JScrollPane(userTable), BorderLayout.CENTER);
        
        refreshUserTable();
        
        // Acción para crear usuario
        btnAddUser.addActionListener(e -> {
            JTextField txtUser = new JTextField();
            JTextField txtName = new JTextField();
            Object[] message = {
                "Username:", txtUser,
                "Nombre Completo:", txtName
            };
            
            int option = JOptionPane.showConfirmDialog(this, message, "Nuevo Usuario", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                if (!txtUser.getText().trim().isEmpty() && !txtName.getText().trim().isEmpty()) {
                    taskManager.addUser(new User(txtUser.getText().trim(), txtName.getText().trim()));
                    refreshUserTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Ambos campos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        return panel;
    }
    
    private void refreshUserTable() {
        userTableModel.setRowCount(0);
        for (User u : taskManager.getUsers()) {
            userTableModel.addRow(new Object[]{u.getUsername(), u.getName()});
        }
    }
    
    private JPanel createTaskPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAddTask = new JButton("Crear Tarea");
        JButton btnChangeStatus = new JButton("Cambiar Estado");
        topPanel.add(btnAddTask);
        topPanel.add(btnChangeStatus);
        panel.add(topPanel, BorderLayout.NORTH);
        
        String[] columns = {"ID", "Título", "Descripción", "Prioridad", "Usuario", "Estado"};
        taskTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable taskTable = new JTable(taskTableModel);
        taskTable.setRowHeight(25);
        panel.add(new JScrollPane(taskTable), BorderLayout.CENTER);
        
        refreshTaskTable();
        
        btnAddTask.addActionListener(e -> {
            if (taskManager.getUsers().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe crear al menos un usuario primero.");
                return;
            }
            
            JTextField txtTitle = new JTextField();
            JTextField txtDesc = new JTextField();
            JComboBox<Priority> cbPriority = new JComboBox<>(Priority.values());
            JComboBox<User> cbUser = new JComboBox<>(taskManager.getUsers().toArray(new User[0]));
            
            Object[] message = {
                "Título:", txtTitle,
                "Descripción:", txtDesc,
                "Prioridad:", cbPriority,
                "Asignar a:", cbUser
            };
            
            int option = JOptionPane.showConfirmDialog(this, message, "Nueva Tarea", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                if (!txtTitle.getText().trim().isEmpty()) {
                    Task task = new Task(
                        txtTitle.getText().trim(),
                        txtDesc.getText().trim(),
                        (Priority) cbPriority.getSelectedItem(),
                        (User) cbUser.getSelectedItem()
                    );
                    taskManager.addTask(task);
                    refreshTaskTable();
                } else {
                    JOptionPane.showMessageDialog(this, "El título es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        btnChangeStatus.addActionListener(e -> {
            int selectedRow = taskTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione una tarea de la tabla primero.");
                return;
            }
            
            int taskId = (int) taskTableModel.getValueAt(selectedRow, 0);
            Task task = taskManager.getTaskById(taskId);
            
            if (task != null) {
                JComboBox<Status> cbStatus = new JComboBox<>(Status.values());
                cbStatus.setSelectedItem(task.getStatus());
                
                Object[] message = { "Nuevo Estado:", cbStatus };
                int option = JOptionPane.showConfirmDialog(this, message, "Cambiar Estado", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    task.setStatus((Status) cbStatus.getSelectedItem());
                    refreshTaskTable();
                }
            }
        });
        
        return panel;
    }
    
    private void refreshTaskTable() {
        taskTableModel.setRowCount(0);
        for (Task t : taskManager.getTasks()) {
            taskTableModel.addRow(new Object[]{
                t.getId(), t.getTitle(), t.getDescription(), t.getPriority(), 
                t.getAssignedUser().getUsername(), t.getStatus().getDescription()
            });
        }
    }
    
    private JPanel createKanbanPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        kanbanTodoPanel = createKanbanColumn("POR REALIZAR");
        kanbanInProgressPanel = createKanbanColumn("EN PROCESO");
        kanbanDonePanel = createKanbanColumn("FINALIZADO");
        
        panel.add(kanbanTodoPanel);
        panel.add(kanbanInProgressPanel);
        panel.add(kanbanDonePanel);
        
        return panel;
    }
    
    private JPanel createKanbanColumn(String title) {
        JPanel colPanel = new JPanel(new BorderLayout());
        colPanel.setBorder(BorderFactory.createTitledBorder(title));
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        colPanel.add(scrollPane, BorderLayout.CENTER);
        
        return colPanel;
    }
    
    private void refreshKanbanBoard() {
        fillKanbanColumn(kanbanTodoPanel, Status.POR_REALIZAR);
        fillKanbanColumn(kanbanInProgressPanel, Status.EN_PROCESO);
        fillKanbanColumn(kanbanDonePanel, Status.FINALIZADO);
    }
    
    private void fillKanbanColumn(JPanel columnPanel, Status status) {
        // Obtenemos el content panel dentro del scroll pane
        JScrollPane scrollPane = (JScrollPane) columnPanel.getComponent(0);
        JPanel contentPanel = (JPanel) scrollPane.getViewport().getView();
        
        contentPanel.removeAll();
        
        List<Task> tasks = taskManager.getTasksByStatus(status);
        for (Task t : tasks) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            card.setBackground(new Color(245, 245, 250));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
            
            JLabel lblTitle = new JLabel("<html><b>" + t.getTitle() + "</b></html>");
            JLabel lblDetails = new JLabel(t.getAssignedUser().getUsername() + " | Prioridad: " + t.getPriority());
            
            card.add(lblTitle, BorderLayout.NORTH);
            card.add(lblDetails, BorderLayout.SOUTH);
            
            contentPanel.add(card);
            contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private JPanel createMyTasksPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Seleccionar Usuario:"));
        
        JComboBox<User> cbUser = new JComboBox<>();
        JButton btnView = new JButton("Ver Tareas");
        
        topPanel.add(cbUser);
        topPanel.add(btnView);
        panel.add(topPanel, BorderLayout.NORTH);
        
        JTextArea txtArea = new JTextArea();
        txtArea.setEditable(false);
        txtArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        txtArea.setMargin(new Insets(10, 10, 10, 10));
        panel.add(new JScrollPane(txtArea), BorderLayout.CENTER);
        
        // Listener para refrescar el combo box al seleccionar esta pestaña
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedComponent() == panel) {
                User selected = (User) cbUser.getSelectedItem();
                cbUser.removeAllItems();
                for (User u : taskManager.getUsers()) {
                    cbUser.addItem(u);
                }
                if (selected != null) {
                    cbUser.setSelectedItem(selected);
                }
            }
        });
        
        btnView.addActionListener(e -> {
            User selected = (User) cbUser.getSelectedItem();
            if (selected != null) {
                List<Task> userTasks = taskManager.getTasksByUserOrderedByPriority(selected);
                StringBuilder sb = new StringBuilder();
                if (userTasks.isEmpty()) {
                    sb.append("No hay tareas asignadas para este usuario.\n");
                } else {
                    sb.append("Tareas de ").append(selected.getName()).append(":\n");
                    sb.append("--------------------------------------------------\n\n");
                    for (Task t : userTasks) {
                        sb.append(t.toString()).append("\n");
                    }
                }
                txtArea.setText(sb.toString());
            }
        });
        
        return panel;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new App().setVisible(true);
        });
    }
}
