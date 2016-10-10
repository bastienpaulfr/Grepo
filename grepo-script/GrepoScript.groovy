@GrabResolver(name='artifactory', root='http://arti-01:8081/artifactory/libs-release-local/')
@GrabResolver(name='local', root='/home/bastien/Workspace/ProjectManagement/Grepo/repo')
@Grab(group='commons-cli', module='commons-cli', version='1.2')

class GrepoTool {

    final CliBuilder cmdLine

    GrepoTool(){
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
        }
    }

    void handleOptions(OptionAccessor opt){
        if(!opt){
            cmdLine.usage()
        } else if (opt.h){
            cmdLine.usage()
        } else {

        }
    }
}

GrepoTool g = new GrepoTool()
g.run(args)
