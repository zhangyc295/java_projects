import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ExecTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        // 单例
        Runtime runtime = Runtime.getRuntime();
        // 写入文件  标准输出
        Process process = runtime.exec("javac");
        // 图形化命令会直接弹出窗口
        // Process process1 = runtime.exec("notepad");
        InputStream stdoutStream = process.getInputStream();
        FileOutputStream stdoutFile = new FileOutputStream("E:\\Javaprojects\\online_judge\\src\\test\\java\\stdout.txt");
        while(true){
            int ch = stdoutStream.read();
            if(ch == -1){
                break;
            }
            stdoutFile.write(ch);
        }
        stdoutStream.close();
        stdoutFile.close();

        InputStream stderrStream = process.getErrorStream();
        FileOutputStream stderrFile = new FileOutputStream("E:\\Javaprojects\\online_judge\\src\\test\\java\\stderr.txt");
        while (true){
            int ch = stderrStream.read();
            if(ch == -1){
                break;
            }
            stderrFile.write(ch);
        }
        stderrStream.close();
        stderrFile.close();

        //waitFor()方法实现进程等待  阻塞到子进程执行完毕为止
        //正常退出返回 0
        // Java程序（父进程）在等待javac编译器进程（子进程）
        int exitValue = process.waitFor();
        int exitValue2 = process.exitValue();
        System.out.println("Exit value: " + exitValue);
        System.out.println("Exit value: " + exitValue2);
    }
}
