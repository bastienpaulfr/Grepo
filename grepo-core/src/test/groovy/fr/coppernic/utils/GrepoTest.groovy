package fr.coppernic.utils

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.core.util.StatusPrinter
import org.eclipse.jgit.api.errors.RefNotFoundException
import org.junit.*
import org.junit.rules.TemporaryFolder
import org.slf4j.LoggerFactory

import java.nio.file.FileAlreadyExistsException
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Class to test Grepo
 */
public class GrepoTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder()

    private static String prot = "http" //http or ssh

    private static Path pathManifest = Paths
            .get("src/test/resources/manifests/${prot}/manifest.xml")
    private static Path pathManifestFault1 = Paths
            .get("src/test/resources/manifests/${prot}/manifest-fault1.xml")
    private static Path pathManifestMore = Paths
            .get("src/test/resources/manifests/${prot}/manifest-more.xml")
    private static Path pathManifestWrongBranch = Paths
            .get("src/test/resources/manifests/${prot}/manifest-wrong-branch.xml")

    private static Path pathWorkspace = Paths.get("build/workspace")
    private static Path pathFile = Paths.get("build/file")


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
        assert grepo.root.toString() == ""
        assert grepo.manifest != null
        assert grepo.manifest.name() == "manifest"
        assert grepo.remoteMap['github'] == 'git@github.com'

        assert grepo.manifest.project.size() == 1
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

        assert grepo.gitMap["Root"]
        assert grepo.gitMap["Root/Folder/RepoTest1"]
        assert grepo.gitMap["Here/RepoTest2"]
        assert grepo.gitMap["There/RepoTest3"]
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

        assert grepo.gitMap["Root"]
        assert grepo.gitMap["Root/Folder/RepoTest1"]
        assert grepo.gitMap["Here/RepoTest2"]
        assert grepo.gitMap["There/RepoTest3"]
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

        assert grepo.gitMap["Root"]
        assert grepo.gitMap["Root/Folder/RepoTest1"]
        assert grepo.gitMap["Here/RepoTest2"]
        assert grepo.gitMap["There/RepoTest3"]
        assert grepo.gitMap["Root/Folder/RepoTest2"]
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

        assert grepo.gitMap["Root"]
        assert grepo.gitMap["Root/Folder/RepoTest1"]
        assert grepo.gitMap["Here/RepoTest2"]
        assert grepo.gitMap["There/RepoTest3"]
    }

    @Test
    void loadAndCheckoutTwice() {
        Grepo grepo = Grepo.Builder.create(pathWorkspace, pathManifest)

        grepo.loadAndCheckout()

        grepo = Grepo.Builder.create(pathWorkspace, pathManifestMore)
        grepo.loadAndCheckout()

        assert grepo.gitMap["Root"]
        assert grepo.gitMap["Root/Folder/RepoTest1"]
        assert grepo.gitMap["Here/RepoTest2"]
        assert grepo.gitMap["There/RepoTest3"]
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

    static void testFileInWorkspace(String path) {
        File f = Paths.get(pathWorkspace.toString(), path).toFile()
        assert f.exists()
        assert f.isFile()
    }

}
