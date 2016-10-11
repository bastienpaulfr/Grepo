
@GrabResolver(name='artifactory', root='http://arti-01:8081/artifactory/libs-release-local/')
@GrabResolver(name='local', root='/home/bastien/Workspace/ProjectManagement/Grepo/repo')
@Grab(group='fr.coppernic.utils', module= 'grepo-core', version= '0.1.1')
@Grab(group='commons-cli', module='commons-cli', version='1.2')
@Grab(group='ch.qos.logback', module='logback-classic', version='1.1.7')

import fr.coppernic.utils.grepo.Grepo

import java.nio.file.Path
import java.nio.file.Paths

class GrepoTool {

    final CliBuilder cmdLine

    GrepoTool(){
        LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).setLevel(Level.INFO)
        cmdLine = new CliBuilder(usage: 'GrepoTool.groovy [load]')
        prepareCmdLineParser()
    }

    void run(String[] args){
        OptionAccessor opt = cmdLine.parse(args)
        handleOptions(opt)
    }

    void prepareCmdLineParser() {
        // Create the list of options.
        cmdLine.with {
            h longOpt: 'help', 'Show usage information'
            l longOpt: 'load', args: 1, argName: 'file.xml', 'load repo from xml file'
            w longOpt: 'workspace', args: 1, argName: 'dir', 'Path to the workspace'
        }
    }

    void handleOptions(OptionAccessor opt){
        if(!opt){
            cmdLine.usage()
        } else if (opt.h){
            cmdLine.usage()
        } else if (opt.l){
            load(opt)
        } else {
            cmdLine.usage()
        }
    }

    void load(OptionAccessor opt){
        Path path = Paths.get(opt.l).normalize().toAbsolutePath()
        Grepo grepo = Grepo.Builder.create(Paths.get(opt.w?opt.w:""), path)
        grepo.load()
        grepo.checkout()
    }
}

GrepoTool g = new GrepoTool()
g.run(args)
