package fr.coppernic.utils.core

import groovy.util.slurpersupport.GPathResult
import org.eclipse.jgit.api.CloneCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.TextProgressMonitor
import org.eclipse.jgit.transport.URIish

import java.nio.file.Path

class Cloner extends FetchAble {

    static class ClonerFactory extends Command.CommandFactory {

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
    def execute() {
        Git git
        if (!folderExists(project)) {
            git = cloneProject(project)
        } else {
            println("${getProjectPath(project)} already exists - do not clone")
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

    private folderExists(GPathResult project) {
        Path local = getProjectPath(project)
        return local.toFile().exists()
    }

    private Git cloneProject(GPathResult project) {
        Path local = getProjectPath(project)

        //println(local.toAbsolutePath().toString())
        URIish remote = new URIish(getGitUri(remotes["${project.@remote}"], "${project.@remote_path}"))

        CloneCommand clone = Git.cloneRepository()

        configureTransportCommand(clone)

        clone.setBranch("HEAD")
        clone.setDirectory(local.toFile())
        clone.setURI(remote.toString())

        if (enableLog) {
            println("Clone ${remote} in ${local}")
            clone.setProgressMonitor(new TextProgressMonitor(TextBuiltin.get().outw));
        }

        Git git = clone.call()

        if (enableLog) {
            println()
        }

        return git
    }

}