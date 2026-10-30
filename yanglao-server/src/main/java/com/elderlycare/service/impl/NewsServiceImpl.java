package com.elderlycare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderlycare.common.BusinessException;
import com.elderlycare.common.PageResult;
import com.elderlycare.common.Result;
import com.elderlycare.dto.NewsQueryDTO;
import com.elderlycare.entity.News;
import com.elderlycare.entity.NewsCategory;
import com.elderlycare.entity.NewsFavorite;
import com.elderlycare.mapper.NewsCategoryMapper;
import com.elderlycare.mapper.NewsFavoriteMapper;
import com.elderlycare.mapper.NewsMapper;
import com.elderlycare.service.NewsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class NewsServiceImpl implements NewsService {

    @Resource
    private NewsMapper newsMapper;

    @Resource
    private NewsCategoryMapper newsCategoryMapper;

    @Resource
    private NewsFavoriteMapper newsFavoriteMapper;

    @Override
    public Result<List<?>> categories() {
        List<NewsCategory> list = newsCategoryMapper.selectList(
                new QueryWrapper<NewsCategory>().orderByAsc("sort"));
        return Result.success(list);
    }

    @Override
    public Result<PageResult<News>> page(NewsQueryDTO query) {
        QueryWrapper<News> qw = new QueryWrapper<>();
        if (query.getCategory() != null && !query.getCategory().isEmpty()) {
            qw.eq("category", query.getCategory());
        }
        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            qw.like("title", query.getKeyword());
        }
        qw.eq("status", 1).orderByDesc("publish_time").orderByDesc("create_time");
        Page<News> p = new Page<>(query.getPage(), query.getPageSize());
        Page<News> result = newsMapper.selectPage(p, qw);
        return Result.success(new PageResult<>(result.getRecords(), result.getTotal(), query.getPage(), query.getPageSize()));
    }

    @Override
    public Result<News> detail(Long id) {
        News news = newsMapper.selectById(id);
        if (news == null) throw new BusinessException(404, "资讯不存在");
        if (news.getViewCount() == null) news.setViewCount(0);
        news.setViewCount(news.getViewCount() + 1);
        newsMapper.updateById(news);
        return Result.success(news);
    }

    @Override
    public Result<PageResult<News>> search(String keyword, Integer page, Integer pageSize) {
        QueryWrapper<News> qw = new QueryWrapper<>();
        qw.and(w -> w.like("title", keyword).or().like("content", keyword));
        qw.eq("status", 1).orderByDesc("view_count");
        Page<News> p = new Page<>(page, pageSize);
        Page<News> result = newsMapper.selectPage(p, qw);
        return Result.success(new PageResult<>(result.getRecords(), result.getTotal(), page, pageSize));
    }

    @Override
    public Result<List<News>> hot(Integer limit) {
        QueryWrapper<News> qw = new QueryWrapper<>();
        qw.eq("status", 1).eq("is_hot", 1).orderByDesc("view_count").last("LIMIT " + (limit == null ? 5 : limit));
        List<News> list = newsMapper.selectList(qw);
        if (list.isEmpty()) {
            qw = new QueryWrapper<>();
            qw.eq("status", 1).orderByDesc("view_count").last("LIMIT " + (limit == null ? 5 : limit));
            list = newsMapper.selectList(qw);
        }
        return Result.success(list);
    }

    @Override
    public Result<Void> addFavorite(Long userId, Long newsId) {
        QueryWrapper<NewsFavorite> qw = new QueryWrapper<>();
        qw.eq("user_id", userId).eq("news_id", newsId);
        if (newsFavoriteMapper.selectCount(qw) > 0) {
            throw new BusinessException(400, "已收藏");
        }
        NewsFavorite nf = new NewsFavorite();
        nf.setUserId(userId);
        nf.setNewsId(newsId);
        newsFavoriteMapper.insert(nf);
        return Result.success();
    }

    @Override
    public Result<Void> removeFavorite(Long userId, Long newsId) {
        QueryWrapper<NewsFavorite> qw = new QueryWrapper<>();
        qw.eq("user_id", userId).eq("news_id", newsId);
        newsFavoriteMapper.delete(qw);
        return Result.success();
    }

    @Override
    public Result<List<News>> myFavorites(Long userId) {
        List<NewsFavorite> favs = newsFavoriteMapper.selectList(new QueryWrapper<NewsFavorite>().eq("user_id", userId));
        if (favs.isEmpty()) return Result.success(new ArrayList<>());
        List<Long> ids = favs.stream().map(NewsFavorite::getNewsId).toList();
        List<News> list = newsMapper.selectBatchIds(ids);
        return Result.success(list);
    }

    @Override
    public Result<List<News>> pending() {
        List<News> list = newsMapper.selectList(new QueryWrapper<News>()
                .eq("status", 0).orderByAsc("create_time"));
        return Result.success(list);
    }

    @Override
    public Result<Void> review(Long id, boolean approved) {
        News news = newsMapper.selectById(id);
        if (news == null) throw new BusinessException(404, "资讯不存在");
        news.setStatus(approved ? 1 : 2);
        if (approved && news.getPublishTime() == null) {
            news.setPublishTime(java.time.LocalDateTime.now());
        }
        newsMapper.updateById(news);
        return Result.success();
    }
}
