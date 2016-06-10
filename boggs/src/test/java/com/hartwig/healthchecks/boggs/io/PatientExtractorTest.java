package com.hartwig.healthchecks.boggs.io;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.boggs.flagstatreader.FlagStatData;
import com.hartwig.healthchecks.boggs.flagstatreader.FlagStatParser;
import com.hartwig.healthchecks.boggs.healthcheck.mapping.PatientExtractor;
import com.hartwig.healthchecks.boggs.model.data.PatientData;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import mockit.Mocked;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PatientExtractorTest {

	@Mocked
	private FlagStatData flagStatData;

	@Test
	public void canProcessRunDirectoryStructure() throws IOException, EmptyFileException {
		URL runDirURL = Resources.getResource("rundir");

        PatientExtractor extractor = new PatientExtractor(new DummyFlagstatParser());
		PatientData patient = extractor.extractFromRunDirectory(runDirURL.getPath());

		assertNotNull("Patient data should not be null", patient);
		assertEquals("Ref External Id Not correct", "CPCT00R", patient.getRefSample().getExternalId());
		assertEquals("Ref size of sorted Mapping not correct", 2,
				patient.getRefSample().getSortedMappingFlagstats().size());
		assertEquals("Ref size of raw Mapping not correct", 2, patient.getRefSample().getRawMappingFlagstats().size());
		assertEquals("Tumor External Id Not correct", "CPCT00T", patient.getTumorSample().getExternalId());
		assertEquals("Tumor size of sorted Mapping not correct", 2,
				patient.getTumorSample().getSortedMappingFlagstats().size());
		assertEquals("Tumor size of raw Mapping not correct", 2,
				patient.getTumorSample().getRawMappingFlagstats().size());

	}

	class DummyFlagstatParser implements FlagStatParser {

		@NotNull
		public FlagStatData parse(@NotNull String filePath) throws IOException, EmptyFileException {
			return flagStatData;
		}
	}
}
