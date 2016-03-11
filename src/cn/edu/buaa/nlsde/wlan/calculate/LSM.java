package cn.edu.buaa.nlsde.wlan.calculate;

public class LSM {
    /**
     * 计算线性拟合的方程
     * @param x 横坐标
     * @param y 纵坐标
     * @return float数组，
     * 	第一项代表截距，第二项代表斜率，第三项代表离差平方和
     */
    public static float[] getLsm(float[] x, float[] y) {
        float avg_x = average(x);
        float avg_y = average(y);
        float sum_xy = 0;
        float sum_x2 = 0;
        for (int i = 0; i < x.length; i++) {
            sum_xy += (x[i] - avg_x) * (y[i] - avg_y);
            sum_x2 += (x[i] - avg_x) * (x[i] - avg_x);
        }
        float b = sum_xy / sum_x2;
        float a = avg_y - avg_x * b;
        
        float ssr = 0;
        for (int i = 0; i < x.length; i++) {
            ssr += (y[i] - a - b * x[i]) * (y[i] - a - b * x[i]);
        }
        float result[] = new float[3];
        result[0] = a;
        result[1] = b;
        result[2] = ssr;
        return result;
    }
    
    private static float average(float[] num) {
        float sum = 0;
        for (int i = 0; i < num.length; i++) {
            sum += num[i];
        }
        return sum / num.length;
    }
    
    public static void main(String args[]) {
        // float y[] = { -90, -88, -88, -87, -89, -89, -89, -89, -89, -85 };
        //float x[] = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        //float y[] = { -58, -46, -46, -42, -42, -38, -41, -41, -42, -46 };
//    	float y[] = { -95, -95, -95, -100 };
//        float x[] = { 2, 3, 4, 7 };
        float y[] = { -68, -65, -71, -77, -73, -87 };
        float x[] = { 0, 1.6f, 3.218f, 4.811f, 6.392f, 7.984f };
//        float y[] = { -50, -50, -50, -50, -54, -54, -52, -52, -60, -60, -62, -62, -66 };
//        float x[] = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};
        float res[] = getLsm(x, y);
        System.out.println("y=" + res[0] + " + " + res[1] + "*x");
        System.out.println("SSR=" + res[2]);
        
        /**
         * y=-64.55377 + -2.236092*x
			SSR=80.379364
         */
    }
}