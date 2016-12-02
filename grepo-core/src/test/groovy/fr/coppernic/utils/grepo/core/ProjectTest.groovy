package fr.coppernic.utils.grepo.core

import fr.coppernic.utils.grepo.Resources
import groovy.util.slurpersupport.GPathResult
import org.junit.Before
import org.junit.Test

/**
 * Created by bastien on 03/11/16.
 */
class ProjectTest implements Resources {

    GPathResult manifest

    @Before
    public void before() {
        manifest = new XmlSlurper().parseText(pathManifestProject.toFile().text)
    }

    @Test
    public void project() {
        Remote r = Remote.from(manifest.remote)
        Map<String, Remote> map = ["$r.name": r]
        Project p = Project.from(manifest.project, map)
        assert p.name == "RepoTest1"
        assert p.localPath == "Root/Folder/RepoTest1"
        assert p.remotePath == "bastienpaulfr/RepoTest1.git"
        assert p.revision == "1.2.3"
    }

    @Test
    public void remote() {
        Remote p = Remote.from(manifest.remote)
        assert p.name == "github"
        assert p.fetch == "git@github.com"
    }
}
