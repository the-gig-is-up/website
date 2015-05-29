package com.dolphin.thegigisup.api;
import retrofit.RestAdapter;


/**
 * REST API service factory
 */
public class ServiceFactory {

    private static final String DEF_ENDPOINT = "http://thegigisup.co.uk/api";
    private static ServiceInterface REST_CLIENT;


    /**
     * Fired when app instantiated
     */
    static {
        createInstance(DEF_ENDPOINT);
    }


    /**
     * Implements and creates a TGIU API service interface pointing at the given
     * endpoint.
     */
    public static ServiceInterface createInstance() {
        return REST_CLIENT;
    }

    /**
     * Implements and creates a TGIU API service interface pointing at the given
     * endpoint.
     * @param endpointURL The API endpoint to bind to.
     */
    private static void createInstance(String endpointURL) {

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(endpointURL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        REST_CLIENT = restAdapter.create(ServiceInterface.class);
    }
}
