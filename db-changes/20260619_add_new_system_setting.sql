INSERT INTO `system_settings` (`created_time`, `updated_time`, `code`, `description`, `editable_status`, `sequence`, `value`) VALUES (NOW(), NOW(), 'USE_WELCOME_COUPON', 'New Registration coupon (1=use, 0=not use)', '1', '2', '1');
INSERT INTO `system_settings` (`created_time`, `updated_time`, `code`, `description`, `editable_status`, `sequence`, `value`) VALUES (NOW(), NOW(), 'WELCOME_COUPON_USE_LIMIT', 'Welcome coupon use limits', '1', '2', '1');
INSERT INTO `system_settings` (`created_time`, `updated_time`, `code`, `description`, `editable_status`, `sequence`, `value`) VALUES (NOW(), NOW(), 'WELCOME_COUPON_EXPIRATION_IN_DAY', 'Welcome coupon expiration in day', '1', '2', '1');
INSERT INTO `system_settings` (`created_time`, `updated_time`, `code`, `description`, `editable_status`, `sequence`, `value`) 
VALUES (NOW(), NOW(), 'WELCOME_COUPON_DISCOUNT_TYPE', 'Welcome coupon discount type (1=Amount, 2=Percentage)', '1', '2', '1');

INSERT INTO `system_settings` (`created_time`, `updated_time`, `code`, `description`, `editable_status`, `sequence`, `value`) 
VALUES (NOW(), NOW(), 'WELCOME_COUPON_DISCOUNT_VALUE', 'Welcome coupon discount value', '1', '2', '10.00');