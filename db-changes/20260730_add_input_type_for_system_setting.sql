ALTER TABLE `system_settings` 
ADD COLUMN `input_type` INT NULL DEFAULT 1 COMMENT 'TEXT(1, \"text\"), NUMBER(2, \"number\"), TOGGLE(3, \"toogle\"), DATE(4, \"date\"), PASSWORD(5, \"password\")' AFTER `upload_by`,
ADD COLUMN `icon` VARCHAR(50) NULL DEFAULT "" After `input_type`;

set sql_safe_updates = 0;
UPDATE system_settings SET icon = 'fas fa-clock' WHERE code = 'CART_EXPIRATION_MINUTE';
UPDATE system_settings SET icon = 'fas fa-ticket-alt' WHERE code = 'USE_WELCOME_COUPON';
UPDATE system_settings SET icon = 'fas fa-layer-group' WHERE code = 'WELCOME_COUPON_USE_LIMIT';
UPDATE system_settings SET icon = 'fas fa-calendar-alt' WHERE code = 'WELCOME_COUPON_EXPIRATION_IN_DAY';
UPDATE system_settings SET icon = 'fas fa-tags' WHERE code = 'WELCOME_COUPON_DISCOUNT_TYPE';
UPDATE system_settings SET icon = 'fas fa-percentage' WHERE code = 'WELCOME_COUPON_DISCOUNT_VALUE';
UPDATE system_settings SET icon = 'fas fa-user' WHERE code = 'CONTACT_PERSON';
UPDATE system_settings SET icon = 'fas fa-phone' WHERE code = 'CONTACT_PHONE';
UPDATE system_settings SET icon = 'fas fa-envelope' WHERE code = 'CONTACT_EMAIL';
UPDATE system_settings SET icon = 'fab fa-facebook-f' WHERE code = 'FACEBOOK_LINK';
UPDATE system_settings SET icon = 'fab fa-tiktok' WHERE code = 'TIKTOK_LINK';
UPDATE system_settings SET icon = 'fas fa-map-marker-alt' WHERE code = 'LOCATION';
UPDATE system_settings SET icon = 'fab fa-viber' WHERE code = 'VIBER_PHONE';

UPDATE system_settings SET input_type = 2 WHERE code = 'CART_EXPIRATION_MINUTE';
UPDATE system_settings SET input_type = 2 WHERE code = 'WELCOME_COUPON_USE_LIMIT';
UPDATE system_settings SET input_type = 2 WHERE code = 'WELCOME_COUPON_EXPIRATION_IN_DAY';
UPDATE system_settings SET input_type = 2 WHERE code = 'WELCOME_COUPON_DISCOUNT_VALUE';
UPDATE system_settings SET input_type = 2 WHERE code = 'WELCOME_COUPON_DISCOUNT_TYPE';

UPDATE system_settings SET input_type = 3 WHERE code = 'USE_WELCOME_COUPON';

UPDATE system_settings SET input_type = 1 WHERE code IN (
    'CONTACT_PERSON',
    'CONTACT_PHONE',
    'CONTACT_EMAIL',
    'FACEBOOK_LINK',
    'TIKTOK_LINK',
    'LOCATION',
    'VIBER_PHONE'
);

set sql_safe_updates = 1;