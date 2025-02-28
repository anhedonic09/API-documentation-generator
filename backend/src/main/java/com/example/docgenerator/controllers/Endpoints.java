package com.example.docgenerator.controllers;

import com.example.docgenerator.service.GeneratePdf;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/doc")
public class Endpoints {
    @Autowired
    private GeneratePdf generatePdf;

    @PostMapping("/download")
    public void makeDoc(@RequestBody String payload, HttpServletResponse response) throws JsonProcessingException {
        Map<String, Object> map = new ObjectMapper().readValue(payload, HashMap.class);
        List<Map<String, Object>>  payloadList = (List<Map<String, Object>>) map.get("endpoints");
        generatePdf.generateDoc(payloadList, response);
        System.out.println("Working");

    }



}
