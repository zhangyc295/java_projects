package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.Result;
import parser.Searcher;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/searcher")
public class DocSearcherServlet extends HttpServlet {
    private static final Searcher searcher = new Searcher();
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String query = req.getParameter("query");
        if (query == null || query.isEmpty()) {
            System.out.println("No query found");
            resp.setStatus(404);
            return;
        }
        System.out.println("Searching for: " + query);
        List<Result> list = searcher.search(query);

        resp.setContentType("application/json;charset=UTF-8");
        mapper.writeValue(resp.getWriter(), list);
    }
}
