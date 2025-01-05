package compile;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileOperation {
    public static String readFile(String filePath) {
        StringBuilder stringBuilder = new StringBuilder();
        //资源管理
        try (FileReader fileReader = new FileReader(filePath)) {
            while (true) {
                int ch = fileReader.read();
                if (ch == -1) {
                    break;
                }
                stringBuilder.append((char) ch);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // return stringBuilder.toString();

        return fileStandard(stringBuilder);
        // 去除末尾的换行符
    }

    public static void writeFile(String filePath, String content) {
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 去除末尾的换行符
    public static String fileStandard(StringBuilder stringBuilder) {
        String content = stringBuilder.toString();
        if (content.endsWith("\n") || content.endsWith("\r\n")) {
            content = content.substring(0, content.length() - (content.endsWith("\r\n") ? 2 : 1));
        }
        return content;
    }

    public static void main(String[] args) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("abc");
        System.out.println(stringBuilder);
        stringBuilder.append("def");
        System.out.println(stringBuilder);
    }
}
