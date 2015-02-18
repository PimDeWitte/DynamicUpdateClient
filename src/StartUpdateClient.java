import control.config.DUClientConfiguration;
import control.config.SetConfigs;
import control.io.ShellExecution;

import java.io.File;

public class StartUpdateClient {

    /**
     * Creates the necessary requirements if they do not yet exist and initialize the configuration.
     * If the configuration is correct, it starts the {@link UpdateClientProcess}
     *
     * @param args Arguments specified
     */

    public static void main(String[] args) {
        boolean configurationMode = false;

        ShellExecution.executeCommand("mkdir -p " + DUClientConfiguration.getWorkspace());
        ShellExecution.executeCommand("mkdir -p " + DUClientConfiguration.getWorkspace() + "versions/");

        if(args.length > 0) {
            if(args[0].equalsIgnoreCase("--config")) {
                System.out.println("  ____  _   _  ____ _ _            _      ____             __ _                       _   _             \n" +
                        " |  _ \\| | | |/ ___| (_) ___ _ __ | |_   / ___|___  _ __  / _(_) __ _ _   _ _ __ __ _| |_(_) ___  _ __  \n" +
                        " | | | | | | | |   | | |/ _ \\ '_ \\| __| | |   / _ \\| '_ \\| |_| |/ _` | | | | '__/ _` | __| |/ _ \\| '_ \\ \n" +
                        " | |_| | |_| | |___| | |  __/ | | | |_  | |__| (_) | | | |  _| | (_| | |_| | | | (_| | |_| | (_) | | | |\n" +
                        " |____/ \\___/ \\____|_|_|\\___|_| |_|\\__|  \\____\\___/|_| |_|_| |_|\\__, |\\__,_|_|  \\__,_|\\__|_|\\___/|_| |_|\n" +
                        "                                                                |___/                                   ");

                SetConfigs.setConfigFile();
                SetConfigs.setDaemon();
                configurationMode = true;
            }
        }

        DUClientConfiguration.put(DUClientConfiguration.getConfigurationPath());

        if(!configurationMode) {
            if(DUClientConfiguration.getConfig() != null && DUClientConfiguration.getConfig().getModules().size() > 0) {
                UpdateClientProcess.start();
            } else {
                System.out.println("No configurations found. Please run this program with --config parameters to configure");
                System.exit(1);
            }
        }
    }
}
