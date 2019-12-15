package MulticastRMIServers;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class Property extends Properties {

    public Property(String path) {
        try {
            this.load(new InputStreamReader(new FileInputStream(path)));

        } catch (IOException e) {
            System.out.println("Properties file not found");
            System.exit(0);
        }
    }
}