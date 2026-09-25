START TRANSACTION;

SET @created_by = 1;
SET @updated_by = 1;
SET @upload_by = 1;
SET @status = 1;
SET @now = NOW(6);

INSERT INTO menus (created_time, updated_time, code, description, icon, name, sequence, status, url, created_by, updated_by, upload_by, parent_id)
VALUES (@now, @now, 'DASHBOARD', 'Dashboard Overview', 'fas fa-chart-pie', 'Dashboard', 10, @status, '/', @created_by, @updated_by, @upload_by, NULL);

INSERT INTO menus (created_time, updated_time, code, description, icon, name, sequence, status, url, created_by, updated_by, upload_by, parent_id)
VALUES (@now, @now, 'USER_CONTROL', 'User Control', 'fas fa-user-shield', 'User Control', 70, @status, '#', @created_by, @updated_by, @upload_by, NULL);
SET @user_control_id = LAST_INSERT_ID();

INSERT INTO menus (created_time, updated_time, code, description, icon, name, sequence, status, url, created_by, updated_by, upload_by, parent_id) VALUES
(@now, @now, 'USER_LIST', 'Users', NULL, 'Users', 10, @status, '/users', @created_by, @updated_by, @upload_by, @user_control_id),
(@now, @now, 'ROLE_LIST', 'Roles', NULL, 'Roles', 20, @status, '/roles', @created_by, @updated_by, @upload_by, @user_control_id);


INSERT INTO menus (created_time, updated_time, code, description, icon, name, sequence, status, url, created_by, updated_by, upload_by, parent_id)
VALUES (@now, @now, 'SETTINGS', 'System Settings', 'fas fa-sliders-h', 'Settings', 100, @status, '/settings', @created_by, @updated_by, @upload_by, NULL);

COMMIT;