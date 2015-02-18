package control.config;

import com.google.gson.Gson;
import control.io.FileUtils;
import control.io.ShellExecution;

import java.util.Scanner;

/**
 * @author Pim de Witte(wwadewitte), Whitespell LLC
 *         12/6/14
 *         control.config
 *         ${FILE_NAME}
 */
public class SetConfigs {


    /**
     * List of default configurations. Values inside @'s will be replaced when the duclient is first ran.
     */

    private static final String DEFAULT_HOST = "packages.local";
    private static final String DEFAULT_PORT = "80";

    private static final String DEFAULT_CRONJOB_USER = "root";
    private static final String DEFAULT_MINUTES_INTERVAL = "1";

    private static final String CONFIG_TEMPLATE = "{\n" +
            "    \"update_server_url\" : \"@HOST@:@PORT@\",\n" +
            "    \"modules\" : [\n" +
            "        @MODULES@\n" +
            "    ]\n" +
            "}\n";

    private static final String CRONJOB_TEMPLATE = "*/@MINUTES_INTERVAL@ * * * * @CRONTAB_FILE@\n";

    /**
     * We generate the cron job file based on the directory of our current workspace and include the libraries by replacing bin with lib.
     */
    private static final String JAVA_TEMPLATE = "#!/bin/bash\n" +
            "java -cp "+SetConfigs.class.getProtectionDomain().getCodeSource().getLocation().getFile()+":"+SetConfigs.class.getProtectionDomain().getCodeSource().getLocation().getFile().replace("/bin/", "/lib/*") +" StartUpdateClient > /tmp/update.log\n";

    public static void setConfigFile() {
        {
            String host, port;
            String[] apps;
            String modulesFinal;

            java.util.Scanner input = new Scanner(System.in);

           System.out.println("Configuration File Generator ");
           System.out.println("Enter host (default=packages.local): ");
           host = input.nextLine();

            if(host.length() <= 0) {
                host = DEFAULT_HOST;
            }

            System.out.println("Enter port (defualt=80): ");
            port = input.nextLine();

            if(port.length() <= 0) {
                port = DEFAULT_PORT;
            }



            System.out.println("Enter modules (Separated by commas, no spaces allowed, no default)");
            apps = input.nextLine().split(",");

            StringBuilder s = new StringBuilder();

            //manually build the strings inside the JSON array
            for (int i = 0; i < apps.length; i++) {
                s.append("\"" + apps[i] + "\"" + (i == (apps.length - 1) ? "" : ","));
            }

            modulesFinal = s.toString();

            if (host == null || host.length() <= 0 ||
                    port == null || port.length() <= 0 ||
                    modulesFinal == null || modulesFinal.length() <= 0 ||
                    modulesFinal.equals("\"\"")) {
                System.out.println("Config file configuration not correct:\n"
                                +
                                CONFIG_TEMPLATE
                                        .replace("@HOST@", host)
                                        .replace("@PORT@", port)
                                        .replace("@MODULES@", modulesFinal)
                );
                setConfigFile();
                return;
            }


            FileUtils.writeToFile(DUClientConfiguration.getConfigurationPath(), CONFIG_TEMPLATE
                            .replace("@HOST@", host)
                            .replace("@PORT@", port)
                            .replace("@MODULES@", modulesFinal)
            );



        }
    }

    public static void setDaemon() {
        String cronjobUser, minuteInterval;
        java.util.Scanner input = new Scanner(System.in);

        System.out.println("Daemon Generator");

        System.out.println("Enter the user that will execute the cronjob (default "+DEFAULT_CRONJOB_USER+") ");
        cronjobUser = input.nextLine();

        if(cronjobUser.length() <= 0) {
            cronjobUser = DEFAULT_CRONJOB_USER;
        }
        System.out.println("Enter the default interval in minutes(default "+DEFAULT_MINUTES_INTERVAL+") ");
        minuteInterval = input.nextLine();

        if(minuteInterval.length() <= 0) {
            minuteInterval = DEFAULT_MINUTES_INTERVAL;
        }

        FileUtils.writeToFile(DUClientConfiguration.getCrontabPath(), JAVA_TEMPLATE);
        System.out.println(JAVA_TEMPLATE);
        ShellExecution.executeCommand("chmod u+x " + DUClientConfiguration.getCrontabPath());


        FileUtils.writeToFile("crontab.tmp", CRONJOB_TEMPLATE
                                .replace("@CRONTAB_FILE@", DUClientConfiguration.getCrontabPath())
                                .replace("@CRONJOB_USER@", cronjobUser)
                                .replace("@MINUTES_INTERVAL@", minuteInterval));

        ShellExecution.executeCommand("crontab crontab.tmp -u " + cronjobUser);
        //todo(pim) clear

    }
}
