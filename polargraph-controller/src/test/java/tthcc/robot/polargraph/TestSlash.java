package tthcc.robot.polargraph;

import org.junit.Test;

import tthcc.robot.polargraph.constant.Paper;
import tthcc.robot.polargraph.constant.Pen;
import tthcc.robot.polargraph.constant.Pixel;
import tthcc.robot.polargraph.image.BWSvgConverter;
import tthcc.robot.polargraph.image.ColorfulSvgConverter;

public class TestSlash {

    @Test
    public void testBWConvert() {
        try {
            BWSvgConverter bw = new BWSvgConverter();
            bw.setPaper(Paper.A4);
            bw.setPen(Pen.P1);
            bw.setPixel(Pixel.L06);
            bw.convert("d:/tmp/7/keji.jpg");
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    @Test
    public void testColorConvert() {
        try {
            ColorfulSvgConverter colorful = new ColorfulSvgConverter();
            colorful.setPaper(Paper.A4);
            colorful.setPen(Pen.P1);
            colorful.setPixel(Pixel.L06);
            colorful.convert("d:/tmp/7/keji.jpg");
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

}
