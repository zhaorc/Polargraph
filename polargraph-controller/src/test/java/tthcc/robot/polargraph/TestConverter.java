package tthcc.robot.polargraph;

import org.junit.Test;

import tthcc.robot.polargraph.constant.Paper;
import tthcc.robot.polargraph.constant.Pen;
import tthcc.robot.polargraph.constant.Pixel;
import tthcc.robot.polargraph.image.BWSvgConverter;
import tthcc.robot.polargraph.image.ColorfulSvgConverter;

public class TestConverter {

    @Test
    public void testBWConvert() {
        try {
            BWSvgConverter bw = new BWSvgConverter();
            bw.setPaper(Paper.A4);
            bw.setPen(Pen.P4);
            bw.setPixel(Pixel.L06);
            bw.convert("d:/tmp/7/miao_2.jpg", 10, 160, 150, 152);
            //            bw.convert("d:/tmp/7/miao_2.jpg", 160, 170, 150, 152);
            //            bw.convert("d:/tmp/7/miao_2.jpg", 330, 240, 150, 152);
            //            bw.convert("d:/tmp/7/miao_2.jpg", 560, 190, 150, 152);
            //            bw.convert("d:/tmp/7/miao_2.jpg", 740, 160, 150, 152);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    @Test
    public void testColorConvert() {
        try {
            ColorfulSvgConverter colorful = new ColorfulSvgConverter();
            colorful.setPaper(Paper.X1);
            colorful.setPen(Pen.P1);
            colorful.setPixel(Pixel.L08);
            colorful.convert("d:/tmp/7/miao_2.jpg", 0, 0, 0, 0);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

}
