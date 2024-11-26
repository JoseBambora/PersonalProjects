package org.jdaextension.examples;


import org.jdaextension.interfaces.CustomType;

public class MyType implements CustomType {
    private final int x;
    private final int y;

    public MyType(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Coords(" + x + "," + y + ")";
    }

    @Override
    public CustomType clone() {
        return new MyType(x,y);
    }
}
