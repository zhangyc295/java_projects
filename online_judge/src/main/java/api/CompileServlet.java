package api;

import com.fasterxml.jackson.databind.ObjectMapper;
import compile.*;
import dao.Question;
import dao.QuestionDao;
import myexception.CodeInValidException;
import myexception.QuestionNotFound;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@WebServlet("/compile")
public class CompileServlet extends HttpServlet {
    private ObjectMapper mapper = new ObjectMapper();

    static class CompileRequest {
        public int id;
        public String code;
    }

    static class CompileResponse {
        public ReturnValue returnValue;
        public String reason;
        public String stdout;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        CompileResponse response = new CompileResponse();
        CompileRequest request = new CompileRequest();

        try {
            //读取请求正文并解析
            int length = req.getContentLength();
            byte[] bytes = new byte[length];
            try (InputStream inputStream = req.getInputStream()) {
                inputStream.read(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String body = new String(bytes, StandardCharsets.UTF_8);
            request = mapper.readValue(body, CompileRequest.class);
            //根据id得到测试代码
            QuestionDao questionDao = new QuestionDao();
            Question question = questionDao.selectQuestionById(request.id);
            System.out.println(request.id);
            // 题目没有找到
            if (question == null) {
                throw new QuestionNotFound("Question not found");
            }
            String testCode = question.getTestCode();
            String requestCode = request.code;
            //将测试方法拼接
            String finalCode = merge(testCode, requestCode);
            if (finalCode == null) {
                throw new CodeInValidException("Invalid code");
            }
            //System.out.println(finalCode);
            //编译运行
            Task task = new Task();
            SrcCode srcCode = new SrcCode();
            srcCode.setCode(finalCode);
            Result result = task.compileRun(srcCode);
            //System.out.println(result);
            response = new CompileResponse();
            response.returnValue = result.getReturnValue();
            response.reason = result.getReason();
            response.stdout = result.getStdout();
        } catch (QuestionNotFound e) {
            response.returnValue = ReturnValue.QUESTION_NOT_FOUND;
            response.reason = "Question not found! id = " + request.id;
        } catch (CodeInValidException e) {
            response.returnValue = ReturnValue.CODE_INVALID;
            response.reason = "Invalid code";
        }finally {
            resp.setContentType("application/json");
            System.out.println(response);
            String json = mapper.writeValueAsString(response);
            resp.getWriter().write(json);
        }
    }

    private String merge(String testCode, String requestCode) {
        // 查找 { 的位置
        int pos = requestCode.lastIndexOf("}");
        if (pos == -1) {
            return null;
        }
        String code = requestCode.substring(0, pos);
        return code + testCode + "\n}";
    }
}
