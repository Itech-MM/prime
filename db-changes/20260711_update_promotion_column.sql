ALTER TABLE `promotions` 
MODIFY COLUMN `long_description` TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
MODIFY COLUMN `long_description_mm` TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL;