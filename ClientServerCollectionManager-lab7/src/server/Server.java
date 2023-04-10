package server;

import entities.HumanBeing;
import entities.User;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.concurrent.ForkJoinPool;
import java.sql.DriverManager;
public class Server {
    public static final int SERVER_PORT = 8000;
    private static final String DB_URL = "jdbc:postgresql://pg:8000/studs"; /*ВСТАВИТЬ НУЖНОЕ* pg:8000/studs*/
    private static final String DB_USER = "user";/*ПРОВЕРИТЬ ВХОД*/
    private static final String DB_PASSWORD = "user";/*ПРОВЕРИТЬ ВХОД*/
    private static CollectionWrapper collectionWrapper;
    private static Connection connection;

    public static CollectionWrapper loadFromDB(Connection connection) throws SQLException {
        createTables(connection);
        Statement statement = connection.createStatement();
        return new CollectionWrapper(statement.executeQuery("SELECT * FROM human_being"));
    }

    private static void createTables(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();

        if(tableNotExists(connection, "human_being")) {
            String humanBeingTable = "CREATE TABLE human_being(" +
                    "id serial," +
                    "author_id integer," +
                    "name varchar(20)," +
                    "created_by date," +
                    "real_hero boolean, " +
                    "has_toothpick boolean," +
                    "impact_speed integer," +
                    "soundtrack_name varchar(20)," +
                    "minutes_of_waiting integer," +
                    "weapon_type varchar(12)," +
                    "car_name varchar(20)," +
                    "coord_x real," +
                    "coord_y integer);";
            statement.execute(humanBeingTable);
        }

        if(tableNotExists(connection, "users")) {
            statement.execute("CREATE TABLE users(id serial, login varchar(32), password bytea);");
        }
    }

    private static boolean tableNotExists(Connection connection, String tableName) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet resultSet = meta.getTables(null, null, tableName, new String[] {"TABLE"});

        return !resultSet.next();
    }

    public static void addToDB(HumanBeing humanBeing) throws SQLException {
        String sql = "INSERT INTO human_being(" +
                "name, created_by, real_hero, has_toothpick, impact_speed, soundtrack_name, minutes_of_waiting, weapon_type, car_name, coord_x, coord_y, author_id) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        LocalDateTime dateTime = humanBeing.getCreationDate();

        PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, humanBeing.getName());
        preparedStatement.setDate(2, Date.valueOf(dateTime.toLocalDate()));
        preparedStatement.setBoolean(3, humanBeing.isRealHero());
        preparedStatement.setBoolean(4, humanBeing.getHasToothpick());
        preparedStatement.setInt(5, humanBeing.getImpactSpeed());
        preparedStatement.setString(6, humanBeing.getSoundtrackName());
        preparedStatement.setLong(7, humanBeing.getMinutesOfWaiting());
        preparedStatement.setString(8,  humanBeing.getWeaponType().toString());
        preparedStatement.setString(9, humanBeing.getCar().getName());
        preparedStatement.setDouble(10, humanBeing.getCoordinates().getX());
        preparedStatement.setLong(11, humanBeing.getCoordinates().getY());
        preparedStatement.setInt(12, humanBeing.getAuthorId());

        if(preparedStatement.executeUpdate() > 0) {
            try(ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if(resultSet.next()) {
                    humanBeing.setId((int)resultSet.getLong(1));
                    humanBeing.setAuthorId(resultSet.getInt("author_id"));
                    collectionWrapper.add(humanBeing);
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } else {
            throw new SQLException("Creating human failed");
        }
    }

    public static void registerUser(User user) throws SQLException {
        if(findUser(user) != null) {
            throw new SQLException("User already exists.");
        }

        String sql = "INSERT INTO users(login, password) " +
                "VALUES(?, ?);";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, user.getLogin());
        preparedStatement.setBytes(2, user.getPassword());

        if (preparedStatement.executeUpdate() < 1) {
            throw new SQLException("Registration failed.");
        }
    }

    public static User findUser(User user) throws SQLException {
        String sql = "SELECT * FROM users WHERE login = ?;";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, user.getLogin());

        ResultSet rs = preparedStatement.executeQuery();

        if(rs.next()) {
            String login = rs.getString("login");
            byte[] password = rs.getBytes("password");
            User usr = new User(login, password);
            usr.setId(rs.getInt("id"));
            return usr;
        } else {
            return null;
        }
    }

    public static void removeFromDB(int id, int userId) throws SQLException {
        if(!collectionWrapper.removeById(id, userId)) {
            throw new SQLException("Human not found by id");
        }

        String sql = "DELETE FROM human_being WHERE id = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
    }

    public static void updateBD(int id, HumanBeing humanBeing) throws SQLException {
        String sql = "UPDATE human_being SET " +
                "name = ?, " +
                "created_by = ?, " +
                "real_hero = ?," +
                "has_toothpick = ?," +
                "impact_speed = ?," +
                "soundtrack_name = ?," +
                "minutes_of_waiting = ?," +
                "weapon_type = ?," +
                "car_name = ?," +
                "coord_x = ?," +
                "coord_y = ?," +
                "author_id = ?" +
                " WHERE id = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, humanBeing.getName());
        preparedStatement.setDate(2, Date.valueOf(humanBeing.getCreationDate().toLocalDate()));
        preparedStatement.setBoolean(3, humanBeing.isRealHero());
        preparedStatement.setBoolean(4, humanBeing.getHasToothpick());
        preparedStatement.setInt(5, humanBeing.getImpactSpeed());
        preparedStatement.setString(6, humanBeing.getSoundtrackName());
        preparedStatement.setLong(7, humanBeing.getMinutesOfWaiting());
        preparedStatement.setString(8, humanBeing.getWeaponType().toString());
        preparedStatement.setString(9, humanBeing.getCar().getName());
        preparedStatement.setDouble(10, humanBeing.getCoordinates().getX());
        preparedStatement.setLong(11, humanBeing.getCoordinates().getY());
        preparedStatement.setInt(12, humanBeing.getAuthorId());
        preparedStatement.setInt(13, id);

        preparedStatement.executeUpdate();
    }

    public static void main(String[] args) {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            ForkJoinPool forkJoinPool = new ForkJoinPool();

            collectionWrapper = loadFromDB(connection);

            while(!serverSocket.isClosed()) {
                Socket client = serverSocket.accept();
                MultiThreadedServerTask task = new MultiThreadedServerTask(client, collectionWrapper);
                forkJoinPool.execute(task);
            }
        } catch (IOException e) {
            System.out.println("Невозможно создать подключение.");
        } catch (SQLException e) {
            System.out.println("Невозможно подключиться к PostgreSQL");
            System.out.println(e.getMessage());
        }
    }
}
