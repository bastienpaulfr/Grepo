package fr.coppernic.utils

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.springframework.boot.test.rule.OutputCapture
import spock.lang.Specification
/**
 */
class GrepoToolTest extends Specification {
    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();
    @Rule
    OutputCapture capture = new OutputCapture()

    static String shared = ""

    @Before
    public void before(){
        shared = folder.root
    }

    @After
    public void after(){
        new File("Root").deleteDir()
        new File("Here").deleteDir()
        new File("There").deleteDir()
    }

    static final String usage = """usage: GrepoTool.groovy [load]
 -h,--help              Show usage information
 -l,--load <file.xml>   load repo from xml file
 -w,--workspace <dir>   Path to the workspace
"""

    static final String loadUsage = """error: Missing argument for option: l
usage: GrepoTool.groovy [load]
 -h,--help              Show usage information
 -l,--load <file.xml>   load repo from xml file
 -w,--workspace <dir>   Path to the workspace
"""
    static final String loadUsageW = """error: Missing argument for option: w
usage: GrepoTool.groovy [load]
 -h,--help              Show usage information
 -l,--load <file.xml>   load repo from xml file
 -w,--workspace <dir>   Path to the workspace
"""
    static final String load = """[INFO ] [Cloner] : Clone https://github.com/bastienpaulfr/Grepo.git in Root
[INFO ] [Cloner] : Clone https://github.com/bastienpaulfr/RepoTest1.git in Root/Folder/RepoTest1
[INFO ] [Cloner] : Clone https://github.com/bastienpaulfr/RepoTest2.git in Here/RepoTest2
[INFO ] [Cloner] : Clone https://github.com/bastienpaulfr/RepoTest3.git in There/RepoTest3
[INFO ] [Checkouter] : Checkout repo Root to master
[INFO ] [Checkouter] : Checkout repo Root/Folder/RepoTest1 to 1.2.3
[INFO ] [Checkouter] : Checkout repo Here/RepoTest2 to 9511df1bf75f67e4f2ba5b324f753bb4c1c00721
[INFO ] [Checkouter] : Checkout repo There/RepoTest3 to dev
"""

    static final String manifest = "src/test/resources/manifests/http/manifest.xml"

    def "execute tool for usage"(){
        when:
            GrepoTool.main(options)
        then:
            assert capture.toString() == usage
        where:
            options << ["","-h","--help", "prout"]
    }

    def "load opt invalid"(){
        when:
        GrepoTool.main(options)
        then:
        assert capture.toString() == usage
        where:
        options | usage
        ["-l"].toArray() as String[] | loadUsage
        ["-l", manifest,"-w"].toArray() as String[] | loadUsageW
    }

    def "load workspace"(){
        when:
        GrepoTool.main(options)
        then:
        println "ok"
        where:
        options << [["-l", manifest].toArray() as String[],
                    ["-l", manifest,"-w", shared ].toArray() as String[]]
    }
}
