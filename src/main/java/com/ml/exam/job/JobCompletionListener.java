package com.ml.exam.job;

import com.ml.exam.model.Weather;
import com.ml.exam.repository.WeatherRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class JobCompletionListener extends JobExecutionListenerSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobCompletionListener.class);

    @Autowired
    private WeatherRepository weatherRepository;

    @Override
    public void afterJob(JobExecution jobExecution) {
        List<Weather> result = (List<Weather>) weatherRepository.findAll();
        LOGGER.info("Weather forecast complete. {} days loaded", result.size());

        if(LOGGER.isDebugEnabled()) {
            result.stream()
                    .collect(Collectors.groupingBy(Weather::getCondition, Collectors.counting()))
                    .forEach((q,w) -> LOGGER.debug("Found {} {}", w, q));
        }
    }
}
