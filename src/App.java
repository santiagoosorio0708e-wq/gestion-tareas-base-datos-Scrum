import model.Priority;
import model.Status;
import model.Task;
import model.Person;
import model.TypePerson;
import service.TaskManager;

import com.formdev.flatlaf.FlatIntelliJLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * Clase principal de la aplicación con GUI mejorada usando FlatLaf.
 */
public class App extends JFrame {
    private TaskManager taskManager;
    private JTabbedPane tabbedPane;
    
    private DefaultTableModel personTableModel;
    private DefaultTableModel taskTableModel;
    
    private JPanel kanbanTodoPanel;
    private JPanel kanbanInProgressPanel;
    private JPanel kanbanDonePanel;
    
    public App() {
        taskManager = new TaskManager(); 
        
        setTitle("Gestor de Tareas Scrum Pro");
        setSize(1000, 700); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        
        // Estilo general de la aplicación
        setFontsAndStyles();
        
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.addTab("👥 Usuarios", createPersonPanel());
        tabbedPane.addTab("📝 Tareas", createTaskPanel());
        tabbedPane.addTab("📋 Tablero Kanban", createKanbanPanel());
        tabbedPane.addTab("👤 Mis Tareas", createMyTasksPanel());
        
        tabbedPane.addChangeListener(e -> refreshKanbanBoard());
        
        // Fondo principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private void setFontsAndStyles() {
        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 13));
        UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("Table.font", new Font("Segoe UI", Font.PLAIN, 13));
        UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 14));
        UIManager.put("ComboBox.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("TextField.font", new Font("Segoe UI", Font.PLAIN, 14));
        
        // Botones más estilizados
        UIManager.put("Button.arc", 10);
        UIManager.put("Component.arc", 10);
        UIManager.put("ProgressBar.arc", 10);
        UIManager.put("TextComponent.arc", 10);
    }
    
    private void preventNumbers(JTextField textField) {
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (Character.isDigit(e.getKeyChar())) {
                    e.consume();
                }
            }
        });
    }
    
    private void styleTable(JTable table) {
        table.setRowHeight(35);
        table.setShowGrid(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(new Color(200, 225, 255));
        table.setSelectionForeground(Color.BLACK);
        
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(245, 245, 250));
        header.setForeground(new Color(50, 50, 50));
        header.setPreferredSize(new Dimension(100, 40));
        
        // Centrar contenido de celdas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for(int i=0; i<table.getColumnCount(); i++){
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }
    
    private JPanel createPersonPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAddPerson = new JButton("➕ Crear Usuario");
        btnAddPerson.setBackground(new Color(66, 139, 202));
        btnAddPerson.setForeground(Color.WHITE);
        topPanel.add(btnAddPerson);
        panel.add(topPanel, BorderLayout.NORTH);
        
        String[] columns = {"ID", "Username", "Nombre Completo", "Rol Scrum"};
        personTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable personTable = new JTable(personTableModel);
        styleTable(personTable);
        
        panel.add(new JScrollPane(personTable), BorderLayout.CENTER);
        
        refreshPersonTable();
        
        btnAddPerson.addActionListener(e -> {
            JTextField txtUser = new JTextField();
            JTextField txtName = new JTextField();
            preventNumbers(txtUser);
            preventNumbers(txtName);
            
            List<TypePerson> roles = taskManager.getTypePersons();
            JComboBox<TypePerson> cbRole = new JComboBox<>(roles.toArray(new TypePerson[0]));
            
            Object[] message = {
                "Username (sin números):", txtUser,
                "Nombre Completo (sin números):", txtName,
                "Rol Scrum:", cbRole
            };
            
            int option = JOptionPane.showConfirmDialog(this, message, "Nuevo Usuario", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                if (!txtUser.getText().trim().isEmpty() && !txtName.getText().trim().isEmpty()) {
                    TypePerson selectedRole = (TypeRoleSelected(cbRole));
                    taskManager.addPerson(new Person(txtUser.getText().trim(), txtName.getText().trim(), selectedRole));
                    refreshPersonTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Nombre y Username son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        return panel;
    }
    
    private TypePerson TypeRoleSelected(JComboBox<TypePerson> cbRole) {
        return (TypePerson) cbRole.getSelectedItem();
    }
    
    private void refreshPersonTable() {
        personTableModel.setRowCount(0);
        for (Person p : taskManager.getPersons()) {
            String roleName = p.getTypePerson() != null ? p.getTypePerson().getName() : "Sin Rol";
            personTableModel.addRow(new Object[]{p.getId(), p.getUsername(), p.getName(), roleName});
        }
    }
    
    private JPanel createTaskPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JButton btnAddTask = new JButton("➕ Crear Tarea");
        btnAddTask.setBackground(new Color(92, 184, 92));
        btnAddTask.setForeground(Color.WHITE);
        
        JButton btnChangeStatus = new JButton("🔄 Cambiar Estado");
        topPanel.add(btnAddTask);
        topPanel.add(btnChangeStatus);
        panel.add(topPanel, BorderLayout.NORTH);
        
        String[] columns = {"ID", "Título", "Descripción", "Prioridad", "Usuario Asignado", "Estado"};
        taskTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable taskTable = new JTable(taskTableModel);
        styleTable(taskTable);
        panel.add(new JScrollPane(taskTable), BorderLayout.CENTER);
        
        refreshTaskTable();
        
        btnAddTask.addActionListener(e -> {
            List<Person> persons = taskManager.getPersons();
            if (persons.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe crear al menos un usuario primero.");
                return;
            }
            
            JTextField txtTitle = new JTextField();
            JTextField txtDesc = new JTextField();
            
            JComboBox<Priority> cbPriority = new JComboBox<>(Priority.values());
            JComboBox<Person> cbPerson = new JComboBox<>(persons.toArray(new Person[0]));
            
            Object[] message = {
                "Título:", txtTitle,
                "Descripción:", txtDesc,
                "Prioridad:", cbPriority,
                "Asignar a:", cbPerson
            };
            
            int option = JOptionPane.showConfirmDialog(this, message, "Nueva Tarea", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                if (!txtTitle.getText().trim().isEmpty()) {
                    Task task = new Task(
                        txtTitle.getText().trim(),
                        txtDesc.getText().trim(),
                        (Priority) cbPriority.getSelectedItem(),
                        (Person) cbPerson.getSelectedItem()
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
                    taskManager.updateTaskStatus(task);
                    refreshTaskTable();
                }
            }
        });
        
        return panel;
    }
    
    private void refreshTaskTable() {
        taskTableModel.setRowCount(0);
        for (Task t : taskManager.getTasks()) {
            String assigned = t.getAssignedPerson() != null ? t.getAssignedPerson().getName() : "N/A";
            taskTableModel.addRow(new Object[]{
                t.getId(), t.getTitle(), t.getDescription(), t.getPriority(), 
                assigned, t.getStatus().getDescription()
            });
        }
    }
    
    private JPanel createKanbanPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 0)); 
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        kanbanTodoPanel = createKanbanColumn("📌 POR REALIZAR", new Color(220, 235, 252));
        kanbanInProgressPanel = createKanbanColumn("⏳ EN PROCESO", new Color(252, 243, 207));
        kanbanDonePanel = createKanbanColumn("✅ FINALIZADO", new Color(212, 239, 223));
        
        panel.add(kanbanTodoPanel);
        panel.add(kanbanInProgressPanel);
        panel.add(kanbanDonePanel);
        
        return panel;
    }
    
    private JPanel createKanbanColumn(String title, Color bgColor) {
        JPanel colPanel = new JPanel(new BorderLayout(0, 10));
        colPanel.setBackground(bgColor);
        
        // Header de la columna
        JLabel lblHeader = new JLabel(title, SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHeader.setOpaque(true);
        lblHeader.setBackground(bgColor.darker());
        lblHeader.setForeground(new Color(50, 50, 50));
        lblHeader.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        colPanel.add(lblHeader, BorderLayout.NORTH);
        
        // Contenido de la columna
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS)); 
        contentPanel.setBackground(bgColor);
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); 
        scrollPane.setBorder(null);
        scrollPane.setBackground(bgColor);
        
        colPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Bordes redondeados para la columna
        colPanel.setBorder(BorderFactory.createLineBorder(bgColor.darker(), 1, true));
        
        return colPanel;
    }
    
    private void refreshKanbanBoard() {
        fillKanbanColumn(kanbanTodoPanel, Status.POR_REALIZAR);
        fillKanbanColumn(kanbanInProgressPanel, Status.EN_PROCESO);
        fillKanbanColumn(kanbanDonePanel, Status.FINALIZADO);
    }
    
    private void fillKanbanColumn(JPanel columnPanel, Status status) {
        JScrollPane scrollPane = (JScrollPane) columnPanel.getComponent(1);
        JPanel contentPanel = (JPanel) scrollPane.getViewport().getView();
        
        contentPanel.removeAll();
        
        List<Task> tasks = taskManager.getTasksByStatus(status);
        for (Task t : tasks) {
            JPanel card = new JPanel(new BorderLayout(5, 5));
            
            // Color según prioridad
            Color priorityColor;
            switch(t.getPriority()) {
                case ALTA: priorityColor = new Color(255, 100, 100); break;
                case MEDIA: priorityColor = new Color(255, 180, 50); break;
                case BAJA: priorityColor = new Color(100, 200, 100); break;
                default: priorityColor = Color.LIGHT_GRAY;
            }
            
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, priorityColor),
                new EmptyBorder(12, 12, 12, 12)
            ));
            card.setBackground(Color.WHITE);
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
            
            JLabel lblTitle = new JLabel("<html><b style='font-size:11px;'>" + t.getTitle() + "</b></html>");
            String personName = t.getAssignedPerson() != null ? t.getAssignedPerson().getName() : "N/A";
            
            JLabel lblPerson = new JLabel("👤 " + personName);
            lblPerson.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblPerson.setForeground(Color.GRAY);
            
            JLabel lblPriority = new JLabel(t.getPriority().name());
            lblPriority.setFont(new Font("Segoe UI", Font.BOLD, 10));
            lblPriority.setForeground(priorityColor.darker());
            
            JPanel southPanel = new JPanel(new BorderLayout());
            southPanel.setBackground(Color.WHITE);
            southPanel.add(lblPerson, BorderLayout.WEST);
            southPanel.add(lblPriority, BorderLayout.EAST);
            
            card.add(lblTitle, BorderLayout.NORTH);
            card.add(southPanel, BorderLayout.SOUTH);
            
            contentPanel.add(card);
            contentPanel.add(Box.createRigidArea(new Dimension(0, 15))); 
        }
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private JPanel createMyTasksPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topPanel.add(new JLabel("Seleccionar Usuario:"));
        
        JComboBox<Person> cbPerson = new JComboBox<>();
        cbPerson.setPreferredSize(new Dimension(250, 30));
        JButton btnView = new JButton("👁 Ver Tareas");
        btnView.setBackground(new Color(91, 192, 222));
        btnView.setForeground(Color.WHITE);
        
        topPanel.add(cbPerson);
        topPanel.add(btnView);
        panel.add(topPanel, BorderLayout.NORTH);
        
        JTextArea txtArea = new JTextArea();
        txtArea.setEditable(false);
        txtArea.setFont(new Font("Monospaced", Font.PLAIN, 15));
        txtArea.setMargin(new Insets(15, 15, 15, 15));
        txtArea.setBackground(new Color(250, 250, 250));
        
        panel.add(new JScrollPane(txtArea), BorderLayout.CENTER);
        
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedComponent() == panel) {
                Person selected = (Person) cbPerson.getSelectedItem();
                cbPerson.removeAllItems();
                for (Person p : taskManager.getPersons()) {
                    cbPerson.addItem(p);
                }
                if (selected != null) {
                    cbPerson.setSelectedItem(selected);
                }
            }
        });
        
        btnView.addActionListener(e -> {
            Person selected = (Person) cbPerson.getSelectedItem();
            if (selected != null) {
                List<Task> userTasks = taskManager.getTasksByUserOrderedByPriority(selected);
                StringBuilder sb = new StringBuilder();
                if (userTasks.isEmpty()) {
                    sb.append("No hay tareas asignadas para este usuario.\n");
                } else {
                    sb.append("📅 Tareas de ").append(selected.getName()).append(":\n");
                    sb.append("==================================================\n\n");
                    for (Task t : userTasks) {
                        sb.append("▪ ").append(t.getTitle())
                          .append("\n  Prioridad: ").append(t.getPriority())
                          .append(" | Estado: ").append(t.getStatus())
                          .append("\n  Detalle: ").append(t.getDescription())
                          .append("\n\n"); 
                    }
                }
                txtArea.setText(sb.toString());
            }
        });
        
        return panel;
    }

    public static void main(String[] args) {
        try {
            // Activar FlatLaf para interfaz moderna
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new App().setVisible(true));
    }
}
