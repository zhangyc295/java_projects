import java.io.File;

public class TestParseTitle {
    public static void main(String[] args) {
        File file = new File("E:\\java_projects\\search_index\\jdk-8u431-docs-all\\docs\\api.html");
        System.out.println(parseTitle(file));
    }
    private static String parseTitle(File file) {
        String fileName = file.getName();
        int lastIndex = fileName.lastIndexOf(".");
        return fileName.substring(0, lastIndex);
        //return file.getName().substring(file.getName().lastIndexOf("."));
    }
}
