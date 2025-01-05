package dao;

import compile.Result;
import database.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuestionDao {

    public void insertQuestion(Question question) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DBUtil.getConnection();
            String sql = "insert into oj_question values(null,?,?,?,?,?)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, question.getTitle());
            preparedStatement.setString(2, question.getLevel());
            preparedStatement.setString(3, question.getDescription());
            preparedStatement.setString(4, question.getInitialCode());
            preparedStatement.setString(5, question.getTestCode());
            int row = preparedStatement.executeUpdate();
            if (row != 1) {
                System.out.println("insert failed");
            } else {
                System.out.println("insert successful");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtil.close(connection, preparedStatement, null);
        }
    }

    public void deleteQuestion(int id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DBUtil.getConnection();
            String sql = "delete from oj_question where id = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            int row = preparedStatement.executeUpdate();
            if (row != 1) {
                System.out.println("delete failed");
            } else {
                System.out.println("delete successful");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtil.close(connection, preparedStatement, null);
        }
    }

    public List<Question> selectAllQuestion() {
        List<Question> questions = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DBUtil.getConnection();
            String sql = "select id, title, level from oj_question";
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Question question = new Question();
                question.setId(resultSet.getInt("id"));
                question.setTitle(resultSet.getString("title"));
                question.setLevel(resultSet.getString("level"));
                questions.add(question);
            }
            return questions;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtil.close(connection, preparedStatement, resultSet);
        }
    }

    public Question selectQuestionById(int id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Question question = new Question();
        try {
            connection = DBUtil.getConnection();
            String sql = "select * from oj_question where id = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                question.setId(resultSet.getInt("id"));
                question.setTitle(resultSet.getString("title"));
                question.setLevel(resultSet.getString("level"));
                question.setDescription(resultSet.getString("description"));
                question.setInitialCode(resultSet.getString("initialCode"));
                question.setTestCode(resultSet.getString("testCode"));
                return question;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtil.close(connection, preparedStatement, resultSet);
        }
        return null;
    }


    public static void testInsert() {
        QuestionDao questionDao = new QuestionDao();
        Question question = new Question();
        question.setTitle("两数之和");
        question.setLevel("简单");
        question.setDescription("给定一个整数数组nums和一个整数目标值target，请你在该数组中找出和为目标值target的那两个整数，并返回它们的数组下标。\n" +
                "\n" +
                "你可以假设每种输入只会对应一个答案，并且你不能使用两次相同的元素。\n" +
                "\n" +
                "你可以按任意顺序返回答案。\n" +
                "输入：nums = [2,7,11,15], target = 9\n" +
                "输出：[0,1]\n" +
                "解释：因为 nums[0] + nums[1] == 9 ，返回 [0, 1] 。");
        question.setInitialCode("class Solution {\n" +
                "    public int[] twoSum(int[] nums, int target) {\n" +
                "        \n" +
                "    }\n" +
                "}");
        question.setTestCode("public static void main(String[] args) {\n" +
                "        Solution solution = new Solution();\n" +
                "        int[] nums = new int[]{2, 7, 11, 15};\n" +
                "        int target = 9;\n" +
                "        int[] result = solution.twoSum(nums, target);\n" +
                "        if (result.length == 2 && result[0] == 0 && result[1] == 1) {\n" +
                "            System.out.println(\"success\");\n" +
                "        }else {\n" +
                "            System.out.println(\"fail\");\n" +
                "        }\n" +
                "    }");
        questionDao.insertQuestion(question);
    }

    public static void testInsert2() {
        QuestionDao questionDao = new QuestionDao();
        Question question = new Question();
        question.setTitle("反转链表");
        question.setLevel("中等");
        question.setDescription("给你单链表的头节点head，请你反转链表，并返回反转后的链表。\n");
        question.setInitialCode("class ListNode {\n" +
                "    int val;\n" +
                "    ListNode next;\n" +
                "    ListNode() {\n" +
                "    }\n" +
                "    ListNode(int val) {\n" +
                "        this.val = val;\n" +
                "    }\n" +
                "    ListNode(int val, ListNode next) {\n" +
                "        this.val = val;\n" +
                "        this.next = next;\n" +
                "    }\n" +
                "}\n" +
                "class Solution {\n" +
                "    public ListNode reverseList(ListNode head) {\n" +
                "        \n" +
                "    }\n" +
                "}");
        question.setTestCode("public static void main(String[] args) {\n" +
                "        Solution solution = new Solution();\n" +
                "        ListNode head = new ListNode(1);\n" +
                "        head.next = new ListNode(2);\n" +
                "        head.next.next = new ListNode(3);\n" +
                "        head.next.next.next = new ListNode(4);\n" +
                "        head = solution.reverseList(head);\n" +
                "        if (head.val == 4 && head.next.val == 3 && head.next.next.val == 2 && head.next.next.next.val == 1) {\n" +
                "            System.out.println(\"success\");\n" +
                "        } else {\n" +
                "            System.out.println(\"fail\");\n" +
                "        }\n" +
                "    }");
        questionDao.insertQuestion(question);
    }

    public static void testDelete() {
        QuestionDao questionDao = new QuestionDao();
        questionDao.deleteQuestion(2);
    }

    public static void testSelectAll() {
        QuestionDao questionDao = new QuestionDao();
        List<Question> questions = questionDao.selectAllQuestion();
        for (Question question : questions) {
            System.out.println(question);
        }
    }

    public static void testSelectOne() {
        QuestionDao questionDao = new QuestionDao();
        Question question = questionDao.selectQuestionById(1);
        System.out.println(question);
    }

    public static void main(String[] args) {
        //testSelectAll();
        //testSelectOne();
        //testDelete();
        //testSelectOne();
        testInsert();
        testInsert2();
        //testSelectAll();
    }
}
