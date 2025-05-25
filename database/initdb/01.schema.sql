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
-- Name: example; Type: SCHEMA; Schema: -; Owner: myuser
--

CREATE SCHEMA example;


ALTER SCHEMA example OWNER TO myuser;

--
-- Name: update_updated_at_column(); Type: FUNCTION; Schema: public; Owner: myuser
--

CREATE FUNCTION public.update_updated_at_column() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$;


ALTER FUNCTION public.update_updated_at_column() OWNER TO myuser;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: users; Type: TABLE; Schema: example; Owner: myuser
--

CREATE TABLE example.users (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    email character varying(255) NOT NULL,
    password character varying(255) NOT NULL,
    name character varying(100) NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT users_email_check CHECK (((email)::text ~* '^[A-Za-z0-9._+%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$'::text))
);


ALTER TABLE example.users OWNER TO myuser;

--
-- Name: flyway_schema_history; Type: TABLE; Schema: public; Owner: myuser
--

CREATE TABLE public.flyway_schema_history (
    installed_rank integer NOT NULL,
    version character varying(50),
    description character varying(200) NOT NULL,
    type character varying(20) NOT NULL,
    script character varying(1000) NOT NULL,
    checksum integer,
    installed_by character varying(100) NOT NULL,
    installed_on timestamp without time zone DEFAULT now() NOT NULL,
    execution_time integer NOT NULL,
    success boolean NOT NULL
);


ALTER TABLE public.flyway_schema_history OWNER TO myuser;

--
-- Name: users users_email_key; Type: CONSTRAINT; Schema: example; Owner: myuser
--

ALTER TABLE ONLY example.users
    ADD CONSTRAINT users_email_key UNIQUE (email);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: example; Owner: myuser
--

ALTER TABLE ONLY example.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: flyway_schema_history flyway_schema_history_pk; Type: CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.flyway_schema_history
    ADD CONSTRAINT flyway_schema_history_pk PRIMARY KEY (installed_rank);


--
-- Name: idx_users_email; Type: INDEX; Schema: example; Owner: myuser
--

CREATE INDEX idx_users_email ON example.users USING btree (email);


--
-- Name: flyway_schema_history_s_idx; Type: INDEX; Schema: public; Owner: myuser
--

CREATE INDEX flyway_schema_history_s_idx ON public.flyway_schema_history USING btree (success);


--
-- Name: users update_users_updated_at; Type: TRIGGER; Schema: example; Owner: myuser
--

CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON example.users FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();


--
-- PostgreSQL database dump complete
--

