package parser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.DocInfo;
import model.Weight;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Index {
    private static final String PATH = "/root/java/java_searcher/";

    //private static final String PATH = "E:/java_projects/search_index/";

    private ObjectMapper objectMapper = new ObjectMapper();

    private ArrayList<DocInfo> forwardIndex = new ArrayList<>();

    private HashMap<String, ArrayList<Weight>> backwardIndex = new HashMap<>();

    private final Object lock1 = new Object();
    private final Object lock2 = new Object();

    //查询文档
    public DocInfo getDocInfo(int docId) {
        return forwardIndex.get(docId);
    }

    //查询关键词
    public List<Weight> getReverse(String term) {
        return backwardIndex.get(term);
    }

    public void addDoc(String title, String url, String content) {
        DocInfo docInfo = buildForward(title, url, content);
        buildBackward(docInfo);
    }

    public DocInfo buildForward(String title, String url, String content) {
        DocInfo docInfo = new DocInfo();
        docInfo.setTitle(title);
        docInfo.setUrl(url);
        docInfo.setContent(content);
        synchronized (lock1) {
            docInfo.setDocId(forwardIndex.size());
            forwardIndex.add(docInfo);
        }
        return docInfo;
    }

    public void buildBackward(DocInfo docInfo) {
        class WordCount {
            public int titleCount;
            public int contentCount;
        }
        HashMap<String, WordCount> wordCountHashMap = new HashMap<>();
        //将文档进行分词

        List<Term> terms = ToAnalysis.parse(docInfo.getTitle()).getTerms();
        //遍历标题  判断分词是否存在
        for (Term term : terms) {
            String word = term.getName();
            WordCount wordCount = wordCountHashMap.get(word);
            if (wordCount == null) {
                WordCount newWord = new WordCount();
                newWord.titleCount = 1;
                newWord.contentCount = 0;
                wordCountHashMap.put(word, newWord);
            } else {
                wordCount.titleCount++;
            }
        }
        //遍历正文
        terms = ToAnalysis.parse(docInfo.getContent()).getTerms();
        for (Term term : terms) {
            String word = term.getName();
            WordCount wordCount = wordCountHashMap.get(word);
            if (wordCount == null) {
                WordCount newWord = new WordCount();
                newWord.titleCount = 0;
                newWord.contentCount = 1;
                wordCountHashMap.put(word, newWord);
            } else {
                wordCount.contentCount++;
            }
        }
        for (Map.Entry<String, WordCount> entry : wordCountHashMap.entrySet()) {
            synchronized (lock2) {
                List<Weight> list = backwardIndex.get(entry.getKey());
                if (list == null) {
                    ArrayList<Weight> newList = new ArrayList<>();
                    Weight newWeight = new Weight();
                    newWeight.setDocId(docInfo.getDocId());
                    newWeight.setWeight(entry.getValue().titleCount * 10 + entry.getValue().contentCount);
                    newList.add(newWeight);
                    backwardIndex.put(entry.getKey(), newList);
                } else {
                    Weight newWeight = new Weight();
                    newWeight.setDocId(docInfo.getDocId());
                    newWeight.setWeight(entry.getValue().titleCount * 10 + entry.getValue().contentCount);
                    list.add(newWeight);
                }
            }
        }
//        for (Map.Entry<String, WordCount> entry : wordCountHashMap.entrySet()) {
//            String word = entry.getKey();
//            WordCount count = entry.getValue();
//            System.out.println("Word: " + word + ", Title Count: " + count.titleCount + ", Content Count: " + count.contentCount);
//        }

    }


    public void save() {
        long begin = System.currentTimeMillis();
        File indexFilePath = new File(PATH);
        if (!indexFilePath.exists()) {
            indexFilePath.mkdirs();
        }
        File forwardIndexFile = new File(PATH + "forwardIndex.txt");
        File backwardIndexFile = new File(PATH + "backwardIndex.txt");
        try {
            objectMapper.writeValue(forwardIndexFile, forwardIndex);
            objectMapper.writeValue(backwardIndexFile, backwardIndex);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();

        System.out.println("保存成功");
        System.out.println("消耗时间" + (end - begin) + "ms");
    }

    public void load() {
        System.out.println("加载文件");
        File forwardIndexFile = new File(PATH + "forwardIndex.txt");
        File backwardIndexFile = new File(PATH + "backwardIndex.txt");
        try {
            forwardIndex = objectMapper.readValue(forwardIndexFile, new TypeReference<ArrayList<DocInfo>>() {
            });
            backwardIndex = objectMapper.readValue(backwardIndexFile, new TypeReference<HashMap<String, ArrayList<Weight>>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();

        }
        System.out.println("加载成功");
    }

    public static void main(String[] args) {
        Index index = new Index();
        index.load();
        System.out.println("加载至内存成功");
    }
}
