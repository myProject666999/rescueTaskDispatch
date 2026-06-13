-- 创建数据库
CREATE DATABASE IF NOT EXISTS rescue_dispatch DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE rescue_dispatch;

-- 技能表
DROP TABLE IF EXISTS `skill`;
CREATE TABLE `skill` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `skill_name` varchar(50) NOT NULL COMMENT '技能名称',
  `skill_code` varchar(50) NOT NULL COMMENT '技能编码',
  `skill_type` varchar(20) NOT NULL COMMENT '技能类型：绳索、水域、医疗、搜救犬、通讯、导航等',
  `description` varchar(200) DEFAULT NULL COMMENT '技能描述',
  `sort_order` int DEFAULT 0 COMMENT '排序',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '是否删除 0否 1是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_skill_code` (`skill_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='技能表';

-- 队员表
DROP TABLE IF EXISTS `rescuer`;
CREATE TABLE `rescuer` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) NOT NULL COMMENT '姓名',
  `phone` varchar(20) NOT NULL COMMENT '手机号',
  `gender` tinyint DEFAULT 1 COMMENT '性别 1男 2女',
  `age` int DEFAULT NULL COMMENT '年龄',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `id_card` varchar(18) DEFAULT NULL COMMENT '身份证号',
  `address` varchar(200) DEFAULT NULL COMMENT '住址',
  `availability_status` varchar(20) NOT NULL DEFAULT 'ON_DUTY' COMMENT '可调动状态：ON_DUTY值班、RESTING休息、AWAY不在本地',
  `level` varchar(20) DEFAULT 'MEMBER' COMMENT '级别：CAPTAIN队长、VICE_CAPTAIN副队长、MEMBER队员',
  `join_date` date DEFAULT NULL COMMENT '入队日期',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '是否删除 0否 1是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='队员表';

-- 队员技能关联表
DROP TABLE IF EXISTS `rescuer_skill`;
CREATE TABLE `rescuer_skill` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `rescuer_id` bigint NOT NULL COMMENT '队员ID',
  `skill_id` bigint NOT NULL COMMENT '技能ID',
  `proficiency` varchar(20) DEFAULT 'INTERMEDIATE' COMMENT '熟练度：BEGINNER初级、INTERMEDIATE中级、ADVANCED高级、EXPERT专家',
  `certification_no` varchar(50) DEFAULT NULL COMMENT '证书编号',
  `certified_date` date DEFAULT NULL COMMENT '认证日期',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` tinyint DEFAULT 0 COMMENT '是否删除 0否 1是',
  PRIMARY KEY (`id`),
  KEY `idx_rescuer_id` (`rescuer_id`),
  KEY `idx_skill_id` (`skill_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='队员技能关联表';

-- 任务表
DROP TABLE IF EXISTS `rescue_task`;
CREATE TABLE `rescue_task` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `task_no` varchar(50) NOT NULL COMMENT '任务编号',
  `task_title` varchar(100) NOT NULL COMMENT '任务标题',
  `task_type` varchar(30) NOT NULL COMMENT '任务类型：山地救援、水域救援、医疗救援、综合救援等',
  `danger_level` varchar(20) DEFAULT 'NORMAL' COMMENT '险情等级：LOW低、NORMAL中、HIGH高、EXTREME极高',
  `location` varchar(200) NOT NULL COMMENT '求救地点',
  `longitude` decimal(10,6) DEFAULT NULL COMMENT '经度',
  `latitude` decimal(10,6) DEFAULT NULL COMMENT '纬度',
  `victim_info` varchar(500) DEFAULT NULL COMMENT '求救人状况',
  `victim_count` int DEFAULT 1 COMMENT '被困人数',
  `reporter_name` varchar(50) DEFAULT NULL COMMENT '报警人姓名',
  `reporter_phone` varchar(20) DEFAULT NULL COMMENT '报警人电话',
  `description` text COMMENT '险情描述',
  `status` varchar(30) NOT NULL DEFAULT 'PENDING' COMMENT '任务状态：PENDING待分配、DISPATCHED已派发、IN_PROGRESS进行中、COMPLETED已完成、CANCELLED已取消',
  `dispatch_time` datetime DEFAULT NULL COMMENT '派发时间',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `creator_id` bigint DEFAULT NULL COMMENT '创建人ID',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '是否删除 0否 1是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_task_no` (`task_no`),
  KEY `idx_status` (`status`),
  KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='救援任务表';

-- 任务技能需求表
DROP TABLE IF EXISTS `task_skill_requirement`;
CREATE TABLE `task_skill_requirement` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `task_id` bigint NOT NULL COMMENT '任务ID',
  `skill_id` bigint NOT NULL COMMENT '技能ID',
  `required_count` int DEFAULT 1 COMMENT '需要人数',
  `min_proficiency` varchar(20) DEFAULT 'BEGINNER' COMMENT '最低熟练度要求',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` tinyint DEFAULT 0 COMMENT '是否删除 0否 1是',
  PRIMARY KEY (`id`),
  KEY `idx_task_id` (`task_id`),
  KEY `idx_skill_id` (`skill_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务技能需求表';

-- 任务队员表
DROP TABLE IF EXISTS `task_rescuer`;
CREATE TABLE `task_rescuer` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `task_id` bigint NOT NULL COMMENT '任务ID',
  `rescuer_id` bigint NOT NULL COMMENT '队员ID',
  `response_status` varchar(30) NOT NULL DEFAULT 'NOTIFIED' COMMENT '响应状态：NOTIFIED已通知、RESPONDED已响应(30分钟内到)、DEPARTED已出动、ARRIVED已到场、WITHDRAWN已撤离、REJECTED已拒绝',
  `response_time` datetime DEFAULT NULL COMMENT '响应时间',
  `depart_time` datetime DEFAULT NULL COMMENT '出动时间',
  `arrive_time` datetime DEFAULT NULL COMMENT '到场时间',
  `withdraw_time` datetime DEFAULT NULL COMMENT '撤离时间',
  `estimated_arrival` int DEFAULT NULL COMMENT '预计到达时间(分钟)',
  `assign_reason` varchar(200) DEFAULT NULL COMMENT '分配原因',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '是否删除 0否 1是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_task_rescuer` (`task_id`, `rescuer_id`),
  KEY `idx_rescuer_id` (`rescuer_id`),
  KEY `idx_response_status` (`response_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务队员关联表';

-- 装备表
DROP TABLE IF EXISTS `equipment`;
CREATE TABLE `equipment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `equip_name` varchar(100) NOT NULL COMMENT '装备名称',
  `equip_code` varchar(50) NOT NULL COMMENT '装备编码',
  `category` varchar(30) NOT NULL COMMENT '装备分类：绳索装备、水域装备、医疗装备、通讯装备、其他',
  `specification` varchar(200) DEFAULT NULL COMMENT '规格型号',
  `total_quantity` int DEFAULT 0 COMMENT '总数量',
  `available_quantity` int DEFAULT 0 COMMENT '可用数量',
  `unit` varchar(10) DEFAULT '套' COMMENT '单位',
  `location` varchar(100) DEFAULT NULL COMMENT '存放位置',
  `description` varchar(500) DEFAULT NULL COMMENT '装备描述',
  `image_url` varchar(255) DEFAULT NULL COMMENT '装备图片',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '是否删除 0否 1是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_equip_code` (`equip_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='装备表';

-- 任务装备领用表
DROP TABLE IF EXISTS `task_equipment`;
CREATE TABLE `task_equipment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `task_id` bigint NOT NULL COMMENT '任务ID',
  `equipment_id` bigint NOT NULL COMMENT '装备ID',
  `quantity` int NOT NULL DEFAULT 1 COMMENT '领用数量',
  `operator_id` bigint DEFAULT NULL COMMENT '领用人ID',
  `operator_name` varchar(50) DEFAULT NULL COMMENT '领用人姓名',
  `checkout_time` datetime DEFAULT NULL COMMENT '出库时间',
  `return_time` datetime DEFAULT NULL COMMENT '归还时间',
  `status` varchar(20) DEFAULT 'CHECKED_OUT' COMMENT '状态：CHECKED_OUT已出库、RETURNED已归还、DAMAGED损坏、LOST丢失',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '是否删除 0否 1是',
  PRIMARY KEY (`id`),
  KEY `idx_task_id` (`task_id`),
  KEY `idx_equipment_id` (`equipment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务装备领用表';

-- 任务复盘表
DROP TABLE IF EXISTS `task_review`;
CREATE TABLE `task_review` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `task_id` bigint NOT NULL COMMENT '任务ID',
  `summary` text COMMENT '任务总结',
  `experience` text COMMENT '经验教训',
  `problems` text COMMENT '存在问题',
  `improvements` text COMMENT '改进建议',
  `casualty_situation` varchar(500) DEFAULT NULL COMMENT '人员伤亡情况',
  `rescue_effect` varchar(20) DEFAULT NULL COMMENT '救援效果：SUCCESS成功、PARTIAL部分成功、FAILURE失败',
  `reviewer_id` bigint DEFAULT NULL COMMENT '复盘人ID',
  `reviewer_name` varchar(50) DEFAULT NULL COMMENT '复盘人姓名',
  `review_time` datetime DEFAULT NULL COMMENT '复盘时间',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '是否删除 0否 1是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_task_id` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务复盘表';

-- 任务时间线表
DROP TABLE IF EXISTS `task_timeline`;
CREATE TABLE `task_timeline` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `task_id` bigint NOT NULL COMMENT '任务ID',
  `event_type` varchar(50) NOT NULL COMMENT '事件类型',
  `event_title` varchar(100) NOT NULL COMMENT '事件标题',
  `event_detail` text COMMENT '事件详情',
  `operator_id` bigint DEFAULT NULL COMMENT '操作人ID',
  `operator_name` varchar(50) DEFAULT NULL COMMENT '操作人姓名',
  `event_time` datetime NOT NULL COMMENT '事件时间',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` tinyint DEFAULT 0 COMMENT '是否删除 0否 1是',
  PRIMARY KEY (`id`),
  KEY `idx_task_id` (`task_id`),
  KEY `idx_event_time` (`event_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务时间线表';

-- ========== 初始化数据 ==========

-- 初始化技能数据
INSERT INTO `skill` (`skill_name`, `skill_code`, `skill_type`, `description`, `sort_order`) VALUES
('绳索救援', 'ROPE_RESCUE', '绳索', '高空、山地绳索救援技能', 1),
('绳索下降', 'RAPPELLING', '绳索', '绳索下降技术', 2),
('攀岩', 'ROCK_CLIMBING', '绳索', '攀岩技术', 3),
('水域救援', 'WATER_RESCUE', '水域', '水域救援技能', 4),
('潜水救援', 'DIVING_RESCUE', '水域', '潜水救援技能', 5),
('冲锋舟操作', 'BOAT_OPERATION', '水域', '冲锋舟/橡皮艇操作', 6),
('急救医疗', 'FIRST_AID', '医疗', '基础急救技能', 7),
('心肺复苏', 'CPR', '医疗', '心肺复苏术', 8),
('创伤救护', 'TRAUMA_CARE', '医疗', '创伤包扎救护', 9),
('搜救犬引导', 'SEARCH_DOG', '搜救犬', '搜救犬引导与指挥', 10),
('卫星通讯', 'SATELLITE_COMM', '通讯', '卫星电话通讯操作', 11),
('无线电通讯', 'RADIO_COMM', '通讯', '对讲机/无线电操作', 12),
('导航定位', 'NAVIGATION', '导航', 'GPS/地图导航定位', 13),
('野外生存', 'WILDERNESS_SURVIVAL', '其他', '野外生存技能', 14);

-- 初始化队员数据
INSERT INTO `rescuer` (`name`, `phone`, `gender`, `age`, `availability_status`, `level`, `join_date`, `remark`) VALUES
('张伟', '13800138001', 1, 32, 'ON_DUTY', 'CAPTAIN', '2018-03-15', '救援队队长，经验丰富'),
('李强', '13800138002', 1, 28, 'ON_DUTY', 'VICE_CAPTAIN', '2019-05-20', '副队长，水域救援专家'),
('王芳', '13800138003', 2, 26, 'ON_DUTY', 'MEMBER', '2020-01-10', '医疗急救员'),
('赵磊', '13800138004', 1, 30, 'RESTING', 'MEMBER', '2019-08-01', '绳索救援高手'),
('陈勇', '13800138005', 1, 35, 'ON_DUTY', 'MEMBER', '2017-06-15', '老队员，全能型'),
('刘婷', '13800138006', 2, 24, 'ON_DUTY', 'MEMBER', '2021-02-28', '搜救犬训导员'),
('杨帆', '13800138007', 1, 27, 'AWAY', 'MEMBER', '2020-11-10', '通讯设备专家，目前出差'),
('黄磊', '13800138008', 1, 29, 'ON_DUTY', 'MEMBER', '2019-04-12', '冲锋舟操作员'),
('周静', '13800138009', 2, 31, 'ON_DUTY', 'MEMBER', '2018-09-05', '医护背景，创伤救护专家'),
('吴强', '13800138010', 1, 33, 'ON_DUTY', 'MEMBER', '2016-07-20', '资深队员，山地救援经验丰富');

-- 初始化队员技能数据
INSERT INTO `rescuer_skill` (`rescuer_id`, `skill_id`, `proficiency`, `certification_no`, `certified_date`) VALUES
(1, 1, 'EXPERT', 'RC-2018-001', '2018-04-10'),
(1, 2, 'EXPERT', 'RP-2018-001', '2018-04-15'),
(1, 7, 'ADVANCED', 'FA-2018-001', '2018-05-01'),
(1, 13, 'ADVANCED', 'NAV-2018-001', '2018-05-10'),
(2, 4, 'EXPERT', 'WR-2019-001', '2019-06-01'),
(2, 5, 'ADVANCED', 'DR-2019-001', '2019-07-01'),
(2, 6, 'EXPERT', 'BO-2019-001', '2019-06-15'),
(2, 7, 'INTERMEDIATE', 'FA-2019-001', '2019-08-01'),
(3, 7, 'EXPERT', 'FA-2020-001', '2020-02-01'),
(3, 8, 'EXPERT', 'CPR-2020-001', '2020-02-10'),
(3, 9, 'ADVANCED', 'TC-2020-001', '2020-03-01'),
(4, 1, 'EXPERT', 'RC-2019-002', '2019-09-01'),
(4, 2, 'EXPERT', 'RP-2019-002', '2019-09-10'),
(4, 3, 'ADVANCED', 'RC-2019-003', '2019-10-01'),
(5, 1, 'ADVANCED', 'RC-2017-001', '2017-07-01'),
(5, 4, 'INTERMEDIATE', 'WR-2017-001', '2017-08-01'),
(5, 7, 'ADVANCED', 'FA-2017-001', '2017-09-01'),
(5, 14, 'ADVANCED', 'WS-2017-001', '2017-10-01'),
(6, 10, 'EXPERT', 'SD-2021-001', '2021-04-01'),
(6, 7, 'INTERMEDIATE', 'FA-2021-001', '2021-05-01'),
(6, 13, 'INTERMEDIATE', 'NAV-2021-001', '2021-05-15'),
(7, 11, 'EXPERT', 'SC-2020-001', '2020-12-01'),
(7, 12, 'EXPERT', 'RAD-2020-001', '2020-12-10'),
(7, 13, 'ADVANCED', 'NAV-2020-002', '2020-12-20'),
(8, 4, 'ADVANCED', 'WR-2019-002', '2019-05-01'),
(8, 6, 'EXPERT', 'BO-2019-002', '2019-05-15'),
(8, 7, 'BEGINNER', 'FA-2019-002', '2019-06-01'),
(9, 7, 'EXPERT', 'FA-2018-002', '2018-10-01'),
(9, 8, 'EXPERT', 'CPR-2018-002', '2018-10-10'),
(9, 9, 'EXPERT', 'TC-2018-002', '2018-10-20'),
(10, 1, 'EXPERT', 'RC-2016-001', '2016-08-01'),
(10, 2, 'EXPERT', 'RP-2016-001', '2016-08-15'),
(10, 3, 'EXPERT', 'RC-2016-002', '2016-09-01'),
(10, 13, 'ADVANCED', 'NAV-2016-001', '2016-09-15'),
(10, 14, 'EXPERT', 'WS-2016-001', '2016-10-01');

-- 初始化装备数据
INSERT INTO `equipment` (`equip_name`, `equip_code`, `category`, `specification`, `total_quantity`, `available_quantity`, `unit`, `location`, `description`) VALUES
('动力绳套装', 'EQ-ROPE-001', '绳索装备', '10.5mm 50米', 20, 20, '套', 'A区1号柜', '攀岩动力绳，含主锁、快挂'),
('静力绳套装', 'EQ-ROPE-002', '绳索装备', '11mm 100米', 15, 15, '套', 'A区2号柜', '救援静力绳套装'),
('安全带', 'EQ-HARNESS-001', '绳索装备', '全身式', 30, 30, '件', 'A区3号柜', '全身式救援安全带'),
('下降器', 'EQ-DESCENDER-001', '绳索装备', '8字环', 25, 25, '个', 'A区4号柜', '8字环下降器'),
('头盔', 'EQ-HELMET-001', '绳索装备', '救援头盔', 40, 40, '顶', 'A区5号柜', '防护头盔'),
('救生衣', 'EQ-LIFEJACKET-001', '水域装备', '专业救援款', 30, 30, '件', 'B区1号柜', '专业水域救援救生衣'),
('冲锋舟', 'EQ-BOAT-001', '水域装备', '4.7米刚性', 3, 3, '艘', 'B区库房', '刚性冲锋舟，含马达'),
('潜水装备套装', 'EQ-DIVE-001', '水域装备', '全套水肺', 5, 5, '套', 'B区2号柜', '专业潜水装备套装'),
('急救包', 'EQ-MED-001', '医疗装备', '标准型', 20, 20, '个', 'C区1号柜', '标准急救医疗包'),
('担架', 'EQ-STRETCHER-001', '医疗装备', '折叠式', 8, 8, '副', 'C区2号柜', '折叠式救援担架'),
('AED除颤仪', 'EQ-AED-001', '医疗装备', '全自动型', 2, 2, '台', 'C区3号柜', '自动体外除颤仪'),
('卫星电话', 'EQ-SATPHONE-001', '通讯装备', '铱星9555', 5, 5, '部', 'D区1号柜', '铱星卫星电话'),
('对讲机', 'EQ-RADIO-001', '通讯装备', '数字防爆', 20, 20, '部', 'D区2号柜', '专业数字对讲机'),
('GPS定位仪', 'EQ-GPS-001', '导航装备', '手持专业级', 10, 10, '台', 'D区3号柜', '专业手持GPS'),
('头灯', 'EQ-HEADLAMP-001', '其他', '强光头灯', 30, 30, '个', 'E区1号柜', 'LED强光头灯'),
('搜救犬装备', 'EQ-DOG-001', '其他', '全套装备', 3, 3, '套', 'E区2号柜', '搜救犬专用装备套装');
