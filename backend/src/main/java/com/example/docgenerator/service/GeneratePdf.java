package com.example.docgenerator.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

@Component
public class GeneratePdf {

    public void generateDoc(List<Map<String, Object>> requests, HttpServletResponse response) {
        int apiCount=0;
        try {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=APIDocumentation.pdf");

            OutputStream outputStream = response.getOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Define fonts (Bold for headings, Normal for values)
            Font heading = new Font(Font.FontFamily.HELVETICA,12,Font.BOLD);
            Font boldFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

            for (Map<String, Object> request : requests) {
                apiCount++;
                // Add a new page for each API request except for the first one
                if (apiCount > 1) {
                    document.newPage();
                }
                document.add(createBoldParagraph(apiCount+": ", request.getOrDefault("name", "N/A"),heading,heading));
                document.add(new Paragraph("\n"));

                document.add(createBoldParagraph("Endpoint: ", request.getOrDefault("url", "N/A"), boldFont, normalFont));
                document.add(createBoldParagraph("Method Type: ", request.getOrDefault("method", "N/A"), boldFont, normalFont));
                document.add(createBoldParagraph("Payload: ", request.getOrDefault("payload", "N/A"), boldFont, normalFont));
                document.add(createBoldParagraph("Headers: ", formatHeaders(request.get("headers")), boldFont, normalFont));
                document.add(createBoldParagraph("Response: ", request.getOrDefault("response", "N/A"), boldFont, normalFont));

                document.add(new Paragraph("\n-----------------------------------\n"));
            }
            document.close();
            outputStream.flush();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }

    private Paragraph createBoldParagraph(String label, Object value, Font boldFont, Font normalFont)
    {
        Chunk boldChunk = new Chunk(label, boldFont);  // Bold heading
        Chunk normalChunk = new Chunk(String.valueOf(value), normalFont); // Normal text
        Paragraph paragraph = new Paragraph();
        paragraph.add(boldChunk);
        paragraph.add(" "); // Space between label and value
        paragraph.add(normalChunk);
        return paragraph;
    }

    private String formatHeaders(Object headers)
    {
//        if (headers instanceof Map<?, ?>)
        if(headers instanceof List<?>) {
            StringBuilder formatted = new StringBuilder();
            formatted.append("[\n");

            List<?> headerList = (List<?>) headers;
            int size = headerList.size(); // Get total number of maps
            int count = 0; // Counter to track the current iteration

            for (Object ls : (List<?>) headers)      //we are iterating through the List<Map<?,?>>
            {
                if (ls instanceof Map<?, ?>)
                {
                    Map<?, ?> map = (Map<?, ?>) ls;
                    if(map.containsKey("key") && map.containsKey("value"))
                    {
                        formatted.append("{ ")
                                .append("\"")
                                .append(map.get("key"))
                                .append("\" : \"")
                                .append(map.get("value"))
                                .append("\" }");  // Close the JSON object

                        count++; // Increment counter

                        if (count < size) {
                            formatted.append(",\n"); // Add comma + newline only if it's NOT the last map
                        }
                    }
                }
            }
            formatted.append("\n]");
        return formatted.toString();
        }

     return headers!=null?headers.toString():"N?A";
    }
}
//
//                for (Map.Entry<?, ?> entry : ((Map<?, ?>) headers).entrySet())
//            {
//                if(!entry.getKey().equals("id"))
//                {
//                    formatted.append(entry.getKey()).append(": ").append(entry.getValue()).append("; ");
//                }
//            }
//            return formatted.toString();
//        }
//        return headers != null ? headers.toString() : "N/A";
