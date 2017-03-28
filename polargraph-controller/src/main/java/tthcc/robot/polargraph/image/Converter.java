package tthcc.robot.polargraph.image;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import lombok.Data;
import tthcc.robot.polargraph.constant.Constants;
import tthcc.robot.polargraph.constant.Paper;
import tthcc.robot.polargraph.constant.Pen;
import tthcc.robot.polargraph.constant.Pixel;

import com.mortennobel.imagescaling.AdvancedResizeOp;
import com.mortennobel.imagescaling.ResampleOp;

@Data
public abstract class Converter {

    //画布
    protected Paper  paper;
    //画笔
    protected Pen    pen;
    //像素块
    protected Pixel  pixel;
    protected String filename;

    protected abstract int[][] convert(int[][] data);

    public int[][] convert(String filename, int offsetX, int width, int offsetY, int height) throws Exception {

        this.filename = filename;
        BufferedImage image = ImageIO.read(new File(filename));
        int w = width;
        int h = height;
        int[][] data = new int[h][w];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                data[y][x] = image.getRGB(x + offsetX, y + offsetY);
            }
        }
        data = cut(data);
        data = scale(data);
        data = convert(data);

        return data;

        //        this.filename = filename;
        //        BufferedImage image = ImageIO.read(new File(filename));
        //        int w = image.getWidth();
        //        int h = image.getHeight();
        //        int[][] data = new int[h][w];
        //        for (int y = 0; y < h; y++) {
        //            for (int x = 0; x < w; x++) {
        //                data[y][x] = image.getRGB(x, y);
        //            }
        //        }
        //        data = cut(data);
        //        data = scale(data);
        //        data = convert(data);
        //
        //        return data;

    }

    private int[][] scale(int[][] data) {
        int pixelsX = (int) ((paper.width() - 2 * paper.offsetW()) / pen.width() / pixel.lines());
        int pixelsY = (int) ((paper.height() - 2 * paper.offsetH()) / pen.width() / pixel.lines());

        int h = data.length;
        int w = data[0].length;
        float rate = (float) pixelsX / (float) w;
        float rate1 = (float) w / (float) h;
        float rate2 = (float) pixelsX / (float) pixelsY;
        if (rate2 > rate1) {
            rate = (float) pixelsY / (float) h;
        }
        int toH = (int) (h * rate);
        int toW = (int) (w * rate);

        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                image.setRGB(x, y, data[y][x]);
            }
        }

        ResampleOp resampleOp = new ResampleOp(toW, toH);
        resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Normal);
        BufferedImage scaledImage = resampleOp.filter(image, null);

        data = new int[toH][toW];
        for (int y = 0; y < toH; y++) {
            for (int x = 0; x < toW; x++) {
                data[y][x] = scaledImage.getRGB(x, y);
            }
        }
        save("scale", Constants.TYPE_COLOR, data);
        return data;
    }

    //    /**
    //     * @param data
    //     * @param colors
    //     * @return
    //     */
    //    protected int[][] reduce(int[][] data, Color color) {
    //        int h = data.length;
    //        int w = data[0].length;
    //        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_INDEXED);
    //        for (int y = 0; y < h; y++) {
    //            for (int x = 0; x < w; x++) {
    //                image.setRGB(x, y, data[y][x]);
    //            }
    //        }
    //
    //        ColorModel cm = image.getColorModel();
    //        IndexColorModel icm = (IndexColorModel) cm;
    //        WritableRaster raster = image.getRaster();
    //        int pixel = raster.getSample(0, 0, 0); // pixel is offset in ICM's palette
    //        int size = icm.getMapSize();
    //        byte[] reds = new byte[size];
    //        byte[] greens = new byte[size];
    //        byte[] blues = new byte[size];
    //        icm.getReds(reds);
    //        icm.getGreens(greens);
    //        icm.getBlues(blues);
    //        IndexColorModel icm2 = new IndexColorModel(8, size, reds, greens, blues, pixel);
    //        BufferedImage out = new BufferedImage(icm2, raster, image.isAlphaPremultiplied(), null);
    //        try {
    //            ImageIO.write(out, "jpeg", new File("d:/tmp/7/keji_256.jpg"));
    //        } catch (Exception exp) {
    //            exp.printStackTrace();
    //        }
    //        return data;
    //
    //        //        //        int[] colors = color.getColors();
    //        //        //        Arrays.sort(colors);
    //        //        int delta = 256 / (color.getColors() - 1);
    //        //        int[] colors = new int[color.getColors()];
    //        //        for (int i = 0; i < color.getColors(); i++) {
    //        //            colors[i] = i * delta;
    //        //            if (i == color.getColors() - 1) {
    //        //                colors[i] = 0xff;
    //        //            }
    //        //        }
    //        //        for (int v : colors) {
    //        //            System.out.println(Integer.toHexString(v));
    //        //        }
    //        //        int h = data.length;
    //        //        int w = data[0].length;
    //        //        int[][] low = new int[h][w];
    //        //        for (int y = 0; y < h; y++) {
    //        //            for (int x = 0; x < w; x++) {
    //        //                low[y][x] = getColor(data[y][x], colors);
    //        //            }
    //        //        }
    //        //        save("low", Constants.TYPE_COLOR, low);
    //        //        return low;
    //    }

    //    /**
    //     * 中值法
    //     * 
    //     * @param color
    //     * @param colors
    //     * @return
    //     */
    //    private int getColor(int color, int[] colors) {
    //        int r = (color >> 16) & 0xff;
    //        int g = (color >> 8) & 0xff;
    //        int b = color & 0xff;
    //        int R = 0, G = 0, B = 0;
    //        int check1 = 0, check2 = 0;
    //        for (int i = 0; i < colors.length; i++) {
    //            check1 = i == 0 ? 0x00 : (colors[i - 1] + colors[i]) / 2;
    //            check2 = i == colors.length - 1 ? 0xff : (colors[i] + colors[i + 1]) / 2;
    //            if (r >= check1 && r < check2) {
    //                R = colors[i];
    //            }
    //            if (g >= check1 && g < check2) {
    //                G = colors[i];
    //            }
    //            if (b >= check1 && b < check2) {
    //                B = colors[i];
    //            }
    //        }
    //        //System.out.println(Integer.toHexString(color) + " -> " + Integer.toHexString(v));
    //        return (R << 16) | (G << 8) | B;
    //
    //    }

    /**
     * @param data
     * @param paper
     * @return
     */
    private int[][] cut(int[][] data) {
        int white = 0xffffff;
        int h = data.length;
        int w = data[0].length;
        int[][] tmp = w > h ? revert(data) : data;
        h = tmp.length;
        w = tmp[0].length;
        float rate = ((float) (paper.width() - 2 * paper.offsetW()) / (float) (paper.height() - 2 * paper.offsetH()));
        if (((float) w) / ((float) h) < rate) {
            //左右补白边
            int addition = (int) ((h * rate - w) / 2);
            int[][] tmp2 = new int[h][w + 2 * addition];
            for (int y = 0; y < h; y++) {
                for (int i = 0; i < addition; i++) {
                    tmp2[y][i] = white;
                    tmp2[y][w + addition + i] = white;
                }
                System.arraycopy(tmp[y], 0, tmp2[y], addition, w);
            }
            tmp = tmp2;
        } else {
            //上下补白边
            int addition = (int) ((w / rate - h) / 2);
            int[][] tmp2 = new int[h + 2 * addition][w];
            for (int y = 0; y < addition; y++) {
                for (int x = 0; x < w; x++) {
                    tmp2[y][x] = white;
                    tmp2[y + addition + h][x] = white;
                }
            }
            for (int y = 0; y < h; y++) {
                System.arraycopy(tmp[y], 0, tmp2[y + addition], 0, w);
            }
            tmp = tmp2;
        }

        save("cut", Constants.TYPE_COLOR, tmp);

        return tmp;
    }

    /**
     * @param data
     * @return
     */
    private int[][] revert(int[][] data) {
        int h = data.length;
        int w = data[0].length;
        int[][] rgb = new int[w][h];
        for (int y = 0; y < w; y++) {
            for (int x = 0; x < h; x++) {
                rgb[w - y - 1][x] = data[x][y];
            }
        }
        return rgb;
    }

    /**
     * @param suffix
     * @param type
     * @param data
     */
    protected void save(String suffix, int type, int[][] data) {
        try {
            int idx = filename.lastIndexOf(".");
            String outfile = filename.substring(0, idx) + "_" + suffix + ".jpg";
            int rgb = 0;
            BufferedImage pixelImage = new BufferedImage(data[0].length, data.length, BufferedImage.TYPE_INT_RGB);
            for (int y = 0; y < data.length; y++) {
                for (int x = 0; x < data[0].length; x++) {
                    if (type == Constants.TYPE_GRAY) {
                        rgb = (data[y][x] << 16) | (data[y][x] << 8) | data[y][x];
                    } else {
                        rgb = data[y][x];
                    }
                    pixelImage.setRGB(x, y, rgb);
                }
            }
            ImageIO.write(pixelImage, "jpeg", new File(outfile));
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }
}
