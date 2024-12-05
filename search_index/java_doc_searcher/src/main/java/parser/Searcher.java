package parser;

import model.DocInfo;
import model.PosInMatrix;
import model.Result;
import model.Weight;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class Searcher {
    //保存停用词

    private static final String STOP_WORD_PATH = "/root/java/java_searcher/stopwords.txt";

    //private static final String STOP_WORD_PATH = "E:\\java_projects\\search_index\\stopwords.txt";
    private HashSet<String> stopWords = new HashSet<>();

    private Index index = new Index();

    public Searcher() {
        index.load();
        loadStopWords();
    }

    public List<Result> search(String query) {
        // 1.对查询语句进行分词
        List<Term> inputTerms = ToAnalysis.parse(query).getTerms();
        List<Term> terms = new ArrayList<>();
        //过滤暂停词
        for (Term term : inputTerms) {
            if (stopWords.contains(term.getName())) {
                continue;
            }
            terms.add(term);
        }
        //单一词对应的数组
//        List<model.Weight> termResult = new ArrayList<>();
//        for (Term term : terms) {
//            String word = term.getName();
//            List<model.Weight> list = index.getReverse(word);
//            if (list == null) {
//                continue;
//            }
//            termResult.addAll(list);
//        }
        //多个搜索词对应的二维数组
        //每一行是一个词对应的结果
        List<List<Weight>> termResults = new ArrayList<>();
        // 2.查询
        for (Term term : terms) {
            String word = term.getName();
            List<Weight> list = index.getReverse(word);
            if (list == null) {
                continue;
            }
            termResults.add(list);
        }

        // 3. 合并并排序
        List<Weight> termResult = merge(termResults);
        termResult.sort(new Comparator<Weight>() {
            @Override
            public int compare(Weight o1, Weight o2) {
                return o2.getWeight() - o1.getWeight();
            }
        });
        // 4. 结果
        List<Result> results = new ArrayList<>();
        for (Weight weight : termResult) {
            DocInfo docInfo = index.getDocInfo(weight.getDocId());
            Result result = new Result();
            result.setTitle(docInfo.getTitle());
            result.setUrl(docInfo.getUrl());
            result.setDescribe(getDesc(docInfo.getContent(), terms));
            results.add(result);
        }
        return results;
    }

    private List<Weight> merge(List<List<Weight>> source) {
        for (List<Weight> oneRow : source) {
            oneRow.sort(new Comparator<Weight>() {
                @Override
                public int compare(Weight o1, Weight o2) {
                    return o1.getDocId() - o2.getDocId();
                }
            });
        }
        //最终结果result
        List<Weight> result = new ArrayList<>();
        //优先级队列
        PriorityQueue<PosInMatrix> queue = new PriorityQueue<>(new Comparator<PosInMatrix>() {
            @Override
            public int compare(PosInMatrix o1, PosInMatrix o2) {
                //分别获取 o1和 o2位置对应的 Weight 对象
                Weight w1 = source.get(o1.row).get(o1.col);
                Weight w2 = source.get(o2.row).get(o2.col);
                return w1.getDocId() - w2.getDocId();
                //最小 DocId的 PosInMatrix对象将是队列的头部
            }
        });
        for (int row = 0; row < source.size(); row++) {
            queue.offer(new PosInMatrix(row, 0));
        }
        while (!queue.isEmpty()) {
            PosInMatrix pos = queue.poll();
            Weight currentWeight = source.get(pos.row).get(pos.col);
            //如果目标结果已经存在数据
            if (!result.isEmpty()) {
                Weight lastWeight = result.get(result.size() - 1);
                if (lastWeight.getDocId() == currentWeight.getDocId()) {
                    lastWeight.setWeight(lastWeight.getWeight() + currentWeight.getWeight());
                } else {
                    result.add(currentWeight);
                }
            } else {
                //如果目标结果第一次插入数据
                result.add(currentWeight);
            }
            PosInMatrix newPos = new PosInMatrix(pos.row, pos.col + 1);
            //某一行的索引是否到达末尾
            if (newPos.col >= source.get(newPos.row).size()) {
                continue;
            }
            queue.offer(newPos);
        }

        return result;
    }

    private String getDesc(String content, List<Term> terms) {
        int pos = -1;
        for (Term term : terms) {
            String word = term.getName();
            content = content.toLowerCase().replaceAll("\\b" + word + "\\b", " " + word + " ");
            pos = content.indexOf(" " + word + " "); // 独立单词
            if (pos >= 0) {
                break;
            }
        }
        if (pos == -1) {
            //正文没有找到
            if (content.length() > 160) {
                return content.substring(0, 160);
            }
            return content;
        }
        String desc = "";
        // System.out.println("Found at position: " + pos); // 调试：输出位置
        int beg = pos < 60 ? 0 : pos - 60;
        if (beg + 160 > content.length()) {
            desc = content.substring(beg);
        } else {
            desc = content.substring(beg, beg + 160) + "...";
        }
        for (Term term : terms) {
            String word = term.getName();
            //(?i)不区分大小写
            desc = desc.replaceAll("(?i) " + word + " ", "<i> " + word + " </i>");
        }
        return desc;

    }

    public void loadStopWords() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(STOP_WORD_PATH))) {
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                stopWords.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Searcher searcher = new Searcher();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String line = scanner.next();
            List<Result> results = searcher.search(line);
            for (Result result : results) {
                System.out.println("----------------");
                System.out.println(result);
            }
        }
    }
}
