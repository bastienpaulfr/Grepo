package fr.coppernic.utils.grepo.core

import fr.coppernic.utils.grepo.command.CommandFactory
import groovy.util.slurpersupport.GPathResult
import org.apache.commons.lang3.StringUtils
import org.eclipse.jgit.api.Git

import java.nio.file.FileAlreadyExistsException
import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Workspace class.
 *
 * Class holding all workspace git repo
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
    private Map<String, Git> gitMap = new ConcurrentHashMap<String, Git>()
    private Map<String, Project> projectMap = new ConcurrentSkipListMap<String, Project>(new ProjectPathLevelComparator())

    void load(Path p) {
        load(p.toFile())
    }

    void load(File f) {
        load(new XmlSlurper().parseText(f.text))
    }

    void load(GPathResult result) {
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

    void setGit(Project p, Git g) {
        gitMap[p.localPath] = g
    }

    Git getGit(Project p) {
        Git g = gitMap[p.localPath]
        if (!g) {
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
        projectMap.each { String k, Project p ->
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
            projectMap[p.localPath] = p
        }
        xml.clone.each {
            Project p = Project.from(it, remoteMap)
            projectMap[p.localPath] = p
        }
        xml.root.each {
            Project p = Project.from(it, remoteMap)
            projectMap[p.localPath] = p
        }
    }

    private void clearAll() {
        remoteMap.clear()
        projectMap.clear()
    }

    static class ProjectPathLevelComparator implements Comparator<String> {

        @Override
        int compare(String o1, String o2) {
            int c1 = StringUtils.countMatches(o1, File.separatorChar)
            int c2 = StringUtils.countMatches(o2, File.separatorChar)

            int ret = c1 - c2
            return ret ? ret : o1.compareTo(o2)
        }
    }
}
