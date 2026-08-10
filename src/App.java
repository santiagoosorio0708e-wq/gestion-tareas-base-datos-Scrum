import model.Priority;
import model.Status;
import model.Task;
import model.User;
import service.TaskManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * Clase principal de la aplicación que extiende JFrame para proveer una Interfaz Gráfica de Usuario (GUI).
 * Esta clase maneja toda la vista de la aplicación, interactuando con el TaskManager.
 */
public class App extends JFrame {
    // Instancia del manejador de tareas que contiene la lógica de negocio
    private TaskManager taskManager = new TaskManager();
    
    // Contenedor principal de pestañas para la navegación entre módulos
    private JTabbedPane tabbedPane;
    
    // Modelos de tabla para actualizar los datos dinámicamente en las vistas
    private DefaultTableModel userTableModel;
    private DefaultTableModel taskTableModel;
    
    // Paneles que representan las columnas del Tablero Kanban
    private JPanel kanbanTodoPanel;
    private JPanel kanbanInProgressPanel;
    private JPanel kanbanDonePanel;
    
    /**
     * Constructor de la aplicación. Configura la ventana principal y sus componentes.
     */
    public App() {
        setTitle("Gestor de Tareas (Jira/Trello Style)");
        
        // DISEÑO: Define las dimensiones iniciales de la ventana en píxeles (ancho, alto).
        setSize(900, 600); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // DISEÑO: Centra la ventana exactamente en el medio de la pantalla al abrirla.
        setLocationRelativeTo(null); 
        
        // Cargar datos de prueba por defecto para visualizar la app
        loadTestData();
        
        // Inicializar el contenedor de pestañas
        tabbedPane = new JTabbedPane();
        
        // Añadir cada panel como una pestaña independiente
        tabbedPane.addTab("Usuarios", createUserPanel());
        tabbedPane.addTab("Tareas", createTaskPanel());
        tabbedPane.addTab("Tablero Kanban", createKanbanPanel());
        tabbedPane.addTab("Mis Tareas", createMyTasksPanel());
        
        // Listener para refrescar el Kanban y otras vistas automáticamente al cambiar de pestaña
        tabbedPane.addChangeListener(e -> {
            refreshKanbanBoard();
        });
        
        // Agregar el contenedor de pestañas a la ventana principal
        add(tabbedPane);
    }
    
