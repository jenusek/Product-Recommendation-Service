package pl.cobaltan.recomendationservice.Recomendationv2;

import com.google.api.services.analyticsreporting.v4.model.DateRangeValues;
import com.google.api.services.analyticsreporting.v4.model.Report;
import com.google.api.services.analyticsreporting.v4.model.ReportRow;
import pl.cobaltan.recomendationservice.AnalyticsService;
import pl.cobaltan.recomendationservice.Product;

import java.util.ArrayList;
import java.util.List;

public class UserBasedRecomendation implements Recomendation {

    private String currentUserID;

    public UserBasedRecomendation(String currentUserID) {
        this.currentUserID = currentUserID;
    }


    @Override
    public String[] getRecomendation(AnalyticsService service) {
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

        if (!userIDs.contains(currentUserID)) {
            userIDs.add(currentUserID);
        }

        int[][] matrix = new int[userIDs.size()][productSKUs.size()];

        for (int i = 0; i < matrix.length; i++)
            for (int j = 0; j < matrix[i].length; j++)
                matrix[i][j] = 0;

        for (ReportRow row: rows) {
            List<String> dimensions = row.getDimensions();
            List<DateRangeValues> metrics = row.getMetrics();

            String productSKU = dimensions.get(0);
            String userID = dimensions.get(1);
            int uniquePurchases = Integer.parseInt(metrics.get(0).getValues().get(0));

            matrix[userIDs.indexOf(userID)][productSKUs.indexOf(productSKU)] = uniquePurchases;
        }

        Report transactionsReport = service.getReport(new String[]{"ga:transactions"}, new String[]{"ga:dimension1"}).getReports().get(0);
        List<ReportRow> transactionsRows = transactionsReport.getData().getRows();

        int[] transactionsPerUser = new int[userIDs.size()];
        for (ReportRow row: transactionsRows) {
            List<String> dimensions = row.getDimensions();
            List<DateRangeValues> metrics = row.getMetrics();
            transactionsPerUser[userIDs.indexOf(dimensions.get(0))] = Integer.parseInt(metrics.get(0).getValues().get(0));
        }

        double[][] normalizedMatrix = new double[matrix.length][matrix[0].length];
        for(int i = 0; i < matrix.length; i++)
            for(int j = 0; j < matrix[i].length; j++)
                normalizedMatrix[i][j] = matrix[i][j] * transactionsPerUser[i];

        double[] machingScore = new double[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            machingScore[i] = Utils.cosineSimilarity(normalizedMatrix[i], normalizedMatrix[userIDs.indexOf(currentUserID)]);
        }

        double[] recomendationScore = new double[matrix[0].length];
        for (int i = 0; i < recomendationScore.length; i++) {
            double numerator = 0;
            double denominator = 0;
            for (int j = 0; j < matrix.length; j++) {
                numerator += machingScore[j] * normalizedMatrix[j][i];
                denominator += machingScore[j];
            }
            recomendationScore[i] += numerator/denominator;
        }

        List<Product> products = new ArrayList<>();
        for (int i = 0; i < recomendationScore.length; i++) {
            products.add(new Product(productSKUs.get(i), recomendationScore[i]));
        }
        products.sort((o1, o2) -> {
            if (o2.getMatchingScore() - o1.getMatchingScore() > 0)
                return 1;
            else if (o2.getMatchingScore() - o1.getMatchingScore() < 0)
                return -1;
            else
                return 0;
        });

        String[] result = new String[products.size()];
        for (int i =0; i < products.size(); i++) {
            result[i] = products.get(i).getProductSKU();
        }

        result = Utils.removeItemsUserAlreadyBought(result, matrix[userIDs.indexOf(currentUserID)], productSKUs);

        return result;
    }
}
