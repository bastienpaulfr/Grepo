package fr.coppernic.utils.grepo.command

import fr.coppernic.utils.grepo.core.Project
import org.eclipse.jgit.api.CheckoutCommand
import org.eclipse.jgit.api.CreateBranchCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.api.errors.JGitInternalException
import org.eclipse.jgit.api.errors.RefNotFoundException
import org.eclipse.jgit.errors.LockFailedException
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.lib.Repository
/**
 * Checkout command. This command checkout a repo git to a branch, tag or commit
 */
class Checkouter extends Command {

    /**
     * Factory
     */
    static class CheckouterFactory extends CommandFactory {

        static CheckouterFactory prepare() {
            return new CheckouterFactory()
        }

        @Override
        Command get() {
            new Checkouter()
        }
    }

    private int nbTry = 0

    private Checkouter() {}

    @Override
    void run() {
        checkout(project)
    }

    private void checkout(Project project) {
        Git git = workspace.getGit(project)

        CheckoutCommand checkout = git.checkout()
        logger.info("Checkout repo ${project.localPath} to ${project.revision}")

        checkout.setName(project.revision)
        try {
            checkout.call()
        } catch (RefNotFoundException e) {
            //println e.toString()
            if (!checkoutRemoteBranch(git, project.revision)) {
                throw e
            }
        } catch (JGitInternalException e) {
            //println e.toString()
            if (e.cause instanceof LockFailedException && nbTry < 1) {
                nbTry++
                recoverFromLockException(git)
                this.checkout(project)
            } else {
                throw e
            }
        }
    }

    private static boolean checkoutRemoteBranch(Git git, String revision) {
        boolean ret = false
        ListBranchCommand command = git.branchList();
        command.setListMode(ListBranchCommand.ListMode.REMOTE);
        List<Ref> refs = command.call();
        for (Ref r : refs) {
            String refName = r.getName() - 'refs/remotes/'
            String name = refName.substring(refName.lastIndexOf('/') + 1)
            if (name == revision) {
                ret = checkoutRef(git, refName, name)
                break;
            }
        }
        return ret
    }

    private static boolean checkoutRef(Git git, String ref, String name) {
        CheckoutCommand checkout = git.checkout()
        checkout.setName(name).setCreateBranch(true)
                .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM)
                .setStartPoint(ref)
        checkout.call()
        return true
    }

    private static void recoverFromLockException(Git git) {
        Repository repo = git.repository
        File lock = new File(repo.getIndexFile().absolutePath + ".lock")
        lock.delete()
    }
}
