package pl.cobaltan.recomendationservice.Recomendation;

import pl.cobaltan.recomendationservice.AnalyticsService;

public interface Recomendation {
    String[] getRecomendation(AnalyticsService service);
}
