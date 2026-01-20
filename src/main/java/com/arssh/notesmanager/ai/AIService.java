package com.arssh.notesmanager.ai;

import java.util.List;

public interface AIService {
    /**
     * Generate a summary of the given text
     * @param text The text to summarize
     * @return A concise summary
     */
    String generateSummary(String text);

    /**
     * Generate relevant tags for the given text
     * @param text The text to analyze
     * @return List of suggested tags
     */
    List<String> generateTags(String text);

    /**
     * Extract action items and tasks from text
     * @param text The text to analyze
     * @return List of extracted tasks
     */
    List<String> extractTasks(String text);

    /**
     * Analyze text and suggest a priority level (1-5)
     * @param text The text to analyze
     * @return Suggested priority (1=lowest, 5=highest)
     */
    int suggestPriority(String text);

    /**
     * Check if the AI service is available and configured
     * @return true if service is ready
     */
    boolean isAvailable();
}
