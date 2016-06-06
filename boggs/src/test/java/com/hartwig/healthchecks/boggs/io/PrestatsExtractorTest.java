package com.hartwig.healthchecks.boggs.io;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.boggs.healthcheck.prestast.PrestatsData;
import com.hartwig.healthchecks.boggs.healthcheck.prestast.PrestatsExtractor;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PrestatsExtractorTest {
	private static final String DUMMY_RUN_DIR = "DummyRunDir";

	@Test
	public void canProcessRunDirectoryStructure() throws IOException {
		URL runDirURL = Resources.getResource("rundir");
		PrestatsExtractor extractor = new PrestatsExtractor();
		List<PrestatsData> prestatsData = extractor.extractFromRunDirectory(runDirURL.getPath().toString());
		assertNotNull("We should get some fails", prestatsData);
		assertEquals("Number of files that has failed is not correct", prestatsData.size(), 3);
	}

	@Test(expected = IOException.class)
	public void extractDataNoneExistingDir() throws IOException {
		PrestatsExtractor extractor = new PrestatsExtractor();
		extractor.extractFromRunDirectory(DUMMY_RUN_DIR);
	}
}
