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

// This file is compatible with Java 21 without changes
// Java 21 is backward compatible with Java 8 language features used here
@WebServlet(urlPatterns = "/MyAsyncServlet", asyncSupported = true)
public class MyAsyncServlet extends HttpServlet {

    private static final long serialVersionUID = 3709640331218336841L;
    
    @Resource
    ManagedExecutorService executor;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AsyncContext asyncContext = request.startAsync();

        // Anonymous inner class implementation is still valid in Java 21
        // Could be replaced with lambda in future refactoring if desired
        asyncContext.addListener(new AsyncListener() {
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
        });
        
        executor.submit(new MyAsyncService(asyncContext));
    }

    // Inner class implementation remains compatible with Java 21
    class MyAsyncService implements Runnable {

        AsyncContext asyncContext;

        public MyAsyncService(AsyncContext asyncContext) {
            this.asyncContext = asyncContext;
        }

        @Override
        public void run() {
            try {
                asyncContext.getResponse().getWriter().println("Running inside MyAsyncService");
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
            asyncContext.complete();
        }
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }
}