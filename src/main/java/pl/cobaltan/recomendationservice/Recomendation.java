package pl.cobaltan.recomendationservice;

import java.util.ArrayList;
import java.util.List;

public class Recomendation {

    private final List<String> brands;

    public Recomendation(List<String> brands) {
        this.brands = brands;
    }

    public List<String> getBrands() {
        return brands;
    }
}
