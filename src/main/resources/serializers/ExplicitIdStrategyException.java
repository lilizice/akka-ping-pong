package serializers;

/**
 * @author Alvin
 * @since 27/7/12 5:26 PM
 */
@SuppressWarnings("serial")
public class ExplicitIdStrategyException extends RuntimeException {

	public ExplicitIdStrategyException(String message) {
		super(message);
	}

	public ExplicitIdStrategyException(Throwable cause) {
		super(cause);
	}
}
