package co.ar_smart.www.controllers.hue;

import android.graphics.Color;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Gabriel on 5/21/2016.
 */
public class PHUtils {

    private static final int CPT_BLUE = 2;
    private static final int CPT_GREEN = 1;
    private static final int CPT_RED = 0;
    private static final List<String> GAMUT_A_BULBS_LIST;
    private static final List<String> GAMUT_B_BULBS_LIST;
    private static final List<String> GAMUT_C_BULBS_LIST;
    private static final List<String> MULTI_SOURCE_LUMINAIRES;
    private static List<PointF> colorPointsDefault;
    private static List<PointF> colorPointsGamut_A;
    private static List<PointF> colorPointsGamut_B;
    private static List<PointF> colorPointsGamut_C;

    static {
        colorPointsGamut_A = new ArrayList<>();
        colorPointsGamut_B = new ArrayList<>();
        colorPointsGamut_C = new ArrayList<>();
        colorPointsDefault = new ArrayList<>();
        GAMUT_A_BULBS_LIST = new ArrayList<>();
        GAMUT_B_BULBS_LIST = new ArrayList<>();
        GAMUT_C_BULBS_LIST = new ArrayList<>();
        MULTI_SOURCE_LUMINAIRES = new ArrayList<>();
        GAMUT_A_BULBS_LIST.add("LLC001");
        GAMUT_A_BULBS_LIST.add("LLC005");
        GAMUT_A_BULBS_LIST.add("LLC006");
        GAMUT_A_BULBS_LIST.add("LLC007");
        GAMUT_A_BULBS_LIST.add("LLC010");
        GAMUT_A_BULBS_LIST.add("LLC011");
        GAMUT_A_BULBS_LIST.add("LLC012");
        GAMUT_A_BULBS_LIST.add("LLC014");
        GAMUT_A_BULBS_LIST.add("LLC013");
        GAMUT_A_BULBS_LIST.add("LST001");
        GAMUT_B_BULBS_LIST.add("LCT001");
        GAMUT_B_BULBS_LIST.add("LCT002");
        GAMUT_B_BULBS_LIST.add("LCT003");
        GAMUT_B_BULBS_LIST.add("LCT004");
        GAMUT_B_BULBS_LIST.add("LLM001");
        GAMUT_B_BULBS_LIST.add("LCT005");
        GAMUT_B_BULBS_LIST.add("LCT006");
        GAMUT_B_BULBS_LIST.add("LCT007");
        GAMUT_C_BULBS_LIST.add("LLC020");
        GAMUT_C_BULBS_LIST.add("LST002");
        MULTI_SOURCE_LUMINAIRES.add("HBL001");
        MULTI_SOURCE_LUMINAIRES.add("HBL002");
        MULTI_SOURCE_LUMINAIRES.add("HBL003");
        MULTI_SOURCE_LUMINAIRES.add("HIL001");
        MULTI_SOURCE_LUMINAIRES.add("HIL002");
        MULTI_SOURCE_LUMINAIRES.add("HEL001");
        MULTI_SOURCE_LUMINAIRES.add("HEL002");
        colorPointsGamut_A.add(new PointF(0.703f, 0.296f));
        colorPointsGamut_A.add(new PointF(0.214f, 0.709f));
        colorPointsGamut_A.add(new PointF(0.139f, 0.081f));
        colorPointsGamut_B.add(new PointF(0.674f, 0.322f));
        colorPointsGamut_B.add(new PointF(0.408f, 0.517f));
        colorPointsGamut_B.add(new PointF(0.168f, 0.041f));
        colorPointsGamut_C.add(new PointF(0.692f, 0.308f));
        colorPointsGamut_C.add(new PointF(0.17f, 0.7f));
        colorPointsGamut_C.add(new PointF(0.153f, 0.048f));
        colorPointsDefault.add(new PointF(1.0f, 0.0f));
        colorPointsDefault.add(new PointF(0.0f, 1.0f));
        colorPointsDefault.add(new PointF(0.0f, 0.0f));
    }

