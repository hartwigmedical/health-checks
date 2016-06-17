package com.hartwig.healthchecks.boggs.healthcheck.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.net.URL;

import org.junit.Test;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.boggs.flagstatreader.FlagStatParser;
import com.hartwig.healthchecks.boggs.flagstatreader.FlagStatTestFactory;
import com.hartwig.healthchecks.boggs.model.report.MappingDataReport;
import com.hartwig.healthchecks.boggs.model.report.MappingReport;
import com.hartwig.healthchecks.common.exception.EmptyFileException;

import mockit.Expectations;
import mockit.Mocked;

public class MappingExtractorTest {
	private static final String EMPTY_FILES = "emptyFiles";

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
		MappingDataReport mappingDataReport = mappingReport.getMappingDataReport();
		assertFalse("Not All Read are Present", mappingDataReport.isAllReadsPresent());
		assertEquals(99.69d, mappingDataReport.getMappedPercentage(), 0d);
		assertEquals(0.0d, mappingDataReport.getMateMappedToDifferentChrPercentage(), 0d);
		assertEquals(99.57d, mappingDataReport.getProperlyPairedPercentage(), 0d);
		assertEquals(55.0d, mappingDataReport.getSingletonPercentage(), 0d);
		assertEquals(5.95d, mappingDataReport.getProportionOfDuplicateRead(), 0d);
	}

	@Test(expected = IOException.class)
	public void extractDataNoneExistingDir() throws IOException, EmptyFileException {
		MappingExtractor extractor = new MappingExtractor(flagstatParser);
		extractor.extractFromRunDirectory(DUMMY_RUN_DIR);
	}

	@Test(expected = EmptyFileException.class)
	public void extractDataEmptyFile() throws IOException, EmptyFileException {
		URL exampleFlagStatURL = Resources.getResource(EMPTY_FILES);
		String path = exampleFlagStatURL.getPath();
		MappingExtractor extractor = new MappingExtractor(flagstatParser);
		extractor.extractFromRunDirectory(path);
	}
}
