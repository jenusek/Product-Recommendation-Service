package pl.cobaltan.recomendationservice.Recomendationv2;

import com.google.api.services.analyticsreporting.v4.model.DateRangeValues;
import com.google.api.services.analyticsreporting.v4.model.Report;
import com.google.api.services.analyticsreporting.v4.model.ReportRow;
import pl.cobaltan.recomendationservice.AnalyticsService;
import pl.cobaltan.recomendationservice.Product;

import java.util.ArrayList;
import java.util.List;

public class BestSellersRecomendation implements Recomendation {
    private int numberOfBestSellers = 10;

    @Override
    public String[] getRecomendation(AnalyticsService service) {
        Report report = service.getReport(new String[]{"ga:uniquePurchases"}, new String[]{"ga:productSku"}).getReports().get(0);
        List<ReportRow> rows = report.getData().getRows();

        List<Product> products = new ArrayList<>();

        for (ReportRow row: rows) {
            List<String> dimensions = row.getDimensions();
            List<DateRangeValues> metrics = row.getMetrics();

            String productSKU = dimensions.get(0);
            int uniquePurchases = Integer.parseInt(metrics.get(0).getValues().get(0));
            products.add(new Product(productSKU, uniquePurchases));
        }

        products.sort((o1, o2) -> o2.getUniquePurchases() - o1.getUniquePurchases());

        int number = numberOfBestSellers;
        if (numberOfBestSellers > products.size())
            number = products.size();

        String[] result = new String[number];
        for (int i = 0; i < result.length; i++)
            result[i] = products.get(i).getProductSKU();

        return result;

    }

    public void setNumberOfBestSellers(int numberOfBestSellers) {
        this.numberOfBestSellers = numberOfBestSellers;
    }
}
