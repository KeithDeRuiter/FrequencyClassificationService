/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package freqclass.rest;

import frequencydisplay.data.Platform;
import frequencydisplay.model.Model;
import static spark.Spark.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 *
 * @author Keith
 */
public class RequestHandler {
    
    private Model model;
    private Gson gson;
    
    /** The port to run the server and respond to request on. */
    private int port = 4567;
    
    public RequestHandler(Model model) {
        this.model = model;
        gson = new GsonBuilder().create();
        initRoutes();
    }
    
    public void stopServer() {
        stop();
    }
    
    
    private void initRoutes() {
        //RESTful Setup
        port(port);
        
        get("/frequencies/:platform", (req, res) -> {
            String platformName = req.params("platform");
            System.out.println("Processing request for frequencies for: " + platformName);
            Platform platform = model.getPlatformByName(platformName);
            System.out.println("Found platform: " + String.valueOf(platform));
            System.out.println(gson.toJson(platform));
            return platform;
        }, new JsonTransformer());
    }
}
