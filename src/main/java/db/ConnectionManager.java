package db;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class ConnectionManager {

    private static final Logger LOGGER = Logger.getLogger(ConnectionManager.class.getName());
    
    private static HikariDataSource dataSource;

    private static final String KEY_DB_URL = "db.url";
    private static final String KEY_DB_USER = "db.user";
    private static final String KEY_DB_PASSWORD = "db.password";
    private static final String KEY_DB_MAX_POOL = "db.maxPoolSize";

    private ConnectionManager() {
        throw new IllegalStateException("Utility class");
    }

    static {
        try {
            Properties props = loadProperties();
            HikariConfig config = createHikariConfig(props);
            dataSource = new HikariDataSource(config);
            
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (dataSource != null) {
                    dataSource.close();
                }
            }));

        } catch (IOException | NumberFormatException e) {
            // Log the specific error and throw a runtime exception to halt initialization
            LOGGER.log(Level.SEVERE, "Failed to initialize DataSource configuration", e);
            throw new IllegalStateException("Database configuration error", e);
        } catch (RuntimeException e) {
             // Catch HikariCP initialization errors
             LOGGER.log(Level.SEVERE, "Failed to create HikariDataSource", e);
             throw e;
        }
    }

    private static Properties loadProperties() throws IOException {
        Properties props = new Properties();
        try (InputStream in = ConnectionManager.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in != null) {
                props.load(in);
            } else {
                setPropertyFromEnv(props, KEY_DB_URL, "DB_URL");
                setPropertyFromEnv(props, KEY_DB_USER, "DB_USER");
                setPropertyFromEnv(props, KEY_DB_PASSWORD, "DB_PASSWORD");
                
                String envMaxPool = System.getenv().getOrDefault("DB_MAX_POOL", "10");
                props.setProperty(KEY_DB_MAX_POOL, envMaxPool);
            }
        }
        return props;
    }

    private static HikariConfig createHikariConfig(Properties props) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(props.getProperty(KEY_DB_URL));
        config.setUsername(props.getProperty(KEY_DB_USER));
        config.setPassword(props.getProperty(KEY_DB_PASSWORD));
        
        String poolSizeStr = props.getProperty(KEY_DB_MAX_POOL, "10");
        config.setMaximumPoolSize(Integer.parseInt(poolSizeStr));

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        
        return config;
    }
    
    private static void setPropertyFromEnv(Properties props, String propKey, String envKey) {
        String value = System.getenv(envKey);
        if (value != null) {
            props.setProperty(propKey, value);
        }
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
}