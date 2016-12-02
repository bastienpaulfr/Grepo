package fr.coppernic.utils.grepo.command

import fr.coppernic.utils.grepo.utils.FolderOp
import fr.coppernic.utils.grepo.utils.TextBuiltin
import org.eclipse.jgit.api.CloneCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.TextProgressMonitor
import org.eclipse.jgit.transport.URIish

import java.nio.file.Path
/**
 * Class that clones git repo
 */
class Cloner extends FetchAble implements FolderOp {

    static class ClonerFactory extends CommandFactory {

        static ClonerFactory prepare() {
            return new ClonerFactory()
        }

        @Override
        Command get() {
            return new Cloner()
        }
    }

    private Cloner() {}

    @Override
    void run() {
        Git git
        if (!projectFolderExists()) {
            git = cloneProject()
        } else {
            logger.info("${getProjectPath()} already exists - do not clone")
            git = Git.open(getProjectPath().toFile())
        }
        if (afterExecute) {
            afterExecute(project, git)
        }
    }

    private Git cloneProject() {
        Path local = getProjectPath()
        URIish remote = new URIish(project.getGitUri())

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