/*
 Navicat Premium Dump SQL

 Source Server         : mysql8
 Source Server Type    : MySQL
 Source Server Version : 80041 (8.0.41)
 Source Host           : localhost:3306
 Source Schema         : aquaculture_management

 Target Server Type    : MySQL
 Target Server Version : 80041 (8.0.41)
 File Encoding         : 65001

 Date: 01/12/2025 13:52:56
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for aquaculture_plan
-- ----------------------------
DROP TABLE IF EXISTS `aquaculture_plan`;
CREATE TABLE `aquaculture_plan`  (
  `plan_id` bigint NOT NULL AUTO_INCREMENT COMMENT '计划ID',
  `plan_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '计划名称',
  `area_id` bigint NOT NULL COMMENT '区域ID（外键）',
  `breed_id` bigint NOT NULL COMMENT '品种ID（外键）',
  `target_yield` decimal(10, 2) NOT NULL COMMENT '目标产量（公斤）',
  `release_amount` decimal(10, 2) NOT NULL COMMENT '投放量',
  `cycle_days` int NOT NULL COMMENT '养殖周期（天）',
  `start_date` date NOT NULL COMMENT '开始日期',
  `end_date` date NULL DEFAULT NULL COMMENT '预计结束日期',
  `feed_budget` decimal(10, 2) NULL DEFAULT NULL COMMENT '饲料预算',
  `prevention_plan` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '防疫计划',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态（0-待审批，1-已通过，2-已驳回，3-执行中，4-已完成，5-已取消）',
  `creator_id` bigint NOT NULL COMMENT '创建人ID（外键）',
  `approver_id` bigint NULL DEFAULT NULL COMMENT '审批人ID（外键）',
  `approve_opinion` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '审批意见',
  `approve_time` datetime NULL DEFAULT NULL COMMENT '审批时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`plan_id`) USING BTREE,
  INDEX `idx_area_id`(`area_id` ASC) USING BTREE,
  INDEX `idx_breed_id`(`breed_id` ASC) USING BTREE,
  INDEX `idx_creator_id`(`creator_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '养殖计划表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of aquaculture_plan
-- ----------------------------
INSERT INTO `aquaculture_plan` VALUES (12, '东海岸1号池南美白对虾春季养殖计划', 13, 15, 5000.00, 100000.00, 120, '2025-03-01', '2025-06-29', 15000.00, '定期检测水质，预防白斑病，保持水温在25-32度', 4, 1, NULL, NULL, NULL, '2025-11-17 04:50:46', '2025-11-17 20:12:54');
INSERT INTO `aquaculture_plan` VALUES (13, '东海岸2号池斑节对虾夏季养殖计划', 14, 16, 8000.00, 150000.00, 150, '2025-04-01', '2025-08-29', 25000.00, '加强增氧，预防弧菌病，控制投饵量', 3, 1, NULL, NULL, NULL, '2025-11-17 04:50:46', '2025-11-17 20:20:14');
INSERT INTO `aquaculture_plan` VALUES (14, '西区1号鱼塘草鱼年度养殖计划', 16, 17, 10000.00, 50000.00, 365, '2025-01-15', '2026-01-15', 30000.00, '定期换水，预防细菌性烂鳃病，保持水质清新', 3, 1, NULL, NULL, NULL, '2025-11-17 04:50:46', '2025-11-17 20:20:16');
INSERT INTO `aquaculture_plan` VALUES (15, '西区2号鱼塘鲤鱼春季养殖计划', 17, 18, 6000.00, 30000.00, 300, '2025-02-01', '2025-11-28', 18000.00, '预防寄生虫病，定期消毒，控制密度', 3, 1, NULL, NULL, NULL, '2025-11-17 04:50:46', '2025-11-17 20:20:20');
INSERT INTO `aquaculture_plan` VALUES (16, '东海岸3号池扇贝养殖计划', 15, 21, 3000.00, 200000.00, 180, '2025-05-01', '2025-10-28', 12000.00, '定期清理附着物，预防赤潮，保持水流畅通', 3, 1, NULL, NULL, NULL, '2025-11-17 04:50:46', '2025-11-17 20:20:29');
INSERT INTO `aquaculture_plan` VALUES (17, '西区3号鱼塘鲫鱼养殖计划', 18, 19, 4000.00, 25000.00, 240, '2025-03-15', '2025-11-10', 10000.00, '预防水霉病，保持水温稳定，合理投饵', 3, 1, NULL, NULL, NULL, '2025-11-17 04:50:46', '2025-11-17 20:20:23');
INSERT INTO `aquaculture_plan` VALUES (18, '东海岸深水区海参养殖计划', 19, 20, 2000.00, 50000.00, 730, '2025-01-01', '2027-01-01', 50000.00, '长期养殖计划，定期检查生长情况，预防病害', 3, 1, 1, '', '2025-11-17 05:04:20', '2025-11-17 04:50:46', '2025-11-17 20:20:26');

-- ----------------------------
-- Table structure for base_area
-- ----------------------------
DROP TABLE IF EXISTS `base_area`;
CREATE TABLE `base_area`  (
  `area_id` bigint NOT NULL AUTO_INCREMENT COMMENT '区域ID',
  `area_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '区域编号（唯一）',
  `area_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '区域名称',
  `area_size` decimal(10, 2) NOT NULL COMMENT '面积（平方米）',
  `department_id` bigint NOT NULL COMMENT '所属部门ID',
  `location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '位置信息',
  `water_quality` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '水质类型',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（0-闲置，1-在用）',
  `manager_id` bigint NULL DEFAULT NULL COMMENT '负责人ID（外键）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`area_id`) USING BTREE,
  UNIQUE INDEX `uk_area_code`(`area_code` ASC) USING BTREE,
  INDEX `idx_department_id`(`department_id` ASC) USING BTREE,
  INDEX `idx_manager_id`(`manager_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '养殖区域表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of base_area
-- ----------------------------
INSERT INTO `base_area` VALUES (13, 'AREA001', '东海岸1号养殖池', 5000.00, 1, '山东省青岛市黄岛区东海岸', '优质海水', 1, NULL, '2025-11-17 04:50:19', '2025-11-17 04:50:19');
INSERT INTO `base_area` VALUES (14, 'AREA002', '东海岸2号养殖池', 8000.00, 1, '山东省青岛市黄岛区东海岸', '优质海水', 1, NULL, '2025-11-17 04:50:19', '2025-11-17 04:50:19');
INSERT INTO `base_area` VALUES (15, 'AREA003', '东海岸3号养殖池', 6000.00, 1, '山东省青岛市黄岛区东海岸', '优质海水', 1, NULL, '2025-11-17 04:50:19', '2025-11-17 04:50:19');
INSERT INTO `base_area` VALUES (16, 'AREA004', '西区1号鱼塘', 3000.00, 2, '湖北省武汉市江夏区', '淡水', 1, NULL, '2025-11-17 04:50:19', '2025-11-17 04:50:19');
INSERT INTO `base_area` VALUES (17, 'AREA005', '西区2号鱼塘', 4500.00, 2, '湖北省武汉市江夏区', '淡水', 1, NULL, '2025-11-17 04:50:19', '2025-11-17 04:50:19');
INSERT INTO `base_area` VALUES (18, 'AREA006', '西区3号鱼塘', 3500.00, 2, '湖北省武汉市江夏区', '淡水', 1, NULL, '2025-11-17 04:50:19', '2025-11-17 04:50:19');
INSERT INTO `base_area` VALUES (19, 'AREA007', '东海岸深水区', 12000.00, 1, '山东省青岛市黄岛区深水区', '优质深海水', 1, NULL, '2025-11-17 04:50:19', '2025-11-17 04:50:19');
INSERT INTO `base_area` VALUES (20, 'AREA008', '西区生态养殖区', 5500.00, 2, '湖北省武汉市江夏区生态园', '优质淡水', 1, NULL, '2025-11-17 04:50:19', '2025-11-17 04:50:19');

-- ----------------------------
-- Table structure for base_breed
-- ----------------------------
DROP TABLE IF EXISTS `base_breed`;
CREATE TABLE `base_breed`  (
  `breed_id` bigint NOT NULL AUTO_INCREMENT COMMENT '品种ID',
  `breed_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '品种名称',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '品类（鱼类/甲壳类等）',
  `growth_cycle` int NULL DEFAULT NULL COMMENT '生长周期（天）',
  `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '公斤' COMMENT '单位',
  `suitable_temp_min` decimal(5, 2) NULL DEFAULT NULL COMMENT '适宜最低水温（℃）',
  `suitable_temp_max` decimal(5, 2) NULL DEFAULT NULL COMMENT '适宜最高水温（℃）',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '其他属性描述',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图片URL',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（0-禁用，1-启用）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`breed_id`) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '养殖品种表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of base_breed
-- ----------------------------
INSERT INTO `base_breed` VALUES (15, '南美白对虾', '虾类', 120, '公斤', 25.00, 32.00, '生长速度快，适应性强，是海水养殖的主要品种之一', '/uploads/breed/2025/12/d69ff9e5-0854-4c9f-8aa7-772b64939516.jpg', 1, '2025-11-17 04:50:11', '2025-12-01 13:43:21');
INSERT INTO `base_breed` VALUES (16, '斑节对虾', '虾类', 150, '公斤', 26.00, 30.00, '肉质鲜美，市场需求量大，适合高密度养殖', NULL, 1, '2025-11-17 04:50:11', '2025-11-17 04:50:11');
INSERT INTO `base_breed` VALUES (17, '草鱼', '鱼类', 365, '公斤', 20.00, 28.00, '淡水养殖的主要品种，生长周期长，肉质鲜嫩', NULL, 1, '2025-11-17 04:50:11', '2025-11-17 04:50:11');
INSERT INTO `base_breed` VALUES (18, '鲤鱼', '鱼类', 300, '公斤', 18.00, 26.00, '适应性强，抗病能力好，适合大规模养殖', NULL, 1, '2025-11-17 04:50:11', '2025-11-17 04:50:11');
INSERT INTO `base_breed` VALUES (19, '鲫鱼', '鱼类', 240, '公斤', 20.00, 28.00, '生长周期短，繁殖能力强，是淡水养殖的常见品种', NULL, 1, '2025-11-17 04:50:11', '2025-11-17 04:50:11');
INSERT INTO `base_breed` VALUES (20, '海参', '海参类', 730, '公斤', 15.00, 22.00, '营养价值高，市场价格昂贵，养殖周期较长', NULL, 1, '2025-11-17 04:50:11', '2025-11-17 04:50:11');
INSERT INTO `base_breed` VALUES (21, '扇贝', '贝类', 180, '公斤', 10.00, 20.00, '适合浅海养殖，生长速度快，经济效益好', NULL, 1, '2025-11-17 04:50:11', '2025-11-17 04:50:11');
INSERT INTO `base_breed` VALUES (22, '鲍鱼', '贝类', 540, '公斤', 18.00, 24.00, '高端水产品，养殖技术要求高，市场价值高', NULL, 1, '2025-11-17 04:50:11', '2025-11-17 04:50:11');

-- ----------------------------
-- Table structure for base_department
-- ----------------------------
DROP TABLE IF EXISTS `base_department`;
CREATE TABLE `base_department`  (
  `department_id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门ID',
  `department_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '部门编号（唯一）',
  `department_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '部门名称',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '部门描述',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（0-禁用，1-启用）',
  `manager_id` bigint NULL DEFAULT NULL COMMENT '部门负责人ID（外键）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`department_id`) USING BTREE,
  UNIQUE INDEX `uk_department_code`(`department_code` ASC) USING BTREE,
  INDEX `idx_manager_id`(`manager_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '部门信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of base_department
-- ----------------------------
INSERT INTO `base_department` VALUES (1, 'DEPT001', '东部养殖基地', '负责东部沿海地区的海水养殖业务，包括对虾、海参、扇贝等品种的养殖管理', 1, NULL, '2025-11-17 04:49:21', '2025-11-17 04:49:21');
INSERT INTO `base_department` VALUES (2, 'DEPT002', '西部淡水养殖基地', '负责西部内陆地区的淡水养殖业务，包括草鱼、鲤鱼、鲫鱼等品种的养殖管理', 1, NULL, '2025-11-17 04:49:21', '2025-11-17 04:49:21');

-- ----------------------------
-- Table structure for base_equipment
-- ----------------------------
DROP TABLE IF EXISTS `base_equipment`;
CREATE TABLE `base_equipment`  (
  `equipment_id` bigint NOT NULL AUTO_INCREMENT COMMENT '设备ID',
  `equipment_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '设备名称',
  `equipment_model` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '设备型号',
  `equipment_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '设备类型（增氧机/投饵机等）',
  `area_id` bigint NOT NULL COMMENT '区域ID（外键）',
  `install_location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '安装位置',
  `quantity` int NOT NULL DEFAULT 1 COMMENT '数量',
  `last_maintain_time` datetime NULL DEFAULT NULL COMMENT '最后维护时间',
  `maintain_record` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '维护记录',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图片URL',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（0-故障，1-正常，2-维修中）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`equipment_id`) USING BTREE,
  INDEX `idx_area_id`(`area_id` ASC) USING BTREE,
  INDEX `idx_equipment_type`(`equipment_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '设备信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of base_equipment
-- ----------------------------
INSERT INTO `base_equipment` VALUES (17, '增氧机', 'ZYJ-5000', '增氧设备', 13, '东海岸1号养殖池中央', 3, '2025-10-18 04:50:35', '定期检查增氧效果，更换滤网', '/uploads/equipment/2025/12/bffc921c-0aa5-4d96-ab15-f8399490ec41.jpg', 1, '2025-11-17 04:50:35', '2025-12-01 13:45:01');
INSERT INTO `base_equipment` VALUES (18, '水质监测仪', 'SZJC-2000', '监测设备', 13, '东海岸1号养殖池东侧', 1, '2025-11-02 04:50:35', '校准传感器，清洁探头', NULL, 1, '2025-11-17 04:50:35', '2025-11-17 04:50:35');
INSERT INTO `base_equipment` VALUES (19, '投饵机', 'TER-3000', '投喂设备', 13, '东海岸1号养殖池西侧', 2, '2025-10-28 04:50:35', '检查投饵精度，清理料仓', NULL, 1, '2025-11-17 04:50:35', '2025-11-17 04:50:35');
INSERT INTO `base_equipment` VALUES (20, '增氧机', 'ZYJ-5000', '增氧设备', 14, '东海岸2号养殖池中央', 4, '2025-10-23 04:50:35', '检查电机运行状态', NULL, 1, '2025-11-17 04:50:35', '2025-11-17 04:50:35');
INSERT INTO `base_equipment` VALUES (21, '水质监测仪', 'SZJC-2000', '监测设备', 14, '东海岸2号养殖池南侧', 1, '2025-11-07 04:50:35', '更换电池，更新固件', NULL, 1, '2025-11-17 04:50:35', '2025-11-17 04:50:35');
INSERT INTO `base_equipment` VALUES (22, '投饵机', 'TER-3000', '投喂设备', 14, '东海岸2号养殖池北侧', 2, '2025-10-30 04:50:35', '调整投饵时间', NULL, 1, '2025-11-17 04:50:35', '2025-11-17 04:50:35');
INSERT INTO `base_equipment` VALUES (23, '增氧机', 'ZYJ-3000', '增氧设备', 16, '西区1号鱼塘中央', 2, '2025-10-13 04:50:35', '检查增氧效果', NULL, 1, '2025-11-17 04:50:35', '2025-11-17 04:50:35');
INSERT INTO `base_equipment` VALUES (24, '水质监测仪', 'SZJC-1000', '监测设备', 16, '西区1号鱼塘东侧', 1, '2025-11-05 04:50:35', '校准pH值传感器', NULL, 1, '2025-11-17 04:50:35', '2025-11-17 04:50:35');
INSERT INTO `base_equipment` VALUES (25, '投饵机', 'TER-2000', '投喂设备', 16, '西区1号鱼塘西侧', 1, '2025-10-26 04:50:35', '清理投饵管道', NULL, 1, '2025-11-17 04:50:35', '2025-11-17 04:50:35');
INSERT INTO `base_equipment` VALUES (26, '增氧机', 'ZYJ-3000', '增氧设备', 17, '西区2号鱼塘中央', 3, '2025-10-20 04:50:35', '更换增氧叶轮', NULL, 1, '2025-11-17 04:50:35', '2025-11-17 04:50:35');
INSERT INTO `base_equipment` VALUES (27, '水质监测仪', 'SZJC-1000', '监测设备', 17, '西区2号鱼塘南侧', 1, '2025-11-09 04:50:35', '检查溶解氧传感器', NULL, 1, '2025-11-17 04:50:35', '2025-11-17 04:50:35');
INSERT INTO `base_equipment` VALUES (28, '投饵机', 'TER-2000', '投喂设备', 17, '西区2号鱼塘北侧', 2, '2025-11-01 04:50:35', '调整投饵量', NULL, 1, '2025-11-17 04:50:35', '2025-11-17 04:50:35');

-- ----------------------------
-- Table structure for customer
-- ----------------------------
DROP TABLE IF EXISTS `customer`;
CREATE TABLE `customer`  (
  `customer_id` bigint NOT NULL AUTO_INCREMENT COMMENT '客户ID',
  `customer_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '客户名称',
  `contact_person` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `contact_address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系地址',
  `customer_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '客户类型（批发商/零售商/加工企业）',
  `credit_rating` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '信用等级（优/良/中/差）',
  `status` int NOT NULL DEFAULT 1 COMMENT '状态（1-正常，0-已删除）',
  `creator_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`customer_id`) USING BTREE,
  INDEX `idx_customer_name`(`customer_name` ASC) USING BTREE,
  INDEX `idx_customer_type`(`customer_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '客户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of customer
-- ----------------------------
INSERT INTO `customer` VALUES (1, '青岛海鲜批发市场', '王经理', '13800138001', '山东省青岛市市南区海鲜市场', '批发商', '优', 1, 1, '2025-11-17 05:56:28', '2025-11-17 05:56:28');
INSERT INTO `customer` VALUES (2, '烟台水产品加工厂', '李主任', '13900139001', '山东省烟台市芝罘区工业园区', '加工企业', '良', 1, 1, '2025-11-17 05:56:28', '2025-11-17 05:56:28');
INSERT INTO `customer` VALUES (3, '济南水产零售商', '张老板', '13700137001', '山东省济南市历下区水产街', '零售商', '中', 1, 1, '2025-11-17 05:56:28', '2025-11-17 05:56:28');
INSERT INTO `customer` VALUES (4, '威海海产品公司', '赵总', '13600136001', '山东省威海市环翠区海滨路', '批发商', '优', 1, 1, '2025-11-17 05:56:28', '2025-11-17 05:56:28');

-- ----------------------------
-- Table structure for daily_inspection
-- ----------------------------
DROP TABLE IF EXISTS `daily_inspection`;
CREATE TABLE `daily_inspection`  (
  `inspection_id` bigint NOT NULL AUTO_INCREMENT COMMENT '检查记录ID',
  `plan_id` bigint NOT NULL COMMENT '计划ID（外键关联养殖计划表）',
  `area_id` bigint NOT NULL COMMENT '区域ID（外键关联养殖区域表）',
  `inspection_date` date NOT NULL COMMENT '检查日期',
  `inspection_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '检查时间',
  `inspection_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '检查类型（日常检查/设备检查/环境检查）',
  `inspection_item` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '检查项目',
  `inspection_result` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '检查结果（正常/异常/待处理）',
  `problem_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '问题描述',
  `handling_method` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '处理方法',
  `handler` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '处理人',
  `status` int NOT NULL DEFAULT 1 COMMENT '状态（1-正常，0-已删除）',
  `creator_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`inspection_id`) USING BTREE,
  INDEX `idx_plan_id`(`plan_id` ASC) USING BTREE,
  INDEX `idx_area_id`(`area_id` ASC) USING BTREE,
  INDEX `idx_inspection_date`(`inspection_date` ASC) USING BTREE,
  INDEX `idx_inspection_type`(`inspection_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '日常检查记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of daily_inspection
-- ----------------------------
INSERT INTO `daily_inspection` VALUES (1, 12, 13, '2025-04-01', '上午', '日常检查', '水体观察、增氧机运行、投饵机运行', '正常', NULL, NULL, '张技术员', 1, 1, '2025-11-17 05:55:03', '2025-11-17 05:55:03');
INSERT INTO `daily_inspection` VALUES (2, 12, 13, '2025-04-05', '上午', '设备检查', '增氧机检查', '异常', '增氧机1号运行异常，噪音较大', '立即停机检修，更换配件', '李维修员', 1, 1, '2025-11-17 05:55:03', '2025-11-17 05:55:03');
INSERT INTO `daily_inspection` VALUES (3, 13, 14, '2025-05-01', '上午', '环境检查', '水质检测、水温检测、溶氧检测', '正常', NULL, NULL, '王技术员', 1, 1, '2025-11-17 05:55:03', '2025-11-17 05:55:03');
INSERT INTO `daily_inspection` VALUES (4, 14, 16, '2025-03-01', '上午', '日常检查', '鱼类活动情况、摄食情况、水体状况', '正常', NULL, NULL, '赵技术员', 1, 1, '2025-11-17 05:55:03', '2025-11-17 05:55:03');
INSERT INTO `daily_inspection` VALUES (5, 14, 16, '2025-03-10', '上午', '环境检查', '水质检测', '异常', 'pH值偏高，需调整', '添加调节剂，降低pH值', '赵技术员', 1, 1, '2025-11-17 05:55:03', '2025-11-17 05:55:03');

-- ----------------------------
-- Table structure for disease_prevention
-- ----------------------------
DROP TABLE IF EXISTS `disease_prevention`;
CREATE TABLE `disease_prevention`  (
  `prevention_id` bigint NOT NULL AUTO_INCREMENT COMMENT '防治记录ID',
  `record_id` bigint NOT NULL COMMENT '病害记录ID（外键关联病害记录表）',
  `plan_id` bigint NOT NULL COMMENT '计划ID（外键关联养殖计划表）',
  `area_id` bigint NOT NULL COMMENT '区域ID（外键关联养殖区域表）',
  `prevention_method` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '防治方法',
  `prevention_date` date NOT NULL COMMENT '防治日期',
  `prevention_result` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '防治结果（有效/无效/待观察）',
  `effect_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '效果描述',
  `operator` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作人',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片URL',
  `status` int NOT NULL DEFAULT 1 COMMENT '状态（1-正常，0-已删除）',
  `creator_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`prevention_id`) USING BTREE,
  INDEX `idx_record_id`(`record_id` ASC) USING BTREE,
  INDEX `idx_plan_id`(`plan_id` ASC) USING BTREE,
  INDEX `idx_area_id`(`area_id` ASC) USING BTREE,
  INDEX `idx_prevention_date`(`prevention_date` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '病害防治记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of disease_prevention
-- ----------------------------
INSERT INTO `disease_prevention` VALUES (1, 1, 12, 13, '使用含氯消毒剂全池泼洒，并配合使用抗病毒药物', '2025-04-02', '有效', '处理后3天，症状明显改善，死亡率下降', '张技术员', '连续用药3天，每日一次', NULL, 1, 1, '2025-11-17 05:51:17', '2025-11-17 05:51:17');
INSERT INTO `disease_prevention` VALUES (2, 2, 13, 14, '立即更换部分水体，使用抗生素拌料投喂', '2025-05-11', '待观察', '已进行紧急处理，观察后续效果', '李技术员', '加强水质监测', NULL, 1, 1, '2025-11-17 05:51:17', '2025-11-17 05:51:17');
INSERT INTO `disease_prevention` VALUES (3, 1, 12, 13, '加强水质管理，定期使用益生菌调节水质', '2025-04-05', '有效', '水质改善后，对虾健康状况明显好转', '张技术员', '作为后续预防措施', NULL, 1, 1, '2025-11-17 05:51:17', '2025-11-17 05:51:17');
INSERT INTO `disease_prevention` VALUES (4, 3, 14, 16, '使用生石灰全池泼洒，配合使用中药制剂', '2025-03-16', '有效', '处理后烂鳃症状逐渐缓解', '王技术员', '每周一次，连续2周', NULL, 1, 1, '2025-11-17 05:51:17', '2025-11-17 05:51:17');

-- ----------------------------
-- Table structure for disease_record
-- ----------------------------
DROP TABLE IF EXISTS `disease_record`;
CREATE TABLE `disease_record`  (
  `record_id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `plan_id` bigint NOT NULL COMMENT '计划ID（外键关联养殖计划表）',
  `area_id` bigint NOT NULL COMMENT '区域ID（外键关联养殖区域表）',
  `breed_id` bigint NOT NULL COMMENT '品种ID（外键关联养殖品种表）',
  `disease_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '病害名称',
  `disease_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '病害类型',
  `occurrence_date` date NOT NULL COMMENT '发生日期',
  `affected_area` decimal(10, 2) NULL DEFAULT NULL COMMENT '影响面积（平方米）',
  `affected_quantity` decimal(10, 2) NULL DEFAULT NULL COMMENT '受影响数量（公斤/尾数）',
  `severity_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '严重程度（轻微/中等/严重）',
  `symptoms` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '症状描述',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片URL',
  `status` int NOT NULL DEFAULT 1 COMMENT '状态（1-正常，0-已删除）',
  `creator_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`record_id`) USING BTREE,
  INDEX `idx_plan_id`(`plan_id` ASC) USING BTREE,
  INDEX `idx_area_id`(`area_id` ASC) USING BTREE,
  INDEX `idx_occurrence_date`(`occurrence_date` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '病害记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of disease_record
-- ----------------------------
INSERT INTO `disease_record` VALUES (1, 12, 13, 15, '白斑病', '病毒性疾病', '2025-04-01', 1000.00, 500.00, '中等', '发现部分对虾出现白色斑点，活动力下降，摄食减少', '/uploads/disease/2025/12/0dc3236a-f0e3-48c1-87f2-f0d996f4ae9e.jpg', 1, 1, '2025-11-17 05:51:11', '2025-12-01 13:45:30');
INSERT INTO `disease_record` VALUES (2, 13, 14, 16, '弧菌病', '细菌性疾病', '2025-05-10', 1500.00, 800.00, '严重', '对虾出现红体症状，部分死亡，需立即处理', NULL, 1, 1, '2025-11-17 05:51:11', '2025-11-17 05:51:11');
INSERT INTO `disease_record` VALUES (3, 14, 16, 17, '细菌性烂鳃病', '细菌性疾病', '2025-03-15', 2000.00, 1000.00, '轻微', '草鱼出现烂鳃症状，需加强水质管理', NULL, 1, 1, '2025-11-17 05:51:11', '2025-11-17 05:51:11');
INSERT INTO `disease_record` VALUES (4, 12, 13, 15, '肠炎病', '细菌性疾病', '2025-04-20', 800.00, 300.00, '轻微', '对虾出现肠炎症状，摄食减少', NULL, 1, 1, '2025-11-17 05:51:11', '2025-11-17 05:51:11');

-- ----------------------------
-- Table structure for feed_inventory
-- ----------------------------
DROP TABLE IF EXISTS `feed_inventory`;
CREATE TABLE `feed_inventory`  (
  `inventory_id` bigint NOT NULL AUTO_INCREMENT COMMENT '库存ID',
  `feed_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '饲料名称',
  `feed_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '饲料类型',
  `current_stock` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '当前库存（公斤）',
  `unit_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '单价（元/公斤）',
  `batch_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '批次号',
  `expiry_date` date NULL DEFAULT NULL COMMENT '保质期至',
  `warehouse_location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '仓储位置',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片URL',
  `status` int NOT NULL DEFAULT 1 COMMENT '状态（1-正常，0-已删除）',
  `creator_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`inventory_id`) USING BTREE,
  INDEX `idx_feed_type`(`feed_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_expiry_date`(`expiry_date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '饲料库存表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of feed_inventory
-- ----------------------------
INSERT INTO `feed_inventory` VALUES (10, '南美白对虾专用配合饲料', '对虾专用饲料', 5278.00, 8.50, NULL, NULL, NULL, '/uploads/feed/2025/12/ff782952-21f8-4dfb-b53c-173786fe03f8.jpg', 1, 1, '2025-11-16 21:42:41', '2025-12-01 13:51:29');
INSERT INTO `feed_inventory` VALUES (11, '斑节对虾高蛋白饲料', '对虾专用饲料', 8000.00, 9.20, NULL, NULL, NULL, NULL, 1, 1, '2025-11-16 21:42:41', '2025-11-16 21:42:41');
INSERT INTO `feed_inventory` VALUES (12, '草鱼颗粒饲料', '淡水鱼饲料', 10000.00, 6.80, NULL, NULL, NULL, NULL, 1, 1, '2025-11-16 21:42:41', '2025-11-16 21:42:41');
INSERT INTO `feed_inventory` VALUES (13, '通用配合饲料', '通用饲料', 3000.00, 7.50, NULL, NULL, NULL, NULL, 1, 1, '2025-11-16 21:42:41', '2025-11-16 21:42:41');

-- ----------------------------
-- Table structure for feed_purchase
-- ----------------------------
DROP TABLE IF EXISTS `feed_purchase`;
CREATE TABLE `feed_purchase`  (
  `purchase_id` bigint NOT NULL AUTO_INCREMENT COMMENT '采购ID',
  `feed_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '饲料名称',
  `feed_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '饲料类型',
  `supplier` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '供应商',
  `purchase_amount` decimal(10, 2) NOT NULL COMMENT '采购数量（公斤）',
  `unit_price` decimal(10, 2) NOT NULL COMMENT '单价（元/公斤）',
  `total_price` decimal(12, 2) NOT NULL COMMENT '总价（元）',
  `purchase_date` date NOT NULL COMMENT '采购日期',
  `batch_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '批次号',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片URL',
  `expiry_date` date NULL DEFAULT NULL COMMENT '保质期至',
  `status` int NOT NULL DEFAULT 1 COMMENT '状态（1-正常，0-已删除）',
  `creator_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`purchase_id`) USING BTREE,
  INDEX `idx_purchase_date`(`purchase_date` ASC) USING BTREE,
  INDEX `idx_feed_type`(`feed_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '饲料采购表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of feed_purchase
-- ----------------------------
INSERT INTO `feed_purchase` VALUES (1, '南美白对虾专用配合饲料', '对虾专用饲料', '青岛水产饲料有限公司', 5000.00, 8.50, 42500.00, '2025-01-15', 'BATCH2025011501', NULL, '2026-07-15', 1, 1, '2025-11-17 05:42:41', '2025-11-17 05:42:41');
INSERT INTO `feed_purchase` VALUES (2, '斑节对虾高蛋白饲料', '对虾专用饲料', '青岛水产饲料有限公司', 8000.00, 9.20, 73600.00, '2025-02-10', 'BATCH2025021001', NULL, '2026-08-10', 1, 1, '2025-11-17 05:42:41', '2025-11-17 05:42:41');
INSERT INTO `feed_purchase` VALUES (3, '草鱼颗粒饲料', '淡水鱼饲料', '山东水产饲料厂', 10000.00, 6.80, 68000.00, '2025-01-20', 'BATCH2025012001', NULL, '2026-07-20', 1, 1, '2025-11-17 05:42:41', '2025-11-17 05:42:41');
INSERT INTO `feed_purchase` VALUES (4, '通用配合饲料', '通用饲料', '青岛水产饲料有限公司', 3000.00, 7.50, 22500.00, '2025-03-01', 'BATCH2025030101', NULL, '2026-09-01', 1, 1, '2025-11-17 05:42:41', '2025-11-17 05:42:41');
INSERT INTO `feed_purchase` VALUES (5, '测试', '对虾专用饲料', '测试', 222.00, 22.00, 4884.00, '2025-11-17', '222', NULL, '2025-11-19', 0, 1, '2025-11-17 18:52:02', '2025-11-17 18:52:01');
INSERT INTO `feed_purchase` VALUES (6, '南美白对虾专用配合饲料', '对虾专用饲料', '青岛水产饲料有限公司', 300.00, 8.50, 2550.00, '2025-11-17', 'BATCH2025011502', NULL, '2026-07-15', 0, 1, '2025-11-17 19:06:10', '2025-11-17 19:06:10');
INSERT INTO `feed_purchase` VALUES (7, '南美白对虾专用配合饲料', '对虾专用饲料', '青岛水产饲料有限公司', 500.00, 8.50, 4250.00, '2025-11-18', 'BATCH2025011523', NULL, '2026-07-15', 0, 1, '2025-11-17 19:08:02', '2025-11-17 19:11:08');
INSERT INTO `feed_purchase` VALUES (8, '南美白对虾专用配合饲料', '对虾专用饲料', '青岛水产饲料有限公司', 300.00, 8.50, 2550.00, '2025-11-17', 'BATCH2025011502', '/uploads/feed/2025/12/ff782952-21f8-4dfb-b53c-173786fe03f8.jpg', '2026-07-15', 1, 1, '2025-11-17 19:22:37', '2025-12-01 13:51:28');

-- ----------------------------
-- Table structure for feed_usage
-- ----------------------------
DROP TABLE IF EXISTS `feed_usage`;
CREATE TABLE `feed_usage`  (
  `usage_id` bigint NOT NULL AUTO_INCREMENT COMMENT '使用记录ID',
  `plan_id` bigint NOT NULL COMMENT '计划ID（外键关联养殖计划表）',
  `area_id` bigint NOT NULL COMMENT '区域ID（外键关联养殖区域表）',
  `feed_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '饲料名称',
  `feed_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '饲料类型',
  `usage_amount` decimal(10, 2) NOT NULL COMMENT '使用数量（公斤）',
  `unit_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '单价（元/公斤）',
  `total_cost` decimal(12, 2) NULL DEFAULT NULL COMMENT '总成本（元）',
  `usage_date` date NOT NULL COMMENT '使用日期',
  `batch_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '批次号',
  `feeding_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '投喂时间（早/中/晚）',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `status` int NOT NULL DEFAULT 1 COMMENT '状态（1-正常，0-已删除）',
  `creator_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`usage_id`) USING BTREE,
  INDEX `idx_plan_id`(`plan_id` ASC) USING BTREE,
  INDEX `idx_area_id`(`area_id` ASC) USING BTREE,
  INDEX `idx_usage_date`(`usage_date` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '饲料使用记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of feed_usage
-- ----------------------------
INSERT INTO `feed_usage` VALUES (9, 19, 13, '南美白对虾专用配合饲料', '对虾专用饲料', 300.00, 8.50, 2550.00, '2025-11-17', 'BATCH2025011501', '早', '', 0, 1, '2025-11-17 19:22:54', '2025-11-17 20:19:33');
INSERT INTO `feed_usage` VALUES (10, 12, 13, '南美白对虾专用配合饲料', '对虾专用饲料', 22.00, 8.50, 187.00, '2025-11-17', 'BATCH2025011502', '早', '', 1, 1, '2025-11-17 20:19:28', '2025-11-17 20:19:28');

-- ----------------------------
-- Table structure for feeding_record
-- ----------------------------
DROP TABLE IF EXISTS `feeding_record`;
CREATE TABLE `feeding_record`  (
  `record_id` bigint NOT NULL AUTO_INCREMENT COMMENT '投喂记录ID',
  `plan_id` bigint NOT NULL COMMENT '计划ID（外键关联养殖计划表）',
  `area_id` bigint NOT NULL COMMENT '区域ID（外键关联养殖区域表）',
  `feeding_date` date NOT NULL COMMENT '投喂日期',
  `feeding_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '投喂时间（早/中/晚）',
  `feed_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '饲料名称',
  `feed_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '饲料类型',
  `feeding_amount` decimal(10, 2) NOT NULL COMMENT '投喂量（公斤）',
  `feeding_method` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '投喂方式（人工/机械）',
  `water_temperature` decimal(5, 2) NULL DEFAULT NULL COMMENT '水温（℃）',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `status` int NOT NULL DEFAULT 1 COMMENT '状态（1-正常，0-已删除）',
  `creator_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`record_id`) USING BTREE,
  INDEX `idx_plan_id`(`plan_id` ASC) USING BTREE,
  INDEX `idx_area_id`(`area_id` ASC) USING BTREE,
  INDEX `idx_feeding_date`(`feeding_date` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '投喂记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of feeding_record
-- ----------------------------
INSERT INTO `feeding_record` VALUES (1, 12, 13, '2025-04-01', '早晚', '南美白对虾专用配合饲料', '对虾专用饲料', 150.00, '人工投喂', 28.50, '初期投喂，少量多次', 1, 1, '2025-11-17 05:54:58', '2025-11-17 05:54:58');
INSERT INTO `feeding_record` VALUES (2, 12, 13, '2025-04-15', '早晚', '南美白对虾专用配合饲料', '对虾专用饲料', 200.00, '人工投喂', 29.00, '生长期增加投喂量', 1, 1, '2025-11-17 05:54:58', '2025-11-17 05:54:58');
INSERT INTO `feeding_record` VALUES (3, 13, 14, '2025-05-01', '早中晚', '斑节对虾高蛋白饲料', '对虾专用饲料', 180.00, '机械投喂', 30.00, '高密度养殖，多次投喂', 1, 1, '2025-11-17 05:54:58', '2025-11-17 05:54:58');
INSERT INTO `feeding_record` VALUES (4, 14, 16, '2025-03-01', '早晚', '草鱼颗粒饲料', '淡水鱼饲料', 300.00, '人工投喂', 22.00, '草鱼日常投喂', 1, 1, '2025-11-17 05:54:58', '2025-11-17 05:54:58');
INSERT INTO `feeding_record` VALUES (5, 14, 16, '2025-03-15', '早晚', '草鱼颗粒饲料', '淡水鱼饲料', 350.00, '人工投喂', 23.50, '春季投喂量增加', 1, 1, '2025-11-17 05:54:58', '2025-11-17 05:54:58');

-- ----------------------------
-- Table structure for medicine_inventory
-- ----------------------------
DROP TABLE IF EXISTS `medicine_inventory`;
CREATE TABLE `medicine_inventory`  (
  `inventory_id` bigint NOT NULL AUTO_INCREMENT COMMENT '库存ID',
  `medicine_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '药品名称',
  `medicine_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '药品类型',
  `current_stock` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '当前库存',
  `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '克' COMMENT '单位',
  `unit_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '单价(元/单位)',
  `status` int NOT NULL DEFAULT 1 COMMENT '状态：0-已删除，1-正常',
  `creator_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`inventory_id`) USING BTREE,
  UNIQUE INDEX `uk_medicine_name_type`(`medicine_name` ASC, `medicine_type` ASC, `status` ASC) USING BTREE,
  INDEX `idx_medicine_name`(`medicine_name` ASC) USING BTREE,
  INDEX `idx_medicine_type`(`medicine_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '药品库存表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of medicine_inventory
-- ----------------------------
INSERT INTO `medicine_inventory` VALUES (1, '含氯消毒剂', '消毒剂', 2500.00, '毫升', 15.50, 1, 1, '2025-11-17 19:48:36', '2025-11-17 20:08:39');
INSERT INTO `medicine_inventory` VALUES (2, '恩诺沙星', '抗生素', 1500.00, '克', 25.00, 1, 1, '2025-11-17 19:48:36', '2025-11-17 19:49:19');
INSERT INTO `medicine_inventory` VALUES (3, '抗病毒药物', '抗病毒药', 400.00, '克', 35.00, 1, 1, '2025-11-17 19:48:36', '2025-11-17 19:49:19');
INSERT INTO `medicine_inventory` VALUES (4, '板蓝根提取物', '中药', 350.00, '克', 20.00, 1, 1, '2025-11-17 19:48:36', '2025-11-17 19:49:19');
INSERT INTO `medicine_inventory` VALUES (5, '生石灰', '消毒剂', 200.00, '公斤', 5.00, 1, 1, '2025-11-17 19:48:36', '2025-11-17 19:49:19');
INSERT INTO `medicine_inventory` VALUES (6, '益生菌制剂', '益生菌', 2000.00, '克', 18.00, 1, 1, '2025-11-17 19:48:36', '2025-11-17 19:49:19');

-- ----------------------------
-- Table structure for medicine_purchase
-- ----------------------------
DROP TABLE IF EXISTS `medicine_purchase`;
CREATE TABLE `medicine_purchase`  (
  `purchase_id` bigint NOT NULL AUTO_INCREMENT COMMENT '采购ID',
  `medicine_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '药品名称',
  `medicine_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '药品类型',
  `supplier` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '供应商',
  `purchase_amount` decimal(10, 2) NOT NULL COMMENT '采购数量',
  `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '克' COMMENT '单位',
  `unit_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '单价(元/单位)',
  `total_price` decimal(12, 2) NULL DEFAULT NULL COMMENT '总价(元)',
  `purchase_date` date NOT NULL COMMENT '采购日期',
  `batch_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '批次号',
  `expiry_date` date NULL DEFAULT NULL COMMENT '保质期至',
  `status` int NOT NULL DEFAULT 1 COMMENT '状态：0-已删除，1-正常',
  `creator_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`purchase_id`) USING BTREE,
  INDEX `idx_medicine_name`(`medicine_name` ASC) USING BTREE,
  INDEX `idx_medicine_type`(`medicine_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_purchase_date`(`purchase_date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '药品采购表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of medicine_purchase
-- ----------------------------
INSERT INTO `medicine_purchase` VALUES (1, '含氯消毒剂', '消毒剂', '化学药品供应商A', 1000.00, '毫升', 15.50, 15500.00, '2025-02-15', 'CL20250215001', '2026-02-15', 1, 1, '2025-11-17 19:48:19', '2025-11-17 20:08:38');
INSERT INTO `medicine_purchase` VALUES (2, '恩诺沙星', '抗生素', '兽药供应商B', 1000.00, '克', 25.00, 25000.00, '2025-04-01', 'EN20250401001', '2026-04-01', 1, 1, '2025-11-17 19:48:19', '2025-11-17 19:48:19');
INSERT INTO `medicine_purchase` VALUES (3, '抗病毒药物', '抗病毒药', '生物制药公司C', 600.00, '克', 35.00, 21000.00, '2025-03-01', 'KB20250301001', '2026-03-01', 1, 1, '2025-11-17 19:48:19', '2025-11-17 19:48:19');
INSERT INTO `medicine_purchase` VALUES (4, '板蓝根提取物', '中药', '中药供应商D', 500.00, '克', 20.00, 10000.00, '2025-02-20', 'BLG20250220001', '2026-02-20', 1, 1, '2025-11-17 19:48:19', '2025-11-17 19:48:19');
INSERT INTO `medicine_purchase` VALUES (5, '生石灰', '消毒剂', '化工原料供应商E', 300.00, '公斤', 5.00, 1500.00, '2025-02-25', 'SH20250225001', '2027-02-25', 1, 1, '2025-11-17 19:48:19', '2025-11-17 19:48:19');
INSERT INTO `medicine_purchase` VALUES (6, '益生菌制剂', '益生菌', '生物科技公司F', 1500.00, '克', 18.00, 27000.00, '2025-03-10', 'YSJ20250310001', '2026-03-10', 1, 1, '2025-11-17 19:48:19', '2025-11-17 19:48:19');
INSERT INTO `medicine_purchase` VALUES (7, '含氯消毒剂', '消毒剂', '化学药品供应商A', 2000.00, '毫升', 14.50, 29000.00, '2025-05-01', 'CL20250501001', '2026-05-01', 1, 1, '2025-11-17 19:49:06', '2025-11-17 19:49:06');
INSERT INTO `medicine_purchase` VALUES (8, '恩诺沙星', '抗生素', '兽药供应商B', 800.00, '克', 24.00, 19200.00, '2025-05-15', 'EN20250515001', '2026-05-15', 1, 1, '2025-11-17 19:49:06', '2025-11-17 19:49:06');
INSERT INTO `medicine_purchase` VALUES (9, '益生菌制剂', '益生菌', '生物科技公司F', 1000.00, '克', 17.50, 17500.00, '2025-05-20', 'YSJ20250520001', '2026-05-20', 1, 1, '2025-11-17 19:49:06', '2025-11-17 19:49:06');
INSERT INTO `medicine_purchase` VALUES (10, '含氯消毒剂', '消毒剂', '化学药品供应商A', 300.00, '毫升', 14.50, 4350.00, '2025-11-17', 'CL20250501002', '2026-05-01', 0, 1, '2025-11-17 20:07:35', '2025-11-17 20:07:35');

-- ----------------------------
-- Table structure for medicine_usage
-- ----------------------------
DROP TABLE IF EXISTS `medicine_usage`;
CREATE TABLE `medicine_usage`  (
  `usage_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用药记录ID',
  `record_id` bigint NULL DEFAULT NULL COMMENT '病害记录ID（外键关联病害记录表）',
  `prevention_id` bigint NULL DEFAULT NULL COMMENT '防治记录ID（外键关联防治记录表）',
  `plan_id` bigint NOT NULL COMMENT '计划ID（外键关联养殖计划表）',
  `area_id` bigint NOT NULL COMMENT '区域ID（外键关联养殖区域表）',
  `medicine_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '药品名称',
  `medicine_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '药品类型（抗生素/消毒剂/中药等）',
  `dosage` decimal(10, 2) NOT NULL COMMENT '用量（克/毫升）',
  `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '克' COMMENT '单位',
  `unit_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '单价（元/单位）',
  `total_cost` decimal(12, 2) NULL DEFAULT NULL COMMENT '总成本（元）',
  `usage_date` date NOT NULL COMMENT '用药日期',
  `usage_method` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '使用方法（拌料/泼洒/注射等）',
  `batch_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '批次号',
  `expiry_date` date NULL DEFAULT NULL COMMENT '有效期至',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `status` int NOT NULL DEFAULT 1 COMMENT '状态（1-正常，0-已删除）',
  `creator_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`usage_id`) USING BTREE,
  INDEX `idx_record_id`(`record_id` ASC) USING BTREE,
  INDEX `idx_prevention_id`(`prevention_id` ASC) USING BTREE,
  INDEX `idx_plan_id`(`plan_id` ASC) USING BTREE,
  INDEX `idx_area_id`(`area_id` ASC) USING BTREE,
  INDEX `idx_usage_date`(`usage_date` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用药记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of medicine_usage
-- ----------------------------
INSERT INTO `medicine_usage` VALUES (1, 1, 1, 12, 13, '含氯消毒剂', '消毒剂', 500.00, '毫升', 5.50, 2750.00, '2025-04-02', '全池泼洒', 'BATCH2025040201', '2026-04-02', '浓度为0.5ppm', 1, 1, '2025-11-17 05:51:28', '2025-11-17 05:51:28');
INSERT INTO `medicine_usage` VALUES (2, 1, 1, 12, 13, '抗病毒药物', '抗病毒药', 200.00, '克', 12.00, 2400.00, '2025-04-02', '拌料投喂', 'BATCH2025040202', '2026-04-02', '连续使用3天', 1, 1, '2025-11-17 05:51:28', '2025-11-17 05:51:28');
INSERT INTO `medicine_usage` VALUES (3, 2, 2, 13, 14, '恩诺沙星', '抗生素', 300.00, '克', 15.00, 4500.00, '2025-05-11', '拌料投喂', 'BATCH2025051101', '2026-05-11', '按每公斤饲料添加', 1, 1, '2025-11-17 05:51:28', '2025-11-17 05:51:28');
INSERT INTO `medicine_usage` VALUES (4, 3, 4, 14, 16, '生石灰', '消毒剂', 100.00, '公斤', 2.50, 250.00, '2025-03-16', '全池泼洒', 'BATCH2025031601', '2027-03-16', '浓度为20kg/亩', 1, 1, '2025-11-17 05:51:28', '2025-11-17 05:51:28');
INSERT INTO `medicine_usage` VALUES (5, 3, 4, 14, 16, '板蓝根提取物', '中药', 150.00, '克', 8.00, 1200.00, '2025-03-16', '拌料投喂', 'BATCH2025031602', '2026-03-16', '预防性用药', 1, 1, '2025-11-17 05:51:28', '2025-11-17 05:51:28');
INSERT INTO `medicine_usage` VALUES (6, 4, NULL, 12, 13, '益生菌制剂', '益生菌', 500.00, '克', 6.00, 3000.00, '2025-04-21', '泼洒', 'BATCH2025042101', '2026-04-21', '调节水质和肠道', 1, 1, '2025-11-17 05:51:28', '2025-11-17 05:51:28');

-- ----------------------------
-- Table structure for plan_adjust
-- ----------------------------
DROP TABLE IF EXISTS `plan_adjust`;
CREATE TABLE `plan_adjust`  (
  `adjust_id` bigint NOT NULL AUTO_INCREMENT COMMENT '调整ID',
  `plan_id` bigint NOT NULL COMMENT '原计划ID（外键）',
  `adjust_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调整原因',
  `old_params` json NULL COMMENT '原参数（JSON格式）',
  `new_params` json NULL COMMENT '新参数（JSON格式）',
  `adjust_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调整类型（延长周期/修改目标产量/其他）',
  `approve_status` tinyint NOT NULL DEFAULT 0 COMMENT '审批状态（0-待审批，1-已通过，2-已驳回）',
  `approver_id` bigint NULL DEFAULT NULL COMMENT '审批人ID（外键）',
  `approve_opinion` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '审批意见',
  `approve_time` datetime NULL DEFAULT NULL COMMENT '审批时间',
  `creator_id` bigint NOT NULL COMMENT '创建人ID（外键）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`adjust_id`) USING BTREE,
  INDEX `idx_plan_id`(`plan_id` ASC) USING BTREE,
  INDEX `idx_approve_status`(`approve_status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '计划调整表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of plan_adjust
-- ----------------------------

-- ----------------------------
-- Table structure for sales_record
-- ----------------------------
DROP TABLE IF EXISTS `sales_record`;
CREATE TABLE `sales_record`  (
  `sales_id` bigint NOT NULL AUTO_INCREMENT COMMENT '销售记录ID',
  `yield_id` bigint NULL DEFAULT NULL COMMENT '产量统计ID（外键关联产量统计表）',
  `plan_id` bigint NOT NULL COMMENT '计划ID（外键关联养殖计划表）',
  `area_id` bigint NOT NULL COMMENT '区域ID（外键关联养殖区域表）',
  `breed_id` bigint NOT NULL COMMENT '品种ID（外键关联养殖品种表）',
  `customer_id` bigint NOT NULL COMMENT '客户ID（外键关联客户表）',
  `sales_quantity` decimal(10, 2) NOT NULL COMMENT '销售数量（公斤）',
  `unit_price` decimal(10, 2) NOT NULL COMMENT '单价（元/公斤）',
  `total_amount` decimal(12, 2) NOT NULL COMMENT '总金额（元）',
  `sales_date` date NOT NULL COMMENT '销售日期',
  `sales_channel` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '销售渠道（批发/零售/加工）',
  `payment_method` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '付款方式（现金/转账/赊账）',
  `payment_status` int NULL DEFAULT 0 COMMENT '付款状态（0-未付款，1-已付款，2-部分付款）',
  `invoice_status` int NULL DEFAULT 0 COMMENT '发票状态（0-未开票，1-已开票）',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `status` int NOT NULL DEFAULT 1 COMMENT '状态（1-正常，0-已删除）',
  `creator_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`sales_id`) USING BTREE,
  INDEX `idx_yield_id`(`yield_id` ASC) USING BTREE,
  INDEX `idx_plan_id`(`plan_id` ASC) USING BTREE,
  INDEX `idx_area_id`(`area_id` ASC) USING BTREE,
  INDEX `idx_customer_id`(`customer_id` ASC) USING BTREE,
  INDEX `idx_sales_date`(`sales_date` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '销售记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sales_record
-- ----------------------------
INSERT INTO `sales_record` VALUES (1, 21, 12, 13, 15, 1, 500.00, 45.00, 22500.00, '2025-04-15', '批发', '转账', 1, 1, '南美白对虾，规格200-300g', 1, 1, '2025-11-17 05:56:38', '2025-11-17 05:56:38');
INSERT INTO `sales_record` VALUES (2, 22, 12, 13, 15, 1, 800.00, 45.00, 36000.00, '2025-04-20', '批发', '转账', 1, 1, '南美白对虾，规格200-300g', 1, 1, '2025-11-17 05:56:38', '2025-11-17 05:56:38');
INSERT INTO `sales_record` VALUES (3, 23, 12, 13, 15, 2, 700.00, 48.00, 33600.00, '2025-04-25', '加工', '转账', 1, 1, '南美白对虾，加工用', 1, 1, '2025-11-17 05:56:38', '2025-11-17 05:56:38');
INSERT INTO `sales_record` VALUES (4, 24, 13, 14, 16, 4, 1000.00, 55.00, 55000.00, '2025-05-20', '批发', '转账', 1, 1, '斑节对虾，规格300-500g', 1, 1, '2025-11-17 05:56:38', '2025-11-17 05:56:38');
INSERT INTO `sales_record` VALUES (5, 25, 13, 14, 16, 3, 200.00, 58.00, 11600.00, '2025-05-22', '零售', '现金', 1, 0, '斑节对虾，零售', 1, 1, '2025-11-17 05:56:38', '2025-11-17 05:56:38');
INSERT INTO `sales_record` VALUES (6, 21, 12, 13, 15, 4, 300.00, 46.00, 13800.00, '2025-04-18', '批发', '转账', 1, 1, '南美白对虾，规格200-300g', 1, 1, '2025-11-17 05:56:38', '2025-11-17 05:56:38');

-- ----------------------------
-- Table structure for statistic_result
-- ----------------------------
DROP TABLE IF EXISTS `statistic_result`;
CREATE TABLE `statistic_result`  (
  `statistic_id` bigint NOT NULL AUTO_INCREMENT COMMENT '统计ID',
  `statistic_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '统计指标名称',
  `statistic_dimension` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '统计维度（部门/区域/品种/时间等）',
  `dimension_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '维度值',
  `start_date` date NULL DEFAULT NULL COMMENT '开始日期',
  `end_date` date NULL DEFAULT NULL COMMENT '结束日期',
  `statistic_value` decimal(15, 2) NOT NULL COMMENT '统计值',
  `statistic_unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '统计单位',
  `calculate_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '计算时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`statistic_id`) USING BTREE,
  INDEX `idx_statistic_name`(`statistic_name` ASC) USING BTREE,
  INDEX `idx_dimension`(`statistic_dimension` ASC, `dimension_value` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 31 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '统计结果表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of statistic_result
-- ----------------------------

-- ----------------------------
-- Table structure for sys_message
-- ----------------------------
DROP TABLE IF EXISTS `sys_message`;
CREATE TABLE `sys_message`  (
  `message_id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `receiver_id` bigint NOT NULL COMMENT '接收人ID（外键）',
  `sender_id` bigint NULL DEFAULT NULL COMMENT '发送人ID（外键，0表示系统）',
  `message_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息标题',
  `message_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息内容',
  `message_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息类型（通知/提醒/公告）',
  `business_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '关联业务类型',
  `business_id` bigint NULL DEFAULT NULL COMMENT '关联业务ID',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态（0-未读，1-已读）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `read_time` datetime NULL DEFAULT NULL COMMENT '阅读时间',
  PRIMARY KEY (`message_id`) USING BTREE,
  INDEX `idx_receiver_id`(`receiver_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_message
-- ----------------------------
INSERT INTO `sys_message` VALUES (24, 1, 0, '新用户注册待审核', '用户【东部测试】（普通操作员）已注册，等待审核。用户名：dongbu，真实姓名：东部测试，联系方式：未填写', '通知', 'user', 25, 1, '2025-11-17 07:43:11', '2025-11-17 08:25:14');
INSERT INTO `sys_message` VALUES (25, 24, 0, '新用户注册待审核', '用户【东部测试】（普通操作员）已注册，等待审核。用户名：dongbu，真实姓名：东部测试，联系方式：未填写', '通知', 'user', 25, 1, '2025-11-17 07:43:11', '2025-11-17 08:40:16');
INSERT INTO `sys_message` VALUES (26, 25, 24, '账号审核通过', '您的账号已通过审核，已分配区域：东海岸2号养殖池。', '通知', 'user', 25, 0, '2025-11-17 08:03:49', NULL);

-- ----------------------------
-- Table structure for sys_oper_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log`  (
  `log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` bigint NOT NULL COMMENT '用户ID（外键）',
  `module` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作模块',
  `oper_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作类型（新增/修改/删除/审核/登录等）',
  `oper_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作内容',
  `oper_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `oper_result` tinyint NOT NULL DEFAULT 1 COMMENT '操作结果（0-失败，1-成功）',
  `error_msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '错误信息',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`log_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_module`(`module` ASC) USING BTREE,
  INDEX `idx_oper_time`(`oper_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 247 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_oper_log
-- ----------------------------
INSERT INTO `sys_oper_log` VALUES (146, 1, '系统管理', '新增', '执行操作: /api/permission/assign', '2025-11-17 05:00:09', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (147, 1, '系统管理', '新增', '执行操作: /api/permission/assign', '2025-11-17 05:00:17', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (148, 1, '系统管理', '新增', '执行操作: /api/permission/assign', '2025-11-17 05:00:20', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (149, 1, '系统管理', '新增', '执行操作: /api/permission/assign', '2025-11-17 05:00:26', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (150, 1, '系统管理', '新增', '执行操作: /api/permission/assign', '2025-11-17 05:00:32', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (151, 1, '养殖计划管理', '新增', '新增养殖计划', '2025-11-17 05:04:20', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (152, 1, '产量统计管理', '新增', '新增产量统计: 东海岸深水区 海参', '2025-11-17 05:18:39', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (153, 1, '产量统计管理', '新增', '新增产量统计', '2025-11-17 05:22:05', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (154, 1, '养殖计划管理', '新增', '新增养殖计划', '2025-11-17 05:29:44', '127.0.0.1', 0, '\r\n### Error updating database.  Cause: com.mysql.cj.jdbc.exceptions.MysqlDataTruncation: Data truncation: Invalid JSON text: \"The document is empty.\" at position 0 in value for column \'plan_adjust.new_params\'.\r\n### The error may exist in com/server/aquacultureserver/mapper/PlanAdjustMapper.java (best guess)\r\n### The error may involve com.server.aquacultureserver.mapper.PlanAdjustMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO plan_adjust  ( plan_id, adjust_reason,  new_params, adjust_type, approve_status,    creator_id )  VALUES  ( ?, ?,  ?, ?, ?,    ? )\r\n### Cause: com.mysql.cj.jdbc.exceptions.MysqlDataTruncation: Data truncation: Invalid JSON text: \"The document is empty.\" at position 0 in value for column \'plan_adjust.new_params\'.\n; Data truncation: Invalid JSON text: \"The document is empty.\" at position 0 in value for column \'plan_adjust.new_params\'.; nested exception is com.mysql.cj.jdbc.exceptions.MysqlDataTruncation: Data truncation: Invalid JSON text: \"The document is empty.\" at position 0 in value for column \'plan_adjust.new_params\'.', NULL);
INSERT INTO `sys_oper_log` VALUES (155, 1, '系统管理', '新增', '执行操作: /api/permission/assign', '2025-11-17 06:14:18', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (156, 1, '系统管理', '新增', '执行操作: /api/permission/assign', '2025-11-17 06:20:39', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (157, 1, '系统管理', '新增', '执行操作: /api/permission/assign', '2025-11-17 06:38:20', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (158, 1, '系统管理', '新增', '执行操作: /api/permission/assign', '2025-11-17 06:51:28', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (159, 1, '系统管理', '新增', '执行操作: /api/permission/assign', '2025-11-17 06:51:50', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (160, 1, '系统管理', '新增', '执行操作: /api/permission/assign', '2025-11-17 07:00:22', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (161, 1, '养殖计划管理', '新增', '新增养殖计划: 测试', '2025-11-17 07:03:50', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (162, 1, '系统管理', '新增', '执行操作: /api/permission/assign', '2025-11-17 07:04:14', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (163, 1, '用户管理', '新增', '新增用户: bumen', '2025-11-17 07:07:36', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (164, 1, '用户管理', '删除', '删除用户: 部门管理员', '2025-11-17 07:07:43', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (165, 1, '用户管理', '新增', '新增用户: bumen', '2025-11-17 07:29:20', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (166, 1, '用户管理', '修改', '修改用户信息: 部门管理员测试', '2025-11-17 07:29:48', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (167, 1, '系统管理', '新增', '执行操作: /api/permission/assign', '2025-11-17 07:33:00', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (168, 1, '用户管理', '修改', '修改用户信息: 部门管理员测试', '2025-11-17 07:39:24', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (169, 1, '用户管理', '登录', '用户登录系统：admin', '2025-11-17 07:43:28', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (170, 1, '用户管理', '修改', '新增用户', '2025-11-17 07:44:08', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (171, 24, '用户管理', '登录', '用户登录系统：bumen', '2025-11-17 07:44:39', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (172, 1, '用户管理', '登录', '用户登录系统：admin', '2025-11-17 07:50:14', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (173, 24, '用户管理', '登录', '用户登录系统：bumen', '2025-11-17 08:02:02', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (174, 24, '用户管理', '新增', '新增用户', '2025-11-17 08:03:49', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (175, 24, '用户管理', '新增', '新增用户', '2025-11-17 08:03:58', '127.0.0.1', 0, '该用户不是待审核状态', NULL);
INSERT INTO `sys_oper_log` VALUES (176, 25, '用户管理', '登录', '用户登录系统：dongbu', '2025-11-17 08:04:10', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (177, 1, '用户管理', '登录', '用户登录系统：admin', '2025-11-17 08:04:38', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (178, 1, '系统管理', '新增', '执行操作: /api/permission/assign', '2025-11-17 08:05:16', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (179, 24, '用户管理', '登录', '用户登录系统：bumen', '2025-11-17 08:05:36', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (180, 1, '用户管理', '登录', '用户登录系统：admin', '2025-11-17 08:15:02', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (181, 24, '用户管理', '登录', '用户登录系统：bumen', '2025-11-17 08:18:16', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (182, 1, '用户管理', '登录', '用户登录系统：admin', '2025-11-17 08:20:40', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (183, 1, '系统管理', '修改', '执行操作: /api/message/all/read', '2025-11-17 08:25:14', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (184, 1, '系统管理', '新增', '执行操作: /api/permission/assign', '2025-11-17 08:26:01', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (185, 24, '用户管理', '登录', '用户登录系统：bumen', '2025-11-17 08:28:46', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (186, 24, '系统管理', '修改', '执行操作: /api/message/all/read', '2025-11-17 08:40:16', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (187, 1, '用户管理', '登录', '用户登录系统：admin', '2025-11-17 08:40:46', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (188, 1, '用户管理', '修改', '修改用户信息: 测试', '2025-11-17 08:40:56', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (189, 1, '用户管理', '修改', '新增用户', '2025-11-17 08:41:07', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (190, 21, '用户管理', '登录', '用户登录系统：bxwy', '2025-11-17 08:41:21', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (191, 1, '用户管理', '登录', '用户登录系统：admin', '2025-11-17 08:42:22', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (192, 1, '系统管理', '新增', '执行操作: /api/permission/assign', '2025-11-17 08:42:40', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (193, 21, '用户管理', '登录', '用户登录系统：bxwy', '2025-11-17 08:42:45', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (194, 1, '用户管理', '登录', '用户登录系统：admin', '2025-11-17 08:54:58', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (195, 1, '饲料管理', '新增', '新增饲料使用记录', '2025-11-17 18:39:54', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (196, 1, '饲料管理', '新增', '新增饲料使用记录', '2025-11-17 18:50:35', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (197, 1, '饲料管理', '新增', '新增饲料采购记录', '2025-11-17 18:52:02', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (198, 1, '饲料管理', '新增', '新增饲料库存记录', '2025-11-17 18:52:02', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (199, 1, '饲料管理', '删除', '删除饲料采购记录', '2025-11-17 18:52:32', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (200, 1, '饲料管理', '删除', '删除饲料使用记录', '2025-11-17 18:56:17', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (201, 1, '饲料管理', '删除', '删除饲料使用记录', '2025-11-17 18:56:20', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (202, 1, '饲料管理', '新增', '新增饲料采购记录', '2025-11-17 19:06:10', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (203, 1, '饲料管理', '删除', '删除饲料采购记录', '2025-11-17 19:07:08', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (204, 1, '饲料管理', '新增', '新增饲料采购记录', '2025-11-17 19:08:02', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (205, 1, '饲料管理', '修改', '修改饲料采购记录', '2025-11-17 19:11:08', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (206, 1, '饲料管理', '删除', '删除饲料采购记录', '2025-11-17 19:12:17', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (207, 1, '饲料管理', '新增', '新增饲料采购记录', '2025-11-17 19:22:37', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (208, 1, '饲料管理', '新增', '新增饲料使用记录', '2025-11-17 19:22:54', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (209, 1, '饲料管理', '修改', '修改饲料使用记录', '2025-11-17 19:24:10', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (210, 1, '养殖计划管理', '修改', '修改养殖计划: 测试', '2025-11-17 19:24:22', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (211, 1, '产量统计管理', '修改', '修改产量统计: 东海岸深水区 海参', '2025-11-17 19:25:23', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (212, 1, '系统管理', '新增', '执行操作: /api/permission/assign', '2025-11-17 19:53:42', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (213, 1, '系统管理', '新增', '执行操作: /api/permission/assign', '2025-11-17 19:54:57', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (214, 1, '系统管理', '新增', '执行操作: /api/permission/assign', '2025-11-17 19:55:04', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (215, 1, '系统管理', '新增', '执行操作: /api/permission/assign', '2025-11-17 19:55:19', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (216, 1, '系统管理', '新增', '执行操作: /api/permission/assign', '2025-11-17 19:55:29', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (217, 1, '系统管理', '新增', '执行操作: /api/medicine/purchase', '2025-11-17 20:07:35', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (218, 1, '系统管理', '删除', '执行操作: /api/medicine/purchase/10', '2025-11-17 20:08:26', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (219, 1, '系统管理', '修改', '执行操作: /api/medicine/purchase', '2025-11-17 20:08:39', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (220, 1, '养殖计划管理', '修改', '修改养殖计划: 东海岸1号池南美白对虾春季养殖计划', '2025-11-17 20:12:55', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (221, 1, '养殖计划管理', '修改', '修改养殖计划: 东海岸2号池斑节对虾夏季养殖计划', '2025-11-17 20:13:06', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (222, 1, '养殖计划管理', '修改', '修改养殖计划: 西区1号鱼塘草鱼年度养殖计划', '2025-11-17 20:13:28', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (223, 1, '养殖计划管理', '修改', '修改养殖计划: 西区2号鱼塘鲤鱼春季养殖计划', '2025-11-17 20:13:55', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (224, 1, '养殖计划管理', '修改', '修改养殖计划: 东海岸深水区海参养殖计划', '2025-11-17 20:14:28', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (225, 1, '养殖计划管理', '修改', '修改养殖计划: 西区3号鱼塘鲫鱼养殖计划', '2025-11-17 20:14:31', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (226, 1, '养殖计划管理', '修改', '修改养殖计划: 东海岸3号池扇贝养殖计划', '2025-11-17 20:14:35', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (227, 1, '养殖计划管理', '新增', '新增养殖计划', '2025-11-17 20:19:00', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (228, 1, '养殖计划管理', '删除', '删除养殖计划: 测试', '2025-11-17 20:19:03', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (229, 1, '饲料管理', '新增', '新增饲料使用记录', '2025-11-17 20:19:28', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (230, 1, '饲料管理', '删除', '删除饲料使用记录', '2025-11-17 20:19:33', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (231, 1, '养殖计划管理', '修改', '修改养殖计划: 东海岸2号池斑节对虾夏季养殖计划', '2025-11-17 20:20:14', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (232, 1, '养殖计划管理', '修改', '修改养殖计划: 西区1号鱼塘草鱼年度养殖计划', '2025-11-17 20:20:17', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (233, 1, '养殖计划管理', '修改', '修改养殖计划: 西区2号鱼塘鲤鱼春季养殖计划', '2025-11-17 20:20:20', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (234, 1, '养殖计划管理', '修改', '修改养殖计划: 西区3号鱼塘鲫鱼养殖计划', '2025-11-17 20:20:23', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (235, 1, '养殖计划管理', '修改', '修改养殖计划: 东海岸深水区海参养殖计划', '2025-11-17 20:20:27', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (236, 1, '养殖计划管理', '修改', '修改养殖计划: 东海岸3号池扇贝养殖计划', '2025-11-17 20:20:30', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (237, 1, '用户管理', '登录', '用户登录系统：admin', '2025-12-01 13:21:58', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (238, 1, '用户管理', '登录', '用户登录系统：admin', '2025-12-01 13:36:54', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (239, 1, '系统管理', '新增', '执行操作: /api/upload/image', '2025-12-01 13:43:18', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (240, 1, '养殖品种管理', '修改', '修改养殖品种: 南美白对虾', '2025-12-01 13:43:21', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (241, 1, '系统管理', '新增', '执行操作: /api/upload/image', '2025-12-01 13:45:00', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (242, 1, '设备管理', '修改', '修改设备信息: 增氧机', '2025-12-01 13:45:01', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (243, 1, '系统管理', '新增', '执行操作: /api/upload/image', '2025-12-01 13:45:29', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (244, 1, '病害防控管理', '修改', '修改病害记录', '2025-12-01 13:45:30', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (245, 1, '系统管理', '新增', '执行操作: /api/upload/image', '2025-12-01 13:51:27', '127.0.0.1', 1, NULL, NULL);
INSERT INTO `sys_oper_log` VALUES (246, 1, '饲料管理', '修改', '修改饲料采购记录', '2025-12-01 13:51:29', '127.0.0.1', 1, NULL, NULL);

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission`  (
  `permission_id` bigint NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `permission_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限标识（唯一）',
  `permission_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限名称',
  `module` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '所属模块',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父权限ID',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序顺序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`permission_id`) USING BTREE,
  UNIQUE INDEX `uk_permission_code`(`permission_code` ASC) USING BTREE,
  INDEX `idx_module`(`module` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 293 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_permission
-- ----------------------------
INSERT INTO `sys_permission` VALUES (141, 'system:view', '系统管理', 'system', 0, 1, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (142, 'system:user:view', '用户查看', 'system', 141, 1, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (143, 'system:user:add', '用户新增', 'system', 141, 2, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (144, 'system:user:edit', '用户编辑', 'system', 141, 3, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (145, 'system:user:delete', '用户删除', 'system', 141, 4, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (146, 'system:user:reset', '用户密码重置', 'system', 141, 5, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (147, 'system:user:approve', '用户审核', 'system', 141, 6, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (148, 'system:role:view', '角色查看', 'system', 141, 7, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (149, 'system:role:add', '角色新增', 'system', 141, 8, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (150, 'system:role:edit', '角色编辑', 'system', 141, 9, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (151, 'system:role:delete', '角色删除', 'system', 141, 10, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (152, 'system:permission:view', '权限查看', 'system', 141, 11, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (153, 'system:permission:add', '权限新增', 'system', 141, 12, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (154, 'system:permission:edit', '权限编辑', 'system', 141, 13, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (155, 'system:permission:delete', '权限删除', 'system', 141, 14, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (156, 'plan:view', '计划管理', 'plan', 0, 2, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (157, 'plan:list', '计划列表查看', 'plan', 156, 1, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (158, 'plan:detail', '计划详情查看', 'plan', 156, 2, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (159, 'plan:add', '计划新增', 'plan', 156, 3, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (160, 'plan:edit', '计划编辑', 'plan', 156, 4, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (161, 'plan:delete', '计划删除', 'plan', 156, 5, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (162, 'plan:approve', '计划审批', 'plan', 156, 6, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (163, 'plan:adjust:view', '计划调整查看', 'plan', 156, 7, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (164, 'plan:adjust:add', '计划调整新增', 'plan', 156, 8, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (165, 'plan:adjust:edit', '计划调整编辑', 'plan', 156, 9, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (166, 'plan:adjust:delete', '计划调整删除', 'plan', 156, 10, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (167, 'plan:adjust:approve', '计划调整审批', 'plan', 156, 11, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (168, 'area:view', '区域管理', 'area', 0, 3, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (169, 'area:list', '区域列表查看', 'area', 168, 1, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (170, 'area:detail', '区域详情查看', 'area', 168, 2, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (171, 'area:add', '区域新增', 'area', 168, 3, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (172, 'area:edit', '区域编辑', 'area', 168, 4, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (173, 'area:delete', '区域删除', 'area', 168, 5, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (174, 'breed:view', '品种管理', 'breed', 0, 4, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (175, 'breed:list', '品种列表查看', 'breed', 174, 1, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (176, 'breed:detail', '品种详情查看', 'breed', 174, 2, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (177, 'breed:add', '品种新增', 'breed', 174, 3, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (178, 'breed:edit', '品种编辑', 'breed', 174, 4, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (179, 'breed:delete', '品种删除', 'breed', 174, 5, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (180, 'equipment:view', '设备管理', 'equipment', 0, 5, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (181, 'equipment:list', '设备列表查看', 'equipment', 180, 1, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (182, 'equipment:detail', '设备详情查看', 'equipment', 180, 2, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (183, 'equipment:add', '设备新增', 'equipment', 180, 3, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (184, 'equipment:edit', '设备编辑', 'equipment', 180, 4, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (185, 'equipment:delete', '设备删除', 'equipment', 180, 5, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (186, 'equipment:maintain', '设备维护', 'equipment', 180, 6, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (187, 'yield:view', '产量统计', 'yield', 0, 6, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (188, 'yield:list', '产量列表查看', 'yield', 187, 1, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (189, 'yield:detail', '产量详情查看', 'yield', 187, 2, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (190, 'yield:add', '产量新增', 'yield', 187, 3, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (191, 'yield:edit', '产量编辑', 'yield', 187, 4, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (192, 'yield:delete', '产量删除', 'yield', 187, 5, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (193, 'yield:audit', '产量审核', 'yield', 187, 6, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (194, 'yield:evidence:view', '产量凭证查看', 'yield', 187, 7, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (195, 'yield:evidence:add', '产量凭证新增', 'yield', 187, 8, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (196, 'yield:evidence:delete', '产量凭证删除', 'yield', 187, 9, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (197, 'statistics:view', '统计分析', 'statistics', 0, 7, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (198, 'statistics:list', '统计列表查看', 'statistics', 197, 1, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (199, 'statistics:report', '统计报表', 'statistics', 197, 2, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (200, 'statistics:export', '统计导出', 'statistics', 197, 3, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (201, 'dashboard:view', '仪表盘查看', 'dashboard', 0, 8, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (202, 'message:view', '消息通知', 'message', 0, 9, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (203, 'message:list', '消息列表查看', 'message', 202, 1, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (204, 'message:detail', '消息详情查看', 'message', 202, 2, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (205, 'message:send', '消息发送', 'message', 202, 3, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (206, 'message:read', '消息标记已读', 'message', 202, 4, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (207, 'message:delete', '消息删除', 'message', 202, 5, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (208, 'log:view', '操作日志', 'log', 0, 10, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (209, 'log:list', '日志列表查看', 'log', 208, 1, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (210, 'log:detail', '日志详情查看', 'log', 208, 2, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (211, 'log:export', '日志导出', 'log', 208, 3, '2025-11-17 02:44:36');
INSERT INTO `sys_permission` VALUES (212, 'department', '部门管理', 'department', 0, 1, '2025-11-17 04:16:38');
INSERT INTO `sys_permission` VALUES (213, 'department:view', '部门查看', 'department', 212, 1, '2025-11-17 04:16:38');
INSERT INTO `sys_permission` VALUES (214, 'department:add', '部门新增', 'department', 212, 2, '2025-11-17 04:16:38');
INSERT INTO `sys_permission` VALUES (215, 'department:edit', '部门编辑', 'department', 212, 3, '2025-11-17 04:16:38');
INSERT INTO `sys_permission` VALUES (216, 'department:delete', '部门删除', 'department', 212, 4, '2025-11-17 04:16:38');
INSERT INTO `sys_permission` VALUES (217, 'feed:purchase:view', '饲料采购查看', '饲料管理', 0, 101, '2025-11-17 05:44:50');
INSERT INTO `sys_permission` VALUES (218, 'feed:purchase:add', '饲料采购新增', '饲料管理', 217, 102, '2025-11-17 05:44:50');
INSERT INTO `sys_permission` VALUES (219, 'feed:purchase:edit', '饲料采购编辑', '饲料管理', 217, 103, '2025-11-17 05:44:50');
INSERT INTO `sys_permission` VALUES (220, 'feed:purchase:delete', '饲料采购删除', '饲料管理', 217, 104, '2025-11-17 05:44:50');
INSERT INTO `sys_permission` VALUES (221, 'feed:inventory:view', '饲料库存查看', '饲料管理', 0, 105, '2025-11-17 05:44:50');
INSERT INTO `sys_permission` VALUES (222, 'feed:inventory:add', '饲料库存新增', '饲料管理', 221, 106, '2025-11-17 05:44:50');
INSERT INTO `sys_permission` VALUES (223, 'feed:inventory:edit', '饲料库存编辑', '饲料管理', 221, 107, '2025-11-17 05:44:50');
INSERT INTO `sys_permission` VALUES (224, 'feed:inventory:delete', '饲料库存删除', '饲料管理', 221, 108, '2025-11-17 05:44:50');
INSERT INTO `sys_permission` VALUES (225, 'feed:usage:view', '饲料使用查看', '饲料管理', 0, 109, '2025-11-17 05:44:50');
INSERT INTO `sys_permission` VALUES (226, 'feed:usage:add', '饲料使用新增', '饲料管理', 225, 110, '2025-11-17 05:44:50');
INSERT INTO `sys_permission` VALUES (227, 'feed:usage:edit', '饲料使用编辑', '饲料管理', 225, 111, '2025-11-17 05:44:50');
INSERT INTO `sys_permission` VALUES (228, 'feed:usage:delete', '饲料使用删除', '饲料管理', 225, 112, '2025-11-17 05:44:50');
INSERT INTO `sys_permission` VALUES (229, 'disease:record:view', '病害记录查看', '病害防控管理', 0, 201, '2025-11-17 05:53:32');
INSERT INTO `sys_permission` VALUES (230, 'disease:record:add', '病害记录新增', '病害防控管理', 229, 202, '2025-11-17 05:53:32');
INSERT INTO `sys_permission` VALUES (231, 'disease:record:edit', '病害记录编辑', '病害防控管理', 229, 203, '2025-11-17 05:53:32');
INSERT INTO `sys_permission` VALUES (232, 'disease:record:delete', '病害记录删除', '病害防控管理', 229, 204, '2025-11-17 05:53:32');
INSERT INTO `sys_permission` VALUES (233, 'disease:prevention:view', '病害防治查看', '病害防控管理', 0, 205, '2025-11-17 05:53:32');
INSERT INTO `sys_permission` VALUES (234, 'disease:prevention:add', '病害防治新增', '病害防控管理', 233, 206, '2025-11-17 05:53:32');
INSERT INTO `sys_permission` VALUES (235, 'disease:prevention:edit', '病害防治编辑', '病害防控管理', 233, 207, '2025-11-17 05:53:32');
INSERT INTO `sys_permission` VALUES (236, 'disease:prevention:delete', '病害防治删除', '病害防控管理', 233, 208, '2025-11-17 05:53:32');
INSERT INTO `sys_permission` VALUES (237, 'disease:medicine:view', '用药记录查看', '病害防控管理', 0, 209, '2025-11-17 05:53:32');
INSERT INTO `sys_permission` VALUES (238, 'disease:medicine:add', '用药记录新增', '病害防控管理', 237, 210, '2025-11-17 05:53:32');
INSERT INTO `sys_permission` VALUES (239, 'disease:medicine:edit', '用药记录编辑', '病害防控管理', 237, 211, '2025-11-17 05:53:32');
INSERT INTO `sys_permission` VALUES (240, 'disease:medicine:delete', '用药记录删除', '病害防控管理', 237, 212, '2025-11-17 05:53:32');
INSERT INTO `sys_permission` VALUES (241, 'production:feeding:view', '投喂记录查看', '日常生产记录', 0, 301, '2025-11-17 05:56:12');
INSERT INTO `sys_permission` VALUES (242, 'production:feeding:add', '投喂记录新增', '日常生产记录', 241, 302, '2025-11-17 05:56:12');
INSERT INTO `sys_permission` VALUES (243, 'production:feeding:edit', '投喂记录编辑', '日常生产记录', 241, 303, '2025-11-17 05:56:12');
INSERT INTO `sys_permission` VALUES (244, 'production:feeding:delete', '投喂记录删除', '日常生产记录', 241, 304, '2025-11-17 05:56:12');
INSERT INTO `sys_permission` VALUES (245, 'production:inspection:view', '日常检查查看', '日常生产记录', 0, 305, '2025-11-17 05:56:12');
INSERT INTO `sys_permission` VALUES (246, 'production:inspection:add', '日常检查新增', '日常生产记录', 245, 306, '2025-11-17 05:56:12');
INSERT INTO `sys_permission` VALUES (247, 'production:inspection:edit', '日常检查编辑', '日常生产记录', 245, 307, '2025-11-17 05:56:12');
INSERT INTO `sys_permission` VALUES (248, 'production:inspection:delete', '日常检查删除', '日常生产记录', 245, 308, '2025-11-17 05:56:12');
INSERT INTO `sys_permission` VALUES (249, 'sales:customer:view', '客户管理查看', '销售管理', 0, 401, '2025-11-17 05:57:45');
INSERT INTO `sys_permission` VALUES (250, 'sales:customer:add', '客户管理新增', '销售管理', 249, 402, '2025-11-17 05:57:45');
INSERT INTO `sys_permission` VALUES (251, 'sales:customer:edit', '客户管理编辑', '销售管理', 249, 403, '2025-11-17 05:57:45');
INSERT INTO `sys_permission` VALUES (252, 'sales:customer:delete', '客户管理删除', '销售管理', 249, 404, '2025-11-17 05:57:45');
INSERT INTO `sys_permission` VALUES (253, 'sales:record:view', '销售记录查看', '销售管理', 0, 405, '2025-11-17 05:57:45');
INSERT INTO `sys_permission` VALUES (254, 'sales:record:add', '销售记录新增', '销售管理', 253, 406, '2025-11-17 05:57:45');
INSERT INTO `sys_permission` VALUES (255, 'sales:record:edit', '销售记录编辑', '销售管理', 253, 407, '2025-11-17 05:57:45');
INSERT INTO `sys_permission` VALUES (256, 'sales:record:delete', '销售记录删除', '销售管理', 253, 408, '2025-11-17 05:57:45');
INSERT INTO `sys_permission` VALUES (257, 'feed:purchase:list', '饲料采购列表查看', '饲料管理', 217, 1, '2025-11-17 06:43:58');
INSERT INTO `sys_permission` VALUES (258, 'feed:purchase:detail', '饲料采购详情查看', '饲料管理', 217, 2, '2025-11-17 06:43:58');
INSERT INTO `sys_permission` VALUES (259, 'feed:inventory:list', '饲料库存列表查看', '饲料管理', 221, 1, '2025-11-17 06:43:59');
INSERT INTO `sys_permission` VALUES (260, 'feed:inventory:detail', '饲料库存详情查看', '饲料管理', 221, 2, '2025-11-17 06:43:59');
INSERT INTO `sys_permission` VALUES (261, 'feed:usage:list', '饲料使用列表查看', '饲料管理', 225, 1, '2025-11-17 06:44:00');
INSERT INTO `sys_permission` VALUES (262, 'feed:usage:detail', '饲料使用详情查看', '饲料管理', 225, 2, '2025-11-17 06:44:00');
INSERT INTO `sys_permission` VALUES (263, 'disease:record:list', '病害记录列表查看', '病害防控管理', 229, 1, '2025-11-17 06:44:01');
INSERT INTO `sys_permission` VALUES (264, 'disease:record:detail', '病害记录详情查看', '病害防控管理', 229, 2, '2025-11-17 06:44:01');
INSERT INTO `sys_permission` VALUES (265, 'disease:prevention:list', '病害防治列表查看', '病害防控管理', 233, 1, '2025-11-17 06:44:02');
INSERT INTO `sys_permission` VALUES (266, 'disease:prevention:detail', '病害防治详情查看', '病害防控管理', 233, 2, '2025-11-17 06:44:02');
INSERT INTO `sys_permission` VALUES (267, 'disease:medicine:list', '用药记录列表查看', '病害防控管理', 237, 1, '2025-11-17 06:44:03');
INSERT INTO `sys_permission` VALUES (268, 'disease:medicine:detail', '用药记录详情查看', '病害防控管理', 237, 2, '2025-11-17 06:44:03');
INSERT INTO `sys_permission` VALUES (269, 'production:feeding:list', '投喂记录列表查看', '日常生产记录', 241, 1, '2025-11-17 06:44:04');
INSERT INTO `sys_permission` VALUES (270, 'production:feeding:detail', '投喂记录详情查看', '日常生产记录', 241, 2, '2025-11-17 06:44:04');
INSERT INTO `sys_permission` VALUES (271, 'production:inspection:list', '日常检查列表查看', '日常生产记录', 245, 1, '2025-11-17 06:44:06');
INSERT INTO `sys_permission` VALUES (272, 'production:inspection:detail', '日常检查详情查看', '日常生产记录', 245, 2, '2025-11-17 06:44:06');
INSERT INTO `sys_permission` VALUES (273, 'sales:customer:list', '客户管理列表查看', '销售管理', 249, 1, '2025-11-17 06:44:07');
INSERT INTO `sys_permission` VALUES (274, 'sales:customer:detail', '客户管理详情查看', '销售管理', 249, 2, '2025-11-17 06:44:07');
INSERT INTO `sys_permission` VALUES (275, 'sales:record:list', '销售记录列表查看', '销售管理', 253, 1, '2025-11-17 06:44:08');
INSERT INTO `sys_permission` VALUES (276, 'sales:record:detail', '销售记录详情查看', '销售管理', 253, 2, '2025-11-17 06:44:08');
INSERT INTO `sys_permission` VALUES (277, 'log:delete', '日志删除', 'log', 208, 4, '2025-11-17 08:23:47');
INSERT INTO `sys_permission` VALUES (278, 'log:clear', '日志清空', 'log', 208, 5, '2025-11-17 08:23:48');
INSERT INTO `sys_permission` VALUES (279, 'medicine:purchase:view', '药品采购查看', '病害防控管理', 0, 113, '2025-11-17 19:51:24');
INSERT INTO `sys_permission` VALUES (280, 'medicine:inventory:view', '药品库存查看', '病害防控管理', 0, 117, '2025-11-17 19:51:25');
INSERT INTO `sys_permission` VALUES (281, 'medicine:purchase:list', '药品采购列表查看', '病害防控管理', 279, 1, '2025-11-17 19:51:32');
INSERT INTO `sys_permission` VALUES (282, 'medicine:purchase:detail', '药品采购详情查看', '病害防控管理', 279, 2, '2025-11-17 19:51:32');
INSERT INTO `sys_permission` VALUES (283, 'medicine:purchase:add', '药品采购新增', '病害防控管理', 279, 114, '2025-11-17 19:51:32');
INSERT INTO `sys_permission` VALUES (284, 'medicine:purchase:edit', '药品采购编辑', '病害防控管理', 279, 115, '2025-11-17 19:51:32');
INSERT INTO `sys_permission` VALUES (285, 'medicine:purchase:delete', '药品采购删除', '病害防控管理', 279, 116, '2025-11-17 19:51:32');
INSERT INTO `sys_permission` VALUES (288, 'medicine:inventory:list', '药品库存列表查看', '病害防控管理', 280, 1, '2025-11-17 19:51:37');
INSERT INTO `sys_permission` VALUES (289, 'medicine:inventory:detail', '药品库存详情查看', '病害防控管理', 280, 2, '2025-11-17 19:51:37');
INSERT INTO `sys_permission` VALUES (290, 'medicine:inventory:add', '药品库存新增', '病害防控管理', 280, 118, '2025-11-17 19:51:37');
INSERT INTO `sys_permission` VALUES (291, 'medicine:inventory:edit', '药品库存编辑', '病害防控管理', 280, 119, '2025-11-17 19:51:37');
INSERT INTO `sys_permission` VALUES (292, 'medicine:inventory:delete', '药品库存删除', '病害防控管理', 280, 120, '2025-11-17 19:51:37');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `role_id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `role_desc` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '权限描述',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（0-禁用，1-启用）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`role_id`) USING BTREE,
  UNIQUE INDEX `uk_role_name`(`role_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '系统管理员', '拥有全系统操作权限，可管理所有部门数据', 1, '2025-11-17 00:31:29', '2025-11-17 04:44:33');
INSERT INTO `sys_role` VALUES (3, '普通操作员', '仅能管理本部门或该区域数据，可录入和查看本部门计划、产量等', 1, '2025-11-17 00:31:29', '2025-11-17 00:31:29');
INSERT INTO `sys_role` VALUES (4, '决策层', '仅具备数据查看权限，可查看各类报表和统计数据，无修改权限', 1, '2025-11-17 00:31:29', '2025-11-17 00:31:29');
INSERT INTO `sys_role` VALUES (5, '部门管理员', '可管理本部门数据，可审批计划、审核产量，可查看本部门统计报表', 1, '2025-11-17 04:16:46', '2025-11-17 04:16:46');

-- ----------------------------
-- Table structure for sys_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint NOT NULL COMMENT '角色ID（外键）',
  `permission_id` bigint NOT NULL COMMENT '权限ID（外键）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_permission`(`role_id` ASC, `permission_id` ASC) USING BTREE,
  INDEX `fk_rp_permission`(`permission_id` ASC) USING BTREE,
  CONSTRAINT `fk_rp_permission` FOREIGN KEY (`permission_id`) REFERENCES `sys_permission` (`permission_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_rp_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`role_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3132 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色权限关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_permission
-- ----------------------------
INSERT INTO `sys_role_permission` VALUES (2705, 1, 141);
INSERT INTO `sys_role_permission` VALUES (2706, 1, 142);
INSERT INTO `sys_role_permission` VALUES (2707, 1, 143);
INSERT INTO `sys_role_permission` VALUES (2708, 1, 144);
INSERT INTO `sys_role_permission` VALUES (2709, 1, 145);
INSERT INTO `sys_role_permission` VALUES (2710, 1, 146);
INSERT INTO `sys_role_permission` VALUES (2711, 1, 147);
INSERT INTO `sys_role_permission` VALUES (2712, 1, 148);
INSERT INTO `sys_role_permission` VALUES (2713, 1, 149);
INSERT INTO `sys_role_permission` VALUES (2714, 1, 150);
INSERT INTO `sys_role_permission` VALUES (2715, 1, 151);
INSERT INTO `sys_role_permission` VALUES (2716, 1, 152);
INSERT INTO `sys_role_permission` VALUES (2717, 1, 153);
INSERT INTO `sys_role_permission` VALUES (2718, 1, 154);
INSERT INTO `sys_role_permission` VALUES (2719, 1, 155);
INSERT INTO `sys_role_permission` VALUES (2720, 1, 156);
INSERT INTO `sys_role_permission` VALUES (2721, 1, 157);
INSERT INTO `sys_role_permission` VALUES (2722, 1, 158);
INSERT INTO `sys_role_permission` VALUES (2723, 1, 159);
INSERT INTO `sys_role_permission` VALUES (2724, 1, 160);
INSERT INTO `sys_role_permission` VALUES (2725, 1, 161);
INSERT INTO `sys_role_permission` VALUES (2726, 1, 162);
INSERT INTO `sys_role_permission` VALUES (2727, 1, 163);
INSERT INTO `sys_role_permission` VALUES (2728, 1, 164);
INSERT INTO `sys_role_permission` VALUES (2729, 1, 165);
INSERT INTO `sys_role_permission` VALUES (2730, 1, 166);
INSERT INTO `sys_role_permission` VALUES (2731, 1, 167);
INSERT INTO `sys_role_permission` VALUES (2732, 1, 168);
INSERT INTO `sys_role_permission` VALUES (2733, 1, 169);
INSERT INTO `sys_role_permission` VALUES (2734, 1, 170);
INSERT INTO `sys_role_permission` VALUES (2735, 1, 171);
INSERT INTO `sys_role_permission` VALUES (2736, 1, 172);
INSERT INTO `sys_role_permission` VALUES (2737, 1, 173);
INSERT INTO `sys_role_permission` VALUES (2738, 1, 174);
INSERT INTO `sys_role_permission` VALUES (2739, 1, 175);
INSERT INTO `sys_role_permission` VALUES (2740, 1, 176);
INSERT INTO `sys_role_permission` VALUES (2741, 1, 177);
INSERT INTO `sys_role_permission` VALUES (2742, 1, 178);
INSERT INTO `sys_role_permission` VALUES (2743, 1, 179);
INSERT INTO `sys_role_permission` VALUES (2744, 1, 180);
INSERT INTO `sys_role_permission` VALUES (2745, 1, 181);
INSERT INTO `sys_role_permission` VALUES (2746, 1, 182);
INSERT INTO `sys_role_permission` VALUES (2747, 1, 183);
INSERT INTO `sys_role_permission` VALUES (2748, 1, 184);
INSERT INTO `sys_role_permission` VALUES (2749, 1, 185);
INSERT INTO `sys_role_permission` VALUES (2750, 1, 186);
INSERT INTO `sys_role_permission` VALUES (2751, 1, 187);
INSERT INTO `sys_role_permission` VALUES (2752, 1, 188);
INSERT INTO `sys_role_permission` VALUES (2753, 1, 189);
INSERT INTO `sys_role_permission` VALUES (2754, 1, 190);
INSERT INTO `sys_role_permission` VALUES (2755, 1, 191);
INSERT INTO `sys_role_permission` VALUES (2756, 1, 192);
INSERT INTO `sys_role_permission` VALUES (2757, 1, 193);
INSERT INTO `sys_role_permission` VALUES (2758, 1, 194);
INSERT INTO `sys_role_permission` VALUES (2759, 1, 195);
INSERT INTO `sys_role_permission` VALUES (2760, 1, 196);
INSERT INTO `sys_role_permission` VALUES (2761, 1, 197);
INSERT INTO `sys_role_permission` VALUES (2762, 1, 198);
INSERT INTO `sys_role_permission` VALUES (2763, 1, 199);
INSERT INTO `sys_role_permission` VALUES (2764, 1, 200);
INSERT INTO `sys_role_permission` VALUES (2765, 1, 201);
INSERT INTO `sys_role_permission` VALUES (2766, 1, 202);
INSERT INTO `sys_role_permission` VALUES (2767, 1, 203);
INSERT INTO `sys_role_permission` VALUES (2768, 1, 204);
INSERT INTO `sys_role_permission` VALUES (2769, 1, 205);
INSERT INTO `sys_role_permission` VALUES (2770, 1, 206);
INSERT INTO `sys_role_permission` VALUES (2771, 1, 207);
INSERT INTO `sys_role_permission` VALUES (2772, 1, 208);
INSERT INTO `sys_role_permission` VALUES (2773, 1, 209);
INSERT INTO `sys_role_permission` VALUES (2774, 1, 210);
INSERT INTO `sys_role_permission` VALUES (2775, 1, 211);
INSERT INTO `sys_role_permission` VALUES (2776, 1, 212);
INSERT INTO `sys_role_permission` VALUES (2777, 1, 213);
INSERT INTO `sys_role_permission` VALUES (2778, 1, 214);
INSERT INTO `sys_role_permission` VALUES (2779, 1, 215);
INSERT INTO `sys_role_permission` VALUES (2780, 1, 216);
INSERT INTO `sys_role_permission` VALUES (2781, 1, 217);
INSERT INTO `sys_role_permission` VALUES (2782, 1, 218);
INSERT INTO `sys_role_permission` VALUES (2783, 1, 219);
INSERT INTO `sys_role_permission` VALUES (2784, 1, 220);
INSERT INTO `sys_role_permission` VALUES (2785, 1, 221);
INSERT INTO `sys_role_permission` VALUES (2786, 1, 222);
INSERT INTO `sys_role_permission` VALUES (2787, 1, 223);
INSERT INTO `sys_role_permission` VALUES (2788, 1, 224);
INSERT INTO `sys_role_permission` VALUES (2789, 1, 225);
INSERT INTO `sys_role_permission` VALUES (2790, 1, 226);
INSERT INTO `sys_role_permission` VALUES (2791, 1, 227);
INSERT INTO `sys_role_permission` VALUES (2792, 1, 228);
INSERT INTO `sys_role_permission` VALUES (2793, 1, 229);
INSERT INTO `sys_role_permission` VALUES (2794, 1, 230);
INSERT INTO `sys_role_permission` VALUES (2795, 1, 231);
INSERT INTO `sys_role_permission` VALUES (2796, 1, 232);
INSERT INTO `sys_role_permission` VALUES (2797, 1, 233);
INSERT INTO `sys_role_permission` VALUES (2798, 1, 234);
INSERT INTO `sys_role_permission` VALUES (2799, 1, 235);
INSERT INTO `sys_role_permission` VALUES (2800, 1, 236);
INSERT INTO `sys_role_permission` VALUES (2801, 1, 237);
INSERT INTO `sys_role_permission` VALUES (2802, 1, 238);
INSERT INTO `sys_role_permission` VALUES (2803, 1, 239);
INSERT INTO `sys_role_permission` VALUES (2804, 1, 240);
INSERT INTO `sys_role_permission` VALUES (2805, 1, 241);
INSERT INTO `sys_role_permission` VALUES (2806, 1, 242);
INSERT INTO `sys_role_permission` VALUES (2807, 1, 243);
INSERT INTO `sys_role_permission` VALUES (2808, 1, 244);
INSERT INTO `sys_role_permission` VALUES (2809, 1, 245);
INSERT INTO `sys_role_permission` VALUES (2810, 1, 246);
INSERT INTO `sys_role_permission` VALUES (2811, 1, 247);
INSERT INTO `sys_role_permission` VALUES (2812, 1, 248);
INSERT INTO `sys_role_permission` VALUES (2813, 1, 249);
INSERT INTO `sys_role_permission` VALUES (2814, 1, 250);
INSERT INTO `sys_role_permission` VALUES (2815, 1, 251);
INSERT INTO `sys_role_permission` VALUES (2816, 1, 252);
INSERT INTO `sys_role_permission` VALUES (2817, 1, 253);
INSERT INTO `sys_role_permission` VALUES (2818, 1, 254);
INSERT INTO `sys_role_permission` VALUES (2819, 1, 255);
INSERT INTO `sys_role_permission` VALUES (2670, 1, 256);
INSERT INTO `sys_role_permission` VALUES (2671, 1, 257);
INSERT INTO `sys_role_permission` VALUES (2672, 1, 258);
INSERT INTO `sys_role_permission` VALUES (2673, 1, 259);
INSERT INTO `sys_role_permission` VALUES (2674, 1, 260);
INSERT INTO `sys_role_permission` VALUES (2675, 1, 261);
INSERT INTO `sys_role_permission` VALUES (2676, 1, 262);
INSERT INTO `sys_role_permission` VALUES (2677, 1, 263);
INSERT INTO `sys_role_permission` VALUES (2678, 1, 264);
INSERT INTO `sys_role_permission` VALUES (2679, 1, 265);
INSERT INTO `sys_role_permission` VALUES (2680, 1, 266);
INSERT INTO `sys_role_permission` VALUES (2681, 1, 267);
INSERT INTO `sys_role_permission` VALUES (2682, 1, 268);
INSERT INTO `sys_role_permission` VALUES (2683, 1, 269);
INSERT INTO `sys_role_permission` VALUES (2684, 1, 270);
INSERT INTO `sys_role_permission` VALUES (2685, 1, 271);
INSERT INTO `sys_role_permission` VALUES (2686, 1, 272);
INSERT INTO `sys_role_permission` VALUES (2687, 1, 273);
INSERT INTO `sys_role_permission` VALUES (2688, 1, 274);
INSERT INTO `sys_role_permission` VALUES (2689, 1, 275);
INSERT INTO `sys_role_permission` VALUES (2690, 1, 276);
INSERT INTO `sys_role_permission` VALUES (2691, 1, 277);
INSERT INTO `sys_role_permission` VALUES (2692, 1, 278);
INSERT INTO `sys_role_permission` VALUES (2693, 1, 279);
INSERT INTO `sys_role_permission` VALUES (2694, 1, 280);
INSERT INTO `sys_role_permission` VALUES (2695, 1, 281);
INSERT INTO `sys_role_permission` VALUES (2696, 1, 282);
INSERT INTO `sys_role_permission` VALUES (2697, 1, 283);
INSERT INTO `sys_role_permission` VALUES (2698, 1, 284);
INSERT INTO `sys_role_permission` VALUES (2699, 1, 285);
INSERT INTO `sys_role_permission` VALUES (2700, 1, 288);
INSERT INTO `sys_role_permission` VALUES (2701, 1, 289);
INSERT INTO `sys_role_permission` VALUES (2702, 1, 290);
INSERT INTO `sys_role_permission` VALUES (2703, 1, 291);
INSERT INTO `sys_role_permission` VALUES (2704, 1, 292);
INSERT INTO `sys_role_permission` VALUES (2989, 3, 156);
INSERT INTO `sys_role_permission` VALUES (2990, 3, 157);
INSERT INTO `sys_role_permission` VALUES (2991, 3, 158);
INSERT INTO `sys_role_permission` VALUES (2992, 3, 159);
INSERT INTO `sys_role_permission` VALUES (2993, 3, 160);
INSERT INTO `sys_role_permission` VALUES (2994, 3, 163);
INSERT INTO `sys_role_permission` VALUES (2995, 3, 164);
INSERT INTO `sys_role_permission` VALUES (2996, 3, 165);
INSERT INTO `sys_role_permission` VALUES (2997, 3, 168);
INSERT INTO `sys_role_permission` VALUES (2998, 3, 169);
INSERT INTO `sys_role_permission` VALUES (2999, 3, 170);
INSERT INTO `sys_role_permission` VALUES (3000, 3, 171);
INSERT INTO `sys_role_permission` VALUES (3001, 3, 172);
INSERT INTO `sys_role_permission` VALUES (3002, 3, 174);
INSERT INTO `sys_role_permission` VALUES (3003, 3, 175);
INSERT INTO `sys_role_permission` VALUES (3004, 3, 176);
INSERT INTO `sys_role_permission` VALUES (3005, 3, 177);
INSERT INTO `sys_role_permission` VALUES (3006, 3, 178);
INSERT INTO `sys_role_permission` VALUES (3007, 3, 180);
INSERT INTO `sys_role_permission` VALUES (3008, 3, 181);
INSERT INTO `sys_role_permission` VALUES (3009, 3, 182);
INSERT INTO `sys_role_permission` VALUES (3010, 3, 183);
INSERT INTO `sys_role_permission` VALUES (3011, 3, 184);
INSERT INTO `sys_role_permission` VALUES (3012, 3, 186);
INSERT INTO `sys_role_permission` VALUES (3013, 3, 187);
INSERT INTO `sys_role_permission` VALUES (3014, 3, 188);
INSERT INTO `sys_role_permission` VALUES (3015, 3, 189);
INSERT INTO `sys_role_permission` VALUES (3016, 3, 190);
INSERT INTO `sys_role_permission` VALUES (3017, 3, 191);
INSERT INTO `sys_role_permission` VALUES (3018, 3, 194);
INSERT INTO `sys_role_permission` VALUES (3019, 3, 195);
INSERT INTO `sys_role_permission` VALUES (3020, 3, 196);
INSERT INTO `sys_role_permission` VALUES (3021, 3, 197);
INSERT INTO `sys_role_permission` VALUES (3022, 3, 198);
INSERT INTO `sys_role_permission` VALUES (3023, 3, 199);
INSERT INTO `sys_role_permission` VALUES (3024, 3, 200);
INSERT INTO `sys_role_permission` VALUES (3025, 3, 201);
INSERT INTO `sys_role_permission` VALUES (3026, 3, 202);
INSERT INTO `sys_role_permission` VALUES (3027, 3, 203);
INSERT INTO `sys_role_permission` VALUES (3028, 3, 204);
INSERT INTO `sys_role_permission` VALUES (3029, 3, 205);
INSERT INTO `sys_role_permission` VALUES (3030, 3, 206);
INSERT INTO `sys_role_permission` VALUES (3031, 3, 207);
INSERT INTO `sys_role_permission` VALUES (3032, 3, 217);
INSERT INTO `sys_role_permission` VALUES (3033, 3, 218);
INSERT INTO `sys_role_permission` VALUES (3034, 3, 219);
INSERT INTO `sys_role_permission` VALUES (3035, 3, 221);
INSERT INTO `sys_role_permission` VALUES (3036, 3, 222);
INSERT INTO `sys_role_permission` VALUES (3037, 3, 223);
INSERT INTO `sys_role_permission` VALUES (3038, 3, 225);
INSERT INTO `sys_role_permission` VALUES (3039, 3, 226);
INSERT INTO `sys_role_permission` VALUES (3040, 3, 227);
INSERT INTO `sys_role_permission` VALUES (3041, 3, 229);
INSERT INTO `sys_role_permission` VALUES (3042, 3, 230);
INSERT INTO `sys_role_permission` VALUES (3043, 3, 231);
INSERT INTO `sys_role_permission` VALUES (3044, 3, 233);
INSERT INTO `sys_role_permission` VALUES (3045, 3, 234);
INSERT INTO `sys_role_permission` VALUES (3046, 3, 235);
INSERT INTO `sys_role_permission` VALUES (3047, 3, 237);
INSERT INTO `sys_role_permission` VALUES (3048, 3, 238);
INSERT INTO `sys_role_permission` VALUES (3049, 3, 239);
INSERT INTO `sys_role_permission` VALUES (3050, 3, 241);
INSERT INTO `sys_role_permission` VALUES (3051, 3, 242);
INSERT INTO `sys_role_permission` VALUES (3052, 3, 243);
INSERT INTO `sys_role_permission` VALUES (3053, 3, 245);
INSERT INTO `sys_role_permission` VALUES (3054, 3, 246);
INSERT INTO `sys_role_permission` VALUES (3055, 3, 247);
INSERT INTO `sys_role_permission` VALUES (3056, 3, 249);
INSERT INTO `sys_role_permission` VALUES (3057, 3, 250);
INSERT INTO `sys_role_permission` VALUES (3058, 3, 253);
INSERT INTO `sys_role_permission` VALUES (3059, 3, 254);
INSERT INTO `sys_role_permission` VALUES (3060, 3, 255);
INSERT INTO `sys_role_permission` VALUES (2960, 3, 257);
INSERT INTO `sys_role_permission` VALUES (2961, 3, 258);
INSERT INTO `sys_role_permission` VALUES (2962, 3, 259);
INSERT INTO `sys_role_permission` VALUES (2963, 3, 260);
INSERT INTO `sys_role_permission` VALUES (2964, 3, 261);
INSERT INTO `sys_role_permission` VALUES (2965, 3, 262);
INSERT INTO `sys_role_permission` VALUES (2966, 3, 263);
INSERT INTO `sys_role_permission` VALUES (2967, 3, 264);
INSERT INTO `sys_role_permission` VALUES (2968, 3, 265);
INSERT INTO `sys_role_permission` VALUES (2969, 3, 266);
INSERT INTO `sys_role_permission` VALUES (2970, 3, 267);
INSERT INTO `sys_role_permission` VALUES (2971, 3, 268);
INSERT INTO `sys_role_permission` VALUES (2972, 3, 269);
INSERT INTO `sys_role_permission` VALUES (2973, 3, 270);
INSERT INTO `sys_role_permission` VALUES (2974, 3, 271);
INSERT INTO `sys_role_permission` VALUES (2975, 3, 272);
INSERT INTO `sys_role_permission` VALUES (2976, 3, 273);
INSERT INTO `sys_role_permission` VALUES (2977, 3, 274);
INSERT INTO `sys_role_permission` VALUES (2978, 3, 275);
INSERT INTO `sys_role_permission` VALUES (2979, 3, 276);
INSERT INTO `sys_role_permission` VALUES (2980, 3, 279);
INSERT INTO `sys_role_permission` VALUES (2981, 3, 280);
INSERT INTO `sys_role_permission` VALUES (2982, 3, 281);
INSERT INTO `sys_role_permission` VALUES (2983, 3, 282);
INSERT INTO `sys_role_permission` VALUES (2984, 3, 283);
INSERT INTO `sys_role_permission` VALUES (2985, 3, 288);
INSERT INTO `sys_role_permission` VALUES (2986, 3, 289);
INSERT INTO `sys_role_permission` VALUES (2987, 3, 290);
INSERT INTO `sys_role_permission` VALUES (2988, 3, 291);
INSERT INTO `sys_role_permission` VALUES (3073, 4, 141);
INSERT INTO `sys_role_permission` VALUES (3075, 4, 142);
INSERT INTO `sys_role_permission` VALUES (3087, 4, 156);
INSERT INTO `sys_role_permission` VALUES (3088, 4, 157);
INSERT INTO `sys_role_permission` VALUES (3089, 4, 158);
INSERT INTO `sys_role_permission` VALUES (3092, 4, 162);
INSERT INTO `sys_role_permission` VALUES (3093, 4, 163);
INSERT INTO `sys_role_permission` VALUES (3094, 4, 167);
INSERT INTO `sys_role_permission` VALUES (3095, 4, 168);
INSERT INTO `sys_role_permission` VALUES (3096, 4, 169);
INSERT INTO `sys_role_permission` VALUES (3097, 4, 170);
INSERT INTO `sys_role_permission` VALUES (3098, 4, 174);
INSERT INTO `sys_role_permission` VALUES (3099, 4, 175);
INSERT INTO `sys_role_permission` VALUES (3100, 4, 176);
INSERT INTO `sys_role_permission` VALUES (3101, 4, 180);
INSERT INTO `sys_role_permission` VALUES (3102, 4, 181);
INSERT INTO `sys_role_permission` VALUES (3103, 4, 182);
INSERT INTO `sys_role_permission` VALUES (3104, 4, 187);
INSERT INTO `sys_role_permission` VALUES (3105, 4, 188);
INSERT INTO `sys_role_permission` VALUES (3106, 4, 189);
INSERT INTO `sys_role_permission` VALUES (3107, 4, 193);
INSERT INTO `sys_role_permission` VALUES (3108, 4, 194);
INSERT INTO `sys_role_permission` VALUES (3109, 4, 197);
INSERT INTO `sys_role_permission` VALUES (3110, 4, 198);
INSERT INTO `sys_role_permission` VALUES (3111, 4, 199);
INSERT INTO `sys_role_permission` VALUES (3112, 4, 200);
INSERT INTO `sys_role_permission` VALUES (3113, 4, 201);
INSERT INTO `sys_role_permission` VALUES (3114, 4, 202);
INSERT INTO `sys_role_permission` VALUES (3115, 4, 203);
INSERT INTO `sys_role_permission` VALUES (3116, 4, 204);
INSERT INTO `sys_role_permission` VALUES (3117, 4, 205);
INSERT INTO `sys_role_permission` VALUES (3118, 4, 206);
INSERT INTO `sys_role_permission` VALUES (3119, 4, 207);
INSERT INTO `sys_role_permission` VALUES (3120, 4, 212);
INSERT INTO `sys_role_permission` VALUES (3121, 4, 213);
INSERT INTO `sys_role_permission` VALUES (3122, 4, 217);
INSERT INTO `sys_role_permission` VALUES (3123, 4, 221);
INSERT INTO `sys_role_permission` VALUES (3124, 4, 225);
INSERT INTO `sys_role_permission` VALUES (3125, 4, 229);
INSERT INTO `sys_role_permission` VALUES (3126, 4, 233);
INSERT INTO `sys_role_permission` VALUES (3127, 4, 237);
INSERT INTO `sys_role_permission` VALUES (3128, 4, 241);
INSERT INTO `sys_role_permission` VALUES (3129, 4, 245);
INSERT INTO `sys_role_permission` VALUES (3130, 4, 249);
INSERT INTO `sys_role_permission` VALUES (3131, 4, 253);
INSERT INTO `sys_role_permission` VALUES (3061, 4, 257);
INSERT INTO `sys_role_permission` VALUES (3062, 4, 258);
INSERT INTO `sys_role_permission` VALUES (3063, 4, 259);
INSERT INTO `sys_role_permission` VALUES (3064, 4, 260);
INSERT INTO `sys_role_permission` VALUES (3065, 4, 261);
INSERT INTO `sys_role_permission` VALUES (3066, 4, 262);
INSERT INTO `sys_role_permission` VALUES (3067, 4, 263);
INSERT INTO `sys_role_permission` VALUES (3068, 4, 264);
INSERT INTO `sys_role_permission` VALUES (3069, 4, 265);
INSERT INTO `sys_role_permission` VALUES (3070, 4, 266);
INSERT INTO `sys_role_permission` VALUES (3071, 4, 267);
INSERT INTO `sys_role_permission` VALUES (3072, 4, 268);
INSERT INTO `sys_role_permission` VALUES (3074, 4, 269);
INSERT INTO `sys_role_permission` VALUES (3076, 4, 270);
INSERT INTO `sys_role_permission` VALUES (3077, 4, 271);
INSERT INTO `sys_role_permission` VALUES (3078, 4, 272);
INSERT INTO `sys_role_permission` VALUES (3079, 4, 273);
INSERT INTO `sys_role_permission` VALUES (3080, 4, 274);
INSERT INTO `sys_role_permission` VALUES (3081, 4, 275);
INSERT INTO `sys_role_permission` VALUES (3082, 4, 276);
INSERT INTO `sys_role_permission` VALUES (3083, 4, 279);
INSERT INTO `sys_role_permission` VALUES (3084, 4, 280);
INSERT INTO `sys_role_permission` VALUES (3085, 4, 281);
INSERT INTO `sys_role_permission` VALUES (3086, 4, 282);
INSERT INTO `sys_role_permission` VALUES (3090, 4, 288);
INSERT INTO `sys_role_permission` VALUES (3091, 4, 289);
INSERT INTO `sys_role_permission` VALUES (2853, 5, 141);
INSERT INTO `sys_role_permission` VALUES (2854, 5, 142);
INSERT INTO `sys_role_permission` VALUES (2855, 5, 143);
INSERT INTO `sys_role_permission` VALUES (2856, 5, 144);
INSERT INTO `sys_role_permission` VALUES (2857, 5, 145);
INSERT INTO `sys_role_permission` VALUES (2858, 5, 146);
INSERT INTO `sys_role_permission` VALUES (2859, 5, 147);
INSERT INTO `sys_role_permission` VALUES (2860, 5, 156);
INSERT INTO `sys_role_permission` VALUES (2861, 5, 157);
INSERT INTO `sys_role_permission` VALUES (2862, 5, 158);
INSERT INTO `sys_role_permission` VALUES (2863, 5, 159);
INSERT INTO `sys_role_permission` VALUES (2864, 5, 160);
INSERT INTO `sys_role_permission` VALUES (2865, 5, 161);
INSERT INTO `sys_role_permission` VALUES (2866, 5, 162);
INSERT INTO `sys_role_permission` VALUES (2867, 5, 163);
INSERT INTO `sys_role_permission` VALUES (2868, 5, 164);
INSERT INTO `sys_role_permission` VALUES (2869, 5, 165);
INSERT INTO `sys_role_permission` VALUES (2870, 5, 166);
INSERT INTO `sys_role_permission` VALUES (2871, 5, 167);
INSERT INTO `sys_role_permission` VALUES (2872, 5, 168);
INSERT INTO `sys_role_permission` VALUES (2873, 5, 169);
INSERT INTO `sys_role_permission` VALUES (2874, 5, 170);
INSERT INTO `sys_role_permission` VALUES (2875, 5, 171);
INSERT INTO `sys_role_permission` VALUES (2876, 5, 172);
INSERT INTO `sys_role_permission` VALUES (2877, 5, 173);
INSERT INTO `sys_role_permission` VALUES (2878, 5, 174);
INSERT INTO `sys_role_permission` VALUES (2879, 5, 175);
INSERT INTO `sys_role_permission` VALUES (2880, 5, 176);
INSERT INTO `sys_role_permission` VALUES (2881, 5, 177);
INSERT INTO `sys_role_permission` VALUES (2882, 5, 178);
INSERT INTO `sys_role_permission` VALUES (2883, 5, 179);
INSERT INTO `sys_role_permission` VALUES (2884, 5, 180);
INSERT INTO `sys_role_permission` VALUES (2885, 5, 181);
INSERT INTO `sys_role_permission` VALUES (2886, 5, 182);
INSERT INTO `sys_role_permission` VALUES (2887, 5, 183);
INSERT INTO `sys_role_permission` VALUES (2888, 5, 184);
INSERT INTO `sys_role_permission` VALUES (2889, 5, 185);
INSERT INTO `sys_role_permission` VALUES (2890, 5, 186);
INSERT INTO `sys_role_permission` VALUES (2891, 5, 187);
INSERT INTO `sys_role_permission` VALUES (2892, 5, 188);
INSERT INTO `sys_role_permission` VALUES (2893, 5, 189);
INSERT INTO `sys_role_permission` VALUES (2894, 5, 190);
INSERT INTO `sys_role_permission` VALUES (2895, 5, 191);
INSERT INTO `sys_role_permission` VALUES (2896, 5, 192);
INSERT INTO `sys_role_permission` VALUES (2897, 5, 193);
INSERT INTO `sys_role_permission` VALUES (2898, 5, 194);
INSERT INTO `sys_role_permission` VALUES (2899, 5, 195);
INSERT INTO `sys_role_permission` VALUES (2900, 5, 196);
INSERT INTO `sys_role_permission` VALUES (2901, 5, 197);
INSERT INTO `sys_role_permission` VALUES (2902, 5, 198);
INSERT INTO `sys_role_permission` VALUES (2903, 5, 199);
INSERT INTO `sys_role_permission` VALUES (2904, 5, 200);
INSERT INTO `sys_role_permission` VALUES (2905, 5, 201);
INSERT INTO `sys_role_permission` VALUES (2906, 5, 202);
INSERT INTO `sys_role_permission` VALUES (2907, 5, 203);
INSERT INTO `sys_role_permission` VALUES (2908, 5, 204);
INSERT INTO `sys_role_permission` VALUES (2909, 5, 205);
INSERT INTO `sys_role_permission` VALUES (2910, 5, 206);
INSERT INTO `sys_role_permission` VALUES (2911, 5, 207);
INSERT INTO `sys_role_permission` VALUES (2912, 5, 208);
INSERT INTO `sys_role_permission` VALUES (2913, 5, 209);
INSERT INTO `sys_role_permission` VALUES (2914, 5, 210);
INSERT INTO `sys_role_permission` VALUES (2915, 5, 211);
INSERT INTO `sys_role_permission` VALUES (2916, 5, 212);
INSERT INTO `sys_role_permission` VALUES (2917, 5, 213);
INSERT INTO `sys_role_permission` VALUES (2918, 5, 214);
INSERT INTO `sys_role_permission` VALUES (2919, 5, 215);
INSERT INTO `sys_role_permission` VALUES (2920, 5, 216);
INSERT INTO `sys_role_permission` VALUES (2921, 5, 217);
INSERT INTO `sys_role_permission` VALUES (2922, 5, 218);
INSERT INTO `sys_role_permission` VALUES (2923, 5, 219);
INSERT INTO `sys_role_permission` VALUES (2924, 5, 220);
INSERT INTO `sys_role_permission` VALUES (2925, 5, 221);
INSERT INTO `sys_role_permission` VALUES (2926, 5, 222);
INSERT INTO `sys_role_permission` VALUES (2927, 5, 223);
INSERT INTO `sys_role_permission` VALUES (2928, 5, 224);
INSERT INTO `sys_role_permission` VALUES (2929, 5, 225);
INSERT INTO `sys_role_permission` VALUES (2930, 5, 226);
INSERT INTO `sys_role_permission` VALUES (2931, 5, 227);
INSERT INTO `sys_role_permission` VALUES (2932, 5, 228);
INSERT INTO `sys_role_permission` VALUES (2933, 5, 229);
INSERT INTO `sys_role_permission` VALUES (2934, 5, 230);
INSERT INTO `sys_role_permission` VALUES (2935, 5, 231);
INSERT INTO `sys_role_permission` VALUES (2936, 5, 232);
INSERT INTO `sys_role_permission` VALUES (2937, 5, 233);
INSERT INTO `sys_role_permission` VALUES (2938, 5, 234);
INSERT INTO `sys_role_permission` VALUES (2939, 5, 235);
INSERT INTO `sys_role_permission` VALUES (2940, 5, 236);
INSERT INTO `sys_role_permission` VALUES (2941, 5, 237);
INSERT INTO `sys_role_permission` VALUES (2942, 5, 238);
INSERT INTO `sys_role_permission` VALUES (2943, 5, 239);
INSERT INTO `sys_role_permission` VALUES (2944, 5, 240);
INSERT INTO `sys_role_permission` VALUES (2945, 5, 241);
INSERT INTO `sys_role_permission` VALUES (2946, 5, 242);
INSERT INTO `sys_role_permission` VALUES (2947, 5, 243);
INSERT INTO `sys_role_permission` VALUES (2948, 5, 244);
INSERT INTO `sys_role_permission` VALUES (2949, 5, 245);
INSERT INTO `sys_role_permission` VALUES (2950, 5, 246);
INSERT INTO `sys_role_permission` VALUES (2951, 5, 247);
INSERT INTO `sys_role_permission` VALUES (2952, 5, 248);
INSERT INTO `sys_role_permission` VALUES (2953, 5, 249);
INSERT INTO `sys_role_permission` VALUES (2954, 5, 250);
INSERT INTO `sys_role_permission` VALUES (2955, 5, 251);
INSERT INTO `sys_role_permission` VALUES (2956, 5, 252);
INSERT INTO `sys_role_permission` VALUES (2957, 5, 253);
INSERT INTO `sys_role_permission` VALUES (2958, 5, 254);
INSERT INTO `sys_role_permission` VALUES (2959, 5, 255);
INSERT INTO `sys_role_permission` VALUES (2820, 5, 256);
INSERT INTO `sys_role_permission` VALUES (2821, 5, 257);
INSERT INTO `sys_role_permission` VALUES (2822, 5, 258);
INSERT INTO `sys_role_permission` VALUES (2823, 5, 259);
INSERT INTO `sys_role_permission` VALUES (2824, 5, 260);
INSERT INTO `sys_role_permission` VALUES (2825, 5, 261);
INSERT INTO `sys_role_permission` VALUES (2826, 5, 262);
INSERT INTO `sys_role_permission` VALUES (2827, 5, 263);
INSERT INTO `sys_role_permission` VALUES (2828, 5, 264);
INSERT INTO `sys_role_permission` VALUES (2829, 5, 265);
INSERT INTO `sys_role_permission` VALUES (2830, 5, 266);
INSERT INTO `sys_role_permission` VALUES (2831, 5, 267);
INSERT INTO `sys_role_permission` VALUES (2832, 5, 268);
INSERT INTO `sys_role_permission` VALUES (2833, 5, 269);
INSERT INTO `sys_role_permission` VALUES (2834, 5, 270);
INSERT INTO `sys_role_permission` VALUES (2835, 5, 271);
INSERT INTO `sys_role_permission` VALUES (2836, 5, 272);
INSERT INTO `sys_role_permission` VALUES (2837, 5, 273);
INSERT INTO `sys_role_permission` VALUES (2838, 5, 274);
INSERT INTO `sys_role_permission` VALUES (2839, 5, 275);
INSERT INTO `sys_role_permission` VALUES (2840, 5, 276);
INSERT INTO `sys_role_permission` VALUES (2841, 5, 279);
INSERT INTO `sys_role_permission` VALUES (2842, 5, 280);
INSERT INTO `sys_role_permission` VALUES (2843, 5, 281);
INSERT INTO `sys_role_permission` VALUES (2844, 5, 282);
INSERT INTO `sys_role_permission` VALUES (2845, 5, 283);
INSERT INTO `sys_role_permission` VALUES (2846, 5, 284);
INSERT INTO `sys_role_permission` VALUES (2847, 5, 285);
INSERT INTO `sys_role_permission` VALUES (2848, 5, 288);
INSERT INTO `sys_role_permission` VALUES (2849, 5, 289);
INSERT INTO `sys_role_permission` VALUES (2850, 5, 290);
INSERT INTO `sys_role_permission` VALUES (2851, 5, 291);
INSERT INTO `sys_role_permission` VALUES (2852, 5, 292);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账号（唯一）',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '加密密码',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `role_id` bigint NOT NULL COMMENT '角色ID（外键）',
  `area_id` bigint NULL DEFAULT NULL COMMENT '所属养殖区域ID',
  `department_id` bigint NULL DEFAULT NULL COMMENT '所属部门ID（部门管理员使用）',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系方式',
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '家庭地址',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像路径',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（0-禁用，1-启用）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE,
  CONSTRAINT `fk_user_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`role_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', '系统管理员', 1, NULL, NULL, '15177188743', '测试', '/uploads/avatar/2025/11/cad96f29-e080-438e-874c-dd3a9bedfae6.jpg', 1, '2025-11-17 00:33:16', '2025-11-17 00:33:16');
INSERT INTO `sys_user` VALUES (21, 'bxwy', 'e10adc3949ba59abbe56e057f20f883e', '测试', 3, 13, NULL, '16578746674', '测试', NULL, 1, '2025-11-17 02:52:41', '2025-11-17 08:40:56');
INSERT INTO `sys_user` VALUES (22, 'juece', 'e10adc3949ba59abbe56e057f20f883e', '决策测试', 4, 6, NULL, '16578746627', '测试测试', NULL, 1, '2025-11-17 03:08:50', '2025-11-17 03:08:50');
INSERT INTO `sys_user` VALUES (24, 'bumen', 'e10adc3949ba59abbe56e057f20f883e', '部门管理员测试', 5, NULL, 1, '', 'vr', NULL, 1, '2025-11-17 07:29:20', '2025-11-17 07:39:23');
INSERT INTO `sys_user` VALUES (25, 'dongbu', 'e10adc3949ba59abbe56e057f20f883e', '东部测试', 3, 14, 1, '', '', NULL, 1, '2025-11-17 07:43:11', '2025-11-17 07:43:11');

-- ----------------------------
-- Table structure for yield_evidence
-- ----------------------------
DROP TABLE IF EXISTS `yield_evidence`;
CREATE TABLE `yield_evidence`  (
  `evidence_id` bigint NOT NULL AUTO_INCREMENT COMMENT '凭证ID',
  `yield_id` bigint NOT NULL COMMENT '产量ID（外键）',
  `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '照片路径',
  `file_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件类型',
  `file_size` bigint NULL DEFAULT NULL COMMENT '文件大小（字节）',
  `upload_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `uploader_id` bigint NOT NULL COMMENT '上传人ID（外键）',
  PRIMARY KEY (`evidence_id`) USING BTREE,
  INDEX `idx_yield_id`(`yield_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '产量凭证表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of yield_evidence
-- ----------------------------

-- ----------------------------
-- Table structure for yield_statistics
-- ----------------------------
DROP TABLE IF EXISTS `yield_statistics`;
CREATE TABLE `yield_statistics`  (
  `yield_id` bigint NOT NULL AUTO_INCREMENT COMMENT '产量ID',
  `plan_id` bigint NOT NULL COMMENT '计划ID（外键）',
  `area_id` bigint NOT NULL COMMENT '区域ID（外键）',
  `breed_id` bigint NOT NULL COMMENT '品种ID（外键）',
  `actual_yield` decimal(10, 2) NOT NULL COMMENT '实际产量（公斤）',
  `specification` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '规格',
  `statistics_date` date NOT NULL COMMENT '统计日期',
  `catch_time` datetime NULL DEFAULT NULL COMMENT '捕捞时间',
  `manager_id` bigint NOT NULL COMMENT '负责人ID（外键）',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态（0-待审核，1-已通过，2-已驳回）',
  `auditor_id` bigint NULL DEFAULT NULL COMMENT '审核人ID（外键）',
  `audit_opinion` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '审核意见',
  `audit_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `creator_id` bigint NOT NULL COMMENT '创建人ID（外键）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`yield_id`) USING BTREE,
  INDEX `idx_plan_id`(`plan_id` ASC) USING BTREE,
  INDEX `idx_area_id`(`area_id` ASC) USING BTREE,
  INDEX `idx_statistics_date`(`statistics_date` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 34 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '产量统计表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of yield_statistics
-- ----------------------------
INSERT INTO `yield_statistics` VALUES (21, 12, 13, 15, 4800.00, '大规格', '2025-04-15', '2025-04-15 08:00:00', 1, 1, NULL, NULL, NULL, 1, '2025-11-17 04:50:59', '2025-11-17 04:50:59');
INSERT INTO `yield_statistics` VALUES (22, 12, 13, 15, 5200.00, '中规格', '2025-05-01', '2025-05-01 09:00:00', 1, 1, NULL, NULL, NULL, 1, '2025-11-17 04:50:59', '2025-11-17 04:50:59');
INSERT INTO `yield_statistics` VALUES (23, 12, 13, 15, 5100.00, '大规格', '2025-05-15', '2025-05-15 08:30:00', 1, 1, NULL, NULL, NULL, 1, '2025-11-17 04:50:59', '2025-11-17 04:50:59');
INSERT INTO `yield_statistics` VALUES (24, 13, 14, 16, 7500.00, '大规格', '2025-05-10', '2025-05-10 07:00:00', 1, 1, NULL, NULL, NULL, 1, '2025-11-17 04:50:59', '2025-11-17 04:50:59');
INSERT INTO `yield_statistics` VALUES (25, 13, 14, 16, 8200.00, '特大规格', '2025-06-01', '2025-06-01 08:00:00', 1, 1, NULL, NULL, NULL, 1, '2025-11-17 04:50:59', '2025-11-17 04:50:59');
INSERT INTO `yield_statistics` VALUES (26, 14, 16, 17, 9500.00, '标准规格', '2025-03-20', '2025-03-20 10:00:00', 1, 1, NULL, NULL, NULL, 1, '2025-11-17 04:50:59', '2025-11-17 04:50:59');
INSERT INTO `yield_statistics` VALUES (27, 14, 16, 17, 9800.00, '标准规格', '2025-04-20', '2025-04-20 09:30:00', 1, 1, NULL, NULL, NULL, 1, '2025-11-17 04:50:59', '2025-11-17 04:50:59');
INSERT INTO `yield_statistics` VALUES (28, 15, 17, 18, 5800.00, '标准规格', '2025-03-15', '2025-03-15 08:00:00', 1, 1, NULL, NULL, NULL, 1, '2025-11-17 04:50:59', '2025-11-17 04:50:59');
INSERT INTO `yield_statistics` VALUES (29, 15, 17, 18, 6200.00, '大规格', '2025-04-15', '2025-04-15 09:00:00', 1, 1, NULL, NULL, NULL, 1, '2025-11-17 04:50:59', '2025-11-17 04:50:59');
INSERT INTO `yield_statistics` VALUES (30, 16, 15, 21, 2800.00, '标准规格', '2025-06-10', '2025-06-10 07:30:00', 1, 1, NULL, NULL, NULL, 1, '2025-11-17 04:50:59', '2025-11-17 04:50:59');
INSERT INTO `yield_statistics` VALUES (31, 17, 18, 19, 3800.00, '标准规格', '2025-04-10', '2025-04-10 08:00:00', 1, 1, NULL, NULL, NULL, 1, '2025-11-17 04:50:59', '2025-11-17 04:50:59');
INSERT INTO `yield_statistics` VALUES (32, 17, 18, 19, 4200.00, '大规格', '2025-05-10', '2025-05-10 09:00:00', 1, 1, NULL, NULL, NULL, 1, '2025-11-17 04:50:59', '2025-11-17 04:50:59');
INSERT INTO `yield_statistics` VALUES (33, 18, 19, 20, 3000.00, '大规格', '2025-11-17', '2025-11-17 05:18:37', 1, 1, 1, '', '2025-11-17 05:22:05', 1, '2025-11-17 05:18:39', '2025-11-17 19:25:22');

SET FOREIGN_KEY_CHECKS = 1;
