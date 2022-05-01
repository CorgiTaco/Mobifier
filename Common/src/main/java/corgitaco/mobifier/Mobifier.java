package corgitaco.mobifier;

import corgitaco.mobifier.common.condition.Condition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Mobifier {
    public static final String MOD_ID = "mobifier";
    public static final Logger LOGGER = LogManager.getLogger();

    static {
        Condition.register();
    }
}
