package tthcc.robot.polargraph.image;

import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import tthcc.robot.polargraph.constant.Constants;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ColorfulSvgConverter extends Converter {
    @Override
    protected int[][] convert(int[][] data) {
        //        data = to8bit(data);
        data = mapColor(data);
        data = colorful(data);
        save("result", Constants.TYPE_COLOR, data);
        return data;
    }

    //    private int[][] mapColor(int[][] data, int[] colorMap) {
    //        Arrays.sort(colorMap);
    //        int h = data.length;
    //        int w = data[0].length;
    //        for (int y = 0; y < h; y++) {
    //            for (int x = 0; x < w; x++) {
    //                data[y][x] = getStdColor(data[y][x], colorMap);
    //            }
    //        }
    //        save("map", Constants.TYPE_COLOR, data);
    //        return data;
    //    }

    private int[][] mapColor(int[][] data) {
        int h = data.length;
        int w = data[0].length;
        BufferedImage src = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                src.setRGB(x, y, data[y][x]);
            }
        }
        BufferedImage dest = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_INDEXED);
        ColorConvertOp cco = new ColorConvertOp(src.getColorModel().getColorSpace(), dest.getColorModel()
                .getColorSpace(), null);
        cco.filter(src, dest);
        //        for (int y = 0; y < h; y++) {
        //            for (int x = 0; x < w; x++) {
        //                data[y][x] = dest.getRGB(x, y) & 0xffffff;
        //            }
        //        }
        //        save("8bit", Constants.TYPE_COLOR, data);

        List<Integer> colorList = Lists.newArrayList();
        Map<Integer, Integer> colorMap = Maps.newHashMap();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                //                int v = data[y][x];
                int v = dest.getRGB(x, y);
                v = v & 0xffffff;
                if (!colorList.contains(v)) {
                    colorList.add(v);
                    colorMap.put(v, 1);
                } else {
                    int c = colorMap.get(v);
                    colorMap.put(v, c + 1);
                }
            }
        }
        String[] kn = new String[colorList.size()];
        int k = 0, n = 0;
        for (int i = 0; i < colorList.size(); i++) {
            k = colorList.get(i);
            n = colorMap.get(k);
            kn[i] = String.format("%06d_%s", n, getHexString(k));
        }
        Arrays.sort(kn);
        int[] cmap = new int[16];
        for (int i = 0; i < cmap.length; i++) {
            cmap[i] = Integer.parseInt(kn[kn.length - 1 - i].substring(9), 16);
        }
        Arrays.sort(cmap);
        //        cmap[0] = Constants.BLACK;
        //        cmap[cmap.length - 1] = Constants.WHITE;

        IndexColorModel icm = new IndexColorModel(4, cmap.length, cmap, 0, false, Transparency.OPAQUE,
                DataBuffer.TYPE_BYTE);
        dest = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_INDEXED, icm);
        cco = new ColorConvertOp(src.getColorModel().getColorSpace(), dest.getColorModel().getColorSpace(), null);
        cco.filter(src, dest);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                data[y][x] = dest.getRGB(x, y) & 0xffffff;
            }
        }
        save("4bit", Constants.TYPE_COLOR, data);

        //        for (String str : kn) {
        //            System.out.println(str);
        //        }
        //        System.out.println("size=" + colorList.size());
        //        Integer[] values = new Integer[colorList.size()];
        //        values = colorList.toArray(values);
        //        Arrays.sort(values);
        //        String format = "000000";
        //        for (int v : values) {
        //            String s = Integer.toHexString(v).toUpperCase();
        //            int len = s.length();
        //            int append = 6 - len;
        //            s = "0x" + format.substring(0, append) + s;
        //            System.out.println(s);
        //        }

        return data;
    }

    /**
     * Converts the source image to 8-bit colour using the default 256-colour
     * palette. No transparency.
     * 
     * @param data the source image to convert
     * @return a copy of the source image with an 8-bit colour depth
     */
    private int[][] to8bit(int[][] data) {

        int h = data.length;
        int w = data[0].length;
        BufferedImage src = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                src.setRGB(x, y, data[y][x]);
            }
        }
        BufferedImage dest = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_INDEXED);
        ColorConvertOp cco = new ColorConvertOp(src.getColorModel().getColorSpace(), dest.getColorModel()
                .getColorSpace(), null);
        cco.filter(src, dest);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                data[y][x] = dest.getRGB(x, y) & 0xffffff;
            }
        }
        save("8bit", Constants.TYPE_COLOR, data);

        List<Integer> colorList = Lists.newArrayList();
        Map<Integer, Integer> colorMap = Maps.newHashMap();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int v = data[y][x];
                if (!colorList.contains(v)) {
                    colorList.add(v);
                    colorMap.put(v, 1);
                } else {
                    int c = colorMap.get(v);
                    colorMap.put(v, c + 1);
                }
            }
        }
        String[] kn = new String[colorList.size()];
        int k = 0, n = 0;
        for (int i = 0; i < colorList.size(); i++) {
            k = colorList.get(i);
            n = colorMap.get(k);
            kn[i] = String.format("%06d_%s", n, getHexString(k));
        }
        Arrays.sort(kn);
        //        for (String str : kn) {
        //            System.out.println(str);
        //        }

        //        System.out.println("size=" + colorList.size());
        //        Integer[] values = new Integer[colorList.size()];
        //        values = colorList.toArray(values);
        //        Arrays.sort(values);
        //        String format = "000000";
        //        for (int v : values) {
        //            String s = Integer.toHexString(v).toUpperCase();
        //            int len = s.length();
        //            int append = 6 - len;
        //            s = "0x" + format.substring(0, append) + s;
        //            System.out.println(s);
        //        }
        return data;
    }

    private String getHexString(int v) {
        String format = "000000";
        String s = Integer.toHexString(v).toUpperCase();
        int len = s.length();
        int append = 6 - len;
        s = "0x" + format.substring(0, append) + s;
        return s;
    }

    private int getStdColor(int rgb, int[] colorMap) {
        rgb = rgb & 0xffffff;
        int check1 = 0, check2 = 0;
        for (int i = 0; i < colorMap.length; i++) {
            if (i == 0) {
                check1 = 0;
                check2 = (colorMap[0] + colorMap[1]) / 2;
            } else if (i == colorMap.length - 1) {
                check1 = (colorMap[i - 1] + colorMap[i]) / 2;
                check2 = colorMap[i];
            } else {
                check1 = (colorMap[i - 1] + colorMap[i]) / 2;
                check2 = (colorMap[i] + colorMap[i + 1]) / 2;
            }
            if (rgb >= check1 && rgb < check2) {
                return colorMap[i];
            }
        }
        return rgb;
        //        int r = (rgb >> 16) & 0xff;
        //        int g = (rgb >> 8) & 0xff;
        //        int b = (rgb) & 0xff;
        //        int unit = 256 / 8;
        //        int half = unit / 2;
        //        for (int i = 0; i < 9; i++) {
        //            int level = i * unit;
        //            if (level == 256) {
        //                level = 255;
        //            }
        //            if (r > level - half && r <= level + half) {
        //                r = level;
        //            }
        //            if (g > level - half && g <= level + half) {
        //                g = level;
        //            }
        //            if (b > level - half && b <= level + half) {
        //                b = level;
        //            }
        //        }
        //        int v = (r << 16) | (g << 8) | b;
        //        //        //XXX
        //        //        System.out.println(Integer.toHexString(v));
        //        return v;
    }

    /**
     * @param data
     * @return
     */
    private int[][] colorful(int[][] data) {
        int h = data.length;
        int w = data[0].length;
        int[][] colors = new int[h * pixel.lines()][w * pixel.lines()];
        int[][] block = null;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                block = buildBlock(data[y][x]);
                //                block = ColorUtil.build(data[y][x], pixel.lines());
                for (int y1 = 0; y1 < pixel.lines(); y1++) {
                    for (int x1 = 0; x1 < pixel.lines(); x1++) {
                        colors[y * pixel.lines() + y1][x * pixel.lines() + x1] = block[y1][x1];
                        //                        colors[y * pixel.lines() + y1][x * pixel.lines() + x1] = data[y][x];
                    }
                }
            }
        }
        return colors;
    }

    private int[][] buildBlock(int rgb) {
        rgb = rgb & 0xffffff;
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = (rgb) & 0xff;
        int[][] block = new int[pixel.lines()][pixel.lines()];
        int count = pixel.lines() * pixel.lines() / 3;
        int countR = r * count / 255;
        int countG = g * count / 255;
        int countB = b * count / 255;
        int overR = count / 2;
        int overG = count / 2;
        int overB = count / 2;
        int mod = 0;
        int idx = 0;
        for (int y = 0; y < pixel.lines(); y++) {
            for (int x = 0; x < pixel.lines(); x++) {
                if (rgb == Constants.BLACK || rgb == Constants.WHITE || rgb == Constants.RED || rgb == Constants.GREEN
                        || rgb == Constants.BLUE) {
                    block[y][x] = rgb;
                } else {
                    mod = (y * pixel.lines() + x) % 3;
                    mod = idx % 3;
                    if (mod == 0) {
                        overR += countR;
                        if (overR > count) {
                            overR -= count;
                            block[y][x] = Constants.RED;
                        }
                    } else if (mod == 1) {
                        overG += countG;
                        if (overG > count) {
                            overG -= count;
                            block[y][x] = Constants.GREEN;
                        }
                    } else if (mod == 2) {
                        overB += countB;
                        if (overB > count) {
                            overB -= count;
                            block[y][x] = Constants.BLUE;
                        }
                    }
                    idx++;
                }
            }
        }

        return block;
    }
}
