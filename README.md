# Grepo [![Travis Widget]][Travis] [![Licence Widget]][Mit] [![Codecov Widget]][Codecov]

[Travis Widget]: https://travis-ci.org/bastienpaulfr/Grepo.svg?branch=master
[Travis]: https://travis-ci.org/bastienpaulfr/Grepo
[Licence Widget]: https://img.shields.io/github/license/mashape/apistatus.svg?maxAge=2592000 
[Mit]: https://opensource.org/licenses/MIT
[Codecov Widget]: https://codecov.io/gh/bastienpaulfr/Grepo/branch/master/graph/badge.svg 
[Codecov]: https://codecov.io/gh/bastienpaulfr/Grepo 

repo like tool written in Groovy

## Introduction

Android [repo](https://source.android.com/source/downloading.html) tool has its own manner to handle git repositories. It is fine for many but is a bit complicated for the way I'm managing many git repositories.
I was looking for a tool to handle more than 100 git repositories. I had some difficulites to find one that is satisfying. So I started developping Grepo. 
Grepo is a library and soon a command line tool to clone, checkout and perform other git operation with many repo. It is based on an xml file that describes all repo managed by Grepo. Grepo is written in 
[Groovy](http://www.groovy-lang.org/). 

## Building

    ./gradlew build

## Usage

```groovy
// Path to the manifest describing projects to clone
Path manifestPath = Paths.get("path/to/manifest")
// Path to the workspace containing all git repo
Path workspace = Paths.get("path/to/clone/repo")

// Get an instance of Grepo
Grepo grepo = Grepo.Builder.create(workspace,manifestPath)

// This will clone all manifests
grepo.load()

// Clone all repo and checkout them to the revision specified in manifest
grepo.loadAndCheckout()
```

## Manifest

    <?xml version="1.0" encoding="UTF-8" standalone="no" ?>
    <manifest>
        <!-- this node represent a git server. It can be Github, Gitlab or your own server -->
        <remote name="github" fetch="git@github.com"/>
        <!-- Both ssh and http are supported but are limited-->
        <remote name="github-http" fetch="https://github.com"/>
    
        <!-- The project node is the main project of the manifest. There are at most one project per manifest.-->
        <project local_path="Root/Folder/RepoTest1" remote="github-http" remote_path="bastienpaulfr/RepoTest1.git" revision="1.2.3"/>
    
        <!-- The root node is a git project that will contain other sub projects.
        It is cloned first to be able to clone other git repo inside-->
        <root local_path="Root" remote="github-http" remote_path="bastienpaulfr/Grepo.git" revision="master"/>
    
        <!-- Clone node is for cloning regular git repo. Number of clone node is unlimited.-->
        <clone local_path="Here/RepoTest2" remote="github-http" remote_path="bastienpaulfr/RepoTest2.git" revision="9511df1bf75f67e4f2ba5b324f753bb4c1c00721"/>
        <clone local_path="There/RepoTest3" remote="github-http" remote_path="bastienpaulfr/RepoTest3.git" revision="dev"/>
    
    </manifest>

  - **local_path** : local path containing git repo
  - **remote** : Remote in which git repo is stored
  - **remote_path** : Path to append to remote's fetch attribute
  - **revision** : branch, tag or sha1 to checkout
  







