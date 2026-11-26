# Sistema de Gestão de Consultas e Registo Clínico

Protótipo web de gestão de consultas e registo clínico, desenvolvido como monografia de licenciatura em Engenharia Informática e de Telecomunicações (Universidade Politécnica de Maputo, 2026) para o **Centro de Saúde Dream Sant'Egídio, Maputo**.

🇵🇹 Português · [🇬🇧 English below](#english)

## O problema

No centro de saúde era possível encerrar consultas sem registar o aconselhamento clínico, e a gestão de faltas era limitada. Os requisitos foram levantados junto de 11 funcionários (inquérito, entrevista semi-estruturada e observação directa), e o sistema foi modelado em UML antes da implementação.

## Funcionalidades

- **Gestão de utilizadores e perfis:** três perfis (Administrador, Médico e Paciente) com acesso limitado ao que cada um precisa.
- **Agenda e consultas:** agenda por médico e especialidade com controlo de vagas. O sistema **impede a dupla marcação** do mesmo horário. Estados da consulta: Agendada, Concluída e Cancelada.
- **Atendimento clínico:** observações, exames, diagnóstico e medicação, em 3 etapas. Estados: Pendente, Aguarda Exames e Concluído. Um atendimento concluído fica só de leitura (apenas o administrador pode alterá-lo).
- **Exames:** tipos como hemograma, glicemia, raio-X e ecografia, com resultado e ficheiro anexado (PDF, JPG ou PNG).
- **Aconselhamento clínico:** registo do aconselhamento associado ao atendimento.
- **Histórico clínico:** vista consolidada do paciente (consultas, atendimentos, exames e aconselhamentos).
- **Presenças:** registo de presença, ausência ou ausência justificada.
- **Dashboards** por perfil.

## Tecnologias

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 17 |
| Framework | Spring Boot 3.2.4 (Spring MVC, Spring Data JPA, Bean Validation) |
| Segurança | Spring Security com palavras-passe encriptadas (BCrypt) |
| Interface | Thymeleaf e AdminLTE 3 (Bootstrap 4), responsiva |
| Base de dados | H2 (em memória) |
| Build | Maven (Maven Wrapper incluído) |
| Modelação | UML: casos de uso, sequência, classes e entidade-relacionamento |

**Arquitectura:** MVC em camadas (Controllers, Services, Repositories/JPA, DTOs e entidades), com 11 entidades de domínio: Utilizador, Administrador, Médico, Paciente, Especialidade, Agenda, Consulta, Atendimento, Exame, Aconselhamento e Presença.

## Como correr

Requisitos: **Java 17**.

```bash
git clone https://github.com/Frigildo/Frigildo-Sistema-de-marcacao-de-gestao-de-consultas-medicas.git
cd Frigildo-Sistema-de-marcacao-de-gestao-de-consultas-medicas
./mvnw spring-boot:run        # no Windows: mvnw.cmd spring-boot:run
```

Abre **http://localhost:8080** no browser.

Esta versão usa uma base de dados **H2 em memória**, por isso os dados são recriados com dados de demonstração sempre que a aplicação arranca. A consola da base de dados está em `/h2-console` (JDBC URL `jdbc:h2:mem:test`, utilizador `sa`, sem palavra-passe).

## Contas de demonstração

Todas com a palavra-passe `111111`. **São apenas dados de teste.**

| Perfil | Email |
|---|---|
| Administrador | admin@hospital.com |
| Médico | dr.silva@hospital.com, dr.costa@hospital.com |
| Paciente | joao@gmail.com, maria@gmail.com, pedro@gmail.com |

## Limitações e trabalho futuro

- Protótipo com base de dados em memória; a persistência em MySQL ou PostgreSQL é um próximo passo natural, por usar Spring Data JPA.
- Integração com programas clínicos específicos e consultas virtuais ficam como recomendações para trabalhos futuros.
- Testes automatizados (JUnit e Mockito) ainda por escrever.

## Autor

**Frigildo Zunguze**, programador back-end (Java e Spring Boot), Maputo, Moçambique.

---

## English

A web prototype for appointment management and clinical records, built as a bachelor's thesis in Computer and Telecommunications Engineering (Universidade Politécnica de Maputo, 2026) for the **Dream Sant'Egídio Health Centre in Maputo**.

**The problem.** At the centre, appointments could be closed without recording clinical counselling, and absence management was limited. Requirements were gathered from 11 staff members (survey, semi-structured interview and direct observation), and the system was modelled in UML before implementation.

**Features**
- Three user roles (Administrator, Doctor, Patient) with role-based access control.
- Schedule per doctor and specialty with slot control; **double booking is prevented**.
- Clinical attendance in 3 steps (observations, exams, diagnosis and medication). A completed attendance becomes read-only, except for the administrator.
- Exams with result file upload (PDF, JPG or PNG), clinical counselling records, attendance tracking and a consolidated clinical history for the patient.

**Tech stack:** Java 17, Spring Boot 3.2.4 (Spring MVC, Spring Data JPA, Bean Validation), Spring Security with BCrypt, Thymeleaf and AdminLTE 3, H2 in-memory database, Maven. Layered MVC architecture with 11 domain entities.

**Run it:** requires Java 17.

```bash
git clone https://github.com/Frigildo/Frigildo-Sistema-de-marcacao-de-gestao-de-consultas-medicas.git
cd Frigildo-Sistema-de-marcacao-de-gestao-de-consultas-medicas
./mvnw spring-boot:run
```

Then open **http://localhost:8080**. Demo accounts (test data only, password `111111`): `admin@hospital.com`, `dr.silva@hospital.com`, `joao@gmail.com`.

**Limitations and next steps:** in-memory database (MySQL or PostgreSQL persistence is the natural next step); automated tests (JUnit and Mockito) still to be written.
