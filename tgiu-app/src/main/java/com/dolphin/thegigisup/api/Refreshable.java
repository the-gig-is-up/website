
package com.dolphin.thegigisup.api;

/**
 * A refreshable interface used to refresh fragments using a serviceInterface
 * and runner object
 *
 * @author Team Dolphin
 */
public interface Refreshable {
    public void refresh(Runner runner, ServiceInterface service);
}
