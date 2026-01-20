package com.arssh.notesmanager.ai;

import java.io.*;
import java.util.Properties;

public class AIConfig {
    private static final String CONFIG_FILE = System.getProperty("user.home") +
            File.separator + ".notesmanager" + File.separator + "ai-config.properties";

    private static AIConfig instance;
    private Properties properties;
    private AIService aiService;

    private AIConfig() {
        properties = new Properties();
        loadConfig();
        initializeAIService();
    }

    public static AIConfig getInstance() {
        if (instance == null) {
            instance = new AIConfig();
        }
        return instance;
    }

    private void loadConfig() {
        File configFile = new File(CONFIG_FILE);
        if (configFile.exists()) {
            try (FileInputStream fis = new FileInputStream(configFile)) {
                properties.load(fis);
            } catch (IOException e) {
                System.err.println("Failed to load AI config: " + e.getMessage());
            }
        }
    }

    private void saveConfig() {
        File configFile = new File(CONFIG_FILE);
        File parentDir = configFile.getParentFile();

        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            properties.store(fos, "Notes Manager AI Configuration");
        } catch (IOException e) {
            System.err.println("Failed to save AI config: " + e.getMessage());
        }
    }

    private void initializeAIService() {
        String apiKey = properties.getProperty("openai.api.key", "");
        String model = properties.getProperty("openai.model", "gpt-3.5-turbo");

        if (!apiKey.isEmpty() && !apiKey.equals("your-api-key-here")) {
            aiService = new OpenAIService(apiKey, model);
        } else {
            aiService = new OpenAIService("", model); // Create disabled service
        }
    }

    public void setApiKey(String apiKey) {
        properties.setProperty("openai.api.key", apiKey);
        saveConfig();
        initializeAIService();
    }

    public String getApiKey() {
        return properties.getProperty("openai.api.key", "");
    }

    public void setModel(String model) {
        properties.setProperty("openai.model", model);
        saveConfig();
        initializeAIService();
    }

    public String getModel() {
        return properties.getProperty("openai.model", "gpt-3.5-turbo");
    }

    public AIService getAIService() {
        return aiService;
    }

    public boolean isConfigured() {
        String apiKey = getApiKey();
        return apiKey != null && !apiKey.isEmpty() && !apiKey.equals("your-api-key-here");
    }
}
