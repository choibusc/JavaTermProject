package movie.ui;

import movie.dao.GenreDAO;
import movie.model.Genre;

import javax.swing.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GenrePanel extends CrudPanel {

    private static final long serialVersionUID = 1L;

    private final GenreDAO dao = new GenreDAO();
    private List<Genre> data = new ArrayList<>();

    private final JTextField tfId = new JTextField(14);
    private final JTextField tfName = new JTextField(14);
    private final JTextField tfDesc = new JTextField(14);

    public GenrePanel() {
        super(new String[] { "장르코드", "장르명", "장르설명" }, GenreDAO.SEARCH_NAMES);
        addField("장르코드 *", tfId);
        addField("장르명 *", tfName);
        addField("장르설명", tfDesc);
    }

    @Override
    protected void loadAll() throws SQLException {
        show(dao.findAll());
    }

    @Override
    protected void search(int field, String keyword) throws SQLException {
        show(dao.search(field, keyword));
    }

    private void show(List<Genre> list) {
        data = list;
        List<Object[]> rows = new ArrayList<>();
        for (Genre g : list) {
            rows.add(new Object[] { g.getId(), g.getName(), g.getDesc() });
        }
        setRows(rows);
    }

    @Override
    protected void onSelect(int modelRow) {
        Genre g = data.get(modelRow);
        tfId.setText(g.getId());
        tfName.setText(g.getName());
        tfDesc.setText(g.getDesc() == null ? "" : g.getDesc());
    }

    @Override
    protected void clearForm() {
        tfId.setText("");
        tfName.setText("");
        tfDesc.setText("");
    }

    @Override
    protected void onModeChanged(boolean editing) {
        tfId.setEnabled(!editing);
    }

    private Genre readForm() {
        return new Genre(
                UIUtil.required(tfId.getText(), "장르코드", 20),
                UIUtil.required(tfName.getText(), "장르명", 50),
                UIUtil.optional(tfDesc.getText(), "장르설명", 200));
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
