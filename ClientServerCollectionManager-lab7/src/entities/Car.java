package entities;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Класс машины
 */
public class Car {
    /**
     * Поле name не может быть null
     */
    private String name;

    /**
     *
     * @param name
     *  имя машины
     */
    public Car(String name) {
        this.name = name;
    }
    public Car() {

    }

    /**
     *
     * @return String
     */
    public String getName() {
        return name;
    }
}