INSERT INTO menus (code, name, description, icon, url, menu_group_code, sequence, status, display_status, parent_id, created_time, updated_time)
VALUES ('INV_WAREHOUSE', 'Warehouse Management', 'Manage warehouses and their storage locations', 'fas fa-warehouse', '/inventory/warehouses', 5, 2, 1, 1, @inv_mgmt_menu_id, NOW(), NOW());

SET @inv_warehouse_menu_id = LAST_INSERT_ID();

INSERT INTO menus (code, name, description, icon, url, menu_group_code, sequence, status, display_status, parent_id, created_time, updated_time)
VALUES ('INV_LOCATION', 'Locations tab access within Warehouse Management', NULL, NULL, NULL, 5, 1, 1, 2, @inv_warehouse_menu_id, NOW(), NOW());

SET @inv_location_menu_id = LAST_INSERT_ID();

INSERT INTO menus (code, name, description, icon, url, menu_group_code, sequence, status, display_status, parent_id, created_time, updated_time)
VALUES ('INV_SUPPLIER', 'Suppliers', 'Manage suppliers', 'fas fa-truck', '/inventory/suppliers', 5, 3, 1, 1, @inv_mgmt_menu_id, NOW(), NOW());

SET @inv_supplier_menu_id = LAST_INSERT_ID();

INSERT INTO menus_roles_access (menu_id, role_id, can_access, can_view, can_edit, can_delete, is_default, permission_priority, created_time, updated_time)
SELECT @inv_warehouse_menu_id, mra.role_id, mra.can_access, mra.can_view, mra.can_edit, mra.can_delete, mra.is_default, mra.permission_priority, NOW(), NOW()
FROM menus_roles_access mra
JOIN menus m ON m.id = mra.menu_id
WHERE m.code = 'INV_ITEM';

INSERT INTO menus_roles_access (menu_id, role_id, can_access, can_view, can_edit, can_delete, is_default, permission_priority, created_time, updated_time)
SELECT @inv_location_menu_id, mra.role_id, mra.can_access, mra.can_view, mra.can_edit, mra.can_delete, mra.is_default, mra.permission_priority, NOW(), NOW()
FROM menus_roles_access mra
JOIN menus m ON m.id = mra.menu_id
WHERE m.code = 'INV_ITEM';

INSERT INTO menus_roles_access (menu_id, role_id, can_access, can_view, can_edit, can_delete, is_default, permission_priority, created_time, updated_time)
SELECT @inv_supplier_menu_id, mra.role_id, mra.can_access, mra.can_view, mra.can_edit, mra.can_delete, mra.is_default, mra.permission_priority, NOW(), NOW()
FROM menus_roles_access mra
JOIN menus m ON m.id = mra.menu_id
WHERE m.code = 'INV_ITEM';