START TRANSACTION;

SET @created_by = 1;
SET @updated_by = 1;
SET @upload_by = 1;
SET @status = 1;
SET @now = NOW(6);

INSERT INTO menus (created_time, updated_time, code, description, icon, name, sequence, status, url, menu_group_code, created_by, updated_by, upload_by, parent_id)
VALUES (@now, @now, 'CUSTOMER_MANAGEMENT', 'Customer Management', 'fas fa-address-book', 'Customer', 20, @status, '#', 3, @created_by, @updated_by, @upload_by, NULL);
SET @customer_group_id = LAST_INSERT_ID();

INSERT INTO menus (created_time, updated_time, code, description, icon, name, sequence, status, url, menu_group_code, created_by, updated_by, upload_by, parent_id)
VALUES (@now, @now, 'CUSTOMER_LIST', 'Customers', NULL, 'Customers', 10, @status, '/customers', 3, @created_by, @updated_by, @upload_by, @customer_group_id);

INSERT INTO menus (created_time, updated_time, code, description, icon, name, sequence, status, url, menu_group_code, created_by, updated_by, upload_by, parent_id)
VALUES (@now, @now, 'PRODUCT_MANAGEMENT', 'Product Management', 'fas fa-box', 'Product', 25, @status, '#', 3, @created_by, @updated_by, @upload_by, NULL);
SET @product_group_id = LAST_INSERT_ID();

INSERT INTO menus (created_time, updated_time, code, description, icon, name, sequence, status, url, menu_group_code, created_by, updated_by, upload_by, parent_id) VALUES
(@now, @now, 'PRODUCT_LIST', 'Products', 'fas fa-box', 'Products', 10, @status, '/products', 3, @created_by, @updated_by, @upload_by, @product_group_id),
(@now, @now, 'PRODUCT_FEATURE_LIST', 'Product Features', 'fas fa-list', 'Product Features', 20, @status, '/product-features', 3, @created_by, @updated_by, @upload_by, @product_group_id);

INSERT INTO menus (created_time, updated_time, code, description, icon, name, sequence, status, url, menu_group_code, created_by, updated_by, upload_by, parent_id)
VALUES (@now, @now, 'LICENSE_MANAGEMENT', 'License Management', 'fas fa-key', 'License', 30, @status, '#', 3, @created_by, @updated_by, @upload_by, NULL);
SET @license_group_id = LAST_INSERT_ID();

INSERT INTO menus (created_time, updated_time, code, description, icon, name, sequence, status, url, menu_group_code, created_by, updated_by, upload_by, parent_id) VALUES
(@now, @now, 'LICENSE_PLAN_LIST', 'License Plans', 'fas fa-layer-group', 'License Plans', 10, @status, '/license-plans', 3, @created_by, @updated_by, @upload_by, @license_group_id),
(@now, @now, 'LICENSE_LIST', 'Licenses', 'fas fa-certificate', 'Licenses', 20, @status, '/licenses', 3, @created_by, @updated_by, @upload_by, @license_group_id);

COMMIT;

INSERT INTO menus_roles_access (created_time, updated_time, can_access, can_delete, can_edit, can_view, is_default, permission_priority, created_by, updated_by, upload_by, menu_id, role_id)
SELECT NOW(6), NOW(6), 1, 1, 1, 1, 0, 1, 1, 1, 1, m.id, 1
FROM menus m
WHERE m.code IN ('CUSTOMER_MANAGEMENT', 'CUSTOMER_LIST', 'PRODUCT_MANAGEMENT', 'PRODUCT_LIST', 'PRODUCT_FEATURE_LIST', 'LICENSE_MANAGEMENT', 'LICENSE_PLAN_LIST', 'LICENSE_LIST');