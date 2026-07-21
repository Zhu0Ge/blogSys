package com.example.controller;

import com.example.common.R;
import com.example.dto.UserVO;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;
import java.net.http.HttpClient;
import java.time.Duration;


import java.util.*;

@RestController
@RequestMapping("/api/oauth")
public class OAuthController {

    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.client.secret}")
    private String clientSecret;

    @Value("${github.redirect.uri}")
    private String redirectUri;

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public OAuthController(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    // 前端调这个接口获取 GitHub 登录的跳转链接
    @GetMapping("/github/authorize")
    public R<Map<String, String>> getAuthUrl() {
        String url = String.format(
            "https://github.com/login/oauth/authorize?client_id=%s&redirect_uri=%s&scope=user:email",
            clientId, redirectUri
        );
        return R.success(Map.of("authUrl", url));
    }

    // GitHub 回调
    @GetMapping("/github/callback")
    public RedirectView callback(@RequestParam String code) {
        // 1. 用 code 换 access_token
        String tokenUrl = "https://github.com/login/oauth/access_token";
        // 替换：RestTemplate rest = new RestTemplate();

        // 配置超时（毫秒）
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(15000);    // 连接超时 15 秒
        factory.setReadTimeout(15000);       // 读取超时 15 秒
        RestTemplate rest = new RestTemplate(factory);
        // 设置 User-Agent（GitHub 要求）
        rest.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().set("User-Agent", "BlogSys/1.0");
            return execution.execute(request, body);
        });

        // 构造 form 表单参数
        org.springframework.util.LinkedMultiValueMap<String, String> body = new org.springframework.util.LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("code", code);

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Accept", "application/json");

        org.springframework.http.HttpEntity<org.springframework.util.LinkedMultiValueMap<String, String>> requestEntity =
            new org.springframework.http.HttpEntity<>(body, headers);

        @SuppressWarnings("unchecked")
        Map<String, Object> tokenResponse = rest.exchange(
            tokenUrl,
            org.springframework.http.HttpMethod.POST,
            requestEntity,
            Map.class
        ).getBody();

        String accessToken = (String) tokenResponse.get("access_token");

        // 2. 用 access_token 查用户信息
        headers.setBearerAuth(accessToken);
        org.springframework.http.HttpEntity<?> entity = new org.springframework.http.HttpEntity<>(headers);

        @SuppressWarnings("unchecked")
        Map<String, Object> githubUser = rest.exchange(
            "https://api.github.com/user",
            org.springframework.http.HttpMethod.GET,
            entity,
            Map.class
        ).getBody();

        String githubId = githubUser.get("id").toString();
        String username = (String) githubUser.get("login");
        String avatarUrl = (String) githubUser.get("avatar_url");

        // 3. 查是否已有该 GitHub 用户
        Optional<User> existingUser = userRepository.findByGithubId(githubId);
        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
        } else {
            // 4. 没绑定过 → 创建新用户
            user = new User();
            user.setUsername(username);
            user.setEmail(githubId + "@github.oauth");
            user.setPassword("");
            user.setGithubId(githubId);
            user.setAvatar(avatarUrl);
            user = userRepository.save(user);
        }

        // 5. 生成 JWT
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", new UserVO(user));

        RedirectView redirect = new RedirectView("http://localhost:5173/oauth/callback?token=" + token);
        return redirect;
    }
}