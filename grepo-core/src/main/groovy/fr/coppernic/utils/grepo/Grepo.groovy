package fr.coppernic.utils.grepo

import fr.coppernic.utils.grepo.command.Checkouter
import fr.coppernic.utils.grepo.command.Cloner
import fr.coppernic.utils.grepo.command.CommandFactory
import fr.coppernic.utils.grepo.command.Fetcher
import fr.coppernic.utils.grepo.core.Project
import fr.coppernic.utils.grepo.core.Workspace
import groovy.util.slurpersupport.GPathResult
import org.eclipse.jgit.api.Git

import java.nio.file.Path
/**
 * Class to execute all Grepo commands
 */
public class Grepo {

    static class Builder {
        static Grepo create(Path root, Path manifestPath) {
            Workspace w = new Workspace(root)
            w.load(manifestPath)
            new Grepo(w)

        }

        static Grepo create(Path root, GPathResult manifest) {
            Workspace w = new Workspace(root)
            w.load(manifest)
            new Grepo(w)
        }
    }

    private Grepo(Workspace w) {
        workspace = w
    }

    final Workspace workspace

    /**
     * Clone all repositories that are defined in xml
     */
    public void load() {
        workspace.createRootDir()
        CommandFactory factory = Cloner.ClonerFactory.prepare()
        factory.setAfterExecute { Project project, Git git ->
            workspace.setGit(project,git)
        }
        factory.setWorkspace(workspace)
        workspace.executeCommandOnAllGitRepo(factory)
    }

    /**
     * Clone all repositories that are defined in xml and checkout repo to the version specified
     * in xml file
     */
    public void loadAndCheckout() {
        load()
        checkout()
    }

    /**
     * Checkout all project of manifest to specified branches
     */
    public void checkout() {
        Checkouter.CheckouterFactory factory = Checkouter.CheckouterFactory.prepare()
        factory.setWorkspace(workspace)
        workspace.executeCommandOnAllGitRepo(factory)
    }

    /**
     * Do an equivalent of git fetch --all on all projects
     */
    public void fetch() {
        Fetcher.FetcherFactory factory = Fetcher.FetcherFactory.prepare()
        factory.setWorkspace(workspace)
        workspace.executeCommandOnAllGitRepo(factory)
    }
}
