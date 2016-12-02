package fr.coppernic.utils.grepo.command

import fr.coppernic.utils.grepo.core.Project
import fr.coppernic.utils.grepo.core.Workspace
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
    Project project
    /**
     * Root path where all git repo are cloned
     */
    Path rootDir
    /**
     * Callback called after command is executed
     */
    Closure afterExecute
    /**
     * Slf4j logger usable from child classes
     */
    Logger logger = LoggerFactory.getLogger(getClass())
    /**
     * Workspace
     */
    Workspace workspace
    /**
     * Cache path data
     */
    private Path projectPath = null;


    public Path getProjectPath() {
        if (!projectPath) {
            projectPath = rootDir.resolve(project.localPath)
        }
        return projectPath
    }

    public boolean projectFolderExists() {
        File f = getProjectPath().toFile()
        return f.exists() && f.isDirectory()
    }
}