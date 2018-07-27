package com.ml.exam.configuration;

import com.ml.exam.job.JobCompletionListener;
import com.ml.exam.job.WeatherForecastProcesor;
import com.ml.exam.model.Planet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class WeatherJobConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    public FlatFileItemReader<Planet> planetFileReader() {
        return new FlatFileItemReaderBuilder<Planet>()
                .name("planetReader")
                .resource(new ClassPathResource("data.csv"))
                .delimited()
                .names(new String[]{"name", "distance", "speed", "clockwise"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Planet>() {{
                    setTargetType(Planet.class);
                }})
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Planet> planetWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Planet>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO planet (name, distance, speed, clockwise) VALUES (:name, :distance, :speed, :clockwise)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Job calculateWeatherJob(JobCompletionListener jobCompletionListener,
                                   Step step1,
                                   Step step2) {
        return jobBuilderFactory.get("calculateWeatherJob")
                .listener(jobCompletionListener)
                .flow(step1)
                .next(step2)
                .end()
                .build();
    }

    @Bean
    public Step step1(JdbcBatchItemWriter<Planet> planetWriter) {
        return stepBuilderFactory.get("step1")
                .<Planet, Planet> chunk(10)
                .reader(planetFileReader())
                .writer(planetWriter)
                .build();
    }

    @Bean
    public Step step2(WeatherForecastProcesor weatherForecastProcesor){
        return stepBuilderFactory.get("step2")
                .tasklet(weatherForecastProcesor)
                .build();
    }
}
