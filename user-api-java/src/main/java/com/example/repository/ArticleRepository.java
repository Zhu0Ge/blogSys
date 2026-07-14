package com.example.repository;

import com.example.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Integer> {
    // 按用户 ID 查所有文章
    List<Article> findByUserIdOrderByCreatedAtDesc(Integer userId);

    // 查询所有文章，按时间倒序
    List<Article> findAllByOrderByCreatedAtDesc();

    // 按标题模糊搜索（不区分大小写），按时间倒序
    List<Article> findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(String keyword);
}