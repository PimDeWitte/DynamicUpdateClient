package model;

import control.config.DUClientConfiguration;

import java.util.HashMap;

/**
 * @author Pim de Witte(wwadewitte), Whitespell LLC
 *         12/6/14
 *         model
 *         ${FILE_NAME}
 */

/**
 * Mo stores the local version and last attempted version for each module
 * It prevents the program from executing previous versions and versions that it has already attempted to avoid unnecessary outage
 * The only occasion in which the update will re-attempt to run itself is when exit code 2 is returned, then the local version file is not updated with the last attempted version.
 */
public class ModuleStageMemory {

    private static final String EMPTY_MODEL_STAGE = "{\"running_version\": null, \"last_attempted_version\": null, \"last_exit_code\" : "+ExitCodes.NEW_STAGE.getCode()+", \"attempts\" : 0}\n";

    public static HashMap<String, ModuleStage> moduleStageHashMap = new HashMap<>();

    public static ModuleStage getModuleStage(String module) {
        return moduleStageHashMap.get(module);
    }

    public static void setModuleStage(String module, ModuleStage moduleStage) {
        moduleStageHashMap.put(module, moduleStage);
    }

    public static String getModuleStagePath(String module) {
        return DUClientConfiguration.getWorkspace() + "versions/" + module + ".json";
    }

    public static String getEmptyModuleStage() {
        return EMPTY_MODEL_STAGE;
    }
}
