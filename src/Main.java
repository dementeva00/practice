// Показатели спортивных команд
// Задача 1. Постройте график по среднему возрасту во всех командах.
// Задача 2. Найдите команду с самым высоким средним ростом. Выведите в консоль
// 5 самых высоких игроков команды.
// Задача 3. Найдите команду, с средним ростом равным от 74 до 78 inches и
// средним весом от 190 до 210 lbs, с самым высоким средним возрастом.

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import javax.swing.*;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.*;
import java.util.*;

public class Main {
    public static List<Sportsman> SportsmanList;

    public static void main(String[] args) {
        // Достаем данные из файла
        SportsmanList = readCSVFile("Показатели спортивных команд.csv");

        // Создаем и заполняем базу данных
        //createSQLite();

        // Задание 1
        buildAGraph();

        // Задание 2
        //printAverageGrantSize();

        // Задание 3
        //printBusinessType();
    }

    // Достаем данные из csv файла
    public static List<Sportsman> readCSVFile(String fileName) {
        List<Sportsman> sportsmanList = new ArrayList<>();
        try {
            Reader reader = new FileReader(fileName);
            com.opencsv.CSVReader csvReader = new CSVReader(reader);
            List<String[]> records = csvReader.readAll();
            int count = 0;
            for (String[] record : records) {
                if (count == 0) {
                    count ++;
                    continue;
                }
                if (Objects.equals(record[3], "")) {
                    break;
                }
                Sportsman sportsman = new Sportsman(record[0],
                        record[1].replaceAll("\"", ""),
                        record[2].replaceAll("\"", ""),
                        Integer.parseInt(record[3]),
                        Integer.parseInt(record[4]),
                        Double.parseDouble(record[5]));
                sportsmanList.add(sportsman);
            }
        } catch (IOException | CsvException e) {
            throw new RuntimeException(e);
        }
        return sportsmanList;
    }

