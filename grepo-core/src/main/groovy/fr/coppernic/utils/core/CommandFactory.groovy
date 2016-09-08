package fr.coppernic.utils.core

import groovy.util.slurpersupport.GPathResult

import java.nio.file.Path;

/**
 * Base class for command factory
 */
public abstract class CommandFactory {

    private Map remotes = null
    private GPathResult project = null
    private Path root = null
    private Closure afterExecute = null

    CommandFactory setProject(GPathResult project) {
        this.project = project
        this
    }

    CommandFactory setRemotes(Map remotes) {
        this.remotes = remotes
        this
    }

    CommandFactory setRoot(Path root) {
        this.root = root
        this
    }

    CommandFactory setEnableLog(boolean enableLog) {
        this.enableLog = enableLog
        this
    }

    CommandFactory setAfterExecute(Closure afterExecute) {
        this.afterExecute = afterExecute
        this
    }

    Command build() {
        Command cmd = get()
        cmd.project = project
        cmd.remotes = remotes
        cmd.root = root
        cmd.afterExecute = afterExecute
        return cmd
    }

    /**
     * method to override to get a {@link Command} instance
     * @return {@link Command} instance
     */
    abstract Command get();
}
