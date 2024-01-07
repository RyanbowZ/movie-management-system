public class Movie {
    private int movieID;
    private String movieName;
    private int categoryID;
    private double rate=3.0;
    private String review;

    public int getMovieID() {
        return movieID;
    }

    public String getMovieName() {
        return movieName;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public double getRate() {
        return rate;
    }

    public void setMovieID(int movieID) {
        this.movieID = movieID;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
