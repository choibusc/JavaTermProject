package movie.ui;

import movie.dao.MemberDAO;
import movie.dao.MovieDAO;
import movie.dao.ReservationDAO;
import movie.model.Member;
import movie.model.Movie;
import movie.model.Reservation;
import movie.ui.UIUtil.Item;

import javax.swing.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReservationPanel extends CrudPanel {

    private static final long serialVersionUID = 1L;
    private static final String AUTO_ID = "(자동 생성)";

    private final ReservationDAO dao = new ReservationDAO();
    private final MemberDAO memberDao = new MemberDAO();
    private final MovieDAO movieDao = new MovieDAO();
    private List<Reservation> data = new ArrayList<>();

    private final JTextField tfId = new JTextField(AUTO_ID, 14);
    private final JComboBox<Item> cbMember = new JComboBox<>();
    private final JComboBox<Item> cbMovie = new JComboBox<>();
    private final JSpinner spCount = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
    private final JSpinner spWatch = UIUtil.createDateSpinner();
    private final JSpinner spReserve = UIUtil.createDateSpinner();

    public ReservationPanel() {
        super(new String[] { "예매번호", "회원아이디", "회원명", "영화코드", "영화제목", "인원수", "관람일", "예매일", "결제금액" },
                ReservationDAO.SEARCH_NAMES);
        tfId.setEditable(false);
        addField("예매번호", tfId);
        addField("회원 *", cbMember);
        addField("영화 *", cbMovie);
        addField("인원수 *", spCount);
        addField("관람일 *", spWatch);
        addField("예매일 *", spReserve);
    }

    @Override
    protected void reloadReferences() throws SQLException {
        String selM = UIUtil.selectedId(cbMember);
        String selV = UIUtil.selectedId(cbMovie);
        cbMember.removeAllItems();
        for (Member m : memberDao.findAll()) {
            cbMember.addItem(new Item(m.getId(), m.getId() + " - " + m.getName()));
        }
        cbMovie.removeAllItems();
        for (Movie m : movieDao.findAll()) {
            cbMovie.addItem(new Item(m.getId(), m.getId() + " - " + m.getTitle()));
        }
        if (selM != null) {
            UIUtil.selectById(cbMember, selM);
        }
        if (selV != null) {
            UIUtil.selectById(cbMovie, selV);
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

    private void show(List<Reservation> list) {
        data = list;
        List<Object[]> rows = new ArrayList<>();
        for (Reservation r : list) {
            rows.add(new Object[] { r.getId(), r.getMemberId(), r.getMemberName(), r.getMovieId(), r.getMovieTitle(),
                    r.getHeadCount(), r.getWatchDate(), r.getReserveDate(), r.getAmount() });
        }
        setRows(rows);
    }

    @Override
    protected void onSelect(int modelRow) {
        Reservation r = data.get(modelRow);
        tfId.setText(r.getId());
        UIUtil.selectById(cbMember, r.getMemberId());
        UIUtil.selectById(cbMovie, r.getMovieId());
        spCount.setValue(r.getHeadCount());
        UIUtil.setDate(spWatch, r.getWatchDate());
        UIUtil.setDate(spReserve, r.getReserveDate());
    }

    @Override
    protected void clearForm() {
        tfId.setText(AUTO_ID);
        if (cbMember.getItemCount() > 0) {
            cbMember.setSelectedIndex(0);
        }
        if (cbMovie.getItemCount() > 0) {
            cbMovie.setSelectedIndex(0);
        }
        spCount.setValue(1);
        UIUtil.setDate(spWatch, new Date());
        UIUtil.setDate(spReserve, new Date());
    }

    private Reservation readForm(String id) {
        String memberId = UIUtil.selectedId(cbMember);
        String movieId = UIUtil.selectedId(cbMovie);
        if (memberId == null || movieId == null) {
            throw new IllegalArgumentException("회원과 영화를 먼저 등록한 뒤 선택하세요.");
        }
        java.sql.Date watch = UIUtil.toSqlDate(spWatch);
        java.sql.Date reserve = UIUtil.toSqlDate(spReserve);
        if (watch.toLocalDate().isBefore(reserve.toLocalDate())) {
            throw new IllegalArgumentException("관람일은 예매일보다 빠를 수 없습니다.");
        }
        return new Reservation(id, memberId, movieId, ((Number) spCount.getValue()).intValue(), watch, reserve);
    }

    @Override
    protected void insertRow() throws SQLException {
        Reservation r = readForm(null); // 입력 검사
        dao.insert(new Reservation(dao.nextId(), r.getMemberId(), r.getMovieId(), r.getHeadCount(),
                r.getWatchDate(), r.getReserveDate()));
    }

    @Override
    protected void updateRow() throws SQLException {
        dao.update(readForm(tfId.getText().trim()));
    }

    @Override
    protected void deleteRow() throws SQLException {
        dao.delete(tfId.getText().trim());
    }

}
