package fr.coppernic.utils.grepo.core

import groovy.transform.Immutable
import groovy.util.slurpersupport.GPathResult

import java.nio.file.Paths;

/**
 */
@Immutable class Project {

    String localPath = ""
    String remotePath = ""
    String name = ""
    String revision = ""
    Remote remote = null

    public static Project from (GPathResult p, Map<String, Remote> remoteMap){
        String localPath = p.@local_path
        Project project = new Project(localPath: localPath,
                remotePath: p.@remote_path,
                revision: "${p.@revision}".trim(),
                name: Paths.get(localPath).getFileName().toString(),
                remote: remoteMap["${p.@remote}"])

        assert project.remotePath != "", "Remote path shall be specified"
        assert project.localPath != "", "Local path shall be specified"
        return project
    }

    String getGitUri(){
        remote.getProjectGitUri(this)
    }

    @Override
    String toString() {
        return localPath
    }
}
