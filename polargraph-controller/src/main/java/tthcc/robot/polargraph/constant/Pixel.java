package tthcc.robot.polargraph.constant;

public enum Pixel {

    L02(2),
    L04(4),
    L06(6),
    L08(8),
    L10(10),
    L12(12);

    private int lines;

    private Pixel(int lines) {
        this.lines = lines;
    }

    public int lines() {
        return lines;
    }
}
