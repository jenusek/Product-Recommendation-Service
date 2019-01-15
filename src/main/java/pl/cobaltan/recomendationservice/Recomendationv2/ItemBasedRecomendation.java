package pl.cobaltan.recomendationservice.Recomendationv2;

import com.google.api.services.analyticsreporting.v4.model.DateRangeValues;
import com.google.api.services.analyticsreporting.v4.model.Report;
import com.google.api.services.analyticsreporting.v4.model.ReportRow;
import pl.cobaltan.recomendationservice.AnalyticsService;
import pl.cobaltan.recomendationservice.Product;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ItemBasedRecomendation implements Recomendation {

    private String currentUserID;

    public ItemBasedRecomendation(String currentUserID) {
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

        int[][] matrix = new int[productSKUs.size()][userIDs.size()];

        for (int i = 0; i < matrix.length; i++)
            for (int j = 0; j < matrix[i].length; j++)
                matrix[i][j] = 0;

        for (ReportRow row: rows) {
            List<String> dimensions = row.getDimensions();
            List<DateRangeValues> metrics = row.getMetrics();

            String productSKU = dimensions.get(0);
            String userID = dimensions.get(1);
            int uniquePurchases = Integer.parseInt(metrics.get(0).getValues().get(0));

            matrix[productSKUs.indexOf(productSKU)][userIDs.indexOf(userID)] = uniquePurchases;
        }

        List<Product> products = new ArrayList<>();

        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i][userIDs.indexOf(currentUserID)] == 0) {
                double[] machingScore = new double[matrix.length];
                for (int j = 0; j < matrix.length; j++) {
                    machingScore[j] = Utils.cosineSimilarity(matrix[j], matrix[i]);
                }

                double numerator = 0;
                double denominator = 0;
                for (int j = 0; j < machingScore.length; j++) {
                    if(machingScore[j] > 0) {
                        numerator += machingScore[j] * matrix[j][userIDs.indexOf(currentUserID)];
                        denominator += machingScore[j];
                    }
                }
                products.add(new Product(productSKUs.get(i), numerator/denominator));
            }
        }

        products.sort(new Comparator<Product>() {
            @Override
            public int compare(Product o1, Product o2) {
                if (o2.getMatchingScore() - o1.getMatchingScore() > 0)
                    return 1;
                else if (o2.getMatchingScore() - o1.getMatchingScore() < 0)
                    return -1;
                else
                    return 0;
            }
        });

        String[] result = new String[products.size()];
        for (int i =0; i < products.size(); i++) {
            result[i] = products.get(i).getProductSKU();
        }

        return result;
    }
}
