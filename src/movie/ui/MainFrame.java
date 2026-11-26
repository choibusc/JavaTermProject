package movie.ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    public MainFrame() {
        super("영화 예매 관리 시스템");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("회원 관리", new MemberPanel());
        tabs.addTab("영화 관리", new MoviePanel());
        tabs.addTab("예매 관리", new ReservationPanel());
        tabs.addTab("배급사 관리", new DistributorPanel());
        tabs.addTab("장르 관리", new GenrePanel());
        tabs.addTab("예매 통계", new StatsPanel());

        tabs.addChangeListener(e -> {
            java.awt.Component c = tabs.getSelectedComponent();
            if (c instanceof Refreshable) {
                ((Refreshable) c).refresh();
            }
        });

        add(tabs);
        setMinimumSize(new Dimension(900, 600));
        setSize(1100, 720);
        setLocationRelativeTo(null);

        ((Refreshable) tabs.getSelectedComponent()).refresh();
    }
}
