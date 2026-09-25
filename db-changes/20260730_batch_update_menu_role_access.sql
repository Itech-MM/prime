-- Grant access to all existing menus for role_id = 1 (admin)
INSERT INTO menus_roles_access (
    created_time, 
    updated_time, 
    can_access, 
    can_delete, 
    can_edit, 
    can_view, 
    is_default, 
    permission_priority, 
    created_by, 
    updated_by, 
    upload_by, 
    menu_id, 
    role_id
)
SELECT 
    NOW(6), 
    NOW(6), 
    1, 
    1, 
    1, 
    1, 
    0, 
    1, 
    1, 
    1, 
    1, 
    m.id, 
    1
FROM menus m;

-- Insert new "Menu Role Access" submenu under USER_CONTROL
INSERT INTO menus (
    created_time, 
    updated_time, 
    code, 
    description, 
    icon, 
    menu_group_code, 
    name, 
    sequence, 
    status, 
    url, 
    created_by, 
    updated_by, 
    upload_by, 
    parent_id
)
SELECT 
    NOW(6), 
    NOW(6), 
    'USER_MANAGEMENT_MENU_ACCESS_MANAGE', 
    'Menu Role Access Management', 
    NULL, 
    NULL,
    'Menu Role Access', 
    30, 
    1, 
    '/menu-role-access', 
    1, 
    1, 
    1, 
    id 
FROM menus 
WHERE code = 'USER_CONTROL';

-- Grant access to the newly created menu for role_id = 1 (admin)
INSERT INTO menus_roles_access (
    created_time, 
    updated_time, 
    can_access, 
    can_delete, 
    can_edit, 
    can_view, 
    is_default, 
    permission_priority, 
    created_by, 
    updated_by, 
    upload_by, 
    menu_id, 
    role_id
)
SELECT 
    NOW(6), 
    NOW(6), 
    1, 
    1, 
    1, 
    1, 
    0, 
    1, 
    1, 
    1, 
    1, 
    id, 
    1
FROM menus 
WHERE code = 'USER_MANAGEMENT_MENU_ACCESS_MANAGE';

-- Activate all roles
UPDATE roles SET status = 1;