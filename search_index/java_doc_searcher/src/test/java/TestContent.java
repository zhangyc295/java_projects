import parser.Parser;

import java.io.File;

public class TestContent {
    public static void main(String[] args) {
        File file = new File("E:\\java_projects\\search_index\\jdk-8u431-docs-all\\docs\\api\\java\\util\\ArrayList.html");
        Parser parser = new Parser();
        String result = parser.parseContent(file);
        String result2 = parser.parseContentRegex(file);
        System.out.println(result);
        System.out.println(result2);
    }
}
