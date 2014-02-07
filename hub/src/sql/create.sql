SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Table `role`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `role` (
  `id` VARCHAR(50) NOT NULL,
  `name` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `user` (
  `id` VARCHAR(50) NOT NULL,
  `username` VARCHAR(50) NOT NULL,
  `role_id` VARCHAR(50) NOT NULL,
  `auto_retrieve_cached_data` BIT NOT NULL,
  `created_by` VARCHAR(50) NULL,
  `updated_by` VARCHAR(50) NULL,
  `created_date` TIMESTAMP NOT NULL,
  `updated_date` TIMESTAMP NULL,
  `password_hash` VARCHAR(256) NOT NULL DEFAULT '',
  `organization_name` VARCHAR(50) NOT NULL,
  `is_deleted` BIT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `username_UNIQUE` (`username` ASC),
  INDEX `fk_user_role_idx` (`role_id` ASC),
  INDEX `fk_user_updated_by_idx` (`updated_by` ASC),
  INDEX `fk_user_created_by_idx` (`created_by` ASC),
  CONSTRAINT `fk_user_role`
    FOREIGN KEY (`role_id`)
    REFERENCES `role` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_created_by`
    FOREIGN KEY (`created_by`)
    REFERENCES `user` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_updated_by`
    FOREIGN KEY (`updated_by`)
    REFERENCES `user` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `data_request`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `data_request` (
  `id` VARCHAR(50) NOT NULL,
  `requester_id` VARCHAR(50) NOT NULL,
  `study_id` VARCHAR(50) NOT NULL,
  `original_requester_id` VARCHAR(50) NULL,
  `query` TEXT NOT NULL,
  `expiration_time` DATETIME NOT NULL,
  `cache_safe` BIT NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `data_request_requested_user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `data_request_requested_user` (
  `data_request_id` VARCHAR(50) NOT NULL,
  `user_id` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`data_request_id`, `user_id`),
  INDEX `fk_data_request_requested_user_data_request_idx` (`data_request_id` ASC),
  INDEX `fk_data_request_requested_user_user_idx` (`user_id` ASC),
  CONSTRAINT `fk_data_request_requested_user_data_request`
    FOREIGN KEY (`data_request_id`)
    REFERENCES `data_request` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_data_request_requested_user_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `user` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `h_user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `h_user` (
  `original_id` VARCHAR(50) NOT NULL,
  `username` VARCHAR(50) NOT NULL,
  `role_id` VARCHAR(50) NOT NULL,
  `auto_retrieve_cached_data` BIT NOT NULL,
  `created_by` VARCHAR(50) NULL,
  `updated_by` VARCHAR(50) NULL,
  `created_date` TIMESTAMP NOT NULL,
  `updated_date` TIMESTAMP NULL,
  `password_hash` VARCHAR(256) NOT NULL,
  `organization_name` VARCHAR(50) NOT NULL,
  `id` VARCHAR(50) NOT NULL,
  `is_deleted` BIT NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `data_response`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `data_response` (
  `data_request_id` VARCHAR(50) NOT NULL,
  `respondent_id` VARCHAR(50) NOT NULL,
  `response_timestamp` TIMESTAMP NOT NULL,
  `request_denied` BIT NOT NULL,
  INDEX `fk_data_response_user_idx` (`respondent_id` ASC),
  INDEX `fk_data_response_request_idx` (`data_request_id` ASC),
  CONSTRAINT `fk_data_response_user`
    FOREIGN KEY (`respondent_id`)
    REFERENCES `user` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_data_response_request`
    FOREIGN KEY (`data_request_id`)
    REFERENCES `data_request` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `user_stat`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_stat` (
  `user_id` VARCHAR(50) NOT NULL,
  `data_requests_received` INT NOT NULL DEFAULT 0,
  `data_requests_responded` INT NOT NULL DEFAULT 0,
  `data_requests_initiated` INT NOT NULL DEFAULT 0,
  `data_requests_declined` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`user_id`),
  CONSTRAINT `fk_user_stat_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `user` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `audit_record`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `audit_record` (
  `id` VARCHAR(50) NOT NULL,
  `user_id` VARCHAR(50) NOT NULL,
  `record_timestamp` TIMESTAMP NOT NULL,
  `action` VARCHAR(100) NOT NULL,
  `denied` BIT NOT NULL,
  `message` LONGTEXT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_audit_record_user_idx` (`user_id` ASC),
  CONSTRAINT `fk_audit_record_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `user` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
