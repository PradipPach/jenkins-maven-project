package com.example;

import static spark.Spark.*;

/**
 * Simple Web Application using Spark Java Framework
 */
public class WebApp {
    
    private static final Calculator calculator = new Calculator();
    
    public static void main(String[] args) {
        // Bind to all network interfaces (0.0.0.0) to allow external access
        ipAddress("0.0.0.0");
        
        // Set port (default: 5000)
        port(getPort());
        
        // Enable CORS
        enableCORS();
        
        // Configure routes
        configureRoutes();
        
        System.out.println("========================================");
        System.out.println("Server started successfully!");
        System.out.println("Access the application at:");
        System.out.println("http://0.0.0.0:" + getPort());
        System.out.println("========================================");
    }
    
    private static int getPort() {
        String port = System.getenv("PORT");
        if (port != null) {
            return Integer.parseInt(port);
        }
        return 5000; // Default application port
    }
    
    private static void enableCORS() {
        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            
            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            
            return "OK";
        });
        
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
        });
    }
    
    private static void configureRoutes() {
        // Home page
        get("/", (req, res) -> {
            res.type("text/html");
            return getHomePage();
        });
        
        // API endpoint - Calculator add
        get("/api/add/:a/:b", (req, res) -> {
            res.type("application/json");
            try {
                int a = Integer.parseInt(req.params(":a"));
                int b = Integer.parseInt(req.params(":b"));
                int result = calculator.add(a, b);
                return String.format("{\"operation\":\"add\",\"a\":%d,\"b\":%d,\"result\":%d}", a, b, result);
            } catch (NumberFormatException e) {
                res.status(400);
                return "{\"error\":\"Invalid numbers provided\"}";
            }
        });
        
        // API endpoint - Calculator subtract
        get("/api/subtract/:a/:b", (req, res) -> {
            res.type("application/json");
            try {
                int a = Integer.parseInt(req.params(":a"));
                int b = Integer.parseInt(req.params(":b"));
                int result = calculator.subtract(a, b);
                return String.format("{\"operation\":\"subtract\",\"a\":%d,\"b\":%d,\"result\":%d}", a, b, result);
            } catch (NumberFormatException e) {
                res.status(400);
                return "{\"error\":\"Invalid numbers provided\"}";
            }
        });
        
        // API endpoint - Calculator multiply
        get("/api/multiply/:a/:b", (req, res) -> {
            res.type("application/json");
            try {
                int a = Integer.parseInt(req.params(":a"));
                int b = Integer.parseInt(req.params(":b"));
                int result = calculator.multiply(a, b);
                return String.format("{\"operation\":\"multiply\",\"a\":%d,\"b\":%d,\"result\":%d}", a, b, result);
            } catch (NumberFormatException e) {
                res.status(400);
                return "{\"error\":\"Invalid numbers provided\"}";
            }
        });
        
        // API endpoint - Calculator divide
        get("/api/divide/:a/:b", (req, res) -> {
            res.type("application/json");
            try {
                int a = Integer.parseInt(req.params(":a"));
                int b = Integer.parseInt(req.params(":b"));
                double result = calculator.divide(a, b);
                return String.format("{\"operation\":\"divide\",\"a\":%d,\"b\":%d,\"result\":%.2f}", a, b, result);
            } catch (NumberFormatException e) {
                res.status(400);
                return "{\"error\":\"Invalid numbers provided\"}";
            } catch (IllegalArgumentException e) {
                res.status(400);
                return "{\"error\":\"" + e.getMessage() + "\"}";
            }
        });
        
        // Health check endpoint
        get("/health", (req, res) -> {
            res.type("application/json");
            return "{\"status\":\"UP\",\"application\":\"jenkins-maven-project\",\"version\":\"1.0-SNAPSHOT\"}";
        });
        
        // API documentation
        get("/api", (req, res) -> {
            res.type("text/html");
            return getApiDocumentation();
        });
        
        // 404 handler
        notFound((req, res) -> {
            res.type("text/html");
            return "<html><body><h1>404 - Page Not Found</h1><p><a href='/'>Go to Home</a></p></body></html>";
        });
    }
    
    private static String getHomePage() {
        return "<!DOCTYPE html>" +
                "<html lang='en'>" +
                "<head>" +
                "    <meta charset='UTF-8'>" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "    <title>Jenkins Maven Project - Web Application</title>" +
                "    <style>" +
                "        * { margin: 0; padding: 0; box-sizing: border-box; }" +
                "        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); min-height: 100vh; padding: 20px; }" +
                "        .container { max-width: 1200px; margin: 0 auto; }" +
                "        .header { text-align: center; color: white; margin-bottom: 40px; }" +
                "        .header h1 { font-size: 3em; margin-bottom: 10px; text-shadow: 2px 2px 4px rgba(0,0,0,0.3); }" +
                "        .header p { font-size: 1.2em; opacity: 0.9; }" +
                "        .card { background: white; border-radius: 15px; padding: 30px; margin-bottom: 30px; box-shadow: 0 10px 30px rgba(0,0,0,0.2); }" +
                "        .card h2 { color: #667eea; margin-bottom: 20px; border-bottom: 3px solid #667eea; padding-bottom: 10px; }" +
                "        .calculator { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 20px; margin-top: 20px; }" +
                "        .calc-section { background: #f8f9fa; padding: 20px; border-radius: 10px; border-left: 4px solid #667eea; }" +
                "        .calc-section h3 { color: #333; margin-bottom: 15px; }" +
                "        .input-group { margin-bottom: 15px; }" +
                "        .input-group label { display: block; margin-bottom: 5px; color: #555; font-weight: bold; }" +
                "        .input-group input { width: 100%; padding: 10px; border: 2px solid #ddd; border-radius: 5px; font-size: 1em; }" +
                "        .input-group input:focus { outline: none; border-color: #667eea; }" +
                "        button { background: #667eea; color: white; border: none; padding: 12px 30px; border-radius: 5px; cursor: pointer; font-size: 1em; font-weight: bold; width: 100%; transition: all 0.3s; }" +
                "        button:hover { background: #764ba2; transform: translateY(-2px); box-shadow: 0 5px 15px rgba(0,0,0,0.2); }" +
                "        .result { margin-top: 15px; padding: 15px; background: #e8f5e9; border-radius: 5px; border-left: 4px solid #4caf50; display: none; }" +
                "        .result.show { display: block; }" +
                "        .result strong { color: #2e7d32; }" +
                "        .api-list { list-style: none; }" +
                "        .api-list li { background: #f8f9fa; margin: 10px 0; padding: 15px; border-radius: 8px; border-left: 4px solid #667eea; }" +
                "        .api-list code { background: #e9ecef; padding: 3px 8px; border-radius: 4px; color: #d63384; font-family: 'Courier New', monospace; }" +
                "        .api-list a { color: #667eea; text-decoration: none; font-weight: bold; }" +
                "        .api-list a:hover { text-decoration: underline; }" +
                "        .status-badge { display: inline-block; background: #4caf50; color: white; padding: 5px 15px; border-radius: 20px; font-size: 0.9em; margin-left: 10px; }" +
                "        .footer { text-align: center; color: white; margin-top: 40px; opacity: 0.9; }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class='container'>" +
                "        <div class='header'>" +
                "            <h1>🚀 Jenkins Maven Project</h1>" +
                "            <p>Web Application with Calculator API <span class='status-badge'>ONLINE</span></p>" +
                "        </div>" +
                "        " +
                "        <div class='card'>" +
                "            <h2>🧮 Interactive Calculator</h2>" +
                "            <div class='calculator'>" +
                "                <div class='calc-section'>" +
                "                    <h3>➕ Addition</h3>" +
                "                    <div class='input-group'>" +
                "                        <label>Number 1:</label>" +
                "                        <input type='number' id='addA' value='10'>" +
                "                    </div>" +
                "                    <div class='input-group'>" +
                "                        <label>Number 2:</label>" +
                "                        <input type='number' id='addB' value='5'>" +
                "                    </div>" +
                "                    <button onclick='calculate(\"add\")'>Calculate</button>" +
                "                    <div class='result' id='addResult'></div>" +
                "                </div>" +
                "                " +
                "                <div class='calc-section'>" +
                "                    <h3>➖ Subtraction</h3>" +
                "                    <div class='input-group'>" +
                "                        <label>Number 1:</label>" +
                "                        <input type='number' id='subA' value='20'>" +
                "                    </div>" +
                "                    <div class='input-group'>" +
                "                        <label>Number 2:</label>" +
                "                        <input type='number' id='subB' value='8'>" +
                "                    </div>" +
                "                    <button onclick='calculate(\"subtract\")'>Calculate</button>" +
                "                    <div class='result' id='subtractResult'></div>" +
                "                </div>" +
                "                " +
                "                <div class='calc-section'>" +
                "                    <h3>✖️ Multiplication</h3>" +
                "                    <div class='input-group'>" +
                "                        <label>Number 1:</label>" +
                "                        <input type='number' id='mulA' value='6'>" +
                "                    </div>" +
                "                    <div class='input-group'>" +
                "                        <label>Number 2:</label>" +
                "                        <input type='number' id='mulB' value='7'>" +
                "                    </div>" +
                "                    <button onclick='calculate(\"multiply\")'>Calculate</button>" +
                "                    <div class='result' id='multiplyResult'></div>" +
                "                </div>" +
                "                " +
                "                <div class='calc-section'>" +
                "                    <h3>➗ Division</h3>" +
                "                    <div class='input-group'>" +
                "                        <label>Number 1:</label>" +
                "                        <input type='number' id='divA' value='50'>" +
                "                    </div>" +
                "                    <div class='input-group'>" +
                "                        <label>Number 2:</label>" +
                "                        <input type='number' id='divB' value='5'>" +
                "                    </div>" +
                "                    <button onclick='calculate(\"divide\")'>Calculate</button>" +
                "                    <div class='result' id='divideResult'></div>" +
                "                </div>" +
                "            </div>" +
                "        </div>" +
                "        " +
                "        <div class='card'>" +
                "            <h2>📡 API Endpoints</h2>" +
                "            <ul class='api-list'>" +
                "                <li><code>GET /</code> - Home page</li>" +
                "                <li><code>GET /api/add/:a/:b</code> - <a href='/api/add/10/5' target='_blank'>Try: Add 10 + 5</a></li>" +
                "                <li><code>GET /api/subtract/:a/:b</code> - <a href='/api/subtract/20/8' target='_blank'>Try: Subtract 20 - 8</a></li>" +
                "                <li><code>GET /api/multiply/:a/:b</code> - <a href='/api/multiply/6/7' target='_blank'>Try: Multiply 6 × 7</a></li>" +
                "                <li><code>GET /api/divide/:a/:b</code> - <a href='/api/divide/50/5' target='_blank'>Try: Divide 50 ÷ 5</a></li>" +
                "                <li><code>GET /health</code> - <a href='/health' target='_blank'>Health check endpoint</a></li>" +
                "            </ul>" +
                "        </div>" +
                "        " +
                "        <div class='footer'>" +
                "            <p>Built with ❤️ using Java, Maven & Spark Framework</p>" +
                "            <p>Deployed via Jenkins CI/CD Pipeline</p>" +
                "        </div>" +
                "    </div>" +
                "    " +
                "    <script>" +
                "        function calculate(operation) {" +
                "            let a, b;" +
                "            if (operation === 'add') {" +
                "                a = document.getElementById('addA').value;" +
                "                b = document.getElementById('addB').value;" +
                "            } else if (operation === 'subtract') {" +
                "                a = document.getElementById('subA').value;" +
                "                b = document.getElementById('subB').value;" +
                "            } else if (operation === 'multiply') {" +
                "                a = document.getElementById('mulA').value;" +
                "                b = document.getElementById('mulB').value;" +
                "            } else if (operation === 'divide') {" +
                "                a = document.getElementById('divA').value;" +
                "                b = document.getElementById('divB').value;" +
                "            }" +
                "            " +
                "            fetch(`/api/${operation}/${a}/${b}`)" +
                "                .then(response => response.json())" +
                "                .then(data => {" +
                "                    const resultDiv = document.getElementById(operation + 'Result');" +
                "                    if (data.error) {" +
                "                        resultDiv.innerHTML = '<strong>Error:</strong> ' + data.error;" +
                "                        resultDiv.style.background = '#ffebee';" +
                "                        resultDiv.style.borderLeft = '4px solid #f44336';" +
                "                    } else {" +
                "                        resultDiv.innerHTML = '<strong>Result:</strong> ' + data.result;" +
                "                        resultDiv.style.background = '#e8f5e9';" +
                "                        resultDiv.style.borderLeft = '4px solid #4caf50';" +
                "                    }" +
                "                    resultDiv.classList.add('show');" +
                "                })" +
                "                .catch(error => {" +
                "                    const resultDiv = document.getElementById(operation + 'Result');" +
                "                    resultDiv.innerHTML = '<strong>Error:</strong> Failed to perform calculation';" +
                "                    resultDiv.style.background = '#ffebee';" +
                "                    resultDiv.style.borderLeft = '4px solid #f44336';" +
                "                    resultDiv.classList.add('show');" +
                "                });" +
                "        }" +
                "    </script>" +
                "</body>" +
                "</html>";
    }
    
    private static String getApiDocumentation() {
        return "<!DOCTYPE html>" +
                "<html><head><title>API Documentation</title></head>" +
                "<body><h1>API Documentation</h1>" +
                "<p>Available endpoints:</p>" +
                "<ul>" +
                "<li>GET /api/add/:a/:b - Add two numbers</li>" +
                "<li>GET /api/subtract/:a/:b - Subtract two numbers</li>" +
                "<li>GET /api/multiply/:a/:b - Multiply two numbers</li>" +
                "<li>GET /api/divide/:a/:b - Divide two numbers</li>" +
                "<li>GET /health - Health check</li>" +
                "</ul>" +
                "<p><a href='/'>Back to Home</a></p>" +
                "</body></html>";
    }
}
