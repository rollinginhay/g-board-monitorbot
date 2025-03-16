package mtt.monitorbotd4j.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public interface Logging {
    Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
}
