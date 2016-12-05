package fr.coppernic.utils.grepo

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.core.util.StatusPrinter
import fr.coppernic.utils.grepo.core.Remote
import org.eclipse.jgit.api.errors.RefNotFoundException
import org.junit.*
import org.junit.rules.TemporaryFolder
import org.slf4j.LoggerFactory

import java.nio.file.FileAlreadyExistsException
import java.nio.file.Paths

/**
 * Class to test Grepo
 */
public class LoadTest implements Resources {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder()


    @Before
    void before() {
        pathWorkspace = Paths.get(temporaryFolder.root.absolutePath, "workspace")

        //LoggerFactory.getLogger(Defines.GIT_LOG_NAME).setLevel(Level.TRACE)
        LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).setLevel(Level.INFO)
    }

    @After
    void after() {
    }

    @Test
    void constructor() {
        Grepo grepo = Grepo.Builder.create(Paths.get(""), pathManifest)
        assert grepo != null
        assert grepo.workspace.rootPath.toString() == ""
        assert grepo.workspace.xml != null
        assert grepo.workspace.xml.name() == "manifest"
        Remote r = grepo.workspace.remoteMap['github']
        assert r.fetch == 'git@github.com'
        assert grepo.workspace.xml.project.size() == 1
    }

    @Test
    void load() {
        // print internal state
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        StatusPrinter.print(lc);

        Grepo grepo = Grepo.Builder.create(pathWorkspace, pathManifest)
        grepo.load()

        File f = pathWorkspace.toFile()
        assert f.exists()
        assert f.isDirectory()

        testFileInWorkspace("Root/README.md")
        testFileInWorkspace("Root/Folder/RepoTest1/README.md")
        testFileInWorkspace("Here/RepoTest2/README.md")
        testFileInWorkspace("There/RepoTest3/README.md")
        testFileInWorkspace("Here/RepoTest3/README.md")

        assert grepo.workspace.gitMap["Root"]
        assert grepo.workspace.gitMap["Root/Folder/RepoTest1"]
        assert grepo.workspace.gitMap["Here/RepoTest2"]
        assert grepo.workspace.gitMap["There/RepoTest3"]
        assert grepo.workspace.gitMap["Here/RepoTest3"]
    }

    @Test
    void loadLevel() {
        // print internal state
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        StatusPrinter.print(lc);

        Grepo grepo = Grepo.Builder.create(pathWorkspace, pathManifestLevel)
        grepo.load()

        File f = pathWorkspace.toFile()
        assert f.exists()
        assert f.isDirectory()

        testFileInWorkspace("One/RepoTest2/README.md")
        testFileInWorkspace("One/RepoTest3/README.md")
        testFileInWorkspace("One/Two/RepoTest2/README.md")
        testFileInWorkspace("One/Two/RepoTest3/README.md")
        testFileInWorkspace("One/Two/Three/RepoTest2/README.md")
        testFileInWorkspace("One/Two/Three/RepoTest3/README.md")

        assert grepo.workspace.gitMap["One/RepoTest2"]
        assert grepo.workspace.gitMap["One/RepoTest3"]
        assert grepo.workspace.gitMap["One/Two/RepoTest2"]
        assert grepo.workspace.gitMap["One/Two/RepoTest3"]
        assert grepo.workspace.gitMap["One/Two/Three/RepoTest2"]
        assert grepo.workspace.gitMap["One/Two/Three/RepoTest3"]
    }

    @Ignore
    @Test
    void loadProfiling() {
        Grepo grepo = Grepo.Builder.create(pathWorkspace, pathManifest)

        profile {
            grepo.load()
        }.prettyPrint()

        File f = pathWorkspace.toFile()
        assert f.exists()
        assert f.isDirectory()

        testFileInWorkspace("Root/README.md")
        testFileInWorkspace("Root/Folder/RepoTest1/README.md")
        testFileInWorkspace("Here/RepoTest2/README.md")
        testFileInWorkspace("There/RepoTest3/README.md")
        testFileInWorkspace("Here/RepoTest3/README.md")

        assert grepo.workspace.gitMap["Root"]
        assert grepo.workspace.gitMap["Root/Folder/RepoTest1"]
        assert grepo.workspace.gitMap["Here/RepoTest2"]
        assert grepo.workspace.gitMap["There/RepoTest3"]
        assert grepo.workspace.gitMap["Here/RepoTest3"]
    }

    @Test
    void loadTwice() {
        load()

        Grepo grepo = Grepo.Builder.create(pathWorkspace, pathManifestMore)
        grepo.load()

        testFileInWorkspace("Root/README.md")
        testFileInWorkspace("Root/Folder/RepoTest1/README.md")
        testFileInWorkspace("Here/RepoTest2/README.md")
        testFileInWorkspace("There/RepoTest3/README.md")
        testFileInWorkspace("Root/Folder/RepoTest2/README.md")
        testFileInWorkspace("Here/RepoTest3/README.md")

        assert grepo.workspace.gitMap["Root"]
        assert grepo.workspace.gitMap["Root/Folder/RepoTest1"]
        assert grepo.workspace.gitMap["Here/RepoTest2"]
        assert grepo.workspace.gitMap["There/RepoTest3"]
        assert grepo.workspace.gitMap["Root/Folder/RepoTest2"]
    }

    @Test(expected = FileAlreadyExistsException.class)
    void loadError() {
        File f = pathFile.toFile()
        f.createNewFile()

        Grepo grepo = Grepo.Builder.create(pathFile, pathManifest)
        grepo.load()
    }

    @Test
    void loadAndCheckout() {
        Grepo grepo = Grepo.Builder.create(pathWorkspace, pathManifest)
        grepo.loadAndCheckout()

        assert grepo.workspace.gitMap["Root"]
        assert grepo.workspace.gitMap["Root/Folder/RepoTest1"]
        assert grepo.workspace.gitMap["Here/RepoTest2"]
        assert grepo.workspace.gitMap["There/RepoTest3"]
        assert grepo.workspace.gitMap["Here/RepoTest3"]
    }

    @Test
    void loadAndCheckoutTwice() {
        Grepo grepo = Grepo.Builder.create(pathWorkspace, pathManifest)

        grepo.loadAndCheckout()

        grepo = Grepo.Builder.create(pathWorkspace, pathManifestMore)
        grepo.loadAndCheckout()

        assert grepo.workspace.gitMap["Root"]
        assert grepo.workspace.gitMap["Root/Folder/RepoTest1"]
        assert grepo.workspace.gitMap["Here/RepoTest2"]
        assert grepo.workspace.gitMap["There/RepoTest3"]
    }

    @Test(expected = RefNotFoundException.class)
    void loadAndCheckoutWrongBranch() {
        Grepo grepo = Grepo.Builder.create(pathWorkspace, pathManifestWrongBranch)
        grepo.loadAndCheckout()
    }

    @Test(expected = RuntimeException.class)
    void loadFaulty() {
        Grepo grepo = Grepo.Builder.create(pathWorkspace, pathManifestFault1)
        grepo.load()

        File f = pathWorkspace.toFile()
        assert !f.exists()
    }

}
