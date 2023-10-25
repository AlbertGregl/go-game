package hr.gregl.gogame.game.utility;

import java.io.*;

public class PrintDocumentationUtilDELETE {
    private static PrintDocumentationUtilDELETE instance;
    private static final String DOC_HTML = "./src/main/java/hr/gregl/gogame/game/documents/documentation.html";
    private final File FILE = new File("./src/main/java/hr/gregl/gogame/game");
    private final File DOC_FILE = new File(DOC_HTML);

    private PrintDocumentationUtilDELETE() {
    }
    public static PrintDocumentationUtilDELETE getInstance() {
        if (instance == null) {
            instance = new PrintDocumentationUtilDELETE();
        }
        return instance;
    }

    public void printDocumentation(){
        print(FILE);
    }

    private void print(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    print(f);
                }
            }
        } else if (file.getName().endsWith(".java")) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                BufferedWriter writer = new BufferedWriter(new FileWriter(DOC_FILE));
                while ((line = reader.readLine()) != null) {
                    if (line.contains("public class")) {
                        System.out.println(line);
                        writer.write("<h1>" + line + "</h1>" + "\n");
                        writer.close();
                    } else if (line.contains("public enum")) {
                        System.out.println(line);
                    } else if (line.contains("public interface")) {
                        System.out.println(line);
                    } else if (line.contains("public abstract class")) {
                        System.out.println(line);
                    } else if (line.contains("public static")) {
                        System.out.println(line);
                    } else if (line.contains("public static final")) {
                        System.out.println(line);
                    } else if (line.contains("public")) {
                        System.out.println(line);
                    } else if (line.contains("private")) {
                        System.out.println(line);
                    } else if (line.contains("private static")) {
                        System.out.println(line);
                    } else if (line.contains("private static final")) {
                        System.out.println(line);
                    } else if (line.contains("protected")) {
                        System.out.println(line);
                    } else if (line.contains("package")) {
                        System.out.println(line);
                    }
                }
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
    }
}
