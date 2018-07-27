package com.ml.exam.job;

import com.ml.exam.model.Planet;
import com.ml.exam.model.Position;
import com.ml.exam.model.Weather;
import com.ml.exam.model.WeatherCondition;
import com.ml.exam.repository.PlanetRepository;
import com.ml.exam.repository.WeatherRepository;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class WeatherForecastProcesor implements Tasklet {

    private static final int MAX_DAYS = 3650;

    private PlanetRepository planetRepository;
    private WeatherRepository weatherRepository;

    @Autowired
    public WeatherForecastProcesor(PlanetRepository planetRepository,
                                   WeatherRepository weatherRepository) {
        this.planetRepository = planetRepository;
        this.weatherRepository = weatherRepository;
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {

        List<Planet> planets = (List<Planet>) planetRepository.findAll();
        Integer maxRainDay = null;
        Double maxPerimeter = 0d;

        for (int d = 1; d <= MAX_DAYS; d++) {

            List<Position> coordenates = getPositions(planets, d);
            WeatherCondition condition = calculateAlignment(coordenates);

            if (WeatherCondition.REGULAR.equals(condition)) {

                Double perimeter;

                // TODO Mejorar este bloque de codigo para que funcione con mas de 3 puntos
                // Si el sol esta entre el triangulo formado por los 3 puntos => lluvia
                if (coordenates.stream().mapToInt(p -> p.getX().intValue()).min().getAsInt() < 0
                        && coordenates.stream().mapToInt(p -> p.getX().intValue()).max().getAsInt() > 0
                        && coordenates.stream().mapToInt(p -> p.getY().intValue()).min().getAsInt() < 0
                        && coordenates.stream().mapToInt(p -> p.getY().intValue()).max().getAsInt() > 0) {

                    condition = WeatherCondition.RAIN;
                    // Para calcular el perimetro, primero debo calcular el largo de sus 3 lados.
                    // Para esto uso la formula de distancia (d) entre dos puntos. Luego sumo las 3 distancias obtenidas
                    Position A = coordenates.get(0);
                    Position B = coordenates.get(1);
                    Position C = coordenates.get(2);
                    perimeter = Math.sqrt(Math.pow(A.getX() - B.getX(), 2) + Math.pow(A.getY() - B.getY(), 2))
                            + Math.sqrt(Math.pow(A.getX() - C.getX(), 2) + Math.pow(A.getY() - C.getY(), 2))
                            + Math.sqrt(Math.pow(C.getX() - B.getX(), 2) + Math.pow(C.getY() - B.getY(), 2));

                    if (perimeter > maxPerimeter) {
                        maxRainDay = d;
                        maxPerimeter = perimeter;
                    }
                }
            }

            weatherRepository.save(new Weather(d, condition));
        }

        if (maxRainDay != null) { //El dia que se forma el triangulo de mayor perimetro => es el dia mas lluvioso
            weatherRepository.save(new Weather(maxRainDay, WeatherCondition.MAX_RAIN));
        }

        return RepeatStatus.FINISHED;
    }

    // Metodo utilizado para calcular si los planetas y el sol estan alineados
    // Si todos los puntos pertenecen a la misma recta y pasan por el origen (0,0) => sequia
    // Si todos los puntos pertenecen a la misma recta y NO pasan por el origen (0,0) => optimo
    private WeatherCondition calculateAlignment(List<Position> positions) {
        Double auxY1 = null; //representa y1
        Double auxX1 = null; //representa x1
        Double result = null; // representa (y2-y1)/(x2-x1)

        for (Position p : positions) {
            // Para las primeras dos coordenadas utiliza la formula de recta que pasa por dos puntos.
            // (y-y1)/(x-x1)=(y2-y1)/(x2-x1)
            // Para los restantes solo valido que pertenezcan a la recta
            if (result == null) {
                if (auxX1 == null) {
                    auxX1 = p.getX();
                    auxY1 = p.getY();
                } else {
                    result = (p.getY() - auxY1) / (p.getX() - auxX1);
                }
            } else if ((p.getY() - auxY1) / (p.getX() - auxX1) != result) { //Si el punto no pertenece a la recta
                return WeatherCondition.REGULAR;
            }
        }

        // Si llego hasta aca es porque las condiciones son optimas o porque hay sequia.
        // Valido si estan alineados con el sol, que esta en el origen (0,0)
        return auxY1 / auxX1 == result ? WeatherCondition.DROUGHT : WeatherCondition.OPTIMAL;
    }


    private List<Position> getPositions(List<Planet> planets, Integer day) {
        return planets.stream().map(p -> getPosition(p, day)).collect(Collectors.toList());
    }

    private Position getPosition(Planet planet, Integer day) {
        // Utilizo la siguiente funcion para ubicar la posicion del punto (planeta),
        // en un tiempo determinado (dia), segun su velocidad angular.
        //x(t) = x0 + r * cos(wt) & y(t) = y0 + r * sen(wt)
        // (x0, y0) = origen = sol = (0,0)

        //Velocidad angular en radianes.
        Double w = Math.toRadians(planet.getClockwise() ? planet.getSpeed() : - planet.getSpeed());

        return new Position(planet.getDistance() * (Math.cos(w * day)),
                planet.getDistance() * (Math.sin(w * day)));
    }
}
