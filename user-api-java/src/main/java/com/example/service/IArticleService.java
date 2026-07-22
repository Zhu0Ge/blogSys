package com.example.service;

import com.example.dto.ArticleDTO;
import com.example.common.R;

import java.util.*;

public interface IArticleService {
    // 创建文章
    R<ArticleDTO> createArticle(String title, String content, Integer userId);

    // 获取所有文章（按时间倒序）
    R<List<ArticleDTO>> getAllArticles();

    // 根据 ID 获取单篇文章
    R<ArticleDTO> getArticleById(Integer id);
    // 获取某个用户的所有文章
    R<List<ArticleDTO>> getUserArticles(Integer userId);

    // 更新文章（只允许作者更新）
    R<ArticleDTO> updateArticle(Integer id, String title, String content, Integer userId);

    // 删除文章（只允许作者删除）
    R<Void> deleteArticle(Integer id, Integer userId);

    // 搜索文章（按标题模糊匹配）
    R<List<ArticleDTO>> searchArticles(String keyword);

    // 全文搜索文章
    R<List<ArticleDTO>> searchArticlesFulltext(String keyword);

    // 分页查询文章（按时间倒序）
    R<Map<String, Object>> getArticlesPaged(int page, int size);
}
