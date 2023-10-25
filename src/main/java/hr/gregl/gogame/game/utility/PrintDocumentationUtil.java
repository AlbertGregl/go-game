package hr.gregl.gogame.game.utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;

public class PrintDocumentationUtil {
    private static PrintDocumentationUtil instance;
    private final File TARGET_DIR = new File("C:\\Users\\alber\\OneDrive\\Desktop\\repo\\go-game\\target");

    private PrintDocumentationUtil() {
    }

    public static PrintDocumentationUtil getInstance() {
        if (instance == null) {
            instance = new PrintDocumentationUtil();
        }
        return instance;
    }

    public void printDocumentation(File saveFile) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile))) {
            processDirectory(TARGET_DIR, writer);
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    private void processDirectory(File dir, BufferedWriter writer) {
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isDirectory()) {
                processDirectory(file, writer);
            } else if (file.getName().endsWith(".class")) {
                try {
                    String className = getClassName(file);
                    Class<?> cls = Class.forName(className);
                    documentClass(cls, writer);
                } catch (ClassNotFoundException | IOException e) {
                    //e.printStackTrace();
                }
            }
        }
    }

    private String getClassName(File file) {
        String path = file.getPath();
        // 8 is the length of "classes" string and 6 is the length of ".class" string :)
        String classPath = path.substring(path.indexOf("classes") + 8, path.length() - 6);
        return classPath.replace(File.separator, ".");
    }

    private void documentClass(Class<?> clazz, BufferedWriter writer) throws IOException {
        documentClasses(clazz, writer);
        documentFields(clazz, writer);
        documentConstructors(clazz, writer);
        documentMethods(clazz, writer);
    }

    private static void documentClasses(Class<?> clazz, BufferedWriter writer) throws IOException {
        if (clazz.isInterface()) {
            writer.write("<h1>Interface: " + clazz.getName() + "</h1>\n");
        } else if (Modifier.isAbstract(clazz.getModifiers())) {
            writer.write("<h1>Abstract Class: " + clazz.getName() + "</h1>\n");
        } else if (clazz.isEnum()) {
            writer.write("<h1>Enum: " + clazz.getName() + "</h1>\n");
        } else {
            writer.write("<h1>Class: " + clazz.getName() + "</h1>\n");
        }
    }
    private static void documentFields(Class<?> clazz, BufferedWriter writer) throws IOException {
        Field[] fields = clazz.getDeclaredFields();
        if (fields.length > 0) {
            writer.write("<h2>Fields</h2>\n");
            writer.write("<ul>\n");
            for (Field field : fields) {
                writer.write("<li>"
                        + Modifier.toString(field.getModifiers())
                        + " " + field.getType().getSimpleName()
                        + " " + field.getName()
                        + "</li>\n");
            }
            writer.write("</ul>\n");
        }
    }
    private void documentConstructors(Class<?> clazz, BufferedWriter writer) throws IOException {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        if (constructors.length > 0) {
            writer.write("<h2>Constructors</h2>\n");
            writer.write("<ul>\n");
            for (Constructor<?> constructor : constructors) {
                writer.write("<li>"
                        + Modifier.toString(constructor.getModifiers())
                        + " " + constructor.getName()
                        + parameterStringFormat(constructor.getParameterTypes())
                        + "</li>\n");
            }
            writer.write("</ul>\n");
        }
    }
    private void documentMethods(Class<?> clazz, BufferedWriter writer) throws IOException {
        Method[] methods = clazz.getDeclaredMethods();
        if (methods.length > 0) {
            writer.write("<h2>Methods</h2>\n");
            writer.write("<ul>\n");
            for (Method method : methods) {
                writer.write("<li>"
                        + Modifier.toString(method.getModifiers())
                        + " " + method.getReturnType().getSimpleName()
                        + " " + method.getName()
                        + parameterStringFormat(method.getParameterTypes())
                        + "</li>\n");
            }
            writer.write("</ul>\n");
        }
    }

    private String parameterStringFormat(Class<?>[] parameterTypes) {
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < parameterTypes.length; i++) {
            sb.append(parameterTypes[i].getSimpleName());
            if (i < parameterTypes.length - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }
}
