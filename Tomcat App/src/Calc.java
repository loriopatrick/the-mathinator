import javax.servlet.annotation.WebServlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.Date;

import com.mathinator.engine.*;

@WebServlet(urlPatterns = {"/calc", "/calc/*"})
public class Calc extends HttpServlet {
    public static void Append(String path, String content) throws IOException {
        File f = new File(path);
        if (f.exists() || f.createNewFile()) {
            FileWriter fstream = new FileWriter(f, true);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(content);
            out.close();
        } else {
            throw new IOException("could not create file: " + path);
        }
    }

    public static void Log(String log) {
        try {
            System.out.println(Settings.logFile);
            System.out.println("log:\t" + log);
            Append(Settings.logFile, log);
        } catch (Exception e) {
            System.out.println("Failed to log");
            e.printStackTrace();
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().write("I see you... HA ha ha ha");
        Log("GET\t" + request.getLocalAddr());
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String mode = request.getParameter("md"),
                target = request.getParameter("tg"),
                eq = request.getParameter("eq");

        PrintWriter writer = response.getWriter();

        Log(new Date().getTime() + "\t" + request.getRemoteAddr() + "\t" + eq + " >> " + target + '\n');

        try {
            Node n = Parser.CreateNode(eq, target);
            Parser.MarkUp(n, target);
            writer.write(Parser.ReadNodeLatex(n) + '\n');

            if (mode.equals("0")) {
                for (int i = 0; i < Settings.STEPS; i++) {
                    Parser.MarkUp(n);
                    try {
                        writer.write(Parser.ReadNodeLatex(n) + '\n');
                    } catch (Exception e) {
                    }

                    if (Solve2.Step(n, target)) break;
                }
            } else if (mode.equals("1")) {
                int count = 0;
                for (int i = 0; i < Settings.STEPS; i++) {
                    Parser.MarkUp(n);
                    try {
                        writer.write(Parser.ReadNodeLatex(n) + '\n');
                    } catch (Exception e) {
                    }
                    if (Simplify.Step(n)) ++count;
                    if (count > 10) break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            writer.write("Error");
        }

    }
}
