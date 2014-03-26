package webservicesapi.command.impl;

import org.apache.commons.configuration.AbstractFileConfiguration;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import webservicesapi.command.Command;
import webservicesapi.command.CommandSet;
import webservicesapi.command.InvalidCommandException;
import webservicesapi.command.impl.weather.GoogleWeatherHandler;
import webservicesapi.command.impl.weather.WeatherForecastCondition;
import webservicesapi.command.impl.weather.WeatherSet;
import webservicesapi.output.Output;
import webservicesapi.output.OutputQueue;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Ben Leov
 */
public class WeatherCommandSet implements CommandSet {

    // http://www.anddev.org/android_weather_forecast_-_google_weather_api_-_full_source-t361.html
    public static final String WEATHER_XML_URL = "http://www.google.com/ig/api?weather=";

    private AbstractFileConfiguration store;

    public WeatherCommandSet(AbstractFileConfiguration store) {
        this.store = store;
    }

    @Override
    public Set<Command> getCommands() {
        HashSet<Command> commands = new HashSet<Command>();
        commands.add(new CommandBase() {

            @Override
            public String[] getOptionalProperties() {
                return new String[]{"weather.location"};
            }

            @Override
            public String getCommandName() {
                return "weather";
            }

            @Override
            public void processCommand(String command, String parameter, OutputQueue queue) throws InvalidCommandException {

                String location = parameter;

                if (location == null || location.trim().equals("")) {
                    location = store.getString("weather.location");
                }

                if (location == null) {
                    throw new InvalidCommandException("A location must be specified");
                }

                Output output = new Output(this);

                try {
                    SAXParserFactory factory = SAXParserFactory.newInstance();
                    SAXParser parser = factory.newSAXParser();
                    XMLReader reader = parser.getXMLReader();

                    String weatherString = WEATHER_XML_URL + location;
                    URL url = new URL(weatherString.replace(" ", "%20"));
                    GoogleWeatherHandler handler = new GoogleWeatherHandler();

                    URLConnection uc = url.openConnection();
                    HttpURLConnection connection = (HttpURLConnection) uc;
                    InputStream in = connection.getInputStream();

                    reader.setContentHandler(handler);
                    reader.parse(new InputSource(in));

                    if (!handler.isCompleted()) {
                        // wait for doc to be completed
                        final Object wait = handler.getEndDocumentLock();

                        synchronized (wait) {
                            wait.wait();
                        }
                    }

                    // wait for parse to be complete

                    WeatherSet set = handler.getWeatherSet();

                    output.addLine("Current Condition");
                    output.addLine("");

                    if (set.getWeatherCurrentCondition() != null) {
                        output.addLine("---- Today: ----");
                        output.addLine("");
                        
                        output.addLine("Condition: " + set.getWeatherCurrentCondition().getCondition());
                        output.addLine("Humidity: " + set.getWeatherCurrentCondition().getHumidity());
                        output.addLine("Temp: " + set.getWeatherCurrentCondition().getTempCelcius());
                        output.addLine("Wind: " + set.getWeatherCurrentCondition().getWindCondition());
                    } else {
                        output.addLine("Current condition not found.");
                    }

                    output.addLine("");
                    output.addLine("--- Forecast --- ");
                    output.addLine("");

                    for (WeatherForecastCondition curr : set.getWeatherForecastConditions()) {
                        output.addLine("---- Day: " + curr.getDayofWeek() + " ----");
                        output.addLine("Condition: " + curr.getCondition());
                        output.addLine("Max: " + curr.getTempMaxCelsius());
                        output.addLine("Min: " + curr.getTempMinCelsius());
                    }

                } catch (ParserConfigurationException e) {
                    throw new InvalidCommandException(e);
                } catch (SAXException e) {
                    throw new InvalidCommandException(e);
                } catch (MalformedURLException e) {
                    throw new InvalidCommandException(e);
                } catch (IOException e) {
                    throw new InvalidCommandException(e);
                } catch (InterruptedException e) {
                    throw new InvalidCommandException(e);
                } finally {
                    queue.send(output);
                }


            }

            @Override
            public String getUsage() {
                return "<location>";
            }

            @Override
            public String getHelp() {
                return "Displays weather information";
            }
        });
        return commands;
    }
}
