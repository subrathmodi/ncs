-- -- Clean up any malformed previous inserts
-- DELETE FROM users WHERE username = 'admin_ncs';
--
-- -- Insert a fresh, pristine record with a verified BCrypt hash for 'admin123'
-- INSERT INTO users (username, password, full_name, role)
-- VALUES ('admin_ncs', '$2a$12$R9h/lSpx7wtykZkM6QpXseY3r8m86bN4K7gN0mY1aC1rGfQ.3M7vG', 'Nalanda Convent Admin', 'ADMIN');

-- [cite: 70]
-- Seed a Master Admin
-- Delete existing test users to clear any wrong hashes
DELETE FROM users WHERE username IN ('admin_user', 'operator_user');

-- Insert clean test users with the exact hash for 'password123'
INSERT INTO users (username, password, role)
VALUES ('admin_user', '$2a$10$2YMc7v1RI0W4qxQYrApFTuJHUrYqQe8Fa6xp/SrZtm640sH9qBewm', 'ADMIN');

INSERT INTO users (username, password, role)
VALUES ('operator_user', '$2a$10$2YMc7v1RI0W4qxQYrApFTuJHUrYqQe8Fa6xp/SrZtm640sH9qBewm', 'OPERATOR');

-- INSERT INTO academic_sessions (session_name, is_current)
-- VALUES ('2026-2027', true)
-- ON CONFLICT (session_name) DO NOTHING;

-- Seed Core Scholastic Exam Subjects
INSERT INTO subjects (subject_name, is_co_scholastic) VALUES
('English', false), ('Hindi', false), ('Sanskrit', false),
('Mathematics', false), ('Environmental Study', false),
('Science', false), ('Social Science', false), ('Computer', false),
('General Knowledge', false), ('English Oral', false),
('Maths Oral', false), ('Hindi Oral', false)
ON CONFLICT (subject_name) DO NOTHING;

-- Seed Co-Scholastic Metrics Checklist[cite: 2]
INSERT INTO subjects (subject_name, is_co_scholastic) VALUES
('Reading Skill', true), ('Writing Skill', true), ('Speaking Skill', true),
('Conversational Skill', true), ('Project Work', true), ('Regularity', true),
('Punctuality', true), ('Disciplines', true), ('P.H.E.', true),
('Sport Activity', true), ('Cultural Activities', true), ('Cleanliness', true),
('Out Door Activity', true), ('Creative Art', true), ('Social Activity', true)
ON CONFLICT (subject_name) DO NOTHING;