-- ========================================================
-- 智慧养老服务平台 — 数据库初始化脚本
-- MySQL 8.0, utf8mb4
-- ========================================================

CREATE DATABASE IF NOT EXISTS elderly_care
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE elderly_care;

-- ---------- 用户权限 ----------

CREATE TABLE sys_user (
  id           BIGINT        NOT NULL AUTO_INCREMENT,
  name         VARCHAR(50)   NOT NULL COMMENT '姓名',
  phone        VARCHAR(20)   NOT NULL COMMENT '手机号',
  password     VARCHAR(100)  NOT NULL COMMENT 'BCrypt 密码',
  avatar       MEDIUMTEXT    DEFAULT NULL COMMENT '头像(Base64或URL)',
  status       TINYINT       NOT NULL DEFAULT 1 COMMENT '1正常 0禁用',
  last_login_time DATETIME   DEFAULT NULL,
  create_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted      TINYINT       NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_phone (phone)
) ENGINE=InnoDB COMMENT='用户';

CREATE TABLE sys_role (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  name        VARCHAR(50)  NOT NULL COMMENT '角色名',
  code        VARCHAR(30)  NOT NULL COMMENT 'USER/DOCTOR/VOLUNTEER/ADMIN',
  description VARCHAR(255) DEFAULT NULL,
  create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted     TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_code (code)
) ENGINE=InnoDB COMMENT='角色';

CREATE TABLE sys_permission (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  name        VARCHAR(50)  NOT NULL,
  code        VARCHAR(100) NOT NULL COMMENT '如 news:publish',
  type        VARCHAR(10)  NOT NULL DEFAULT 'BUTTON' COMMENT 'MENU/BUTTON',
  parent_id   BIGINT       DEFAULT 0,
  sort        INT          DEFAULT 0,
  create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_code (code)
) ENGINE=InnoDB COMMENT='权限点';

CREATE TABLE sys_user_role (
  id       BIGINT NOT NULL AUTO_INCREMENT,
  user_id  BIGINT NOT NULL,
  role_id  BIGINT NOT NULL,
  PRIMARY KEY (id),
  KEY idx_user (user_id),
  KEY idx_role (role_id)
) ENGINE=InnoDB COMMENT='用户-角色';

CREATE TABLE sys_role_permission (
  id            BIGINT NOT NULL AUTO_INCREMENT,
  role_id       BIGINT NOT NULL,
  permission_id BIGINT NOT NULL,
  PRIMARY KEY (id),
  KEY idx_role (role_id)
) ENGINE=InnoDB COMMENT='角色-权限';

