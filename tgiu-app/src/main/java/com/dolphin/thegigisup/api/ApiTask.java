
package com.dolphin.thegigisup.api;

import java.lang.Exception;

/**
 * An abstract class ApiTask that initialises, sends a query to the API and
 * runs a done() or a failed() method depending on the success of the API call
 *
 * @param <Result> The API should return this specified result type
 * @author Team Dolphin
 */
public abstract class ApiTask<Result>
        implements Runner.Task<Result> {

    private final ServiceInterface service;

    public ApiTask(ServiceInterface service) {
        this.service = service;
    }

    abstract public Result doQuery(ServiceInterface service);
    abstract public void done(Result result);
    abstract public void failed(Exception exception);

    @Override
    public final Result execute() {
        return doQuery(service);
    }
}
