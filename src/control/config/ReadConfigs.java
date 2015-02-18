package control.config;

import com.google.gson.Gson;
import model.Config;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ReadConfigs {


    /**
     * Reads the duclient configuration and prompts a request to generate one if necessary
     *
     * @param path          The path of where to find the config file or generate one
     * @param retry         Should we retry if an error is thrown
     * @param allowConfig   Allows for configuration of the config file on failure (only on first run)
     * @return {@link Config} object model to place in the {@link DUClientConfiguration} object for static reference
     */

    public static Config read(String path, boolean retry, boolean allowConfig) {

        Gson gson = new Gson();
        Config config = null;

        try {

            BufferedReader br = new BufferedReader(
                    new FileReader(path));

            //convert the json string back to object
            config = gson.fromJson(br, Config.class);

            if (br != null) {
                br.close();
            }

        } catch (FileNotFoundException f) {

            if (allowConfig)
            // retry to read it once, otherwise exit
            if (retry) {
                return read(path, false, false);
            } else {
                System.out.println("Config file \" + CONFIG_PATH + \" can not be read, shutting down");
                System.exit(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return config;
    }
}
