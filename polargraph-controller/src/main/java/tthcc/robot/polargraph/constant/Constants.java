package tthcc.robot.polargraph.constant;

public class Constants {

    public static final int    RED               = 0xFF0000;
    public static final int    GREEN             = 0x00FF00;
    public static final int    BLUE              = 0x0000FF;
    public static final int    WHITE             = 0xFFFFFF;
    public static final int    BLACK             = 0x000000;

    public static final float  WEIGHT_RED        = 0.33f;
    public static final float  WEIGHT_GREEN      = 0.56f;
    public static final float  WEIGHT_BLUE       = 0.11f;

    public static final int    TYPE_COLOR        = 1;
    public static final int    TYPE_GRAY         = 2;
    public static final String SVG_HEADER        = "<?xml version=\"1.0\" encoding=\"GBK\" standalone=\"no\"?>\r\n";
    public static final String SVG_SVG           = "<svg version=\"1.1\"\r\n"
                                                         + "     xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\r\n"
                                                         + "     xmlns:cc=\"http://creativecommons.org/ns#\"\r\n"
                                                         + "     xmlns:dc=\"http://purl.org/dc/elements/1.1/\"\r\n"
                                                         + "     xmlns=\"http://www.w3.org/2000/svg\"\r\n"
                                                         + "     width=\"%s\"\r\n" + "     height=\"%s\"\r\n"
                                                         + "     viewBox=\"0 0 %s %s\">\r\n";
    public static final String SVG_G             = "<g fill=\"none\" stroke=\"#000000\" stroke-width=\"1\">\r\n";
    public static final String SVG_PATH          = "<path d=\"M%s %sL%s %s\"/>\r\n";
    public static final String SVG_MOVE          = "M %s %s";
    public static final String SVG_SVG_END       = "</g></svg>";
    public static final byte[] BUF_END           = "\n".getBytes();

    public static final int    MIN_PIXEL_ON_LINE = 2;

    public static final int    BUF_SIZE          = 62;

    //i am ready
    public static final String CMD_I_AM_READY    = "X";
    //motor
    public static final String CMD_MOTOR         = "W";
    //pen
    public static final String CMD_PEN           = "V";
    //paper
    public static final String CMD_PAPER         = "U";
    //send path
    public static final String CMD_DONE          = "Y";
    public static final String CMD_SEND_PATH     = "_SEND_PATH_";

    public static final String PRINT_ARDUINO     = "arduino>[%s] - %s";
    public static final String PRINT_CONTROLLER  = "controller>[%s] - [%s] - %s";

}
