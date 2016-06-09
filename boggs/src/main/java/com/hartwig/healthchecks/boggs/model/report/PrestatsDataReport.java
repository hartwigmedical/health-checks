package com.hartwig.healthchecks.boggs.model.report;

public class PrestatsDataReport {

	private final String checkName;
	private final String status;
	private final String file;

	public PrestatsDataReport(String status, String checkName, String file) {
		this.status = status;
		this.checkName = checkName;
		this.file = file;
	}

	public String getCheckName() {
		return checkName;
	}

	public String getStatus() {
		return status;
	}

	public String getFile() {
		return file;
	}

	@Override
	public String toString() {
		return "PrestatsDataReport [checkName=" + checkName + ", status=" + status + ", file=" + file + "]";
	}
}
