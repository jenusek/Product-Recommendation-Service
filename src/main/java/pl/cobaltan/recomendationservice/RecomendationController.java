package pl.cobaltan.recomendationservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.cobaltan.recomendationservice.Recomendationv2.BestSellersRecomendation;
import pl.cobaltan.recomendationservice.Recomendationv2.ItemBasedRecomendation;
import pl.cobaltan.recomendationservice.Recomendationv2.UserBasedRecomendation;

@RestController
public class RecomendationController {

    @RequestMapping(value = "/item_based_recomendation", method = RequestMethod.GET)
    public String[] itemBasedRecomendation(@RequestParam(value = "userId") String userId) {
        AnalyticsService service = new AnalyticsService(System.getProperty("user.dir"));
        return new ItemBasedRecomendation(userId).getRecomendation(service);
    }

    @RequestMapping(value = "/user_based_recomendation", method = RequestMethod.GET)
    public String[] userBasedRecomendation(@RequestParam(value = "userId") String userId) {
        AnalyticsService service = new AnalyticsService(System.getProperty("user.dir"));
        return new UserBasedRecomendation(userId).getRecomendation(service);
    }

    @RequestMapping(value = "/best_sellers", method = RequestMethod.GET)
    public String[] getBestSellers() {
        AnalyticsService service = new AnalyticsService(System.getProperty("user.dir"));
        return new BestSellersRecomendation().getRecomendation(service);
    }
}
