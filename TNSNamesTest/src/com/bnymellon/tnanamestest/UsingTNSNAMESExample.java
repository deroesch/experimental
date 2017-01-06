package com.bnymellon.tnanamestest;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * An example of making a JDBC database connections using a TNSNAMES.ORA file.
 * 
 * @author Adapted by Douglas Roesch from http://tinyurl.com/kgzwe3j
 * 
 */
public class UsingTNSNAMESExample {

    /********************************************************************
     * 
     * Main entry point
     * 
     ********************************************************************/

    /**
     * Main entry point
     * 
     * @param args
     *            command line arguments
     * @throws Exception
     *             One error
     */
    public static void main(final String[] args) {

        try {

            new UsingTNSNAMESExample().run(args);

        } catch (final Exception e) {
            System.err.println("Failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /********************************************************************
     * 
     * Run controller
     * 
     ********************************************************************/

    /**
     * Connect to database and fetch some test data.
     * 
     * @throws Exception
     *             On error
     */
    public void run(final String[] args) throws Exception {

        assert null != args;

        loadConfiguration(args);

        final String dbURL = loadDriver();

        Connection conn = null;
        Statement stmt = null;

        try {
            conn = DriverManager.getConnection(dbURL, m_username, m_password);
            stmt = conn.createStatement();

            final String q = this.q;
            final ResultSet rs = stmt.executeQuery(q);

            int count = 0;

            while (rs.next()) {
                count++;
            }
            final String msg = "Found " + count + " records.";
            System.out.println(msg);

        } finally {
            closeIf(stmt);
            closeIf(conn);
        }
    }

    /**
     * Connect to database and fetch some test data.
     * 
     * @throws Exception
     *             On error
     */
    public void runOld(final String[] args) throws Exception {

        assert null != args;

        loadConfiguration(args);

        final String dbURL = loadDriver();

        Connection conn = null;
        Statement stmt = null;

        try {
            conn = DriverManager.getConnection(dbURL, m_username, m_password);
            stmt = conn.createStatement();

            final String q = "SELECT COUNT(*) FROM " + m_schema + "." + m_table;
            final ResultSet rs = stmt.executeQuery(q);

            if (rs.next()) {
                final int count = rs.getInt(1);
                final String msg = "Found " + count + " in " + m_schema + "."
                        + m_table + ".";
                System.out.println(msg);
            }

        } finally {
            closeIf(stmt);
            closeIf(conn);
        }
    }

    /********************************************************************
     * 
     * Command line arg parse & set
     * 
     ********************************************************************/

    /**
     * Parse command line, set global command conditions and properties.
     * 
     * @param args
     *            Args to parse
     * 
     * @throws FileNotFoundException
     *             On missing config file
     * @throws IOException
     *             On other I/O error
     */
    void loadConfiguration(final String[] args) throws FileNotFoundException,
            IOException {

        assert null != args;

        try {
            opts.addOption("h", "help", false, "Show help");
            opts.addOption("v", "version", false, "Version information");
            opts.addOption("c", "config", true, "Path to config file");
            opts.addOption("u", "user", true, "Database username");
            opts.addOption("p", "pass", true, "Database password");
            opts.addOption("s", "schema", true, "Database schema");
            opts.addOption("t", "table", true, "Datbase table");
            opts.addOption("n", "name", true, "TNS name to use");
            opts.addOption("d", "dir", true,
                    "Path to directory holding TNSNAMES.ora file");

            final CommandLineParser parser = new BasicParser();
            final CommandLine cmd = parser.parse(opts, args);

            if (cmd.hasOption("h"))
                help();

            if (cmd.hasOption("v")) {
                System.out.println("UsingTNSNAMESExample version " + VERSION);
                System.exit(0);
            }

            if (cmd.hasOption("c"))
                m_configFilePath = cmd.getOptionValue("c");

            // Load config file first, then start setting overrides.
            loadProps(m_configFilePath);

            if (cmd.hasOption("u"))
                m_username = cmd.getOptionValue("u");

            if (cmd.hasOption("p"))
                m_password = cmd.getOptionValue("p");

            if (cmd.hasOption("s"))
                m_schema = cmd.getOptionValue("s");

            if (cmd.hasOption("t"))
                m_table = cmd.getOptionValue("t");

            if (cmd.hasOption("n"))
                this.m_tnsName = cmd.getOptionValue("n");

            if (cmd.hasOption("d"))
                this.m_tnsLocation = cmd.getOptionValue("d");

        } catch (final ParseException e) {
            help();
            System.err.println();
            e.printStackTrace();
        }
    }

    /**
     * Print some help
     */
    void help() {
        new HelpFormatter().printHelp("Main", opts);
        System.exit(0);
    }

    /********************************************************************
     * 
     * Helpers
     * 
     ********************************************************************/

    /**
     * Load runtime properties
     * 
     * @throws FileNotFoundException
     *             On missing property file
     * @throws IOException
     *             On other I/O error
     */
    void loadProps(final String path) throws FileNotFoundException, IOException {

        assert null != path;

        // Load properties files
        m_props.load(new FileReader(path));

        // Extract and set var values based on properties.
        m_tnsLocation = m_props.getProperty("db.tnsLocation", m_tnsLocation);
        m_tnsName = m_props.getProperty("db.tnsName", m_tnsName);
        m_username = m_props.getProperty("db.username", m_username);
        m_password = m_props.getProperty("db.password", m_password);
        m_schema = m_props.getProperty("db.schema", m_schema);
        m_table = m_props.getProperty("db.table", m_table);
    }

    /**
     * Loads database driver, constructs connection URL
     * 
     * @return The connection URL for DriverManager.getConection() call
     * @throws ClassNotFoundException
     *             Driver class not found
     */
    String loadDriver() throws ClassNotFoundException {

        // tell the driver where to look for the TNSNAMES.ORA file
        System.setProperty("oracle.net.tns_admin", m_tnsLocation);

        // ORCL is net service name from the TNSNAMES.ORA file
        final String dbURL = "jdbc:oracle:thin:@" + m_tnsName;

        // load the driver
        Class.forName("oracle.jdbc.OracleDriver");

        return dbURL;
    }

    /**
     * Close the statement if it's not null
     * 
     * @param stmt
     *            The statement to close
     */
    void closeIf(final Statement stmt) {
        if (stmt != null)
            try {
                if (null != stmt)
                    stmt.close();
            } catch (final Exception e) {
                e.printStackTrace();
            }
    }

    /**
     * Close the connection if it's not null.
     * 
     * @param conn
     *            The connection to close
     */
    void closeIf(final Connection conn) {
        if (conn != null)
            try {
                if (null != conn)
                    conn.close();
            } catch (final Exception e) {
                e.printStackTrace();
            }
    }

    /********************************************************************
     * 
     * Local state and global property defaults.
     * 
     ********************************************************************/

    // Location of config file.
    String                      m_configFilePath = "./config.properties";

    // Location of TNSNAMES.ORA file
    String                      m_tnsLocation    = "C:/Oracle_11G_ClientR2/product/11.2.0/client_1/network/admin";

    // TNS identifier of desired database connection
    String                      m_tnsName        = "ELDEV6.WORLD";

    // Username for database connection
    String                      m_username       = "";

    // Password for database connection
    String                      m_password       = "";

    // Desired database schema
    String                      m_schema         = "EL_APP_DEVELOPMENT";

    // Table within that schema
    String                      m_table          = "BNYM_SRS_REQUEST_TRACKING";

    // Runtime configuration properties
    Properties                  m_props          = new Properties();

    // Command line options
    Options                     opts             = new Options();

    // Version stamp for --version command line arg
    private static final String VERSION          = "0.9.1 for Java 1.7";

    String                      q                = "  SELECT TRIM (PUH.USERNAME) USER_ID, "
                                                         + "         MAX (PUH.FULL_NAME) NAME, "
                                                         + "         UJ.INSTANCE LOG_ENTRY_NUM, "
                                                         + "         UJ.ITEM_TYPE, "
                                                         + "         UJ.ITEM_ID, "
                                                         + "         UJ.COLUMN_NAME, "
                                                         + "         TRIM (UJ.OLD_VALUE) OLD_VALUE, "
                                                         + "         TRIM (UJ.NEW_VALUE) NEW_VALUE, "
                                                         + "         UJ.UPD_USER, "
                                                         + "         UJ.UPD_DATETIME "
                                                         + "    FROM PACE_MASTERDBO.UPDATE_JOURNAL uj, PACE_MASTERDBO.PACE_USERS_HIST puh "
                                                         + "   WHERE     PUH.INSTANCE = UJ.ITEM_ID "
                                                         + "         AND UJ.ITEM_TYPE IN "
                                                         + "                ('STAR_ADD_U', "
                                                         + "                 'STAR_ADD_P', "
                                                         + "                 'STAR_ADD_G', "
                                                         + "                 'DELETEUSER', "
                                                         + "                 'UPDATEROLE', "
                                                         + "                 'P_DELGROUP', "
                                                         + "                 'ADD_ROLE_R', "
                                                         + "                 'DEL_ROLE_R', "
                                                         + "                 'P_UPDGROUP', "
                                                         + "                 'ADDUSER   ', "
                                                         + "                 'STAR_DEL_U', "
                                                         + "                 'ADDROLE   ', "
                                                         + "                 'UPDATEUSER', "
                                                         + "                 'S_ADDGROUP', "
                                                         + "                 'STAR_DEL_G', "
                                                         + "                 'STAR_DEL_P', "
                                                         + "                 'P_ADDGROUP', "
                                                         + "                 'PACE_ADD_P') "
                                                         + "         AND TRUNC (UJ.UPD_DATETIME) = TO_DATE ('13-feb-2014', 'dd-MON-yy') "
                                                         + "         AND PUH.EFFECTIVE_DATE >= TO_DATE ('01-jun-2015', 'dd-MON-yy') "
                                                         + "         AND PUH.EFFECTIVE_DATE <= TO_DATE ('01-jun-2015', 'dd-MON-yy') "
                                                         + "GROUP BY PUH.USERNAME, "
                                                         + "         UJ.INSTANCE, "
                                                         + "         UJ.ITEM_TYPE, "
                                                         + "         UJ.ITEM_ID, "
                                                         + "         UJ.COLUMN_NAME, "
                                                         + "         UJ.OLD_VALUE, "
                                                         + "         UJ.NEW_VALUE, "
                                                         + "         UJ.UPD_USER, "
                                                         + "         UJ.UPD_DATETIME "
                                                         + "ORDER BY LOG_ENTRY_NUM ASC ";
}
