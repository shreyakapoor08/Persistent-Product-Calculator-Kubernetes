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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
public class Container2Controller {

    //    private static final String STORAGE_PATH = "/home/volume/";
//    private static final String STORAGE_PATH = "/Users/lib-user/Downloads/volume/";
    private static final String STORAGE_PATH = "E:\\DALHOUSIE COURSEWORK\\Cloud Computing\\Kubernetes Assignment\\container1\\container1\\";

    @PostMapping("/calculatesum")
    public ResponseEntity<Map<String,Object>> calculateSum(@RequestBody Map<String, String> request) {
        try {
            String fileName = request.get("file");
            String product = request.get("product");

            String filePath = STORAGE_PATH + fileName;
            System.out.println("FilePath --> " + filePath);
            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("Line - 35");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createResult(fileName, "File not found.", null));
            }

            String fileContent;
            try {
                fileContent = readFileContent(filePath);
            } catch (IOException e) {
                System.out.println("line 43");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createResult(fileName, "Error reading file content.", null));
            }

            int sum = 0;
            try {
                CSVReader reader = new CSVReader(new StringReader(fileContent));
                String[] nextLine;

                String[] headers = reader.readNext();
                System.out.println(Arrays.toString(headers));
                while ((nextLine = reader.readNext()) != null) {

                    if(nextLine.length > 2) {
                        System.out.println("Error --> line 58");
                        throw new Exception();
                    }
//
                    String rowProduct = nextLine[0].trim();
                    if (rowProduct.equals(product)) {
                        System.out.println("hello inside sum");
                        sum += Integer.parseInt(nextLine[1].trim());
                        System.out.println("sum is: " + sum);
                    }

                }
            } catch (Exception e) {
                System.out.println("line 51");
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
//    private int calculateSumFromCSV(String fileContent, String product) throws Exception {
//        try (CSVReader reader = new CSVReader(new StringReader(fileContent))) {
//            String[] nextLine;
//            int sum = 0;
//
//            String[] headers = reader.readNext();
//            System.out.println(Arrays.toString(headers));
//            if(headers.length > 2){
//                return -1;
//            }
//
//            while ((nextLine = reader.readNext()) != null) {
//
//                if(nextLine.length > 2) {
//                    return -1;
//                }
//                String rowProduct = nextLine[0];
//                if (rowProduct.equals(product)) {
//                    sum += Integer.parseInt(nextLine[1]);
//                }
//            }
//
//            return sum;
//        }
//    }
}