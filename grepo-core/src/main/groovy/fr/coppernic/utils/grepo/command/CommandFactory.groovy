package fr.coppernic.utils.grepo.command

import fr.coppernic.utils.grepo.core.Project
import fr.coppernic.utils.grepo.core.Workspace

import java.nio.file.Path
/**
 * Base class for command factory
 */
public abstract class CommandFactory {

    Map remotes = null
    Project project = null
    Path rootDir = null
    Closure afterExecute = null
    Workspace workspace = null

    CommandFactory setProject(Project project) {
        this.project = project
        this
    }

    CommandFactory setRemotes(Map remotes) {
        this.remotes = remotes
        this
    }

    CommandFactory setRootDir(Path root) {
        this.rootDir = root
        this
    }

    CommandFactory setAfterExecute(Closure afterExecute) {
        this.afterExecute = afterExecute
        this
    }

    CommandFactory setWorkspace(Workspace workspace) {
        this.workspace = workspace
        this
    }

    Command build() {
        Command cmd = get()
        cmd.project = project
        cmd.remotes = remotes
        cmd.rootDir = rootDir
        cmd.afterExecute = afterExecute
        cmd.workspace = workspace
        return cmd
    }

    /**
     * method to override to get a {@link Command} instance
     * @return {@link Command} instance
     */
    abstract Command get();
}