    public static int colorFromXY(float[] points, String model) {
        if (points == null || model == null) {
            return 0;
        }
        PointF pointF = new PointF(points[0], points[CPT_GREEN]);
        List<PointF> colorPoints = colorPointsForModel(model);
        if (!checkPointInLampsReach(pointF, colorPoints)) {
            PointF pAB = getClosestPointToPoints((PointF) colorPoints.get(0), (PointF) colorPoints.get(CPT_GREEN), pointF);
            PointF pAC = getClosestPointToPoints((PointF) colorPoints.get(CPT_BLUE), (PointF) colorPoints.get(0), pointF);
            PointF pBC = getClosestPointToPoints((PointF) colorPoints.get(CPT_GREEN), (PointF) colorPoints.get(CPT_BLUE), pointF);
            float dAB = getDistanceBetweenTwoPoints(pointF, pAB);
            float dAC = getDistanceBetweenTwoPoints(pointF, pAC);
            float dBC = getDistanceBetweenTwoPoints(pointF, pBC);
            float lowest = dAB;
            PointF closestPoint = pAB;
            if (dAC < lowest) {
                lowest = dAC;
                closestPoint = pAC;
            }
            if (dBC < lowest) {
                lowest = dBC;
                closestPoint = pBC;
            }
            pointF.x = closestPoint.x;
            pointF.y = closestPoint.y;
        }
        float x = pointF.x;
        float y = pointF.y;
        float x2 = (1.0f / y) * x;
        float z2 = (1.0f / y) * ((1.0f - x) - y);
        float r = ((1.656492f * x2) - (0.354851f * 1.0f)) - (0.255038f * z2);
        float g = (((-x2) * 0.707196f) + (1.655397f * 1.0f)) + (0.036152f * z2);
        float b = ((0.051713f * x2) - (0.121364f * 1.0f)) + (1.01153f * z2);
        if (r > b && r > g && r > 1.0f) {
            g /= r;
            b /= r;
            r = 1.0f;
        } else if (g > b && g > r && g > 1.0f) {
            r /= g;
            b /= g;
            g = 1.0f;
        } else if (b > r && b > g && b > 1.0f) {
            r /= b;
            g /= b;
            b = 1.0f;
        }
        if (r <= 0.0031308f) {
            r *= 12.92f;
        } else {
            r = (1.055f * ((float) Math.pow((double) r, 0.4166666567325592d))) - 0.055f;
        }
        if (g <= 0.0031308f) {
            g *= 12.92f;
        } else {
            g = (1.055f * ((float) Math.pow((double) g, 0.4166666567325592d))) - 0.055f;
        }
        if (b <= 0.0031308f) {
            b *= 12.92f;
        } else {
            b = (1.055f * ((float) Math.pow((double) b, 0.4166666567325592d))) - 0.055f;
        }
        if (r <= b || r <= g) {
            if (g <= b || g <= r) {
                if (b > r && b > g && b > 1.0f) {
                    r /= b;
                    g /= b;
                    b = 1.0f;
                }
            } else if (g > 1.0f) {
                r /= g;
                b /= g;
                g = 1.0f;
            }
        } else if (r > 1.0f) {
            g /= r;
            b /= r;
            r = 1.0f;
        }
        if (r < 0.0f) {
            r = 0.0f;
        }
        if (g < 0.0f) {
            g = 0.0f;
        }
        if (b < 0.0f) {
            b = 0.0f;
        }
        return Color.rgb((int) (255.0f * r), (int) (255.0f * g), (int) (255.0f * b));
    }


    private static boolean checkPointInLampsReach(PointF point, List<PointF> colorPoints) {
        if (point == null || colorPoints == null) {
            return false;
        }
        PointF red = (PointF) colorPoints.get(0);
        PointF green = (PointF) colorPoints.get(CPT_GREEN);
        PointF blue = (PointF) colorPoints.get(CPT_BLUE);
        PointF v1 = new PointF(green.x - red.x, green.y - red.y);
        PointF v2 = new PointF(blue.x - red.x, blue.y - red.y);
        PointF q = new PointF(point.x - red.x, point.y - red.y);
        float s = crossProduct(q, v2) / crossProduct(v1, v2);
        float t = crossProduct(v1, q) / crossProduct(v1, v2);
        if (s < 0.0f || t < 0.0f || s + t > 1.0f) {
            return false;
        }
        return true;
    }

    private static float getDistanceBetweenTwoPoints(PointF one, PointF two) {
        float dx = one.x - two.x;
        float dy = one.y - two.y;
        return (float) Math.sqrt((double) ((dx * dx) + (dy * dy)));
    }

    private static float crossProduct(PointF point1, PointF point2) {
        return (point1.x * point2.y) - (point1.y * point2.x);
    }

    private static List<PointF> colorPointsForModel(String model) {
        if (model == null) {
            model = " ";
        }
        if (GAMUT_B_BULBS_LIST.contains(model) || MULTI_SOURCE_LUMINAIRES.contains(model)) {
            return colorPointsGamut_B;
        }
        if (GAMUT_A_BULBS_LIST.contains(model)) {
            return colorPointsGamut_A;
        }
        if (GAMUT_C_BULBS_LIST.contains(model)) {
            return colorPointsGamut_C;
        }
        return colorPointsDefault;
    }

    private static PointF getClosestPointToPoints(PointF pointA, PointF pointB, PointF pointP) {
        if (pointA == null || pointB == null || pointP == null) {
            return null;
        }
        PointF pointAP = new PointF(pointP.x - pointA.x, pointP.y - pointA.y);
        PointF pointAB = new PointF(pointB.x - pointA.x, pointB.y - pointA.y);
        float t = ((pointAP.x * pointAB.x) + (pointAP.y * pointAB.y)) / ((pointAB.x * pointAB.x) + (pointAB.y * pointAB.y));
        if (t < 0.0f) {
            t = 0.0f;
        } else if (t > 1.0f) {
            t = 1.0f;
        }
        return new PointF(pointA.x + (pointAB.x * t), pointA.y + (pointAB.y * t));
    }
}
