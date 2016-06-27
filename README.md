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

[INFO ] 2016-06-27 14:53:16.381 [main] MappingExtractor - For Patient CPCT12345678R the Result for mapping health check  'mapped' is '99.69'
[INFO ] 2016-06-27 14:53:16.384 [main] MappingExtractor - For Patient CPCT12345678R the Result for mapping health check  'properly paired' is '99.57'
[INFO ] 2016-06-27 14:53:16.386 [main] MappingExtractor - For Patient CPCT12345678R the Result for mapping health check  'singletons' is '55.0'
[INFO ] 2016-06-27 14:53:16.386 [main] MappingExtractor - For Patient CPCT12345678R the Result for mapping health check  'with mate mapped to a different chr' is '0.0'
[INFO ] 2016-06-27 14:53:16.386 [main] MappingExtractor - For Patient CPCT12345678R the Result for mapping health check  'duplicates' is '5.95'
[INFO ] 2016-06-27 14:53:16.387 [main] MappingExtractor - For Patient CPCT12345678R the Result for mapping health check  'is all read' is 'false'
[INFO ] 2016-06-27 14:53:16.424 [main] MappingExtractor - For Patient CPCT12345678T the Result for mapping health check  'mapped' is '99.69'
[INFO ] 2016-06-27 14:53:16.424 [main] MappingExtractor - For Patient CPCT12345678T the Result for mapping health check  'properly paired' is '99.57'
[INFO ] 2016-06-27 14:53:16.425 [main] MappingExtractor - For Patient CPCT12345678T the Result for mapping health check  'singletons' is '55.0'
[INFO ] 2016-06-27 14:53:16.425 [main] MappingExtractor - For Patient CPCT12345678T the Result for mapping health check  'with mate mapped to a different chr' is '0.0'
[INFO ] 2016-06-27 14:53:16.426 [main] MappingExtractor - For Patient CPCT12345678T the Result for mapping health check  'duplicates' is '5.95'
[INFO ] 2016-06-27 14:53:16.427 [main] MappingExtractor - For Patient CPCT12345678T the Result for mapping health check  'is all read' is 'false'
[INFO ] 2016-06-27 14:53:16.464 [main] PrestatsExtractor - NOT OK: Sequence Length Distribution has status FAIL for Patient CPCT12345678R
[INFO ] 2016-06-27 14:53:16.464 [main] PrestatsExtractor - NOT OK: Total Sequences has status FAIL for Patient CPCT12345678R
[INFO ] 2016-06-27 14:53:16.484 [main] PrestatsExtractor - NOT OK: Overrepresented sequences has status FAIL for Patient CPCT12345678T
[INFO ] 2016-06-27 14:53:16.484 [main] PrestatsExtractor - NOT OK: Sequence Duplication Levels has status FAIL for Patient CPCT12345678T
[INFO ] 2016-06-27 14:53:16.485 [main] PrestatsExtractor - NOT OK: Adapter Content has status FAIL for Patient CPCT12345678T
[INFO ] 2016-06-27 14:53:16.485 [main] PrestatsExtractor - NOT OK: Sequence Length Distribution has status FAIL for Patient CPCT12345678T
[INFO ] 2016-06-27 14:53:16.485 [main] PrestatsExtractor - NOT OK: Kmer Content has status FAIL for Patient CPCT12345678T
[INFO ] 2016-06-27 14:53:16.485 [main] PrestatsExtractor - NOT OK: Per base N content has status FAIL for Patient CPCT12345678T
[INFO ] 2016-06-27 14:53:16.485 [main] PrestatsExtractor - NOT OK: Total Sequences has status FAIL for Patient CPCT12345678T
[INFO ] 2016-06-27 14:53:16.511 [main] HealthChecksApplication - Report generated with following name -> /tmp/health-checks_1467031996501.json