CREATE TABLE sys_login_log (
  id        BIGINT       NOT NULL AUTO_INCREMENT,
  user_id   BIGINT       DEFAULT NULL,
  phone     VARCHAR(20)  DEFAULT NULL,
  ip        VARCHAR(50)  DEFAULT NULL,
  status    TINYINT      NOT NULL COMMENT '1成功 0失败',
  message   VARCHAR(255) DEFAULT NULL,
  login_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB COMMENT='登录日志';

-- ---------- 健康守护 ----------

CREATE TABLE health_record (
  id                  BIGINT       NOT NULL AUTO_INCREMENT,
  user_id             BIGINT       NOT NULL,
  systolic_pressure   INT          DEFAULT NULL COMMENT '收缩压',
  diastolic_pressure  INT          DEFAULT NULL COMMENT '舒张压',
  blood_sugar         DECIMAL(5,2) DEFAULT NULL COMMENT '血糖 mmol/L',
  sleep_hours         DECIMAL(4,1) DEFAULT NULL COMMENT '睡眠小时',
  heart_rate          INT          DEFAULT NULL COMMENT '心率',
  feeling             VARCHAR(50)  DEFAULT NULL COMMENT '今日感受',
  note                VARCHAR(255) DEFAULT NULL,
  checkin_time        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  create_time         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted             TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_user (user_id)
) ENGINE=InnoDB COMMENT='健康打卡';

CREATE TABLE health_tip (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  title       VARCHAR(100) NOT NULL,
  content     TEXT,
  category    VARCHAR(30)  DEFAULT '养生',
  author_id   BIGINT       DEFAULT NULL,
  create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted     TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (id)
) ENGINE=InnoDB COMMENT='健康小知识';

-- ---------- 反诈 ----------

CREATE TABLE fraud_case (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  title       VARCHAR(150) NOT NULL,
  tag         VARCHAR(30)  DEFAULT '高发',
  method      TEXT         COMMENT '作案手法',
  example     TEXT         COMMENT '典型案例',
  tip         TEXT         COMMENT '防骗提示',
  create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted     TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (id)
) ENGINE=InnoDB COMMENT='诈骗案例';

CREATE TABLE fraud_report (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  reporter_id BIGINT       NOT NULL COMMENT '举报人userId',
  name        VARCHAR(50)  NOT NULL,
  phone       VARCHAR(20)  NOT NULL,
  type        VARCHAR(50)  NOT NULL COMMENT '诈骗类型',
  platform    VARCHAR(100) DEFAULT NULL COMMENT '涉事平台',
  amount      DECIMAL(12,2) DEFAULT NULL COMMENT '金额',
  detail      TEXT         COMMENT '详细描述',
  status      VARCHAR(20)  NOT NULL DEFAULT 'pending' COMMENT 'pending/processing/closed',
  handler_id  BIGINT       DEFAULT NULL,
  result      VARCHAR(255) DEFAULT NULL COMMENT '处理结果',
  create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted     TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_reporter (reporter_id)
) ENGINE=InnoDB COMMENT='举报记录';

CREATE TABLE fraud_test_question (
  id        BIGINT       NOT NULL AUTO_INCREMENT,
  question  VARCHAR(500) NOT NULL,
  option_a  VARCHAR(200) NOT NULL,
  option_b  VARCHAR(200) NOT NULL,
  option_c  VARCHAR(200) NOT NULL,
  option_d  VARCHAR(200) NOT NULL,
  answer    VARCHAR(10)  NOT NULL COMMENT 'A/B/C/D',
  analysis  VARCHAR(500) DEFAULT NULL,
  category  VARCHAR(50)  DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB COMMENT='防骗测试题';

CREATE TABLE fraud_test_record (
  id         BIGINT       NOT NULL AUTO_INCREMENT,
  user_id    BIGINT       NOT NULL,
  score      INT          NOT NULL,
  answers    VARCHAR(2000) DEFAULT NULL COMMENT 'JSON',
  create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_user (user_id)
) ENGINE=InnoDB COMMENT='防骗测试记录';

-- ---------- 课程 / 预约 ----------

CREATE TABLE course (
  id              BIGINT       NOT NULL AUTO_INCREMENT,
  name            VARCHAR(150) NOT NULL,
  description     TEXT,
  type            VARCHAR(20)  NOT NULL COMMENT 'ONLINE/OFFLINE',
  tag             VARCHAR(30)  DEFAULT '热门',
  cover_url       VARCHAR(255) DEFAULT NULL,
  teacher_id      BIGINT       DEFAULT NULL,
  start_time      DATETIME     DEFAULT NULL,
  end_time        DATETIME     DEFAULT NULL,
  location        VARCHAR(100) DEFAULT NULL COMMENT '线下地点',
  max_students    INT          DEFAULT 30,
  current_students INT         DEFAULT 0,
  status          VARCHAR(20)  NOT NULL DEFAULT 'approved' COMMENT 'approved待开课/pending待审核/rejected已驳回',
  create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted         TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (id)
) ENGINE=InnoDB COMMENT='课程';

CREATE TABLE my_course (
  id          BIGINT   NOT NULL AUTO_INCREMENT,
  user_id     BIGINT   NOT NULL,
  course_id   BIGINT   NOT NULL,
  progress    INT      NOT NULL DEFAULT 0 COMMENT '0-100',
  last_time   DATETIME DEFAULT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_course (user_id, course_id)
) ENGINE=InnoDB COMMENT='我的课程';

CREATE TABLE booking (
  id            BIGINT       NOT NULL AUTO_INCREMENT,
  course_id     BIGINT       NOT NULL,
  user_id       BIGINT       NOT NULL,
  user_name     VARCHAR(50)  NOT NULL,
  user_phone    VARCHAR(20)  NOT NULL,
  book_date     DATE         DEFAULT NULL,
  time_slot     VARCHAR(50)  DEFAULT NULL,
  note          VARCHAR(255) DEFAULT NULL,
  status        VARCHAR(20)  NOT NULL DEFAULT 'pending' COMMENT 'pending/confirmed/rejected/cancelled',
  signin_status TINYINT      NOT NULL DEFAULT 0 COMMENT '0未签到 1已签到',
  create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted       TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_user (user_id),
  KEY idx_course (course_id)
) ENGINE=InnoDB COMMENT='预约';

-- ---------- 健康咨询（问医生） ----------

CREATE TABLE consultation (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  user_id     BIGINT       NOT NULL COMMENT '提问用户',
  user_name   VARCHAR(50)  DEFAULT NULL,
  question    VARCHAR(500) NOT NULL COMMENT '问题内容',
  answer      TEXT         DEFAULT NULL COMMENT '医生回复',
  doctor_id   BIGINT       DEFAULT NULL,
  doctor_name VARCHAR(50)  DEFAULT NULL,
  status      VARCHAR(20)  NOT NULL DEFAULT 'pending' COMMENT 'pending待回复/answered已回复',
  create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted     TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_user (user_id),
  KEY idx_status (status)
) ENGINE=InnoDB COMMENT='健康咨询';

-- ---------- 资讯 ----------

CREATE TABLE news_category (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  name        VARCHAR(30)  NOT NULL,
  code        VARCHAR(30)  NOT NULL,
  sort        INT          DEFAULT 0,
  create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_code (code)
) ENGINE=InnoDB COMMENT='资讯分类';

CREATE TABLE news (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  title       VARCHAR(200) NOT NULL,
  excerpt     VARCHAR(500) DEFAULT NULL,
  content     LONGTEXT,
  category    VARCHAR(30)  DEFAULT NULL,
  cover_url   VARCHAR(255) DEFAULT NULL,
  author_id   BIGINT       DEFAULT NULL,
  view_count  INT          NOT NULL DEFAULT 0,
  is_hot      TINYINT      NOT NULL DEFAULT 0,
  publish_time DATETIME    DEFAULT NULL,
  status      TINYINT      NOT NULL DEFAULT 1 COMMENT '1已发布 0待审核 2已驳回',
  create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted     TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_category (category)
) ENGINE=InnoDB COMMENT='资讯';

CREATE TABLE news_favorite (
  id          BIGINT   NOT NULL AUTO_INCREMENT,
  user_id     BIGINT   NOT NULL,
  news_id     BIGINT   NOT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_news (user_id, news_id)
) ENGINE=InnoDB COMMENT='资讯收藏';


-- ========================================================
-- 初始化数据
-- ========================================================

-- 4 个角色
INSERT INTO sys_role (name, code, description) VALUES
  ('普通用户', 'USER', '平台主要使用者'),
  ('健康管理员', 'DOCTOR', '健康数据管理与健康建议发布'),
  ('志愿者', 'VOLUNTEER', '线下活动组织者、课程讲师'),
  ('系统管理员', 'ADMIN', '平台运营管理');

-- 初始化 admin 用户（密码 BCrypt 加密 "admin123"，htpasswd 生成）
-- $2a$10$4l3S8lW6g7C5Tan1FhAzLeYhkLmKNIAcXyNgFV.slMQkq/HdFeF4y
INSERT INTO sys_user (name, phone, password, avatar, status) VALUES
  ('系统管理员', '13800000000', '$2a$10$4l3S8lW6g7C5Tan1FhAzLeYhkLmKNIAcXyNgFV.slMQkq/HdFeF4y', NULL, 1),
  ('张医生',     '13900000000', '$2a$10$4l3S8lW6g7C5Tan1FhAzLeYhkLmKNIAcXyNgFV.slMQkq/HdFeF4y', NULL, 1),
  ('王老师',     '13700000000', '$2a$10$4l3S8lW6g7C5Tan1FhAzLeYhkLmKNIAcXyNgFV.slMQkq/HdFeF4y', NULL, 1);

-- admin 绑定 ADMIN 角色，医生绑定 DOCTOR，老师绑定 VOLUNTEER
INSERT INTO sys_user_role (user_id, role_id) VALUES
  (1, 4),
  (2, 2),
  (3, 3);

-- 示例普通用户（密码 admin123）
-- $2a$10$4l3S8lW6g7C5Tan1FhAzLeYhkLmKNIAcXyNgFV.slMQkq/HdFeF4y
INSERT INTO sys_user (name, phone, password, status) VALUES
  ('李奶奶', '13611112222', '$2a$10$4l3S8lW6g7C5Tan1FhAzLeYhkLmKNIAcXyNgFV.slMQkq/HdFeF4y', 1),
  ('赵爷爷', '13622223333', '$2a$10$4l3S8lW6g7C5Tan1FhAzLeYhkLmKNIAcXyNgFV.slMQkq/HdFeF4y', 1);
INSERT INTO sys_user_role (user_id, role_id) VALUES
  (4, 1),
  (5, 1);

-- 资讯分类
INSERT INTO news_category (name, code, sort) VALUES
  ('健康养生', 'HEALTH', 1),
  ('反诈警示', 'FRAUD', 2),
  ('活动通知', 'EVENT', 3),
  ('政策法规', 'POLICY', 4),
  ('生活服务', 'LIFE', 5);

-- 示例资讯
INSERT INTO news (title, excerpt, content, category, author_id, view_count, is_hot, publish_time, status) VALUES
  ('老年人如何预防高血压', '高血压是老年人常见慢性病，合理饮食和规律运动是关键。', '高血压是老年人常见的慢性病之一，建议低盐低脂饮食，每天保证适量有氧运动，定期监测血压并遵医嘱服药。', '健康养生', 2, 328, 1, NOW(), 1),
  ('警惕"冒充公检法"诈骗新套路', '近期多发，骗子冒充警察要求转账"安全账户"', '骗子冒充公检法人员打电话，说你的身份证涉嫌洗钱，要求把钱转到"安全账户"。公安机关绝不会电话办案、要求转账。', '反诈警示', 4, 512, 1, NOW(), 1),
  ('社区重阳节活动预告', '本周五上午9点，社区老年活动中心举办重阳节联欢会。', '欢迎各位老人前来参加，现场有文艺表演、健康体检和节日礼品发放。', '活动通知', 4, 120, 0, NOW(), 1),
  ('国家基本养老金上调 3.8%', '2026 年退休人员基本养老金再次上调。', '根据人力资源和社会保障部通知，2026 年退休人员基本养老金总体调整水平为上年退休人员月人均基本养老金的 3.8%。', '政策法规', 4, 876, 1, NOW(), 1),
  ('手机防诈骗设置小技巧', '开启这些功能，让骗子无从下手', '1. 开启手机陌生号码拦截；2. 关闭陌生人短信预览；3. 不要点击陌生链接；4. 安装国家反诈中心 APP。', '生活服务', 4, 234, 0, NOW(), 1);

-- 健康小知识
INSERT INTO health_tip (title, content, category, author_id) VALUES
  ('每天喝多少水合适？', '老年人每天饮水量建议 1500-2000ml，分次少量饮用，避免一次大量饮水。', '养生', 2),
  ('饭后多久可以运动', '建议饭后 1 小时再进行轻度运动，如散步、太极等，避免剧烈运动。', '运动', 2),
  ('老年人睡眠建议', '保证每天 7-8 小时睡眠，尽量早睡早起，午休控制在 30 分钟以内。', '睡眠', 2);

-- 诈骗案例
INSERT INTO fraud_case (title, tag, method, example, tip) VALUES
  ('冒充公检法诈骗', '高发', '骗子冒充警察/法官打电话，声称受害者身份证涉嫌洗钱犯罪，要求将资金转入"安全账户"。', '张大爷接到自称"市公安局"的电话，对方说他的身份证被用来诈骗，要冻结他的账户。张大爷在恐慌中分 3 次向骗子转账共计 12 万元。', '公安机关不会通过电话办案，绝不会要求你转账。接到此类电话直接挂断并拨打 110 或 96110。'),
  ('保健品诈骗', '警惕', '骗子以"免费体检""专家坐诊"为由，夸大老年人病情，推销价格虚高的"特效药"。', '王大妈在菜市场被拉去"免费体检"，"专家"说她"三高严重"，让买 8000 元的"特效药"，结果吃了毫无效果。', '不要相信街头"免费体检"。药品须有国药准字，保健品不能治病，身体不舒服请去正规医院。'),
  ('冒充客服退款', '新型', '骗子冒充购物平台客服，说你买的东西有问题要退款，诱导你提供银行卡号和验证码。', '李奶奶接到电话说在"某东"买的保健品有问题要退款，按骗子说的在手机上操作，结果银行卡里 2 万多元被转走。', '购物平台客服不会通过私人电话联系你退款。有问题请在 APP 里联系官方客服。');

-- 防骗测试题
INSERT INTO fraud_test_question (question, option_a, option_b, option_c, option_d, answer, analysis, category) VALUES
  ('接到自称"公安局"电话说你涉嫌洗钱，要你转账到安全账户，你应该？', 'A. 立即转账保命', 'B. 挂断并拨打 96110 核实', 'C. 半信半疑先转一点', 'D. 把电话给家人', 'B', '公安机关绝不可能通过电话办案，更不会要求转账。96110 是反诈专线。', '冒充公检法'),
  ('有人打电话说你买的保健品有问题要退款，让你提供银行卡号和验证码，你应该？', 'A. 配合提供，尽快退款', 'B. 挂断，在购物 APP 里找官方客服确认', 'C. 只提供卡号不提供验证码', 'D. 让对方加微信退', 'B', '正规平台不会通过电话或私人渠道要求验证码。', '冒充客服'),
  ('社区里有"专家"免费义诊，说你有多种疾病，要买他的"特效药"，你应该？', 'A. 专家说的对，赶紧买', 'B. 去正规医院挂号检查', 'C. 少买一点试试', 'D. 让子女代买', 'B', '保健品不能替代药品，治病请去正规医院。', '保健品诈骗'),
  ('微信群里有人发红包让你点链接领取，你应该？', 'A. 立即点击领取', 'B. 不点击，这是钓鱼链接', 'C. 转发给家人一起领', 'D. 先点开看看是什么', 'B', '红包链接大多是钓鱼网站，窃取个人信息。', '网络诈骗'),
  ('你的孙子通过微信说"在学校出了事需要打钱"，你应该？', 'A. 立即打钱给孙子', 'B. 打电话给孙子或老师核实', 'C. 只打一半', 'D. 让他发语音确认', 'B', '孙子的微信可能被盗。任何涉及转账的事务必电话核实。', '冒充亲友');

-- 示例课程
INSERT INTO course (name, description, type, tag, teacher_id, start_time, end_time, location, max_students) VALUES
  ('智能手机基础操作', '从零学习智能手机基本功能，包括打电话、发短信、微信视频。', 'ONLINE', '热门', 3, NULL, NULL, NULL, 100),
  ('移动支付安全指南', '教您安全使用微信支付和支付宝，防止上当受骗。', 'ONLINE', '推荐', 3, NULL, NULL, NULL, 100),
  ('太极拳入门', '线下太极拳培训，强身健体、修身养性。', 'OFFLINE', '新上', 3, DATE_ADD(NOW(), INTERVAL 3 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 3 DAY), INTERVAL 2 HOUR), '社区老年活动中心', 30),
  ('高血压防治知识', '由健康医生讲解高血压的饮食、运动和药物管理。', 'ONLINE', '推荐', 2, NULL, NULL, NULL, 100),
  ('老年人安全出行指南', '教您安全乘坐公交地铁、用手机打车，以及出行防走失、防诈骗的实用技巧。', 'ONLINE', '热门', 3, NULL, NULL, NULL, 100);

-- 健康打卡示例数据
INSERT INTO health_record (user_id, systolic_pressure, diastolic_pressure, blood_sugar, sleep_hours, heart_rate, feeling, checkin_time) VALUES
  (4, 128, 82, 5.6, 7.5, 72, '良好', DATE_SUB(NOW(), INTERVAL 1 DAY)),
  (4, 132, 85, 5.8, 7.0, 75, '一般', DATE_SUB(NOW(), INTERVAL 2 DAY)),
  (4, 125, 80, 5.4, 8.0, 70, '良好', DATE_SUB(NOW(), INTERVAL 3 DAY)),
  (5, 138, 88, 6.2, 6.5, 78, '一般', DATE_SUB(NOW(), INTERVAL 1 DAY)),
  (5, 142, 90, 6.5, 6.0, 80, '较差', DATE_SUB(NOW(), INTERVAL 2 DAY));
