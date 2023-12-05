package com.company;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.*;


public class MedcineDatabase {
    private Connection connection;

    public MedcineDatabase(String url) {
        openConnection(url);
    }

    private void openConnection(String url) {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(url);
            System.out.println("CONNECT");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("NOT CONNECTED");
        }
    }

    public int getColumnCount(String table) {
        int columnCount = 0;
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet columnsResultSet = metaData.getColumns(null, null, table, null);
            while (columnsResultSet.next()) {
                columnCount++;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return columnCount;
    }

    public void insertAny(String table, List<String> values) {
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet columnsResultSet = metaData.getColumns(null, null, table, null);

            List<String> columns = new ArrayList<>();
            while (columnsResultSet.next()) {
                columns.add(columnsResultSet.getString("COLUMN_NAME"));
            }

            if (columns.size() != values.size()) {
                throw new IllegalArgumentException("Количество столбцов и значений должно совпадать");
            }

            String joinedColumns = String.join(", ", columns);
            String placeholders = String.join(", ", Collections.nCopies(values.size(), "?"));

            String query = "INSERT INTO " + table + " (" + joinedColumns + ") VALUES (" + placeholders + ")";

            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                for (int i = 0; i < values.size(); i++) {
                    pstmt.setString(i + 1, values.get(i));
                }
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insert(String name, String symp, String treat, String prevention) {
        try {
            if (connection != null) {
                String state = "INSERT INTO Illnesses (name, symptoms, treatment, prevention) VALUES (?,?,?,?)";
                // Scanner scanner = new Scanner(System.in);
                System.out.println("Введите название болезни");
                // String name = scanner.nextLine();
                System.out.println("\nВведите симптомы");
                // String symp = scanner.nextLine();
                System.out.println("\nВведите симптомы");
                // String treat = scanner.nextLine();
                System.out.println("\nВведите профилактику от заболевания");
                // String prevention = scanner.nextLine();
                PreparedStatement prstm = connection.prepareStatement(state);
                prstm.setString(1, name);
                prstm.setString(2, symp);
                prstm.setString(3, treat);
                prstm.setString(4, prevention);
                prstm.executeUpdate();

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public String tables() {
        String tableName = "";
        try {
            if (connection != null) {
                ResultSet rs = connection.getMetaData().getTables(null, null, null, null);
                while (rs.next()) {
                    tableName = rs.getString("TABLE_NAME");
                    //  System.out.println(rs.getString("TABLE_NAME"));
                }

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        if (Objects.equals(tableName, "")) {
            System.out.println("Таблицы отсутсвуют");
        }
        return tableName;
    }

    public List<String> search(String table, String column, String keyword) {
        List<String> results = new ArrayList<>();
        String query = "SELECT * FROM " + table + " WHERE " + column + " LIKE ?";
        try (
                PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String record = "ID: " + rs.getString("ID") + "\n" + "Название заболевания: " + rs.getString("name") + "\n" + "Симптомы: " + rs.getString("symptoms") + "\n" + "Лечение: " + rs.getString("treatment") + "\n" + "Профилактика: " + rs.getString("prevention");
                results.add(record);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return results;
    }

    public String fileWritter(String table) {
        String result = "";
        try {
            File myFile = new File("filename.txt");
            // Если файл не существует, он будет создан
            if (myFile.createNewFile()) {
                System.out.println("Файл создан: " + myFile.getName());
            } else {
                System.out.println("Файл уже существует.");
            }

            // Создание объекта FileWriter
            FileWriter myWriter = new FileWriter("filename.txt");

            // Запись данных в файл
            for (String str : this.getTable(table)) {
                myWriter.write(str + System.lineSeparator());
            }
            // myWriter.write();
            myWriter.close();
            System.out.println("Успешно записано в файл.");
            String path = myFile.getAbsolutePath();
            System.out.println("Путь к файлу: " + path);
            result = "Успешно записано в файл. ";
            result += "Путь к файлу: " + path;
        } catch (IOException e) {
            System.out.println("Произошла ошибка.");
            result = "Произошла ошибка.";
            e.printStackTrace();
        }
        return result;
    }

    public List<String> getTable(String table) {
        List<String> results = new ArrayList<>();
        String query = "SELECT * FROM " + table;
        try (
                PreparedStatement pstmt = connection.prepareStatement(query)) {
            // pstmt.setString(1,query);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String record = "ID: " + rs.getString("ID") + "\n" + "Название заболевания: " + rs.getString("name") + "\n" + "Симптомы: " + rs.getString("symptoms") + "\n" + "Лечение: " + rs.getString("treatment") + "\n" + "Профилактика: " + rs.getString("prevention");
                results.add(record);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return results;
    }

    public void close() {
        try {
            this.connection.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}//end of class



