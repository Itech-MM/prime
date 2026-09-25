CREATE TABLE IF NOT EXISTS promotions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    upload_by BIGINT,

    name VARCHAR(255) NOT NULL,
    name_mm VARCHAR(255),
    short_description VARCHAR(500),
    short_description_mm VARCHAR(500),
    long_description TEXT,
    long_description_mm TEXT,
    promotion_status INT NOT NULL DEFAULT 1,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    promotion_image VARCHAR(255),
    promotion_banner_image VARCHAR(255),
    sequence INT default 0,

    -- Foreign Key Constraints matching your @ManyToOne annotations
    CONSTRAINT fk_promotions_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_promotions_updated_by FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_promotions_upload_by FOREIGN KEY (upload_by) REFERENCES users(id) ON DELETE SET NULL
);

-- Crucial performance index for active promotion lookups
CREATE INDEX idx_promotions_lookup ON promotions (promotion_status, start_time, end_time);

ALTER TABLE promotions 
    MODIFY short_description VARCHAR(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    MODIFY short_description_mm VARCHAR(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    MODIFY long_description TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    MODIFY long_description_mm TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;