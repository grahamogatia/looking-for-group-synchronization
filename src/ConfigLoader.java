import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {
    private final int n;
    private final int t;
    private final int h;
    private final int d;
    private final int t1;
    private final int t2;
    private final Properties prop;

    public ConfigLoader() throws IOException {
        prop = new Properties();

        try (FileInputStream fis = new FileInputStream("config.properties")) {
            prop.load(fis);

            this.n = getValidatedIntProperty("n");
            this.t = getValidatedIntProperty("t");
            this.h = getValidatedIntProperty("h");
            this.d = getValidatedIntProperty("d");
            this.t1 = getValidatedIntProperty("t1");
            this.t2 = getValidatedIntProperty("t2");


            if (n == 0) {
                throw new IllegalArgumentException("The maximum number of instances (n) cannot be zero. Dungeon raid with no dungeon  is .... eh?");
            }
            if (t1 > t2) {
                throw new IllegalArgumentException("t1 (" + t1 + ") cannot be greater than t2 (" + t2 + ").");
            }

        } catch (FileNotFoundException e) {
            throw new IOException("Configuration file 'config.properties' not found.", e);
        } catch (IOException e) {
            throw new IOException("Error reading configuration file.", e);
        }
    }

    private int getValidatedIntProperty(String key) {
        String value = prop.getProperty(key);

        if (value == null) {
            throw new IllegalArgumentException("Missing value for '" + key + "'.");
        }

        try {
            int number = Integer.parseInt(value.trim());
            if (number >= 0) {
                return number;
            } else {
                throw new IllegalArgumentException("Invalid value for '" + key + "'. Must be a non-negative integer.");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid format for '" + key + "'. Must be a non-negative integer.");
        }
    }

    public int getN() {
        return n;
    }

    public int getT() {
        return t;
    }

    public int getH() {
        return h;
    }

    public int getD() {
        return d;
    }

    public int getT1() {
        return t1;
    }

    public int getT2() {
        return t2;
    }
}
