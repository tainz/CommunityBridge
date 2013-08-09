-- phpMyAdmin SQL Dump
-- version 3.5.8
-- http://www.phpmyadmin.net
--
-- Vært: db14.meebox.net
-- Genereringstid: 08. 08 2013 kl. 14:54:24
-- Serverversion: 5.1.66-0+squeeze1-log
-- PHP-version: 5.3.17

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `mcvuzedk_forum`
--

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_addon`
--

CREATE TABLE IF NOT EXISTS `xf_addon` (
  `addon_id` varbinary(25) NOT NULL,
  `title` varchar(75) NOT NULL,
  `version_string` varchar(30) NOT NULL DEFAULT '',
  `version_id` int(10) unsigned NOT NULL DEFAULT '0',
  `url` varchar(100) NOT NULL,
  `install_callback_class` varchar(75) NOT NULL DEFAULT '',
  `install_callback_method` varchar(75) NOT NULL DEFAULT '',
  `uninstall_callback_class` varchar(75) NOT NULL DEFAULT '',
  `uninstall_callback_method` varchar(75) NOT NULL DEFAULT '',
  `active` tinyint(3) unsigned NOT NULL,
  PRIMARY KEY (`addon_id`),
  KEY `title` (`title`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_admin`
--

CREATE TABLE IF NOT EXISTS `xf_admin` (
  `user_id` int(10) unsigned NOT NULL,
  `extra_user_group_ids` varbinary(255) NOT NULL,
  `last_login` int(10) unsigned NOT NULL DEFAULT '0',
  `permission_cache` mediumblob,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_admin_log`
--

CREATE TABLE IF NOT EXISTS `xf_admin_log` (
  `admin_log_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL,
  `ip_address` int(10) unsigned NOT NULL DEFAULT '0',
  `request_date` int(10) unsigned NOT NULL,
  `request_url` text NOT NULL,
  `request_data` mediumblob NOT NULL,
  PRIMARY KEY (`admin_log_id`),
  KEY `request_date` (`request_date`),
  KEY `user_id_request_date` (`user_id`,`request_date`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=379 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_admin_navigation`
--

CREATE TABLE IF NOT EXISTS `xf_admin_navigation` (
  `navigation_id` varbinary(25) NOT NULL,
  `parent_navigation_id` varbinary(25) NOT NULL,
  `display_order` int(10) unsigned NOT NULL DEFAULT '0',
  `link` varchar(50) NOT NULL DEFAULT '',
  `admin_permission_id` varbinary(25) NOT NULL DEFAULT '',
  `debug_only` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `hide_no_children` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `addon_id` varbinary(25) NOT NULL DEFAULT '',
  PRIMARY KEY (`navigation_id`),
  KEY `parent_navigation_id_display_order` (`parent_navigation_id`,`display_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_admin_permission`
--

CREATE TABLE IF NOT EXISTS `xf_admin_permission` (
  `admin_permission_id` varbinary(25) NOT NULL,
  `display_order` int(10) unsigned NOT NULL DEFAULT '0',
  `addon_id` varbinary(25) NOT NULL DEFAULT '',
  PRIMARY KEY (`admin_permission_id`),
  KEY `display_order` (`display_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_admin_permission_entry`
--

CREATE TABLE IF NOT EXISTS `xf_admin_permission_entry` (
  `user_id` int(11) NOT NULL,
  `admin_permission_id` varbinary(25) NOT NULL,
  PRIMARY KEY (`user_id`,`admin_permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_admin_search_type`
--

CREATE TABLE IF NOT EXISTS `xf_admin_search_type` (
  `search_type` varbinary(25) NOT NULL,
  `handler_class` varchar(50) NOT NULL,
  `display_order` int(10) unsigned NOT NULL,
  PRIMARY KEY (`search_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_admin_template`
--

CREATE TABLE IF NOT EXISTS `xf_admin_template` (
  `template_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `title` varbinary(50) NOT NULL,
  `template` mediumtext NOT NULL COMMENT 'User-editable HTML and template syntax',
  `template_parsed` mediumblob NOT NULL,
  `addon_id` varbinary(25) NOT NULL DEFAULT '',
  PRIMARY KEY (`template_id`),
  UNIQUE KEY `title` (`title`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=401 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_admin_template_compiled`
--

CREATE TABLE IF NOT EXISTS `xf_admin_template_compiled` (
  `language_id` int(10) unsigned NOT NULL,
  `title` varbinary(50) NOT NULL,
  `template_compiled` mediumblob NOT NULL COMMENT 'Executable PHP code built by template compiler',
  PRIMARY KEY (`language_id`,`title`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_admin_template_include`
--

CREATE TABLE IF NOT EXISTS `xf_admin_template_include` (
  `source_id` int(10) unsigned NOT NULL,
  `target_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`source_id`,`target_id`),
  KEY `target` (`target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_admin_template_modification`
--

CREATE TABLE IF NOT EXISTS `xf_admin_template_modification` (
  `modification_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `addon_id` varbinary(25) NOT NULL,
  `template` varbinary(50) NOT NULL,
  `modification_key` varbinary(50) NOT NULL,
  `description` varchar(255) NOT NULL,
  `execution_order` int(10) unsigned NOT NULL,
  `enabled` tinyint(3) unsigned NOT NULL,
  `action` varchar(25) NOT NULL,
  `find` text NOT NULL,
  `replace` text NOT NULL,
  PRIMARY KEY (`modification_id`),
  UNIQUE KEY `modification_key` (`modification_key`),
  KEY `addon_id` (`addon_id`),
  KEY `template_order` (`template`,`execution_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_admin_template_modification_log`
--

CREATE TABLE IF NOT EXISTS `xf_admin_template_modification_log` (
  `template_id` int(10) unsigned NOT NULL,
  `modification_id` int(10) unsigned NOT NULL,
  `status` varchar(25) NOT NULL,
  `apply_count` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`template_id`,`modification_id`),
  KEY `modification_id` (`modification_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_admin_template_phrase`
--

CREATE TABLE IF NOT EXISTS `xf_admin_template_phrase` (
  `template_id` int(10) unsigned NOT NULL,
  `phrase_title` varbinary(75) NOT NULL,
  PRIMARY KEY (`template_id`,`phrase_title`),
  KEY `phrase_title` (`phrase_title`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_attachment`
--

CREATE TABLE IF NOT EXISTS `xf_attachment` (
  `attachment_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `data_id` int(10) unsigned NOT NULL,
  `content_type` varbinary(25) NOT NULL,
  `content_id` int(10) unsigned NOT NULL,
  `attach_date` int(10) unsigned NOT NULL,
  `temp_hash` varchar(32) NOT NULL DEFAULT '',
  `unassociated` tinyint(3) unsigned NOT NULL,
  `view_count` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`attachment_id`),
  KEY `content_type_id_date` (`content_type`,`content_id`,`attach_date`),
  KEY `temp_hash_attach_date` (`temp_hash`,`attach_date`),
  KEY `unassociated_attach_date` (`unassociated`,`attach_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_attachment_data`
--

CREATE TABLE IF NOT EXISTS `xf_attachment_data` (
  `data_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL,
  `upload_date` int(10) unsigned NOT NULL,
  `filename` varchar(100) NOT NULL,
  `file_size` int(10) unsigned NOT NULL,
  `file_hash` varchar(32) NOT NULL,
  `width` int(10) unsigned NOT NULL DEFAULT '0',
  `height` int(10) unsigned NOT NULL DEFAULT '0',
  `thumbnail_width` int(10) unsigned NOT NULL DEFAULT '0',
  `thumbnail_height` int(10) unsigned NOT NULL DEFAULT '0',
  `attach_count` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`data_id`),
  KEY `user_id_upload_date` (`user_id`,`upload_date`),
  KEY `attach_count` (`attach_count`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_attachment_view`
--

CREATE TABLE IF NOT EXISTS `xf_attachment_view` (
  `attachment_id` int(10) unsigned NOT NULL,
  KEY `attachment_id` (`attachment_id`)
) ENGINE=MEMORY DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_ban_email`
--

CREATE TABLE IF NOT EXISTS `xf_ban_email` (
  `banned_email` varchar(120) NOT NULL,
  PRIMARY KEY (`banned_email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_bb_code_media_site`
--

CREATE TABLE IF NOT EXISTS `xf_bb_code_media_site` (
  `media_site_id` varbinary(25) NOT NULL,
  `site_title` varchar(50) NOT NULL,
  `site_url` varchar(100) NOT NULL DEFAULT '',
  `match_urls` text NOT NULL,
  `match_is_regex` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT 'If 1, match_urls will be treated as regular expressions rather than simple URL matches.',
  `match_callback_class` varchar(75) NOT NULL DEFAULT '',
  `match_callback_method` varchar(50) NOT NULL DEFAULT '',
  `embed_html` text NOT NULL,
  `embed_html_callback_class` varchar(75) NOT NULL DEFAULT '',
  `embed_html_callback_method` varchar(50) NOT NULL DEFAULT '',
  `supported` tinyint(3) unsigned NOT NULL DEFAULT '1' COMMENT 'If 0, this media type will not be listed as available, but will still be usable.',
  `addon_id` varbinary(25) NOT NULL DEFAULT '',
  PRIMARY KEY (`media_site_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_bb_code_parse_cache`
--

CREATE TABLE IF NOT EXISTS `xf_bb_code_parse_cache` (
  `bb_code_parse_cache_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `content_type` varbinary(25) NOT NULL,
  `content_id` int(10) unsigned NOT NULL,
  `parse_tree` mediumblob NOT NULL,
  `cache_version` int(10) unsigned NOT NULL,
  `cache_date` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`bb_code_parse_cache_id`),
  UNIQUE KEY `content_type_id` (`content_type`,`content_id`),
  KEY `cache_version` (`cache_version`),
  KEY `cache_date` (`cache_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_captcha_log`
--

CREATE TABLE IF NOT EXISTS `xf_captcha_log` (
  `hash` varbinary(40) NOT NULL,
  `captcha_type` varchar(250) NOT NULL,
  `captcha_data` varchar(250) NOT NULL,
  `captcha_date` int(10) unsigned NOT NULL,
  PRIMARY KEY (`hash`),
  KEY `captcha_date` (`captcha_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_captcha_question`
--

CREATE TABLE IF NOT EXISTS `xf_captcha_question` (
  `captcha_question_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `question` varchar(250) NOT NULL,
  `answers` blob NOT NULL COMMENT 'Serialized array of possible correct answers.',
  `active` tinyint(3) unsigned NOT NULL DEFAULT '1',
  PRIMARY KEY (`captcha_question_id`),
  KEY `active` (`active`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_code_event`
--

CREATE TABLE IF NOT EXISTS `xf_code_event` (
  `event_id` varbinary(50) NOT NULL,
  `description` text NOT NULL,
  `addon_id` varbinary(25) NOT NULL DEFAULT '',
  PRIMARY KEY (`event_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_code_event_listener`
--

CREATE TABLE IF NOT EXISTS `xf_code_event_listener` (
  `event_listener_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `event_id` varbinary(50) NOT NULL,
  `execute_order` int(10) unsigned NOT NULL,
  `description` text NOT NULL,
  `callback_class` varchar(75) NOT NULL,
  `callback_method` varchar(50) NOT NULL,
  `active` tinyint(3) unsigned NOT NULL,
  `addon_id` varbinary(25) NOT NULL,
  `hint` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`event_listener_id`),
  KEY `event_id_execute_order` (`event_id`,`execute_order`),
  KEY `addon_id_event_id` (`addon_id`,`event_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_content_spam_cache`
--

CREATE TABLE IF NOT EXISTS `xf_content_spam_cache` (
  `spam_cache_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `content_type` varbinary(25) NOT NULL,
  `content_id` int(10) unsigned NOT NULL,
  `spam_params` mediumblob NOT NULL,
  `insert_date` int(11) NOT NULL,
  PRIMARY KEY (`spam_cache_id`),
  UNIQUE KEY `content_type` (`content_type`,`content_id`),
  KEY `insert_date` (`insert_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_content_type`
--

CREATE TABLE IF NOT EXISTS `xf_content_type` (
  `content_type` varbinary(25) NOT NULL,
  `addon_id` varbinary(25) NOT NULL DEFAULT '',
  `fields` mediumblob NOT NULL,
  PRIMARY KEY (`content_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_content_type_field`
--

CREATE TABLE IF NOT EXISTS `xf_content_type_field` (
  `content_type` varbinary(25) NOT NULL,
  `field_name` varbinary(50) NOT NULL,
  `field_value` varchar(75) NOT NULL,
  PRIMARY KEY (`content_type`,`field_name`),
  KEY `field_name` (`field_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_conversation_master`
--

CREATE TABLE IF NOT EXISTS `xf_conversation_master` (
  `conversation_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(150) NOT NULL,
  `user_id` int(10) unsigned NOT NULL,
  `username` varchar(50) NOT NULL,
  `start_date` int(10) unsigned NOT NULL,
  `open_invite` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `conversation_open` tinyint(3) unsigned NOT NULL DEFAULT '1',
  `reply_count` int(10) unsigned NOT NULL DEFAULT '0',
  `recipient_count` int(10) unsigned NOT NULL DEFAULT '0',
  `first_message_id` int(10) unsigned NOT NULL,
  `last_message_date` int(10) unsigned NOT NULL,
  `last_message_id` int(10) unsigned NOT NULL,
  `last_message_user_id` int(10) unsigned NOT NULL,
  `last_message_username` varchar(50) NOT NULL,
  `recipients` mediumblob NOT NULL,
  PRIMARY KEY (`conversation_id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_conversation_message`
--

CREATE TABLE IF NOT EXISTS `xf_conversation_message` (
  `message_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `conversation_id` int(10) unsigned NOT NULL,
  `message_date` int(10) unsigned NOT NULL,
  `user_id` int(10) unsigned NOT NULL,
  `username` varchar(50) NOT NULL,
  `message` mediumtext NOT NULL,
  `attach_count` smallint(5) unsigned NOT NULL DEFAULT '0',
  `ip_id` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`message_id`),
  KEY `conversation_id_message_date` (`conversation_id`,`message_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_conversation_recipient`
--

CREATE TABLE IF NOT EXISTS `xf_conversation_recipient` (
  `conversation_id` int(10) unsigned NOT NULL,
  `user_id` int(10) unsigned NOT NULL,
  `recipient_state` enum('active','deleted','deleted_ignored') NOT NULL,
  `last_read_date` int(10) unsigned NOT NULL,
  PRIMARY KEY (`conversation_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_conversation_user`
--

CREATE TABLE IF NOT EXISTS `xf_conversation_user` (
  `conversation_id` int(10) unsigned NOT NULL,
  `owner_user_id` int(10) unsigned NOT NULL,
  `is_unread` tinyint(3) unsigned NOT NULL,
  `reply_count` int(10) unsigned NOT NULL,
  `last_message_date` int(10) unsigned NOT NULL,
  `last_message_id` int(10) unsigned NOT NULL,
  `last_message_user_id` int(10) unsigned NOT NULL,
  `last_message_username` varchar(50) NOT NULL,
  `is_starred` tinyint(3) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`conversation_id`,`owner_user_id`),
  KEY `owner_user_id_last_message_date` (`owner_user_id`,`last_message_date`),
  KEY `owner_user_id_is_unread` (`owner_user_id`,`is_unread`),
  KEY `owner_starred_date` (`owner_user_id`,`is_starred`,`last_message_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_cron_entry`
--

CREATE TABLE IF NOT EXISTS `xf_cron_entry` (
  `entry_id` varbinary(25) NOT NULL,
  `cron_class` varchar(75) NOT NULL,
  `cron_method` varchar(50) NOT NULL,
  `run_rules` mediumblob NOT NULL,
  `active` tinyint(3) unsigned NOT NULL,
  `next_run` int(10) unsigned NOT NULL,
  `addon_id` varbinary(25) NOT NULL,
  PRIMARY KEY (`entry_id`),
  KEY `active_next_run` (`active`,`next_run`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_data_registry`
--

CREATE TABLE IF NOT EXISTS `xf_data_registry` (
  `data_key` varbinary(25) NOT NULL,
  `data_value` mediumblob NOT NULL,
  PRIMARY KEY (`data_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_deferred`
--

CREATE TABLE IF NOT EXISTS `xf_deferred` (
  `deferred_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `unique_key` varbinary(50) DEFAULT NULL,
  `execute_class` varchar(75) NOT NULL,
  `execute_data` mediumblob NOT NULL,
  `manual_execute` tinyint(4) NOT NULL,
  `trigger_date` int(11) NOT NULL,
  PRIMARY KEY (`deferred_id`),
  UNIQUE KEY `unique_key` (`unique_key`),
  KEY `trigger_date` (`trigger_date`),
  KEY `manual_execute_date` (`manual_execute`,`trigger_date`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=267 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_deletion_log`
--

CREATE TABLE IF NOT EXISTS `xf_deletion_log` (
  `content_type` varbinary(25) NOT NULL,
  `content_id` int(11) NOT NULL,
  `delete_date` int(11) NOT NULL,
  `delete_user_id` int(11) NOT NULL,
  `delete_username` varchar(50) NOT NULL,
  `delete_reason` varchar(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`content_type`,`content_id`),
  KEY `delete_user_id_date` (`delete_user_id`,`delete_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_draft`
--

CREATE TABLE IF NOT EXISTS `xf_draft` (
  `draft_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `draft_key` varbinary(75) NOT NULL,
  `user_id` int(10) unsigned NOT NULL,
  `last_update` int(10) unsigned NOT NULL,
  `message` mediumtext NOT NULL,
  `extra_data` mediumblob NOT NULL,
  PRIMARY KEY (`draft_id`),
  UNIQUE KEY `draft_key_user` (`draft_key`,`user_id`),
  KEY `last_update` (`last_update`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_edit_history`
--

CREATE TABLE IF NOT EXISTS `xf_edit_history` (
  `edit_history_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `content_type` varbinary(25) NOT NULL,
  `content_id` int(10) unsigned NOT NULL,
  `edit_user_id` int(10) unsigned NOT NULL,
  `edit_date` int(10) unsigned NOT NULL,
  `old_text` mediumtext NOT NULL,
  PRIMARY KEY (`edit_history_id`),
  KEY `content_type` (`content_type`,`content_id`,`edit_date`),
  KEY `edit_date` (`edit_date`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_email_template`
--

CREATE TABLE IF NOT EXISTS `xf_email_template` (
  `template_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `title` varbinary(50) NOT NULL,
  `custom` tinyint(3) unsigned NOT NULL,
  `subject` mediumtext NOT NULL COMMENT 'User-editable subject with template syntax',
  `subject_parsed` mediumblob NOT NULL,
  `body_text` mediumtext NOT NULL COMMENT 'User-editable plain text body with template syntax',
  `body_text_parsed` mediumblob NOT NULL,
  `body_html` mediumtext NOT NULL COMMENT 'User-editable HTML body t with template syntax',
  `body_html_parsed` mediumblob NOT NULL,
  `addon_id` varbinary(25) NOT NULL DEFAULT '',
  PRIMARY KEY (`template_id`),
  UNIQUE KEY `title_custom` (`title`,`custom`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=21 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_email_template_compiled`
--

CREATE TABLE IF NOT EXISTS `xf_email_template_compiled` (
  `language_id` int(10) unsigned NOT NULL,
  `title` varbinary(50) NOT NULL,
  `template_compiled` mediumblob NOT NULL COMMENT 'Executable PHP code from compilation. Outputs 3 vars.',
  PRIMARY KEY (`title`,`language_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_email_template_modification`
--

CREATE TABLE IF NOT EXISTS `xf_email_template_modification` (
  `modification_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `addon_id` varbinary(25) NOT NULL,
  `template` varbinary(50) NOT NULL,
  `modification_key` varbinary(50) NOT NULL,
  `description` varchar(255) NOT NULL,
  `execution_order` int(10) unsigned NOT NULL,
  `enabled` tinyint(3) unsigned NOT NULL,
  `search_location` varchar(25) NOT NULL,
  `action` varchar(25) NOT NULL,
  `find` text NOT NULL,
  `replace` text NOT NULL,
  PRIMARY KEY (`modification_id`),
  UNIQUE KEY `modification_key` (`modification_key`),
  KEY `addon_id` (`addon_id`),
  KEY `template_order` (`template`,`execution_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_email_template_modification_log`
--

CREATE TABLE IF NOT EXISTS `xf_email_template_modification_log` (
  `template_id` int(10) unsigned NOT NULL,
  `modification_id` int(10) unsigned NOT NULL,
  `status` varchar(25) NOT NULL,
  `apply_count` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`template_id`,`modification_id`),
  KEY `modification_id` (`modification_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_email_template_phrase`
--

CREATE TABLE IF NOT EXISTS `xf_email_template_phrase` (
  `title` varbinary(50) NOT NULL,
  `phrase_title` varbinary(75) NOT NULL,
  PRIMARY KEY (`title`,`phrase_title`),
  KEY `phrase_title` (`phrase_title`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_error_log`
--

CREATE TABLE IF NOT EXISTS `xf_error_log` (
  `error_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `exception_date` int(10) unsigned NOT NULL,
  `user_id` int(10) unsigned DEFAULT NULL,
  `ip_address` int(10) unsigned NOT NULL DEFAULT '0',
  `exception_type` varchar(75) NOT NULL,
  `message` text NOT NULL,
  `filename` varchar(255) NOT NULL,
  `line` int(10) unsigned NOT NULL,
  `trace_string` mediumtext NOT NULL,
  `request_state` mediumblob NOT NULL,
  PRIMARY KEY (`error_id`),
  KEY `exception_date` (`exception_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_feed`
--

CREATE TABLE IF NOT EXISTS `xf_feed` (
  `feed_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(250) NOT NULL,
  `url` varchar(2083) NOT NULL,
  `frequency` int(10) unsigned NOT NULL DEFAULT '1800',
  `node_id` int(10) unsigned NOT NULL,
  `user_id` int(10) unsigned NOT NULL DEFAULT '0',
  `prefix_id` int(10) unsigned NOT NULL DEFAULT '0',
  `title_template` varchar(250) NOT NULL DEFAULT '',
  `message_template` mediumtext NOT NULL,
  `discussion_visible` tinyint(3) unsigned NOT NULL DEFAULT '1',
  `discussion_open` tinyint(3) unsigned NOT NULL DEFAULT '1',
  `discussion_sticky` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `last_fetch` int(10) unsigned NOT NULL DEFAULT '0',
  `active` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`feed_id`),
  KEY `active` (`active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_feed_log`
--

CREATE TABLE IF NOT EXISTS `xf_feed_log` (
  `feed_id` int(10) unsigned NOT NULL,
  `unique_id` varchar(250) NOT NULL,
  `hash` char(32) NOT NULL COMMENT 'MD5(title + content)',
  `thread_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`feed_id`,`unique_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_flood_check`
--

CREATE TABLE IF NOT EXISTS `xf_flood_check` (
  `user_id` int(10) unsigned NOT NULL,
  `flood_action` varchar(25) NOT NULL,
  `flood_time` int(10) unsigned NOT NULL,
  PRIMARY KEY (`user_id`,`flood_action`),
  KEY `flood_time` (`flood_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_forum`
--

CREATE TABLE IF NOT EXISTS `xf_forum` (
  `node_id` int(10) unsigned NOT NULL,
  `discussion_count` int(10) unsigned NOT NULL DEFAULT '0',
  `message_count` int(10) unsigned NOT NULL DEFAULT '0',
  `last_post_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'Most recent post_id',
  `last_post_date` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'Date of most recent post',
  `last_post_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'User_id of user posting most recently',
  `last_post_username` varchar(50) NOT NULL DEFAULT '' COMMENT 'Username of most recently-posting user',
  `last_thread_title` varchar(150) NOT NULL DEFAULT '' COMMENT 'Title of thread most recent post is in',
  `moderate_messages` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `allow_posting` tinyint(3) unsigned NOT NULL DEFAULT '1',
  `count_messages` tinyint(3) unsigned NOT NULL DEFAULT '1' COMMENT 'If not set, messages posted (directly) within this forum will not contribute to user message totals.',
  `find_new` tinyint(3) unsigned NOT NULL DEFAULT '1' COMMENT 'Include posts from this forum when running /find-new/threads',
  `prefix_cache` mediumblob NOT NULL COMMENT 'Serialized data from xf_forum_prefix, [group_id][prefix_id] => prefix_id',
  `default_prefix_id` int(10) unsigned NOT NULL DEFAULT '0',
  `default_sort_order` varchar(25) NOT NULL DEFAULT 'last_post_date',
  `default_sort_direction` varchar(5) NOT NULL DEFAULT 'desc',
  `require_prefix` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `allowed_watch_notifications` varchar(10) NOT NULL DEFAULT 'all',
  PRIMARY KEY (`node_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_forum_prefix`
--

CREATE TABLE IF NOT EXISTS `xf_forum_prefix` (
  `node_id` int(10) unsigned NOT NULL,
  `prefix_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`node_id`,`prefix_id`),
  KEY `prefix_id` (`prefix_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_forum_read`
--

CREATE TABLE IF NOT EXISTS `xf_forum_read` (
  `forum_read_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL,
  `node_id` int(10) unsigned NOT NULL,
  `forum_read_date` int(10) unsigned NOT NULL,
  PRIMARY KEY (`forum_read_id`),
  UNIQUE KEY `user_id_node_id` (`user_id`,`node_id`),
  KEY `node_id` (`node_id`),
  KEY `forum_read_date` (`forum_read_date`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=7 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_forum_watch`
--

CREATE TABLE IF NOT EXISTS `xf_forum_watch` (
  `user_id` int(10) unsigned NOT NULL,
  `node_id` int(10) unsigned NOT NULL,
  `notify_on` enum('','thread','message') NOT NULL,
  `send_alert` tinyint(3) unsigned NOT NULL,
  `send_email` tinyint(3) unsigned NOT NULL,
  PRIMARY KEY (`user_id`,`node_id`),
  KEY `node_id_notify_on` (`node_id`,`notify_on`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_import_log`
--

CREATE TABLE IF NOT EXISTS `xf_import_log` (
  `content_type` varbinary(25) NOT NULL,
  `old_id` varbinary(50) NOT NULL,
  `new_id` varbinary(50) NOT NULL,
  PRIMARY KEY (`content_type`,`old_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_ip`
--

CREATE TABLE IF NOT EXISTS `xf_ip` (
  `ip_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL,
  `content_type` varbinary(25) NOT NULL,
  `content_id` int(10) unsigned NOT NULL,
  `action` varbinary(25) NOT NULL DEFAULT '',
  `ip` int(10) unsigned NOT NULL,
  `log_date` int(10) unsigned NOT NULL,
  PRIMARY KEY (`ip_id`),
  KEY `user_id_log_date` (`user_id`,`log_date`),
  KEY `ip_log_date` (`ip`,`log_date`),
  KEY `content_type_content_id` (`content_type`,`content_id`),
  KEY `log_date` (`log_date`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=70 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_ip_match`
--

CREATE TABLE IF NOT EXISTS `xf_ip_match` (
  `ip` varchar(25) NOT NULL,
  `match_type` enum('banned','discouraged') NOT NULL DEFAULT 'banned',
  `first_octet` tinyint(3) unsigned NOT NULL,
  `start_range` int(10) unsigned NOT NULL COMMENT 'PHP ip2long format',
  `end_range` int(10) unsigned NOT NULL COMMENT 'PHP ip2long format',
  PRIMARY KEY (`ip`,`match_type`),
  KEY `start_range` (`start_range`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_language`
--

CREATE TABLE IF NOT EXISTS `xf_language` (
  `language_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `parent_id` int(10) unsigned NOT NULL,
  `parent_list` varbinary(100) NOT NULL,
  `title` varchar(50) NOT NULL,
  `date_format` varchar(30) NOT NULL,
  `time_format` varchar(15) NOT NULL,
  `decimal_point` varchar(1) NOT NULL,
  `thousands_separator` varchar(1) NOT NULL,
  `phrase_cache` mediumblob NOT NULL,
  `language_code` varchar(25) NOT NULL DEFAULT '',
  `text_direction` enum('LTR','RTL') NOT NULL DEFAULT 'LTR',
  PRIMARY KEY (`language_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_liked_content`
--

CREATE TABLE IF NOT EXISTS `xf_liked_content` (
  `like_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `content_type` varbinary(25) NOT NULL,
  `content_id` int(10) unsigned NOT NULL,
  `like_user_id` int(10) unsigned NOT NULL,
  `like_date` int(10) unsigned NOT NULL,
  `content_user_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`like_id`),
  UNIQUE KEY `content_type_id_like_user_id` (`content_type`,`content_id`,`like_user_id`),
  KEY `like_user_content_type_id` (`like_user_id`,`content_type`,`content_id`),
  KEY `content_user_id_like_date` (`content_user_id`,`like_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_link_forum`
--

CREATE TABLE IF NOT EXISTS `xf_link_forum` (
  `node_id` int(10) unsigned NOT NULL,
  `link_url` varchar(150) NOT NULL,
  `redirect_count` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`node_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_login_attempt`
--

CREATE TABLE IF NOT EXISTS `xf_login_attempt` (
  `login` varchar(60) NOT NULL,
  `ip_address` int(10) unsigned NOT NULL,
  `attempt_date` int(10) unsigned NOT NULL,
  KEY `login_check` (`login`,`ip_address`,`attempt_date`),
  KEY `attempt_date` (`attempt_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_mail_queue`
--

CREATE TABLE IF NOT EXISTS `xf_mail_queue` (
  `mail_queue_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `mail_data` mediumblob NOT NULL,
  `queue_date` int(10) unsigned NOT NULL,
  PRIMARY KEY (`mail_queue_id`),
  KEY `queue_date` (`queue_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_moderation_queue`
--

CREATE TABLE IF NOT EXISTS `xf_moderation_queue` (
  `content_type` varbinary(25) NOT NULL,
  `content_id` int(10) unsigned NOT NULL,
  `content_date` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`content_type`,`content_id`),
  KEY `content_date` (`content_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_moderator`
--

CREATE TABLE IF NOT EXISTS `xf_moderator` (
  `user_id` int(10) unsigned NOT NULL,
  `is_super_moderator` tinyint(3) unsigned NOT NULL,
  `moderator_permissions` mediumblob NOT NULL,
  `extra_user_group_ids` varbinary(255) NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_moderator_content`
--

CREATE TABLE IF NOT EXISTS `xf_moderator_content` (
  `moderator_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `content_type` varbinary(25) NOT NULL,
  `content_id` int(10) unsigned NOT NULL,
  `user_id` int(10) unsigned NOT NULL,
  `moderator_permissions` mediumblob NOT NULL,
  PRIMARY KEY (`moderator_id`),
  UNIQUE KEY `content_user_id` (`content_type`,`content_id`,`user_id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_moderator_log`
--

CREATE TABLE IF NOT EXISTS `xf_moderator_log` (
  `moderator_log_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `log_date` int(10) unsigned NOT NULL,
  `user_id` int(10) unsigned NOT NULL,
  `ip_address` int(10) unsigned NOT NULL DEFAULT '0',
  `content_type` varbinary(25) NOT NULL,
  `content_id` int(10) unsigned NOT NULL,
  `content_user_id` int(10) unsigned NOT NULL,
  `content_username` varchar(50) NOT NULL,
  `content_title` varchar(150) NOT NULL,
  `content_url` text NOT NULL,
  `discussion_content_type` varchar(25) NOT NULL,
  `discussion_content_id` int(10) unsigned NOT NULL,
  `action` varchar(25) NOT NULL,
  `action_params` mediumblob NOT NULL,
  PRIMARY KEY (`moderator_log_id`),
  KEY `log_date` (`log_date`),
  KEY `content_type_id` (`content_type`,`content_id`),
  KEY `discussion_content_type_id` (`discussion_content_type`,`discussion_content_id`),
  KEY `user_id_log_date` (`user_id`,`log_date`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_news_feed`
--

CREATE TABLE IF NOT EXISTS `xf_news_feed` (
  `news_feed_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL COMMENT 'The user who performed the action',
  `username` varchar(50) NOT NULL DEFAULT '' COMMENT 'Corresponds to user_id',
  `content_type` varbinary(25) NOT NULL COMMENT 'eg: thread',
  `content_id` int(10) unsigned NOT NULL,
  `action` varchar(25) NOT NULL COMMENT 'eg: edit',
  `event_date` int(10) unsigned NOT NULL,
  `extra_data` mediumblob NOT NULL COMMENT 'Serialized. Stores any extra data relevant to the action',
  PRIMARY KEY (`news_feed_id`),
  KEY `userId_eventDate` (`user_id`,`event_date`),
  KEY `contentType_contentId` (`content_type`,`content_id`),
  KEY `event_date` (`event_date`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=18 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_node`
--

CREATE TABLE IF NOT EXISTS `xf_node` (
  `node_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(50) NOT NULL,
  `description` text NOT NULL,
  `node_name` varchar(50) DEFAULT NULL COMMENT 'Unique column used as string ID by some node types',
  `node_type_id` varbinary(25) NOT NULL,
  `parent_node_id` int(10) unsigned NOT NULL DEFAULT '0',
  `display_order` int(10) unsigned NOT NULL DEFAULT '1',
  `display_in_list` tinyint(3) unsigned NOT NULL DEFAULT '1' COMMENT 'If 0, hidden from node list. Still counts for lft/rgt.',
  `lft` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'Nested set info ''left'' value',
  `rgt` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'Nested set info ''right'' value',
  `depth` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'Depth = 0: no parent',
  `style_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'Style override for specific node',
  `effective_style_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'Style override; pushed down tree',
  `breadcrumb_data` blob,
  PRIMARY KEY (`node_id`),
  UNIQUE KEY `node_name_unique` (`node_name`,`node_type_id`),
  KEY `parent_node_id` (`parent_node_id`),
  KEY `display_order` (`display_order`),
  KEY `display_in_list` (`display_in_list`,`lft`),
  KEY `lft` (`lft`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=14 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_node_type`
--

CREATE TABLE IF NOT EXISTS `xf_node_type` (
  `node_type_id` varbinary(25) NOT NULL,
  `handler_class` varchar(75) NOT NULL,
  `controller_admin_class` varchar(75) NOT NULL COMMENT 'extends XenForo_ControllerAdmin_Abstract',
  `datawriter_class` varchar(75) NOT NULL COMMENT 'extends XenForo_DataWriter_Node',
  `permission_group_id` varchar(25) NOT NULL DEFAULT '',
  `moderator_interface_group_id` varchar(50) NOT NULL DEFAULT '',
  `public_route_prefix` varchar(25) NOT NULL,
  PRIMARY KEY (`node_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_notice`
--

CREATE TABLE IF NOT EXISTS `xf_notice` (
  `notice_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(150) NOT NULL,
  `message` mediumtext NOT NULL,
  `active` tinyint(3) unsigned NOT NULL DEFAULT '1',
  `display_order` int(10) unsigned NOT NULL DEFAULT '0',
  `dismissible` tinyint(3) unsigned NOT NULL DEFAULT '1' COMMENT 'Notice may be hidden when read by users',
  `wrap` tinyint(3) unsigned NOT NULL DEFAULT '1' COMMENT 'Wrap this notice in div.noticeContent',
  `user_criteria` mediumblob NOT NULL,
  `page_criteria` mediumblob NOT NULL,
  PRIMARY KEY (`notice_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_notice_dismissed`
--

CREATE TABLE IF NOT EXISTS `xf_notice_dismissed` (
  `notice_id` int(10) unsigned NOT NULL,
  `user_id` int(10) unsigned NOT NULL,
  `dismiss_date` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`notice_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_option`
--

CREATE TABLE IF NOT EXISTS `xf_option` (
  `option_id` varbinary(50) NOT NULL,
  `option_value` mediumblob NOT NULL,
  `default_value` mediumblob NOT NULL,
  `edit_format` enum('textbox','spinbox','onoff','radio','select','checkbox','template','callback','onofftextbox') NOT NULL,
  `edit_format_params` mediumtext NOT NULL,
  `data_type` enum('string','integer','numeric','array','boolean','positive_integer','unsigned_integer','unsigned_numeric') NOT NULL,
  `sub_options` mediumtext NOT NULL,
  `can_backup` tinyint(3) unsigned NOT NULL,
  `validation_class` varchar(75) NOT NULL,
  `validation_method` varchar(50) NOT NULL,
  `addon_id` varbinary(25) NOT NULL DEFAULT '',
  PRIMARY KEY (`option_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_option_group`
--

CREATE TABLE IF NOT EXISTS `xf_option_group` (
  `group_id` varbinary(50) NOT NULL,
  `display_order` int(10) unsigned NOT NULL,
  `debug_only` tinyint(3) unsigned NOT NULL,
  `addon_id` varbinary(25) NOT NULL DEFAULT '',
  PRIMARY KEY (`group_id`),
  KEY `display_order` (`display_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_option_group_relation`
--

CREATE TABLE IF NOT EXISTS `xf_option_group_relation` (
  `option_id` varbinary(50) NOT NULL,
  `group_id` varbinary(50) NOT NULL,
  `display_order` int(10) unsigned NOT NULL,
  PRIMARY KEY (`option_id`,`group_id`),
  KEY `group_id_display_order` (`group_id`,`display_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_page`
--

CREATE TABLE IF NOT EXISTS `xf_page` (
  `node_id` int(10) unsigned NOT NULL,
  `publish_date` int(10) unsigned NOT NULL,
  `modified_date` int(10) unsigned NOT NULL DEFAULT '0',
  `view_count` int(10) unsigned NOT NULL DEFAULT '0',
  `log_visits` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `list_siblings` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `list_children` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `callback_class` varchar(75) NOT NULL DEFAULT '',
  `callback_method` varchar(75) NOT NULL DEFAULT '',
  PRIMARY KEY (`node_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_permission`
--

CREATE TABLE IF NOT EXISTS `xf_permission` (
  `permission_id` varbinary(25) NOT NULL,
  `permission_group_id` varbinary(25) NOT NULL,
  `permission_type` enum('flag','integer') NOT NULL,
  `interface_group_id` varbinary(50) NOT NULL,
  `depend_permission_id` varbinary(25) NOT NULL,
  `display_order` int(10) unsigned NOT NULL,
  `default_value` enum('allow','deny','unset') NOT NULL,
  `default_value_int` int(11) NOT NULL,
  `addon_id` varbinary(25) NOT NULL DEFAULT '',
  PRIMARY KEY (`permission_id`,`permission_group_id`),
  KEY `display_order` (`display_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_permission_cache_content`
--

CREATE TABLE IF NOT EXISTS `xf_permission_cache_content` (
  `permission_combination_id` int(10) unsigned NOT NULL,
  `content_type` varbinary(25) NOT NULL,
  `content_id` int(10) unsigned NOT NULL,
  `cache_value` mediumblob NOT NULL,
  PRIMARY KEY (`permission_combination_id`,`content_type`,`content_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_permission_combination`
--

CREATE TABLE IF NOT EXISTS `xf_permission_combination` (
  `permission_combination_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL,
  `user_group_list` mediumblob NOT NULL,
  `cache_value` mediumblob NOT NULL,
  PRIMARY KEY (`permission_combination_id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=8 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_permission_combination_user_group`
--

CREATE TABLE IF NOT EXISTS `xf_permission_combination_user_group` (
  `user_group_id` int(10) unsigned NOT NULL,
  `permission_combination_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`user_group_id`,`permission_combination_id`),
  KEY `permission_combination_id` (`permission_combination_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_permission_entry`
--

CREATE TABLE IF NOT EXISTS `xf_permission_entry` (
  `permission_entry_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_group_id` int(10) unsigned NOT NULL,
  `user_id` int(10) unsigned NOT NULL,
  `permission_group_id` varbinary(25) NOT NULL,
  `permission_id` varbinary(25) NOT NULL,
  `permission_value` enum('unset','allow','deny','use_int') NOT NULL,
  `permission_value_int` int(11) NOT NULL,
  PRIMARY KEY (`permission_entry_id`),
  UNIQUE KEY `unique_permission` (`user_group_id`,`user_id`,`permission_group_id`,`permission_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=231 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_permission_entry_content`
--

CREATE TABLE IF NOT EXISTS `xf_permission_entry_content` (
  `permission_entry_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `content_type` varbinary(25) NOT NULL,
  `content_id` int(10) unsigned NOT NULL,
  `user_group_id` int(10) unsigned NOT NULL,
  `user_id` int(10) unsigned NOT NULL,
  `permission_group_id` varbinary(25) NOT NULL,
  `permission_id` varbinary(25) NOT NULL,
  `permission_value` enum('unset','reset','content_allow','deny','use_int') NOT NULL,
  `permission_value_int` int(11) NOT NULL,
  PRIMARY KEY (`permission_entry_id`),
  UNIQUE KEY `user_group_id_unique` (`user_group_id`,`user_id`,`content_type`,`content_id`,`permission_group_id`,`permission_id`),
  KEY `content_type_content_id` (`content_type`,`content_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_permission_group`
--

CREATE TABLE IF NOT EXISTS `xf_permission_group` (
  `permission_group_id` varbinary(25) NOT NULL,
  `addon_id` varbinary(25) NOT NULL DEFAULT '',
  PRIMARY KEY (`permission_group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_permission_interface_group`
--

CREATE TABLE IF NOT EXISTS `xf_permission_interface_group` (
  `interface_group_id` varbinary(50) NOT NULL,
  `display_order` int(10) unsigned NOT NULL,
  `addon_id` varbinary(25) NOT NULL DEFAULT '',
  PRIMARY KEY (`interface_group_id`),
  KEY `display_order` (`display_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_phrase`
--

CREATE TABLE IF NOT EXISTS `xf_phrase` (
  `phrase_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `language_id` int(10) unsigned NOT NULL,
  `title` varbinary(75) NOT NULL,
  `phrase_text` mediumtext NOT NULL,
  `global_cache` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `addon_id` varbinary(25) NOT NULL DEFAULT '',
  `version_id` int(10) unsigned NOT NULL DEFAULT '0',
  `version_string` varchar(30) NOT NULL DEFAULT '',
  PRIMARY KEY (`phrase_id`),
  UNIQUE KEY `title` (`title`,`language_id`),
  KEY `language_id_global_cache` (`language_id`,`global_cache`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=5014 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_phrase_compiled`
--

CREATE TABLE IF NOT EXISTS `xf_phrase_compiled` (
  `language_id` int(10) unsigned NOT NULL,
  `title` varbinary(75) NOT NULL,
  `phrase_text` mediumtext NOT NULL,
  PRIMARY KEY (`language_id`,`title`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_phrase_map`
--

CREATE TABLE IF NOT EXISTS `xf_phrase_map` (
  `phrase_map_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `language_id` int(10) unsigned NOT NULL,
  `title` varbinary(75) NOT NULL,
  `phrase_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`phrase_map_id`),
  UNIQUE KEY `language_id_title` (`language_id`,`title`),
  KEY `phrase_id` (`phrase_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=15037 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_poll`
--

CREATE TABLE IF NOT EXISTS `xf_poll` (
  `poll_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `content_type` varbinary(25) NOT NULL,
  `content_id` int(10) unsigned NOT NULL,
  `question` varchar(100) NOT NULL,
  `responses` mediumblob NOT NULL,
  `voter_count` int(10) unsigned NOT NULL DEFAULT '0',
  `public_votes` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `multiple` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `close_date` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`poll_id`),
  UNIQUE KEY `content_type_content_id` (`content_type`,`content_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_poll_response`
--

CREATE TABLE IF NOT EXISTS `xf_poll_response` (
  `poll_response_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `poll_id` int(10) unsigned NOT NULL,
  `response` varchar(100) NOT NULL,
  `response_vote_count` int(10) unsigned NOT NULL DEFAULT '0',
  `voters` mediumblob NOT NULL,
  PRIMARY KEY (`poll_response_id`),
  KEY `poll_id_response_id` (`poll_id`,`poll_response_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_poll_vote`
--

CREATE TABLE IF NOT EXISTS `xf_poll_vote` (
  `user_id` int(10) unsigned NOT NULL,
  `poll_response_id` int(10) unsigned NOT NULL,
  `poll_id` int(10) unsigned NOT NULL,
  `vote_date` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`poll_response_id`,`user_id`),
  KEY `poll_id_user_id` (`poll_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_post`
--

CREATE TABLE IF NOT EXISTS `xf_post` (
  `post_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `thread_id` int(10) unsigned NOT NULL,
  `user_id` int(10) unsigned NOT NULL,
  `username` varchar(50) NOT NULL,
  `post_date` int(10) unsigned NOT NULL,
  `message` mediumtext NOT NULL,
  `ip_id` int(10) unsigned NOT NULL DEFAULT '0',
  `message_state` enum('visible','moderated','deleted') NOT NULL DEFAULT 'visible',
  `attach_count` smallint(5) unsigned NOT NULL DEFAULT '0',
  `position` int(10) unsigned NOT NULL,
  `likes` int(10) unsigned NOT NULL DEFAULT '0',
  `like_users` blob NOT NULL,
  `warning_id` int(10) unsigned NOT NULL DEFAULT '0',
  `warning_message` varchar(255) NOT NULL DEFAULT '',
  `last_edit_date` int(10) unsigned NOT NULL DEFAULT '0',
  `last_edit_user_id` int(10) unsigned NOT NULL DEFAULT '0',
  `edit_count` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`post_id`),
  KEY `thread_id_post_date` (`thread_id`,`post_date`),
  KEY `thread_id_position` (`thread_id`,`position`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=7 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_profile_post`
--

CREATE TABLE IF NOT EXISTS `xf_profile_post` (
  `profile_post_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `profile_user_id` int(10) unsigned NOT NULL,
  `user_id` int(10) unsigned NOT NULL,
  `username` varchar(50) NOT NULL,
  `post_date` int(10) unsigned NOT NULL,
  `message` mediumtext NOT NULL,
  `ip_id` int(10) unsigned NOT NULL DEFAULT '0',
  `message_state` enum('visible','moderated','deleted') NOT NULL DEFAULT 'visible',
  `attach_count` smallint(5) unsigned NOT NULL DEFAULT '0',
  `likes` int(10) unsigned NOT NULL DEFAULT '0',
  `like_users` blob NOT NULL,
  `comment_count` int(10) unsigned NOT NULL DEFAULT '0',
  `first_comment_date` int(10) unsigned NOT NULL DEFAULT '0',
  `last_comment_date` int(10) unsigned NOT NULL DEFAULT '0',
  `latest_comment_ids` varbinary(100) NOT NULL DEFAULT '',
  `warning_id` int(10) unsigned NOT NULL DEFAULT '0',
  `warning_message` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`profile_post_id`),
  KEY `profile_user_id_post_date` (`profile_user_id`,`post_date`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=8 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_profile_post_comment`
--

CREATE TABLE IF NOT EXISTS `xf_profile_post_comment` (
  `profile_post_comment_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `profile_post_id` int(10) unsigned NOT NULL,
  `user_id` int(10) unsigned NOT NULL,
  `username` varchar(50) NOT NULL,
  `comment_date` int(10) unsigned NOT NULL,
  `message` mediumtext NOT NULL,
  `ip_id` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`profile_post_comment_id`),
  KEY `profile_post_id_comment_date` (`profile_post_id`,`comment_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_registration_spam_cache`
--

CREATE TABLE IF NOT EXISTS `xf_registration_spam_cache` (
  `cache_key` varbinary(128) NOT NULL DEFAULT '',
  `decision` varchar(25) NOT NULL DEFAULT '',
  `timeout` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`cache_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_report`
--

CREATE TABLE IF NOT EXISTS `xf_report` (
  `report_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `content_type` varbinary(25) NOT NULL,
  `content_id` int(10) unsigned NOT NULL,
  `content_user_id` int(10) unsigned NOT NULL,
  `content_info` mediumblob NOT NULL,
  `first_report_date` int(10) unsigned NOT NULL,
  `report_state` enum('open','assigned','resolved','rejected') NOT NULL,
  `assigned_user_id` int(10) unsigned NOT NULL,
  `comment_count` int(10) unsigned NOT NULL DEFAULT '0',
  `last_modified_date` int(10) unsigned NOT NULL,
  `last_modified_user_id` int(10) unsigned NOT NULL DEFAULT '0',
  `last_modified_username` varchar(50) NOT NULL DEFAULT '',
  `report_count` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`report_id`),
  UNIQUE KEY `content_type_content_id` (`content_type`,`content_id`),
  KEY `report_state` (`report_state`),
  KEY `assigned_user_id_state` (`assigned_user_id`,`report_state`),
  KEY `last_modified_date` (`last_modified_date`),
  KEY `content_user_id_modified` (`content_user_id`,`last_modified_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_report_comment`
--

CREATE TABLE IF NOT EXISTS `xf_report_comment` (
  `report_comment_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `report_id` int(10) unsigned NOT NULL,
  `comment_date` int(10) unsigned NOT NULL,
  `user_id` int(10) unsigned NOT NULL,
  `username` varchar(50) NOT NULL,
  `message` mediumtext NOT NULL,
  `state_change` enum('','open','assigned','resolved','rejected') NOT NULL DEFAULT '',
  `is_report` tinyint(3) unsigned NOT NULL DEFAULT '1',
  PRIMARY KEY (`report_comment_id`),
  KEY `report_id_date` (`report_id`,`comment_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_route_filter`
--

CREATE TABLE IF NOT EXISTS `xf_route_filter` (
  `route_filter_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `route_type` varbinary(25) NOT NULL,
  `prefix` varchar(25) NOT NULL,
  `find_route` varchar(255) NOT NULL,
  `replace_route` varchar(255) NOT NULL,
  `enabled` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `url_to_route_only` tinyint(3) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`route_filter_id`),
  KEY `route_type_prefix` (`route_type`,`prefix`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_route_prefix`
--

CREATE TABLE IF NOT EXISTS `xf_route_prefix` (
  `route_type` enum('public','admin') NOT NULL,
  `original_prefix` varchar(25) NOT NULL,
  `route_class` varchar(75) NOT NULL,
  `build_link` enum('all','data_only','none') NOT NULL DEFAULT 'none',
  `addon_id` varbinary(25) NOT NULL DEFAULT '',
  PRIMARY KEY (`route_type`,`original_prefix`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_search`
--

CREATE TABLE IF NOT EXISTS `xf_search` (
  `search_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `search_results` mediumblob NOT NULL,
  `result_count` smallint(5) unsigned NOT NULL,
  `search_type` varchar(25) NOT NULL,
  `search_query` varchar(200) NOT NULL,
  `search_constraints` mediumblob NOT NULL,
  `search_order` varchar(50) NOT NULL,
  `search_grouping` tinyint(4) NOT NULL DEFAULT '0',
  `user_results` mediumblob NOT NULL,
  `warnings` mediumblob NOT NULL,
  `user_id` int(10) unsigned NOT NULL,
  `search_date` int(10) unsigned NOT NULL,
  `query_hash` varchar(32) NOT NULL DEFAULT '',
  PRIMARY KEY (`search_id`),
  KEY `search_date` (`search_date`),
  KEY `query_hash` (`query_hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_search_index`
--

CREATE TABLE IF NOT EXISTS `xf_search_index` (
  `content_type` varchar(25) NOT NULL,
  `content_id` int(10) unsigned NOT NULL,
  `title` varchar(250) NOT NULL DEFAULT '',
  `message` mediumtext NOT NULL,
  `metadata` mediumtext NOT NULL,
  `user_id` int(10) unsigned NOT NULL DEFAULT '0',
  `item_date` int(10) unsigned NOT NULL,
  `discussion_id` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`content_type`,`content_id`),
  KEY `user_id_item_date` (`user_id`,`item_date`),
  FULLTEXT KEY `title_message_metadata` (`title`,`message`,`metadata`),
  FULLTEXT KEY `title_metadata` (`title`,`metadata`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_session`
--

CREATE TABLE IF NOT EXISTS `xf_session` (
  `session_id` varbinary(32) NOT NULL,
  `session_data` mediumblob NOT NULL,
  `expiry_date` int(10) unsigned NOT NULL,
  PRIMARY KEY (`session_id`),
  KEY `expiry_date` (`expiry_date`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_session_activity`
--

CREATE TABLE IF NOT EXISTS `xf_session_activity` (
  `user_id` int(10) unsigned NOT NULL,
  `unique_key` int(10) unsigned NOT NULL,
  `ip` int(10) unsigned NOT NULL DEFAULT '0',
  `controller_name` varbinary(50) NOT NULL,
  `controller_action` varbinary(50) NOT NULL,
  `view_state` enum('valid','error') NOT NULL,
  `params` varbinary(100) NOT NULL,
  `view_date` int(10) unsigned NOT NULL,
  `robot_key` varbinary(25) NOT NULL DEFAULT '',
  PRIMARY KEY (`user_id`,`unique_key`),
  KEY `view_date` (`view_date`)
) ENGINE=MEMORY DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_session_admin`
--

CREATE TABLE IF NOT EXISTS `xf_session_admin` (
  `session_id` varbinary(32) NOT NULL,
  `session_data` mediumblob NOT NULL,
  `expiry_date` int(10) unsigned NOT NULL,
  PRIMARY KEY (`session_id`),
  KEY `expiry_date` (`expiry_date`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_smilie`
--

CREATE TABLE IF NOT EXISTS `xf_smilie` (
  `smilie_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(50) NOT NULL,
  `smilie_text` text NOT NULL,
  `image_url` varchar(200) NOT NULL,
  `sprite_mode` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `sprite_params` text NOT NULL,
  PRIMARY KEY (`smilie_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=13 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_spam_cleaner_log`
--

CREATE TABLE IF NOT EXISTS `xf_spam_cleaner_log` (
  `spam_cleaner_log_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL DEFAULT '0',
  `username` varchar(50) NOT NULL DEFAULT '',
  `applying_user_id` int(10) unsigned NOT NULL DEFAULT '0',
  `applying_username` varchar(50) NOT NULL DEFAULT '',
  `application_date` int(10) unsigned NOT NULL DEFAULT '0',
  `data` mediumblob NOT NULL COMMENT 'Serialized array containing log data for undo purposes',
  `restored_date` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`spam_cleaner_log_id`),
  KEY `application_date` (`application_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_stats_daily`
--

CREATE TABLE IF NOT EXISTS `xf_stats_daily` (
  `stats_date` int(10) unsigned NOT NULL,
  `stats_type` varbinary(25) NOT NULL,
  `counter` int(10) unsigned NOT NULL,
  PRIMARY KEY (`stats_date`,`stats_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_style`
--

CREATE TABLE IF NOT EXISTS `xf_style` (
  `style_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `parent_id` int(10) unsigned NOT NULL,
  `parent_list` varbinary(100) NOT NULL COMMENT 'IDs of ancestor styles in order, eg: this,parent,grandparent,root',
  `title` varchar(50) NOT NULL,
  `description` varchar(100) NOT NULL DEFAULT '',
  `properties` mediumblob NOT NULL COMMENT 'Serialized array of materialized style properties for this style',
  `last_modified_date` int(10) unsigned NOT NULL DEFAULT '0',
  `user_selectable` tinyint(3) unsigned NOT NULL DEFAULT '1' COMMENT 'Unselectable styles are unselectable by non-admin visitors',
  PRIMARY KEY (`style_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=8 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_style_property`
--

CREATE TABLE IF NOT EXISTS `xf_style_property` (
  `property_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `property_definition_id` int(10) unsigned NOT NULL,
  `style_id` int(11) NOT NULL,
  `property_value` mediumblob NOT NULL,
  PRIMARY KEY (`property_id`),
  UNIQUE KEY `definition_id_style_id` (`property_definition_id`,`style_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=825 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_style_property_definition`
--

CREATE TABLE IF NOT EXISTS `xf_style_property_definition` (
  `property_definition_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `definition_style_id` int(11) NOT NULL,
  `group_name` varbinary(25) DEFAULT NULL,
  `title` varchar(100) NOT NULL,
  `description` varchar(255) NOT NULL DEFAULT '',
  `property_name` varbinary(100) NOT NULL,
  `property_type` enum('scalar','css') NOT NULL,
  `css_components` blob NOT NULL,
  `scalar_type` enum('','longstring','color','number','boolean','template') NOT NULL DEFAULT '',
  `scalar_parameters` varchar(250) NOT NULL DEFAULT '' COMMENT 'Additional arguments for the given scalar type',
  `addon_id` varbinary(25) NOT NULL,
  `display_order` int(10) unsigned NOT NULL DEFAULT '0',
  `sub_group` varchar(25) NOT NULL DEFAULT '' COMMENT 'Allows loose grouping of scalars within a group',
  PRIMARY KEY (`property_definition_id`),
  UNIQUE KEY `definition_style_id_property_name` (`definition_style_id`,`property_name`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=435 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_style_property_group`
--

CREATE TABLE IF NOT EXISTS `xf_style_property_group` (
  `property_group_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `group_name` varbinary(25) NOT NULL,
  `group_style_id` int(11) NOT NULL,
  `title` varchar(100) NOT NULL,
  `description` varchar(255) NOT NULL DEFAULT '',
  `display_order` int(10) unsigned NOT NULL,
  `addon_id` varbinary(25) NOT NULL,
  PRIMARY KEY (`property_group_id`),
  UNIQUE KEY `group_name_style_id` (`group_name`,`group_style_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=32 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_template`
--

CREATE TABLE IF NOT EXISTS `xf_template` (
  `template_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `title` varbinary(50) NOT NULL,
  `style_id` int(10) unsigned NOT NULL,
  `template` mediumtext NOT NULL COMMENT 'User-editable HTML and template syntax',
  `template_parsed` mediumblob NOT NULL,
  `addon_id` varbinary(25) NOT NULL DEFAULT '',
  `version_id` int(10) unsigned NOT NULL DEFAULT '0',
  `version_string` varchar(30) NOT NULL DEFAULT '',
  `disable_modifications` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `last_edit_date` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`template_id`),
  UNIQUE KEY `title_style_id` (`title`,`style_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=604 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_template_compiled`
--

CREATE TABLE IF NOT EXISTS `xf_template_compiled` (
  `style_id` int(10) unsigned NOT NULL,
  `language_id` int(10) unsigned NOT NULL,
  `title` varbinary(50) NOT NULL,
  `template_compiled` mediumblob NOT NULL COMMENT 'Executable PHP code built by template compiler',
  PRIMARY KEY (`style_id`,`language_id`,`title`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_template_history`
--

CREATE TABLE IF NOT EXISTS `xf_template_history` (
  `template_history_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `title` varbinary(50) NOT NULL,
  `style_id` int(11) unsigned NOT NULL,
  `template` mediumtext NOT NULL,
  `edit_date` int(11) unsigned NOT NULL,
  `log_date` int(11) unsigned NOT NULL,
  PRIMARY KEY (`template_history_id`),
  KEY `log_date` (`log_date`),
  KEY `style_id_title` (`style_id`,`title`),
  KEY `title` (`title`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_template_include`
--

CREATE TABLE IF NOT EXISTS `xf_template_include` (
  `source_map_id` int(10) unsigned NOT NULL,
  `target_map_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`source_map_id`,`target_map_id`),
  KEY `target` (`target_map_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_template_map`
--

CREATE TABLE IF NOT EXISTS `xf_template_map` (
  `template_map_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `style_id` int(10) unsigned NOT NULL,
  `title` varbinary(50) NOT NULL,
  `template_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`template_map_id`),
  UNIQUE KEY `style_id_title` (`style_id`,`title`),
  KEY `template_id` (`template_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1286 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_template_modification`
--

CREATE TABLE IF NOT EXISTS `xf_template_modification` (
  `modification_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `addon_id` varbinary(25) NOT NULL,
  `template` varbinary(50) NOT NULL,
  `modification_key` varbinary(50) NOT NULL,
  `description` varchar(255) NOT NULL,
  `execution_order` int(10) unsigned NOT NULL,
  `enabled` tinyint(3) unsigned NOT NULL,
  `action` varchar(25) NOT NULL,
  `find` text NOT NULL,
  `replace` text NOT NULL,
  PRIMARY KEY (`modification_id`),
  UNIQUE KEY `modification_key` (`modification_key`),
  KEY `addon_id` (`addon_id`),
  KEY `template_order` (`template`,`execution_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_template_modification_log`
--

CREATE TABLE IF NOT EXISTS `xf_template_modification_log` (
  `template_id` int(10) unsigned NOT NULL,
  `modification_id` int(10) unsigned NOT NULL,
  `status` varchar(25) NOT NULL,
  `apply_count` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`template_id`,`modification_id`),
  KEY `modification_id` (`modification_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_template_phrase`
--

CREATE TABLE IF NOT EXISTS `xf_template_phrase` (
  `template_map_id` int(10) unsigned NOT NULL,
  `phrase_title` varbinary(75) NOT NULL,
  PRIMARY KEY (`template_map_id`,`phrase_title`),
  KEY `phrase_title` (`phrase_title`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_thread`
--

CREATE TABLE IF NOT EXISTS `xf_thread` (
  `thread_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `node_id` int(10) unsigned NOT NULL,
  `title` varchar(150) NOT NULL,
  `reply_count` int(10) unsigned NOT NULL DEFAULT '0',
  `view_count` int(10) unsigned NOT NULL DEFAULT '0',
  `user_id` int(10) unsigned NOT NULL,
  `username` varchar(50) NOT NULL,
  `post_date` int(10) unsigned NOT NULL,
  `sticky` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `discussion_state` enum('visible','moderated','deleted') NOT NULL DEFAULT 'visible',
  `discussion_open` tinyint(3) unsigned NOT NULL DEFAULT '1',
  `discussion_type` varchar(25) NOT NULL DEFAULT '',
  `first_post_id` int(10) unsigned NOT NULL,
  `first_post_likes` int(10) unsigned NOT NULL DEFAULT '0',
  `last_post_date` int(10) unsigned NOT NULL,
  `last_post_id` int(10) unsigned NOT NULL,
  `last_post_user_id` int(10) unsigned NOT NULL,
  `last_post_username` varchar(50) NOT NULL,
  `prefix_id` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`thread_id`),
  KEY `node_id_last_post_date` (`node_id`,`last_post_date`),
  KEY `node_id_sticky_state_last_post` (`node_id`,`sticky`,`discussion_state`,`last_post_date`),
  KEY `last_post_date` (`last_post_date`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=7 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_thread_prefix`
--

CREATE TABLE IF NOT EXISTS `xf_thread_prefix` (
  `prefix_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `prefix_group_id` int(10) unsigned NOT NULL,
  `display_order` int(10) unsigned NOT NULL,
  `materialized_order` int(10) unsigned NOT NULL COMMENT 'Internally-set order, based on prefix_group.display_order, prefix.display_order',
  `css_class` varchar(50) NOT NULL DEFAULT '',
  `allowed_user_group_ids` blob NOT NULL,
  PRIMARY KEY (`prefix_id`),
  KEY `materialized_order` (`materialized_order`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=7 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_thread_prefix_group`
--

CREATE TABLE IF NOT EXISTS `xf_thread_prefix_group` (
  `prefix_group_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `display_order` int(10) unsigned NOT NULL,
  PRIMARY KEY (`prefix_group_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_thread_read`
--

CREATE TABLE IF NOT EXISTS `xf_thread_read` (
  `thread_read_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL,
  `thread_id` int(10) unsigned NOT NULL,
  `thread_read_date` int(10) unsigned NOT NULL,
  PRIMARY KEY (`thread_read_id`),
  UNIQUE KEY `user_id_thread_id` (`user_id`,`thread_id`),
  KEY `thread_id` (`thread_id`),
  KEY `thread_read_date` (`thread_read_date`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=7 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_thread_redirect`
--

CREATE TABLE IF NOT EXISTS `xf_thread_redirect` (
  `thread_id` int(10) unsigned NOT NULL,
  `target_url` text NOT NULL,
  `redirect_key` varchar(50) NOT NULL DEFAULT '',
  `expiry_date` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`thread_id`),
  KEY `redirect_key_expiry_date` (`redirect_key`,`expiry_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_thread_user_post`
--

CREATE TABLE IF NOT EXISTS `xf_thread_user_post` (
  `thread_id` int(10) unsigned NOT NULL,
  `user_id` int(10) unsigned NOT NULL,
  `post_count` int(10) unsigned NOT NULL,
  PRIMARY KEY (`thread_id`,`user_id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_thread_view`
--

CREATE TABLE IF NOT EXISTS `xf_thread_view` (
  `thread_id` int(10) unsigned NOT NULL,
  KEY `thread_id` (`thread_id`)
) ENGINE=MEMORY DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_thread_watch`
--

CREATE TABLE IF NOT EXISTS `xf_thread_watch` (
  `user_id` int(10) unsigned NOT NULL,
  `thread_id` int(10) unsigned NOT NULL,
  `email_subscribe` tinyint(3) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`user_id`,`thread_id`),
  KEY `thread_id_email_subscribe` (`thread_id`,`email_subscribe`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_trophy`
--

CREATE TABLE IF NOT EXISTS `xf_trophy` (
  `trophy_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `trophy_points` int(10) unsigned NOT NULL,
  `user_criteria` mediumblob NOT NULL,
  PRIMARY KEY (`trophy_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=11 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_trophy_user_title`
--

CREATE TABLE IF NOT EXISTS `xf_trophy_user_title` (
  `minimum_points` int(10) unsigned NOT NULL,
  `title` varchar(250) NOT NULL,
  PRIMARY KEY (`minimum_points`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_upgrade_log`
--

CREATE TABLE IF NOT EXISTS `xf_upgrade_log` (
  `version_id` int(10) unsigned NOT NULL,
  `completion_date` int(10) unsigned NOT NULL DEFAULT '0',
  `user_id` int(10) unsigned NOT NULL DEFAULT '0',
  `log_type` enum('install','upgrade') NOT NULL DEFAULT 'upgrade',
  PRIMARY KEY (`version_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_user`
--

CREATE TABLE IF NOT EXISTS `xf_user` (
  `user_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `email` varchar(120) NOT NULL,
  `gender` enum('','male','female') NOT NULL DEFAULT '' COMMENT 'Leave empty for ''unspecified''',
  `custom_title` varchar(50) NOT NULL DEFAULT '',
  `language_id` int(10) unsigned NOT NULL,
  `style_id` int(10) unsigned NOT NULL COMMENT '0 = use system default',
  `timezone` varchar(50) NOT NULL COMMENT 'Example: ''Europe/London''',
  `visible` tinyint(3) unsigned NOT NULL DEFAULT '1' COMMENT 'Show browsing activity to others',
  `user_group_id` int(10) unsigned NOT NULL,
  `secondary_group_ids` varbinary(255) NOT NULL,
  `display_style_group_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'User group ID that provides user styling',
  `permission_combination_id` int(10) unsigned NOT NULL,
  `message_count` int(10) unsigned NOT NULL DEFAULT '0',
  `conversations_unread` smallint(5) unsigned NOT NULL DEFAULT '0',
  `register_date` int(10) unsigned NOT NULL DEFAULT '0',
  `last_activity` int(10) unsigned NOT NULL DEFAULT '0',
  `trophy_points` int(10) unsigned NOT NULL DEFAULT '0',
  `alerts_unread` smallint(5) unsigned NOT NULL DEFAULT '0',
  `avatar_date` int(10) unsigned NOT NULL DEFAULT '0',
  `avatar_width` smallint(5) unsigned NOT NULL DEFAULT '0',
  `avatar_height` smallint(5) unsigned NOT NULL DEFAULT '0',
  `gravatar` varchar(120) NOT NULL DEFAULT '' COMMENT 'If specified, this is an email address corresponding to the user''s ''Gravatar''',
  `user_state` enum('valid','email_confirm','email_confirm_edit','moderated') NOT NULL DEFAULT 'valid',
  `is_moderator` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `is_admin` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `is_banned` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `like_count` int(10) unsigned NOT NULL DEFAULT '0',
  `warning_points` int(10) unsigned NOT NULL DEFAULT '0',
  `is_staff` tinyint(3) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`),
  KEY `email` (`email`),
  KEY `user_state` (`user_state`),
  KEY `last_activity` (`last_activity`),
  KEY `message_count` (`message_count`),
  KEY `trophy_points` (`trophy_points`),
  KEY `like_count` (`like_count`),
  KEY `register_date` (`register_date`),
  KEY `staff_username` (`is_staff`,`username`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=14 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_user_alert`
--

CREATE TABLE IF NOT EXISTS `xf_user_alert` (
  `alert_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `alerted_user_id` int(10) unsigned NOT NULL COMMENT 'User being alerted',
  `user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'User who did the action that caused the alert',
  `username` varchar(50) NOT NULL DEFAULT '' COMMENT 'Corresponds to user_id',
  `content_type` varbinary(25) NOT NULL COMMENT 'eg: trophy',
  `content_id` int(10) unsigned NOT NULL DEFAULT '0',
  `action` varbinary(25) NOT NULL COMMENT 'eg: edit',
  `event_date` int(10) unsigned NOT NULL,
  `view_date` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'Time when this was viewed by the alerted user',
  `extra_data` mediumblob NOT NULL COMMENT 'Serialized. Stores any extra data relevant to the alert',
  PRIMARY KEY (`alert_id`),
  KEY `alertedUserId_eventDate` (`alerted_user_id`,`event_date`),
  KEY `contentType_contentId` (`content_type`,`content_id`),
  KEY `viewDate_eventDate` (`view_date`,`event_date`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_user_alert_optout`
--

CREATE TABLE IF NOT EXISTS `xf_user_alert_optout` (
  `user_id` int(10) unsigned NOT NULL,
  `alert` varbinary(50) NOT NULL,
  PRIMARY KEY (`user_id`,`alert`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_user_authenticate`
--

CREATE TABLE IF NOT EXISTS `xf_user_authenticate` (
  `user_id` int(10) unsigned NOT NULL,
  `scheme_class` varchar(75) NOT NULL,
  `data` mediumblob NOT NULL,
  `remember_key` varbinary(40) NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_user_ban`
--

CREATE TABLE IF NOT EXISTS `xf_user_ban` (
  `user_id` int(10) unsigned NOT NULL,
  `ban_user_id` int(10) unsigned NOT NULL,
  `ban_date` int(10) unsigned NOT NULL DEFAULT '0',
  `end_date` int(10) unsigned NOT NULL DEFAULT '0',
  `user_reason` varchar(255) NOT NULL,
  `triggered` tinyint(3) unsigned NOT NULL DEFAULT '1',
  PRIMARY KEY (`user_id`),
  KEY `ban_date` (`ban_date`),
  KEY `end_date` (`end_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_user_confirmation`
--

CREATE TABLE IF NOT EXISTS `xf_user_confirmation` (
  `user_id` int(10) unsigned NOT NULL,
  `confirmation_type` varchar(25) NOT NULL,
  `confirmation_key` varchar(16) NOT NULL,
  `confirmation_date` int(10) unsigned NOT NULL,
  PRIMARY KEY (`user_id`,`confirmation_type`),
  KEY `confirmation_date` (`confirmation_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_user_external_auth`
--

CREATE TABLE IF NOT EXISTS `xf_user_external_auth` (
  `user_id` int(10) unsigned NOT NULL,
  `provider` varbinary(25) NOT NULL,
  `provider_key` varbinary(150) NOT NULL,
  `extra_data` mediumblob NOT NULL,
  PRIMARY KEY (`user_id`,`provider`),
  UNIQUE KEY `provider` (`provider`,`provider_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_user_field`
--

CREATE TABLE IF NOT EXISTS `xf_user_field` (
  `field_id` varbinary(25) NOT NULL,
  `display_group` enum('personal','contact','preferences') NOT NULL DEFAULT 'personal',
  `display_order` int(10) unsigned NOT NULL DEFAULT '1',
  `field_type` enum('textbox','textarea','select','radio','checkbox','multiselect') NOT NULL DEFAULT 'textbox',
  `field_choices` blob NOT NULL,
  `match_type` enum('none','number','alphanumeric','email','url','regex','callback') NOT NULL DEFAULT 'none',
  `match_regex` varchar(250) NOT NULL DEFAULT '',
  `match_callback_class` varchar(75) NOT NULL DEFAULT '',
  `match_callback_method` varchar(75) NOT NULL DEFAULT '',
  `max_length` int(10) unsigned NOT NULL DEFAULT '0',
  `required` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `show_registration` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `user_editable` enum('yes','once','never') NOT NULL DEFAULT 'yes',
  `viewable_profile` tinyint(4) NOT NULL DEFAULT '1',
  `viewable_message` tinyint(4) NOT NULL DEFAULT '0',
  `display_template` text NOT NULL,
  PRIMARY KEY (`field_id`),
  KEY `display_group_order` (`display_group`,`display_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_user_field_value`
--

CREATE TABLE IF NOT EXISTS `xf_user_field_value` (
  `user_id` int(10) unsigned NOT NULL,
  `field_id` varbinary(25) NOT NULL,
  `field_value` mediumtext NOT NULL,
  PRIMARY KEY (`user_id`,`field_id`),
  KEY `field_id` (`field_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_user_follow`
--

CREATE TABLE IF NOT EXISTS `xf_user_follow` (
  `user_id` int(10) unsigned NOT NULL,
  `follow_user_id` int(10) unsigned NOT NULL COMMENT 'User being followed',
  `follow_date` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`user_id`,`follow_user_id`),
  KEY `follow_user_id` (`follow_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_user_group`
--

CREATE TABLE IF NOT EXISTS `xf_user_group` (
  `user_group_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(50) NOT NULL,
  `display_style_priority` int(10) unsigned NOT NULL DEFAULT '0',
  `username_css` text NOT NULL,
  `user_title` varchar(100) NOT NULL DEFAULT '',
  `banner_css_class` varchar(75) NOT NULL DEFAULT '',
  `banner_text` varchar(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`user_group_id`),
  KEY `title` (`title`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_user_group_change`
--

CREATE TABLE IF NOT EXISTS `xf_user_group_change` (
  `user_id` int(10) unsigned NOT NULL,
  `change_key` varbinary(50) NOT NULL,
  `group_ids` varbinary(255) NOT NULL,
  PRIMARY KEY (`user_id`,`change_key`),
  KEY `change_key` (`change_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_user_group_promotion`
--

CREATE TABLE IF NOT EXISTS `xf_user_group_promotion` (
  `promotion_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(100) NOT NULL,
  `active` tinyint(4) NOT NULL DEFAULT '1',
  `user_criteria` mediumblob NOT NULL,
  `extra_user_group_ids` varbinary(255) NOT NULL,
  PRIMARY KEY (`promotion_id`),
  KEY `title` (`title`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_user_group_promotion_log`
--

CREATE TABLE IF NOT EXISTS `xf_user_group_promotion_log` (
  `promotion_id` int(10) unsigned NOT NULL,
  `user_id` int(10) unsigned NOT NULL,
  `promotion_date` int(10) unsigned NOT NULL,
  `promotion_state` enum('automatic','manual','disabled') NOT NULL DEFAULT 'automatic',
  PRIMARY KEY (`promotion_id`,`user_id`),
  KEY `promotion_date` (`promotion_date`),
  KEY `user_id_date` (`user_id`,`promotion_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_user_group_relation`
--

CREATE TABLE IF NOT EXISTS `xf_user_group_relation` (
  `user_id` int(10) unsigned NOT NULL,
  `user_group_id` int(10) unsigned NOT NULL,
  `is_primary` tinyint(3) unsigned NOT NULL,
  PRIMARY KEY (`user_id`,`user_group_id`),
  KEY `user_group_id_is_primary` (`user_group_id`,`is_primary`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_user_ignored`
--

CREATE TABLE IF NOT EXISTS `xf_user_ignored` (
  `user_id` int(10) unsigned NOT NULL,
  `ignored_user_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`user_id`,`ignored_user_id`),
  KEY `ignored_user_id` (`ignored_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_user_news_feed_cache`
--

CREATE TABLE IF NOT EXISTS `xf_user_news_feed_cache` (
  `user_id` int(10) unsigned NOT NULL,
  `news_feed_cache` mediumblob NOT NULL COMMENT 'Serialized. Contains fetched, parsed news_feed items for user_id',
  `news_feed_cache_date` int(10) unsigned NOT NULL COMMENT 'Date at which the cache was last refreshed',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_user_option`
--

CREATE TABLE IF NOT EXISTS `xf_user_option` (
  `user_id` int(10) unsigned NOT NULL,
  `show_dob_year` tinyint(3) unsigned NOT NULL DEFAULT '1' COMMENT 'Show date of month year (thus: age)',
  `show_dob_date` tinyint(3) unsigned NOT NULL DEFAULT '1' COMMENT 'Show date of birth day and month',
  `content_show_signature` tinyint(3) unsigned NOT NULL DEFAULT '1' COMMENT 'Show user''s signatures with content',
  `receive_admin_email` tinyint(3) unsigned NOT NULL DEFAULT '1',
  `email_on_conversation` tinyint(3) unsigned NOT NULL DEFAULT '1' COMMENT 'Receive an email upon receiving a conversation message',
  `is_discouraged` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT 'If non-zero, this user will be subjected to annoying random system failures.',
  `default_watch_state` enum('','watch_no_email','watch_email') NOT NULL DEFAULT '',
  `alert_optout` text NOT NULL COMMENT 'Comma-separated list of alerts from which the user has opted out. Example: ''post_like,user_trophy''',
  `enable_rte` tinyint(3) unsigned NOT NULL DEFAULT '1',
  `enable_flash_uploader` tinyint(3) unsigned NOT NULL DEFAULT '1',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_user_privacy`
--

CREATE TABLE IF NOT EXISTS `xf_user_privacy` (
  `user_id` int(10) unsigned NOT NULL,
  `allow_view_profile` enum('everyone','members','followed','none') NOT NULL DEFAULT 'everyone',
  `allow_post_profile` enum('everyone','members','followed','none') NOT NULL DEFAULT 'everyone',
  `allow_send_personal_conversation` enum('everyone','members','followed','none') NOT NULL DEFAULT 'everyone',
  `allow_view_identities` enum('everyone','members','followed','none') NOT NULL DEFAULT 'everyone',
  `allow_receive_news_feed` enum('everyone','members','followed','none') NOT NULL DEFAULT 'everyone',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_user_profile`
--

CREATE TABLE IF NOT EXISTS `xf_user_profile` (
  `user_id` int(10) unsigned NOT NULL,
  `dob_day` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `dob_month` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `dob_year` smallint(5) unsigned NOT NULL DEFAULT '0',
  `status` text NOT NULL,
  `status_date` int(10) unsigned NOT NULL DEFAULT '0',
  `status_profile_post_id` int(10) unsigned NOT NULL DEFAULT '0',
  `signature` text NOT NULL,
  `homepage` text NOT NULL,
  `location` varchar(50) NOT NULL DEFAULT '',
  `occupation` varchar(50) NOT NULL DEFAULT '',
  `following` text NOT NULL COMMENT 'Comma-separated integers from xf_user_follow',
  `ignored` text NOT NULL COMMENT 'Comma-separated integers from xf_user_ignored',
  `csrf_token` varchar(40) NOT NULL COMMENT 'Anti CSRF data key',
  `avatar_crop_x` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'X-Position from which to start the square crop on the m avatar',
  `avatar_crop_y` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'Y-Position from which to start the square crop on the m avatar',
  `about` text NOT NULL,
  `facebook_auth_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `custom_fields` mediumblob NOT NULL,
  PRIMARY KEY (`user_id`),
  KEY `dob` (`dob_month`,`dob_day`,`dob_year`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_user_status`
--

CREATE TABLE IF NOT EXISTS `xf_user_status` (
  `profile_post_id` int(10) unsigned NOT NULL,
  `user_id` int(10) unsigned NOT NULL,
  `post_date` int(10) unsigned NOT NULL,
  PRIMARY KEY (`profile_post_id`),
  KEY `post_date` (`post_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_user_trophy`
--

CREATE TABLE IF NOT EXISTS `xf_user_trophy` (
  `user_id` int(10) unsigned NOT NULL,
  `trophy_id` int(10) unsigned NOT NULL,
  `award_date` int(10) unsigned NOT NULL,
  PRIMARY KEY (`trophy_id`,`user_id`),
  KEY `user_id_award_date` (`user_id`,`award_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_user_upgrade`
--

CREATE TABLE IF NOT EXISTS `xf_user_upgrade` (
  `user_upgrade_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(50) NOT NULL,
  `description` text NOT NULL,
  `display_order` int(10) unsigned NOT NULL DEFAULT '0',
  `extra_group_ids` varbinary(255) NOT NULL DEFAULT '',
  `recurring` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `cost_amount` decimal(10,2) unsigned NOT NULL,
  `cost_currency` varchar(3) NOT NULL,
  `length_amount` tinyint(3) unsigned NOT NULL,
  `length_unit` enum('day','month','year','') NOT NULL DEFAULT '',
  `disabled_upgrade_ids` varbinary(255) NOT NULL DEFAULT '',
  `can_purchase` tinyint(3) unsigned NOT NULL DEFAULT '1',
  PRIMARY KEY (`user_upgrade_id`),
  KEY `display_order` (`display_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_user_upgrade_active`
--

CREATE TABLE IF NOT EXISTS `xf_user_upgrade_active` (
  `user_upgrade_record_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL,
  `user_upgrade_id` int(10) unsigned NOT NULL,
  `extra` mediumblob NOT NULL,
  `start_date` int(10) unsigned NOT NULL,
  `end_date` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`user_upgrade_record_id`),
  UNIQUE KEY `user_id_upgrade_id` (`user_id`,`user_upgrade_id`),
  KEY `end_date` (`end_date`),
  KEY `start_date` (`start_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_user_upgrade_expired`
--

CREATE TABLE IF NOT EXISTS `xf_user_upgrade_expired` (
  `user_upgrade_record_id` int(10) unsigned NOT NULL,
  `user_id` int(10) unsigned NOT NULL,
  `user_upgrade_id` int(10) unsigned NOT NULL,
  `extra` mediumblob NOT NULL,
  `start_date` int(10) unsigned NOT NULL,
  `end_date` int(10) unsigned NOT NULL DEFAULT '0',
  `original_end_date` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`user_upgrade_record_id`),
  KEY `end_date` (`end_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_user_upgrade_log`
--

CREATE TABLE IF NOT EXISTS `xf_user_upgrade_log` (
  `user_upgrade_log_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_upgrade_record_id` int(10) unsigned NOT NULL,
  `processor` varchar(25) NOT NULL,
  `transaction_id` varchar(50) NOT NULL,
  `subscriber_id` varchar(50) NOT NULL DEFAULT '',
  `transaction_type` enum('payment','cancel','info','error') NOT NULL,
  `message` varchar(255) NOT NULL DEFAULT '',
  `transaction_details` mediumblob NOT NULL,
  `log_date` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`user_upgrade_log_id`),
  KEY `transaction_id` (`transaction_id`),
  KEY `subscriber_id` (`subscriber_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_warning`
--

CREATE TABLE IF NOT EXISTS `xf_warning` (
  `warning_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `content_type` varbinary(25) NOT NULL,
  `content_id` int(10) unsigned NOT NULL,
  `content_title` varchar(255) NOT NULL,
  `user_id` int(10) unsigned NOT NULL,
  `warning_date` int(10) unsigned NOT NULL,
  `warning_user_id` int(10) unsigned NOT NULL,
  `warning_definition_id` int(10) unsigned NOT NULL,
  `title` varchar(255) NOT NULL,
  `notes` text NOT NULL,
  `points` smallint(5) unsigned NOT NULL,
  `expiry_date` int(10) unsigned NOT NULL,
  `is_expired` tinyint(3) unsigned NOT NULL,
  `extra_user_group_ids` varbinary(255) NOT NULL,
  PRIMARY KEY (`warning_id`),
  KEY `content_type_id` (`content_type`,`content_id`),
  KEY `user_id_date` (`user_id`,`warning_date`),
  KEY `expiry` (`expiry_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_warning_action`
--

CREATE TABLE IF NOT EXISTS `xf_warning_action` (
  `warning_action_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `points` smallint(5) unsigned NOT NULL,
  `action` enum('ban_length','ban_points','discourage','groups') NOT NULL,
  `ban_length_type` enum('permanent','days','weeks','months','years') NOT NULL,
  `ban_length` smallint(5) unsigned NOT NULL,
  `extra_user_group_ids` varbinary(255) NOT NULL,
  PRIMARY KEY (`warning_action_id`),
  KEY `points` (`points`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_warning_action_trigger`
--

CREATE TABLE IF NOT EXISTS `xf_warning_action_trigger` (
  `action_trigger_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `warning_action_id` int(10) unsigned NOT NULL,
  `user_id` int(10) unsigned NOT NULL,
  `trigger_points` smallint(5) unsigned NOT NULL,
  `action_date` int(10) unsigned NOT NULL,
  `action` enum('ban_points','discourage','groups') NOT NULL,
  `min_unban_date` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`action_trigger_id`),
  KEY `user_id_points` (`user_id`,`trigger_points`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Struktur-dump for tabellen `xf_warning_definition`
--

CREATE TABLE IF NOT EXISTS `xf_warning_definition` (
  `warning_definition_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `points_default` smallint(5) unsigned NOT NULL,
  `expiry_type` enum('never','days','weeks','months','years') NOT NULL,
  `expiry_default` smallint(5) unsigned NOT NULL,
  `extra_user_group_ids` varbinary(255) NOT NULL,
  `is_editable` tinyint(3) unsigned NOT NULL,
  PRIMARY KEY (`warning_definition_id`),
  KEY `points_default` (`points_default`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
