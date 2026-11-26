package movie.dao;

import movie.db.JDBCConnector;
import movie.model.Member;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberDAO {

    public static final String[] SEARCH_NAMES = { "회원아이디", "이름", "등급" };
    private static final String[] SEARCH_COLUMNS = { "member_id", "name", "grade" };

    private static final String SELECT = "SELECT member_id, password, name, age, grade, point FROM member";

    private Member mapRow(ResultSet rs) throws SQLException {
        int age = rs.getInt(4);
        Integer ageObj = rs.wasNull() ? null : Integer.valueOf(age);
        return new Member(
                rs.getString(1),
                rs.getString(2),
                rs.getString(3),
                ageObj,
                rs.getString(5),
                rs.getInt(6)
        );
    }

    public List<Member> findAll() throws SQLException {
        String sql = SELECT + " ORDER BY member_id";
        List<Member> list = new ArrayList<>();

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

    public List<Member> search(int field, String keyword) throws SQLException {
        String sql = SELECT + " WHERE UPPER(" + SEARCH_COLUMNS[field] + ") LIKE UPPER(?) ORDER BY member_id";
        List<Member> list = new ArrayList<>();

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

    public int insert(Member m) throws SQLException {
        String sql = "INSERT INTO member (member_id, password, name, age, grade, point) VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = JDBCConnector.getConnection();
        if (conn == null) {
            throw new SQLException("DB 연결에 실패했습니다.");
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, m.getId());
            pstmt.setString(2, m.getPassword());
            pstmt.setString(3, m.getName());
            if (m.getAge() == null) {
                pstmt.setNull(4, Types.INTEGER);
            } else {
                pstmt.setInt(4, m.getAge());
            }
            pstmt.setString(5, m.getGrade());
            pstmt.setInt(6, m.getPoint());
            return pstmt.executeUpdate();
        } finally {
            conn.close();
        }
    }

    public int update(Member m) throws SQLException {
        String sql = "UPDATE member SET password = ?, name = ?, age = ?, grade = ?, point = ? WHERE member_id = ?";

        Connection conn = JDBCConnector.getConnection();
        if (conn == null) {
            throw new SQLException("DB 연결에 실패했습니다.");
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, m.getPassword());
            pstmt.setString(2, m.getName());
            if (m.getAge() == null) {
                pstmt.setNull(3, Types.INTEGER);
            } else {
                pstmt.setInt(3, m.getAge());
            }
            pstmt.setString(4, m.getGrade());
            pstmt.setInt(5, m.getPoint());
            pstmt.setString(6, m.getId());
            return pstmt.executeUpdate();
        } finally {
            conn.close();
        }
    }

    public int delete(String id) throws SQLException {
        String sql = "DELETE FROM member WHERE member_id = ?";

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