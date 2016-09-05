package fr.coppernic.utils.core

import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.Session
import com.jcraft.jsch.UserInfo
import org.eclipse.jgit.api.TransportCommand
import org.eclipse.jgit.api.TransportConfigCallback
import org.eclipse.jgit.transport.JschConfigSessionFactory
import org.eclipse.jgit.transport.OpenSshConfig
import org.eclipse.jgit.transport.SshTransport
import org.eclipse.jgit.transport.Transport
import org.eclipse.jgit.util.FS

abstract class FetchAble extends Command {

    static final boolean DEBUG = true

    final JschConfigSessionFactory sshSessionFactory = new JschConfigSessionFactory() {

        @Override
        protected void configure(OpenSshConfig.Host hc, Session session) {
            session.setConfig("StrictHostKeyChecking", "no");
            session.setUserInfo(new DefaultUserInfo())
        }

        @Override
        protected JSch createDefaultJSch(FS fs) throws JSchException {
            JSch defaultJSch = super.createDefaultJSch(fs);
            loadAllSshKeys(fs, defaultJSch)
            return defaultJSch;
        }

        private loadAllSshKeys(FS fs, JSch jSch) {
            final File home = fs.userHome();
            if (home == null)
                return;
            final File sshDir = new File(home, ".ssh"); //$NON-NLS-1$
            if (sshDir.isDirectory()) {
                sshDir.listFiles().each { File f ->
                    if (f.name.endsWith(".pub")) {
                        // Load all keys that have a public counterpart
                        loadCustomIdentity(jSch, new File(sshDir, f.name - ".pub"))
                    } // no else
                }
            }
        }

        private static void loadCustomIdentity(final JSch sch, final File privateKey) {
            if (DEBUG) {
                println "load $privateKey.name"
            }
            if (privateKey.isFile()) {
                try {
                    sch.addIdentity(privateKey.getAbsolutePath());
                } catch (JSchException ignore) {
                    // Instead, pretend the key doesn't exist.
                    if (DEBUG) {
                        println ignore.toString()
                    }
                }
            }
        }
    }

    final TransportConfigCallback transportConfigCallback = new TransportConfigCallback() {

        @Override
        void configure(Transport transport) {
            SshTransport sshTransport = (SshTransport) transport;
            sshTransport.setSshSessionFactory(sshSessionFactory);
        }
    }

    void configureTransportCommand(TransportCommand cmd) {
        cmd.setTransportConfigCallback(transportConfigCallback)
    }

    static String getGitUri(String remote, String path) {
        String sep
        if (remote.startsWith("http")) {
            sep = "/"
        } else if (remote.contains("@")) {
            sep = ":"
        } else {
            throw new MalformedURLException()
        }
        return remote + sep + path
    }

    public static class DefaultUserInfo implements UserInfo {

        @Override
        String getPassphrase() {
            return ""
        }

        @Override
        String getPassword() {
            return ""
        }

        @Override
        boolean promptPassword(String message) {
            println message
            return true
        }

        @Override
        boolean promptPassphrase(String message) {
            println message
            return true
        }

        @Override
        boolean promptYesNo(String message) {
            println message
            return true
        }

        @Override
        void showMessage(String message) {
            println message
        }
    }
}