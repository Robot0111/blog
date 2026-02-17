package com.leolxthy.blog.controller;

import org.springframework.stereotype.Controller;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

@Controller
public class Home {
    public static void main(String[] args) {
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//        System.out.println(encoder.encode("123456"));
    	String apikey = "AIzaSyDCoBNVNeXvoUs9JK6YjV5Gpm5ChppVTPU";
        Client client = new Client.Builder()
        		.apiKey(apikey)
        		.build();
        
        GenerateContentResponse response =
            client.models.generateContent(
                "gemini-3-flash-preview",
                "Explain how AI works in a few words",
                null);

        System.out.println(response.text());
    }
	// The client gets the API key from the environment variable `GEMINI_API_KEY`.

}