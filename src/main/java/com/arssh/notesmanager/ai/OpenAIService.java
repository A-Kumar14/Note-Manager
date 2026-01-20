package com.arssh.notesmanager.ai;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OpenAIService implements AIService {
    private final OpenAiService service;
    private final String model;
    private boolean available;

    public OpenAIService(String apiKey) {
        this(apiKey, "gpt-3.5-turbo");
    }

    public OpenAIService(String apiKey, String model) {
        this.model = model;
        this.available = false;

        if (apiKey != null && !apiKey.isEmpty() && !apiKey.equals("your-api-key-here")) {
            try {
                this.service = new OpenAiService(apiKey, Duration.ofSeconds(30));
                this.available = true;
            } catch (Exception e) {
                System.err.println("Failed to initialize OpenAI service: " + e.getMessage());
                this.service = null;
            }
        } else {
            this.service = null;
        }
    }

    @Override
    public String generateSummary(String text) {
        if (!isAvailable()) {
            return "AI service not available. Please configure your API key.";
        }

        if (text == null || text.trim().isEmpty()) {
            return "No content to summarize.";
        }

        try {
            String prompt = "Summarize the following note in 2-3 concise sentences:\n\n" + text;

            ChatMessage message = new ChatMessage(ChatMessageRole.USER.value(), prompt);
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(model)
                    .messages(Arrays.asList(message))
                    .maxTokens(150)
                    .temperature(0.7)
                    .build();

            ChatCompletionResult result = service.createChatCompletion(request);
            return result.getChoices().get(0).getMessage().getContent().trim();
        } catch (Exception e) {
            System.err.println("Error generating summary: " + e.getMessage());
            return "Failed to generate summary: " + e.getMessage();
        }
    }

    @Override
    public List<String> generateTags(String text) {
        if (!isAvailable()) {
            return Arrays.asList("AI-unavailable");
        }

        if (text == null || text.trim().isEmpty()) {
            return new ArrayList<>();
        }

        try {
            String prompt = "Analyze this note and suggest 3-5 relevant tags/categories. " +
                    "Return ONLY the tags as a comma-separated list, no explanations.\n\nNote: " + text;

            ChatMessage message = new ChatMessage(ChatMessageRole.USER.value(), prompt);
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(model)
                    .messages(Arrays.asList(message))
                    .maxTokens(50)
                    .temperature(0.5)
                    .build();

            ChatCompletionResult result = service.createChatCompletion(request);
            String response = result.getChoices().get(0).getMessage().getContent().trim();

            // Parse comma-separated tags
            return Arrays.stream(response.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .limit(5)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error generating tags: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<String> extractTasks(String text) {
        if (!isAvailable()) {
            return Arrays.asList("AI service not available");
        }

        if (text == null || text.trim().isEmpty()) {
            return new ArrayList<>();
        }

        try {
            String prompt = "Extract all action items and tasks from this note. " +
                    "Return each task on a new line. If no tasks found, return 'No tasks found'.\n\nNote: " + text;

            ChatMessage message = new ChatMessage(ChatMessageRole.USER.value(), prompt);
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(model)
                    .messages(Arrays.asList(message))
                    .maxTokens(200)
                    .temperature(0.3)
                    .build();

            ChatCompletionResult result = service.createChatCompletion(request);
            String response = result.getChoices().get(0).getMessage().getContent().trim();

            if (response.equalsIgnoreCase("No tasks found") || response.isEmpty()) {
                return new ArrayList<>();
            }

            // Parse line-separated tasks
            return Arrays.stream(response.split("\n"))
                    .map(String::trim)
                    .map(s -> s.replaceFirst("^[-•*]\\s*", "")) // Remove bullet points
                    .map(s -> s.replaceFirst("^\\d+\\.\\s*", "")) // Remove numbered lists
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error extracting tasks: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public int suggestPriority(String text) {
        if (!isAvailable()) {
            return 3; // Default medium priority
        }

        if (text == null || text.trim().isEmpty()) {
            return 3;
        }

        try {
            String prompt = "Analyze this note and suggest a priority level from 1-5 " +
                    "(1=lowest, 5=highest/urgent). Consider urgency keywords like 'urgent', 'ASAP', 'critical'. " +
                    "Return ONLY a single number 1-5.\n\nNote: " + text;

            ChatMessage message = new ChatMessage(ChatMessageRole.USER.value(), prompt);
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(model)
                    .messages(Arrays.asList(message))
                    .maxTokens(10)
                    .temperature(0.3)
                    .build();

            ChatCompletionResult result = service.createChatCompletion(request);
            String response = result.getChoices().get(0).getMessage().getContent().trim();

            // Parse the priority number
            int priority = Integer.parseInt(response.replaceAll("[^0-9]", ""));
            return Math.max(1, Math.min(5, priority)); // Clamp between 1-5
        } catch (Exception e) {
            System.err.println("Error suggesting priority: " + e.getMessage());
            return 3; // Default to medium priority
        }
    }

    @Override
    public boolean isAvailable() {
        return available && service != null;
    }
}
