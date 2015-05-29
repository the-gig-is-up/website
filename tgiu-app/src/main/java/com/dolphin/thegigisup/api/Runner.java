
package com.dolphin.thegigisup.api;

import android.os.Handler;
import java.lang.Exception;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.Map;
import java.util.HashMap;

/**
 * Queues tasks to a work pool, and posts completions.
 *
 * @author Team Dolphin
 */
public class Runner {

    private final ExecutorService exec;
    private final Handler handler;

    /**
     * Creates a Runner that queues tasks to the given ExecutorService, and
     * posts completions to the given Handler.
     *
     * @param exec The ExecutorService used to run tasks.
     * @param postHandler The Handler to which completions will be posted.
     */
    public Runner(ExecutorService exec, Handler postHandler) {
        this.exec = exec;
        this.handler = postHandler;
    }

    /**
     * Stops running tasks.
     */
    public void stop() {
        exec.shutdownNow();
    }

    /**
     * Represents a scope in which tasks are relevant. Allows groups of related
     * tasks to be cancelled together.
     */
    public static class Scope {
        private final Map<Task<?>, Future<?>> futures = new HashMap<>();

        public void add(Task<?> task, Future<?> future) {
            futures.put(task, future);
        }

        public void remove(Task<?> task) {
            futures.remove(task);
        }

        /**
         * Cancels a given task.
         *
         * @param task The task to be cancelled.
         */
        public void cancel(Task<?> task) {
            Future<?> future = futures.get(task);
            future.cancel(true);
            remove(task);
        }

        /**
         * Cancels all tasks associated with this scope.
         */
        public void cancelAll() {
            for (Future<?> future : futures.values())
                future.cancel(true);
            futures.clear();
        }
    }

    /**
     * A task to be completed in another thread.
     *
     * @param R The type of the result of the task.
     */
    public interface Task<R> {
        /**
         * Performs useful work and returns a result. Runs in a pool thread.
         *
         * @returns A result to be posted to done.
         * @throws Any Exception to be posted to failed.
         */
        R execute();
        
        /**
         * Completes the task successfully. Runs in the handler thread.
         *
         * @param result The result of execute.
         */
        void done(R result);
        
        /**
         * Completes the task unsuccessfully. Runs in the handler thread.
         * @param e The exception thrown by execute.
         */
        void failed(Exception e);
    }

    // Wraps tasks, wrangles their results and exceptions, and maintains scopes.
    private class Wrapper<R> implements Runnable {
        private Task<R> task;
        private Scope scope;

        public Wrapper(Task<R> task, Scope scope) {
            this.task = task;
            this.scope = scope;
        }

        @Override
        public void run() {
            Runnable posting;

            try {
                final R result = task.execute();
                posting = new Runnable() {
                    public void run() {
                        scope.remove(task);
                        task.done(result);
                    }
                };
            }
            catch (final Exception e) {
                posting = new Runnable() {
                    public void run() {
                        scope.remove(task);
                        task.failed(e);
                    }
                };
            }

            handler.post(posting);
        }
    }

    /**
     * Queues a task to be run in the thread pool.
     *
     * @param task The task to be executed.
     * @param scope The scope with which the task should be associated.
     */
    public<R> void run(Task<R> task, Scope scope) {
        Runnable wrapper = new Wrapper<R>(task, scope);
        Future<?> future = exec.submit(wrapper);
        scope.add(task, future);
    }

}
