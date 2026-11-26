package movie.dao;

import movie.db.JDBCConnector;
import movie.model.Reservation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    public static final String[] SEARCH_NAMES = { "예매번호", "회원아이디", "회원명", "영화제목" };
    private static final String[] SEARCH_COLUMNS = { "r.reservation_id", "r.member_id", "mb.name", "mv.title" };

    private static final String SELECT = "SELECT r.reservation_id, r.member_id, mb.name, r.movie_id, mv.title, "
            + "r.head_count, r.watch_date, r.reserve_date, r.head_count * mv.ticket_price "
            + "FROM reservation r "
            + "JOIN member mb ON r.member_id = mb.member_id "
            + "JOIN movie mv ON r.movie_id = mv.movie_id";

    private Reservation mapRow(ResultSet rs) throws SQLException {
        Reservation r = new Reservation(
                rs.getString(1),
                rs.getString(2),
                rs.getString(4),
                rs.getInt(6),
                rs.getDate(7),
                rs.getDate(8)
        );
        r.setViewInfo(rs.getString(3), rs.getString(5), rs.getInt(9));
        return r;
    }

    private Date toSqlDate(java.util.Date d) {
        if (d == null) {
            return null;
        }
        return (d instanceof Date) ? (Date) d : new Date(d.getTime());
    }

    public List<Reservation> findAll() throws SQLException {
        String sql = SELECT + " ORDER BY r.reservation_id";
        List<Reservation> list = new ArrayList<>();

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

    public List<Reservation> search(int field, String keyword) throws SQLException {
        String sql = SELECT + " WHERE UPPER(" + SEARCH_COLUMNS[field] + ") LIKE UPPER(?) ORDER BY r.reservation_id";
        List<Reservation> list = new ArrayList<>();

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

    /** 다음 예매번호 생성: R0001, R0002 ... */
    public String nextId() throws SQLException {
        String sql = "SELECT NVL(MAX(TO_NUMBER(SUBSTR(reservation_id, 2))), 0) + 1 FROM reservation "
                + "WHERE REGEXP_LIKE(reservation_id, '^R[0-9]+$')";
        int nextNum = 1;

        Connection conn = JDBCConnector.getConnection();
        if (conn == null) {
            throw new SQLException("DB 연결에 실패했습니다.");
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                nextNum = rs.getInt(1);
            }
        } finally {
            conn.close();
        }
        return String.format("R%04d", nextNum);
    }

    public int insert(Reservation r) throws SQLException {
        String sql = "INSERT INTO reservation (reservation_id, member_id, movie_id, head_count, watch_date, reserve_date) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = JDBCConnector.getConnection();
        if (conn == null) {
            throw new SQLException("DB 연결에 실패했습니다.");
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, r.getId());
            pstmt.setString(2, r.getMemberId());
            pstmt.setString(3, r.getMovieId());
            pstmt.setInt(4, r.getHeadCount());
            pstmt.setDate(5, toSqlDate(r.getWatchDate()));
            pstmt.setDate(6, toSqlDate(r.getReserveDate()));
            return pstmt.executeUpdate();
        } finally {
            conn.close();
        }
    }

    public int update(Reservation r) throws SQLException {
        String sql = "UPDATE reservation SET member_id = ?, movie_id = ?, head_count = ?, watch_date = ?, reserve_date = ? "
                + "WHERE reservation_id = ?";

        Connection conn = JDBCConnector.getConnection();
        if (conn == null) {
            throw new SQLException("DB 연결에 실패했습니다.");
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, r.getMemberId());
            pstmt.setString(2, r.getMovieId());
            pstmt.setInt(3, r.getHeadCount());
            pstmt.setDate(4, toSqlDate(r.getWatchDate()));
            pstmt.setDate(5, toSqlDate(r.getReserveDate()));
            pstmt.setString(6, r.getId());
            return pstmt.executeUpdate();
        } finally {
            conn.close();
        }
    }

    public int delete(String id) throws SQLException {
        String sql = "DELETE FROM reservation WHERE reservation_id = ?";

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

    /** 영화별 예매 통계: 영화코드, 제목, 예매건수, 총인원, 매출액 */
    public List<Object[]> statsByMovie() throws SQLException {
        String sql = "SELECT mv.movie_id, mv.title, COUNT(r.reservation_id), NVL(SUM(r.head_count), 0), "
                + "NVL(SUM(r.head_count * mv.ticket_price), 0) "
                + "FROM movie mv LEFT JOIN reservation r ON mv.movie_id = r.movie_id "
                + "GROUP BY mv.movie_id, mv.title ORDER BY 5 DESC, mv.movie_id";
        List<Object[]> list = new ArrayList<>();

        Connection conn = JDBCConnector.getConnection();
        if (conn == null) {
            throw new SQLException("DB 연결에 실패했습니다.");
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[] {
                        rs.getString(1),
                        rs.getString(2),
                        rs.getInt(3),
                        rs.getInt(4),
                        rs.getInt(5)
                });
            }
        } finally {
            conn.close();
        }
        return list;
    }
}