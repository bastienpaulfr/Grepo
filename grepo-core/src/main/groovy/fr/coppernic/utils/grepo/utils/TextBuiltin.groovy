package fr.coppernic.utils.grepo.utils

import org.eclipse.jgit.util.io.ThrowingPrintWriter
import org.slf4j.Logger
import org.slf4j.LoggerFactory

public class TextBuiltin {

    /**
     * Writer to output to, typically this is standard output.
     *
     */
    ThrowingPrintWriter outWriter

    /**
     * Error writer, typically this is standard error.
     *
     */
    ThrowingPrintWriter errWriter

    /**
     * Writer that print data to a slf4j logger
     */
    LoggerWriter loggerWriter

    /**
     * Logger used to print data
     */
    Logger logger = LoggerFactory.getLogger(Defines.GIT_LOG_NAME)

    private TextBuiltin() {
        BufferedWriter outBufW = new BufferedWriter(new OutputStreamWriter(System.out));
        outWriter = new ThrowingPrintWriter(outBufW);
        BufferedWriter errBufW = new BufferedWriter(new OutputStreamWriter(System.err));
        errWriter = new ThrowingPrintWriter(errBufW);
        loggerWriter = new LoggerWriter(logger)
    }

    static class Holder {
        static TextBuiltin instance = new TextBuiltin()
    }

    static synchronized TextBuiltin get() {
        Holder.instance
    }

    /**
     * Writer that print data using slf4j logger
     */
    static class LoggerWriter extends Writer {

        private final Logger logger

        public LoggerWriter(Logger logger) {
            this.logger = logger
        }

        @Override
        void write(char[] buf, int off, int len) throws IOException {
            logger.trace(new String(buf, off, len))
        }

        @Override
        void flush() throws IOException {

        }

        @Override
        void close() throws IOException {

        }
    }
}