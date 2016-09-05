package fr.coppernic.utils.core

import groovy.util.slurpersupport.GPathResult
import org.eclipse.jgit.api.CheckoutCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.api.errors.JGitInternalException
import org.eclipse.jgit.api.errors.RefNotFoundException
import org.eclipse.jgit.errors.LockFailedException
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.lib.Repository

class Checkouter extends Command {


    static class CheckouterFactory extends Command.CommandFactory {

        def gitMap = null

        static CheckouterFactory prepare() {
            return new CheckouterFactory()
        }

        Command.CommandFactory setGitMap(gitMap) {
            this.gitMap = gitMap
            this
        }

        @Override
        Command get() {
            Checkouter c = new Checkouter()
            c.setGitMap(gitMap)
            return c
        }
    }

    def gitMap = null
    private int nbTry = 0

    private Checkouter() {}

    @Override
    def execute() {
        checkout(project)
    }

    private checkout(GPathResult project) {
        Git git = gitMap["${project.@local_path}"]
        String revision = "${project.@revision}".trim()
        CheckoutCommand checkout = git.checkout()
        if (enableLog) {
            println("Checkout repo ${project.@local_path} to ${revision}")
        }
        checkout.setName(revision)
        try {
            checkout.call()
        } catch (RefNotFoundException ignored) {
            checkoutRemoteBranch(git, revision)
        } catch (JGitInternalException e) {
            if (e.cause instanceof LockFailedException && nbTry < 1) {
                nbTry++
                recoverFromLockException(git)
                this.checkout(project)
            }
        }
    }

    private checkoutRemoteBranch(Git git, String revision) {
        ListBranchCommand command = git.branchList();
        command.setListMode(ListBranchCommand.ListMode.REMOTE);
        List<Ref> refs = command.call();
        for (Ref r : refs) {
            String realName = r.getName() - 'refs/remotes/'
            String name = realName.substring(realName.lastIndexOf('/') + 1)
            if (name == revision) {
                checkoutRef(git, realName)
                break;
            }
        }
    }

    private checkoutRef(Git git, String rev) {
        CheckoutCommand checkout = git.checkout()
        checkout.setName(rev)
        checkout.call()
    }

    private recoverFromLockException(Git git) {
        Repository repo = git.repository
        File lock = new File(repo.getIndexFile().absolutePath + ".lock")
        lock.delete()
    }
}