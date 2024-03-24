// Container 1 Controller
package com.example.container1.controller;

import com.example.container1.entity.FileStorageRequest;
import com.example.container1.entity.Container1Request;
import com.example.container1.exception.InvalidInputException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/container1")
public class Container1Controller {

//    private static final String CONTAINER2_URL = "http://container2-service:8080/calculatesum";
    private static final String CONTAINER2_URL = "http://localhost:8080/calculatesum";
private static final String STORAGE_PATH = "E:\\DALHOUSIE COURSEWORK\\Cloud Computing\\Kubernetes Assignment\\container1\\container1\\";
//    private static final String STORAGE_PATH = "/home/volume/";

    private final RestTemplate restTemplate;

    public Container1Controller(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/store-file")
    public ResponseEntity<Object> storeFile(@RequestBody FileStorageRequest request) {
        try {
            validateFileStorageRequest(request);

            // Store the file in the persistent storage
            String filePath = STORAGE_PATH + request.getFile();
            System.out.println("File path is: " + filePath);
            Files.write(Paths.get(filePath), request.getData().getBytes());

            return ResponseEntity.ok(createFileStorageResponse(request.getFile(), "Success."));
        } catch (InvalidInputException | IOException e) {
            return ResponseEntity.badRequest().body(createFileStorageResponse(request.getFile(), "Error while storing the file to the storage."));
        }
    }

    @PostMapping("/calculate")
    public ResponseEntity<Object> calculate(@RequestBody Container1Request request) {
        try {
            validateInput(request);

            ResponseEntity<Integer> responseEntity = sendRequestToContainer2(request.getProduct(), request.getFile()); //filename

            return ResponseEntity.status(responseEntity.getStatusCode()).body(createResult(request.getFile(), "", responseEntity.getBody()));
        } catch (InvalidInputException e) {
            return ResponseEntity.badRequest().body(createResult(request.getFile(), e.getMessage(), null));
        }
    }

    private void validateFileStorageRequest(FileStorageRequest request) throws InvalidInputException {
        if (request.getFile() == null) {
            throw new InvalidInputException("Invalid JSON input.");
        }
    }

    private void validateInput(Container1Request request) throws InvalidInputException {
        if (request.getFile() == null) {
            throw new InvalidInputException("Invalid JSON input.");
        }
    }

    private ResponseEntity<Integer> sendRequestToContainer2(String product, String file) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("file", file);
            requestBody.put("product", product);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

            return restTemplate.postForEntity(CONTAINER2_URL, entity, Integer.class);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(-2);
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

    private Map<String, Object> createFileStorageResponse(String fileName, String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("file", fileName);
        result.put("message", message);
        return result;
    }
}
