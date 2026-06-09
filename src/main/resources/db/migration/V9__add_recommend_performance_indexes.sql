CREATE INDEX IF NOT EXISTS idx_cards_recommend_candidates
    ON cards (status, target_gender, height, weight)
    WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_cards_approved_updated_at
    ON cards (status, updated_at DESC)
    WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_card_categories_card_id
    ON card_categories (card_id)
    WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_active_logs_user_swipe
    ON active_logs (user_id, swipe_type)
    WHERE deleted_at IS NULL;
