package fr.coppernic.utils.grepo.core

import ch.qos.logback.classic.Level
import fr.coppernic.utils.grepo.Resources
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.slf4j.LoggerFactory

import java.nio.file.Paths

/**
 * Created on 05/12/16
 * @author bastien
 */
class WorkspaceTest implements Resources {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder()


    @Before
    void before() {
        pathWorkspace = Paths.get(temporaryFolder.root.absolutePath, "workspace")

        //LoggerFactory.getLogger(Defines.GIT_LOG_NAME).setLevel(Level.TRACE)
        LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).setLevel(Level.INFO)
    }

    @Test
    public void loadManifestLevel() {
        Workspace w = new Workspace(pathWorkspace)
        w.load(pathManifestLevel)

        assert w.projectMap.keySet()[0] == "One/RepoTest2"
        assert w.projectMap.keySet()[5] == "One/Two/Three/RepoTest3"
    }
}
