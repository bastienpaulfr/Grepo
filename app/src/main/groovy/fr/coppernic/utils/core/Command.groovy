package fr.coppernic.utils.core

import groovy.util.slurpersupport.GPathResult

import java.nio.file.Path

abstract class Command implements Executable {

    static abstract class CommandFactory {

        private Map remotes = null
        private GPathResult project = null
        private Path root = null
        boolean enableLog = true
        Closure afterExecute = null

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
            cmd.enableLog = enableLog
            cmd.afterExecute = afterExecute
            return cmd
        }

        abstract Command get();
    }

    boolean enableLog = true
    Map remotes
    GPathResult project
    Path root
    Closure afterExecute
}