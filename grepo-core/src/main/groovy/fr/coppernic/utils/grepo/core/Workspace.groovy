package fr.coppernic.utils.grepo.core

import fr.coppernic.utils.grepo.command.CommandFactory
import groovy.util.slurpersupport.GPathResult
import org.eclipse.jgit.api.Git

import java.nio.file.FileAlreadyExistsException
import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by bastien on 03/11/16.
 */
public class Workspace {

    Workspace(Path root) {
        rootPath = root
    }

    final Path rootPath

    private GPathResult xml
    private Map<String, Remote> remoteMap = [:]
    /**
     * Hold every {@link org.eclipse.jgit.api.Git} instance for all git repo
     */
    private Map<String, Git> gitMap = [:] as ConcurrentHashMap<String, Git>
    private Map<String, Project> projMap = [:] as ConcurrentHashMap<String, Project>
    private Project rootProject

    void load(Path p){
        load(p.toFile())
    }

    void load(File f){
        load(new XmlSlurper().parseText(f.text))
    }

    void load(GPathResult result){
        xml = result
        validateXml()
        parseXml()
    }

    boolean createRootDir() {
        File fRoot = rootPath.toFile();
        if (fRoot.exists()) {
            if (fRoot.isDirectory()) {
                return true
            } else {
                throw new FileAlreadyExistsException("$rootPath already exists and is not a directory")
            }
        } else {
            fRoot.mkdirs()
        }
    }

    void setGit(Project p, Git g){
        gitMap[p.localPath] = g
    }

    Git getGit(Project p){
        Git g = gitMap[p.localPath]
        if(!g){
            g = Git.open(rootPath.resolve(p.localPath).toFile())
            gitMap[p.localPath] = g
        }
        return g
    }

    /**
     * Execute the command given by the factory for each repo defined in xml
     * @param factory Command factory
     */
    void executeCommandOnAllGitRepo(CommandFactory factory) {
        factory.setRemotes(remoteMap).setRootDir(rootPath)

        // there is one root per manifest
        factory.setProject(rootProject).build().run()

        projMap.each { String k, Project p ->
            factory.setProject(p).build().run()
        }
    }
    /**
     * Validate xml file
     *
     * <ul>
     *     <li> At least one remote node
     *     <li> At most one root node
     * </ul>
     */
    private void validateXml() {
        if (xml.remote.size() == 0) {
            throw new RuntimeException('No remote file in xml')
        }
        if (xml.root.size() > 1) {
            throw new RuntimeException("Too many 'root' node, only one is allowed")
        }
    }

    private void parseXml() {
        clearAll()

        xml.remote.each {
            Remote r = Remote.from(it)
            remoteMap[r.name] = r
        }
        xml.project.each {
            Project p = Project.from(it, remoteMap)
            projMap[p.localPath] = p
        }
        xml.clone.each {
            Project p = Project.from(it, remoteMap)
            projMap[p.localPath] = p
        }
        xml.root.each {
            rootProject = Project.from(it, remoteMap)
        }
    }

    private void clearAll(){
        remoteMap.clear()
        projMap.clear()
        rootProject = null
    }
}
