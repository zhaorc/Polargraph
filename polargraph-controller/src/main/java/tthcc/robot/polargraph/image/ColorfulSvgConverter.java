package tthcc.robot.polargraph.image;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import tthcc.robot.polargraph.constant.Constants;
import tthcc.robot.polargraph.util.ConvertUtil;

public class ColorfulSvgConverter extends Converter {

    @Override
    protected int[][] convert(int[][] data) {

        data = to8bit(data);

        save("result", Constants.TYPE_COLOR, data);
        return data;
    }

    private int[][] to8bit(int[][] data) {
        int h = data.length;
        int w = data[0].length;
        BufferedImage src = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                src.setRGB(x, y, data[y][x]);
            }
        }
        BufferedImage dest = ConvertUtil.convert8(src);
        try {
            ImageIO.write(dest, "jpeg", new File("d:/tmp/7/keji_8bit.jpg"));
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        return data;
    }

}
