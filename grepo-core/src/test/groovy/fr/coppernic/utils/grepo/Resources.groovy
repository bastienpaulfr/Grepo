package fr.coppernic.utils.grepo

import java.nio.file.Path
import java.nio.file.Paths;

/**
 * Created by bastien on 03/11/16.
 */

public trait Resources {
    String prot = "http" //http or ssh

    Path pathManifest = Paths
            .get("src/test/resources/manifests/${prot}/manifest.xml")
    Path pathManifestFault1 = Paths
            .get("src/test/resources/manifests/${prot}/manifest-fault1.xml")
    Path pathManifestMore = Paths
            .get("src/test/resources/manifests/${prot}/manifest-more.xml")
    Path pathManifestWrongBranch = Paths
            .get("src/test/resources/manifests/${prot}/manifest-wrong-branch.xml")
    Path pathManifestLevel = Paths
            .get("src/test/resources/manifests/${prot}/manifest-level.xml")
    Path pathManifestProject = Paths
            .get("src/test/resources/manifests/other/manifest-project.xml")

    Path pathWorkspace = Paths.get("build/workspace")
    Path pathFile = Paths.get("build/file")

    void testFileInWorkspace(String path) {
        File f = Paths.get(pathWorkspace.toString(), path).toFile()
        assert f.exists()
        assert f.isFile()
    }

    void testFileNotInWorkspace(String path) {
        File f = Paths.get(pathWorkspace.toString(), path).toFile()
        assert !f.exists()
    }
}
