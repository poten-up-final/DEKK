-- 1. cards 테이블 제약 조건 수정 (origin_id, platform)
ALTER TABLE public.cards ALTER COLUMN origin_id DROP NOT NULL;
ALTER TABLE public.cards ALTER COLUMN platform DROP NOT NULL;

-- 2. products 테이블 제약 조건 수정 (origin_id)
ALTER TABLE public.products ALTER COLUMN origin_id DROP NOT NULL;
