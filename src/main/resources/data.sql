-- 기본 리스트
INSERT INTO reminder_list (id, name, color, icon, display_order, is_default, created_at, updated_at)
VALUES (1, 'Reminders', '#007AFF', 'list.bullet', 0, true, NOW(), NOW());

-- 샘플 리마인더
INSERT INTO reminder (id, title, memo, completed, completed_at, display_order, list_id, created_at, updated_at)
VALUES (1, 'Buy groceries', 'Milk, eggs, bread', false, null, 0, 1, NOW(), NOW());

INSERT INTO reminder (id, title, memo, completed, completed_at, display_order, list_id, created_at, updated_at)
VALUES (2, 'Read a book', 'Clean Code by Robert C. Martin', false, null, 1, 1, NOW(), NOW());

INSERT INTO reminder (id, title, memo, completed, completed_at, display_order, list_id, created_at, updated_at)
VALUES (3, 'Go for a walk', null, false, null, 2, 1, NOW(), NOW());

INSERT INTO reminder (id, title, memo, completed, completed_at, display_order, list_id, created_at, updated_at)
VALUES (4, 'Call dentist', 'Schedule annual checkup', false, null, 3, 1, NOW(), NOW());

INSERT INTO reminder (id, title, memo, completed, completed_at, display_order, list_id, created_at, updated_at)
VALUES (5, 'Update resume', null, false, null, 4, 1, NOW(), NOW());

-- IDENTITY 시퀀스 재설정 (새 INSERT 시 PK 충돌 방지)
ALTER TABLE reminder_list ALTER COLUMN id RESTART WITH 100;
ALTER TABLE reminder ALTER COLUMN id RESTART WITH 100;
