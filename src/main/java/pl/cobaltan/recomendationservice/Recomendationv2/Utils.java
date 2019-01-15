package pl.cobaltan.recomendationservice.Recomendation;

import com.google.api.services.analyticsreporting.v4.model.DateRangeValues;
import com.google.api.services.analyticsreporting.v4.model.Report;
import com.google.api.services.analyticsreporting.v4.model.ReportRow;
import pl.cobaltan.recomendationservice.AnalyticsService;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.sqrt;

public class Utils {

    public enum MatrixType {
        USER_BASED, ITEM_BASED
    }

    static ColaborativeFiltering generateMatrix(AnalyticsService service, MatrixType type) {
        Report report = service.getReport(new String[]{"ga:uniquePurchases"}, new String[]{"ga:productSku", "ga:dimension1"}).getReports().get(0);
        List<ReportRow> rows = report.getData().getRows();
        List<String> productSKUs = new ArrayList<>();
        List<String> userIDs = new ArrayList<>();

        for (ReportRow row: rows) {
            List<String> dimensions = row.getDimensions();

            String productSKU = dimensions.get(0);
            String userID = dimensions.get(1);

            if (!productSKUs.contains(productSKU))
                productSKUs.add(productSKU);

            if (!userIDs.contains(userID))
                userIDs.add(userID);
        }

        ColaborativeFiltering colaborativeFiltering;
        if (type == MatrixType.ITEM_BASED)
            colaborativeFiltering = new ColaborativeFiltering(productSKUs, userIDs);
        else
            colaborativeFiltering = new ColaborativeFiltering(userIDs, productSKUs);

        for (int i = 0; i < colaborativeFiltering.getMatrix().length; i++)
            for (int j = 0; j < colaborativeFiltering.getMatrix()[i].length; j++)
                colaborativeFiltering.getMatrix()[i][j] = 0;

        for (ReportRow row: rows) {
            List<String> dimensions = row.getDimensions();
            List<DateRangeValues> metrics = row.getMetrics();

            String productSKU = dimensions.get(0);
            String userID = dimensions.get(1);
            int uniquePurchases = Integer.parseInt(metrics.get(0).getValues().get(0));

            if (type == MatrixType.ITEM_BASED)
                colaborativeFiltering.getMatrix()[productSKUs.indexOf(productSKU)][userIDs.indexOf(userID)] = uniquePurchases;
            else
                colaborativeFiltering.getMatrix()[userIDs.indexOf(userID)][productSKUs.indexOf(productSKU)] = uniquePurchases;
        }

        return colaborativeFiltering;
    }

    static public double cosineSimilarity(int[] a, int[] b) {
        double[] numericA = new double[a.length];
        double[] numericB = new double[b.length];
        assert a.length != b.length;
        for (int i = 0; i < a.length; i++) {
            numericA[i] = a[i];
            numericB[i] = b[i];
        }
        return cosineSimilarity(numericA, numericB);
    }

    static public double cosineSimilarity(double[] a, double[] b) {
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

    static public double[][] normalization(int[][]matrix, int[] numberOfTransactions) {
        double[][] normalizedMatrix = new double[matrix.length][matrix[0].length];
        for(int i = 0; i < matrix.length; i++)
            for(int j = 0; j < matrix[i].length; j++)
                normalizedMatrix[i][j] = matrix[i][j] * numberOfTransactions[i];
        return normalizedMatrix;
    }

    static public String[] removeItemsUserAlreadyBought(String[] productsSKU, int[] itemsBought, List<String> itemsSKU) {
        List<String> list = new ArrayList<>();
        for (String prductSKU: productsSKU) {
            if (itemsBought[itemsSKU.indexOf(prductSKU)] == 0)
                list.add(prductSKU);
        }
        String[] result = new String[list.size()];
        list.toArray(result);
        return result;
    }

}
