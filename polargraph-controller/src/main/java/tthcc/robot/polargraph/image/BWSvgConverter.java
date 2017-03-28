package tthcc.robot.polargraph.image;

import java.io.FileOutputStream;
import java.util.List;

import tthcc.robot.polargraph.constant.Constants;

import com.google.common.collect.Lists;

public class BWSvgConverter extends Converter {

    @Override
    protected int[][] convert(int[][] data) {
        data = gray(data);
        data = diagonal(data);
        toSVG(data);
        save("result", Constants.TYPE_COLOR, data);
        return data;
    }

    /**
     * convert rgb to gray
     * 
     * @param image
     * @return
     */
    private int[][] gray(int[][] data) {
        int h = data.length;
        int w = data[0].length;
        int r = 0, g = 0, b = 0;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                r = (data[y][x] >> 16) & 0xff;
                g = (data[y][x] >> 8) & 0xff;
                b = (data[y][x]) & 0xff;
                data[y][x] = (int) (r * Constants.WEIGHT_RED + g * Constants.WEIGHT_GREEN + b * Constants.WEIGHT_BLUE);
            }
        }
        save("gray", Constants.TYPE_GRAY, data);
        return data;
    }

    /**
     * @param data
     * @return
     */
    private int[][] diagonal(int[][] data) {
        int h = data.length;
        int w = data[0].length;
        int colors = pixel.lines() * pixel.lines() / 2;
        int[][] rgb = new int[h * pixel.lines()][w * pixel.lines()];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int k = calcLevel(data[y][x], colors);
                int[][] block = generateBlock(pixel.lines(), k);
                for (int j = 0; j < pixel.lines(); j++) {
                    for (int i = 0; i < pixel.lines(); i++) {
                        rgb[y * pixel.lines() + j][x * pixel.lines() + i] = block[j][i];
                    }
                }
            }
        }

        return rgb;
    }

    /**
     * @param data
     * @return
     */
    private List<String> generateSVGPath(int[][] data) {
        int offset = 2;
        int b = offset;
        int k = -1;
        int h = data.length;
        int w = data[0].length;
        int y;
        int x1, X, Y;
        boolean upflag = true;
        boolean downflag = true;
        List<String> pathList = Lists.newArrayList();
        for (b = offset; b < w + h - offset;) {
            //up
            upflag = true;
            while (upflag) {
                x1 = -1;
                X = b < w - offset ? b : w - offset;
                for (int x = 0; x <= X; x++) {
                    y = k * x + b;
                    if (y >= h || y < 0) {
                        x1 = -1;
                        continue;
                    }
                    if (x >= w - offset && y >= h - offset) {
                        upflag = false;
                    }
                    if (x == X) {
                        if (x1 > -1) {
                            if (data[y][x] == Constants.BLACK) {
                                pathList.add(path(k, b, x1, x));
                            } else {
                                pathList.add(path(k, b, x1, x - 1));
                            }
                            x1 = -1;
                        }
                        break;
                    }
                    if (x1 == -1 && data[y][x] == Constants.BLACK) {
                        x1 = x;
                        upflag = false;
                        continue;
                    }
                    if (x1 > -1 && data[y][x] == Constants.WHITE) {
                        if (checkLength(data, x, y, 1, -1, Constants.MIN_PIXEL_ON_LINE)) {
                            pathList.add(path(k, b, x1, x - 1));
                            x1 = -1;
                            continue;
                        }
                    }
                }
                b++;
            }
            //down
            downflag = true;
            while (downflag) {
                x1 = -1;
                Y = b < h - offset ? b : h - offset;
                for (int x = b; x >= 0; x--) {
                    if (x >= w) {
                        x1 = -1;
                        continue;
                    }
                    y = k * x + b;
                    if (y >= h || y < 0) {
                        x1 = -1;
                        break;
                    }
                    if (x >= w - offset && y >= h - offset) {
                        downflag = false;
                    }
                    if (y == Y) {
                        if (x1 > -1) {
                            if (data[y][x] == Constants.BLACK) {
                                pathList.add(path(k, b, x1, x));
                            } else {
                                pathList.add(path(k, b, x1, x + 1));
                            }
                            x1 = -1;
                        }
                        break;
                    }
                    if (x1 == -1 && data[y][x] == Constants.BLACK) {
                        x1 = x;
                        downflag = false;
                        continue;
                    }
                    if (x1 > -1 && data[y][x] == Constants.WHITE) {
                        if (checkLength(data, x, y, -1, 1, Constants.MIN_PIXEL_ON_LINE)) {
                            pathList.add(path(k, b, x1, x + 1));
                            x1 = -1;
                            continue;
                        }
                    }
                }
                b++;
            }
        }

        return pathList;
    }

    private String path(int k, int b, int x1, int x2) {
        int y1 = b + k * x1;
        int y2 = b + k * x2;
        return String.format(Constants.SVG_PATH, x1, y1, x2, y2);
    }

    /**
     * @param data
     */
    private void toSVG(int[][] data) {

        int h = data.length;
        int w = data[0].length;
        List<String> pathList = generateSVGPath(data);
        int idx = filename.lastIndexOf(".");
        String outfile = filename.substring(0, idx) + "_pixel.svg";
        try {
            FileOutputStream fout = new FileOutputStream(outfile);
            fout.write(Constants.SVG_HEADER.getBytes());
            fout.write(String.format(Constants.SVG_SVG, w, h, w, h).getBytes());
            fout.write(Constants.SVG_G.getBytes());
            for (String path : pathList) {
                fout.write(path.getBytes());
            }
            fout.write(Constants.SVG_SVG_END.getBytes());
            fout.close();
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    /**
     * @param n
     * @param k
     * @return
     */
    private int[][] generateBlock(int n, int k) {
        int level = n * n / 2;
        if (k <= level / 2) {
            return generatePixel(n, k);
        } else {
            int[][] slash = generatePixel(n, level / 2);
            int[][] block = generatePixel(n, k - level / 2);
            for (int y = 0; y < n; y++) {
                for (int x = 0; x < n; x++) {
                    if (block[y][x] == Constants.BLACK) {
                        slash[n - 1 - y][x] = block[y][x];
                    }
                }
            }
            return slash;
        }
    }

    /**
     * 计算灰度级别
     * 
     * @param gray
     * @param colors
     * @return
     */
    private int calcLevel(int gray, int colors) {
        int level = colors * (255 - gray) / 255;
        return level;
    }

    /**
     * 生成灰度级别为k的像素块
     * 
     * @param n
     * @param k
     * @return
     */
    private int[][] generatePixel(int n, int k) {
        int[][] block = new int[n][n];
        // 初始化为白色
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < n; x++) {
                block[y][x] = Constants.WHITE;
            }
        }

        int maxLine = calcMaxLine(k);
        generateCenter(n, maxLine, block);

        //上一个灰度的maxLine
        int lastLine = k == 1 ? maxLine : calcMaxLine(k - 1);
        //左右平分
        int p = 2 * k - maxLine;
        int m = p / 2;

        int left = (m % 2 == 0) ? m : (m + 1);
        int n1 = n;
        maxLine = lastLine;
        while (left > 0) {
            int q = maxLine / 2;
            int k1 = (q - 1) * (q - 1);
            maxLine = calcMaxLine(k1);
            if (maxLine > left) {
                k1 = (q - 2) * (q - 2);
                maxLine = calcMaxLine(k1);
            }
            n1 = n1 - 2;
            generateLeftTop(n1, maxLine, block);
            left = left - maxLine;
        }

        int right = p - ((m % 2 == 0) ? m : (m + 1));
        int n2 = n;
        maxLine = lastLine;
        while (right > 0) {
            int q = maxLine / 2;
            int k1 = (q - 1) * (q - 1);
            maxLine = calcMaxLine(k1);
            if (maxLine > right) {
                k1 = (q - 2) * (q - 2);
                maxLine = calcMaxLine(k1);
            }
            n2 = n2 - 2;
            generateRightBottom(n2, maxLine, n - 1, block);
            right = right - maxLine;
        }

        return block;
    }

    /**
     * 计算第k级灰度的最长斜线段的像素数<br>
     * 像素数=2*sqrt(大于等于k的最小平方数)
     * 
     * @param k 灰度级别
     * @return
     */
    private int calcMaxLine(int k) {
        for (int i = k;; i++) {
            double a = Math.sqrt(i);
            if (a == (int) a) {
                return ((int) a) * 2;
            }
        }
    }

    /**
     * 生成中心对角线
     * 
     * @param n
     * @param maxLine
     * @param block
     */
    private void generateCenter(int n, int maxLine, int[][] block) {
        // y=n-1-x
        // n/2-maxK/2 <= x <= n/2+maxK/2-1
        int x1 = n / 2 - maxLine / 2;
        int x2 = n / 2 + maxLine / 2 - 1;
        for (int x = x1; x <= x2; x++) {
            block[n - 1 - x][x] = Constants.BLACK;
        }
    }

    /**
     * 生成左上角斜线的像素值
     * 
     * @param n
     * @param maxLine
     * @param block 方块像素值
     * @return 斜线的像素数
     */
    private void generateLeftTop(int n, int maxLine, int[][] block) {
        // y=n-1-x
        // n/2-maxK/2 <= x <= n/2+maxK/2-1
        int x1 = n / 2 - maxLine / 2;
        int x2 = n / 2 + maxLine / 2 - 1;
        for (int x = x1; x <= x2; x++) {
            block[n - 1 - x][x] = Constants.BLACK;
        }
    }

    /**
     * 生成右下角斜线的像素值
     * 
     * @param n
     * @param maxLine
     * @param mirror 对称轴
     * @param block 方块像素值
     * @return 斜线的像素数
     */
    private void generateRightBottom(int n, int maxLine, int mirror, int[][] block) {
        // y=n-1-x
        // n/2-maxK/2 <= x <= n/2+maxK/2-1
        int x1 = n / 2 - maxLine / 2;
        int x2 = n / 2 + maxLine / 2 - 1;
        int y = 0;
        for (int x = x1; x <= x2; x++) {
            y = n - 1 - x;
            block[mirror - x][mirror - y] = Constants.BLACK;
            //            block[n - 1 - x][x] = BLACK;
        }
    }

    private boolean checkLength(int[][] data, int x, int y, int dx, int dy, int checkNum) {
        int h = data.length;
        int w = data[0].length;
        int color = data[y - dx][x - dx];
        int count = 1;
        for (int i = 0; i < checkNum - 1; i++) {
            if (x + dx < 0 || x + dx >= w || y + dy < 0 || y + dy >= h) {
                break;
            }
            if (data[y + i * dy][x + i * dx] == color) {
                count++;
            }
        }

        return count >= checkNum;
    }
}
