SET SQL_SAFE_UPDATES = 0;

START TRANSACTION;

SET @created_by = 1;
SET @updated_by = 1;
SET @upload_by = 1;
SET @status = 1;
SET @now = NOW(6);

INSERT INTO menus (created_time, updated_time, code, description, icon, menu_group_code, name, sequence, status, url, created_by, updated_by, upload_by, parent_id)
VALUES (@now, @now, 'REPORTS_MANAGEMENT', 'Reports', 'fas fa-chart-bar', 4, 'Reports', 90, @status, '#', @created_by, @updated_by, @upload_by, NULL);
SET @reports_group_id = LAST_INSERT_ID();

INSERT INTO menus (created_time, updated_time, code, description, icon, menu_group_code, name, sequence, status, url, created_by, updated_by, upload_by, parent_id)
VALUES (@now, @now, 'CUSTOMER_PURCHASE_REPORT', 'Customer Purchase Report', NULL, 4, 'Customer Purchase Report', 10, @status, '/reports/customer-purchases', @created_by, @updated_by, @upload_by, @reports_group_id);

COMMIT;

INSERT INTO menus_roles_access (created_time, updated_time, can_access, can_delete, can_edit, can_view, is_default, permission_priority, created_by, updated_by, upload_by, menu_id, role_id)
SELECT NOW(6), NOW(6), 1, 1, 1, 1, 0, 1, 1, 1, 1, m.id, 1
FROM menus m
WHERE m.code IN ('REPORTS_MANAGEMENT', 'CUSTOMER_PURCHASE_REPORT');

SET SQL_SAFE_UPDATES = 1;