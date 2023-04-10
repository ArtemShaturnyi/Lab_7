package entities;

/**
 * Класс координат
 */
public class Coordinates {
    /**
     * Поле x должно быть больше -125
     */
    private double x;

    /**
     * Поле y
     */
    private long y;

    public Coordinates() {

    }

    public Coordinates(double x, long y) {
        this.x = x;
        this.y = y;
    }

    /**
     *
     * @return double
     */
    public double getX() {
        return x;
    }

    /**
     *
     * @param x
     * устанавливает свойтсво
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     *
     * @return long
     */
    public long getY() {
        return y;
    }

    /**
     *
     * @param y
     * устанавливает свойство
     */
    public void setY(long y) {
        this.y = y;
    }
}