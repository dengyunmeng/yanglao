package com.elderlycare.controller;

import com.elderlycare.common.PageResult;
import com.elderlycare.common.Result;
import com.elderlycare.dto.NewsQueryDTO;
import com.elderlycare.entity.News;
import com.elderlycare.service.NewsService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    @Resource
    private NewsService newsService;

    @GetMapping("/categories")
    public Result<List<?>> categories() {
        return newsService.categories();
    }

    @GetMapping("/list")
    public Result<PageResult<News>> list(NewsQueryDTO query) {
        return newsService.page(query);
    }

    @GetMapping("/{id}")
    public Result<News> detail(@PathVariable Long id) {
        return newsService.detail(id);
    }

    @GetMapping("/search")
    public Result<PageResult<News>> search(@RequestParam String keyword,
                                           @RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer pageSize) {
        return newsService.search(keyword, page, pageSize);
    }

    @GetMapping("/hot")
    public Result<List<News>> hot(@RequestParam(defaultValue = "5") Integer limit) {
        return newsService.hot(limit);
    }

    @PostMapping("/favorite")
    public Result<Void> addFavorite(@RequestBody Map<String, Long> body) {
        return newsService.addFavorite(getUserId(), body.get("newsId"));
    }

    @PostMapping("/favorite/remove")
    public Result<Void> removeFavorite(@RequestBody Map<String, Long> body) {
        return newsService.removeFavorite(getUserId(), body.get("newsId"));
    }

    @GetMapping("/favorites")
    public Result<List<News>> myFavorites() {
        return newsService.myFavorites(getUserId());
    }

    private Long getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Long) return (Long) auth.getPrincipal();
        return null;
    }
}
