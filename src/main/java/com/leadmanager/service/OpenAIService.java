package com.leadmanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leadmanager.model.Lead;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
public class OpenAIService {

    @Value("${openai.api.key:}")
    private String apiKey;

    @Value("${openai.api.url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public OpenAIService() {

        this.client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

        this.objectMapper = new ObjectMapper();
    }

    public String generatePersonalizedReply(Lead lead) {

        if (apiKey == null
                || apiKey.isEmpty()
                || apiKey.equals("your-openai-api-key-here")) {

            return null;
        }

        try {

            String prompt = buildPrompt(lead);
            String requestBody = buildRequestBody(prompt);

            Request request = new Request.Builder()
                .url(apiUrl)
                .post(
                    RequestBody.create(
                        requestBody,
                        MediaType.parse("application/json")
                    )
                )
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .build();

            try (Response response =
                    client.newCall(request).execute()) {

                if (response.isSuccessful()
                        && response.body() != null) {

                    String responseBody =
                        response.body().string();

                    return extractMessageFromResponse(
                        responseBody
                    );

                } else {

                    System.err.println(
                        "OpenAI API error: "
                        + response.code()
                    );

                    return null;
                }
            }

        } catch (Exception e) {

            System.err.println(
                "Error calling OpenAI API: "
                + e.getMessage()
            );

            return null;
        }
    }

    private String buildPrompt(Lead lead) {

        return String.format(
            "Write a professional and friendly auto-reply " +
            "email for a customer named %s " +
            "who inquired about %s. " +
            "Their message was: \"%s\". " +
            "Thank them for reaching out, acknowledge " +
            "their inquiry, and let them know " +
            "someone from our team will respond within " +
            "24-48 hours. Keep it concise and warm.",
            lead.getName(),
            lead.getBusinessType(),
            lead.getMessage()
        );
    }

    private String buildRequestBody(String prompt)
            throws IOException {

        return String.format(
            "{"
                + "\"model\":\"gpt-3.5-turbo\","
                + "\"messages\":[{"
                + "\"role\":\"user\","
                + "\"content\":\"%s\""
                + "}],"
                + "\"max_tokens\":300,"
                + "\"temperature\":0.7"
                + "}",
            escapeJson(prompt)
        );
    }

    private String extractMessageFromResponse(
            String responseBody) throws IOException {

        JsonNode root =
            objectMapper.readTree(responseBody);

        JsonNode choices = root.get("choices");

        if (choices != null
                && choices.isArray()
                && choices.size() > 0) {

            JsonNode message =
                choices.get(0).get("message");

            if (message != null) {
                return message.get("content").asText();
            }
        }

        return null;
    }

    private String escapeJson(String text) {

        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}