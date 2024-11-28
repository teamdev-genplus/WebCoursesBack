package com.aecode.webcoursesback.servicesimplement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class SessionSequenceSyncRunner implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        try {
            // Sincronizar la secuencia para 'session_sequence'
            jdbcTemplate.execute("""
            SELECT setval('session_sequence', (SELECT COALESCE(MAX(session_id), 1) FROM sessions), true)
        """);

            // Sincronizar la secuencia para 'course_sequence'
            jdbcTemplate.execute("""
            SELECT setval('course_sequence', (SELECT COALESCE(MAX(course_id), 1) FROM courses), true)
        """);

            // Sincronizar la secuencia para 'module_sequence'
            jdbcTemplate.execute("""
            SELECT setval('module_sequence', (SELECT COALESCE(MAX(module_id), 1) FROM modules), true)
        """);

            // Sincronizar la secuencia para 'unit_sequence'
            jdbcTemplate.execute("""
            SELECT setval('unit_sequence', (SELECT COALESCE(MAX(unit_id), 1) FROM units), true)
        """);

            // Sincronizar la secuencia para 'work_sequence'
            jdbcTemplate.execute("""
            SELECT setval('work_sequence', (SELECT COALESCE(MAX(work_id), 1) FROM relatedworks), true)
        """);

            // Sincronizar la secuencia para 'user_sequence'
            jdbcTemplate.execute("""
            SELECT setval('user_sequence', (SELECT COALESCE(MAX(user_id), 1) FROM userprofiles), true)
        """);

            // Sincronizar la secuencia para 'sessiontest_sequence'
            jdbcTemplate.execute("""
            SELECT setval('sessiontest_sequence', (SELECT COALESCE(MAX(test_id), 1) FROM sessiontests), true)
        """);

            // Sincronizar la secuencia para 'sessionans_sequence'
            jdbcTemplate.execute("""
            SELECT setval('sessionans_sequence', (SELECT COALESCE(MAX(answer_id), 1) FROM sessionanswers), true)
        """);

            System.out.println("Todas las secuencias han sido sincronizadas correctamente.");
        } catch (Exception e) {
            System.err.println("Error al sincronizar secuencias: " + e.getMessage());
        }
    }


}
