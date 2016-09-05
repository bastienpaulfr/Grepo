package fr.coppernic.utils

import fr.coppernic.utils.core.Checkouter
import fr.coppernic.utils.core.Cloner
import fr.coppernic.utils.core.Command.CommandFactory
import groovy.util.slurpersupport.GPathResult
import org.eclipse.jgit.api.Git

import java.nio.file.FileAlreadyExistsException
import java.nio.file.Path

/**
 * Created by bastien on 10/05/16.
 */
public class Grepo {

    static class Builder {
        static Grepo newInstance(Path root, Path manifestPath) {
            GPathResult manifest = new XmlSlurper().parseText(manifestPath.toFile().text)
            newInstance(root, manifest)
        }

        static Grepo newInstance(Path root, GPathResult manifest) {
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
    private def remoteMap = [:]
    private def gitMap = [:]
    private boolean enableLog = true

    public load() {
        createRootDir()
        CommandFactory factory = Cloner.ClonerFactory.prepare()
        factory.setAfterExecute { GPathResult project, Git git ->
            gitMap["${project.@local_path}"] = git
        }
        forAllGitRepo(factory)
        //println(gitMap)
    }

    public loadAndCheckout() {
        load()
        Checkouter.CheckouterFactory factory = Checkouter.CheckouterFactory.prepare()
        factory.setGitMap(gitMap)
        forAllGitRepo(factory)
    }

    private forAllGitRepo(CommandFactory factory) {
        factory.setRemotes(remoteMap)
                .setEnableLog(enableLog)
                .setRoot(root)

        // there is one root per manifest
        factory.setProject(manifest.root).build().execute()

        // there is at most one project per manifest
        manifest.project.each() {
            factory.setProject(it).build().execute()
        }
        manifest.clone.each() {
            factory.setProject(it).build().execute()
        }
    }

    private validate() {
        if(manifest.remote.size() == 0){
            throw new RuntimeException('No remote file in xml')
        }
        if(manifest.project.size() > 1){
            throw new RuntimeException("Too many 'project' node, only one is allowed")
        }
        if(manifest.root.size() > 1){
            throw new RuntimeException("Too many 'root' node, only one is allowed")
        }
    }

    private init() {
        manifest.remote.each {
            remoteMap["${it.@name}"] = "${it.@fetch}"
        }
    }

    private createRootDir() {
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
