DO
$$
    DECLARE
        user_id_var users.id%TYPE;
    BEGIN
        FOR user_id_var IN (SELECT DISTINCT user_id FROM user_preferences
                            WHERE user_id NOT IN (
                           	    SELECT user_id FROM user_preferences WHERE name = 'DEFAULT_TEST_VIEW'
                            ))
            LOOP
                INSERT INTO user_preferences (name, value, user_id) VALUES ('DEFAULT_TEST_VIEW', 'runs', user_id_var);
            END LOOP;
    END;
$$;