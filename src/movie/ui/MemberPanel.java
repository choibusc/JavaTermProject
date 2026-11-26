package movie.ui;

import movie.dao.MemberDAO;
import movie.model.Member;

import javax.swing.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MemberPanel extends CrudPanel {

    private static final long serialVersionUID = 1L;

    private final MemberDAO dao = new MemberDAO();
    private List<Member> data = new ArrayList<>();

    private final JTextField tfId = new JTextField(14);
    private final JPasswordField pfPw = new JPasswordField(14);
    private final JTextField tfName = new JTextField(14);
    private final JTextField tfAge = new JTextField(14);
    private final JComboBox<String> cbGrade = new JComboBox<>(new String[] { "general", "vip" });
    private final JTextField tfPoint = new JTextField("0", 14);

    public MemberPanel() {
        super(new String[] { "회원아이디", "이름", "나이", "등급", "적립금" }, MemberDAO.SEARCH_NAMES);
        addField("회원아이디 *", tfId);
        addField("비밀번호 *", pfPw);
        addField("이름 *", tfName);
        addField("나이", tfAge);
        addField("등급 *", cbGrade);
        addField("적립금 *", tfPoint);
    }

    @Override
    protected void loadAll() throws SQLException {
        show(dao.findAll());
    }

    @Override
    protected void search(int field, String keyword) throws SQLException {
        show(dao.search(field, keyword));
    }

    private void show(List<Member> list) {
        data = list;
        List<Object[]> rows = new ArrayList<>();
        for (Member m : list) {
            rows.add(new Object[] { m.getId(), m.getName(), m.getAge(), m.getGrade(), m.getPoint() });
        }
        setRows(rows);
    }

    @Override
    protected void onSelect(int modelRow) {
        Member m = data.get(modelRow);
        tfId.setText(m.getId());
        pfPw.setText(m.getPassword());
        tfName.setText(m.getName());
        tfAge.setText(m.getAge() == null ? "" : String.valueOf(m.getAge()));
        cbGrade.setSelectedItem(m.getGrade());
        tfPoint.setText(String.valueOf(m.getPoint()));
    }

    @Override
    protected void clearForm() {
        tfId.setText("");
        pfPw.setText("");
        tfName.setText("");
        tfAge.setText("");
        cbGrade.setSelectedIndex(0);
        tfPoint.setText("0");
    }

    @Override
    protected void onModeChanged(boolean editing) {
        tfId.setEnabled(!editing); // 기본키는 수정할 수 없다.
    }

    /** 입력 칸 -> Member (검사 실패 시 IllegalArgumentException) */
    private Member readForm() {
        return new Member(
                UIUtil.required(tfId.getText(), "회원아이디", 20),
                UIUtil.required(new String(pfPw.getPassword()), "비밀번호", 20),
                UIUtil.required(tfName.getText(), "이름", 30),
                UIUtil.parseIntOrNull(tfAge.getText(), "나이", 0, 150),
                (String) cbGrade.getSelectedItem(),
                UIUtil.parseInt(tfPoint.getText(), "적립금", 0, 100000000));
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
