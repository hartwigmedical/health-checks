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
		"REALIGNER": {
			"reference_sample": [{
				"patient_id": "CPCT12345678R",
				"check_name": "MAPPING_REALIGNER_CHANGED_ALIGNMENTS",
				"value": "0.00101"
			}],
			"tumor_sample": [{
				"patient_id": "CPCT12345678T",
				"check_name": "MAPPING_REALIGNER_CHANGED_ALIGNMENTS",
				"value": "0.00101"
			}],
			"check_type": "REALIGNER"
		}
	}, {
		"INSERT_SIZE": {
			"reference_sample": [{
				"patient_id": "CPCT12345678R",
				"check_name": "MAPPING_MEDIAN_INSERT_SIZE",
				"value": "409"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "MAPPING_WIDTH_OF_70_PERCENT",
				"value": "247"
			}],
			"tumor_sample": [{
				"patient_id": "CPCT12345678T",
				"check_name": "MAPPING_MEDIAN_INSERT_SIZE",
				"value": "209"
			}, {
				"patient_id": "CPCT12345678T",
				"check_name": "MAPPING_WIDTH_OF_70_PERCENT",
				"value": "147"
			}],
			"check_type": "INSERT_SIZE"
		}
	}, {
		"COVERAGE": {
			"reference_sample": [{
				"patient_id": "CPCT12345678R",
				"check_name": "COVERAGE_MEAN",
				"value": "0.000856"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "COVERAGE_MEDIAN",
				"value": "0"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "COVERAGE_PCT_EXC_BASEQ",
				"value": "0.002378"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "COVERAGE_PCT_EXC_DUPE",
				"value": "0.059484"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "COVERAGE_PCT_EXC_MAPQ",
				"value": "0.000585"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "COVERAGE_PCT_EXC_OVERLAP",
				"value": "0.020675"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "COVERAGE_PCT_EXC_TOTAL",
				"value": "0.086479"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "COVERAGE_PCT_EXC_UNPAIRED",
				"value": "0.002331"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "COVERAGE_SD",
				"value": "0.157469"
			}],
			"tumor_sample": [{
				"patient_id": "CPCT12345678T",
				"check_name": "COVERAGE_MEAN",
				"value": "0.000856"
			}, {
				"patient_id": "CPCT12345678T",
				"check_name": "COVERAGE_MEDIAN",
				"value": "0"
			}, {
				"patient_id": "CPCT12345678T",
				"check_name": "COVERAGE_PCT_EXC_BASEQ",
				"value": "0.002378"
			}, {
				"patient_id": "CPCT12345678T",
				"check_name": "COVERAGE_PCT_EXC_DUPE",
				"value": "0.059484"
			}, {
				"patient_id": "CPCT12345678T",
				"check_name": "COVERAGE_PCT_EXC_MAPQ",
				"value": "0.000585"
			}, {
				"patient_id": "CPCT12345678T",
				"check_name": "COVERAGE_PCT_EXC_OVERLAP",
				"value": "0.020675"
			}, {
				"patient_id": "CPCT12345678T",
				"check_name": "COVERAGE_PCT_EXC_TOTAL",
				"value": "0.086479"
			}, {
				"patient_id": "CPCT12345678T",
				"check_name": "COVERAGE_PCT_EXC_UNPAIRED",
				"value": "0.002331"
			}, {
				"patient_id": "CPCT12345678T",
				"check_name": "COVERAGE_SD",
				"value": "0.157469"
			}],
			"check_type": "COVERAGE"
		}
	}, {
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
		"KINSHIP": {
			"patient_data": {
				"patient_id": "CPCT12345678R",
				"check_name": "KINSHIP_TEST",
				"value": "0.2155"
			},
			"check_type": "KINSHIP"
		}
	}, {
		"SLICED": {
			"error": "java.io.FileNotFoundException",
			"message": "File _Cosmicv76_GoNLv5_sliced.vcf not Found in path /Users/nhazaa/mydata/git_tree/work/health-checks/boggs/src/test/resources/rundir",
			"check_type": "SLICED"
		}
	}, {
		"VARIANTS": {
			"patient_data": [{
				"patient_id": "CPCT12345678R",
				"check_name": "VARIANTS_GERMLINE_SNP",
				"value": "74"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "VARIANTS_GERMLINE_INDELS",
				"value": "4"
			}],
			"check_type": "VARIANTS"
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
	}, {
		"SUMMARY_METRICS": {
			"reference_sample": [{
				"patient_id": "CPCT12345678R",
				"check_name": "MAPPING_PF_INDEL_RATE",
				"value": "0.000261"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "MAPPING_PCT_ADAPTER",
				"value": "0.000046"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "MAPPING_PCT_CHIMERA",
				"value": "0.000212"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "MAPPING_PF_MISMATCH_RATE",
				"value": "0.006024"
			}, {
				"patient_id": "CPCT12345678R",
				"check_name": "MAPPING_STRAND_BALANCE",
				"value": "0.399972"
			}],
			"tumor_sample": [{
				"patient_id": "CPCT12345678T",
				"check_name": "MAPPING_PF_INDEL_RATE",
				"value": "0.000161"
			}, {
				"patient_id": "CPCT12345678T",
				"check_name": "MAPPING_PCT_ADAPTER",
				"value": "0.000056"
			}, {
				"patient_id": "CPCT12345678T",
				"check_name": "MAPPING_PCT_CHIMERA",
				"value": "0.000112"
			}, {
				"patient_id": "CPCT12345678T",
				"check_name": "MAPPING_PF_MISMATCH_RATE",
				"value": "0.005024"
			}, {
				"patient_id": "CPCT12345678T",
				"check_name": "MAPPING_STRAND_BALANCE",
				"value": "0.499972"
			}],
			"check_type": "SUMMARY_METRICS"
		}
	}]
}
```
