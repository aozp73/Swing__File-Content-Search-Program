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
         * 1. 전체 레이아웃
         ******************************************/
        // 창 기본 속성
        setTitle("파일 내용 검색기");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // - 창 닫을 때 동작: 완전 종료
        setLocationRelativeTo(null);                  // - 실행 후 화면 위치: 가운데
        

        /******************************************
         * 2. 컨테이너 생성
         ******************************************/
        // ? JPanel
        // - 컴포넌트를 모을 수 있는 컨테이너
        // - BorderLayout: 상,하,좌,우,가운데에 컴포넌트 배치 가능
        // - GridLayout  : argumnet의 행, 열의 그리드 구조 생성

        // 컨테이너1 생성 (전체)
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        // 컨테이너2 생성 (검색 및 입력)
        JPanel inputPanel = new JPanel(new GridLayout(3, 1));


        /******************************************
         * 3. 컴포넌트 생성
         ******************************************/
        // 1) 컨테이너2 GridLayout 컴포넌트 (폴더 선택 버튼 / 폴더 경로 텍스트필드)
        // - 레이아웃 배치
        JPanel folderPanel = new JPanel(new BorderLayout());
        folderField = new JTextField();
        JButton browseButton = new JButton("폴더 선택");
        folderPanel.add(folderField, BorderLayout.CENTER);
        folderPanel.add(browseButton, BorderLayout.EAST);

        // - 폴더 선택 버튼 이벤트 리스너 등록
        browseButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser(); // 세팅 - 파일 탐색 다이얼로그
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // 옵션 - 폴더만 선택 가능
            int option = chooser.showOpenDialog(this); // 오픈 - 파일 탐색 다이얼로그
            if (option == JFileChooser.APPROVE_OPTION) {
                // OPEN 클릭 시에만 => 선택한 폴더 객체를 가져와서 / 전체 경로를 문자열로 반환 / 텍스트 필드에 주입
                folderField.setText(chooser.getSelectedFile().getAbsolutePath()); 
            }
        });

        // 2) 컨테이너2 GridLayout 컴포넌트 (검색어 입력)
        JPanel keywordPanel = new JPanel(new BorderLayout());
        keywordField = new JTextField();
        keywordPanel.add(new JLabel("검색어: "), BorderLayout.WEST);
        keywordPanel.add(keywordField, BorderLayout.CENTER);
        
        // 3) 컨테이너2 GridLayout 컴포넌트 (확인 버튼)
        JButton searchButton = new JButton("확인");

        // 4) 컨테이너1 BorderLayout 컴포넌트 (결과 출력)
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea); // 스크롤바 


        /******************************************
         * 4. 컨테이너 조립
         ******************************************/
        // 1) 컨테이너2 조립 후 컨테이너1에 합침 (폴더 선택, 검색어 입력, 확인 버튼)
        inputPanel.add(folderPanel);
        inputPanel.add(keywordPanel);
        inputPanel.add(searchButton);
        panel.add(inputPanel, BorderLayout.NORTH);

        // 2) 컨테이너1 조립 (결과)
        panel.add(scrollPane, BorderLayout.CENTER);

        add(panel);


        /******************************************
         * 5. 확인 버튼 클릭 시, 로직
         ******************************************/
        searchButton.addActionListener(e -> searchFiles());
    }

    private void searchFiles() {
        /******************************************
         * 1. 유효성 체크
         ******************************************/
        String folderPath = folderField.getText().trim();
        String keyword = keywordField.getText().trim();
    
        if (folderPath.isEmpty() || keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "폴더와 검색어를 모두 입력하세요.");
            return;
        }
    
        File folder = new File(folderPath);
        if (!folder.isDirectory()) {
            JOptionPane.showMessageDialog(this, "유효한 폴더를 선택하세요.");
            return;
        }
    
        resultArea.setText("검색 중...\n");
    
        /******************************************
         * 2. 검색 시작 시간 측정
         ******************************************/
        long startTime = System.currentTimeMillis();
    
        /******************************************
         * 3. 검색 로직
         ******************************************/
        ArrayList<File> foundFiles = new ArrayList<>();
        searchInFolder(folder, keyword, foundFiles);
    
        /******************************************
         * 4. 검색 종료 시간 측정 및 경과 계산
         ******************************************/
        long endTime = System.currentTimeMillis();
        double elapsedSeconds = (endTime - startTime) / 1000.0;
    
        /******************************************
         * 5. 검색 결과 출력
         ******************************************/
        StringBuilder sb = new StringBuilder();
    
        if (foundFiles.isEmpty()) {
            sb.append("검색된 결과가 없습니다.\n");
        } else {
            sb.append("검색된 파일 목록:\n");
            for (File file : foundFiles) {
                sb.append(file.getAbsolutePath()).append("\n");
            }
        }
    
        sb.append(String.format("\n검색 완료 (소요 시간: %.2f초)", elapsedSeconds));
        resultArea.setText(sb.toString());
    }

    /**
     * @param folder
     * @param keyword
     * @param result
     * @implNote 지정된 폴더 내부 순회 -> 순회 중 keyword를 포함한 파일 탐색 -> 찾으면, result 리스트에 추가
     */
    private void searchInFolder(File folder, String keyword, ArrayList<File> result) {
        File[] files = folder.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                searchInFolder(file, keyword, result);
            } else {
                if (file.canRead() && file.length() < 10 * 1024 * 1024) { // 10MB 이하 파일만
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (line.contains(keyword)) {
                                result.add(file);
                                break;
                            }
                        }
                    } catch (IOException e) {
                        // 읽기 실패 무시
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        // SwingUtilities.invokeLater / 이벤트 디스패치 스레드(EDT)
        // - Swing GUI는 싱글스레드로 설계
        // - 스레드-세이프(thread-safe)하지 않음
        //   (멀티 스레딩 환경에서의 안전한 UI 프레임 워크 진행이 쉽지는 않은듯)
        SwingUtilities.invokeLater(() -> new FileSearchApp().setVisible(true));
    }
}
