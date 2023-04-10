package server;

import com.google.gson.Gson;
import entities.HumanBeing;
import entities.User;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MultiThreadedServerTask implements Runnable {
    private final Socket socket;
    private final CollectionWrapper collectionWrapper;
    private final Gson gson;

    public MultiThreadedServerTask(Socket socket, CollectionWrapper collectionWrapper) {
        this.socket = socket;
        this.collectionWrapper = collectionWrapper;
        this.gson = new Gson();
    }

    @Override
    public void run() {
        System.out.println("Соединение получено.");

        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String data;
            while((data = in.readLine()) != null) {
                String[] commandParts = data.trim().split(" ", 4);
                out.writeBytes(analyzeCommand(commandParts) + '\n');
                out.flush();
            }

            in.close();
            out.close();
            socket.close();
        } catch(IOException e) {
            System.out.println("Невозможно получить подключение от клиента.");
        }

        System.out.println("Соединение закрыто.\n");
    }

    private String analyzeCommand(String[] commandParts) {
        commandParts[0] = commandParts[0].trim();
        System.out.println(commandParts[0]);

        switch(commandParts[0]) {
            case "help":
                return help();
            case "info":
                return info();
            case "show":
                return show();
            case "add":
                return add(commandParts[1], commandParts[2]);
            case "clear":
                return clear(commandParts[1]);
            case "update":
                return update(Integer.parseInt(commandParts[1]), commandParts[2], commandParts[3]);
            case "remove_by_id":
                return removeById(Integer.parseInt(commandParts[1]), commandParts[2]);
            case "print_ascending":
                return printAscending();
            case "count_by_car":
                return countByCar(commandParts[1]);
            case "print_unique_car":
                return printUniqueCar();
            case "remove_greater":
                return removeGreater(commandParts[1], commandParts[2]);
            case "remove_lower":
                return removeLower(commandParts[1], commandParts[2]);
            case "register":
                return register(commandParts[1]);
            case "login":
                return login(commandParts[1]);
        }

        return "Command not found\n";
    }

    private String help() {
        return  "•	help : print help\n"+
                "•	info : print information about collection\n" +
                "•	show : print to standard output all elements of the collection in string representation\n" +
                "•	add {element} : add a new element to the collection\n" +
                "•	update id {element} : update the value of the collection element whose id is equal to the given one\n" +
                "•	remove_by_id id : remove element from collection by its id\n" +
                "•	clear : clear collection\n" +
                "•	execute_script file_name : read and execute the script from the specified file. The script contains commands in the same form in which they are entered by the user in interactive mode.\n" +
                "•	remove_greater {element} : remove from the collection all elements greater than the given\n" +
                "•	remove_lower {element} : remove from the collection all elements smaller than the given one\n" +
                "•	history : print the last 5 commands (without their arguments)\n" +
                "•	count_by_car car : display the number of elements whose car field value is equal to the given one\n" +
                "•	print_ascending : display the elements of the collection in ascending order\n" +
                "•	print_unique_car : display the unique values of the car field of all elements in the collection\n";
    }

    private User checkUser(String jsonUser) {
        User user = gson.fromJson(jsonUser, User.class);
        try {
            User cmpUser = Server.findUser(user);
            if(user.compareTo(cmpUser)) {
                return cmpUser;
            }
            return null;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private String add(String jsonUser, String jsonHuman) {
        try {
            HumanBeing humanBeing = gson.fromJson(jsonHuman, HumanBeing.class);

            User user = checkUser(jsonUser);

            if(user == null) {
                return "User not found.";
            }

            humanBeing.setAuthorId(user.getId());
            Server.addToDB(humanBeing);
            return "Added.\n";
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return "Cannot add new user.\n";
        }
    }

    private String show() {
        if(collectionWrapper.getCollection().size() < 1) {
            return "Empty\n";
        }

        StringBuilder builder = new StringBuilder();

        for(HumanBeing humanBeing: collectionWrapper.getCollection().stream().sorted().collect(Collectors.toList())) {
            builder.append(humanBeing).append('\n');
        }
        return builder.toString();
    }

    private String info() {
        return String.format("Collection size: %d Initialization Date: %s\n", collectionWrapper.getCollection().size(), collectionWrapper.getInitializationDateTime().format(DateTimeFormatter.ISO_DATE_TIME));
    }

    private String removeById(int id, String jsonUser) {
        try {
            User user = checkUser(jsonUser);

            if(user == null) {
                return "User not found.";
            }

            Server.removeFromDB(id, user.getId());
            return "Deleted.\n";
        } catch (SQLException exception) {
            return exception.getMessage() + '\n';
        }
    }

    private String update(int id, String jsonUser, String jsonHuman) {
        User user = checkUser(jsonUser);

        if(user == null) {
            return "User not found.";
        }

        if(!collectionWrapper.update(id, gson.fromJson(jsonHuman, HumanBeing.class), user.getId())) {
            return "Cannot update human.\n";
        }

        try {
            Server.updateBD(id, collectionWrapper.find(id));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return "Cannot update human.\n";
        }

        return "Updated\n";
    }

    private String printAscending() {
        StringBuilder builder = new StringBuilder();

        if(collectionWrapper.getCollection().size() < 1) {
            return "Empty.\n";
        }

        collectionWrapper.getCollection().stream().sorted().forEach(human -> builder.append(human).append('\n'));
        return builder + "\n";
    }

    private String countByCar(String carName) {
        long count = collectionWrapper.getCollection().stream().filter(human -> carName.trim().equals(human.getCar().getName().trim())).count();
        return "Count: " + count + "\n";
    }

    private String printUniqueCar() {
        List<String> carNames = collectionWrapper.getCollection().stream().map(human -> human.getCar().getName()).collect(Collectors.toList());

        StringBuilder builder = new StringBuilder();

        builder.append("Unique cars: \n");
        carNames.stream().distinct().filter(name -> Collections.frequency(carNames, name) == 1).forEach(name -> builder.append(name).append('\n'));
        return builder.toString();
    }

    private String removeGreater(String jsonUser, String jsonHuman) {
        User user = checkUser(jsonUser);

        if(user == null) {
            return "User not found.";
        }

        HumanBeing humanBeing = gson.fromJson(jsonHuman, HumanBeing.class);

        collectionWrapper.getCollection().removeIf(h -> h.greaterTo(humanBeing) && h.getAuthorId() == user.getId());
        return "Success.\n";
    }

    private String removeLower(String jsonUser, String jsonHuman) {
        User user = checkUser(jsonUser);

        if(user == null) {
            return "User not found.";
        }

        HumanBeing humanBeing = gson.fromJson(jsonHuman, HumanBeing.class);

        collectionWrapper.getCollection().removeIf(h -> !h.greaterTo(humanBeing) && h.getAuthorId() == user.getId());
        return "Success.\n";
    }

    private String clear(String jsonUser) {
        User user = checkUser(jsonUser);

        if(user == null) {
            return "User not found.";
        }

        collectionWrapper.getCollection().removeIf(h -> {
            if(h.getAuthorId() == user.getId()) {
                try {
                    Server.removeFromDB(h.getId(), user.getId());
                    return true;
                } catch (SQLException e) {
                    return false;
                }
            }
            return false;
        });
        return "Success.\n";
    }

    private String register(String userJson) {
        try {
            Server.registerUser(gson.fromJson(userJson, User.class));
            return "Success.\n";
        } catch (SQLException exception) {
            return exception.getMessage() + '\n';
        }
    }

    private String login(String userJson) {
        try {
            User user = gson.fromJson(userJson, User.class);
            User cmpUser = Server.findUser(user);

            if(!user.compareTo(cmpUser)) {
                return "User not found.\n";
            }
            return "Success\n";
        } catch (SQLException e) {
            return e.getMessage() + '\n';
        }
    }
}
