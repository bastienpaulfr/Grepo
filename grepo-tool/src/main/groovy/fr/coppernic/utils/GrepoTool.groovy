package fr.coppernic.utils

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.ConsoleAppender
import fr.coppernic.utils.grepo.Grepo
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.nio.file.Path
import java.nio.file.Paths

class GrepoTool {

    final CliBuilder cmdLine

    GrepoTool(){
        configureLogging()
        cmdLine = new CliBuilder(usage: 'GrepoTool.groovy [load]')
        prepareCmdLineParser()
    }

    static void configureLogging(){
        Logger rootLogger = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        LoggerContext loggerContext = rootLogger.getLoggerContext();
        // we are not interested in auto-configuration
        loggerContext.reset();

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(loggerContext);
        encoder.setPattern("[%-5level] [%thread] [%logger{0}] : %message%n");
        encoder.start();

        ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<ILoggingEvent>();
        appender.setContext(loggerContext);
        appender.setEncoder(encoder);
        appender.start();

        rootLogger.addAppender(appender);
        rootLogger.setLevel(Level.INFO)
    }

    void run(String[] args){
        OptionAccessor opt = cmdLine.parse(args)
        handleOptions(opt)
    }

    void prepareCmdLineParser() {
        // Create the list of options.
        cmdLine.with {
            h longOpt: 'help', 'Show usage information'
            l longOpt: 'load', args: 1, argName: 'file.xml', 'Load repo from xml file'
            w longOpt: 'workspace', args: 1, argName: 'dir', 'Path to the workspace'
            f longOpt: 'fetch', args: 1, argName: 'file.xml', 'Do a fetch --all on all repositories'
        }
    }

    void handleOptions(OptionAccessor opt){
        if(!opt){
            return
        }

        if (opt.h){
            cmdLine.usage()
        } else if (opt.l){
            load(opt)
        } else {
            cmdLine.usage()
        }
    }

    @Override
    void setProperty(String property, Object newValue) {
        super.setProperty(property, newValue)
    }

    void load(OptionAccessor opt){
        Path path = Paths.get(opt.l).normalize().toAbsolutePath()
        Grepo grepo = Grepo.Builder.create(Paths.get(opt.w?opt.w:""), path)
        grepo.load()
        grepo.checkout()
    }

    void fetch(OptionAccessor opt){
        Path path = Paths.get(opt.f).normalize().toAbsolutePath()
        Grepo grepo = Grepo.Builder.create(Paths.get(opt.w?opt.w:""), path)
        grepo.fetch()
    }

    static void main(String... args) {
        //println args
        GrepoTool g = new GrepoTool()
        g.run(args)
    }
}