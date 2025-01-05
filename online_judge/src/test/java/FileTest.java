import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileTest {
    public static void main(String[] args) {
        //读取文件并写入（拷贝）
        String srcPath = "E:\\Javaprojects\\online_judge\\src\\test\\java\\test1.txt";
        String destPath = "E:\\Javaprojects\\online_judge\\src\\test\\java\\test2.txt";

        try {
            FileInputStream fileInputStream = new FileInputStream(srcPath);
            FileOutputStream fileOutputStream = new FileOutputStream(destPath);
            while (true) {
                // read()返回一个字节(byte) 但是类型是 int
                // 读到文件末尾返回 EOF,-1表示
                int ch = fileInputStream.read();
                if (ch == -1) {
                    break;
                }
                fileOutputStream.write(ch);
            }
            // 关闭文件，避免文件资源泄露
            fileInputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
