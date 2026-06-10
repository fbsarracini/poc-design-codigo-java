-- Seed de dados para ambiente dev
-- Senha de todos os usuários: 12345678

DO $$
DECLARE
    v_hash TEXT := '$argon2id$v=19$m=19456,t=2,p=1$AjF/ECS4PGTtVEmLyrieSg$vySd7AK78qOn8dVKi65xBJ4WxTwSLFegcqpXRRMQfVg';

    -- users
    v_alice   BIGINT;
    v_bob     BIGINT;
    v_carol   BIGINT;
    v_dan     BIGINT;
    v_eva     BIGINT;

    -- accounts
    v_agencia BIGINT;
    v_startup BIGINT;

    -- projects
    v_site       BIGINT;
    v_app_mobile BIGINT;
    v_campanha   BIGINT;
    v_api        BIGINT;
    v_dashboard  BIGINT;

BEGIN
    -- users
    INSERT INTO users (name, email, password) VALUES ('Alice Ferreira',  'alice@example.dev',  v_hash) RETURNING id INTO v_alice;
    INSERT INTO users (name, email, password) VALUES ('Bob Oliveira',    'bob@example.dev',    v_hash) RETURNING id INTO v_bob;
    INSERT INTO users (name, email, password) VALUES ('Carol Santos',    'carol@example.dev',  v_hash) RETURNING id INTO v_carol;
    INSERT INTO users (name, email, password) VALUES ('Dan Costa',       'dan@example.dev',    v_hash) RETURNING id INTO v_dan;
    INSERT INTO users (name, email, password) VALUES ('Eva Rodrigues',   'eva@example.dev',    v_hash) RETURNING id INTO v_eva;

    -- accounts
    INSERT INTO accounts (name) VALUES ('Agência Pixel')  RETURNING id INTO v_agencia;
    INSERT INTO accounts (name) VALUES ('Startup Verde')  RETURNING id INTO v_startup;

    -- memberships — Agência Pixel
    INSERT INTO memberships (account_id, user_id, role) VALUES (v_agencia, v_alice, 'OWNER');
    INSERT INTO memberships (account_id, user_id, role) VALUES (v_agencia, v_bob,   'ADMIN');
    INSERT INTO memberships (account_id, user_id, role) VALUES (v_agencia, v_carol, 'MEMBER');
    INSERT INTO memberships (account_id, user_id, role) VALUES (v_agencia, v_eva,   'CLIENT');

    -- memberships — Startup Verde
    INSERT INTO memberships (account_id, user_id, role) VALUES (v_startup, v_bob,   'OWNER');
    INSERT INTO memberships (account_id, user_id, role) VALUES (v_startup, v_dan,   'ADMIN');
    INSERT INTO memberships (account_id, user_id, role) VALUES (v_startup, v_alice, 'MEMBER');

    -- projects — Agência Pixel
    INSERT INTO projects (name, status, account_id) VALUES ('Redesign do Site',       'ACTIVE',   v_agencia) RETURNING id INTO v_site;
    INSERT INTO projects (name, status, account_id) VALUES ('App Mobile',             'ACTIVE',   v_agencia) RETURNING id INTO v_app_mobile;
    INSERT INTO projects (name, status, account_id) VALUES ('Campanha Q3',            'ACTIVE',   v_agencia) RETURNING id INTO v_campanha;

    -- projects — Startup Verde
    INSERT INTO projects (name, status, account_id) VALUES ('API Core',               'ACTIVE',   v_startup) RETURNING id INTO v_api;
    INSERT INTO projects (name, status, account_id) VALUES ('Dashboard Analytics',    'ACTIVE',   v_startup) RETURNING id INTO v_dashboard;

    -- project_memberships — site
    INSERT INTO project_memberships (project_id, user_id) VALUES (v_site, v_alice);
    INSERT INTO project_memberships (project_id, user_id) VALUES (v_site, v_bob);
    INSERT INTO project_memberships (project_id, user_id) VALUES (v_site, v_carol);

    -- project_memberships — app_mobile
    INSERT INTO project_memberships (project_id, user_id) VALUES (v_app_mobile, v_alice);
    INSERT INTO project_memberships (project_id, user_id) VALUES (v_app_mobile, v_bob);
    INSERT INTO project_memberships (project_id, user_id) VALUES (v_app_mobile, v_carol);

    -- project_memberships — campanha
    INSERT INTO project_memberships (project_id, user_id) VALUES (v_campanha, v_alice);
    INSERT INTO project_memberships (project_id, user_id) VALUES (v_campanha, v_carol);

    -- project_memberships — api
    INSERT INTO project_memberships (project_id, user_id) VALUES (v_api, v_bob);
    INSERT INTO project_memberships (project_id, user_id) VALUES (v_api, v_dan);
    INSERT INTO project_memberships (project_id, user_id) VALUES (v_api, v_alice);

    -- project_memberships — dashboard
    INSERT INTO project_memberships (project_id, user_id) VALUES (v_dashboard, v_bob);
    INSERT INTO project_memberships (project_id, user_id) VALUES (v_dashboard, v_dan);
    INSERT INTO project_memberships (project_id, user_id) VALUES (v_dashboard, v_alice);

    -- todos — Redesign do Site
    INSERT INTO todos (title, completed, visible_to_client, due_date, assignee_id, project_id)
    VALUES ('Criar wireframes das páginas principais',  false, false, CURRENT_DATE + 5,  v_alice, v_site);
    INSERT INTO todos (title, completed, visible_to_client, due_date, assignee_id, project_id)
    VALUES ('Revisar identidade visual',                 false, false, CURRENT_DATE + 3,  v_bob,   v_site);
    INSERT INTO todos (title, completed, visible_to_client, due_date, assignee_id, project_id)
    VALUES ('Implementar página de contato',             false, true,  CURRENT_DATE + 10, v_carol, v_site);
    INSERT INTO todos (title, completed, visible_to_client, due_date, assignee_id, project_id)
    VALUES ('Testes de responsividade mobile',           false, true,  CURRENT_DATE + 12, v_carol, v_site);
    INSERT INTO todos (title, completed, visible_to_client, due_date, assignee_id, project_id)
    VALUES ('Integrar Google Analytics',                 false, false, CURRENT_DATE + 7,  v_bob,   v_site);

    -- todos — App Mobile
    INSERT INTO todos (title, completed, visible_to_client, due_date, assignee_id, project_id)
    VALUES ('Configurar projeto React Native',           false, false, CURRENT_DATE + 2,  v_bob,         v_app_mobile);
    INSERT INTO todos (title, completed, visible_to_client, due_date, assignee_id, project_id)
    VALUES ('Tela de login',                             false, false, CURRENT_DATE + 6,  v_carol,       v_app_mobile);
    INSERT INTO todos (title, completed, visible_to_client, due_date, assignee_id, project_id)
    VALUES ('Tela de perfil do usuário',                 false, false, CURRENT_DATE + 8,  v_carol,       v_app_mobile);
    INSERT INTO todos (title, completed, visible_to_client, due_date, assignee_id, project_id)
    VALUES ('Publicar beta no TestFlight',               false, true,  CURRENT_DATE + 20, v_alice,       v_app_mobile);

    -- todos — Campanha Q3
    INSERT INTO todos (title, completed, visible_to_client, due_date, assignee_id, project_id)
    VALUES ('Criar peças para redes sociais',            false, true,  CURRENT_DATE + 4,  v_carol, v_campanha);
    INSERT INTO todos (title, completed, visible_to_client, due_date, assignee_id, project_id)
    VALUES ('Redigir copy para email marketing',         false, false, CURRENT_DATE + 3,  v_alice, v_campanha);
    INSERT INTO todos (title, completed, visible_to_client, due_date, assignee_id, project_id)
    VALUES ('Agendar publicações no buffer',             false, false, CURRENT_DATE + 5,  v_carol, v_campanha);

    -- todos — API Core
    INSERT INTO todos (title, completed, visible_to_client, due_date, assignee_id, project_id)
    VALUES ('Modelar schema de autenticação',               false, false, CURRENT_DATE + 1,  v_bob,   v_api);
    INSERT INTO todos (title, completed, visible_to_client, due_date, assignee_id, project_id)
    VALUES ('Implementar endpoint de listagem de usuários', false, false, CURRENT_DATE + 4,  v_dan,   v_api);
    INSERT INTO todos (title, completed, visible_to_client, due_date, assignee_id, project_id)
    VALUES ('Configurar Swagger',                           false, false, CURRENT_DATE + 3,  v_alice, v_api);
    INSERT INTO todos (title, completed, visible_to_client, due_date, assignee_id, project_id)
    VALUES ('Escrever testes de integração',                false, false, CURRENT_DATE + 8,  v_dan,   v_api);
    INSERT INTO todos (title, completed, visible_to_client, due_date, assignee_id, project_id)
    VALUES ('Deploy no ambiente de staging',                false, false, CURRENT_DATE + 14, v_bob,   v_api);

    -- todos — Dashboard Analytics
    INSERT INTO todos (title, completed, visible_to_client, due_date, assignee_id, project_id)
    VALUES ('Definir métricas do painel',                false, false, CURRENT_DATE + 2,  v_alice, v_dashboard);
    INSERT INTO todos (title, completed, visible_to_client, due_date, assignee_id, project_id)
    VALUES ('Criar gráficos de linha e barra',           false, false, CURRENT_DATE + 6,  v_dan,   v_dashboard);
    INSERT INTO todos (title, completed, visible_to_client, due_date, assignee_id, project_id)
    VALUES ('Filtros por data e categoria',              false, false, CURRENT_DATE + 9,  v_dan,   v_dashboard);
    INSERT INTO todos (title, completed, visible_to_client, due_date, assignee_id, project_id)
    VALUES ('Exportar relatório em PDF',                 false, false, CURRENT_DATE + 15, v_alice, v_dashboard);

END $$;
