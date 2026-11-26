package movie.model;

public class Movie {
    private String id;
    private String title;
    private String director;
    private int runningTime;
    private int ticketPrice;
    private String distributorId;
    private String genreId;
    private String distributorName;
    private String genreName;

    public Movie(String id, String title, String director, int runningTime, int ticketPrice,
            String distributorId, String genreId) {
        this.id = id;
        this.title = title;
        this.director = director;
        this.runningTime = runningTime;
        this.ticketPrice = ticketPrice;
        this.distributorId = distributorId;
        this.genreId = genreId;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDirector() { return director; }
    public int getRunningTime() { return runningTime; }
    public int getTicketPrice() { return ticketPrice; }
    public String getDistributorId() { return distributorId; }
    public String getGenreId() { return genreId; }
    public String getDistributorName() { return distributorName; }
    public String getGenreName() { return genreName; }

    public void setNames(String distributorName, String genreName) {
        this.distributorName = distributorName;
        this.genreName = genreName;
    }
}