    /**
     * Método auxiliar para restringir el ingreso de números en campos de texto.
     * @param textField El campo de texto al que se le aplicará la restricción.
     */
    private void preventNumbers(JTextField textField) {
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                // Si el carácter ingresado es un número, consumimos el evento (no se escribe)
                if (Character.isDigit(c)) {
                    e.consume();
                }
            }
        });
    }
    
    /**
     * Carga usuarios y tareas de prueba para la demostración.
     */
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
    
    /**
     * Crea el panel correspondiente a la gestión de usuarios.
     * Incluye una tabla para listarlos y un botón para crear nuevos.
     */
    private JPanel createUserPanel() {
        // DISEÑO: BorderLayout divide la pantalla en 5 zonas (Norte, Sur, Centro, Este, Oeste).
        // Usamos esto para poner la barra de botones fija arriba (NORTH) y que la tabla ocupe todo el resto (CENTER).
        JPanel panel = new JPanel(new BorderLayout());
        
        // DISEÑO: FlowLayout posiciona los elementos en línea, uno tras otro. 
        // El parámetro LEFT hace que los botones se peguen al lado izquierdo en lugar de centrarse.
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAddUser = new JButton("Crear Usuario");
        topPanel.add(btnAddUser);
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Configuración de la tabla de usuarios
        String[] columns = {"Username", "Nombre Completo"};
        userTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; } // Celdas no editables directamente
        };
        JTable userTable = new JTable(userTableModel);
        
        // DISEÑO: Aumentamos un poco la altura de cada fila de la tabla para que el texto respire y no se vea amontonado.
        userTable.setRowHeight(25);
        
        panel.add(new JScrollPane(userTable), BorderLayout.CENTER);
        
        refreshUserTable(); // Llenar la tabla inicialmente
        
        // Acción del botón para crear un nuevo usuario
        btnAddUser.addActionListener(e -> {
            JTextField txtUser = new JTextField();
            JTextField txtName = new JTextField();
            
            // Aplicar restricción para que el usuario NO pueda colocar números
            preventNumbers(txtUser);
            preventNumbers(txtName);
            
            Object[] message = {
                "Username (sin números):", txtUser,
                "Nombre Completo (sin números):", txtName
            };
            
            // Mostrar cuadro de diálogo para ingresar datos
            int option = JOptionPane.showConfirmDialog(this, message, "Nuevo Usuario", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                // Validar que los campos no estén vacíos
                if (!txtUser.getText().trim().isEmpty() && !txtName.getText().trim().isEmpty()) {
                    taskManager.addUser(new User(txtUser.getText().trim(), txtName.getText().trim()));
                    refreshUserTable(); // Refrescar la tabla
                } else {
                    JOptionPane.showMessageDialog(this, "Ambos campos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        return panel;
    }
    
    /**
     * Refresca el modelo de la tabla de usuarios con los datos actuales del TaskManager.
     */
    private void refreshUserTable() {
        userTableModel.setRowCount(0); // Limpiar tabla
        for (User u : taskManager.getUsers()) {
            userTableModel.addRow(new Object[]{u.getUsername(), u.getName()});
        }
    }
    
    /**
     * Crea el panel correspondiente a la gestión de tareas.
     */
    private JPanel createTaskPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAddTask = new JButton("Crear Tarea");
        JButton btnChangeStatus = new JButton("Cambiar Estado");
        topPanel.add(btnAddTask);
        topPanel.add(btnChangeStatus);
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Configuración de la tabla de tareas
        String[] columns = {"ID", "Título", "Descripción", "Prioridad", "Usuario", "Estado"};
        taskTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable taskTable = new JTable(taskTableModel);
        
        // DISEÑO: Ajustamos altura de las filas para mejorar la lectura.
        taskTable.setRowHeight(25);
        panel.add(new JScrollPane(taskTable), BorderLayout.CENTER);
        
        refreshTaskTable();
        
        // Acción para crear nueva tarea
        btnAddTask.addActionListener(e -> {
            if (taskManager.getUsers().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe crear al menos un usuario primero.");
                return;
            }
            
            JTextField txtTitle = new JTextField();
            JTextField txtDesc = new JTextField();
            
            // Aplicar restricción para que el usuario NO pueda colocar números en título y descripción
            preventNumbers(txtTitle);
            preventNumbers(txtDesc);
            
            JComboBox<Priority> cbPriority = new JComboBox<>(Priority.values());
            JComboBox<User> cbUser = new JComboBox<>(taskManager.getUsers().toArray(new User[0]));
            
            Object[] message = {
                "Título (sin números):", txtTitle,
                "Descripción (sin números):", txtDesc,
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
        
        // Acción para cambiar el estado de una tarea existente
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
                    refreshTaskTable(); // Actualizar tabla al cambiar estado
                }
            }
        });
        
        return panel;
    }
    
    /**
     * Refresca la tabla de tareas con los últimos datos.
     */
    private void refreshTaskTable() {
        taskTableModel.setRowCount(0);
        for (Task t : taskManager.getTasks()) {
            taskTableModel.addRow(new Object[]{
                t.getId(), t.getTitle(), t.getDescription(), t.getPriority(), 
                t.getAssignedUser().getUsername(), t.getStatus().getDescription()
            });
        }
    }
    
    /**
     * Crea el Tablero Kanban, que es una vista de 3 columnas para organizar visualmente las tareas por estado.
     */
    private JPanel createKanbanPanel() {
        // DISEÑO: GridLayout crea una cuadrícula perfecta. Aquí indicamos: 1 fila, 3 columnas.
        // Los dos "10" finales son los píxeles de separación (espacio vacío) horizontal y vertical entre columnas.
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 10)); 
        
        // DISEÑO: Añade un margen invisible (padding) alrededor de las tres columnas, para que no toquen los bordes de la pantalla.
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Inicializar cada columna
        kanbanTodoPanel = createKanbanColumn("POR REALIZAR");
        kanbanInProgressPanel = createKanbanColumn("EN PROCESO");
        kanbanDonePanel = createKanbanColumn("FINALIZADO");
        
        panel.add(kanbanTodoPanel);
        panel.add(kanbanInProgressPanel);
        panel.add(kanbanDonePanel);
        
        return panel;
    }
    
    /**
     * Helper para crear una columna individual del Tablero Kanban.
     */
    private JPanel createKanbanColumn(String title) {
        JPanel colPanel = new JPanel(new BorderLayout());
        
        // DISEÑO: Dibuja un rectángulo gris alrededor del panel y le incrusta el título en la línea superior (como una caja etiquetada).
        colPanel.setBorder(BorderFactory.createTitledBorder(title)); 
        
        JPanel contentPanel = new JPanel();
        
        // DISEÑO: BoxLayout organiza los elementos de forma estricta. Y_AXIS obliga a que todas las tarjetas se apilen verticalmente, una debajo de otra.
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS)); 
        
        // DISEÑO: Fondo blanco para que haya un contraste claro con las tarjetas de tareas grises.
        contentPanel.setBackground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        
        // DISEÑO: Aumenta la cantidad de píxeles que se mueven al usar la rueda del ratón, haciendo el "scroll" mucho más rápido y natural.
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); 
        
        colPanel.add(scrollPane, BorderLayout.CENTER);
        
        return colPanel;
    }
    
    /**
     * Refresca todas las columnas del Tablero Kanban.
     */
    private void refreshKanbanBoard() {
        fillKanbanColumn(kanbanTodoPanel, Status.POR_REALIZAR);
        fillKanbanColumn(kanbanInProgressPanel, Status.EN_PROCESO);
        fillKanbanColumn(kanbanDonePanel, Status.FINALIZADO);
    }
    
    /**
     * Llena una columna específica del Kanban con tarjetas representando las tareas en ese estado.
     */
    private void fillKanbanColumn(JPanel columnPanel, Status status) {
        // Obtenemos el content panel dentro del scroll pane de la columna
        JScrollPane scrollPane = (JScrollPane) columnPanel.getComponent(0);
        JPanel contentPanel = (JPanel) scrollPane.getViewport().getView();
        
        contentPanel.removeAll(); // Limpiar tarjetas actuales
        
        List<Task> tasks = taskManager.getTasksByStatus(status);
        for (Task t : tasks) {
            // Crear una "tarjeta" visual para cada tarea
            JPanel card = new JPanel(new BorderLayout());
            
            // DISEÑO: CompoundBorder mezcla dos bordes. Por fuera dibujamos una línea gris fina (LineBorder),
            // y por dentro dejamos un margen vacío de 10px (EmptyBorder) para que las letras no se peguen a la línea gris.
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            
            // DISEÑO: Le damos un color RGB (Rojo, Verde, Azul) personalizado para que parezca un gris/azulado muy tenue y elegante.
            card.setBackground(new Color(245, 245, 250));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
            
            JLabel lblTitle = new JLabel("<html><b>" + t.getTitle() + "</b></html>");
            JLabel lblDetails = new JLabel(t.getAssignedUser().getUsername() + " | Prioridad: " + t.getPriority());
            
            card.add(lblTitle, BorderLayout.NORTH);
            card.add(lblDetails, BorderLayout.SOUTH);
            
            contentPanel.add(card);
            
            // DISEÑO: RigidArea funciona como un "ladrillo transparente". Lo ponemos debajo de cada tarjeta para forzar un espacio en blanco de 10 píxeles entre ellas.
            contentPanel.add(Box.createRigidArea(new Dimension(0, 10))); 
        }
        
        // Actualizar la interfaz
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Crea el panel de "Mis Tareas" donde se pueden filtrar y ver las tareas por usuario.
     */
    private JPanel createMyTasksPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Seleccionar Usuario:"));
        
        JComboBox<User> cbUser = new JComboBox<>();
        JButton btnView = new JButton("Ver Tareas");
        
        topPanel.add(cbUser);
        topPanel.add(btnView);
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Área de texto para mostrar las tareas formateadas
        JTextArea txtArea = new JTextArea();
        txtArea.setEditable(false);
        
        // DISEÑO: "Monospaced" es una tipografía tipo máquina de escribir donde cada letra mide exactamente lo mismo. 
        // Esto es ideal para que las listas y tablas de texto plano se alineen perfectamente sin torcerse.
        txtArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        // DISEÑO: Agrega margen interior (padding) dentro de la caja de texto.
        txtArea.setMargin(new Insets(10, 10, 10, 10));
        
        panel.add(new JScrollPane(txtArea), BorderLayout.CENTER);
        
        // Listener para refrescar la lista desplegable (combo box) al seleccionar esta pestaña
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedComponent() == panel) {
                User selected = (User) cbUser.getSelectedItem();
                cbUser.removeAllItems(); // Recargar usuarios por si se agregó alguno nuevo
                for (User u : taskManager.getUsers()) {
                    cbUser.addItem(u);
                }
                if (selected != null) {
                    cbUser.setSelectedItem(selected);
                }
            }
        });
        
        // Acción para visualizar las tareas del usuario seleccionado
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
                        sb.append(t.toString()).append("\n"); // Muestra cada tarea ordenada
                    }
                }
                txtArea.setText(sb.toString());
            }
        });
        
        return panel;
    }

    /**
     * Punto de entrada principal a la aplicación.
     */
    public static void main(String[] args) {
        try {
            // DISEÑO: El "Look And Feel" le dice a la app que no use los colores y botones antiguos de Java por defecto (Metal), 
            // sino que intente imitar el aspecto visual del sistema operativo donde se ejecuta (botones estilo Windows si estás en Windows).
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Iniciar la GUI en el hilo de eventos (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            new App().setVisible(true);
        });
    }
}
