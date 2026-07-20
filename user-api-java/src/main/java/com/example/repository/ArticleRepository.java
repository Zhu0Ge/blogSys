package com.example.repository;

import com.example.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArticleRepository extends JpaRepository<Article, Integer> {
    // 按用户 ID 查所有文章
    @EntityGraph(attributePaths = {"user"})
    List<Article> findByUserIdOrderByCreatedAtDesc(Integer userId);

    // 查询所有文章，按时间倒序
    @EntityGraph(attributePaths = {"user"})
    List<Article> findAllByOrderByCreatedAtDesc();

    // 按标题模糊搜索（不区分大小写），按时间倒序
    @EntityGraph(attributePaths = {"user"})
    List<Article> findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(String keyword);
    
    @EntityGraph(attributePaths = {"user"})
    Page<Article> findAllBy(Pageable pageable);
}