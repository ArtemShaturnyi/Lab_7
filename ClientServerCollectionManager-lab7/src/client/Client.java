package client;

import com.google.gson.Gson;
import entities.*;
import server.Server;

import java.io.*;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Client {
    private User user;
    private Socket client;
    private Scanner scanner;
    private List<String> commandHistory;

    private DataOutputStream out;
    private BufferedReader in;

    private final Gson gson;


    public Client() throws InterruptedException {
        gson = new Gson();

        run();
    }

    public void run() throws InterruptedException {
        scanner = new Scanner(System.in);
        commandHistory = new ArrayList<>();
        connect();

        while(!client.isClosed()) {
            System.out.print("> ");

            String command = scanner.nextLine();
            String[] commandParts = command.trim().split(" ");

            if(commandHistory.size() >= 5) {
                commandHistory.remove(0);
            }

            commandHistory.add(commandParts[0]);
            analyzeCommand(commandParts);
        }
    }

    public void connect() {
        while(true) {
            try {
                client = new Socket("localhost", Server.SERVER_PORT);
                out = new DataOutputStream(client.getOutputStream());
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                while(true) {
                    System.out.println("Выберите действие: региcтрация - 0, вход - 1");
                    int action;

                    try {
                        action = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException exception) {
                        System.out.println("Неверный формат ввода");
                        continue;
                    }

                    switch(action) {
                        case 0:
                            System.out.println(sendMessage("register " + gson.toJson(makeUser(), User.class)));
                            break;
                        case 1:
                            user = makeUser();
                            String message = sendMessage("login " + gson.toJson(user, User.class));
                            if(message.equals("Success\n")) {
                                System.out.println("Успешно.");
                                return;
                            }
                            System.out.println(message);
                            break;
                        default:
                            System.out.println("Неверное действие.");
                    }
                }
            } catch (IOException | NoSuchAlgorithmException ignored) {
            }
        }
    }

    public String sendMessage(String message) {
        try {
            StringBuilder builder = new StringBuilder();

            out.writeBytes(message + '\n');

            String line;
            while((line = in.readLine()) != null && !line.isEmpty()) {
                builder.append(line).append("\n");
            }

            return builder.toString();
        } catch (IOException e) {
            System.out.println("Невозможно получить соединение с сервером.\nОжидание подключения.");
            connect();
            return null;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new Client();
    }

    private User makeUser() throws NoSuchAlgorithmException {
        String login, password;

        while(true) {
            System.out.print("Введите логин: ");
            login = scanner.nextLine();

            if(login.split(" ").length > 1) {
                System.out.println("В логине пробелов не должно быть.");
                continue;
            }

            break;
        }

        while(true) {
            System.out.print("Введите пароль: ");
            password = scanner.nextLine();

            if(password.split(" ").length > 1) {
                System.out.println("В пароле не должно быть пробелов.");
            } else if(password.length() <= 7) {
                System.out.println("Длина пароля должна быть не менее 8 символов.");
            } else {
                break;
            }
        }

        return new User(login, User.hashPassword(password));
    }


    private void analyzeCommand(String[] commandParts) throws InterruptedException {
        switch(commandParts[0]) {
            case "exit":
                System.out.println("EXIT");
                System.exit(0);
                return;
            case "help":
                System.out.println(sendMessage("help"));
                break;
            case "info":
                System.out.println(sendMessage("info"));
                break;
            case "show":
                System.out.println(sendMessage("show"));
                break;
            case "add":
                add(0);
                break;
            case "remove_by_id":
                if(commandParts.length > 1) {
                    removeById(commandParts[1]);
                } else {
                    System.out.println("Не введен параметр");
                }
                break;
            case "update":
                if(commandParts.length > 1) {
                    update(commandParts[1]);
                } else {
                    System.out.println("Не введен параметр");
                }
                break;
            case "clear":
                System.out.println(sendMessage("clear" + " " + gson.toJson(user, User.class)));
                break;
            case "history":
                commandHistory.forEach(System.out::println);
                break;
            case "remove_greater":
                System.out.println(sendMessage("remove_greater " + " " + gson.toJson(user, User.class) + " " + gson.toJson(add(-1), HumanBeing.class)));
                break;
            case "remove_lower":
                System.out.println(sendMessage("remove_lower " + " " + gson.toJson(user, User.class) + " " + gson.toJson(add(-1), HumanBeing.class)));
                break;
            case "count_by_car":
                countByCar();
                break;
            case "print_ascending":
                System.out.println(sendMessage("print_ascending"));
                break;
            case "print_unique_car":
                System.out.println(sendMessage("print_unique_car"));
                break;
            case "execute_script":
                if(commandParts.length > 1) {
                    executeScript(commandParts[1]);
                } else {
                    System.out.println("Не введен параметр");
                }
                break;
            default:
                System.out.println("Неизвестная команда");
                break;
        }
    }


    private HumanBeing add(int id) {
        HumanBeing human = new HumanBeing();

        while(true) {
            System.out.print("Введите имя: ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("Имя не может быть пустым.");
                continue;
            }
            human.setName(name);
            break;
        }

        Coordinates coordinates = new Coordinates();

        while(true) {
            System.out.print("Введите координату x: ");
            try {
                double x = Double.parseDouble(scanner.nextLine());
                if (x <= -125) {
                    System.out.println("Значение координаты x должно быть больше -125.");
                    continue;
                }
                coordinates.setX(x);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Некорректный формат числа для координаты x.");
            }
        }

        while (true) {
            System.out.print("Введите координату y: ");
            try {
                long y = Long.parseLong(scanner.nextLine());
                coordinates.setY(y);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Некорректный формат числа для координаты y.");
            }
        }

        human.setCoordinates(coordinates);
        human.setCreationDate(LocalDateTime.now());

        while(true) {
            System.out.print("Является ли героем настоящим (true/false): ");
            try {
                boolean realHero = Boolean.parseBoolean(scanner.nextLine());
                human.setRealHero(realHero);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Некорректный формат для значения поля realHero.");
            }
        }

        while(true) {
            System.out.print("Есть ли у персонажа зубочистка (true/false): ");
            try {
                Boolean hasToothpick = Boolean.parseBoolean(scanner.nextLine());
                human.setHasToothpick(hasToothpick);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Некорректный формат для значения поля hasToothpick.");
            }
        }

        while(true) {
            System.out.print("Введите скорость удара: ");
            try {
                int impactSpeed = Integer.parseInt(scanner.nextLine());
                human.setImpactSpeed(impactSpeed);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Некорректный формат числа для скорости удара.");
            }
        }

        while(true) {
            System.out.print("Введите название саундтрека: ");
            String soundtrackName = scanner.nextLine().trim();
            if (soundtrackName.isEmpty()) {
                System.out.println("Название саундтрека не может быть пустым.");
                continue;
            }

            human.setSoundtrackName(soundtrackName);
            break;
        }

        while(true) {
            System.out.print("Введите время ожидания: ");
            try {
                human.setMinutesOfWaiting(Long.parseLong(scanner.nextLine().trim()));
                break;
            } catch (NumberFormatException e) {
                System.out.println("Некорректный формат числа для время ожидания.");
            }
        }

        while(true) {
            System.out.print("Введите тип оружия(RIFLE/SHOTGUN/MACHINE_GUN): ");
            String weaponTypeStr = scanner.nextLine();

            try {
                WeaponType weaponType = WeaponType.valueOf(weaponTypeStr);
                human.setWeaponType(weaponType);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Некорректное название оружия.");
            }
        }

        System.out.print("Введите название машины: ");
        String carName = scanner.nextLine();

        human.setCar(new Car(carName));

        if(id == 0) {
            System.out.println(sendMessage("add " + gson.toJson(user, User.class) + " " + gson.toJson(human, HumanBeing.class)));
        }

        return human;
    }


    private void update(String idStr) {
        try {
            System.out.println(sendMessage("update " + Integer.parseInt(idStr) + " " + gson.toJson(user, User.class) + " " + gson.toJson(add(-1), HumanBeing.class)));
        } catch (NumberFormatException e) {
            System.out.println("Неверный формат id.");
        }
    }


    private void removeById(String idStr) {
        try {
            System.out.println(sendMessage("remove_by_id " + Integer.parseInt(idStr) + " " + gson.toJson(user, User.class)));
        } catch (NumberFormatException e) {
            System.out.println("Неверный формат id.");
        }
    }

    private void countByCar() {
        System.out.print("Введите название машины: ");
        System.out.println(sendMessage("count_by_car " + scanner.nextLine()));
    }


    private void executeScript(String fileName) throws InterruptedException {
        Scanner fileScanner;

        try {
            fileScanner = new Scanner(new File(fileName));
        } catch (FileNotFoundException e) {
            System.out.println("Невозможно открыть файл.");
            return;
        }

        while(fileScanner.hasNext()) {
            String[] commandParts = fileScanner.nextLine().trim().split(" ");
            analyzeCommand(commandParts);
        }
    }
}
