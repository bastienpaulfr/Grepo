package fr.coppernic.utils.core

import groovy.util.slurpersupport.GPathResult

import java.nio.file.Path

abstract class Command implements Runnable {
    boolean enableLog = true
    Map<String, String> remotes
    GPathResult project
    Path root
    Closure afterExecute
}