package model;

/**
 * @author Pim de Witte(wwadewitte), Whitespell LLC
 *         11/16/14
 *         model
 */

public class ModuleStage {

    String running_version;
    String last_attempted_version;
    int last_exit_code;
    int attempts;

    public String getRunningVersion() {
        return this.running_version;
    }
    public String getLastAttemptedVersion() {
        return this.last_attempted_version;
    }
    public int getLastExitCode() {
        return this.last_exit_code;
    }

    public void setLastExitCode(int exitCode) {
        this.last_exit_code = exitCode;
    }

    public int getAttempts() {
        return this.attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public void setRunningVersion(String version) {
        running_version = version;
    }
    public void setLastAttemptedVersion(String version) {
        last_attempted_version = version;
    }


}

