package movie.ui;

import movie.dao.DistributorDAO;
import movie.model.Distributor;

import javax.swing.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DistributorPanel extends CrudPanel {

    private static final long serialVersionUID = 1L;

    private final DistributorDAO dao = new DistributorDAO();
    private List<Distributor> data = new ArrayList<>();

    private final JTextField tfId = new JTextField(14);
    private final JTextField tfName = new JTextField(14);
    private final JTextField tfPhone = new JTextField(14);
    private final JTextField tfAddress = new JTextField(14);
    private final JTextField tfManager = new JTextField(14);

    public DistributorPanel() {
        super(new String[] { "배급사코드", "배급사명", "전화번호", "주소", "담당자명" }, DistributorDAO.SEARCH_NAMES);
        addField("배급사코드 *", tfId);
        addField("배급사명 *", tfName);
        addField("전화번호", tfPhone);
        addField("주소", tfAddress);
        addField("담당자명", tfManager);
    }

    @Override
    protected void loadAll() throws SQLException {
        show(dao.findAll());
    }

    @Override
    protected void search(int field, String keyword) throws SQLException {
        show(dao.search(field, keyword));
    }

    private void show(List<Distributor> list) {
        data = list;
        List<Object[]> rows = new ArrayList<>();
        for (Distributor d : list) {
            rows.add(new Object[] { d.getId(), d.getName(), d.getPhone(), d.getAddress(), d.getManager() });
        }
        setRows(rows);
    }

    @Override
    protected void onSelect(int modelRow) {
        Distributor d = data.get(modelRow);
        tfId.setText(d.getId());
        tfName.setText(d.getName());
        tfPhone.setText(d.getPhone() == null ? "" : d.getPhone());
        tfAddress.setText(d.getAddress() == null ? "" : d.getAddress());
        tfManager.setText(d.getManager() == null ? "" : d.getManager());
    }

    @Override
    protected void clearForm() {
        tfId.setText("");
        tfName.setText("");
        tfPhone.setText("");
        tfAddress.setText("");
        tfManager.setText("");
    }

    @Override
    protected void onModeChanged(boolean editing) {
        tfId.setEnabled(!editing);
    }

    private Distributor readForm() {
        return new Distributor(
                UIUtil.required(tfId.getText(), "배급사코드", 20),
                UIUtil.required(tfName.getText(), "배급사명", 50),
                UIUtil.optional(tfPhone.getText(), "전화번호", 20),
                UIUtil.optional(tfAddress.getText(), "주소", 100),
                UIUtil.optional(tfManager.getText(), "담당자명", 30));
    }

    @Override
    protected void insertRow() throws SQLException {
        dao.insert(readForm());
    }

    @Override
    protected void updateRow() throws SQLException {
        dao.update(readForm());
    }

    @Override
    protected void deleteRow() throws SQLException {
        dao.delete(tfId.getText().trim());
    }
}
