-- 1. cards 테이블 수정
ALTER TABLE public.cards ADD COLUMN resource_id BIGINT;

ALTER TABLE public.cards
    ADD CONSTRAINT fk_cards_resource_id FOREIGN KEY (resource_id) REFERENCES public.resources (id);

CREATE INDEX idx_cards_resource_id ON public.cards (resource_id);

-- 2. products 테이블 수정
ALTER TABLE public.products ADD COLUMN resource_id BIGINT;

ALTER TABLE public.products
    ADD CONSTRAINT fk_products_resource_id FOREIGN KEY (resource_id) REFERENCES public.resources (id);

CREATE INDEX idx_products_resource_id ON public.products (resource_id);

-- 2-1. 사용하지 않는 레거시 컬럼 삭제
ALTER TABLE public.products
    DROP COLUMN IF EXISTS price,
DROP COLUMN IF EXISTS option,
    DROP COLUMN IF EXISTS is_similar;
