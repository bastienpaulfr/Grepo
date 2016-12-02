package fr.coppernic.utils.grepo.command

import fr.coppernic.utils.grepo.utils.FolderOp
import fr.coppernic.utils.grepo.utils.TextBuiltin
import org.eclipse.jgit.api.FetchCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.TextProgressMonitor
import org.eclipse.jgit.transport.RefSpec

import java.nio.file.Path
/**
 * Class that clones git repo
 */
class Fetcher extends FetchAble implements FolderOp {

    static class FetcherFactory extends CommandFactory {

        static FetcherFactory prepare() {
            return new FetcherFactory()
        }

        @Override
        Command get() {
            return new Fetcher()
        }
    }

    private Fetcher() {}

    @Override
    void run() {
        if (!projectFolderExists()) {
            throw new FileNotFoundException("${getProjectPath()} not found")
        } else {
            fetchProject()
        }
    }

    private void fetchProject() {
        Git git = workspace.getGit(project)
        Path local = getProjectPath()

        FetchCommand fetch = git.fetch()
        configureTransportCommand(fetch)

        List<RefSpec> specs = new ArrayList<RefSpec>()
        specs.add(new RefSpec("+refs/heads/*:refs/remotes/origin/*"))
        specs.add(new RefSpec("+refs/tags/*:refs/tags/*"))
        specs.add(new RefSpec("+refs/notes/*:refs/notes/*"))
        fetch.setRefSpecs(specs)
        fetch.setRemoveDeletedRefs(true)

        logger.info("Fetch ${local}")
        fetch.setProgressMonitor(new TextProgressMonitor(TextBuiltin.get().loggerWriter));

        fetch.call()
    }

}