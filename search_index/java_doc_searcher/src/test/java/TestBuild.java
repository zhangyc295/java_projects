import model.DocInfo;
import parser.Index;
import parser.Parser;

import java.io.File;

public class TestBuild {
    public static void main(String[] args) {
        File file = new File("E:\\java_projects\\search_index\\jdk-8u431-docs-all\\docs\\api\\java\\util\\ArrayList.html");
        Parser parser = new Parser();
        String result = parser.parseContent(file);

        Index index = new Index();
        DocInfo docInfo = new DocInfo();
        docInfo.setContent(result);
        index.buildBackward(docInfo);
    }

}
