-- Application management phase-1 incremental migration
-- Scope: inf_application extension + relationship/alert/notice tables

SET @db_name = DATABASE();

SET @ddl = (
    SELECT IF(
        EXISTS (
            SELECT 1 FROM information_schema.COLUMNS
            WHERE TABLE_SCHEMA = @db_name AND TABLE_NAME = 'inf_application' AND COLUMN_NAME = 'construction_source'
        ),
        'SELECT 1',
        "ALTER TABLE inf_application ADD COLUMN construction_source varchar(32) NOT NULL DEFAULT 'SELF_BUILT' COMMENT 'construction source' AFTER classification_level"
    )
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl = (
    SELECT IF(
        EXISTS (
            SELECT 1 FROM information_schema.COLUMNS
            WHERE TABLE_SCHEMA = @db_name AND TABLE_NAME = 'inf_application' AND COLUMN_NAME = 'owner_dept_id'
        ),
        'SELECT 1',
        "ALTER TABLE inf_application ADD COLUMN owner_dept_id bigint(20) DEFAULT NULL COMMENT 'owner department id' AFTER owner_org"
    )
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl = (
    SELECT IF(
        EXISTS (
            SELECT 1 FROM information_schema.COLUMNS
            WHERE TABLE_SCHEMA = @db_name AND TABLE_NAME = 'inf_application' AND COLUMN_NAME = 'ops_owner'
        ),
        'SELECT 1',
        "ALTER TABLE inf_application ADD COLUMN ops_owner varchar(64) DEFAULT NULL COMMENT 'ops owner' AFTER owner_name"
    )
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl = (
    SELECT IF(
        EXISTS (
            SELECT 1 FROM information_schema.COLUMNS
            WHERE TABLE_SCHEMA = @db_name AND TABLE_NAME = 'inf_application' AND COLUMN_NAME = 'linkage_status'
        ),
        'SELECT 1',
        "ALTER TABLE inf_application ADD COLUMN linkage_status varchar(32) NOT NULL DEFAULT 'UNKNOWN' COMMENT 'linkage status' AFTER application_status"
    )
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl = (
    SELECT IF(
        EXISTS (
            SELECT 1 FROM information_schema.COLUMNS
            WHERE TABLE_SCHEMA = @db_name AND TABLE_NAME = 'inf_application' AND COLUMN_NAME = 'last_alert_time'
        ),
        'SELECT 1',
        "ALTER TABLE inf_application ADD COLUMN last_alert_time datetime DEFAULT NULL COMMENT 'last alert time' AFTER linkage_status"
    )
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl = (
    SELECT IF(
        EXISTS (
            SELECT 1 FROM information_schema.COLUMNS
            WHERE TABLE_SCHEMA = @db_name AND TABLE_NAME = 'inf_application' AND COLUMN_NAME = 'deleted_flag'
        ),
        'SELECT 1',
        "ALTER TABLE inf_application ADD COLUMN deleted_flag char(1) NOT NULL DEFAULT '0' COMMENT 'logical delete flag' AFTER relation_summary"
    )
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl = (
    SELECT IF(
        EXISTS (
            SELECT 1 FROM information_schema.STATISTICS
            WHERE TABLE_SCHEMA = @db_name AND TABLE_NAME = 'inf_application' AND INDEX_NAME = 'idx_application_linkage'
        ),
        'SELECT 1',
        'ALTER TABLE inf_application ADD INDEX idx_application_linkage (linkage_status)'
    )
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl = (
    SELECT IF(
        EXISTS (
            SELECT 1 FROM information_schema.STATISTICS
            WHERE TABLE_SCHEMA = @db_name AND TABLE_NAME = 'inf_application' AND INDEX_NAME = 'idx_application_owner_dept'
        ),
        'SELECT 1',
        'ALTER TABLE inf_application ADD INDEX idx_application_owner_dept (owner_dept_id)'
    )
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl = (
    SELECT IF(
        EXISTS (
            SELECT 1 FROM information_schema.STATISTICS
            WHERE TABLE_SCHEMA = @db_name AND TABLE_NAME = 'inf_application' AND INDEX_NAME = 'idx_application_deleted_flag'
        ),
        'SELECT 1',
        "ALTER TABLE inf_application ADD INDEX idx_application_deleted_flag (deleted_flag)"
    )
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

CREATE TABLE IF NOT EXISTS inf_application_project_rel (
  rel_id bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'relation id',
  application_id bigint(20) NOT NULL COMMENT 'application id',
  project_id bigint(20) NOT NULL COMMENT 'project id',
  relation_type varchar(32) NOT NULL DEFAULT 'RELATED' COMMENT 'PRIMARY/RELATED/HISTORICAL',
  active_flag char(1) NOT NULL DEFAULT '1' COMMENT '1 active 0 inactive',
  create_by varchar(64) DEFAULT '' COMMENT 'created by',
  create_time datetime DEFAULT NULL COMMENT 'created time',
  update_by varchar(64) DEFAULT '' COMMENT 'updated by',
  update_time datetime DEFAULT NULL COMMENT 'updated time',
  remark varchar(500) DEFAULT NULL COMMENT 'remark',
  PRIMARY KEY (rel_id),
  UNIQUE KEY uk_app_project_rel (application_id, project_id),
  KEY idx_app_project_application (application_id, active_flag),
  KEY idx_app_project_project (project_id, active_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='application project relation';

CREATE TABLE IF NOT EXISTS inf_application_dependency_rel (
  dependency_id bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'dependency id',
  application_id bigint(20) NOT NULL COMMENT 'application id',
  dependency_type varchar(32) NOT NULL COMMENT 'RESOURCE/API/DATABASE/NETWORK/PROJECT/APPLICATION/OTHER',
  target_id bigint(20) DEFAULT NULL COMMENT 'target id',
  target_code varchar(64) DEFAULT NULL COMMENT 'target code',
  target_name varchar(128) NOT NULL COMMENT 'target name',
  target_source varchar(32) NOT NULL DEFAULT 'INTERNAL' COMMENT 'INTERNAL/EXTERNAL/MANUAL',
  target_key varchar(128) DEFAULT NULL COMMENT 'manual target key',
  dependency_direction varchar(32) NOT NULL DEFAULT 'UPSTREAM' COMMENT 'UPSTREAM/DOWNSTREAM/BIDIRECTIONAL',
  dependency_role varchar(32) NOT NULL DEFAULT 'RUNTIME' COMMENT 'RUNTIME/DATA/ACCESS/NETWORK',
  importance_level varchar(16) NOT NULL DEFAULT 'MEDIUM' COMMENT 'LOW/MEDIUM/HIGH/CRITICAL',
  status_link_enabled char(1) NOT NULL DEFAULT '1' COMMENT 'link status switch',
  alert_link_enabled char(1) NOT NULL DEFAULT '1' COMMENT 'link alert switch',
  dependency_status varchar(32) NOT NULL DEFAULT 'UNKNOWN' COMMENT 'manual status',
  last_sync_time datetime DEFAULT NULL COMMENT 'last sync time',
  target_snapshot_json text COMMENT 'snapshot json',
  create_by varchar(64) DEFAULT '' COMMENT 'created by',
  create_time datetime DEFAULT NULL COMMENT 'created time',
  update_by varchar(64) DEFAULT '' COMMENT 'updated by',
  update_time datetime DEFAULT NULL COMMENT 'updated time',
  remark varchar(500) DEFAULT NULL COMMENT 'remark',
  PRIMARY KEY (dependency_id),
  KEY idx_app_dependency_application (application_id),
  KEY idx_app_dependency_type_target (dependency_type, target_id),
  KEY idx_app_dependency_status (dependency_status),
  KEY idx_app_dependency_linkage (status_link_enabled, alert_link_enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='application dependency relation';

SET @ddl = (
    SELECT IF(
        EXISTS (
            SELECT 1 FROM information_schema.STATISTICS
            WHERE TABLE_SCHEMA = @db_name AND TABLE_NAME = 'inf_application_dependency_rel' AND INDEX_NAME = 'uk_app_dependency_unique'
        ),
        'SELECT 1',
        'ALTER TABLE inf_application_dependency_rel ADD UNIQUE KEY uk_app_dependency_unique (application_id, dependency_type, target_id, target_key)'
    )
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

CREATE TABLE IF NOT EXISTS inf_application_alert_event (
  alert_id bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'alert id',
  application_id bigint(20) NOT NULL COMMENT 'application id',
  source_type varchar(32) NOT NULL DEFAULT 'SYSTEM' COMMENT 'source type',
  source_object_type varchar(32) NOT NULL DEFAULT 'APPLICATION' COMMENT 'source object type',
  source_object_id bigint(20) DEFAULT NULL COMMENT 'source object id',
  event_code varchar(64) DEFAULT NULL COMMENT 'event code',
  event_title varchar(128) NOT NULL COMMENT 'event title',
  event_level varchar(16) NOT NULL DEFAULT 'WARNING' COMMENT 'INFO/WARNING/CRITICAL',
  event_status varchar(32) NOT NULL DEFAULT 'OPEN' COMMENT 'OPEN/ACKED/RESOLVED/IGNORED',
  impact_summary varchar(500) DEFAULT NULL COMMENT 'impact summary',
  event_time datetime NOT NULL COMMENT 'event time',
  ack_time datetime DEFAULT NULL COMMENT 'ack time',
  resolved_time datetime DEFAULT NULL COMMENT 'resolved time',
  handler_name varchar(64) DEFAULT NULL COMMENT 'handler',
  payload_json text COMMENT 'payload',
  create_by varchar(64) DEFAULT '' COMMENT 'created by',
  create_time datetime DEFAULT NULL COMMENT 'created time',
  update_by varchar(64) DEFAULT '' COMMENT 'updated by',
  update_time datetime DEFAULT NULL COMMENT 'updated time',
  remark varchar(500) DEFAULT NULL COMMENT 'remark',
  PRIMARY KEY (alert_id),
  KEY idx_app_alert_application (application_id, event_status),
  KEY idx_app_alert_time (event_time),
  KEY idx_app_alert_source (source_type, source_object_id),
  KEY idx_app_alert_level (event_level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='application alert event';

CREATE TABLE IF NOT EXISTS inf_application_notice_log (
  notice_id bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'notice id',
  alert_id bigint(20) NOT NULL COMMENT 'alert id',
  application_id bigint(20) NOT NULL COMMENT 'application id',
  receiver_name varchar(64) NOT NULL COMMENT 'receiver name',
  receiver_role varchar(32) NOT NULL COMMENT 'receiver role',
  channel_type varchar(32) NOT NULL DEFAULT 'INTERNAL' COMMENT 'channel',
  send_status varchar(32) NOT NULL DEFAULT 'SENT' COMMENT 'PENDING/SENT/FAILED',
  biz_status varchar(32) NOT NULL DEFAULT 'UNREAD' COMMENT 'UNREAD/READ/PROCESSING/DONE',
  sent_time datetime DEFAULT NULL COMMENT 'sent time',
  read_time datetime DEFAULT NULL COMMENT 'read time',
  processed_time datetime DEFAULT NULL COMMENT 'processed time',
  content_summary varchar(500) DEFAULT NULL COMMENT 'content summary',
  create_by varchar(64) DEFAULT '' COMMENT 'created by',
  create_time datetime DEFAULT NULL COMMENT 'created time',
  update_by varchar(64) DEFAULT '' COMMENT 'updated by',
  update_time datetime DEFAULT NULL COMMENT 'updated time',
  remark varchar(500) DEFAULT NULL COMMENT 'remark',
  PRIMARY KEY (notice_id),
  KEY idx_app_notice_alert (alert_id),
  KEY idx_app_notice_application (application_id, biz_status),
  KEY idx_app_notice_receiver (receiver_name, biz_status),
  KEY idx_app_notice_sent_time (sent_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='application notice log';

-- Backfill primary project relation from existing application.project_id
INSERT INTO inf_application_project_rel (application_id, project_id, relation_type, active_flag, create_by, create_time, remark)
SELECT a.application_id,
       a.project_id,
       'PRIMARY',
       '1',
       'system',
       NOW(),
       'backfill from inf_application.project_id'
FROM inf_application a
WHERE a.project_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM inf_application_project_rel r
    WHERE r.application_id = a.application_id
      AND r.project_id = a.project_id
  );
