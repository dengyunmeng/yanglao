package com.elderlycare.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("news_favorite")
public class NewsFavorite {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long newsId;

    private LocalDateTime createTime;
}