    //Создаем и заполняем базу данных
    public static void createSQLite() {
        Connection connection = null;
        try {
            // подключение к базе данных
            connection = DriverManager.getConnection("jdbc:sqlite:Sportsman.db");
            Statement statement = connection.createStatement();

            // создание таблиц
            statement.execute("CREATE TABLE IF NOT EXISTS Sportsman " +
                    "(id_sportsman INTEGER PRIMARY KEY, name TEXT, " +
                    "height INTEGER, weight INTEGER," +
                    "age DOUBLE, id_team INTEGER," +
                    "id_position INTEGER)");
            statement.execute("CREATE TABLE IF NOT EXISTS Team " +
                    "(id_team INTEGER, team TEXT)");
            statement.execute("CREATE TABLE IF NOT EXISTS Position " +
                    "(id_position INTEGER, position TEXT)");

            // запросы для наполнения таблиц
            String request1 = "INSERT INTO 'Sportsman'" +
                    "('name', 'height', 'weight', 'age', 'id_team', 'id_position') " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            String request2 = "INSERT INTO 'Team'" +
                    "('id_team', 'team') " +
                    "VALUES (?, ?)";
            String request3 = "INSERT INTO 'Position'" +
                    "('id_position', 'position') " +
                    "VALUES (?, ?)";
            Map<Integer, String> mapForTeam = new HashMap<>();
            int counterForTeam = 0;
            Map<Integer, String> mapForPos = new HashMap<>();
            int counterForPos = 0;
            int currentPos;


            // наполняем таблицy Sportsman
            for (Sportsman arg: SportsmanList) {
                if (!mapForTeam.containsValue(arg.getTeam())) {
                    counterForTeam++;
                    mapForTeam.put(counterForTeam, arg.getTeam());
                }
                if (!mapForPos.containsValue(arg.getPosition())) {
                    counterForPos ++;
                    mapForPos.put(counterForPos, arg.getPosition());
                    currentPos = counterForPos;
                }
                else {
                    currentPos = getKey(mapForPos, arg.getPosition());
                }
                PreparedStatement pstmt = connection.prepareStatement(request1);
                pstmt.setString(1, arg.getName());
                pstmt.setInt(2, arg.getHeight());
                pstmt.setInt(3, arg.getWeight());
                pstmt.setDouble(4, arg.getAge());
                pstmt.setInt(5, counterForTeam);
                pstmt.setInt(6, currentPos);
                pstmt.executeUpdate();
                pstmt.close();
            }

            // наполняем таблицy Team
            Map<Integer, String> sortedMapGrantSize = new TreeMap<>(mapForTeam);
            for (Map.Entry<Integer, String> entry : sortedMapGrantSize.entrySet()) {
                PreparedStatement pstmt = connection.prepareStatement(request2);
                pstmt.setInt(1, entry.getKey());
                pstmt.setString(2, entry.getValue());
                pstmt.executeUpdate();
                pstmt.close();
            }

            // наполняем таблицy Position
            Map<Integer, String> sortedMapStreetName = new TreeMap<>(mapForPos);
            for (Map.Entry<Integer, String> entry : sortedMapStreetName.entrySet()) {
                PreparedStatement pstmt4 = connection.prepareStatement(request3);
                pstmt4.setInt(1, entry.getKey());
                pstmt4.setString(2, entry.getValue());
                pstmt4.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (connection != null) {
                    connection.close(); // закрытие соединения
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static <K, V> K getKey(Map<K, V> map, V value)
    {
        return map.entrySet().stream()
                .filter(entry -> value.equals(entry.getValue()))
                .findFirst().map(Map.Entry::getKey)
                .orElse(null);
    }

    // Задача 1
    public static void buildAGraph() {
        // Строки запросов к базе данных
        String strRequest1 = "SELECT AVG(age) AS age " +
                "FROM Sportsman " +
                "INNER JOIN Team " +
                "ON Team.id_team = Sportsman.id_team " +
                "GROUP BY Team.team " +
                "ORDER BY Team.team;";
        String strRequest2 = "SELECT team " +
                "AS team FROM Team " +
                "GROUP BY team " +
                "ORDER BY team;";

        // Делаем запросы и получаем результат
        String age = connectionBD("age", strRequest1).strip();
        String team = connectionBD("team", strRequest2).strip();

        System.out.println(age);
        System.out.println(team);

        // Создаем график по полученным значениям
        Graph graph = new Graph("График", age, team);
        graph.setSize(800, 400);
        graph.setLocationRelativeTo(null);
        graph.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        graph.setVisible(true);
    }

    // Для подключения и работы с базой данных
    public static String connectionBD (String columnName, String strRequest) {
        Connection connection = null;
        StringBuilder result = new StringBuilder();
        try {
            // Подключение к базе данных
            connection = DriverManager.getConnection("jdbc:sqlite:Sportsman.db");
            Statement statement = connection.createStatement();

            // Запрос к базе данных
            ResultSet resultSet = statement.executeQuery(strRequest);
            while(resultSet.next()){
                assert false;
                result.append(resultSet.getString(columnName)).append(" ");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (connection != null) {
                    connection.close(); // закрытие соединения
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return result.toString();
    }

    // Задача 2
//    public static void printAverageGrantSize() {
//        // Строка запроса к базе данных
//        String strRequest =  "SELECT AVG(grantSize) " +
//                "AS averageGrantSize FROM GrantSize " +
//                "INNER JOIN BusinessTypeAndYear " +
//                "ON GrantSize.id_grant_size = BusinessTypeAndYear.id_grant_size " +
//                "WHERE BusinessTypeAndYear.businessType = 'Salon/Barbershop';";
//
//        // Делаем запрос и получаем результат
//        String result = connectionBD("averageGrantSize", strRequest);
//
//        // Выводим результат в консоль
//        System.out.println("Задача 2");
//        System.out.println("Средний размер гранта для Salon/Barbershop: " + result);
//        System.out.println();
//    }
//
//    // Задача 3
//    public static void printBusinessType() {
//        // Строка запроса к базе данных
//        String strRequest = "SELECT businessType " +
//                "AS oneBusinessType FROM BusinessTypeAndYear " +
//                "INNER JOIN MAINCompanyName " +
//                "ON BusinessTypeAndYear.id_business = MAINCompanyName.id_business " +
//                "INNER JOIN GrantSize " +
//                "ON BusinessTypeAndYear.id_grant_size = GrantSize.id_grant_size " +
//                "WHERE GrantSize.grantSize < 55000.00 " +
//                "GROUP BY BusinessTypeAndYear.businessType " +
//                "ORDER BY MAINCompanyName.numberOfJobs DESC " +
//                "LIMIT 1;";
//
//        // Делаем запрос и получаем результат
//        String result = connectionBD("oneBusinessType", strRequest);
//
//        // Выводим результат в консоль
//        System.out.println("Задача 3");
//        System.out.println("Тип бизнеса, предоставивший наибольшее количество " +
//                "рабочих мест, где размер гранта не превышает $55,000.00: " + result);
//    }
}