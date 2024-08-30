import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Date;

public class TaskManagerGUI {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/task_manager";
    private static final String USER = "root";
    private static final String PASSWORD = "12345678!";

    private static JTextArea outputArea = new JTextArea(15, 50);
    private static JTextField idField = new JTextField(5);
    private static JTextField titleField = new JTextField(20);
    private static JTextField descriptionField = new JTextField(20);
    private static JTextField statusField = new JTextField(10);

    public static void main(String[] args) {
        JFrame frame = new JFrame("Gerenciador de Tarefas");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(5, 2, 10, 10));

        inputPanel.add(new JLabel("ID:"));
        inputPanel.add(idField);

        inputPanel.add(new JLabel("Título:"));
        inputPanel.add(titleField);

        inputPanel.add(new JLabel("Descrição:"));
        inputPanel.add(descriptionField);

        inputPanel.add(new JLabel("Status:"));
        inputPanel.add(statusField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 4, 10, 10));

        JButton createButton = new JButton("Criar tarefa");
        JButton readButton = new JButton("Visualizar tarefa");
        JButton editButton = new JButton("Editar tarefa");
        JButton deleteButton = new JButton("Deletar tarefa");

        buttonPanel.add(createButton);
        buttonPanel.add(readButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);

        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Saída"));

        panel.add(scrollPane, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setVisible(true);

        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int id = Integer.parseInt(idField.getText());
                    String title = titleField.getText();
                    String description = descriptionField.getText();
                    String status = statusField.getText();
                    Date date = new Date();

                    Task task = new Task(id, title, description, status, date);
                    addTaskToDatabase(task);
                    outputArea.append("Task criada: " + task + "\n");
                } catch (NumberFormatException ex) {
                    outputArea.append("Formato de ID inválido.\n");
                } catch (SQLException ex) {
                    outputArea.append("Erro ao conectar ao banco de dados.\n");
                }
            }
        });

        readButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int id = Integer.parseInt(idField.getText());
                    Task task = getTaskFromDatabase(id);
                    outputArea.append(task != null ? task.toString() + "\n" : "Tarefa não encontrada.\n");
                } catch (NumberFormatException ex) {
                    outputArea.append("Formato de ID inválido.\n");
                } catch (SQLException ex) {
                    outputArea.append("Erro ao conectar ao banco de dados.\n");
                }
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int id = Integer.parseInt(idField.getText());
                    String title = titleField.getText();
                    String description = descriptionField.getText();
                    String status = statusField.getText();

                    updateTaskInDatabase(id, title, description, status);
                    outputArea.append("Task atualizada.\n");
                } catch (NumberFormatException ex) {
                    outputArea.append("Formato de ID inválido.\n");
                } catch (SQLException ex) {
                    outputArea.append("Erro ao conectar ao banco de dados.\n");
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int id = Integer.parseInt(idField.getText());
                    deleteTaskFromDatabase(id);
                    outputArea.append("Task deletada.\n");
                } catch (NumberFormatException ex) {
                    outputArea.append("Formato de ID inválido.\n");
                } catch (SQLException ex) {
                    outputArea.append("Erro ao conectar ao banco de dados.\n");
                }
            }
        });
    }

    private static void addTaskToDatabase(Task task) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO tasks (id, title, description, status, date) VALUES (?, ?, ?, ?, ?)")) {

            stmt.setInt(1, task.getId());
            stmt.setString(2, task.getTitle());
            stmt.setString(3, task.getDescription());
            stmt.setString(4, task.getStatus());
            stmt.setTimestamp(5, new Timestamp(task.getDate().getTime()));

            stmt.executeUpdate();
        }
    }

    private static Task getTaskFromDatabase(int id) throws SQLException {
        Task task = null;

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tasks WHERE id = ?")) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                task = new Task(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("status"),
                        rs.getTimestamp("date"));
            }
        }
        return task;
    }

    private static void updateTaskInDatabase(int id, String title, String description, String status)
            throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                PreparedStatement stmt = conn
                        .prepareStatement("UPDATE tasks SET title = ?, description = ?, status = ? WHERE id = ?")) {

            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setString(3, status);
            stmt.setInt(4, id);

            stmt.executeUpdate();
        }
    }

    private static void deleteTaskFromDatabase(int id) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM tasks WHERE id = ?")) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}