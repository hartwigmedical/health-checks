package com.hartwig.healthchecks.boggs.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.junit.Test;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.boggs.healthcheck.prestast.PrestatsData;
import com.hartwig.healthchecks.boggs.healthcheck.prestast.PrestatsExtractor;

public class PrestatsExtractorTest {
	private static final String DUMMY_RUN_DIR = "DummyRunDir";

	@Test
	public void canProcessRunDirectoryStructure() throws IOException {
		URL runDirURL = Resources.getResource("rundir");
		PrestatsExtractor extractor = new PrestatsExtractor();
		List<PrestatsData> prestatsData = extractor.extractFromRunDirectory(runDirURL.getPath().toString());
		assertNotNull("We should get some fails", prestatsData);
		assertEquals("Number of files that has failed is not correct", prestatsData.size(), 2);
	}

	@Test(expected = IOException.class)
	public void extractDataNoneExistingDir() throws IOException {
		PrestatsExtractor extractor = new PrestatsExtractor();
		extractor.extractFromRunDirectory(DUMMY_RUN_DIR);
	}
}
