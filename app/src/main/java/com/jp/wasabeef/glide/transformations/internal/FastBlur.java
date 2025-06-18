package com.jp.wasabeef.glide.transformations.internal;

import android.graphics.Bitmap;

public class FastBlur {

    private FastBlur() {
        // This class should not be instantiated
    }

    public static Bitmap blur(Bitmap sentBitmap, int radius, boolean canReuseInBitmap) {
        if (radius < 1) return null;

        Bitmap bitmap = canReuseInBitmap ? sentBitmap : sentBitmap.copy(sentBitmap.getConfig(), true);

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int[] r = new int[w * h], g = new int[w * h], b = new int[w * h];
        int div = radius + radius + 1;
        int[] dv = createDivTable(div);
        int[][] stack = new int[div][3];
        int[] vmin = new int[Math.max(w, h)];

        horizontalBlur(w, h, pix, r, g, b, radius, dv, stack, vmin);
        verticalBlur(w, h, pix, r, g, b, radius, dv, stack, vmin);

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return bitmap;
    }

    private static int[] createDivTable(int div) {
        int divsum = ((div + 1) >> 1);
        divsum *= divsum;
        int[] dv = new int[256 * divsum];
        for (int i = 0; i < dv.length; i++) {
            dv[i] = i / divsum;
        }
        return dv;
    }

    private static void horizontalBlur(int w, int h, int[] pix, int[] r, int[] g, int[] b,
                                       int radius, int[] dv, int[][] stack, int[] vmin) {
        int wm = w - 1, div = radius + radius + 1;
        int r1 = radius + 1, yw = 0, yi = 0;

        for (int y = 0; y < h; y++) {
            int rinsum = 0, ginsum = 0, binsum = 0;
            int routsum = 0, goutsum = 0, boutsum = 0;
            int rsum = 0, gsum = 0, bsum = 0;

            for (int i = -radius; i <= radius; i++) {
                int p = pix[yi + Math.min(wm, Math.max(i, 0))];
                int[] sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                int rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }

            int stackpointer = radius;
            for (int x = 0; x < w; x++) {
                r[yi] = dv[rsum]; g[yi] = dv[gsum]; b[yi] = dv[bsum];
                rsum -= routsum; gsum -= goutsum; bsum -= boutsum;

                int stackstart = (stackpointer - radius + div) % div;
                int[] sir = stack[stackstart];
                routsum -= sir[0]; goutsum -= sir[1]; boutsum -= sir[2];

                if (y == 0) vmin[x] = Math.min(x + r1, wm);
                int p = pix[yw + vmin[x]];
                sir[0] = (p & 0xff0000) >> 16; sir[1] = (p & 0x00ff00) >> 8; sir[2] = (p & 0x0000ff);

                rinsum += sir[0]; ginsum += sir[1]; binsum += sir[2];
                rsum += rinsum; gsum += ginsum; bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];
                routsum += sir[0]; goutsum += sir[1]; boutsum += sir[2];
                rinsum -= sir[0]; ginsum -= sir[1]; binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
    }

    private static void verticalBlur(int w, int h, int[] pix, int[] r, int[] g, int[] b,
                                     int radius, int[] dv, int[][] stack, int[] vmin) {
        int hm = h - 1, div = radius + radius + 1, r1 = radius + 1;
        for (int x = 0; x < w; x++) {
            int rinsum = 0, ginsum = 0, binsum = 0;
            int routsum = 0, goutsum = 0, boutsum = 0;
            int rsum = 0, gsum = 0, bsum = 0;
            int yp = -radius * w, yi = x, stackpointer = radius;

            for (int i = -radius; i <= radius; i++) {
                int yPos = Math.max(0, yp) + x;
                int[] sir = stack[i + radius];
                sir[0] = r[yPos]; sir[1] = g[yPos]; sir[2] = b[yPos];
                int rbs = r1 - Math.abs(i);
                rsum += r[yPos] * rbs;
                gsum += g[yPos] * rbs;
                bsum += b[yPos] * rbs;

                if (i > 0) {
                    rinsum += sir[0]; ginsum += sir[1]; binsum += sir[2];
                } else {
                    routsum += sir[0]; goutsum += sir[1]; boutsum += sir[2];
                }

                if (i < hm) yp += w;
            }

            for (int y = 0; y < h; y++) {
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];
                rsum -= routsum; gsum -= goutsum; bsum -= boutsum;

                int stackstart = (stackpointer - radius + div) % div;
                int[] sir = stack[stackstart];
                routsum -= sir[0]; goutsum -= sir[1]; boutsum -= sir[2];

                if (x == 0) vmin[y] = Math.min(y + r1, hm) * w;
                int p = x + vmin[y];
                sir[0] = r[p]; sir[1] = g[p]; sir[2] = b[p];

                rinsum += sir[0]; ginsum += sir[1]; binsum += sir[2];
                rsum += rinsum; gsum += ginsum; bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];
                routsum += sir[0]; goutsum += sir[1]; boutsum += sir[2];
                rinsum -= sir[0]; ginsum -= sir[1]; binsum -= sir[2];

                yi += w;
            }
        }
    }
}
