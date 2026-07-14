package com.example.service;

import com.example.model.Article;
import java.util.*;

public interface IArticleService {
    // 创建文章
    Article createArticle(String title, String content, Integer userId);

    // 获取所有文章（按时间倒序）
    List<Map<String, Object>> getAllArticles();

    // 根据 ID 获取单篇文章
    Article getArticleById(Integer id);
    // 获取某个用户的所有文章
    List<Article> getUserArticles(Integer userId);

    // 更新文章（只允许作者更新）
    Article updateArticle(Integer id, String title, String content, Integer userId);

    // 删除文章（只允许作者删除）
    void deleteArticle(Integer id, Integer userId);

    // 搜索文章（按标题模糊匹配）
    List<Map<String, Object>> searchArticles(String keyword);
}
