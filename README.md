// @Valid @RequestBody failures (e.g. CronUpdateRequest.jobName / .cron blank)
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException ex) {
    String message = ex.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining("; "));
    log.warn("Request validation failed: {}", message);
    ApiResponse<Object> apiResponse = new ApiResponse<>(SyncErrorCode.MISSING_FIELDS, message, HttpStatus.BAD_REQUEST.value());
    return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
}

// @Validated @RequestParam / @PathVariable failures (e.g. disableJob's jobName)
@ExceptionHandler(ConstraintViolationException.class)
public ResponseEntity<ApiResponse<Object>> handleConstraintViolation(ConstraintViolationException ex) {
    String message = ex.getConstraintViolations().stream()
            .map(v -> v.getMessage())
            .collect(Collectors.joining("; "));
    log.warn("Request validation failed: {}", message);
    ApiResponse<Object> apiResponse = new ApiResponse<>(SyncErrorCode.MISSING_FIELDS, message, HttpStatus.BAD_REQUEST.value());
    return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
}




@PostMapping("/disableJob")
public ResponseEntity<ApiResponse<Void>> disableJob(
        @RequestParam @NotBlank(message = "jobName is required") String jobName) {

    if (!StringUtils.hasText(jobName)) {
        throw new DataBridgeException(SyncErrorCode.MISSING_FIELDS, "jobName");
    }

    log.info("Disabling job '{}'", jobName);

    jobUpdateService.disableJob(jobName);

    ApiResponse<Void> response = ApiResponse.<Void>builder()
            .statusCode(200)
            .message("JOB_DISABLED")
            .displayMessage(jobName + " disabled successfully")
            .traceId(MDC.get(ApiResponse.TRACE_ID))
            .build();

    return ResponseEntity.ok(response);
}





public void disableJob(String jobName) {

    JobConfigEntity jobConfig = jobConfigRepository.findByName(jobName)
            .orElseThrow(() ->
                    new DataBridgeException(SyncErrorCode.INVALID_DATA, jobName)
            );

    if (!jobConfig.isActive()) {
        log.info("Job '{}' is already disabled, skipping", jobName);
        return;
    }

    // 1. Remove from JobRunr first.
    try {
        schedulerUtil.delete(jobConfig.getName());
    } catch (Exception e) {
        log.error("Failed to delete JobRunr recurring job for '{}'", jobName, e);
        throw new DataBridgeException(SyncErrorCode.FAILED_TO_DELETE_JOBS, e, jobName);
    }

    // 2. Only mark inactive in the DB once JobRunr confirms it's gone.
    try {
        jobConfig.setActive(false);
        jobConfigRepository.save(jobConfig);
    } catch (Exception e) {
        log.error("JobRunr job '{}' was deleted, but DB update to inactive failed -- " +
                        "job is stopped but DB still shows it active, needs reconciliation",
                jobName, e);
        throw new DataBridgeException(SyncErrorCode.FAILED_TO_DELETE_JOBS, e, jobName);
    }

    log.info("Disabled job '{}'", jobName);
}




public void updateJobCron(String jobName, String newCron) {

    if (!CronExpression.isValidExpression(newCron)) {
        throw new DataBridgeException(
                SyncErrorCode.INVALID_CRON_EXPRESSION,
                newCron
        );
    }

    JobConfigEntity jobConfig = jobConfigRepository.findByName(jobName)
            .orElseThrow(() ->
                    new DataBridgeException(SyncErrorCode.INVALID_DATA, jobName)
            );

    if (jobConfig.getTrigger() == null) {
        throw new DataBridgeException(
                SyncErrorCode.MISSING_FIELDS,
                "trigger config missing for job " + jobName
        );
    }

    if (!jobConfig.isActive()) {
        throw new DataBridgeException(
                SyncErrorCode.CRON_UPDATE_NOT_ALLOWED,
                "job '" + jobName + "' is disabled; enable it before updating its cron"
        );
    }

    String previousCron = jobConfig.getTrigger().getCron();

    if (newCron.equals(previousCron)) {
        log.info("Cron for job '{}' is already '{}', skipping update", jobName, newCron);
        return;
    }

    // 1. Update JobRunr first.
    SchedulerJobDefinition job = jobRegistry.get(jobName);
    try {
        schedulerUtil.registerOrUpdateJob(
                jobConfig.getName(),
                newCron,
                job.getJobLogic()
        );
    } catch (Exception e) {
        log.error("Failed to update JobRunr cron for job '{}' from '{}' to '{}'",
                jobName, previousCron, newCron, e);
        throw new DataBridgeException(SyncErrorCode.UPDATE_CRON_OR_JOB_FAILED, e, jobName);
    }

    // 2. Only persist to the DB once JobRunr has accepted the new schedule.
    try {
        jobConfig.getTrigger().setCron(newCron);
        jobConfigRepository.save(jobConfig);
    } catch (Exception e) {
        log.error("JobRunr cron updated to '{}' for '{}', but DB persist failed -- " +
                        "DB and JobRunr are now out of sync and need reconciliation",
                newCron, jobName, e);
        throw new DataBridgeException(SyncErrorCode.UPDATE_CRON_OR_JOB_FAILED, e, jobName);
    }

    log.info("Updated cron for job '{}' from '{}' to '{}'", jobName, previousCron, newCron);
}
