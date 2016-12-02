package fr.coppernic.utils.grepo

import ch.qos.logback.classic.Level
import org.junit.*
import org.junit.rules.TemporaryFolder
import org.slf4j.LoggerFactory

import java.nio.file.Path
import java.nio.file.Paths
/**
 * Class to test Grepo
 */
@Ignore
public class FetchTest implements Resources {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder()
    @Rule
    public TemporaryFolder temp = new TemporaryFolder()

    Path pathSecondWksp;

    @Before
    void before() {
        pathWorkspace = Paths.get(temporaryFolder.root.absolutePath, "workspace")
        pathSecondWksp = Paths.get(temp.root.absolutePath)

        //LoggerFactory.getLogger(Defines.GIT_LOG_NAME).setLevel(Level.TRACE)
        LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).setLevel(Level.INFO)
    }

    @After
    void after() {
    }

    @Test
    void fetchSimple() {
        Grepo grepo = Grepo.Builder.create(pathWorkspace, pathManifest)
        grepo.load()
        grepo.fetch()

        File f = pathWorkspace.toFile()
        assert f.exists()
        assert f.isDirectory()

        testFileInWorkspace("Root/README.md")
        testFileInWorkspace("Root/Folder/RepoTest1/README.md")
        testFileInWorkspace("Here/RepoTest2/README.md")
        testFileInWorkspace("There/RepoTest3/README.md")

        assert grepo.workspace.gitMap["Root"]
        assert grepo.workspace.gitMap["Root/Folder/RepoTest1"]
        assert grepo.workspace.gitMap["Here/RepoTest2"]
        assert grepo.workspace.gitMap["There/RepoTest3"]
    }

    @Test
    void fetch() {
        Grepo grepo = Grepo.Builder.create(pathWorkspace, pathManifest)
        grepo.load()

        File f = pathWorkspace.toFile()
        assert f.exists()
        assert f.isDirectory()

        testFileInWorkspace("Root/Folder/RepoTest1/README.md")
        testFileNotInWorkspace("Root/Folder/RepoTest1/nouveau.txt")
        assert grepo.workspace.gitMap["Root/Folder/RepoTest1"]

        Path repoPath = pathSecondWksp.resolve("RepoTest1")
        assert 0 == executeOnShell("git clone git@github.com:bastienpaulfr/RepoTest1.git",
                pathSecondWksp)
        assert 0 == executeOnShell("echo \"pouet\" > nouveau.txt", repoPath)
        assert 0 == executeOnShell("git add nouveau.txt", repoPath)
        assert 0 == executeOnShell("git commit -m \"[DEV] feature\"", repoPath)
        assert 0 == executeOnShell("git tag 123", repoPath)
        assert 0 == executeOnShell("git checkout -b newBranch", repoPath)
        assert 0 == executeOnShell("git push --all", repoPath)
        assert 0 == executeOnShell("git push --tags", repoPath)


        grepo.fetch()

        assert 0 == executeOnShell("git checkout master", repoPath)
        assert 0 == executeOnShell("git reset --hard origin/goodBranch", repoPath)
        assert 0 == executeOnShell("git push -f origin master", repoPath)
        assert 0 == executeOnShell("git branch -D newBranch", repoPath)
        assert 0 == executeOnShell("git push origin :newBranch", repoPath)
        assert 0 == executeOnShell("git tag -d 123", repoPath)
        assert 0 == executeOnShell("git push origin :123", repoPath)

        repoPath = pathWorkspace.resolve("Root/Folder/RepoTest1/")
        testFileInWorkspace("Root/Folder/RepoTest1/README.md")

        StringBuilder sb = new StringBuilder()
        assert 0 == executeOnShell("git tag", repoPath, sb)
        assert sb.toString().contains("123")
        sb = new StringBuilder()
        assert 0 == executeOnShell("git branch -a", repoPath, sb)
        assert sb.toString().contains("origin/newBranch")
    }

    @Test(expected = FileNotFoundException.class)
    void fetchNoClone() {
        Grepo grepo = Grepo.Builder.create(pathWorkspace, pathManifest)
        grepo.fetch()
    }

    private static int executeOnShell(String command, Path workingDir) {
        executeOnShell(command, workingDir, null)
    }

    private static int executeOnShell(String command, Path workingDir, StringBuilder sb) {
        println command
        def process = new ProcessBuilder(addShellPrefix(command))
                .directory(workingDir.toFile())
                .redirectErrorStream(true)
                .start()
        if (sb != null) {
            sb.append(process.inputStream.text)
            println sb.toString()
        } else {
            println process.inputStream.text
        }

        process.waitFor();
        return process.exitValue()
    }

    private static String[] addShellPrefix(String command) {
        def commandArray = new String[3]
        commandArray[0] = "sh"
        commandArray[1] = "-c"
        commandArray[2] = command
        return commandArray
    }
}
