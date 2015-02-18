package model;

/**
 * @author Pim de Witte(wwadewitte), Whitespell LLC
 *         11/16/14
 *         model
 */

import java.util.List;

public class Config {

    String update_server_url;
    int update_interval_minutes;
    List<String> modules;

    public String getUpdateServerUrl() {
        return update_server_url;
    }

    public List<String> getModules() {
        return modules;
    }

    public void setListenPackages(List<String> listenApps) {
        this.modules = modules;
    }

}

