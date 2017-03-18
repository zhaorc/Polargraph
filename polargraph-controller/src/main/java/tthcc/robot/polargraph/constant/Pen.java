package tthcc.robot.polargraph.constant;

public enum Pen {

    P1(0.20),
    P2(0.25),
    P3(0.30),
    P4(0.35),
    P5(0.40),
    P6(0.45),
    P7(0.50);

    private double width;

    private Pen(double width) {
        this.width = width;
    }

    public double width() {
        return this.width;
    }
}
