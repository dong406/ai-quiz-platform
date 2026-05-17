-- 数据库初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS tengke_question DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE tengke_question;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_account` varchar(256) NOT NULL COMMENT '账号',
  `user_password` varchar(512) NOT NULL COMMENT '密码',
  `user_name` varchar(256) DEFAULT NULL COMMENT '用户昵称',
  `user_avatar` varchar(1024) DEFAULT NULL COMMENT '用户头像',
  `user_role` varchar(256) DEFAULT 'user' COMMENT '用户角色：user/admin',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_account` (`user_account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户';

-- 应用表
CREATE TABLE IF NOT EXISTS `app` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `app_name` varchar(256) NOT NULL COMMENT '应用名称',
  `app_desc` text COMMENT '应用描述',
  `app_cover` varchar(1024) DEFAULT NULL COMMENT '应用封面',
  `app_type` int NOT NULL COMMENT '应用类型（0-打分类，1-测评类）',
  `scoring_strategy` int NOT NULL COMMENT '评分策略（0-自定义打分，1-自定义测评，2-AI测评）',
  `review_status` int DEFAULT 0 COMMENT '审核状态（0-待审核，1-通过，2-拒绝）',
  `review_message` varchar(512) DEFAULT NULL COMMENT '审核信息',
  `user_id` bigint NOT NULL COMMENT '创建用户 id',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应用';

-- 题目表
CREATE TABLE IF NOT EXISTS `question` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `app_id` bigint NOT NULL COMMENT '应用 id',
  `question_content` text NOT NULL COMMENT '题目内容（JSON格式）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_app_id` (`app_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='题目';

-- 用户答题记录表
CREATE TABLE IF NOT EXISTS `user_answer_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_id` bigint NOT NULL COMMENT '用户 id',
  `app_id` bigint NOT NULL COMMENT '应用 id',
  `choices` text NOT NULL COMMENT '答题选项（JSON数组）',
  `total_score` int DEFAULT NULL COMMENT '总分（打分类）',
  `result_name` varchar(256) DEFAULT NULL COMMENT '结果名称（测评类）',
  `result_desc` text DEFAULT NULL COMMENT '结果描述（测评类）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_app_id` (`app_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户答题记录';

-- 插入默认管理员账号（密码：admin123456）
INSERT INTO `user` (`user_account`, `user_password`, `user_role`, `user_name`) VALUES
('admin', 'a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6', 'admin', '管理员');


UPDATE app
SET app_type = 1, scoring_strategy = 2
WHERE id = 20;
