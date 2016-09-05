package fr.coppernic.utils.core

import org.eclipse.jgit.util.io.ThrowingPrintWriter


public class TextBuiltin {
    /**
     * Input stream, typically this is standard input.
     *
     */
    InputStream ins;

    /**
     * Writer to output to, typically this is standard output.
     *
     */
    ThrowingPrintWriter outw;

    /**
     * Stream to output to, typically this is standard output.
     *
     */
    OutputStream outs;

    /**
     * Error writer, typically this is standard error.
     *
     */
    ThrowingPrintWriter errw;

    /**
     * Error output stream, typically this is standard error.
     *
     */
    OutputStream errs;

    private TextBuiltin() {
        if (ins == null)
            ins = new FileInputStream(FileDescriptor.in);
        if (outs == null)
            outs = new FileOutputStream(FileDescriptor.out);
        if (errs == null)
            errs = new FileOutputStream(FileDescriptor.err);
        BufferedWriter outbufw = new BufferedWriter(new OutputStreamWriter(outs));
        outw = new ThrowingPrintWriter(outbufw);
        BufferedWriter errbufw = new BufferedWriter(new OutputStreamWriter(errs));
        errw = new ThrowingPrintWriter(errbufw);
    }

    static class Holder {
        static TextBuiltin instance = new TextBuiltin()
    }

    static synchronized TextBuiltin get() {
        Holder.instance
    }
}