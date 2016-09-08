package fr.coppernic.utils.command

import fr.coppernic.utils.core.Command
import fr.coppernic.utils.core.CommandFactory
import fr.coppernic.utils.core.FetchAble
import fr.coppernic.utils.core.TextBuiltin
import groovy.util.slurpersupport.GPathResult
import org.eclipse.jgit.api.CloneCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.TextProgressMonitor
import org.eclipse.jgit.transport.URIish

import java.nio.file.Path

/**
 * Class that clones git repo
 */
class Cloner extends FetchAble {

    static class ClonerFactory extends CommandFactory {

        static ClonerFactory prepare() {
            return new ClonerFactory()
        }

        @Override
        Command get() {
            return new Cloner()
        }
    }

    private Path path = null;

    private Cloner() {}

    @Override
    void run() {
        Git git
        if (!folderExists(project)) {
            git = cloneProject(project)
        } else {
            logger.info("${getProjectPath(project)} already exists - do not clone")
            git = Git.open(getProjectPath(project).toFile())
        }
        if (afterExecute) {
            afterExecute(project, git)
        }
    }

    private Path getProjectPath(GPathResult project) {
        if (!path) {
            path = root.resolve("${project.@local_path}")
        }
        return path
    }

    private boolean folderExists(GPathResult project) {
        Path local = getProjectPath(project)
        return local.toFile().exists()
    }

    private Git cloneProject(GPathResult project) {
        Path local = getProjectPath(project)

        URIish remote = new URIish(getGitUri(remotes["${project.@remote}"], "${project.@remote_path}"))

        CloneCommand clone = Git.cloneRepository()

        configureTransportCommand(clone)

        clone.setBranch("HEAD")
        clone.setDirectory(local.toFile())
        clone.setURI(remote.toString())

        logger.info("Clone ${remote} in ${local}")
        clone.setProgressMonitor(new TextProgressMonitor(TextBuiltin.get().loggerWriter));

        Git git = clone.call()

        return git
    }

}