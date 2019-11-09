package nwh.legendkeeper.gateway;

/**
 * Class used to contain all data for a
 * given logout response such as status
 * 
 * @author Nathanial.W.Heard
 *
 */
public class LogoutResponse {
	
	private String status;
	
	public LogoutResponse() {
		// Default
	}
	
	public LogoutResponse(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
