package com.hartwig.healthchecks.boggs.healthcheck.prestast;

import java.util.List;

public class PrestatsData {

	String name ;
	List<String> prestatsErrors;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getPrestatsErrors() {
		return prestatsErrors;
	}
	public void setPrestatsErrors(List<String> prestatsErrors) {
		this.prestatsErrors = prestatsErrors;
	}
	
}
