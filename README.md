# Health Checks

[![wercker status](https://app.wercker.com/status/a51d71fbe59d634461b37ab989d6f594/m/master "wercker status")](https://app.wercker.com/project/bykey/a51d71fbe59d634461b37ab989d6f594)

[![Coverage Status](https://coveralls.io/repos/github/hartwigmedical/health-checks/badge.svg?branch=master)](https://coveralls.io/github/hartwigmedical/health-checks?branch=master)

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
		"MAPPING": {
			"external_id": "CPCT00R",
			"total_sequences": "8960",
			"mapping_data_report": {
				"mapped_percentage": 99.69,
				"properly_paired_percentage": 99.57,
				"singleton_percentage": 55.0,
				"mate_mapped_to_different_chr_percentage": 0.0,
				"proportion_of_duplicate_read": 5.95,
				"is_all_reads_present": true
			},
			"check_type": "MAPPING"
		}
	}, {
		"PRESTATS": {
			"summary": [{
				"check_name": "Basic Statistics",
				"status": "PASS",
				"file": "CPCT12345678T_HJJLGCCXX_S1_L001_R1_001.fastq.gz"
			}, {
				"check_name": "Per base sequence quality",
				"status": "PASS",
				"file": "CPCT12345678T_HJJLGCCXX_S1_L001_R1_001.fastq.gz"
			}, {
				"check_name": "Per tile sequence quality",
				"status": "FAIL",
				"file": "CPCT12345678T_HJJLGCCXX_S1_L001_R1_001.fastq.gz"
			}, {
				"check_name": "Per sequence quality scores",
				"status": "PASS",
				"file": "CPCT12345678T_HJJLGCCXX_S1_L001_R1_001.fastq.gz"
			}, {
				"check_name": "Per base sequence content",
				"status": "PASS",
				"file": "CPCT12345678T_HJJLGCCXX_S1_L001_R1_001.fastq.gz"
			}, {
				"check_name": "Per sequence GC content",
				"status": "FAIL",
				"file": "CPCT12345678T_HJJLGCCXX_S1_L001_R1_001.fastq.gz"
			}, {
				"check_name": "Per base N content",
				"status": "PASS",
				"file": "CPCT12345678T_HJJLGCCXX_S1_L001_R1_001.fastq.gz"
			}, {
				"check_name": "Sequence Length Distribution",
				"status": "PASS",
				"file": "CPCT12345678T_HJJLGCCXX_S1_L001_R1_001.fastq.gz"
			}, {
				"check_name": "Sequence Duplication Levels",
				"status": "PASS",
				"file": "CPCT12345678T_HJJLGCCXX_S1_L001_R1_001.fastq.gz"
			}, {
				"check_name": "Overrepresented sequences",
				"status": "PASS",
				"file": "CPCT12345678T_HJJLGCCXX_S1_L001_R1_001.fastq.gz"
			}, {
				"check_name": "Adapter Content",
				"status": "PASS",
				"file": "CPCT12345678T_HJJLGCCXX_S1_L001_R1_001.fastq.gz"
			}, {
				"check_name": "Kmer Content",
				"status": "WARN",
				"file": "CPCT12345678T_HJJLGCCXX_S1_L001_R1_001.fastq.gz"
			}, {
				"check_name": "Basic Statistics",
				"status": "PASS",
				"file": "CPCT12345678T_HJJLGCCXX_S1_L001_R2_001.fastq.gz"
			}, {
				"check_name": "Per base sequence quality",
				"status": "PASS",
				"file": "CPCT12345678T_HJJLGCCXX_S1_L001_R2_001.fastq.gz"
			}, {
				"check_name": "Per tile sequence quality",
				"status": "WARN",
				"file": "CPCT12345678T_HJJLGCCXX_S1_L001_R2_001.fastq.gz"
			}, {
				"check_name": "Per sequence quality scores",
				"status": "PASS",
				"file": "CPCT12345678T_HJJLGCCXX_S1_L001_R2_001.fastq.gz"
			}, {
				"check_name": "Per base sequence content",
				"status": "WARN",
				"file": "CPCT12345678T_HJJLGCCXX_S1_L001_R2_001.fastq.gz"
			}, {
				"check_name": "Per sequence GC content",
				"status": "FAIL",
				"file": "CPCT12345678T_HJJLGCCXX_S1_L001_R2_001.fastq.gz"
			}, {
				"check_name": "Per base N content",
				"status": "PASS",
				"file": "CPCT12345678T_HJJLGCCXX_S1_L001_R2_001.fastq.gz"
			}, {
				"check_name": "Sequence Length Distribution",
				"status": "PASS",
				"file": "CPCT12345678T_HJJLGCCXX_S1_L001_R2_001.fastq.gz"
			}, {
				"check_name": "Sequence Duplication Levels",
				"status": "PASS",
				"file": "CPCT12345678T_HJJLGCCXX_S1_L001_R2_001.fastq.gz"
			}, {
				"check_name": "Overrepresented sequences",
				"status": "PASS",
				"file": "CPCT12345678T_HJJLGCCXX_S1_L001_R2_001.fastq.gz"
			}, {
				"check_name": "Adapter Content",
				"status": "PASS",
				"file": "CPCT12345678T_HJJLGCCXX_S1_L001_R2_001.fastq.gz"
			}, {
				"check_name": "Kmer Content",
				"status": "PASS",
				"file": "CPCT12345678T_HJJLGCCXX_S1_L001_R2_001.fastq.gz"
			}, {
				"check_name": "Total Sequences",
				"status": "FAIL",
				"file": "CPCT12345678T_HJJLGCCXX_S1_L001_R1_001.fastq.gz"
			}, {
				"check_name": "Total Sequences",
				"status": "FAIL",
				"file": "CPCT12345678T_HJJLGCCXX_S1_L001_R2_001.fastq.gz"
			}],
			"check_type": "PRESTATS"
		}
	}]
}
```
