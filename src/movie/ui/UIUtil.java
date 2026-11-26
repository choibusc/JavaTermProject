package movie.ui;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public final class UIUtil {

    public static final Font FONT = new Font("Malgun Gothic", Font.PLAIN, 13);

    private UIUtil() {
    }

    public static class Item {
        public final String id;
        public final String label;

        public Item(String id, String label) {
            this.id = id;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public static void init() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // 기본 룩앤필 사용
        }
        for (Object key : Collections.list(UIManager.getDefaults().keys())) {
            if (UIManager.get(key) instanceof FontUIResource) {
                UIManager.put(key, new FontUIResource(FONT));
            }
        }
        UIManager.put("OptionPane.yesButtonText", "예");
        UIManager.put("OptionPane.noButtonText", "아니오");
        UIManager.put("OptionPane.okButtonText", "확인");
        UIManager.put("OptionPane.cancelButtonText", "취소");
    }

    public static void info(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "알림", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void warn(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "입력 확인", JOptionPane.WARNING_MESSAGE);
    }

    public static void error(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "오류", JOptionPane.ERROR_MESSAGE);
    }

    public static boolean confirm(Component parent, String msg) {
        return JOptionPane.showConfirmDialog(parent, msg, "확인", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    public static DefaultTableModel nonEditableModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int c) {
                if (getRowCount() > 0 && getValueAt(0, c) != null) {
                    return getValueAt(0, c).getClass();
                }
                return Object.class;
            }
        };
    }

    public static JTable createTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setRowHeight(26);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.setAutoCreateRowSorter(true);
        t.setFillsViewportHeight(true);
        t.getTableHeader().setReorderingAllowed(false);
        DefaultTableCellRenderer center = new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void setValue(Object v) {
                if (v instanceof Number) {
                    setText(NumberFormat.getIntegerInstance().format(((Number) v).longValue()));
                } else {
                    super.setValue(v);
                }
            }
        };
        center.setHorizontalAlignment(SwingConstants.CENTER);
        t.setDefaultRenderer(Object.class, center);
        t.setDefaultRenderer(Integer.class, center);
        t.setDefaultRenderer(java.sql.Date.class, center);
        return t;
    }

    public static String required(String s, String name, int maxLen) {
        String v = s == null ? "" : s.trim();
        if (v.isEmpty()) {
            throw new IllegalArgumentException(name + "을(를) 입력하세요.");
        }
        if (v.length() > maxLen) {
            throw new IllegalArgumentException(name + "은(는) " + maxLen + "자 이내로 입력하세요.");
        }
        return v;
    }

    public static String optional(String s, String name, int maxLen) {
        String v = s == null ? "" : s.trim();
        if (v.isEmpty()) {
            return null;
        }
        if (v.length() > maxLen) {
            throw new IllegalArgumentException(name + "은(는) " + maxLen + "자 이내로 입력하세요.");
        }
        return v;
    }

    public static int parseInt(String s, String name, int min, int max) {
        String v = s == null ? "" : s.trim();
        if (v.isEmpty()) {
            throw new IllegalArgumentException(name + "을(를) 입력하세요.");
        }
        try {
            int n = Integer.parseInt(v.replace(",", ""));
            if (n < min || n > max) {
                throw new IllegalArgumentException(name + "은(는) " + min + " ~ " + max + " 사이의 숫자로 입력하세요.");
            }
            return n;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(name + "은(는) 숫자로 입력하세요.");
        }
    }

    public static Integer parseIntOrNull(String s, String name, int min, int max) {
        if (s == null || s.trim().isEmpty()) {
            return null;
        }
        return parseInt(s, name, min, max);
    }

    public static JSpinner createDateSpinner() {
        JSpinner sp = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        sp.setEditor(new JSpinner.DateEditor(sp, "yyyy-MM-dd"));
        return sp;
    }

    public static java.sql.Date toSqlDate(JSpinner sp) {
        LocalDate ld = ((Date) sp.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return java.sql.Date.valueOf(ld);
    }

    public static void setDate(JSpinner sp, Date d) {
        sp.setValue(d == null ? new Date() : new Date(d.getTime()));
    }

    public static void selectById(JComboBox<Item> cb, String id) {
        for (int i = 0; i < cb.getItemCount(); i++) {
            if (cb.getItemAt(i).id.equals(id)) {
                cb.setSelectedIndex(i);
                return;
            }
        }
    }

    public static String selectedId(JComboBox<Item> cb) {
        Item it = (Item) cb.getSelectedItem();
        return it == null ? null : it.id;
    }
}
