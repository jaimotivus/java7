package org.javaee7.servlet.async;

import java.io.IOException;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Asynchronous servlet that demonstrates the use of AsyncContext and ManagedExecutorService.
 * The servlet starts an asynchronous context and submits a task to a managed executor.
 */
@WebServlet(urlPatterns = "/MyAsyncServlet", asyncSupported = true)
public class MyAsyncServlet extends HttpServlet {

    private static final long serialVersionUID = 3709640331218336841L;
    
    @Resource
    private ManagedExecutorService executor;

    /**
     * Processes requests for both HTTP GET and POST methods.
     * Creates an AsyncContext and submits an asynchronous task.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        AsyncContext asyncContext = request.startAsync();
        
        // Add listener to handle async events
        asyncContext.addListener(new AsyncEventHandler());
        
        // Submit the async task to the executor
        executor.submit(new AsyncServiceTask(asyncContext));
    }

    /**
     * Handles the HTTP GET method by delegating to processRequest.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP POST method by delegating to processRequest.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     */
    @Override
    public String getServletInfo() {
        return "Asynchronous Servlet Example";
    }
    
    /**
     * Handler for AsyncContext events.
     */
    private static class AsyncEventHandler implements AsyncListener {
        @Override
        public void onComplete(AsyncEvent event) throws IOException {
            event.getSuppliedResponse().getWriter().println("onComplete");
        }

        @Override
        public void onTimeout(AsyncEvent event) throws IOException {
            event.getSuppliedResponse().getWriter().println("onTimeout");
            event.getAsyncContext().complete();
        }

        @Override
        public void onError(AsyncEvent event) throws IOException {
            event.getSuppliedResponse().getWriter().println("onError");
        }

        @Override
        public void onStartAsync(AsyncEvent event) throws IOException {
            event.getSuppliedResponse().getWriter().println("onStartAsync");
        }
    }

    /**
     * Task that runs asynchronously to process the request.
     */
    private static class AsyncServiceTask implements Runnable {
        private final AsyncContext asyncContext;

        public AsyncServiceTask(AsyncContext asyncContext) {
            this.asyncContext = asyncContext;
        }

        @Override
        public void run() {
            try {
                asyncContext.getResponse().getWriter().println("Running inside MyAsyncService");
                asyncContext.complete();
            } catch (IOException e) {
                throw new IllegalStateException("Error writing to the response", e);
            }
        }
    }
}