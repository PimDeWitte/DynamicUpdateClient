import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import control.config.DUClientConfiguration;
import control.config.ReadConfigs;
import control.http.HttpHandler;
import control.io.FileUtils;
import control.io.ShellExecution;
import model.ExitCodes;
import model.ModuleStageMemory;
import model.RemoteVersion;

import java.util.List;

/**
 * @author Pim de Witte(wwadewitte), Whitespell LLC
 *         10/4/14
 */
public class UpdateClientProcess {

    /**
     * Starts the update process
     * @see StartUpdateClient
     */

    public static void start() {
        System.out.println("Starting Update Client Process with " + DUClientConfiguration.getConfig().getModules().size() + " module(s) to listen for");
        checkForUpdates();
   }

    /**
     * @flag Important function
     * @flag Completely depends on the server ordering results correctly. The semantic versions are not checked against each other at all.
     * It simply looks for a string match and flags any versions that come after the string match in the array as an update that needs to be installed.
     * <p/>
     * First reads the configurations in case anything has changed, then iterates through the modules(refered to as modules) to listen for.
     * Per module, this function first checks whether the correct directory has been created for this module, and creates it if that is required
     * It then executes a GET request to the Update Server URL in control.config.json and iterates through the versions in the list.
     * It triggers updateRequired the current version in the list if the current version is found in the remote versions, therefore we should
     * then install any updates that came after the version we have right now. If no version is installed yet, it will install the first version it founds in the list
     * <p/>
     * The update is flagged as failed if the update script returns anything but a 0. We will then update the local version file.
     * If the update script returns a 1, we will not update our local version to this and ignore this version in the future.
     * The next version will then contain a fix for this problem and is able to update from the current version. We also set a last attempted version if it failed to make sure we never
     * keep trying a script that returns 1 and failed, because it doesn't update the local version, but should still not keep attempting
     * because the update script might stop the process, which would cause outage.
     * If the update script returns a 2, we will retry and parse the retry count as a parameter so that the update script can decide
     * whether to postpone again or return a 1 so that we can ship a new version that addresses this issue
     */

    public static void checkForUpdates() {

        ReadConfigs.read(DUClientConfiguration.getConfigurationPath(), true, false);

        List<String> packagesToCheck = DUClientConfiguration.getConfig().getModules();
        for (String module : packagesToCheck) {
            try {
                ShellExecution.executeCommand("mkdir -p " + DUClientConfiguration.getWorkspace() + "versions/" + module);

                String versionsResponse = HttpHandler.returnGetRequest(DUClientConfiguration.getConfig().getUpdateServerUrl() + "/" + module + ".json");
                JsonArray remoteVerionList = new JsonParser().parse(versionsResponse).getAsJsonArray();

                ModuleStageMemory.setModuleStage(module, FileUtils.readLocalVersionFromFile(ModuleStageMemory.getModuleStagePath(module)));

                if(ModuleStageMemory.getModuleStage(module).getLastAttemptedVersion() == null && ModuleStageMemory.getModuleStage(module).getRunningVersion() != null) {
                    FileUtils.updateLastAttemptedVersion(module, ModuleStageMemory.getModuleStage(module).getRunningVersion());
                }

                boolean updateRequired = false;
                for (final JsonElement remoteVersionElement : remoteVerionList) {
                    RemoteVersion remoteVersion = new Gson().fromJson(remoteVersionElement, RemoteVersion.class);

                    if (ModuleStageMemory.getModuleStage(module).getRunningVersion() == null || remoteVersion.getVersion().equals(ModuleStageMemory.getModuleStage(module).getRunningVersion())) {
                        updateRequired = true;

                        // We require updates after this point, continue to the next iteration if we already have a version installed or update to this version if we have no update installed yet.
                        if (ModuleStageMemory.getModuleStage(module).getRunningVersion() != null) {
                            System.out.println("The current local version for "+module+" is " + ModuleStageMemory.getModuleStage(module).getRunningVersion());
                            continue;
                        }
                    }

                    if (updateRequired) {
                        handleUpdate(module, remoteVersion.getVersion(), remoteVersion.getSource());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Runs an update script located in a remote archive and returns an integer representation of the update script exit code.
     * The src should always ben an absolute URL
     *
     * @param module                             The exact modulelication module ID
     * @param remoteVersion                   The semantic version that is available for update
     * @param src                             The absolute URL where the update archive is located
     * @param params                          All parameters that should be passed on to the script
     * @return Integer representation of the update script exit code
     */

    private static int attemptVersionUpdate(String module, String remoteVersion, String src, Object... params) throws Exception {

       if(ModuleStageMemory.getModuleStage(module).getLastExitCode() == ExitCodes.NEW_STAGE.getCode()
                || ModuleStageMemory.getModuleStage(module).getLastExitCode() == ExitCodes.EXECUTED_SUCCESSFULLY.getCode()
                || ModuleStageMemory.getModuleStage(module).getLastExitCode() == ExitCodes.FAIL_AND_RETRY.getCode()) {
           FileUtils.updateLastAttemptedVersion(module, remoteVersion);
           FileUtils.addAttempt(module);
           FileUtils.downloadAndExtractArchive(src, DUClientConfiguration.getWorkspace() + "versions/" + module + "/");
           String workspace = DUClientConfiguration.getWorkspace() + "versions/" + module + "/" + remoteVersion;
           return FileUtils.runBash(workspace + "/update.sh", params);
        }
        //ignore the update and maintain the last exit code (1=regular fail, any other code is also an ignore)
        return ModuleStageMemory.getModuleStage(module).getLastExitCode();
    }

    /**
     * Handles the updates, including re-attempts and failures.
     *
     * @param module                          The exact module ID
     * @param remoteVersion                   The semantic version that is available for update
     * @param src                             The absolute URL where the update archive is located
     */

    public static void handleUpdate(String module, String remoteVersion, String src) throws Exception {
        int exitCode = attemptVersionUpdate(module, remoteVersion, src, ModuleStageMemory.getModuleStage(module).getRunningVersion(), remoteVersion, ModuleStageMemory.getModuleStage(module).getAttempts());

        if(exitCode == ExitCodes.FAIL_AND_RETRY.getCode() && ModuleStageMemory.getModuleStage(module).getAttempts() > 5) {
            System.out.println("We can only system exit 4 times max for a failed update, as a security failover.");
            System.exit(ExitCodes.FAIL_AND_RETRY.getCode());
            return;
        }
        if (exitCode == ExitCodes.EXECUTED_SUCCESSFULLY.getCode()) {
                FileUtils.updateRunningVersion(module, remoteVersion);
                FileUtils.resetAttempts(module);
                System.out.println("[SUCCESS] " +module+ " was updated to " + remoteVersion);
        } else {
            System.out.println("[FAIL] " + module + " failed with exit code " + exitCode);
        }
        FileUtils.updateLastExitCode(module, exitCode);
    }
}
