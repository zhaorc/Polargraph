package tthcc.robot.polargraph;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.junit.Test;

import tthcc.robot.polargraph.util.ColorUtil;

public class TestColorUtil {

    @Test
    public void doTest() {
        int n = 100;
        int[][] block = ColorUtil.build(0x00FFFF, n);
        //        for (int y = 0; y < n; y++) {
        //            for (int x = 0; x < n; x++) {
        //                if (y % 2 == 0) {
        //                    if (x % 2 == 0) {
        //                        block[y][x] = Constants.RED;
        //                    } else {
        //                        block[y][x] = Constants.GREEN;
        //                    }
        //                } else {
        //                    if (x % 2 == 0) {
        //                        block[y][x] = Constants.GREEN;
        //                    } else {
        //                        block[y][x] = Constants.RED;
        //                    }
        //                }
        //            }
        //        }
        try {
            BufferedImage image = new BufferedImage(n, n, BufferedImage.TYPE_INT_RGB);
            for (int y = 0; y < n; y++) {
                for (int x = 0; x < n; x++) {
                    image.setRGB(x, y, block[y][x]);
                }
            }
            ImageIO.write(image, "jpeg", new File("d://tmp/7/abc.jpg"));
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }
}
