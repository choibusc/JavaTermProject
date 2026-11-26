package movie.dao;

import movie.db.JDBCConnector;
import movie.model.Genre;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GenreDAO {

    public static final String[] SEARCH_NAMES = { "장르코드", "장르명" };
    private static final String[] SEARCH_COLUMNS = { "genre_id", "genre_name" };

    private static final String SELECT = "SELECT genre_id, genre_name, genre_desc FROM genre";

    private Genre mapRow(ResultSet rs) throws SQLException {
        return new Genre(
                rs.getString(1),
                rs.getString(2),
                rs.getString(3)
        );
    }

    public List<Genre> findAll() throws SQLException {
        String sql = SELECT + " ORDER BY genre_id";
        List<Genre> list = new ArrayList<>();

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

    public List<Genre> search(int field, String keyword) throws SQLException {
        String sql = SELECT + " WHERE UPPER(" + SEARCH_COLUMNS[field] + ") LIKE UPPER(?) ORDER BY genre_id";
        List<Genre> list = new ArrayList<>();

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

    public int insert(Genre g) throws SQLException {
        String sql = "INSERT INTO genre (genre_id, genre_name, genre_desc) VALUES (?, ?, ?)";

        Connection conn = JDBCConnector.getConnection();
        if (conn == null) {
            throw new SQLException("DB 연결에 실패했습니다.");
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, g.getId());
            pstmt.setString(2, g.getName());
            pstmt.setString(3, g.getDesc());
            return pstmt.executeUpdate();
        } finally {
            conn.close();
        }
    }

    public int update(Genre g) throws SQLException {
        String sql = "UPDATE genre SET genre_name = ?, genre_desc = ? WHERE genre_id = ?";

        Connection conn = JDBCConnector.getConnection();
        if (conn == null) {
            throw new SQLException("DB 연결에 실패했습니다.");
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, g.getName());
            pstmt.setString(2, g.getDesc());
            pstmt.setString(3, g.getId());
            return pstmt.executeUpdate();
        } finally {
            conn.close();
        }
    }

    public int delete(String id) throws SQLException {
        String sql = "DELETE FROM genre WHERE genre_id = ?";

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