package com.ml.exam.job;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ml.exam.model.Planet;
import com.ml.exam.model.Weather;
import com.ml.exam.model.WeatherCondition;
import com.ml.exam.repository.PlanetRepository;
import com.ml.exam.repository.WeatherRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class WeatherForecastProcesorTest {

    private WeatherForecastProcesor weatherForecastProcesor;
    private Set<Weather> jobResult = Sets.newHashSet();

    @Before
    public void setup() {
        PlanetRepository planetRepository = Mockito.mock(PlanetRepository.class);
        WeatherRepository weatherRepository = Mockito.mock(WeatherRepository.class);

        Mockito.when(planetRepository.findAll()).thenReturn(Lists.newArrayList(
                new Planet("Ferengi",1d,500d,true),
                new Planet("Betasoide",3d,2000d,true),
                new Planet("Vulcano",5d,1000d,false)
        ));

        Mockito.when(weatherRepository.save(Mockito.any())).then(new Answer<Weather>() {
            @Override
            public Weather answer(InvocationOnMock invocationOnMock) throws Throwable {
                Weather weather = invocationOnMock.getArgument(0);

                // workaround para que no guarde el dia duplicado en el test
                if(weather.getCondition().equals(WeatherCondition.MAX_RAIN)) {
                    jobResult.removeIf(w -> w.getDay().equals(weather.getDay()));
                }

                jobResult.add(weather);
                return weather;
            }
        });

        weatherForecastProcesor = new WeatherForecastProcesor(planetRepository, weatherRepository);
    }

    @Test
    public void testJob() {

        RepeatStatus result = weatherForecastProcesor.execute(Mockito.mock(StepContribution.class),
                Mockito.mock(ChunkContext.class));

        Assert.assertEquals(RepeatStatus.FINISHED, result);

        Map<WeatherCondition, Long> resultGrouped = jobResult.stream()
                .collect(Collectors.groupingBy(Weather::getCondition, Collectors.counting()));

        Assert.assertEquals(new Long(1420), resultGrouped.get(WeatherCondition.REGULAR));
        Assert.assertEquals(new Long(405), resultGrouped.get(WeatherCondition.DROUGHT));
        Assert.assertEquals(new Long(1), resultGrouped.get(WeatherCondition.MAX_RAIN));
        Assert.assertEquals(new Long(1824), resultGrouped.get(WeatherCondition.RAIN));

    }
}

