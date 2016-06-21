# Health Checks

[![wercker status](https://app.wercker.com/status/a51d71fbe59d634461b37ab989d6f594/m/master "wercker status")](https://app.wercker.com/project/bykey/a51d71fbe59d634461b37ab989d6f594)

[![Coverage Status](https://coveralls.io/repos/github/hartwigmedical/health-checks/badge.svg?branch=master)](https://coveralls.io/github/hartwigmedical/health-checks?branch=master)

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/7dbf5374790240bca110940a9319d6d0)](https://www.codacy.com/app/ekho/health-checks?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=hartwigmedical/health-checks&amp;utm_campaign=Badge_Grade)

This repository consists of a set of utilities that perform health checks against genomics data.

# Build and Run the Health Checks

The Health Checks project uses Gradle as the build management tool. However, in order to build the project Gradle doesn't have to be installed. One can simply use the Gradle wrapper (i.e. gradlew) to build the project once it has been cloned. The example below shows how it can be accomplished.

```
$ ./gradlew wrapper
$ ./gradlw clean build shadowJar
```

The first line (i.e. ./gradlew wrapper) has to be executed only for the first time the project is built.

Once built, the project can be executed in the following way:

```
java -jar build/libs/health-checks-1.0.0-SNAPSHOT-all.jar -rundir [run-directory] -checktype boggs
```

# Logs location

After a succesful run, the logs can be found under the sub-modules logs directory.

Here is a sample of a run with the existing Health Checks:

```
sbpltk1zffh04:health-checks wrodrigues$ cat boggs/logs/healthchecks-trace.log 

[INFO ] 2016-06-03 10:23:27.123 [Test worker] MappingHealthChecker - Checking mapping health for DUMMY
[INFO ] 2016-06-03 10:23:27.125 [Test worker] MappingHealthChecker -  Verifying AnyPath
[INFO ] 2016-06-03 10:23:27.125 [Test worker] MappingHealthChecker -   WARN: Low mapped percentage: 38.46%
[INFO ] 2016-06-03 10:23:27.126 [Test worker] MappingHealthChecker -   WARN: Low properly paired percentage: 0.0%
[INFO ] 2016-06-03 10:23:27.126 [Test worker] MappingHealthChecker -   OK: Acceptable singleton percentage: 0.0%
[INFO ] 2016-06-03 10:23:27.126 [Test worker] MappingHealthChecker -   OK: Acceptable mate mapped to different chr percentage: 0.0%
[INFO ] 2016-06-03 10:23:27.126 [Test worker] MappingHealthChecker - Checking mapping health for DUMMY
[INFO ] 2016-06-03 10:23:27.126 [Test worker] MappingHealthChecker -  Verifying AnyPath
[INFO ] 2016-06-03 10:23:27.127 [Test worker] MappingHealthChecker -   WARN: Low mapped percentage: 38.46%
[INFO ] 2016-06-03 10:23:27.127 [Test worker] MappingHealthChecker -   WARN: Low properly paired percentage: 0.0%
[INFO ] 2016-06-03 10:23:27.127 [Test worker] MappingHealthChecker -   OK: Acceptable singleton percentage: 0.0%
[INFO ] 2016-06-03 10:23:27.127 [Test worker] MappingHealthChecker -   OK: Acceptable mate mapped to different chr percentage: 0.0%
```

# Reports

The current version of the Health Checks generates a JSON file with information about the executed checks. The reports are genrated under ```/tmp``` directory with the prefixed name ```health-checks_[timestamp]```.

The location of the reports can be changed via the ```config.properties``` file located under the **resources** directory of the **common** module.

# Report Snippet

```
{
	"health_checks": [{
		"PRESTATS": {
			"summary": [{
				"patient_id": "CPCT12345678R",
				"check_name": "PRESTATS_PER_BASE_SEQUENCE_CONTENT",
				"status": "PASS"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "PRESTATS_PER_SEQUENCE_QUALITY_SCORES",
				"status": "PASS"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "PRESTATS_SEQUENCE_LENGTH_DISTRIBUTION",
				"status": "FAIL"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "PRESTATS_PER_SEQUENCE_GC_CONTENT",
				"status": "PASS"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "PRESTATS_PER_BASE_SEQUENCE_QUALITY",
				"status": "PASS"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "PRESTATS_KMER_CONTENT",
				"status": "WARN"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "PRESTATS_ADAPTER_CONTENT",
				"status": "PASS"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "PRESTATS_SEQUENCE_DUPLICATION_LEVELS",
				"status": "PASS"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "PRESTATS_BASIC_STATISTICS",
				"status": "PASS"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "PRESTATS_PER_BASE_N_CONTENT",
				"status": "PASS"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "PRESTATS_OVERREPRESENTED_SEQUENCES",
				"status": "WARN"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "PRESTATS_PER_TILE_SEQUENCE_QUALITY",
				"status": "WARN"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "PRESTATS_NUMBER_OF_READS",
				"status": "FAIL"
			}],
			"check_type": "PRESTATS"
		}
	}, {
		"MAPPING": {
			"external_id": "CPCT12345678R",
			"total_sequences": "36809",
			"mapping_data_report": {
				"mapped_percentage": 99.69,
				"properly_paired_percentage": 99.57,
				"singleton_percentage": 55.0,
				"mate_mapped_to_different_chr_percentage": 0.0,
				"proportion_of_duplicate_read": 5.95,
				"is_all_reads_present": false
			},
			"check_type": "MAPPING"
		}
	}]
}
```
