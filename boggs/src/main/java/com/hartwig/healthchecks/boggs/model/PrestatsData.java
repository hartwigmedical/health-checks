package com.hartwig.healthchecks.boggs.model;

import org.jetbrains.annotations.NotNull;

public class PrestatsData {

    @NotNull
	private String check;
    @NotNull
	private String file;

	public PrestatsData(@NotNull String check, @NotNull String file) {
		this.check = check;
		this.file = file;
	}

    @NotNull
	public String getCheck() {
		return check;
	}

    @NotNull
	public String getFile() {
		return file;
	}

	@Override
	public String toString() {
		return "PrestatsData{" +
				"check='" + check + '\'' +
				", file='" + file + '\'' +
				'}';
	}
}
