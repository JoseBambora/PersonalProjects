package org.jdaextension.responses;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.FileTemplateLoader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreCompileTemplates {
    private Map<String, Template> templates = null;

    private void compileDirectory(String directoryTemplates, String directoryPartials) {
        Handlebars handlebars = new Handlebars();
        try {
            List<String> hbsFiles = getFilesWithExtension(directoryTemplates, ".hbs");
            for(String p : hbsFiles) {
                String content = new String(Files.readAllBytes(Paths.get(PreCompileTemplates.class.getClassLoader().getResource(directoryTemplates + p).toURI())));
                String inline = content.replaceAll("\\{\\{>","\\{\\{>" + directoryPartials);
                templates.put(p.replaceAll(".hbs",""), handlebars.compileInline(inline));
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
                .toList();
    }

    private PreCompileTemplates() {
        templates = new HashMap<>();
        compileDirectory(System.getenv("TEMPLATES_FOLDER"), System.getenv("PARTIALS_FOLDER"));
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