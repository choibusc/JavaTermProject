package movie.ui;

import movie.dao.ReservationDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class StatsPanel extends JPanel implements Refreshable {

    private static final long serialVersionUID = 1L;

    private final ReservationDAO dao = new ReservationDAO();
    private final DefaultTableModel model = UIUtil
            .nonEditableModel(new String[] { "영화코드", "제목", "예매 건수", "총 인원", "매출액(원)" });
    private final JTable table = UIUtil.createTable(model);
    private final JLabel lblTotal = new JLabel(" ");

    public StatsPanel() {
        super(new BorderLayout(0, 10));
        setBorder(new EmptyBorder(12, 14, 12, 14));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JButton btn = new JButton("새로고침");
        btn.addActionListener(e -> refresh());
        top.add(new JLabel("영화별 예매 현황 (매출액 높은 순)"));
        top.add(btn);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(lblTotal, BorderLayout.SOUTH);
    }

    @Override
    public void refresh() {
        try {
            List<Object[]> rows = dao.statsByMovie();
            model.setRowCount(0);
            int count = 0;
            int people = 0;
            long sales = 0;
            for (Object[] r : rows) {
                model.addRow(r);
                count += (Integer) r[2];
                people += (Integer) r[3];
                sales += (Integer) r[4];
            }
            lblTotal.setText(String.format("전체 합계  |  예매 %,d건  |  관람 인원 %,d명  |  매출액 %,d원", count, people, sales));
        } catch (SQLException e) {
            String msg = e.getMessage();
            UIUtil.error(this, (msg == null || msg.isBlank()) ? "통계 조회 중 오류가 발생했습니다." : msg);
        }
    }
}