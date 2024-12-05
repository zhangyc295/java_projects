package org.example.java_doc_searcher_spring.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.java_doc_searcher_spring.model.Result;
import org.example.java_doc_searcher_spring.parser.Searcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DocSearcherController {
    private static Searcher searcher = new Searcher();

    private ObjectMapper mapper = new ObjectMapper();

    @RequestMapping(value = "/searcher", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String search(@RequestParam("query") String query) throws JsonProcessingException {
        List<Result> list = searcher.search(query);
        System.out.println("Searching for: " + query);
        return mapper.writeValueAsString(list);
    }
//    public List<Result> search(@RequestParam("query") String query) throws JsonProcessingException {
//        List<Result> list = searcher.search(query);
//        System.out.println("Searching for: " + query);
//        System.out.println(list);
//        return list;
}
