INSERT INTO menus (code, name, description, icon, url, menu_group_code, sequence, status, display_status, parent_id, created_time, updated_time)
VALUES ('INV_STOCK_REPORT', 'Stock Reports', 'Stock on hand and stock card movement history', 'fas fa-chart-bar', '/inventory/reports', 4, 20, 1, 1, 16, NOW(), NOW());

SET @inv_stock_report_menu_id = LAST_INSERT_ID();

INSERT INTO menus (code, name, description, icon, url, menu_group_code, sequence, status, display_status, parent_id, created_time, updated_time)
VALUES
('INV_STOCK_ON_HAND', 'Stock On Hand tab access within Stock Reports', NULL, NULL, NULL, 4, 1, 1, 2, @inv_stock_report_menu_id, NOW(), NOW()),
('INV_STOCK_CARD', 'Stock Card tab access within Stock Reports', NULL, NULL, NULL, 4, 2, 1, 2, @inv_stock_report_menu_id, NOW(), NOW());

SET @inv_stock_on_hand_menu_id = (SELECT id FROM menus WHERE code = 'INV_STOCK_ON_HAND');
SET @inv_stock_card_menu_id = (SELECT id FROM menus WHERE code = 'INV_STOCK_CARD');

INSERT INTO menus_roles_access (menu_id, role_id, can_access, can_view, can_edit, can_delete, is_default, permission_priority, created_time, updated_time)
SELECT @inv_stock_report_menu_id, mra.role_id, mra.can_access, mra.can_view, 0, 0, mra.is_default, mra.permission_priority, NOW(), NOW()
FROM menus_roles_access mra
JOIN menus m ON m.id = mra.menu_id
WHERE m.code = 'INV_ITEM';

INSERT INTO menus_roles_access (menu_id, role_id, can_access, can_view, can_edit, can_delete, is_default, permission_priority, created_time, updated_time)
SELECT @inv_stock_on_hand_menu_id, mra.role_id, mra.can_access, mra.can_view, 0, 0, mra.is_default, mra.permission_priority, NOW(), NOW()
FROM menus_roles_access mra
JOIN menus m ON m.id = mra.menu_id
WHERE m.code = 'INV_ITEM';

INSERT INTO menus_roles_access (menu_id, role_id, can_access, can_view, can_edit, can_delete, is_default, permission_priority, created_time, updated_time)
SELECT @inv_stock_card_menu_id, mra.role_id, mra.can_access, mra.can_view, 0, 0, mra.is_default, mra.permission_priority, NOW(), NOW()
FROM menus_roles_access mra
JOIN menus m ON m.id = mra.menu_id
WHERE m.code = 'INV_ITEM';