package com.hartwig.healthchecks.boggs.healthcheck.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URL;

import org.junit.Test;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.boggs.flagstatreader.FlagStatParser;
import com.hartwig.healthchecks.boggs.flagstatreader.FlagStatTestFactory;
import com.hartwig.healthchecks.boggs.healthcheck.mapping.MappingExtractor;
import com.hartwig.healthchecks.boggs.model.report.MappingReport;
import com.hartwig.healthchecks.common.exception.EmptyFileException;

import mockit.Expectations;
import mockit.Mocked;

public class MappingExtractorTest {
	private static final String RUNDIR = "rundir";

	private static final String DUMMY_RUN_DIR = "DummyRunDir";

	@Mocked
	private FlagStatParser flagstatParser;

	@Test
	public void extractData() throws IOException, EmptyFileException {
		URL runDirURL = Resources.getResource(RUNDIR);
		String path = runDirURL.getPath();
		MappingExtractor extractor = new MappingExtractor(flagstatParser);
		new Expectations() {
			{
				flagstatParser.parse(anyString);
				returns(FlagStatTestFactory.createTestData());
			}
		};

		MappingReport mappingReport = extractor.extractFromRunDirectory(path);
		assertNotNull("We should have data", mappingReport);
		assertEquals(38.46d, mappingReport.getMappingDataReport().getMappedPercentage().doubleValue(),
				0d);
		assertEquals(0.0d, mappingReport.getMappingDataReport().getMateMappedToDifferentChrPercentage().doubleValue(),
				0d);
		assertEquals(0.0d, mappingReport.getMappingDataReport().getProperlyPairedPercentage().doubleValue(), 0d);
		assertEquals(57.69d, mappingReport.getMappingDataReport().getSingletonPercentage().doubleValue(),
				0d);
		assertEquals(0.0d, mappingReport.getMappingDataReport().getProportionOfDuplicateRead().doubleValue(), 0d);
	}

	@Test(expected = IOException.class)
	public void extractDataNoneExistingDir() throws IOException, EmptyFileException {
		MappingExtractor extractor = new MappingExtractor(flagstatParser);
		extractor.extractFromRunDirectory(DUMMY_RUN_DIR);
	}
}
