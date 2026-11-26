package movie.ui;

import movie.dao.DistributorDAO;
import movie.dao.GenreDAO;
import movie.dao.MovieDAO;
import movie.model.Distributor;
import movie.model.Genre;
import movie.model.Movie;
import movie.ui.UIUtil.Item;

import javax.swing.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MoviePanel extends CrudPanel {

    private static final long serialVersionUID = 1L;

    private final MovieDAO dao = new MovieDAO();
    private final DistributorDAO distributorDao = new DistributorDAO();
    private final GenreDAO genreDao = new GenreDAO();
    private List<Movie> data = new ArrayList<>();

    private final JTextField tfId = new JTextField(14);
    private final JTextField tfTitle = new JTextField(14);
    private final JTextField tfDirector = new JTextField(14);
    private final JTextField tfTime = new JTextField(14);
    private final JTextField tfPrice = new JTextField(14);
    private final JComboBox<Item> cbDistributor = new JComboBox<>();
    private final JComboBox<Item> cbGenre = new JComboBox<>();

    public MoviePanel() {
        super(new String[] { "영화코드", "제목", "감독", "상영시간(분)", "티켓가격", "배급사", "장르" }, MovieDAO.SEARCH_NAMES);
        addField("영화코드 *", tfId);
        addField("제목 *", tfTitle);
        addField("감독 *", tfDirector);
        addField("상영시간(분) *", tfTime);
        addField("티켓가격(원) *", tfPrice);
        addField("배급사 *", cbDistributor);
        addField("장르 *", cbGenre);
    }

    @Override
    protected void reloadReferences() throws SQLException {
        String selD = UIUtil.selectedId(cbDistributor);
        String selG = UIUtil.selectedId(cbGenre);
        cbDistributor.removeAllItems();
        for (Distributor d : distributorDao.findAll()) {
            cbDistributor.addItem(new Item(d.getId(), d.getId() + " - " + d.getName()));
        }
        cbGenre.removeAllItems();
        for (Genre g : genreDao.findAll()) {
            cbGenre.addItem(new Item(g.getId(), g.getId() + " - " + g.getName()));
        }
        if (selD != null) {
            UIUtil.selectById(cbDistributor, selD);
        }
        if (selG != null) {
            UIUtil.selectById(cbGenre, selG);
        }
    }

    @Override
    protected void loadAll() throws SQLException {
        show(dao.findAll());
    }

    @Override
    protected void search(int field, String keyword) throws SQLException {
        show(dao.search(field, keyword));
    }

    private void show(List<Movie> list) {
        data = list;
        List<Object[]> rows = new ArrayList<>();
        for (Movie m : list) {
            rows.add(new Object[] { m.getId(), m.getTitle(), m.getDirector(), m.getRunningTime(), m.getTicketPrice(),
                    m.getDistributorName(), m.getGenreName() });
        }
        setRows(rows);
    }

    @Override
    protected void onSelect(int modelRow) {
        Movie m = data.get(modelRow);
        tfId.setText(m.getId());
        tfTitle.setText(m.getTitle());
        tfDirector.setText(m.getDirector());
        tfTime.setText(String.valueOf(m.getRunningTime()));
        tfPrice.setText(String.valueOf(m.getTicketPrice()));
        UIUtil.selectById(cbDistributor, m.getDistributorId());
        UIUtil.selectById(cbGenre, m.getGenreId());
    }

    @Override
    protected void clearForm() {
        tfId.setText("");
        tfTitle.setText("");
        tfDirector.setText("");
        tfTime.setText("");
        tfPrice.setText("");
        if (cbDistributor.getItemCount() > 0) {
            cbDistributor.setSelectedIndex(0);
        }
        if (cbGenre.getItemCount() > 0) {
            cbGenre.setSelectedIndex(0);
        }
    }

    @Override
    protected void onModeChanged(boolean editing) {
        tfId.setEnabled(!editing);
    }

    private Movie readForm() {
        String distributorId = UIUtil.selectedId(cbDistributor);
        String genreId = UIUtil.selectedId(cbGenre);
        if (distributorId == null || genreId == null) {
            throw new IllegalArgumentException("배급사와 장르를 먼저 등록한 뒤 선택하세요.");
        }
        return new Movie(
                UIUtil.required(tfId.getText(), "영화코드", 20),
                UIUtil.required(tfTitle.getText(), "제목", 100),
                UIUtil.required(tfDirector.getText(), "감독", 50),
                UIUtil.parseInt(tfTime.getText(), "상영시간", 1, 1000),
                UIUtil.parseInt(tfPrice.getText(), "티켓가격", 0, 1000000),
                distributorId, genreId);
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
