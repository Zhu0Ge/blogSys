package com.example.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateArticleRequest(
    @NotBlank(message = "标题不能为空") String title,
    @NotBlank(message = "内容不能为空") String content
) {}