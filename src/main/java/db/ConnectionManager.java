package db;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class ConnectionManager {

    // FIX 2: Gunakan Logger
    private static final Logger LOGGER = Logger.getLogger(ConnectionManager.class.getName());
    
    private static HikariDataSource dataSource;

    // FIX 3: Konstanta untuk String duplikat (mengurangi Code Smell)
    private static final String KEY_DB_URL = "db.url";
    private static final String KEY_DB_USER = "db.user";
    private static final String KEY_DB_PASSWORD = "db.password";
    private static final String KEY_DB_MAX_POOL = "db.maxPoolSize";

    // FIX 1: Tambahkan Private Constructor (Utility Class tidak boleh di-instansiasi)
    private ConnectionManager() {
        throw new IllegalStateException("Utility class");
    }

    static {
        try {
            Properties props = new Properties();
            try (InputStream in = ConnectionManager.class.getClassLoader().getResourceAsStream("config.properties")) {
                if (in != null) {
                    props.load(in);
                } else {
                    // Fallback to env vars
                    // FIX 4: Gunakan helper method agar tidak NullPointerException jika Env tidak ada
                    setPropertyFromEnv(props, KEY_DB_URL, "DB_URL");
                    setPropertyFromEnv(props, KEY_DB_USER, "DB_USER");
                    setPropertyFromEnv(props, KEY_DB_PASSWORD, "DB_PASSWORD");
                    
                    String envMaxPool = System.getenv().getOrDefault("DB_MAX_POOL", "10");
                    props.setProperty(KEY_DB_MAX_POOL, envMaxPool);
                }
            }

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(props.getProperty(KEY_DB_URL));
            config.setUsername(props.getProperty(KEY_DB_USER));
            config.setPassword(props.getProperty(KEY_DB_PASSWORD));
            
            // Tambahkan validasi parsing integer
            String poolSizeStr = props.getProperty(KEY_DB_MAX_POOL, "10");
            config.setMaximumPoolSize(Integer.parseInt(poolSizeStr));

            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            dataSource = new HikariDataSource(config);
            
            // Opsional: Tambahkan shutdown hook agar koneksi tertutup rapi saat aplikasi mati
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (dataSource != null) {
                    dataSource.close();
                }
            }));

        } catch (Exception e) {
            // FIX 2: Ganti printStackTrace dengan Logger
            LOGGER.log(Level.SEVERE, "Failed to initialize DataSource", e);
            // Melempar RuntimeException agar aplikasi berhenti jika DB gagal connect (Fail Fast)
            throw new RuntimeException("Failed to initialize DataSource", e);
        }
    }
    
    // Helper method untuk FIX 4 (Menghindari null value di Properties)
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