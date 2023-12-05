package com.company;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Objects;


public class SimpleGui extends JFrame {
    private JButton inputConfirmed = new JButton("Нажми для того, чтобы подтвердить ввод");
    private JTextField inputFieldName = new JTextField("Введите название болезни", 40);
    private JTextField inputFieldSymptom = new JTextField("Введите симптомы", 40);
    private JTextField inputFieldTreatment = new JTextField("Введите методы лечения", 40);
    private JTextField inputFieldPrevention = new JTextField("Введите Правила профилактики заболевания", 40);
    private JButton addInput = new JButton("Нажмите, чтобы добавить новые материалы в базу данных");
    private JLabel label = new JLabel("Выберите действие");
    private JLabel inputLabel = new JLabel("<html>Вводите поочередно все данные, и<br> нажмите подтвердить для ввода всех параметров<br>(не забудьте стереть текст-подсказку).</html>)");
    private JButton fileWriter = new JButton("Кнопка для записи Базы Данных в текстовый файл");
    private JButton searchInDb = new JButton("Нажмите для поиска в справочнике");
    private JLabel searchLabel = new JLabel("<html>Введите данные поиска. <br> В выпадающем списке выберите <br> критерий для поиска.</html>");
    private String[] columnNames = {"Заболевание", "Симптом", "Лечение", "Профилактика"};
    private JComboBox<String> columnList = new JComboBox<>(columnNames);
    private JTextField searchField = new JTextField("Введи данные для поиска", 20);
    private JButton searchConfirm = new JButton("Поиск");
    private final ImageIcon exitIcon = new ImageIcon("D:\\Jav_labi\\Medcine2\\logout.png");

    public SimpleGui() {
        setTitle("Медицинский справочник");
        String url = "jdbc:sqlite:D:\\Jav_labi\\Medcine2\\Sqlite\\med.db";
        String s = "Illnesses";
        MedcineDatabase mdb = new MedcineDatabase(url);
        this.setBounds(100, 100, 700, 700);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container container = this.getContentPane();
        container.setLayout(new GridLayout(5, 5, 2, 2));
        label.setBorder(new EmptyBorder(10, 40, 10, 10));
        label.setFont(new Font("Arial", Font.PLAIN, 64));
        inputLabel.setFont(new Font("Arial", Font.BOLD, 15));
        container.add(label);
        container.add(addInput);
        container.add(searchInDb);
        // container.add(inputField);
        container.add(fileWriter);
        addInput.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame newFrame = new JFrame("Ввод данных");
                newFrame.setSize(500, 500);
                newFrame.setLayout(new FlowLayout());
                newFrame.getContentPane().add(inputLabel);

                newFrame.getContentPane().add(inputFieldName);
                newFrame.getContentPane().add(inputFieldSymptom);
                newFrame.getContentPane().add(inputFieldTreatment);
                newFrame.getContentPane().add(inputFieldPrevention);
                newFrame.getContentPane().add(inputConfirmed);
                newFrame.setVisible(true);
            }
        });
        inputConfirmed.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean b = false;
                String name;
                String sympt;
                String treatment;
                String prevention;
                if (inputFieldName.getText().length() > 15 ||
                        inputFieldSymptom.getText().length() > 300 ||
                        inputFieldTreatment.getText().length() > 300 ||
                        inputFieldPrevention.getText().length() > 300) {
                    JOptionPane.showMessageDialog(null, "У вас должно быть не более 15 символов в первом поле и не более 300 в остальных полях");
                } else if (inputFieldName.getText().isEmpty() ||
                        inputFieldSymptom.getText().isEmpty() ||
                        inputFieldTreatment.getText().isEmpty() ||
                        inputFieldPrevention.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Пожалуйста, введите все данные");
                } else {
                    name = inputFieldName.getText();
                    sympt = inputFieldSymptom.getText();
                    treatment = inputFieldTreatment.getText();
                    prevention = inputFieldPrevention.getText();
                    b = true;
                    JOptionPane.showMessageDialog(null, "Данные успешно добавлены");
                    mdb.insert(name, sympt, treatment, prevention);
                    mdb.close();
                }
                if (!b) {
                    JOptionPane.showMessageDialog(null, "Данные не добавлены, произошла ошибка, попробуйте снова");
                }

            }
        });
        searchInDb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame searchFrame = new JFrame();
                searchFrame.setSize(500, 500);
                searchFrame.setLayout(new FlowLayout());
                searchFrame.getContentPane().add(searchLabel);
                searchFrame.getContentPane().add(searchField);
                searchFrame.getContentPane().add(columnList);
                searchFrame.getContentPane().add(searchConfirm);
                searchFrame.setVisible(true);
            }
        });
        searchConfirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedColumn = (String) columnList.getSelectedItem();
                String value = "";
                String keyword = searchField.getText();
                boolean B = true;
                switch (Objects.requireNonNull(selectedColumn)) {
                    case "Заболевание":
                        value = "name";
                        break;
                    case "Симптом":
                        value = "symptoms";
                        break;
                    case "Лечение":
                        value = "treatment";
                        break;
                    case "Профилактика":
                        value = "prevention";
                        break;
                }
                if (keyword.length() > (value.equals("name") ? 15 : 300)) {
                    JOptionPane.showMessageDialog(null, "Слишком длинный запрос, количество символов в поле Заболевания не должно быть больше 15, в остальных не более 300");
                    B = false;
                }
