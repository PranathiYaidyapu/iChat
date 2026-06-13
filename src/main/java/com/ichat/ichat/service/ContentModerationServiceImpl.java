package com.ichat.ichat.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ContentModerationServiceImpl implements ContentModerationService{
    @Value("${ichat.moderation.endpoint}")
    private String moderationURL;



    @Override
    public boolean verifyContentSafe(String payload) {
        boolean isContentSafe = false;
        RestTemplate restTemplate = new RestTemplate();



        String json = "{ \"prompt\": \""+payload+"\" }";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(json, headers);
        try{

        ResponseEntity<String> response = restTemplate.postForEntity(moderationURL, entity, String.class);

       if(response.getStatusCode().is2xxSuccessful()){
           String  responseBody = response.getBody();
           Gson gson = new Gson();
           JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);
           if("SAFE".equalsIgnoreCase(jsonObject.get("safety").getAsString())){
               isContentSafe = true;
           }
       }
        }catch(Exception ignored){
           ignored.printStackTrace();
        }
        return isContentSafe;
    }
}
