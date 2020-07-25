INSERT INTO roles(name) VALUES('ROLE_USER');
INSERT INTO roles(name) VALUES('ROLE_ADMIN');
INSERT INTO public.users (id, email, firstname, lastname, is_locked, password, username) VALUES (1, 'admin@localhost.local', 'Admin', 'Administrator', false, '$2a$10$H66B8Z3isgnW/HmqnJDqJ.LJpaZ.qC9vOKtagpQNU.UeqmLT6k0XO', 'admin');
INSERT INTO user_roles(user_id, role_id) VALUES (1, 1), (1, 2)