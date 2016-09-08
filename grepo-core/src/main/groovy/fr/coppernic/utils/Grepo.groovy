package fr.coppernic.utils

import fr.coppernic.utils.command.Checkouter
import fr.coppernic.utils.command.Cloner
import fr.coppernic.utils.core.CommandFactory
import groovy.util.slurpersupport.GPathResult
import org.eclipse.jgit.api.Git

import java.nio.file.FileAlreadyExistsException
import java.nio.file.Path

/**
 * Class to execute all Grepo commands
 */
public class Grepo {

    static class Builder {
        static Grepo create(Path root, Path manifestPath) {
            GPathResult manifest = new XmlSlurper().parseText(manifestPath.toFile().text)
            create(root, manifest)
        }

        static Grepo create(Path root, GPathResult manifest) {
            new Grepo(root, manifest)
        }
    }

    private Grepo(Path root, GPathResult manifest) {
        this.root = root
        this.manifest = manifest

        validate()
        init()
    }

    private Path root
    private GPathResult manifest
    private Map<String, String> remoteMap = [:]
    private Map<String, Git> gitMap = [:]

    /**
     * Clone all repositories that are defined in xml
     */
    public void load() {
        createRootDir()
        CommandFactory factory = Cloner.ClonerFactory.prepare()
        factory.setAfterExecute { GPathResult project, Git git ->
            gitMap["${project.@local_path}"] = git
        }
        forAllGitRepo(factory)
    }

    /**
     * Clone all repositories that are defined in xml and checkout repo to the version specified
     * in xml file
     */
    public void loadAndCheckout() {
        load()
        Checkouter.CheckouterFactory factory = Checkouter.CheckouterFactory.prepare()
        factory.setGitMap(gitMap)
        forAllGitRepo(factory)
    }

    /**
     * Execute the command given by the factory for each repo defined in xml
     * @param factory Command factory
     */
    private void forAllGitRepo(CommandFactory factory) {
        factory.setRemotes(remoteMap)
                .setRoot(root)

        // there is one root per manifest
        factory.setProject(manifest.root as GPathResult).build().run()

        // there is at most one project per manifest
        manifest.project.each() { GPathResult it ->
            factory.setProject(it).build().run()
        }
        manifest.clone.each() { GPathResult it ->
            factory.setProject(it).build().run()
        }
    }

    /**
     * Validate xml file
     *
     * <ul>
     *     <li> At least one remote node
     *     <li> At most one project node
     *     <li> At most one root node
     * </ul>
     */
    private void validate() {
        if (manifest.remote.size() == 0) {
            throw new RuntimeException('No remote file in xml')
        }
        if (manifest.project.size() > 1) {
            throw new RuntimeException("Too many 'project' node, only one is allowed")
        }
        if (manifest.root.size() > 1) {
            throw new RuntimeException("Too many 'root' node, only one is allowed")
        }
    }

    private void init() {
        manifest.remote.each {
            remoteMap["${it.@name}"] = "${it.@fetch}"
        }
    }

    private boolean createRootDir() {
        File fRoot = root.toFile();
        if (fRoot.exists()) {
            if (fRoot.isDirectory()) {
                return true
            } else {
                throw new FileAlreadyExistsException("$root already exists and is not a directory")
            }
        } else {
            fRoot.mkdirs()
        }
    }

}
