import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class FileSearchApp extends JFrame {

    private JTextField folderField;
    private JTextField keywordField;
    private JTextArea resultArea;

    public FileSearchApp() {

        /******************************************
         * 1. ��ü ���̾ƿ�
         ******************************************/
        // â �⺻ �Ӽ�
        setTitle("���� ���� �˻���");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // - â ���� �� ����: ���� ����
        setLocationRelativeTo(null);                  // - ���� �� ȭ�� ��ġ: ���
        

        /******************************************
         * 2. �����̳� ����
         ******************************************/
        // ? JPanel
        // - ������Ʈ�� ���� �� �ִ� �����̳�
        // - BorderLayout: ��,��,��,��,����� ������Ʈ ��ġ ����
        // - GridLayout  : argumnet�� ��, ���� �׸��� ���� ����

        // �����̳�1 ���� (��ü)
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        // �����̳�2 ���� (�˻� �� �Է�)
        JPanel inputPanel = new JPanel(new GridLayout(3, 1));


        /******************************************
         * 3. ������Ʈ ����
         ******************************************/
        // 1) �����̳�2 GridLayout ������Ʈ (���� ���� ��ư / ���� ��� �ؽ�Ʈ�ʵ�)
        // - ���̾ƿ� ��ġ
        JPanel folderPanel = new JPanel(new BorderLayout());
        folderField = new JTextField();
        JButton browseButton = new JButton("���� ����");
        folderPanel.add(folderField, BorderLayout.CENTER);
        folderPanel.add(browseButton, BorderLayout.EAST);

        // - ���� ���� ��ư �̺�Ʈ ������ ���
        browseButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser(); // ���� - ���� Ž�� ���̾�α�
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // �ɼ� - ������ ���� ����
            int option = chooser.showOpenDialog(this); // ���� - ���� Ž�� ���̾�α�
            if (option == JFileChooser.APPROVE_OPTION) {
                // OPEN Ŭ�� �ÿ��� => ������ ���� ��ü�� �����ͼ� / ��ü ��θ� ���ڿ��� ��ȯ / �ؽ�Ʈ �ʵ忡 ����
                folderField.setText(chooser.getSelectedFile().getAbsolutePath()); 
            }
        });

        // 2) �����̳�2 GridLayout ������Ʈ (�˻��� �Է�)
        JPanel keywordPanel = new JPanel(new BorderLayout());
        keywordField = new JTextField();
        keywordPanel.add(new JLabel("�˻���: "), BorderLayout.WEST);
        keywordPanel.add(keywordField, BorderLayout.CENTER);
        
        // 3) �����̳�2 GridLayout ������Ʈ (Ȯ�� ��ư)
        JButton searchButton = new JButton("Ȯ��");

        // 4) �����̳�1 BorderLayout ������Ʈ (��� ���)
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea); // ��ũ�ѹ� 


        /******************************************
         * 4. �����̳� ����
         ******************************************/
        // 1) �����̳�2 ���� �� �����̳�1�� ��ħ (���� ����, �˻��� �Է�, Ȯ�� ��ư)
        inputPanel.add(folderPanel);
        inputPanel.add(keywordPanel);
        inputPanel.add(searchButton);
        panel.add(inputPanel, BorderLayout.NORTH);

        // 2) �����̳�1 ���� (���)
        panel.add(scrollPane, BorderLayout.CENTER);

        add(panel);


        /******************************************
         * 5. Ȯ�� ��ư Ŭ�� ��, ����
         ******************************************/
        searchButton.addActionListener(e -> searchFiles());
    }

    private void searchFiles() {
        /******************************************
         * 1. ��ȿ�� üũ
         ******************************************/
        String folderPath = folderField.getText().trim();
        String keyword = keywordField.getText().trim();
    
        if (folderPath.isEmpty() || keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "������ �˻�� ��� �Է��ϼ���.");
            return;
        }
    
        File folder = new File(folderPath);
        if (!folder.isDirectory()) {
            JOptionPane.showMessageDialog(this, "��ȿ�� ������ �����ϼ���.");
            return;
        }
    
        resultArea.setText("�˻� ��...\n");
    
        /******************************************
         * 2. �˻� ���� �ð� ����
         ******************************************/
        long startTime = System.currentTimeMillis();
    
        /******************************************
         * 3. �˻� ����
         ******************************************/
        ArrayList<File> foundFiles = new ArrayList<>();
        searchInFolder(folder, keyword, foundFiles);
    
        /******************************************
         * 4. �˻� ���� �ð� ���� �� ��� ���
         ******************************************/
        long endTime = System.currentTimeMillis();
        double elapsedSeconds = (endTime - startTime) / 1000.0;
    
        /******************************************
         * 5. �˻� ��� ���
         ******************************************/
        StringBuilder sb = new StringBuilder();
    
        if (foundFiles.isEmpty()) {
            sb.append("�˻��� ����� �����ϴ�.\n");
        } else {
            sb.append("�˻��� ���� ���:\n");
            for (File file : foundFiles) {
                sb.append(file.getAbsolutePath()).append("\n");
            }
        }
    
        sb.append(String.format("\n�˻� �Ϸ� (�ҿ� �ð�: %.2f��)", elapsedSeconds));
        resultArea.setText(sb.toString());
    }

    /**
     * @param folder
     * @param keyword
     * @param result
     * @implNote ������ ���� ���� ��ȸ -> ��ȸ �� keyword�� ������ ���� Ž�� -> ã����, result ����Ʈ�� �߰�
     */
    private void searchInFolder(File folder, String keyword, ArrayList<File> result) {
        File[] files = folder.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                searchInFolder(file, keyword, result);
            } else {
                if (file.canRead() && file.length() < 10 * 1024 * 1024) { // 10MB ���� ���ϸ�
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (line.contains(keyword)) {
                                result.add(file);
                                break;
                            }
                        }
                    } catch (IOException e) {
                        // �б� ���� ����
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        // SwingUtilities.invokeLater / �̺�Ʈ ����ġ ������(EDT)
        // - Swing GUI�� �̱۽������ ����
        // - ������-������(thread-safe)���� ����
        //   (��Ƽ ������ ȯ�濡���� ������ UI ������ ��ũ ������ ������ ������)
        SwingUtilities.invokeLater(() -> new FileSearchApp().setVisible(true));
    }
}
