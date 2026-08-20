package service;

import model.Person;
import model.Priority;
import model.Status;
import model.Task;
import model.TypePerson;
import model.Team;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskManager {

    public TaskManager() {
        // Inicializa la base de datos si no existe
        DatabaseConnection.initializeDatabase();
    }

    public List<TypePerson> getTypePersons() {
        List<TypePerson> types = new ArrayList<>();
        String sql = "SELECT * FROM type_person";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                types.add(new TypePerson(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return types;
    }

    public List<Team> getTeams() {
        List<Team> teams = new ArrayList<>();
        String sql = "SELECT * FROM team";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                teams.add(new Team(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return teams;
    }
    
    public void addTeam(Team team) {
        String sql = "INSERT INTO team(name) VALUES(?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, team.getName());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addPerson(Person person) {
        String sql = "INSERT INTO person(username, name, id_type_person) VALUES(?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, person.getUsername());
            pstmt.setString(2, person.getName());
            if (person.getTypePerson() != null) {
                pstmt.setInt(3, person.getTypePerson().getId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Person> getPersons() {
        List<Person> persons = new ArrayList<>();
        String sql = "SELECT p.id, p.username, p.name, t.id as type_id, t.name as type_name " +
                     "FROM person p " +
                     "LEFT JOIN type_person t ON p.id_type_person = t.id";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                TypePerson type = null;
                if (rs.getInt("type_id") != 0) {
                    type = new TypePerson(rs.getInt("type_id"), rs.getString("type_name"));
                }
                persons.add(new Person(rs.getInt("id"), rs.getString("username"), rs.getString("name"), type));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return persons;
    }

    public void addTask(Task task) {
        String sqlTask = "INSERT INTO task(title, description, priority, id_status_task) VALUES(?,?,?,?)";
        String sqlAssesment = "INSERT INTO assement_task(id_task, id_person) VALUES(?,?)";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Transacción
            try (PreparedStatement pstmtTask = conn.prepareStatement(sqlTask, Statement.RETURN_GENERATED_KEYS)) {
                pstmtTask.setString(1, task.getTitle());
                pstmtTask.setString(2, task.getDescription());
                pstmtTask.setString(3, task.getPriority().name());
                // Por defecto, nueva tarea es POR_REALIZAR (id = 1, asumiendo inserción inicial en schema)
                pstmtTask.setInt(4, 1); 
                pstmtTask.executeUpdate();
                
                ResultSet rs = pstmtTask.getGeneratedKeys();
                int taskId = -1;
                if (rs.next()) {
                    taskId = rs.getInt(1);
                }
                
                if (task.getAssignedPerson() != null && taskId != -1) {
                    try (PreparedStatement pstmtAss = conn.prepareStatement(sqlAssesment)) {
                        pstmtAss.setInt(1, taskId);
                        pstmtAss.setInt(2, task.getAssignedPerson().getId());
                        pstmtAss.executeUpdate();
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT t.id, t.title, t.description, t.priority, s.name as status_name, " +
                     "p.id as p_id, p.username, p.name as p_name, tp.id as tp_id, tp.name as tp_name " +
                     "FROM task t " +
                     "JOIN status_task s ON t.id_status_task = s.id " +
                     "LEFT JOIN assement_task a ON t.id = a.id_task " +
                     "LEFT JOIN person p ON a.id_person = p.id " +
                     "LEFT JOIN type_person tp ON p.id_type_person = tp.id";
                     
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Person person = null;
                if (rs.getInt("p_id") != 0) {
                    TypePerson tp = null;
                    if (rs.getInt("tp_id") != 0) {
                        tp = new TypePerson(rs.getInt("tp_id"), rs.getString("tp_name"));
                    }
                    person = new Person(rs.getInt("p_id"), rs.getString("username"), rs.getString("p_name"), tp);
                }
                
                Status status = Status.valueOf(rs.getString("status_name"));
                Priority priority = Priority.valueOf(rs.getString("priority"));
                
                tasks.add(new Task(rs.getInt("id"), rs.getString("title"), rs.getString("description"), priority, status, person));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public Task getTaskById(int id) {
        return getTasks().stream().filter(t -> t.getId() == id).findFirst().orElse(null);
    }
    
    public void updateTaskStatus(Task task) {
        String sql = "UPDATE task SET id_status_task = (SELECT id FROM status_task WHERE name = ?) WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, task.getStatus().name());
            pstmt.setInt(2, task.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Task> getTasksByStatus(Status status) {
        return getTasks().stream()
                .filter(task -> task.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<Task> getTasksByUserOrderedByPriority(Person person) {
        return getTasks().stream()
                .filter(task -> task.getAssignedPerson() != null && task.getAssignedPerson().getId() == person.getId())
                .sorted((t1, t2) -> t1.getPriority().compareTo(t2.getPriority()))
                .collect(Collectors.toList());
    }
}
