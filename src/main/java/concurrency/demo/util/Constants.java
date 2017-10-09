package concurrency.demo.util;

public class Constants {
    public static final int KEYS_PER_CORE = 200;
    public static int COMPUTED_CAPACITY;
    public static int CORES_50;
    public static int CORES_75;
    public static int CORES_90;
    public static int THRESHOLD_50;
    public static int THRESHOLD_75;
    public static int THRESHOLD_90;
    public static final int AVAILABLE_CORES = KeyGenerator.getAvailableCores();

    static {
        COMPUTED_CAPACITY = KEYS_PER_CORE * AVAILABLE_CORES;
        CORES_50 = AVAILABLE_CORES;
        CORES_75 = AVAILABLE_CORES / 2;
        CORES_90 = AVAILABLE_CORES / 4;
        THRESHOLD_50 = (int) (COMPUTED_CAPACITY * 0.50);
        THRESHOLD_75 = (int) (COMPUTED_CAPACITY * 0.75);
        THRESHOLD_90 = (int) (COMPUTED_CAPACITY * 0.90);
    }
}
