package control.config;

import model.Config;

/**
 * @author Pim de Witte(wwadewitte), Whitespell LLC
 *         11/16/14
 *         control
 */

/*
ConfigurationHolder holds the latest version of the Config model. It is refreshed every 5 minutes.
 */
public class DUClientConfiguration {

    private static Config config;

    private static final String WORKSPACE = "~/duclient";
    private static final String CONFIG_PATH = WORKSPACE + "config.json";
    private static final String CRONTAB_PATH = WORKSPACE + "duclient.sh";

    public static final String getWorkspace() {
        return WORKSPACE;
    }

    public static final String getConfigurationPath() {
        return CONFIG_PATH;
    }
 public static final String getCrontabPath() {
        return CRONTAB_PATH;
    }

    public static void put(String path) {
        config = ReadConfigs.read(path, true, true);
    }

    public static Config getConfig() {
        return config;
    }

    public static boolean isDebug() {
        return false;
    }
}
