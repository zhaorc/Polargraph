package tthcc.robot.polargraph.constant;

public enum Paper {
    //标准
    A0(841, 1189, 5, 5),
    A1(594, 841, 5, 5),
    A2(420, 594, 5, 5),
    A3(297, 420, 5, 5),
    A4(210, 297, 5, 5),
    A5(148, 210, 5, 5),
    //自定义
    X1(265, 355, 5, 5);

    private final int width;
    private final int height;
    private final int offsetW;
    private final int offsetH;

    /**
     * @param width width of paper in mm
     * @param height height of paper in mm
     */
    private Paper(int width, int height, int offsetW, int offsetH) {
        this.width = width;
        this.height = height;
        this.offsetW = offsetW;
        this.offsetH = offsetH;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public int offsetW() {
        return offsetW;
    }

    public int offsetH() {
        return offsetH;
    }
}
