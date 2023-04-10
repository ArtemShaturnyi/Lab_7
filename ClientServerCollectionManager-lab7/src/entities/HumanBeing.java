package entities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Класс Человека
 */
public class HumanBeing implements Comparable<HumanBeing> {
    /**
     * Поле id должно быть больше нуля
     */
    private int id;

    private int authorId;

    /**
     * Поле name не должно быть null и пустым
     */
    private String name;

    /**
     * Поле coordinates не должно быть null
     */
    private Coordinates coordinates;

    /**
     * Поле creationDate не должно быть null
     */
    private java.time.LocalDateTime creationDate;

    /**
     * Поле realHero
     */
    private boolean realHero;

    /**
     * Поле hasToothpick не должно быть null
     */
    private Boolean hasToothpick;

    /**
     * Поле impactSpeed
     */
    private int impactSpeed;

    /**
     * Поле soundtrackName не должно быть null
     */
    private String soundtrackName;

    /**
     * Поле minutesOfWaiting
     */
    private long minutesOfWaiting;

    /**
     * Поле weaponType не должно быть null
     */
    private WeaponType weaponType;

    /**
     * Поле car не должно быть null
     */
    private Car car;

    public HumanBeing() {
        car = new Car();
        coordinates = new Coordinates();
        hasToothpick = false;
        name = "empty";
        weaponType = WeaponType.RIFLE;
    }

    /**
     *
     * @return int
     */
    public int getId() {
        return id;
    }

    /**
     * set Id
     * @param id
     * устанавливает свойство
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     *
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * set name
     * @param name
     * устанавливает свойство
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     *
     * @return Coordinates
     */
    public Coordinates getCoordinates() {
        return coordinates;
    }

    /**
     * set coordinates
     * @param coordinates
     * устанавливает свойство
     */
    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    /**
     *
     * @return LocalDateTime
     */
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    /**
     * set creationDate
     * @param creationDate
     * устанавливает свойство
     */
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    /**
     *
     * @return boolean
     */
    public boolean isRealHero() {
        return realHero;
    }

    /**
     * set realHero
     * @param realHero
     * устанавливает свойство
     */
    public void setRealHero(boolean realHero) {
        this.realHero = realHero;
    }

    /**
     *
     * @return Boolean
     */
    public Boolean getHasToothpick() {
        return hasToothpick;
    }

    /**
     * set hasToothpick
     * @param hasToothpick
     * устанавливает свойство
     */
    public void setHasToothpick(Boolean hasToothpick) {
        this.hasToothpick = hasToothpick;
    }

    /**
     *
     * @return int
     */
    public int getImpactSpeed() {
        return impactSpeed;
    }

    /**
     * set impactSpeed
     * @param impactSpeed
     * устанавливает свойство
     */
    public void setImpactSpeed(int impactSpeed) {
        this.impactSpeed = impactSpeed;
    }

    /**
     *
     * @return String
     */
    public String getSoundtrackName() {
        return soundtrackName;
    }

    /**
     * set soundTrackName
     * @param soundtrackName
     * устанавливает свойство
     */
    public void setSoundtrackName(String soundtrackName) {
        this.soundtrackName = soundtrackName;
    }

    /**
     *
     * @return long
     */
    public long getMinutesOfWaiting() {
        return minutesOfWaiting;
    }

    /**
     * set minutesOfWaiting
     * @param minutesOfWaiting
     * устанавливает свойство
     */
    public void setMinutesOfWaiting(long minutesOfWaiting) {
        this.minutesOfWaiting = minutesOfWaiting;
    }

    /**
     *
     * @return WeaponType
     */
    public WeaponType getWeaponType() {
        return weaponType;
    }

    /**
     * set weaponType
     * @param weaponType
     * устанавливает свойство
     */
    public void setWeaponType(WeaponType weaponType) {
        this.weaponType = weaponType;
    }

    /**
     *
     * @return Car
     */
    public Car getCar() {
        return car;
    }

    /**
     *
     * @param car
     * устанавливает машину
     */
    public void setCar(Car car) {
        this.car = car;
    }

    /**
     *
     * @return String
     */
    @Override
    public String toString() {
        return String.format(
                "id: %d, name: %s, coordinates(x: %f, y: %d), creation date: %s, real hero: %s, has toothpick: %s, impact speed: %d, "
                        + "soundtrack name: %s, minutes of waiting: %d, weapon type: %s, car(name : %s)",
                id, name, coordinates.getX(), coordinates.getY(), creationDate.format(DateTimeFormatter.ISO_DATE_TIME), realHero,
                hasToothpick, impactSpeed, soundtrackName, minutesOfWaiting, weaponType, car.getName());
    }

    /**
     * Compares by id
     * @param other
     * сравнивает класс
     * @return int
     */
    @Override
    public int compareTo(HumanBeing other) {
        return Integer.compare(this.id, other.id);
    }

    public boolean greaterTo(HumanBeing other) {
        return impactSpeed + minutesOfWaiting < other.impactSpeed + other.minutesOfWaiting;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }
}