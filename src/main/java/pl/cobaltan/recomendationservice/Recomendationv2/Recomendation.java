package pl.cobaltan.recomendationservice.Recomendationv2;

import pl.cobaltan.recomendationservice.AnalyticsService;

public interface Recomendation {
    String[] getRecomendation(AnalyticsService service);
}
