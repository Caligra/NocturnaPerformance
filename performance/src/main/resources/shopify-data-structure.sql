
CREATE TABLE shopify_product_staging (
    -- Identity
                                         upc             VARCHAR(12)     NOT NULL PRIMARY KEY,
                                         part_number     VARCHAR(255)    NOT NULL,
                                         brand           VARCHAR(255)    NOT NULL,

    -- Content
                                         name            VARCHAR(255)    NOT NULL,           -- → product title
                                         short_description VARCHAR(500)  NULL,               -- → metafield or subtitle
                                         marketing_description TEXT      NULL,               -- → product body_html

    -- Taxonomy
                                         category        VARCHAR(255)    NULL,               -- → product_type or collection tag
                                         sub_category    VARCHAR(255)    NULL,               -- → tag or secondary collection

    -- Vehicle compatibility metafields
                                         application_make        VARCHAR(255)    NULL,       -- → custom.vehicle_make
                                         application_model       TEXT            NULL,       -- → custom.vehicle_model
                                         application_year_from_to VARCHAR(255)   NULL,       -- → custom.year_range (e.g. "1999-2005")

    -- Optional: full detail for reference/debugging
                                         application_full_detail TEXT            NULL,

    -- Media
                                         media_url       TEXT            NULL,               -- → product image src

    -- Pricing
                                         list_price      DECIMAL(10,2)   NULL,               -- → variant price (use DECIMAL, not VARCHAR)

    -- Shipping dimensions (for Shopify weight/dims on variant)
                                         shipping_weight DOUBLE          NULL,               -- → merch_weight maps here
                                         shipping_length DOUBLE          NULL,
                                         shipping_width  DOUBLE          NULL,
                                         shipping_height DOUBLE          NULL,
    -- Product/merch dimensions (physical product, unpackaged)
                                         merch_length    DOUBLE  NULL,
                                         merch_width     DOUBLE  NULL,
                                         merch_height    DOUBLE  NULL,
                                         merch_weight    DOUBLE  NULL,

    -- Sync control
                                         shopify_product_id      BIGINT          NULL,       -- populated after first sync
                                         shopify_variant_id      BIGINT          NULL,
                                         sync_status     ENUM('pending','synced','error','skipped') NOT NULL DEFAULT 'pending',
                                         sync_error      TEXT            NULL,
                                         last_synced_at  DATETIME        NULL,
                                         created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                         updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                         INDEX idx_brand (brand),
                                         INDEX idx_category (category),
                                         INDEX idx_make (application_make),
                                         INDEX idx_sync_status (sync_status),
                                         INDEX idx_shopify_product_id (shopify_product_id)
);