package com.ftn.sbnz.sv10.kjar.template;

import java.io.InputStream;

import org.drools.decisiontable.ExternalSpreadsheetCompiler;
import com.ftn.sbnz.sv10.kjar.KjarApplication;

public final class TemplateRuleLoader {

    private TemplateRuleLoader() {
    }

    public static String compileTemplate(String templatePath, String xlsPath) {
        try (InputStream xlsStream = KjarApplication.class.getResourceAsStream(xlsPath);
             InputStream templateStream = KjarApplication.class.getResourceAsStream(templatePath)) {

            if (xlsStream == null) {
                throw new IllegalStateException("Ne mogu da nadjem XLS: " + xlsPath);
            }
            if (templateStream == null) {
                throw new IllegalStateException("Ne mogu da nadjem template: " + templatePath);
            }

            ExternalSpreadsheetCompiler compiler = new ExternalSpreadsheetCompiler();
            return compiler.compile(xlsStream, templateStream, 2, 1);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
