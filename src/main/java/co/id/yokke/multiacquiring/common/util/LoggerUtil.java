package co.id.yokke.multiacquiring.common.util;

import org.slf4j.Logger;

public class LoggerUtil {

    private static String FORMAT = "ERROR ----------------->  {} ";

    public static void print(Logger logger, Exception exception) {
        logger.error(FORMAT, exception);
    }
}
