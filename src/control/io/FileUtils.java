package control.io;

import com.google.gson.Gson;
import model.ModuleStage;
import model.ModuleStageMemory;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


/**
 * @author Pim de Witte(wwadewitte), Whitespell LLC
 *         11/20/14
 *         control
 */
public class FileUtils {

    /**
     * Runs a bash script with params
     *
     * @param pathToScript  A relative or absolute path to a shell script
     * @param params The individual parameters to parse for the shell script
     * @return exit code represented in an integer
     */

    public static int runBash(String pathToScript, Object... params) throws IOException {
        int exitValueUpdate = ShellExecution.readBashScript(pathToScript, params);
        return exitValueUpdate;
    }

    /**
     * Downloads an archive and extracts it in an application's workspace.
     * This function overwrites any existing files and removes the archive after it's done extracting
     *
     * @param url          The exact application module ID
     * @param appWorkspace The semantic version that is available for update
     */

    public static void downloadAndExtractArchive(String url, String appWorkspace) throws IOException {
        String scriptPath[] = url.split("/");
        String fileName = scriptPath[scriptPath.length - 1];
        Path path = Paths.get(appWorkspace + "/" + fileName);
        Files.copy(new URL(url).openStream(), path, StandardCopyOption.REPLACE_EXISTING);
        ShellExecution.executeCommand("unzip -o " + path.toString() + " -d " + appWorkspace);
        ShellExecution.executeCommand("mv " + fileName.replace(".zip", "") + " " + appWorkspace);
        ShellExecution.executeCommand("rm " + path.toString());
    }

    /**
     * Write a string to a a specified file
     *
     * @param path    The path of the file that needs to be written to
     * @param content The semantic version that is available for update
     */

    public static void writeToFile(String path, String content) {
        Writer writer = null;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(path), "utf-8"));
            writer.write(content);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (Exception ex) {
            }
        }
    }

    /**
     * Updates a JSON file that stores the current local version to the new version
     *
     * @param module              The exact application module ID
     * @param version    The semantic version of the update to write to the file
     */

    public static void updateRunningVersion(String module, String version) {
        ModuleStage moduleStage = ModuleStageMemory.getModuleStage(module);
        moduleStage.setRunningVersion(version);
        String json = new Gson().toJson(moduleStage);
        FileUtils.writeToFile(ModuleStageMemory.getModuleStagePath(module), json);
        ModuleStageMemory.setModuleStage(module, moduleStage);
    }
    /**
     * Updates a JSON file that stores the current local version to the new version
     *
     * @param module              The exact application module ID
     * @param version    The semantic version of the update to write to the file
     */

    public static void updateLastAttemptedVersion(String module, String version) {
        ModuleStage moduleStage = ModuleStageMemory.getModuleStage(module);
        moduleStage.setLastAttemptedVersion(version);
        String json = new Gson().toJson(moduleStage);
        FileUtils.writeToFile(ModuleStageMemory.getModuleStagePath(module), json);
        ModuleStageMemory.setModuleStage(module, moduleStage);
    }

    /**
     * Updates a JSON file that stores the current local version to the new version
     *
     * @param module              The exact application module ID
     * @param exitCode    The exit code of a script
     */

    public static void updateLastExitCode(String module, int exitCode) {
        ModuleStage moduleStage = ModuleStageMemory.getModuleStage(module);
        moduleStage.setLastExitCode(exitCode);
        String json = new Gson().toJson(moduleStage);
        FileUtils.writeToFile(ModuleStageMemory.getModuleStagePath(module), json);
        ModuleStageMemory.setModuleStage(module, moduleStage);
    }


    /**
     * Updates a JSON file that stores the current local version to the new version
     *
     * @param module              The exact application module ID
     */

    public static void addAttempt(String module) {
        ModuleStage moduleStage = ModuleStageMemory.getModuleStage(module);
        moduleStage.setAttempts(moduleStage.getAttempts() + 1);
        String json = new Gson().toJson(moduleStage);
        FileUtils.writeToFile(ModuleStageMemory.getModuleStagePath(module), json);
        ModuleStageMemory.setModuleStage(module, moduleStage);
    }


    /**
     * Updates a JSON file that stores the current local version to the new version
     *
     * @param module              The exact application module ID
     */

    public static void resetAttempts(String module) {
        ModuleStage moduleStage = ModuleStageMemory.getModuleStage(module);
        moduleStage.setAttempts(0);
        String json = new Gson().toJson(moduleStage);
        FileUtils.writeToFile(ModuleStageMemory.getModuleStagePath(module), json);
        ModuleStageMemory.setModuleStage(module, moduleStage);
    }

    /**
     * Reads the local version file and places it into a LocalVersion model.
     *
     * @param localVersionPath          The path to the version file to return
     * @return {@link model.ModuleStage} object
     */

    public static ModuleStage readLocalVersionFromFile(String localVersionPath) {
        BufferedReader br;
        try {
            br = new BufferedReader(
                    new FileReader(localVersionPath));
        } catch (FileNotFoundException e) {
            System.out.println(ModuleStageMemory.getEmptyModuleStage());
            br = new BufferedReader(
                    new StringReader(ModuleStageMemory.getEmptyModuleStage()));
        }
        ModuleStage moduleStage = new Gson().fromJson(br, ModuleStage.class);

        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return moduleStage;
    }
}
