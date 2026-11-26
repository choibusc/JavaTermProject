package movie.dao;

import movie.db.JDBCConnector;
import movie.model.Movie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO {

    public static final String[] SEARCH_NAMES = { "영화코드", "제목", "감독", "배급사", "장르" };
    private static final String[] SEARCH_COLUMNS = {
            "m.movie_id", "m.title", "m.director", "d.distributor_name", "g.genre_name"
    };

    private static final String SELECT =
            "SELECT m.movie_id, m.title, m.director, m.running_time, m.ticket_price, " +
                    "m.distributor_id, d.distributor_name, m.genre_id, g.genre_name " +
                    "FROM movie m " +
                    "JOIN distributor d ON m.distributor_id = d.distributor_id " +
                    "JOIN genre g ON m.genre_id = g.genre_id";

    private Movie mapRow(ResultSet rs) throws SQLException {
        Movie m = new Movie(
                rs.getString(1),
                rs.getString(2),
                rs.getString(3),
                rs.getInt(4),
                rs.getInt(5),
                rs.getString(6),
                rs.getString(8)
        );
        m.setNames(rs.getString(7), rs.getString(9));
        return m;
    }

    public List<Movie> findAll() throws SQLException {
        String sql = SELECT + " ORDER BY m.movie_id";
        List<Movie> list = new ArrayList<>();

        Connection conn = JDBCConnector.getConnection();
        if (conn == null) {
            throw new SQLException("DB 연결에 실패했습니다.");
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } finally {
            conn.close();
        }
        return list;
    }

    public List<Movie> search(int field, String keyword) throws SQLException {
        String sql = SELECT + " WHERE UPPER(" + SEARCH_COLUMNS[field] + ") LIKE UPPER(?) ORDER BY m.movie_id";
        List<Movie> list = new ArrayList<>();

        Connection conn = JDBCConnector.getConnection();
        if (conn == null) {
            throw new SQLException("DB 연결에 실패했습니다.");
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + (keyword == null ? "" : keyword) + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } finally {
            conn.close();
        }
        return list;
    }

    public int insert(Movie m) throws SQLException {
        String sql = "INSERT INTO movie (movie_id, title, director, running_time, ticket_price, distributor_id, genre_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conn = JDBCConnector.getConnection();
        if (conn == null) {
            throw new SQLException("DB 연결에 실패했습니다.");
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, m.getId());
            pstmt.setString(2, m.getTitle());
            pstmt.setString(3, m.getDirector());
            pstmt.setInt(4, m.getRunningTime());
            pstmt.setInt(5, m.getTicketPrice());
            pstmt.setString(6, m.getDistributorId());
            pstmt.setString(7, m.getGenreId());
            return pstmt.executeUpdate();
        } finally {
            conn.close();
        }
    }

    public int update(Movie m) throws SQLException {
        String sql = "UPDATE movie SET title = ?, director = ?, running_time = ?, ticket_price = ?, " +
                "distributor_id = ?, genre_id = ? WHERE movie_id = ?";

        Connection conn = JDBCConnector.getConnection();
        if (conn == null) {
            throw new SQLException("DB 연결에 실패했습니다.");
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, m.getTitle());
            pstmt.setString(2, m.getDirector());
            pstmt.setInt(3, m.getRunningTime());
            pstmt.setInt(4, m.getTicketPrice());
            pstmt.setString(5, m.getDistributorId());
            pstmt.setString(6, m.getGenreId());
            pstmt.setString(7, m.getId());
            return pstmt.executeUpdate();
        } finally {
            conn.close();
        }
    }

    public int delete(String id) throws SQLException {
        String sql = "DELETE FROM movie WHERE movie_id = ?";

        Connection conn = JDBCConnector.getConnection();
        if (conn == null) {
            throw new SQLException("DB 연결에 실패했습니다.");
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            return pstmt.executeUpdate();
        } finally {
            conn.close();
        }
    }
}