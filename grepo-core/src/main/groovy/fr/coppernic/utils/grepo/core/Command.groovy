package fr.coppernic.utils.grepo.core

import groovy.util.slurpersupport.GPathResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.nio.file.Path

/**
 * Base class to all git commands. It holds common data
 */
abstract class Command implements Runnable {
    /**
     * Map of remote url. Remotes are defined in xml file
     */
    Map<String, String> remotes
    /**
     * Project xml node
     */
    GPathResult project
    /**
     * Root path where all git repo are cloned
     */
    Path root
    /**
     * Callback called after command is executed
     */
    Closure afterExecute
    /**
     * Slf4j logger usable from child classes
     */
    Logger logger = LoggerFactory.getLogger(getClass())
}