
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TennisScoreApp {

    private static final Logger logger =
            Logger.getLogger(TennisScoreApp.class.getName());

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0);

        server.createContext("/", TennisScoreApp::handleRequest);
        server.setExecutor(null);
        server.start();

        logger.info("Tennis Score Tracker started on port 8080");
    }

    private static void handleRequest(HttpExchange exchange)
            throws IOException {

        String method = exchange.getRequestMethod();

        try {
            logger.fine("Received HTTP request: " + method);

            if ("GET".equalsIgnoreCase(method)) {
                logger.info("Score tracker page accessed");

                sendHtml(exchange, page(
                        "Tennis Score Tracker", "", "", "", 0, 0));
                return;
            }

            if ("POST".equalsIgnoreCase(method)) {
                String body = new String(
                        exchange.getRequestBody().readAllBytes(),
                        StandardCharsets.UTF_8);

                Map<String, String> form = parseForm(body);

                String player1 = form.getOrDefault(
                        "player1", "Player 1");
                String player2 = form.getOrDefault(
                        "player2", "Player 2");

                int games1 = parseScore(form.get("games1"));
                int games2 = parseScore(form.get("games2"));

                logger.info("Score submitted: " + player1 +
                        "=" + games1 + ", " + player2 + "=" + games2);

                String result;

                if (games1 == 6 && games1 - games2 >= 2) {
                    result = player1 + " has won the set " +
                            games1 + "-" + games2 + "!";
                    logger.info("Set completed: " + result);
                } else if (games2 == 6 && games2 - games1 >= 2) {
                    result = player2 + " has won the set " +
                            games2 + "-" + games1 + "!";
                    logger.info("Set completed: " + result);
                } else if (games1 == games2) {
                    result = "The set is tied at " +
                            games1 + "-" + games2 + ".";
                    logger.info("Tied score recorded");
                } else if (games1 > games2) {
                    result = player1 + " is leading " +
                            games1 + "-" + games2 + ".";
                } else {
                    result = player2 + " is leading " +
                            games2 + "-" + games1 + ".";
                }

                sendHtml(exchange, page(
                        "Tennis Score Tracker", result,
                        player1, player2, games1, games2));
                return;
            }

            logger.warning("Unsupported HTTP method: " + method);
            exchange.sendResponseHeaders(405, -1);

        } catch (IOException | RuntimeException e) {
            logger.log(Level.SEVERE,
                    "Request processing failed for method: " + method, e);
            throw e;
        } finally {
            exchange.close();
        }
    }

    static int parseScore(String value) {
        try {
            int score = Integer.parseInt(value);

            if (score < 0 || score > 6) {
                logger.warning("Score out of range: " + score);
            }

            return Math.max(0, Math.min(score, 6));

        } catch (NumberFormatException e) {
            logger.log(Level.WARNING,
                    "Invalid score input: " + value, e);
            return 0;
        }
    }

    private static Map<String, String> parseForm(String body) {
        Map<String, String> form = new HashMap<>();

        for (String pair : body.split("&")) {
            String[] parts = pair.split("=", 2);

            if (parts.length == 2) {
                form.put(
                        URLDecoder.decode(
                                parts[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(
                                parts[1], StandardCharsets.UTF_8)
                );
            }
        }

        logger.fine("Form parsing completed");
        return form;
    }

    private static String page(String title, String result,
                               String player1, String player2,
                               int games1, int games2) {

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>%s</title>
                <style>
                    body { font-family: Arial, sans-serif; max-width: 700px; margin: 50px auto; padding: 20px; }
                    form { display: grid; gap: 12px; max-width: 400px; }
                    input, button { padding: 10px; font-size: 16px; }
                    .result { margin-top: 25px; padding: 15px; background: #eef6ff; border-radius: 8px; }
                </style>
            </head>
            <body>
                <h1>🎾 Tennis Score Tracker</h1>
                <p>Enter the current game score to track the match! CI update verified.</p>
                <form method="post">
                    <input name="player1" placeholder="Player 1" value="%s" required>
                    <input name="games1" type="number" min="0" max="6" value="%d" required>
                    <input name="player2" placeholder="Player 2" value="%s" required>
                    <input name="games2" type="number" min="0" max="6" value="%d" required>
                    <button type="submit">Check Score</button>
                    <button type="button" onclick="window.location.href='/'">Reset Scores</button>
                </form>
                %s
            </body>
            </html>
            """.formatted(title, player1, games1, player2, games2,
                result.isBlank() ? "" :
                    "<div class='result'><strong>" +
                    result + "</strong></div>");
    }

    private static void sendHtml(HttpExchange exchange, String html)
            throws IOException {

        byte[] response = html.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type", "text/html; charset=UTF-8");

        exchange.sendResponseHeaders(200, response.length);

        try (OutputStream output = exchange.getResponseBody()) {
            output.write(response);
        }
    }
}
