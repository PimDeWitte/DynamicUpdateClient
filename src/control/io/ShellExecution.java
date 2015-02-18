package control.io;

import control.config.DUClientConfiguration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Pim de Witte(wwadewitte), Whitespell LLC
 *         11/20/14
 *         control
 */
public class ShellExecution {

    /**
     * Executes a command
     *
     * @param command          The exact command to execute
     * @return exit code integer representation
     */

    public static int executeCommand(String command) {

        StringBuffer output = new StringBuffer();

        Process p = null;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            if (DUClientConfiguration.isDebug()) {
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(p.getInputStream()));

                String line = "";
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                    output.append(line + "\n");
                }
                reader.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return (p == null ? -1 : p.exitValue());

    }

    /**
     * Executes a shell script
     *
     * @param path to the shell script to execute
     * @return exit code integer representation
     */

    public static int readBashScript(String path, Object...params) {

        StringBuilder output = new StringBuilder();
        Process proc = null;

        StringBuilder parameters = new StringBuilder();
        for(int i = 0; i < params.length; i++) {
          parameters.append(" " + params[i]);
        }

        try {
            proc = Runtime.getRuntime().exec(path + parameters.toString()); //todo(pim) not reading these params, fix
            BufferedReader read = new BufferedReader(new InputStreamReader(
                    proc.getInputStream()));
            try {
                proc.waitFor();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
            while (read.ready()) {
                output.append(read.readLine());
            }

            System.out.println(output);

            read.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return (proc == null ? -1 : proc.exitValue());
    }

}
