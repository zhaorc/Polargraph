package tthcc.robot.polargraph.constant;

public enum Color {
    C3(3),
    C5(5),
    C8(8);
    private int colors;

    private Color(int colors) {
        this.colors = colors;
    }

    public int getColors() {
        return colors;
    }
    //    C3(new int[] { 0x00, 0x80, 0xff }),
    //    C5(new int[] { 0x00, 0x40, 0x80, 0xc0, 0xff });
    //    private int[] colors;
    //
    //    private Color(int[] colors) {
    //        this.colors = colors;
    //    }
    //
    //    public int[] getColors() {
    //        return colors;
    //    }
}
