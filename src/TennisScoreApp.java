import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class TennisScoreApp {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", TennisScoreApp::handleRequest);
        server.setExecutor(null);
        System.out.println("Tennis Score Tracker running at http://localhost:8080");
        server.start();
    }

    private static void handleRequest(HttpExchange exchange) throws IOException {
        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendHtml(exchange, page("Tennis Score Tracker", ""));
            return;
        }

        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, String> form = parseForm(body);

            String player1 = form.getOrDefault("player1", "Player 1");
            String player2 = form.getOrDefault("player2", "Player 2");
            int games1 = parseScore(form.get("games1"));
            int games2 = parseScore(form.get("games2"));

            String result;
            if (games1 == 6 && games1 - games2 >= 2) {
    result = player1 + " has won the set " + games1 + "-" + games2 + "!";
} else if (games2 == 6 && games2 - games1 >= 2) {
    result = player2 + " has won the set " + games2 + "-" + games1 + "!";
} else if (games1 == games2) {
    result = "The set is tied at " + games1 + "-" + games2 + ".";
} else if (games1 > games2) {
    result = player1 + " is leading " + games1 + "-" + games2 + ".";
} else {
    result = player2 + " is leading " + games2 + "-" + games1 + ".";
}

            sendHtml(exchange, page("Tennis Score Tracker", result));
            return;
        }

        exchange.sendResponseHeaders(405, -1);
    }

    private static int parseScore(String value) {
        try {
            int score = Integer.parseInt(value);
            return Math.max(0, Math.min(score, 6));
        } catch (Exception e) {
            return 0;
        }
    }

    private static Map<String, String> parseForm(String body) {
        Map<String, String> form = new HashMap<>();
        for (String pair : body.split("&")) {
            String[] parts = pair.split("=", 2);
            if (parts.length == 2) {
                form.put(
                    URLDecoder.decode(parts[0], StandardCharsets.UTF_8),
                    URLDecoder.decode(parts[1], StandardCharsets.UTF_8)
                );
            }
        }
        return form;
    }

    private static String page(String title, String result) {
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
                <p>Enter the current game score to see which player is leading.</p>
                <form method="post">
                    <input name="player1" placeholder="Player 1" required>
                    <input name="games1" type="number" min="0" max="6" value="0" required>
                    <input name="player2" placeholder="Player 2" required>
                    <input name="games2" type="number" min="0" max="6" value="0" required>
                    <button type="submit">Check Score</button>
                </form>
                %s
            </body>
            </html>
            """.formatted(title, result.isBlank() ? "" : "<div class='result'><strong>" + result + "</strong></div>");
    }

    private static void sendHtml(HttpExchange exchange, String html) throws IOException {
        byte[] response = html.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, response.length);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(response);
        }
    }
}
