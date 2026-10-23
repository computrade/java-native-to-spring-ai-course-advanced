package com.computrade.course.spring.ai.claude.service;

import com.opencsv.CSVReaderHeaderAware;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VectorDBService {

    String SOURCE_KEY_WORD = "source";

    private final ChatClient chatClient;
    private final EmbeddingModel embeddingModel;
    private final VectorStore vectorStore;

    @Value("classpath:systemPromptForCourse.st")
    private Resource courseSystemPrompt;

    @Value("classpath:data/courses_dataset.csv")
    private Resource csvResource;


    public Map<String,EmbeddingResponse> embed(String prompt) {
        EmbeddingResponse embeddingResponse = embeddingModel.embedForResponse(List.of(prompt));
        return Map.of("embedding", embeddingResponse);
    }

    public void ingestCoursesToVectorStore() {
        List<Document> documents = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(csvResource.getInputStream(), StandardCharsets.UTF_8));
             CSVReaderHeaderAware csvReader = new CSVReaderHeaderAware(bufferedReader)) {

            Map<String, String> values;
            while ((values = csvReader.readMap()) != null) {
                String id = values.get("ID");
                String courseName = values.get("Course Name");
                String category = values.get("Category");
                String description = values.get("Description");
                String rating = values.get("Rating");
                String noOfStudents = values.get("No of Students");
                String totalHours = values.get("Total Course Hours");

                // Create content for the embedding
                String content = String.format("Course: %s. Category: %s. Description: %s",
                        courseName, category, description);

                // Create metadata for the Vector DB.
                Map<String, Object> metadata = new HashMap<>();
                metadata.put(SOURCE_KEY_WORD, Objects.requireNonNull(csvResource.getFilename()));
                metadata.put("course_id", id);
                metadata.put("category", category);
                metadata.put("rating", Double.parseDouble(rating));
                metadata.put("students", Integer.parseInt(noOfStudents));
                metadata.put("hours", Double.parseDouble(totalHours));

                Document document = new Document(content, metadata);
                documents.add(document);
            }

            // Push all documents to the Vector Store.
            if (!documents.isEmpty()) {
                vectorStore.accept(documents);
                log.info("Successfully ingested {} courses using OpenCSV.", documents.size());
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse CSV and upload to vector store via OpenCSV", e);
        }
    }



    public String queryVectorStore(String convId, String prompt) {

        SearchRequest searchRequest = SearchRequest.builder()
                .query(prompt)
                .topK(3)
                .build();


        return getChatResponse(convId, prompt, searchRequest);
    }


    @Nullable
    private String getChatResponse(String convId, String prompt, SearchRequest searchRequest) {
        List<Document> similarDocs = vectorStore.similaritySearch(searchRequest);
        String similarDocsContent = similarDocs.stream()
                .map(Document::getText)
                .collect(Collectors.joining(System.lineSeparator()));

        String response = chatClient.prompt()
                .system(promptSystemSpec -> promptSystemSpec.text(courseSystemPrompt).param("courses", similarDocsContent))
                .user(prompt)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, convId))
                .call()
                .content();

        return response;
    }

}
