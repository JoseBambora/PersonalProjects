package cases;

public class MyType {
    private final int x;
    private final int y;

    public MyType(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public MyType(String string) {
        string = string.substring(1, string.length() - 1);
        String[] split = string.split(",");
        this.x = Integer.parseInt(split[0]);
        this.y = Integer.parseInt(split[1]);
    }

    @Override
    public String toString() {
        return "Coords(" + x + "," + y + ")";
    }
}