package pl.cobaltan.recomendationservice.Recomendationv2;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.sqrt;

class Utils {

    static double cosineSimilarity(int[] a, int[] b) {
        double[] numericA = new double[a.length];
        double[] numericB = new double[b.length];
        assert a.length != b.length;
        for (int i = 0; i < a.length; i++) {
            numericA[i] = a[i];
            numericB[i] = b[i];
        }
        return cosineSimilarity(numericA, numericB);
    }

    static double cosineSimilarity(double[] a, double[] b) {
        double numerator = 0;
        double denominatorA = 0;
        double denominatorB = 0;
        for (int i = 0; i < a.length; i++) {
            numerator += a[i] * b[i];
            denominatorA += a[i] * a[i];
            denominatorB += b[i] * b[i];
        }
        denominatorA = sqrt(denominatorA);
        denominatorB = sqrt(denominatorB);

        return numerator / denominatorA / denominatorB;
    }

    static String[] removeItemsUserAlreadyBought(String[] result, int[] itemsBought, List<String> productsSKUs) {
        List<String> list = new ArrayList<>();

        for (String p: result) {
            if (itemsBought[productsSKUs.indexOf(p)] == 0)
                list.add(p);
        }

        String[] listArray = new String[list.size()];
        list.toArray(listArray);
        return listArray;
    }

}
