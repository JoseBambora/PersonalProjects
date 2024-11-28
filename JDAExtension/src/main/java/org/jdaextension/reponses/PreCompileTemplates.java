package org.jdaextension.reponses;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreCompileTemplates {
    private Map<String, Template> templates = null;

    private void compileDirectory(String directory, Handlebars handlebars) {
        try {
            List<String> hbsFiles = getFilesWithExtension(directory, ".hbs");
            for(String p : hbsFiles) {
                templates.put(p, handlebars.compile(directory + p));
            }
        } catch (IOException e) {
            System.err.println("Error reading file: \n" + e);
        } catch (URISyntaxException e) {
            System.err.println("URI error: \n" + e);
        }
    }

    private List<String> getFilesWithExtension(String folder, String extension) throws IOException, URISyntaxException {
        Path resourcePath = Paths.get(ClassLoader.getSystemResource(folder).toURI());
        return Files.walk(resourcePath)
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(extension))
                .map(path -> resourcePath.relativize(path).toString())
                .map(name -> name.replaceAll(".hbs",""))
                .toList();
    }

    private PreCompileTemplates() {
        templates = new HashMap<>();
        Handlebars handlebars = new Handlebars();
        compileDirectory(System.getenv("VIEWS_FOLDER") + "/",handlebars);
    }

    private String getResult(String template, Map<String, Object> variables) {
        try {
            return templates.get(template).apply(variables);
        } catch (IOException e) {
            System.err.println("Error variables: \n" + e + "\n\nReturning Empty String");
            return "";
        }
    }

    private static PreCompileTemplates instance = null;

    public static String apply(String template, Map<String, Object> variables) {
        if(instance == null)
            instance = new PreCompileTemplates();
        return instance.getResult(template,variables);
    }
}
