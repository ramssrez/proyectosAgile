package domain;

import constants.Messages;
import interfaces.IFigure;

public class Rectangle implements IFigure {
    private double base;
    private double height;

    public Rectangle(double base, double height) {
        this.base = base;
        this.height = height;
    }

    @Override
    public double calculatePerimeter() {
        return (2 * this.base) + (2 * this.height);
    }

    @Override
    public double calculateArea() {
        return this.base * this.height;
    }

    @Override
    public String readAttributes() {
        return String.format(Messages.RECTANGLE_PROPERTIES,this.height,Messages.MEASUREMENT_UNITS,this.base,Messages.MEASUREMENT_UNITS);
    }

    @Override
    public String toString() {
        return "Rectangle{" +
                "base=" + base +
                ", height=" + height +
                '}';
    }
}
