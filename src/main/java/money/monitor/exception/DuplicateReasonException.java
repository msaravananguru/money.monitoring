package money.monitor.exception;

public class DuplicateReasonException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DuplicateReasonException(String message) {
		super(message);
	}
}
