/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package freqclass.rest;

import com.google.gson.Gson;
import spark.ResponseTransformer;

/**
 *
 * @author Keith
 */
public class JsonTransformer implements ResponseTransformer {
    
    private Gson gson = new Gson();

    @Override
    public String render(Object model) {
        return gson.toJson(model);
    }
}
