# Health Checks

This repository consists of a set of utilities that perform health checks against genomics data.

# Running the Heath-Checks application

After cloning the repository, one can run the Health-Checks application in the following way:

```
./gradlew clean build run -Pargs="-rundir <directory> -checktype boggs"
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
