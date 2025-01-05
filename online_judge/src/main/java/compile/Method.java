package compile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Method {
    // 通过Runtime得到实例，执行exec方法
    // 获取标准输出和标准错误，并写入文件
    // 获得子进程的状态码
    public static int run(String command, String stdout, String stderr) {
        try {
            Process process = Runtime.getRuntime().exec(command);
            if (stdout != null) {
                InputStream inputStream = process.getInputStream();
                copy(stdout, inputStream);
            }
            if (stderr != null) {
                InputStream inputStream = process.getErrorStream();
                copy(stderr, inputStream);
            }
            int exitValue = process.waitFor();
            return exitValue;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private static void copy(String std, InputStream inputStream) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(std);
        while (true) {
            int read = inputStream.read();
            if (read == -1) {
                break;
            }
            outputStream.write(read);
        }
        outputStream.close();
        inputStream.close();
    }

    public static void main(String[] args) {
        int ret = Method.run("javac", "stdout.txt", "stderr.txt");
        System.out.println(ret);
    }
}
