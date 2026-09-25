ALTER TABLE menus 
ADD COLUMN menu_group_code INT DEFAULT NULL COMMENT '1: Core, 2: Operations, 3: Managements, 4: Reports';

UPDATE menus 
SET menu_group_code = 1 
WHERE id = 1;

UPDATE menus 
SET menu_group_code = 3 
WHERE id IN (2, 3, 4, 6);

UPDATE menus 
SET menu_group_code = 4 
WHERE id = 5;