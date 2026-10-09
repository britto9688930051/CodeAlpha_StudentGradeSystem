import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Web version of the Student Grade System (for deploying on Render).
 * Uses Java's built-in HTTP server, so no extra libraries are needed.
 * Reuses the same Student class as the console version.
 */
public class WebServer {

    private static final List<Student> students = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        // Render provides the port in the PORT environment variable.
        String portEnv = System.getenv("PORT");
        int port = (portEnv != null && !portEnv.isEmpty()) ? Integer.parseInt(portEnv) : 8080;

        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);
        server.createContext("/", WebServer::handleHome);
        server.createContext("/add", WebServer::handleAddStudent);
        server.createContext("/grade", WebServer::handleAddGrade);
        server.createContext("/remove", WebServer::handleRemove);
        server.start();
        System.out.println("Student Grade System running on port " + port);
    }

    // ---------- Handlers ----------

    private static void handleHome(HttpExchange ex) throws IOException {
        if (!ex.getRequestURI().getPath().equals("/")) {
            send(ex, 404, "Not found");
            return;
        }
        send(ex, 200, renderPage());
    }

    private static void handleAddStudent(HttpExchange ex) throws IOException {
        if (!requirePost(ex)) return;
        Map<String, String> form = readForm(ex);
        String name = form.getOrDefault("name", "").trim();
        String gradesText = form.getOrDefault("grades", "").trim();

        synchronized (students) {
            if (!name.isEmpty() && findStudent(name) == null) {
                Student s = new Student(name);
                if (!gradesText.isEmpty()) {
                    for (String part : gradesText.split("[,\\s]+")) {
                        Double g = parseGrade(part);
                        if (g != null) s.addGrade(g);
                    }
                }
                students.add(s);
            }
        }
        redirectHome(ex);
    }

    private static void handleAddGrade(HttpExchange ex) throws IOException {
        if (!requirePost(ex)) return;
        Map<String, String> form = readForm(ex);
        Double g = parseGrade(form.getOrDefault("grade", ""));
        synchronized (students) {
            Student s = findStudent(form.getOrDefault("name", ""));
            if (s != null && g != null) s.addGrade(g);
        }
        redirectHome(ex);
    }

    private static void handleRemove(HttpExchange ex) throws IOException {
        if (!requirePost(ex)) return;
        Map<String, String> form = readForm(ex);
        synchronized (students) {
            Student s = findStudent(form.getOrDefault("name", ""));
            if (s != null) students.remove(s);
        }
        redirectHome(ex);
    }

    // ---------- Page rendering ----------

    private static String renderPage() {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'>")
          .append("<meta name='viewport' content='width=device-width, initial-scale=1'>")
          .append("<title>Student Grade System</title><style>")
          .append("body{font-family:Segoe UI,Arial,sans-serif;background:#f4f6fb;margin:0;color:#222}")
          .append("header{background:#2d3a8c;color:#fff;padding:20px;text-align:center}")
          .append("main{max-width:900px;margin:20px auto;padding:0 16px}")
          .append(".card{background:#fff;border-radius:10px;padding:18px;margin-bottom:18px;box-shadow:0 2px 6px rgba(0,0,0,.08)}")
          .append("h2{margin-top:0;font-size:18px}")
          .append("input,select,button{padding:9px;margin:4px 4px 4px 0;border:1px solid #ccc;border-radius:6px;font-size:14px}")
          .append("button{background:#2d3a8c;color:#fff;border:none;cursor:pointer}")
          .append("button.del{background:#c0392b}")
          .append("table{width:100%;border-collapse:collapse}")
          .append("th,td{text-align:left;padding:8px;border-bottom:1px solid #eee}")
          .append("th{background:#eef0fa}")
          .append(".stats p{margin:4px 0}")
          .append("</style></head><body>")
          .append("<header><h1>Student Grade System</h1><p>CodeAlpha Java Internship Task</p></header><main>");

        synchronized (students) {
            // Add student
            sb.append("<div class='card'><h2>Add a student</h2>")
              .append("<form method='post' action='/add'>")
              .append("<input name='name' placeholder='Student name' required>")
              .append("<input name='grades' placeholder='Grades e.g. 90, 85.5, 72' size='28'>")
              .append("<button>Add student</button></form></div>");

            // Add grade
            if (!students.isEmpty()) {
                sb.append("<div class='card'><h2>Add a grade to a student</h2>")
                  .append("<form method='post' action='/grade'><select name='name'>");
                for (Student s : students) {
                    sb.append("<option>").append(esc(s.getName())).append("</option>");
                }
                sb.append("</select>")
                  .append("<input name='grade' type='number' min='0' max='100' step='0.01' placeholder='0-100' required>")
                  .append("<button>Add grade</button></form></div>");
            }

            // Summary report
            sb.append("<div class='card'><h2>Summary report</h2>");
            if (students.isEmpty()) {
                sb.append("<p>No students yet. Add one above.</p>");
            } else {
                sb.append("<table><tr><th>Name</th><th>Grades</th><th>Average</th><th>Highest</th>")
                  .append("<th>Lowest</th><th>Grade</th><th></th></tr>");

                double total = 0;
                int graded = 0;
                double classHigh = Double.NEGATIVE_INFINITY, classLow = Double.POSITIVE_INFINITY;
                String topName = "-", lowName = "-";

                for (Student s : students) {
                    sb.append("<tr><td>").append(esc(s.getName())).append("</td>");
                    sb.append("<td>").append(esc(s.getGrades().toString())).append("</td>");
                    if (s.getGrades().isEmpty()) {
                        sb.append("<td>N/A</td><td>N/A</td><td>N/A</td><td>N/A</td>");
                    } else {
                        sb.append(String.format("<td>%.2f</td><td>%.2f</td><td>%.2f</td><td>%s</td>",
                                s.getAverage(), s.getHighest(), s.getLowest(), s.getLetterGrade()));
                        total += s.getAverage();
                        graded++;
                        if (s.getHighest() > classHigh) { classHigh = s.getHighest(); topName = s.getName(); }
                        if (s.getLowest() < classLow) { classLow = s.getLowest(); lowName = s.getName(); }
                    }
                    sb.append("<td><form method='post' action='/remove'>")
                      .append("<input type='hidden' name='name' value='").append(esc(s.getName())).append("'>")
                      .append("<button class='del'>Remove</button></form></td></tr>");
                }
                sb.append("</table><div class='stats'><hr>");
                sb.append("<p><b>Total students:</b> ").append(students.size()).append("</p>");
                if (graded > 0) {
                    sb.append(String.format("<p><b>Class average:</b> %.2f</p>", total / graded));
                    sb.append(String.format("<p><b>Highest score:</b> %.2f (%s)</p>", classHigh, esc(topName)));
                    sb.append(String.format("<p><b>Lowest score:</b> %.2f (%s)</p>", classLow, esc(lowName)));
                }
                sb.append("</div>");
            }
            sb.append("</div>");
        }
        sb.append("</main></body></html>");
        return sb.toString();
    }

    // ---------- Helpers ----------

    private static Student findStudent(String name) {
        for (Student s : students) {
            if (s.getName().equalsIgnoreCase(name.trim())) return s;
        }
        return null;
    }

    private static Double parseGrade(String text) {
        try {
            double v = Double.parseDouble(text.trim());
            return (v >= 0 && v <= 100) ? v : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static boolean requirePost(HttpExchange ex) throws IOException {
        if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) {
            send(ex, 405, "Method not allowed");
            return false;
        }
        return true;
    }

    private static Map<String, String> readForm(HttpExchange ex) throws IOException {
        Map<String, String> map = new HashMap<>();
        try (InputStream in = ex.getRequestBody()) {
            String body = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            for (String pair : body.split("&")) {
                if (pair.isEmpty()) continue;
                String[] kv = pair.split("=", 2);
                String key = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
                String value = kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : "";
                map.put(key, value);
            }
        }
        return map;
    }

    private static void redirectHome(HttpExchange ex) throws IOException {
        ex.getResponseHeaders().set("Location", "/");
        ex.sendResponseHeaders(303, -1);
        ex.close();
    }

    private static void send(HttpExchange ex, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static String esc(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
}
