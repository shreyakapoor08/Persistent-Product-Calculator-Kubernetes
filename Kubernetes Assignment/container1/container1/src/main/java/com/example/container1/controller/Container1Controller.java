// Container 1 Controller
package com.example.container1.controller;

import com.example.container1.entity.FileStorageRequest;
import com.example.container1.entity.Container1Request;
import com.example.container1.exception.InvalidInputException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
public class Container1Controller {

    private static final String CONTAINER2_URL = "http://localhost:8080/calculatesum";
    private static final String STORAGE_PATH = "/Users/lib-user/Downloads/volume/";

    private final RestTemplate restTemplate;

    public Container1Controller(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/store-file")
    public ResponseEntity<Object> storeFile(@RequestBody FileStorageRequest request) {
        try {
            validateInput(request.getFile());

            String filePath = STORAGE_PATH + request.getFile();
            System.out.println("File path is: " + filePath);
            Files.write(Paths.get(filePath), request.getData().getBytes());

            return ResponseEntity.ok(createResponse(request.getFile(), "Success."));
        } catch (InvalidInputException | IOException e) {
            return ResponseEntity.badRequest().body(createResponse(request.getFile(), "Error while storing the file to the storage."));
        }
    }

    @PostMapping("/calculate")
    public ResponseEntity<Map<String,Object>> calculate(@RequestBody Container1Request request) {
        try {
            validateInput(request.getFile());
            return sendRequestToContainer2(request.getFile(), request.getProduct());
        } catch (InvalidInputException e) {
            Map<String,Object> response = new HashMap<>();
            response.put("file", request.getFile());
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    private void validateInput(String fileName) throws InvalidInputException {
        if (fileName == null) {
            throw new InvalidInputException("Invalid JSON input.");
        }
    }

    private ResponseEntity< Map<String,Object> > sendRequestToContainer2(String file, String product) {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("file", file);
            requestBody.put("product", product);

            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);
        ParameterizedTypeReference<Map<String, Object>> responseType =
                new ParameterizedTypeReference<Map<String, Object>>() {};

        // Make the POST request
        try{
            ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                    CONTAINER2_URL,
                    HttpMethod.POST,
                    requestEntity,
                    responseType);
            return responseEntity;
        } catch (Exception e){
            System.out.println(e.getMessage());
            return  null;
        }

    }

    private Map<String, String> createResponse(String fileName, String message) {
        Map<String, String> result = new HashMap<>();
        result.put("file", fileName);
        result.put("message", message);
        return result;
    }
}
