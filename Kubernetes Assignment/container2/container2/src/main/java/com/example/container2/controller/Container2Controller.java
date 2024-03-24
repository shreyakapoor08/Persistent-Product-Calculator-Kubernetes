package com.example.container2.controller;
import com.opencsv.CSVReader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
public class Container2Controller {

//    private static final String STORAGE_PATH = "/home/volume/";
private static final String STORAGE_PATH = "E:\\DALHOUSIE COURSEWORK\\Cloud Computing\\Kubernetes Assignment\\container1\\container1\\";

    @PostMapping("/calculatesum")
    public ResponseEntity<Object> calculateSum(@RequestBody Map<String, String> request) {
        try {
            String fileName = request.get("file");
            String product = request.get("product");

            String filePath = STORAGE_PATH + fileName;
            File file = new File(filePath);
            if (!file.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createResult(fileName, "File not found.", null));
            }

            String fileContent;
            try {
                fileContent = readFileContent(filePath);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createResult(fileName, "Error reading file content.", null));
            }

            if (fileName == null || fileName.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createResult(null, "Invalid JSON input.", null));
            }

            int sum;
            try {
                sum = calculateSumFromCSV(fileContent, product);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createResult(fileName, "Input file not in CSV format.", null));
            }

            return ResponseEntity.ok(createResult(fileName, null, sum));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createResult(null, "Internal Server Error.", null));
        }
    }


    private Map<String, Object> createResult(String fileName, String errorMessage, Integer sum) {
        Map<String, Object> result = new HashMap<>();
        result.put("file", fileName);
        if (sum == null) {
            result.put("error", errorMessage);
        } else {
            result.put("sum", sum);
        }
        return result;
    }

        private String readFileContent(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return new String(Files.readAllBytes(path));
    }
    private int calculateSumFromCSV(String fileContent, String product) throws Exception {
        try (CSVReader reader = new CSVReader(new StringReader(fileContent))) {
            String[] nextLine;
            int sum = 0;

            String[] headers = reader.readNext();

            if(headers.length > 2){
                return -1;
            }

            while ((nextLine = reader.readNext()) != null) {

                if(nextLine.length > 2) {
                    return -1;
                }
                String rowProduct = nextLine[0];
                if (rowProduct.equals(product)) {
                    sum += Integer.parseInt(nextLine[1]);
                }
            }

            return sum;
        }
    }
}