```

# Reports

The current version of the Health Checks generates a JSON file with information about the executed checks. The reports are genrated under ```/tmp``` directory with the prefixed name ```health-checks_[timestamp]```.

The location of the reports can be changed via the ```config.properties``` file located under the **resources** directory of the **common** module.

# Report Snippet

```
{
	"health_checks": [{
		"MAPPING": {
			"reference_sample": [{
				"patient_id": "CPCT12345678R",
				"check_name": "mapped",
				"value": "99.69"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "properly paired",
				"value": "99.57"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "singletons",
				"value": "55.0"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "with mate mapped to a different chr",
				"value": "0.0"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "duplicates",
				"value": "5.95"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "is all read",
				"value": "false"
			}],
			"tumor_sample": [{
				"patient_id": "CPCT12345678T",
				"check_name": "mapped",
				"value": "99.69"
			}, {
				"patient_id": "CPCT12345678T",
				"check_name": "properly paired",
				"value": "99.57"
			}, {
				"patient_id": "CPCT12345678T",
				"check_name": "singletons",
				"value": "55.0"
			}, {
				"patient_id": "CPCT12345678T",
				"check_name": "with mate mapped to a different chr",
				"value": "0.0"
			}, {
				"patient_id": "CPCT12345678T",
				"check_name": "duplicates",
				"value": "5.95"
			}, {
				"patient_id": "CPCT12345678T",
				"check_name": "is all read",
				"value": "false"
			}],
			"check_type": "MAPPING"
		}
	}, {
		"PRESTATS": {
			"reference_sample": [{
				"patient_id": "CPCT12345678R",
				"check_name": "Per sequence quality scores",
				"value": "PASS"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "Basic Statistics",
				"value": "PASS"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "Overrepresented sequences",
				"value": "WARN"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "Per base sequence quality",
				"value": "PASS"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "Per sequence GC content",
				"value": "PASS"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "Per base sequence content",
				"value": "PASS"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "Sequence Duplication Levels",
				"value": "PASS"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "Adapter Content",
				"value": "PASS"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "Sequence Length Distribution",
				"value": "FAIL"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "Per tile sequence quality",
				"value": "WARN"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "Kmer Content",
				"value": "WARN"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "Per base N content",
				"value": "PASS"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "Total Sequences",
				"value": "FAIL"
			}],
			"tumor_sample": [{
				"patient_id": "CPCT12345678T",
				"check_name": "Per sequence quality scores",
				"value": "PASS"
			}, {
				"patient_id": "CPCT12345678T",
				"check_name": "Basic Statistics",
				"value": "PASS"
			}, {
				"patient_id": "CPCT12345678T",
				"check_name": "Overrepresented sequences",
				"value": "FAIL"
			}, {
				"patient_id": "CPCT12345678T",
				"check_name": "Per base sequence quality",
				"value": "PASS"
			}, {
				"patient_id": "CPCT12345678T",
				"check_name": "Per sequence GC content",
				"value": "PASS"
			}, {
				"patient_id": "CPCT12345678T",
				"check_name": "Per base sequence content",
				"value": "PASS"
			}, {
				"patient_id": "CPCT12345678T",
				"check_name": "Sequence Duplication Levels",
				"value": "FAIL"
			}, {
				"patient_id": "CPCT12345678T",
				"check_name": "Adapter Content",
				"value": "FAIL"
			}, {
				"patient_id": "CPCT12345678T",
				"check_name": "Sequence Length Distribution",
				"value": "FAIL"
			}, {
				"patient_id": "CPCT12345678T",
				"check_name": "Per tile sequence quality",
				"value": "WARN"
			}, {
				"patient_id": "CPCT12345678T",
				"check_name": "Kmer Content",
				"value": "FAIL"
			}, {
				"patient_id": "CPCT12345678T",
				"check_name": "Per base N content",
				"value": "FAIL"
			}, {
				"patient_id": "CPCT12345678T",
				"check_name": "Total Sequences",
				"value": "FAIL"
			}],
			"check_type": "PRESTATS"
		}
	}]
}
```
# In Case of Error Report Snippet

```
{
	"health_checks": [{
		"MAPPING": {
			"reference_sample": [{
				"patient_id": "CPCT12345678R",
				"check_name": "mapped",
				"value": "99.69"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "properly paired",
				"value": "99.57"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "singletons",
				"value": "55.0"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "with mate mapped to a different chr",
				"value": "0.0"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "duplicates",
				"value": "5.95"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "is all read",
				"value": "false"
			}],
			"tumor_sample": [{
				"patient_id": "CPCT12345678T",
				"check_name": "mapped",
				"value": "99.69"
			}, {
				"patient_id": "CPCT12345678T",
				"check_name": "properly paired",
				"value": "99.57"
			}, {
				"patient_id": "CPCT12345678T",
				"check_name": "singletons",
				"value": "55.0"
			}, {
				"patient_id": "CPCT12345678T",
				"check_name": "with mate mapped to a different chr",
				"value": "0.0"
			}, {
				"patient_id": "CPCT12345678T",
				"check_name": "duplicates",
				"value": "5.95"
			}, {
				"patient_id": "CPCT12345678T",
				"check_name": "is all read",
				"value": "false"
			}],
			"check_type": "MAPPING"
		}
	}, {
		"PRESTATS": {
			"error": "com.hartwig.healthchecks.common.exception.EmptyFileException",
			"message": "Found empty Summary files under path -> /health-checks/boggs/src/test/resources/emptyFiles/CPCT12345678R",
			"check_type": "PRESTATS"
		}
	}]
}
```