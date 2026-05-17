-- 数据库字段名迁移脚本（从驼峰命名改为下划线命名）
-- 如果数据库已经存在，执行此脚本进行迁移

USE tengke_question;

-- 用户表字段重命名
ALTER TABLE `user` 
  CHANGE COLUMN `userAccount` `user_account` varchar(256) NOT NULL COMMENT '账号',
  CHANGE COLUMN `userPassword` `user_password` varchar(512) NOT NULL COMMENT '密码',
  CHANGE COLUMN `userName` `user_name` varchar(256) DEFAULT NULL COMMENT '用户昵称',
  CHANGE COLUMN `userAvatar` `user_avatar` varchar(1024) DEFAULT NULL COMMENT '用户头像',
  CHANGE COLUMN `userRole` `user_role` varchar(256) DEFAULT 'user' COMMENT '用户角色：user/admin',
  CHANGE COLUMN `createTime` `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  CHANGE COLUMN `updateTime` `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  CHANGE COLUMN `isDelete` `is_delete` tinyint DEFAULT 0 COMMENT '是否删除';

-- 删除旧索引并创建新索引
ALTER TABLE `user` DROP INDEX IF EXISTS `uk_userAccount`;
ALTER TABLE `user` ADD UNIQUE KEY `uk_user_account` (`user_account`);

-- 应用表字段重命名
ALTER TABLE `app` 
  CHANGE COLUMN `appName` `app_name` varchar(256) NOT NULL COMMENT '应用名称',
  CHANGE COLUMN `appDesc` `app_desc` text COMMENT '应用描述',
  CHANGE COLUMN `appCover` `app_cover` varchar(1024) DEFAULT NULL COMMENT '应用封面',
  CHANGE COLUMN `appType` `app_type` int NOT NULL COMMENT '应用类型（0-打分类，1-测评类）',
  CHANGE COLUMN `scoringStrategy` `scoring_strategy` int NOT NULL COMMENT '评分策略（0-自定义打分，1-自定义测评，2-AI测评）',
  CHANGE COLUMN `reviewStatus` `review_status` int DEFAULT 0 COMMENT '审核状态（0-待审核，1-通过，2-拒绝）',
  CHANGE COLUMN `reviewMessage` `review_message` varchar(512) DEFAULT NULL COMMENT '审核信息',
  CHANGE COLUMN `userId` `user_id` bigint NOT NULL COMMENT '创建用户 id',
  CHANGE COLUMN `createTime` `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  CHANGE COLUMN `updateTime` `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  CHANGE COLUMN `isDelete` `is_delete` tinyint DEFAULT 0 COMMENT '是否删除';

-- 题目表字段重命名
ALTER TABLE `question` 
  CHANGE COLUMN `appId` `app_id` bigint NOT NULL COMMENT '应用 id',
  CHANGE COLUMN `questionContent` `question_content` text NOT NULL COMMENT '题目内容（JSON格式）',
  CHANGE COLUMN `createTime` `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  CHANGE COLUMN `updateTime` `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  CHANGE COLUMN `isDelete` `is_delete` tinyint DEFAULT 0 COMMENT '是否删除';

-- 删除旧索引并创建新索引
ALTER TABLE `question` DROP INDEX IF EXISTS `idx_appId`;
ALTER TABLE `question` ADD KEY `idx_app_id` (`app_id`);

-- 用户答题记录表字段重命名
ALTER TABLE `user_answer_record` 
  CHANGE COLUMN `userId` `user_id` bigint NOT NULL COMMENT '用户 id',
  CHANGE COLUMN `appId` `app_id` bigint NOT NULL COMMENT '应用 id',
  CHANGE COLUMN `totalScore` `total_score` int DEFAULT NULL COMMENT '总分（打分类）',
  CHANGE COLUMN `resultName` `result_name` varchar(256) DEFAULT NULL COMMENT '结果名称（测评类）',
  CHANGE COLUMN `resultDesc` `result_desc` text DEFAULT NULL COMMENT '结果描述（测评类）',
  CHANGE COLUMN `createTime` `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  CHANGE COLUMN `updateTime` `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  CHANGE COLUMN `isDelete` `is_delete` tinyint DEFAULT 0 COMMENT '是否删除';

-- 删除旧索引并创建新索引
ALTER TABLE `user_answer_record` DROP INDEX IF EXISTS `idx_userId`;
ALTER TABLE `user_answer_record` DROP INDEX IF EXISTS `idx_appId`;
ALTER TABLE `user_answer_record` ADD KEY `idx_user_id` (`user_id`);
ALTER TABLE `user_answer_record` ADD KEY `idx_app_id` (`app_id`);
