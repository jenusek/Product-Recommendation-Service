package pl.cobaltan.recomendationservice;

public class Product {
    private String productSKU;
    private double matchingScore;
    private int uniquePurchases;

    public Product(String productSKU, int uniquePurchases) {
        this.productSKU = productSKU;
        this.uniquePurchases = uniquePurchases;
        this.matchingScore = -1;
    }

    public Product(String productSKU, double matchingScore) {
        this.productSKU = productSKU;
        this.uniquePurchases = -1;
        this.matchingScore = matchingScore;
    }

    public String getProductSKU() {
        return productSKU;
    }

    public double getMatchingScore() {
        return matchingScore;
    }

    public int getUniquePurchases() {
        return uniquePurchases;
    }

    public void setMatchingScore(double matchingScore) {
        this.matchingScore = matchingScore;
    }

    public void setUniquePurchases(int uniquePurchases) {
        this.uniquePurchases = uniquePurchases;
    }
}
