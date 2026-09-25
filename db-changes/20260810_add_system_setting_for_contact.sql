INSERT INTO system_settings
(created_time, updated_time, code, description, editable_status, sequence, value, created_by, updated_by, upload_by, input_type, icon)
VALUES
(NOW(), NOW(), 'INSTAGRAM_LINK', 'FOR INSTAGRAM LINK', 1, 1, 'https://instagram.com/', NULL, NULL, NULL, 1, 'fab fa-instagram'),
(NOW(), NOW(), 'MESSENGER_LINK', 'FOR MESSENGER LINK', 1, 1, 'https://m.me/', NULL, NULL, NULL, 1, 'fab fa-facebook-messenger');