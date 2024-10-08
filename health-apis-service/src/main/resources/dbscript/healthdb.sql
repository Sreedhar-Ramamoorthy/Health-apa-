SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for lead
-- ----------------------------
DROP TABLE IF EXISTS `lead`;
CREATE TABLE `lead`  (
  `lead_id` varchar(50) NOT NULL,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `mobile` varchar(255) NOT NULL,
  `birthday` datetime NOT NULL,
  `interest` json NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `agent_id` varchar(50) NOT NULL,
  PRIMARY KEY (`lead_id`)
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for question
-- ----------------------------
DROP TABLE IF EXISTS `question`;
CREATE TABLE `question`  (
  `customer_id` varchar(50) NOT NULL,
  `agent_id` varchar(50) NULL DEFAULT NULL,
  `quote_id` varchar(50) NOT NULL,
  `answers` json NOT NULL,
  PRIMARY KEY (`quote_id`)
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for quote
-- ----------------------------
DROP TABLE IF EXISTS `quote`;
CREATE TABLE `quote`  (
  `id` varchar(50) NOT NULL,
  `code` varchar(100) NULL DEFAULT NULL,
  `product_id` int(11) NOT NULL,
  `customer_id` varchar(50) NOT NULL,
  `agent_id` varchar(50) NULL DEFAULT NULL,
  `start_date` datetime(0) NULL DEFAULT NULL,
  `effective_date` datetime(0) NULL DEFAULT NULL,
  `renewal_date` datetime(0) NULL DEFAULT NULL,
  `benefit` json NOT NULL,
  `status` varchar(30) NOT NULL,
  `total_premium` decimal(20, 2) NOT NULL,
  `itl` decimal(20, 2)  NULL DEFAULT NULL,
  `phcf` decimal(20, 2)  NULL DEFAULT NULL,
  `stamp_duty` decimal(20, 2)  NULL DEFAULT NULL,
  `premium` decimal(20, 2)  NULL DEFAULT NULL,
  `balance` decimal(20, 2) NULL DEFAULT NULL,
  `payment_style` varchar(30)  NULL DEFAULT NULL,
  `ext_policy_id` int(11) NULL DEFAULT NULL,
  `ext_policy_number` varchar(50)  NULL DEFAULT NULL,
  `children_only` tinyint(1) NULL DEFAULT NULL,
  `archived` tinyint(1) NULL DEFAULT 0,
  `create_time` datetime(0) NOT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `create_by` varchar(50)  NULL DEFAULT NULL,
  `update_by` varchar(50)  NULL DEFAULT NULL,
  `hide` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for customer
-- ----------------------------
DROP TABLE IF EXISTS `customer`;
CREATE TABLE `customer`  (
  `customer_id` varchar(50) NOT NULL,
  `parent_id` varchar(50) NULL DEFAULT NULL,
  `agent_id` varchar(50) NULL DEFAULT NULL,
  `entity_id` bigint(11) NULL DEFAULT NULL,
  `quote_id` varchar(50) NULL DEFAULT NULL,
  `super_customer_id` varchar(50) NULL DEFAULT NULL,
  `first_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `last_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `date_of_birth` datetime(0) NOT NULL,
  `title` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `gender` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `phone_number` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `start_date` datetime(0) NULL DEFAULT NULL,
  `spouse_summary` json NULL,
  `children_summary` json NULL,
  `benefit` json NULL,
  `relationship_desc` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `id_no` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `kra_pin` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `last_login_time` datetime(0) NULL DEFAULT NULL,
  `update_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `create_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `update_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`customer_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for payment_history
-- ----------------------------
DROP TABLE IF EXISTS `payment_history`;
CREATE TABLE `payment_history`  (
  `id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `renewal` tinyint(1) NULL DEFAULT NULL,
  `customer_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `payment_phone` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `amount` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `payment_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `quote_number` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `policy_number` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `effective_date` datetime(0) NULL DEFAULT NULL,
  `premium` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `merchant_request_id` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `checkout_request_id` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `response_code` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `response_desc` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `customer_msg` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for payment_transaction
-- ----------------------------
DROP TABLE IF EXISTS `payment_transaction`;
CREATE TABLE `payment_transaction`  (
  `id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `customer_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `order_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `transaction_ref` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `amount` decimal(50, 2) NULL DEFAULT NULL,
  `merchant_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `domain` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `preauth` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `terminal_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `currency` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `payment_customer_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `payment_method` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `effective_date` datetime(0) NULL DEFAULT NULL,
  `quote_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `quote_number` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `policy_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `policy_number` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `renewal` tinyint(1) NOT NULL,
  `client_result` tinyint(1) NULL DEFAULT NULL,
  `client_message` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `status` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `payment_message` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `balance_result` tinyint(1) NULL DEFAULT NULL,
  `balance_message` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for shedlock
-- ----------------------------
DROP TABLE IF EXISTS `shedlock`;
CREATE TABLE `shedlock` (
  `name` varchar(64) NOT NULL,
  `lock_until` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  `locked_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `locked_by` varchar(255) NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for policy_notification_task
-- ----------------------------
DROP TABLE IF EXISTS `policy_notification_task`;
CREATE TABLE `policy_notification_task`  (
  `task_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `subtype` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `destination` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `subject` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `text` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `policy_number` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `status` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `schedule_time` datetime(0) NOT NULL,
  `message_id` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `failure_number` int(11) NOT NULL,
  `category` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `create_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `update_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`task_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for transaction_detail_task
-- ----------------------------
DROP TABLE IF EXISTS `transaction_detail_task`;
CREATE TABLE `transaction_detail_task`  (
  `task_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `schedule_time` datetime(0) NOT NULL,
  `type` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `amount` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `checkout_request_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `payment_status` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `response_code` int(10) NULL DEFAULT NULL,
  `response_desc` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `result_code` int(10) NULL DEFAULT NULL,
  `result_desc` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `create_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `update_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`task_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for customer_policy_cache
-- ----------------------------
DROP TABLE IF EXISTS `customer_policy_cache`;
CREATE TABLE `customer_policy_cache`  (
  `entity_id` bigint(50) NOT NULL,
  `policy` json NULL,
  `check_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`entity_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for cognito_sns
-- ----------------------------
DROP TABLE IF EXISTS `cognito_sns`;
CREATE TABLE `cognito_sns`  (
  `cognito_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `firebase_token` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `endpoint_arn` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`cognito_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for policy
-- ----------------------------
DROP TABLE IF EXISTS `policy`;
CREATE TABLE `policy`  (
  `policy_id` int(11) NOT NULL,
  `policy_number` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `quote_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `policy_holder_id` bigint(30) NULL DEFAULT NULL,
  `product_id` int(11) NULL DEFAULT NULL,
  `start_date` datetime(0) NULL DEFAULT NULL,
  `effective_date` datetime(0) NOT NULL,
  `renewal_date` datetime(0) NULL DEFAULT NULL,
  `status` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `benefit` json NULL,
  `phcf` decimal(20, 2) NULL DEFAULT NULL,
  `itl` decimal(20, 2) NULL DEFAULT NULL,
  `premium` decimal(20, 2) NULL DEFAULT NULL,
  `total_premium` decimal(20, 2) NULL DEFAULT NULL,
  `stamp_duty` decimal(20, 2) NULL DEFAULT NULL,
  `balance` decimal(20, 2) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `create_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `loading` decimal(20, 2) NULL DEFAULT NULL,
  `discount` decimal(20, 2) NULL DEFAULT NULL,
  `loss_ratio` decimal(20, 2) NULL DEFAULT NULL,
  `earned_premium` decimal(20, 2) NULL DEFAULT NULL,
  `claims_paid` decimal(20, 2) NULL DEFAULT NULL,
  `manual_adjustment` decimal(20, 2) NULL DEFAULT NULL,
  `renewal_phcf` decimal(20, 2) NULL DEFAULT NULL,
  `renewal_itl` decimal(20, 2) NULL DEFAULT NULL,
  `renewal_premium` decimal(20, 2) NULL DEFAULT NULL,
  `renewal_total_premium` decimal(20, 2) NULL DEFAULT NULL,
  `renewal_stamp_duty` decimal(20, 2) NULL DEFAULT NULL,
  `renewal_balance` decimal(20, 2) NULL DEFAULT NULL,
  `archived` tinyint(1) NULL DEFAULT 0,
  PRIMARY KEY (`policy_number`, `effective_date`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `id` int(11) NOT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_swedish_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for policy_premium
-- ----------------------------
DROP TABLE IF EXISTS `policy_premium`;
CREATE TABLE `policy_premium`  (
  `id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `policy_id` int(11) NULL DEFAULT 0,
  `policy_number` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `effective_date` datetime(0) NULL DEFAULT NULL,
  `quote_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `customer_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `entity_id` bigint(30) NULL DEFAULT NULL,
  `relationship` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `age` int(10) NULL DEFAULT NULL,
  `benefit_type` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `benefit_limit` decimal(20, 2) NULL DEFAULT NULL,
  `premium` decimal(20, 2) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for policy_complaint
-- ----------------------------
DROP TABLE IF EXISTS `policy_complaint`;
CREATE TABLE `policy_complaint`  (
  `id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `agent_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `customer_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `policy_id` int(11) NULL DEFAULT NULL,
  `policy_number` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `effective_date` datetime(0) NOT NULL,
  `title` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `content` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `create_time` datetime(6) NULL DEFAULT NULL,
  `update_time` datetime(6) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `policy_over_coming`;
CREATE TABLE `policy_over_coming` (
                                      `id` int(11) NOT NULL AUTO_INCREMENT,
                                      `agentName` varchar(255) DEFAULT NULL,
                                      `asagentId` varchar(255) DEFAULT NULL,
                                      `claims` varchar(255) DEFAULT NULL,
                                      `create_time` datetime(6) NOT NULL,
                                      `discount` decimal(19,2) DEFAULT NULL,
                                      `effectiveDate` datetime(6) DEFAULT NULL,
                                      `email` varchar(255) DEFAULT NULL,
                                      `loading` decimal(19,2) DEFAULT NULL,
                                      `mobile` varchar(255) DEFAULT NULL,
                                      `needToUpdate` bit(1) NOT NULL,
                                      `plan` varchar(255) DEFAULT NULL,
                                      `policyAmount` varchar(255) DEFAULT NULL,
                                      `policyNumber` varchar(255) DEFAULT NULL,
                                      `premium` decimal(19,2) DEFAULT NULL,
                                      `principalName` varchar(255) DEFAULT NULL,
                                      `renewalDate` datetime(6) DEFAULT NULL,
                                      `totalPremium` decimal(19,2) DEFAULT NULL,
                                      
                                      `loadingPercentage` decimal(19,2) DEFAULT 0,
                                      `earnedPremium` decimal(19,2) DEFAULT 0,

                                      `update_time` datetime(6) NOT NULL,
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `policy_over_coming_record`;
CREATE TABLE `policy_over_coming_record` (
                                             `id` int(11) NOT NULL AUTO_INCREMENT,
                                             `create_time` datetime(6) NOT NULL,
                                             `current_amount` int(11) DEFAULT '0',
                                             `record` varchar(255) DEFAULT NULL,
                                             `total_amount` int(11) DEFAULT '0',
                                             `update_time` datetime(6) NOT NULL,
                                             PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
SET FOREIGN_KEY_CHECKS = 1;


-- alter table policy_over_coming
--     add loadingPercentage decimal(19,2) DEFAULT 0,
--     add earnedPremium decimal(19,2) DEFAULT 0
--     ;