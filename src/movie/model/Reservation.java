package movie.model;

import java.sql.Date;

public class Reservation {
    private String id;
    private String memberId;
    private String movieId;
    private int headCount;
    private Date watchDate;
    private Date reserveDate;

    private String memberName;
    private String movieTitle;
    private int amount;

    public Reservation(String id, String memberId, String movieId, int headCount, Date watchDate,
            Date reserveDate) {
        this.id = id;
        this.memberId = memberId;
        this.movieId = movieId;
        this.headCount = headCount;
        this.watchDate = watchDate;
        this.reserveDate = reserveDate;
    }

    public String getId() { return id; }
    public String getMemberId() { return memberId; }
    public String getMovieId() { return movieId; }
    public int getHeadCount() { return headCount; }
    public Date getWatchDate() { return watchDate; }
    public Date getReserveDate() { return reserveDate; }
    public String getMemberName() { return memberName; }
    public String getMovieTitle() { return movieTitle; }
    public int getAmount() { return amount; }

    public void setViewInfo(String memberName, String movieTitle, int amount) {
        this.memberName = memberName;
        this.movieTitle = movieTitle;
        this.amount = amount;
    }
}
