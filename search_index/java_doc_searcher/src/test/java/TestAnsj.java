import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import java.util.List;

public class TestAnsj {
    public static void main(String[] args) {
        String string = "在数字化时代，个人隐私保护比便捷性更为重要，" +
                "是维护人权和社会秩序不可或缺的基石。";
        ToAnalysis toAnalysis = new ToAnalysis();
        List<Term> terms = ToAnalysis.parse(string).getTerms();
        // terms分词结果
        // getTerms方法转成列表格式
        for (Term term : terms) {
            System.out.println(term.getName());
        }
    }
}
