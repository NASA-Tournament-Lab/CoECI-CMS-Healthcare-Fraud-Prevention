CREATE TRIGGER user_insert AFTER INSERT ON `user`
  FOR EACH ROW
  BEGIN
    INSERT INTO h_user (id, is_deleted, original_id, username, role_id, auto_retrieve_cached_data, created_by, updated_by, created_date, updated_date, password_hash, organization_name) VALUES (UUID(), 0, NEW.id, NEW.username, NEW.role_id, NEW.auto_retrieve_cached_data, NEW.created_by, NEW.updated_by, NEW.created_date, NEW.updated_date, NEW.password_hash, NEW.organization_name);
  END;;

CREATE TRIGGER user_update AFTER UPDATE ON `user`
  FOR EACH ROW
  BEGIN
  	IF (NEW.is_deleted = 0) THEN
        INSERT INTO h_user (id, is_deleted, original_id, username, role_id, auto_retrieve_cached_data, created_by, updated_by, created_date, updated_date, password_hash, organization_name) VALUES (UUID(), 0, NEW.id, NEW.username, NEW.role_id, NEW.auto_retrieve_cached_data, NEW.created_by, NEW.updated_by, NEW.created_date, NEW.updated_date, NEW.password_hash, NEW.organization_name);
    ELSE
        INSERT INTO h_user (id, is_deleted, original_id, username, role_id, auto_retrieve_cached_data, created_by, updated_by, created_date, updated_date, password_hash, organization_name) VALUES (UUID(), 1, NEW.id, NEW.username, NEW.role_id, NEW.auto_retrieve_cached_data, NEW.created_by, NEW.updated_by, NEW.created_date, NEW.updated_date, NEW.password_hash, NEW.organization_name);
    END IF;
  END;;

CREATE TRIGGER user_delete AFTER DELETE ON `user`
  FOR EACH ROW
  BEGIN
    INSERT INTO h_user (id, is_deleted, original_id, username, role_id, auto_retrieve_cached_data, created_by, updated_by, created_date, updated_date, password_hash, organization_name) VALUES (UUID(), 1, OLD.id, OLD.username, OLD.role_id, OLD.auto_retrieve_cached_data, OLD.created_by, OLD.updated_by, OLD.created_date, OLD.updated_date, OLD.password_hash, OLD.organization_name);
  END;;
