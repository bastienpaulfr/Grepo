package fr.coppernic.utils.grepo.core

import groovy.transform.Immutable
import groovy.util.slurpersupport.GPathResult;

/**
 * Created by bastien on 03/11/16.
 */

@Immutable class Remote {

    String name = ""
    String fetch = ""

    public static Remote from (GPathResult r){
        Remote remote = new Remote(name: r.@name, fetch: r.@fetch)

        assert remote.name, "remote name shall be specified"
        assert remote.fetch, "remote fetch attribute shall be specified"
        return remote
    }

    String getProjectGitUri(Project p) {
        String sep
        if (fetch.startsWith("http")) {
            sep = "/"
        } else if (fetch.contains("@")) {
            sep = ":"
        } else {
            throw new MalformedURLException()
        }
        return fetch + sep + p.remotePath
    }

}
