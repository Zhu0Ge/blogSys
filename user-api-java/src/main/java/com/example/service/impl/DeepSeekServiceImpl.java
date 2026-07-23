package com.example.service.impl;

import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.example.common.R;
import com.example.dto.ArticleDTO;
import com.example.service.IArticleService;
import com.example.service.IDeepSeekService;

@Service
public class DeepSeekServiceImpl implements IDeepSeekService {
    @Value("${deepseek.api.key}")
    private String apiKey;

    @Value("${deepseek.api.url}")
    private String apiUrl;

    private final IArticleService articleService;

    public DeepSeekServiceImpl(IArticleService articleService) {
        this.articleService = articleService;
    }

    public Map<String, Object> chat(String userMessage) {
        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // === 第一阶段：让 AI 判断意图 + 提取关键词 ===
        List<Map<String, String>> stage1Messages = new ArrayList<>();
        stage1Messages.add(Map.of("role", "system", "content",
            "你是博客系统的 AI 助手。判断用户意图，只回复以下格式之一：\n" +
            "1. 如果用户在搜索/查找文章 → SEARCH:关键词\n" +
            "   例如 '帮我找Spring的文章' → SEARCH:Spring\n" +
            "   例如 '有哪些关于JWT的文章' → SEARCH:JWT\n" +
            "   例如 '测试相关的内容' → SEARCH:测试\n" +
            "2. 如果用户是聊天、提问知识、要求写文章等 → CHAT:你的回答\n" +
            "   直接回答用户，不要搜索文章"
        ));
        stage1Messages.add(Map.of("role", "user", "content", userMessage));

        Map<String, Object> stage1Body = new HashMap<>();
        stage1Body.put("model", "deepseek-chat");
        stage1Body.put("messages", stage1Messages);
        stage1Body.put("temperature", 0.1);

        HttpEntity<Map<String, Object>> stage1Entity = new HttpEntity<>(stage1Body, headers);
        
        System.out.println("\n========== 第一阶段输入（意图识别） ==========");
        System.out.println("用户问题: " + userMessage);
        System.out.println("========== 第一阶段输入结束 ==========\n");

        try {
            System.out.println("\n========== 第一阶段完整请求体 ==========");
            System.out.println(new com.fasterxml.jackson.databind.ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(stage1Body));
            System.out.println("=====================================\n");
        } catch (Exception e) {
            System.out.println("序列化请求体失败: " + e.getMessage());
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> stage1Resp = rest.exchange(apiUrl, HttpMethod.POST, stage1Entity, Map.class).getBody();

        try {
            System.out.println("\n========== 第一阶段完整响应 ==========");
            System.out.println(new com.fasterxml.jackson.databind.ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(stage1Resp));
            System.out.println("=====================================\n");
        } catch (Exception e) {
            System.out.println("序列化响应体失败: " + e.getMessage());
        }


        @SuppressWarnings("unchecked")
        String stage1Reply = (String) ((Map<String, Object>) ((List<Map<String, Object>>) stage1Resp.get("choices")).get(0).get("message")).get("content");

        System.out.println("\n========== 第一阶段输出（意图识别） ==========");
        System.out.println("意图识别结果: " + stage1Reply);
        System.out.println("========== 第一阶段输出结束 ==========\n");


        // === 第二阶段：根据意图处理 ===
        if (stage1Reply.startsWith("SEARCH:")) {
            // 用户想搜索 → 从文章库里搜
            String keyword = stage1Reply.substring(7).trim();
            R<List<ArticleDTO>> searchResult = articleService.searchArticlesFulltext(keyword);
            List<ArticleDTO> articles = searchResult.getData();

            StringBuilder context = new StringBuilder();
            if (articles != null && !articles.isEmpty()) {
                context.append("以下是博客中找到的相关文章：\n\n");
                for (int i = 0; i < Math.min(articles.size(), 5); i++) {
                    ArticleDTO a = articles.get(i);
                    context.append("【").append(i + 1).append("】").append(a.getTitle()).append("\n");
                    context.append(a.getContent().substring(0, Math.min(a.getContent().length(), 500))).append("...\n\n");
                }
            } else {
                context.append("博客中没有找到与\"").append(keyword).append("\"相关的文章。");
            }

            // 用搜索结果来回答
            List<Map<String, String>> stage2Messages = new ArrayList<>();
            stage2Messages.add(Map.of("role", "system", "content",
                "你是博客系统的 AI 助手。基于以下文章内容回答用户的问题。如果没找到相关文章，如实告知。"));
            String fullPrompt = "用户问题：" + userMessage + "\n\n搜索文章结果：\n" + context.toString();
            stage2Messages.add(Map.of("role", "user", "content", fullPrompt));

            Map<String, Object> stage2Body = new HashMap<>();
            stage2Body.put("model", "deepseek-chat");
            stage2Body.put("messages", stage2Messages);
            stage2Body.put("temperature", 0.7);

            System.out.println("\n========== 第二阶段输入（搜索+回答） ==========");
            System.out.println("关键词: " + keyword);
            System.out.println("搜索到的文章数: " + (articles != null ? articles.size() : 0));
            System.out.println(context.toString().substring(0, Math.min(context.length(), 200)));
            System.out.println("========== 第二阶段输入结束 ==========\n");
            
            try {
                System.out.println("\n========== 第二阶段完整请求体 ==========");
                System.out.println(new com.fasterxml.jackson.databind.ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(stage2Body));
                System.out.println("=====================================\n");
            } catch (Exception e) {
                System.out.println("序列化请求体失败: " + e.getMessage());
            }
            
            HttpEntity<Map<String, Object>> stage2Entity = new HttpEntity<>(stage2Body, headers);
            @SuppressWarnings("unchecked")
            Map<String, Object> stage2Resp = rest.exchange(apiUrl, HttpMethod.POST, stage2Entity, Map.class).getBody();

            try {
                System.out.println("\n========== 第二阶段完整响应 ==========");
                System.out.println(new com.fasterxml.jackson.databind.ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(stage2Resp));
                System.out.println("=====================================\n");
            } catch (Exception e) {
                System.out.println("序列化响应体失败: " + e.getMessage());
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> choices = (List<Map<String, Object>>) stage2Resp.get("choices");
            @SuppressWarnings("unchecked")
            Map<String, Object> msgMap = (Map<String, Object>) choices.get(0).get("message");
            String content = (String) msgMap.get("content");
            Map<String, Object> result = new HashMap<>();
            result.put("reply", content);
            result.put("articles", articles);
            return result;
        } else {
            // 聊天/创作/知识问答 → 直接返回 AI 的回答（去掉 CHAT: 前缀）
            Map<String, Object> result = new HashMap<>();
        result.put("reply", 
            stage1Reply.startsWith("CHAT:") ? stage1Reply.substring(5).trim() : stage1Reply);
            return result;
        }
    }
}
