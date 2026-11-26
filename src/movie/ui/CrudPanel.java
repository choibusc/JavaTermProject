package movie.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public abstract class CrudPanel extends JPanel implements Refreshable {

    private static final long serialVersionUID = 1L;

    protected interface SqlAction {
        void run() throws SQLException;
    }

    protected final DefaultTableModel model;
    protected final JTable table;

    private final JComboBox<String> cbSearch;
    private final JTextField tfSearch = new JTextField(18);
    private final JLabel lblCount = new JLabel("총 0건");
    private final JPanel form = new JPanel(new GridBagLayout());
    private int fieldCount = 0;

    protected final JButton btnAdd = new JButton("등록");
    protected final JButton btnUpdate = new JButton("수정");
    protected final JButton btnDelete = new JButton("삭제");
    protected final JButton btnClear = new JButton("초기화");

    protected CrudPanel(String[] columns, String[] searchNames) {
        super(new BorderLayout(0, 10));
        setBorder(new EmptyBorder(12, 14, 12, 14));

        model = UIUtil.nonEditableModel(columns);
        table = UIUtil.createTable(model);
        cbSearch = new JComboBox<>(searchNames);

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        JButton btnSearch = new JButton("검색");
        JButton btnAll = new JButton("전체보기");
        searchBar.add(new JLabel("검색 항목"));
        searchBar.add(cbSearch);
        searchBar.add(tfSearch);
        searchBar.add(btnSearch);
        searchBar.add(btnAll);
        JPanel north = new JPanel(new BorderLayout());
        north.add(searchBar, BorderLayout.WEST);
        north.add(lblCount, BorderLayout.EAST);

        form.setBorder(BorderFactory.createTitledBorder("상세 정보 (목록에서 행을 선택하면 수정/삭제할 수 있습니다)"));
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttons.add(btnAdd);
        buttons.add(btnUpdate);
        buttons.add(btnDelete);
        buttons.add(btnClear);
        JPanel south = new JPanel(new BorderLayout(0, 8));
        south.add(form, BorderLayout.CENTER);
        south.add(buttons, BorderLayout.SOUTH);

        add(north, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        btnSearch.addActionListener(e -> doSearch());
        tfSearch.addActionListener(e -> doSearch());
        btnAll.addActionListener(e -> {
            tfSearch.setText("");
            reload();
        });
        btnAdd.addActionListener(e -> run(this::insertRow, "등록되었습니다."));
        btnUpdate.addActionListener(e -> run(this::updateRow, "수정되었습니다."));
        btnDelete.addActionListener(e -> {
            if (UIUtil.confirm(this, "선택한 데이터를 삭제하시겠습니까?")) {
                run(this::deleteRow, "삭제되었습니다.");
            }
        });
        btnClear.addActionListener(e -> resetForm());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int viewRow = table.getSelectedRow();
                if (viewRow >= 0) {
                    onSelect(table.convertRowIndexToModel(viewRow));
                    setEditing(true);
                }
            }
        });

        btnAdd.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    protected abstract void loadAll() throws SQLException;

    protected abstract void search(int field, String keyword) throws SQLException;

    protected abstract void onSelect(int modelRow);

    protected abstract void clearForm();

    protected abstract void insertRow() throws SQLException;

    protected abstract void updateRow() throws SQLException;

    protected abstract void deleteRow() throws SQLException;

    protected void onModeChanged(boolean editing) {
    }

    protected void reloadReferences() throws SQLException {
    }

    protected void addField(String label, JComponent comp) {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 8, 5, 8);
        g.anchor = GridBagConstraints.WEST;
        int col = (fieldCount % 2) * 2;
        g.gridx = col;
        g.gridy = fieldCount / 2;
        form.add(new JLabel(label), g);
        g.gridx = col + 1;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        form.add(comp, g);
        fieldCount++;
    }

    protected void setRows(List<Object[]> rows) {
        model.setRowCount(0);
        for (Object[] r : rows) {
            model.addRow(r);
        }
        lblCount.setText("총 " + rows.size() + "건");
    }

    protected void setEditing(boolean editing) {
        btnAdd.setEnabled(!editing);
        btnUpdate.setEnabled(editing);
        btnDelete.setEnabled(editing);
        onModeChanged(editing);
    }

    protected void resetForm() {
        table.clearSelection();
        clearForm();
        setEditing(false);
    }

    protected void reload() {
        try {
            String kw = tfSearch.getText().trim();
            if (kw.isEmpty()) {
                loadAll();
            } else {
                search(cbSearch.getSelectedIndex(), kw);
            }
        } catch (SQLException e) {
            UIUtil.error(this, friendlyMessage(e));
        }
    }

    private void doSearch() {
        reload();
    }

    private void run(SqlAction action, String okMessage) {
        try {
            action.run();
        } catch (IllegalArgumentException e) {
            UIUtil.warn(this, e.getMessage());
            return;
        } catch (SQLException e) {
            UIUtil.error(this, friendlyMessage(e));
            return;
        }
        resetForm();
        reload();
        UIUtil.info(this, okMessage);
    }

    @Override
    public void refresh() {
        try {
            reloadReferences();
        } catch (SQLException e) {
            UIUtil.error(this, friendlyMessage(e));
            return;
        }
        reload();
    }

    private String friendlyMessage(SQLException e) {
        int code = e.getErrorCode();
        if (code == 1) {
            return "이미 존재하는 코드(기본키)입니다.";
        } else if (code == 2292) {
            return "다른 테이블에서 참조 중인 데이터라 삭제할 수 없습니다.";
        } else if (code == 2291) {
            return "참조하는 상위 코드(배급사/장르/회원/영화)가 존재하지 않습니다.";
        } else if (code == 1400 || code == 1407) {
            return "필수 입력 항목이 비어 있습니다.";
        }
        String msg = e.getMessage();
        return (msg == null || msg.isBlank()) ? "데이터베이스 처리 중 오류가 발생했습니다." : msg;
    }
}