package db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.util.Properties;
import java.io.InputStream;

public class ConnectionManager {

    private static HikariDataSource dataSource;

    static {
        try {
            Properties props = new Properties();
            try (InputStream in = ConnectionManager.class.getClassLoader().getResourceAsStream("config.properties")) {
                if (in != null) {
                    props.load(in);
                } else {
                    // fallback to env vars (opsional)
                    props.setProperty("db.url", System.getenv("DB_URL"));
                    props.setProperty("db.user", System.getenv("DB_USER"));
                    props.setProperty("db.password", System.getenv("DB_PASSWORD"));
                    props.setProperty("db.maxPoolSize", System.getenv().getOrDefault("DB_MAX_POOL", "10"));
                }
            }

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(props.getProperty("db.url"));
            config.setUsername(props.getProperty("db.user"));
            config.setPassword(props.getProperty("db.password"));
            config.setMaximumPoolSize(Integer.parseInt(props.getProperty("db.maxPoolSize", "10")));

            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize DataSource", e);
        }
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
}