//                if (keyword.length() > 300) {
//                    JOptionPane.showMessageDialog(null, "Слишком длинный запрос, количество символов в поле Заболевания не должно быть больше 15, в остальных не более 300");
//                    B = false;
//                }
                if (searchField.getText().isEmpty() || !B) {
                    JOptionPane.showMessageDialog(null, "Произошла ошибка");
                } else {
                    ArrayList<String> resultList = new ArrayList<>(mdb.search(s, value, keyword));
                    String[][] list = new String[resultList.size()][];
                    for (int i = 0; i < resultList.size(); i++) {
                        list[i] = resultList.get(i).split("\n"); // Разделяем строку на подстроки по символу новой строки
                    }
                    String[] columnNames1 = {"ID", "Заболевание", "Симптом", "Лечение", "Профилактика"};
                    JTable table = new JTable(list, columnNames1);
                    table.setTransferHandler(new TransferHandler("copy"));
                    DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
                        @Override
                        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                            JTextArea textArea = new JTextArea((String) value);
                            textArea.setEditable(true);
                            textArea.addKeyListener(new KeyAdapter() {
                                public void keyReleased(KeyEvent e) {
                                    if ((e.getKeyCode() == KeyEvent.VK_C) && ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)) {
                                        StringSelection stringSelection = new StringSelection(textArea.getText());
                                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                                        clipboard.setContents(stringSelection, null);
                                    }
                                }
                            });
                            textArea.setWrapStyleWord(true); // Включаем перенос по словам
                            textArea.setLineWrap(true); // Включаем перенос строк
                            return textArea;
                        }
                    };
// Устанавливаем рендерер для всех столбцов
                    for (int i = 0; i < table.getColumnCount(); i++) {
                        table.getColumnModel().getColumn(i).setCellRenderer(renderer);
                    }
// Устанавливаем высоту строк в соответствии с содержимым
                    table.setRowHeight(250);
                    JScrollPane scrollPane = new JScrollPane(table);
                    JFrame resFrame = new JFrame();
                    resFrame.add(scrollPane);
                    resFrame.setSize(500, 500);
                    //resFrame.pack();
                    resFrame.setVisible(true);
                    //  mdb.close();
                }

            }
        });
        fileWriter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String result = mdb.fileWritter(s);
                JOptionPane.showMessageDialog(null, result);
                mdb.close();
            }
        });
        // setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
//        JPanel panel =new JPanel();
//        JButton button = new JButton("Нажми");
//        getContentPane().add(button);
//        addWindowListener(new WindowAdapter() {
//            @Override
//            public void windowClosing(WindowEvent e) {
//                String[] options = {"Да", "Нет"};
//                int confirmed = JOptionPane.showOptionDialog(null,
//                        "Вы уверены, что хотите выйти?", "Подтверждение выхода",
//                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
//                        exitIcon, options, options[0]);
//                if (confirmed == 0) {
//                    dispose();
//                }
//            }
//        });
//        setSize(700, 700);
    }//SimpleGui constructor


}
