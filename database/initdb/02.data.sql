--
-- PostgreSQL database dump
--

-- Dumped from database version 15.10 (Debian 15.10-1.pgdg120+1)
-- Dumped by pg_dump version 15.10 (Debian 15.10-1.pgdg120+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Data for Name: users; Type: TABLE DATA; Schema: example; Owner: myuser
--

INSERT INTO example.users VALUES ('ea721e77-abbb-4b5a-ae92-407b282c2e9f', 'dev.test.user1@example.com', '$2a$10$V5j0oeXVqOzJ4jW.qJu4TeQEgN.XZKAhU8MbLf1kmqxW2sQv54cCK', 'DEV Test User 1', '2025-05-25 07:29:03.462223+00', '2025-05-25 07:29:03.462223+00');
INSERT INTO example.users VALUES ('959892bd-4a1d-4fd6-90a9-c3457331813f', 'dev.admin@example.com', '$2a$10$V5j0oeXVqOzJ4jW.qJu4TeQEgN.XZKAhU8MbLf1kmqxW2sQv54cCK', 'DEV Admin User', '2025-05-25 07:29:03.462223+00', '2025-05-25 07:29:03.462223+00');
INSERT INTO example.users VALUES ('9d1f0cec-8d8e-4946-bd03-9737a44c785c', 'dev.test.user2@example.com', '$2a$10$V5j0oeXVqOzJ4jW.qJu4TeQEgN.XZKAhU8MbLf1kmqxW2sQv54cCK', 'DEV Test User 2', '2025-05-25 07:29:03.462223+00', '2025-05-25 07:29:03.462223+00');


--
-- Data for Name: flyway_schema_history; Type: TABLE DATA; Schema: public; Owner: myuser
--

INSERT INTO public.flyway_schema_history VALUES (1, '1.0.0', 'init', 'SQL', 'V1_0_0__init.sql', -1276743990, 'myuser', '2025-05-25 16:29:03.431934', 12, true);
INSERT INTO public.flyway_schema_history VALUES (2, '1.0.1', 'init data', 'SQL', 'V1_0_1__init_data.sql', -1231157784, 'myuser', '2025-05-25 16:29:03.458591', 1, true);


--
-- PostgreSQL database dump complete
--

