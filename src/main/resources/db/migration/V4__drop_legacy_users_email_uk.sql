-- 환경별 이름 불일치로 제거되지 않은 email 단독 UK 제거
ALTER TABLE users DROP CONSTRAINT IF EXISTS uc_users_email;
ALTER TABLE users DROP CONSTRAINT IF EXISTS uk6dotkott2kjsp8vw4d0m25fb7;
