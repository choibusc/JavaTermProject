package movie.dao;

import movie.db.JDBCConnector;
import movie.model.Distributor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DistributorDAO {

    public static final String[] SEARCH_NAMES = { "배급사코드", "배급사명", "담당자명" };
    private static final String[] SEARCH_COLUMNS = { "distributor_id", "distributor_name", "manager_name" };

    private static final String SELECT = "SELECT distributor_id, distributor_name, phone, address, manager_name FROM distributor";

    private Distributor mapRow(ResultSet rs) throws SQLException {
        return new Distributor(
                rs.getString(1),
                rs.getString(2),
                rs.getString(3),
                rs.getString(4),
                rs.getString(5)
        );
    }

    public List<Distributor> findAll() throws SQLException {
        String sql = SELECT + " ORDER BY distributor_id";
        List<Distributor> list = new ArrayList<>();

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

    public List<Distributor> search(int field, String keyword) throws SQLException {
        String sql = SELECT + " WHERE UPPER(" + SEARCH_COLUMNS[field] + ") LIKE UPPER(?) ORDER BY distributor_id";
        List<Distributor> list = new ArrayList<>();

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

    public int insert(Distributor d) throws SQLException {
        String sql = "INSERT INTO distributor (distributor_id, distributor_name, phone, address, manager_name) VALUES (?, ?, ?, ?, ?)";

        Connection conn = JDBCConnector.getConnection();
        if (conn == null) {
            throw new SQLException("DB 연결에 실패했습니다.");
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, d.getId());
            pstmt.setString(2, d.getName());
            pstmt.setString(3, d.getPhone());
            pstmt.setString(4, d.getAddress());
            pstmt.setString(5, d.getManager());
            return pstmt.executeUpdate();
        } finally {
            conn.close();
        }
    }

    public int update(Distributor d) throws SQLException {
        String sql = "UPDATE distributor SET distributor_name = ?, phone = ?, address = ?, manager_name = ? WHERE distributor_id = ?";

        Connection conn = JDBCConnector.getConnection();
        if (conn == null) {
            throw new SQLException("DB 연결에 실패했습니다.");
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, d.getName());
            pstmt.setString(2, d.getPhone());
            pstmt.setString(3, d.getAddress());
            pstmt.setString(4, d.getManager());
            pstmt.setString(5, d.getId());
            return pstmt.executeUpdate();
        } finally {
            conn.close();
        }
    }

    public int delete(String id) throws SQLException {
        String sql = "DELETE FROM distributor WHERE distributor_id = ?";

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