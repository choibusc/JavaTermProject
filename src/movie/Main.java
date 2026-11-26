package movie;

import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.SwingUtilities;

import movie.db.JDBCConnector;
import movie.ui.MainFrame;
import movie.ui.UIUtil;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UIUtil.init();

            try (Connection c = JDBCConnector.getConnection()) {
                if (c == null || c.isClosed()) {
                    throw new SQLException("DB 연결에 실패했습니다.");
                }
            } catch (SQLException e) {
                UIUtil.error(null, "DB 연결 오류: " + e.getMessage()
                        + "\n\nJDBCConnector의 URL / USER_NAME / PASSWORD 를 확인하세요.");
                System.exit(1);
            }

            new MainFrame().setVisible(true);
        });
    }
}