import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Main {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", new RootHandler());
        server.createContext("/scrape", new ScrapeHandler());
        server.createContext("/download", new DownloadHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("✅ Server started at: http://localhost:8080");
    }

    // ---- HTML Form ----
    static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String html = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <title>News Metadata Scraper</title>
                    <style>
                        body {
                            font-family: 'Segoe UI', Arial, sans-serif;
                            background-color: #f5f7fa;
                            display: flex;
                            flex-direction: column;
                            align-items: center;
                            margin: 0;
                            padding: 40px;
                        }
                        h2 {
                            color: #333;
                            margin-bottom: 20px;
                        }
                        form {
                            background: #fff;
                            padding: 20px 30px;
                            border-radius: 15px;
                            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
                            width: 60%;
                            max-width: 600px;
                            text-align: center;
                        }
                        input[type="text"] {
                            width: 80%;
                            padding: 10px;
                            border-radius: 8px;
                            border: 1px solid #ccc;
                            font-size: 14px;
                        }
                        button {
                            background-color: #0078d7;
                            color: white;
                            border: none;
                            padding: 10px 20px;
                            margin-top: 10px;
                            border-radius: 8px;
                            cursor: pointer;
                            font-size: 16px;
                        }
                        button:hover {
                            background-color: #00a316ff;
                        }
                        table {
                            border-collapse: collapse;
                            width: 100%;
                            margin-top: 25px;
                        }
                        th, td {
                            border: 1px solid #ddd;
                            padding: 10px;
                            text-align: left;
                        }
                        th {
                            background-color: #0078d7;
                            color: white;
                        }
                        td {
                            background-color: #fff;
                        }
                        #result {
                            margin-top: 30px;
                            width: 60%;
                            max-width: 600px;
                        }
                        footer {
                            margin-top: 60px;
                            color: #777;
                            font-size: 14px;
                        }
                        footer a {
                            color: #0078d7;
                            text-decoration: none;
                        }
                        footer a:hover {
                            text-decoration: underline;
                        }
                    </style>
                    <script>
                        let lastResult = "";

                        async function scrapeMetadata(event) {
                            event.preventDefault();
                            const url = document.getElementById('url').value;
                            const response = await fetch('/scrape?url=' + encodeURIComponent(url));
                            const data = await response.text();
                            lastResult = data;
                            document.getElementById('result').innerHTML = data;
                        }

                        function downloadCSV() {
                            if (!lastResult.trim()) {
                                alert('Please scrape some metadata first!');
                                return;
                            }
                            window.location.href = '/download';
                        }
                    </script>
                </head>
                <body>
                    <h2>📰 News Metadata Scraper</h2>
                    <form onsubmit="scrapeMetadata(event)">
                        <input type="text" id="url" name="url" placeholder="Enter news article URL" required>
                        <br>
                        <button type="submit">Scrape Metadata</button>
                        <button type="button" onclick="downloadCSV()">Download Metadata</button>
                    </form>
                    <div id="result"></div>

                    <footer>
                        <p>Developed by <b>Saurabh Kisan Butale</b> — <a href="#">@2025</a></p>
                    </footer>
                </body>
                </html>
                """;

            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, html.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(html.getBytes());
            }
        }
    }

    // ---- Scraping Logic ----
    static class ScrapeHandler implements HttpHandler {
        static String lastCSV = ""; // stores last scraped data

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            String url = "";

            if (query != null && query.startsWith("url=")) {
                url = URLDecoder.decode(query.substring(4), StandardCharsets.UTF_8);
            }

            String title = "", date = "", author = "", description = "", siteName = "", error = null;

            try {
                Document doc = Jsoup.connect(url).get();

                title = doc.title();

                Element dateEl = doc.selectFirst("meta[property=article:published_time], meta[name=date], time");
                if (dateEl != null) {
                    date = dateEl.attr("content");
                    if (date.isEmpty()) date = dateEl.text();
                }

                Element authorEl = doc.selectFirst("meta[name=author], meta[property=article:author]");
                if (authorEl != null) author = authorEl.attr("content");

                Element descEl = doc.selectFirst("meta[name=description], meta[property=og:description]");
                if (descEl != null) description = descEl.attr("content");

                Element siteEl = doc.selectFirst("meta[property=og:site_name]");
                if (siteEl != null) siteName = siteEl.attr("content");

                if (date.isEmpty()) date = "Not Found";
                if (author.isEmpty()) author = "Not Found";
                if (description.isEmpty()) description = "Not Found";
                if (siteName.isEmpty()) siteName = "Not Found";

            } catch (Exception e) {
                error = e.getMessage();
            }

            String html;
            if (error == null) {
                html = """
                    <table>
                        <tr><th>Field</th><th>Value</th></tr>
                    """ +
                    "<tr><td>URL</td><td>" + url + "</td></tr>" +
                    "<tr><td>Title</td><td>" + title + "</td></tr>" +
                    "<tr><td>Date</td><td>" + date + "</td></tr>" +
                    "<tr><td>Author</td><td>" + author + "</td></tr>" +
                    "<tr><td>Description</td><td>" + description + "</td></tr>" +
                    "<tr><td>Site Name</td><td>" + siteName + "</td></tr>" +
                    "</table>";

                // Prepare CSV for download
                lastCSV = "Field,Value\n"
                        + "URL," + escapeCSV(url) + "\n"
                        + "Title," + escapeCSV(title) + "\n"
                        + "Date," + escapeCSV(date) + "\n"
                        + "Author," + escapeCSV(author) + "\n"
                        + "Description," + escapeCSV(description) + "\n"
                        + "Site Name," + escapeCSV(siteName) + "\n";
            } else {
                html = "<p style='color:red;'>Error: " + error + "</p>";
            }

            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, html.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(html.getBytes());
            }
        }

        private static String escapeCSV(String text) {
            return text.replace("\"", "\"\"");
        }
    }

    // ---- Download CSV ----
    static class DownloadHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String csv = ScrapeHandler.lastCSV;
            if (csv.isEmpty()) {
                csv = "No data available. Please scrape metadata first.";
            }

            exchange.getResponseHeaders().set("Content-Disposition", "attachment; filename=\"metadata.csv\"");
            exchange.getResponseHeaders().set("Content-Type", "text/csv");
            exchange.sendResponseHeaders(200, csv.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(csv.getBytes());
            }
        }
    }
}
