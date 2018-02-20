/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package freqclass;

import frequencydisplay.controller.DisplayController;
import frequencydisplay.data.Platform;
import frequencydisplay.model.AppModel;
import frequencydisplay.model.CsvPlatformModelLoader;
import frequencydisplay.model.Model;
import freqclass.rest.RequestHandler;
import frequencydisplay.data.SearchParameters;
import frequencydisplay.model.ModelListener;
import frequencydisplay.view.View;
import frequencydisplay.view.ViewListener;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;



/**
 *
 * @author Keith
 */
public class FrequencyClassificationService {
    
    String databaseFile = "data/Sonar_Profiles_LWAMI_311.csv";

    /**
     * Default constructor for the service, leaving the database file as the default.
     */
    public FrequencyClassificationService() {
    }
    
    /**
     * Constructs an instance of this service.  Platform data for the database will be read
     * from the file provided in this constructor.
     * @param file The file to be read in as the initial platform database.
     */
    public FrequencyClassificationService(String file) {
        this.databaseFile = file;
    }
    
    public void run() {
        //Model
        Model model = new AppModel();
        model.addModelListener(new ModelListener() {
            @Override
            public void platformAdded(Platform p) {
                System.out.println("Added platform: " + p.getPlatformClass());
            }

            @Override
            public void platformRemoved(Platform p) {
            }

            @Override
            public void searchParametersAdded(SearchParameters searchParameters) {
            }

            @Override
            public void searchParametersRemoved(SearchParameters searchParameters) {
            }
        });
        
        //RESTful request handler
        RequestHandler handler = new RequestHandler(model);
        
        //View
        //AppView view = new AppView();
        View view = new NopView();
        view.addViewListener(new ViewListener() {
            @Override
            public void viewClosed() {
                System.out.println("View closing, stopping server");
                handler.stopServer();  //Should decouple from server in the future
                System.exit(0);
            }
        });
        view.initialize();

        //Controller
        DisplayController controller = new DisplayController(model, view);
        controller.launch();
        
        List<Platform> database = CsvPlatformModelLoader.loadPlatformsFromFile(databaseFile);
        for (Platform p : database) {
            model.addPlatform(p);
        }
    }    

    public static Options prepareCommandlineOptions(String[] args) {
        //Set up options
        Options options = new Options();

        Option input = new Option("f", "file", true, "The path to the database file");
        input.setRequired(false);
        options.addOption(input);

        Option output = new Option("nw", "no-window", true, "Run in headless mode with a NOP View");
        output.setRequired(false);
        output.setArgs(0);
        options.addOption(output);
        
        return options;
    }
    
    public static void main(String[] args) {
        for (String s : args) {
            System.out.println("Arg: " + s);
        }
        
        //Prep possible commandline args
        Options options = FrequencyClassificationService.prepareCommandlineOptions(args);
        
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("FrequencyClassificationService", options);
            System.exit(1);
            return;
        }

        String databaseFile = cmd.getOptionValue("file");
        boolean headless = cmd.hasOption("no-window");
        System.out.println("Using database file: " + databaseFile);
        System.out.println("Running in " + (headless ? "headless" : "windowed") + " mode");
        
        
        FrequencyClassificationService fcs = new FrequencyClassificationService();
        fcs.run();
    }
    
}
