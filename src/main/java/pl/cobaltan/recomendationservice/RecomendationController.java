package pl.cobaltan.recomendationservice;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
public class RecomendationController {

    @RequestMapping(value = "/item_based_recomendation", method = RequestMethod.GET)
    public Recomendation item_based_recomendation() {
        return new Recomendation(Arrays.asList("XS", "XD"));
    }

    @RequestMapping(value = "/user_based_recomendation", method = RequestMethod.GET)
    public Recomendation user_based_recomendation(@RequestParam(value = "userId") String userId) {
        return new Recomendation(Arrays.asList(userId, userId,userId,userId));
    }
}
