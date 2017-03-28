package tthcc.robot.polargraph.util;

import tthcc.robot.polargraph.constant.Constants;

public class ColorUtil {

    public static int[][] build(int rgb, int n) {
        int[][] block = new int[n][n];
        if (rgb == Constants.BLACK || rgb == Constants.WHITE || rgb == Constants.RED || rgb == Constants.GREEN
                || rgb == Constants.BLUE) {
            for (int y = 0; y < n; y++) {
                for (int x = 0; x < n; x++) {
                    block[y][x] = rgb;
                }
            }
            return block;
        }

        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = (rgb) & 0xff;
        int[] nc = new int[] { n * n * r / 765, n * n * g / 765, n * n * b / 765, (n * n * (765 - r - g - b) / 765) };
        //        int delta = n * n - nc[0] - nc[1] - nc[2];
        //        for (int i = 0; i < delta; i++) {
        //            nc[i]++;
        //        }
        int[] vc = new int[] { Constants.RED, Constants.GREEN, Constants.BLUE, Constants.BLACK };
        int max = 0, tmp = 0;
        //sort
        for (int i = 0; i < 4; i++) {
            max = nc[i];
            for (int j = i; j < nc.length; j++) {
                if (nc[j] > max) {
                    max = nc[j];
                    tmp = nc[i];
                    nc[i] = nc[j];
                    nc[j] = tmp;
                    tmp = vc[i];
                    vc[i] = vc[j];
                    vc[j] = tmp;

                }
            }
        }
        int[] data = new int[n * n];
        for (int i = 0; i < data.length; i++) {
            data[i] = Constants.WHITE;
        }
        for (int i = 0; i < nc.length; i++) {
            int N = n * n;
            for (int j = 0; j < i; j++) {
                N -= nc[j];
            }
            int over = N / 2;
            for (int k = 0; k < n * n; k++) {
                if (data[k] != Constants.WHITE) {
                    continue;
                }
                over += nc[i];
                if (over > N) {
                    over -= N;
                    data[k] = vc[i];
                }
            }
        }

        for (int y = 0; y < n; y++) {
            for (int x = 0; x < n; x++) {
                block[y][x] = data[y * n + x];
            }
        }
        return block;
    }
}
