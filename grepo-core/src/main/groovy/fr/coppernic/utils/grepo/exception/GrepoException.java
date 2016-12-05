package fr.coppernic.utils.grepo.exception;

/**
 * Base class of all grepo exception
 * <p>
 * Created on 05/12/16
 *
 * @author bastien
 */

public class GrepoException extends Exception {
	public GrepoException() {
	}

	public GrepoException(Throwable cause) {
		super(cause);
	}

	public GrepoException(String message) {
		super(message);
	}

	public GrepoException(String message, Throwable cause) {
		super(message, cause);
	}

	public GrepoException(String message, Throwable cause, boolean enableSuppression,
	                      boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
