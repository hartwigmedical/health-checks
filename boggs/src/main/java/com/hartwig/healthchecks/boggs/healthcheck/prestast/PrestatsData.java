package com.hartwig.healthchecks.boggs.healthcheck.prestast;

public class PrestatsData {

	private String check ;
	private String file;

	public PrestatsData(String check, String file) {
		this.check = check;
		this.file = file;
	}

	public String getCheck() {
		return check;
	}

	public void setCheck(String check) {
		this.check = check;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	@Override
	public String toString() {
		return "PrestatsData{" +
				"check='" + check + '\'' +
				", file='" + file + '\'' +
				'}';
	}
}
