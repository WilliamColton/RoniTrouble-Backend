package org.roni.ronitrouble.util;

public class MathUtil {

    public static float getCosineSimilarity(float[] x, float[] y) {
        // 1. 基础校验：维度必须一致
        if (x.length != y.length) {
            throw new IllegalArgumentException("Vector dimensions must match");
        }

        double numerator = 0D;
        double leftDenominator = 0D;
        double rightDenominator = 0D;

        // 2. 可以在一个循环里完成计算（假设长度一致）
        for (int i = 0; i < x.length; i++) {
            numerator += x[i] * y[i];
            leftDenominator += x[i] * x[i];
            rightDenominator += y[i] * y[i];
        }

        // 3. 防止分母为 0 (例如其中一个向量是零向量)
        if (leftDenominator == 0 || rightDenominator == 0) {
            return 0f;
        }

        return (float) (numerator / (Math.sqrt(leftDenominator) * Math.sqrt(rightDenominator)));
    }
}