-- 기존의 단일 email 유니크 제약 조건 삭제
ALTER TABLE public.users DROP CONSTRAINT IF EXISTS uk6dotkott2kjsp8vw4d0m25fb7;

-- email과 provider 조합으로 새로운 유니크 제약 조건 생성
ALTER TABLE public.users ADD CONSTRAINT uk_user_email_provider UNIQUE (email, provider);

COMMENT ON CONSTRAINT uk_user_email_provider ON public.users
IS '동일한 이메일이더라도 소셜 제공자가 다르면 별개의 계정으로 가입 허용';
