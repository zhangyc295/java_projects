package api;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.Question;
import dao.QuestionDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@WebServlet("/question")
public class QuestionServlet extends HttpServlet {
    private final ObjectMapper mapper = new ObjectMapper();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("收到请求");
        QuestionDao dao = new QuestionDao();
        resp.setContentType("application/json;charset=utf-8");
        if (req.getParameter("id") == null || Objects.equals(req.getParameter("id"), "")) {
            //获取题目列表
            List<Question> questionList = dao.selectAllQuestion();
            String json = mapper.writeValueAsString(questionList);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(json);
        }else {
            //查询题目详情
            Question question = dao.selectQuestionById(Integer.parseInt(req.getParameter("id")));
            String json = mapper.writeValueAsString(question);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(json);
        }
    }
}
