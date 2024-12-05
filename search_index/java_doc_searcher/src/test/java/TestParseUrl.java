import java.io.File;

public class TestParseUrl {
    public static void main(String[] args) {
        String INPUT_PATH = "E:\\java_projects\\search_index\\jdk-8u431-docs-all\\docs\\api\\";

        File file = new File("E:\\java_projects\\search_index\\jdk-8u431-docs-all\\docs\\api\\java\\util\\ArrayList.html");
        String part1 = "https://docs.oracle.com/javase/8/docs/api/";
        String part2 = file.getAbsolutePath().substring(INPUT_PATH.length());
        System.out.println(part1);

        System.out.println(part2);
        System.out.println(part1+part2);
    }

}
