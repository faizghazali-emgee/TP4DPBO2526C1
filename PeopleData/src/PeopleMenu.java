import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class PeopleMenu extends JFrame {
    // Variabel komponen form sesuai pola latihan
    public JPanel mainPanel;
    private JLabel titleLabel;
    private JTextField idField;
    private JTextField namaField;
    private JTextField tahunLahirField;
    private JComboBox<String> kategoriComboBox;
    private JRadioButton rbLaki;
    private JRadioButton rbPerempuan;
    private ButtonGroup bgGender;
    private JTable personTable;
    private JButton addUpdateButton;
    private JButton deleteButton;
    private JButton cancelButton;

    // Atribut pendukung logika CRUD
    private ArrayList<Person> listPerson;
    private int selectedIndex = -1;

    // Constructor
    public PeopleMenu() {
        // Inisialisasi listPerson
        listPerson = new ArrayList<>();

        // Isi listPerson dengan data awal (populateList)
        populateList();

        // Isi tabel person (Pastikan personTable tidak null)
        if (personTable != null) {
            personTable.setModel(setTable());
        }

        // Ubah styling title jika label tersedia
        if (titleLabel != null) {
            titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 20f));
        }

        // Atur isi combo box kategori (Tema Kampus / Sivitas Akademika)
        String[] kategoriData = { "Mahasiswa", "Dosen", "Staf Administrasi", "Alumni", "Peneliti" };
        kategoriComboBox.setModel(new DefaultComboBoxModel<>(kategoriData));

        // Konfigurasi ButtonGroup untuk JRadioButton (Atribut Bonus: Jenis Kelamin)
        bgGender = new ButtonGroup();
        bgGender.add(rbLaki);
        bgGender.add(rbPerempuan);
        rbLaki.setSelected(true);

        // Sembunyikan button delete saat awal dijalankan
        deleteButton.setVisible(false);

        // Saat tombol add/update ditekan
        addUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedIndex == -1) {
                    insertData();
                } else {
                    updateData();
                }
            }
        });

        // Saat tombol delete ditekan (dilengkapi konfirmasi prompt)
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                        null,
                        "Apakah Anda yakin ingin menghapus data ini?",
                        "Konfirmasi Hapus",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    deleteData();
                }
            }
        });

        // Saat tombol cancel ditekan
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });

        // Saat salah satu baris tabel ditekan
        personTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Ubah selectedIndex menjadi baris tabel yang diklik
                selectedIndex = personTable.getSelectedRow();

                // Simpan value textfield, combobox, dan radiobutton dari tabel
                String curId = personTable.getModel().getValueAt(selectedIndex, 1).toString();
                String curNama = personTable.getModel().getValueAt(selectedIndex, 2).toString();
                String curTahun = personTable.getModel().getValueAt(selectedIndex, 3).toString();
                String curKategori = personTable.getModel().getValueAt(selectedIndex, 4).toString();
                String curGender = personTable.getModel().getValueAt(selectedIndex, 5).toString();

                // Ubah isi textfield, combobox, dan radio button
                idField.setText(curId);
                idField.setEnabled(false); // ID tidak boleh diedit saat update
                namaField.setText(curNama);
                tahunLahirField.setText(curTahun);
                kategoriComboBox.setSelectedItem(curKategori);

                if (curGender.equalsIgnoreCase("Laki-laki")) {
                    rbLaki.setSelected(true);
                } else {
                    rbPerempuan.setSelected(true);
                }

                // Ubah button "Add" menjadi "Update"
                addUpdateButton.setText("Update");

                // Tampilkan button delete
                deleteButton.setVisible(true);
            }
        });
    }

    // Method populateList untuk data awal
    public void populateList() {
        if (listPerson.isEmpty()) {
            listPerson.add(new Person("2504531", "Faiz Muhammad Ghazali", 2006, "Mahasiswa", "Laki-laki"));
            listPerson.add(new Person("2200001", "Abdullah Hafidz Furqon", 2003, "Alumni", "Laki-laki"));
            listPerson.add(new Person("198109182009122003", "Rosa Ariani Sukamto", 1981, "Dosen", "Perempuan"));
        }
    }

    // Method setTable()
    public final DefaultTableModel setTable() {
        // Tentukan kolom tabel
        Object[] cols = { "No", "ID Person", "Nama", "Tahun Lahir", "Kategori", "Jenis Kelamin" };

        // Buat objek tabel dengan kolom yang sudah dibuat
        DefaultTableModel tmp = new DefaultTableModel(null, cols) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabel read-only
            }
        };

        // Isi tabel dengan listPerson
        for (int i = 0; i < listPerson.size(); i++) {
            Object[] row = {
                    i + 1,
                    listPerson.get(i).getId(),
                    listPerson.get(i).getNama(),
                    listPerson.get(i).getTahunLahir(),
                    listPerson.get(i).getKategori(),
                    listPerson.get(i).getJenisKelamin()
            };
            tmp.addRow(row);
        }

        return tmp;
    }

    // Method insertData()
    public void insertData() {
        try {
            // Ambil value dari textfield, combobox, dan radio button
            String id = idField.getText().trim();
            String nama = namaField.getText().trim();
            int tahunLahir = Integer.parseInt(tahunLahirField.getText().trim());
            String kategori = kategoriComboBox.getSelectedItem().toString();
            String jenisKelamin = rbLaki.isSelected() ? "Laki-laki" : "Perempuan";

            if (id.isEmpty() || nama.isEmpty()) {
                JOptionPane.showMessageDialog(null, "ID dan Nama tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Tambahkan data ke dalam list
            listPerson.add(new Person(id, nama, tahunLahir, kategori, jenisKelamin));

            // Update tabel
            personTable.setModel(setTable());

            // Bersihkan form
            clearForm();

            // Feedback
            System.out.println("Insert berhasil");
            JOptionPane.showMessageDialog(null, "Data berhasil ditambahkan");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Tahun lahir harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method updateData()
    public void updateData() {
        try {
            // Ambil data dari form
            String id = idField.getText().trim();
            String nama = namaField.getText().trim();
            int tahunLahir = Integer.parseInt(tahunLahirField.getText().trim());
            String kategori = kategoriComboBox.getSelectedItem().toString();
            String jenisKelamin = rbLaki.isSelected() ? "Laki-laki" : "Perempuan";

            // Ubah data person di list
            listPerson.get(selectedIndex).setId(id);
            listPerson.get(selectedIndex).setNama(nama);
            listPerson.get(selectedIndex).setTahunLahir(tahunLahir);
            listPerson.get(selectedIndex).setKategori(kategori);
            listPerson.get(selectedIndex).setJenisKelamin(jenisKelamin);

            // Update tabel
            personTable.setModel(setTable());

            // Bersihkan form
            clearForm();

            // Feedback
            System.out.println("Update berhasil");
            JOptionPane.showMessageDialog(null, "Data berhasil diubah");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Tahun lahir harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method deleteData()
    public void deleteData() {
        // Hapus data dari list
        listPerson.remove(selectedIndex);

        // Update tabel
        personTable.setModel(setTable());

        // Bersihkan form
        clearForm();

        // Feedback
        System.out.println("Delete berhasil");
        JOptionPane.showMessageDialog(null, "Data berhasil dihapus");
    }

    // Method clearForm()
    public void clearForm() {
        // Kosongkan semua textfield dan reset combo/radio
        idField.setText("");
        idField.setEnabled(true);
        namaField.setText("");
        tahunLahirField.setText("");
        kategoriComboBox.setSelectedIndex(0);
        rbLaki.setSelected(true);

        // Ubah button "Update" menjadi "Add"
        addUpdateButton.setText("Add");

        // Sembunyikan button delete
        deleteButton.setVisible(false);

        // Ubah selectedIndex menjadi -1 (tidak ada baris yang dipilih)
        selectedIndex = -1;
        personTable.clearSelection();
    }

    // Main Method
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            // Buat object window
            PeopleMenu menu = new PeopleMenu();

            // Atur ukuran window
            menu.setSize(750, 600);

            // Letakkan window di tengah layar
            menu.setLocationRelativeTo(null);

            // Isi window
            menu.setContentPane(menu.mainPanel);

            // Ubah warna background
            menu.getContentPane().setBackground(Color.WHITE);

            // Tampilkan window
            menu.setVisible(true);

            // Agar program ikut berhenti saat window di-close
            menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        });
    }
}