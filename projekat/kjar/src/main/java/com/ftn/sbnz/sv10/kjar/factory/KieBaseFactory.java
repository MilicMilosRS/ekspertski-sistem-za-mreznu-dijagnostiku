package com.ftn.sbnz.sv10.kjar.factory;

import java.io.InputStream;

import org.kie.api.KieBase;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.io.ResourceType;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;

import com.ftn.sbnz.sv10.kjar.KjarApplication;
import com.ftn.sbnz.sv10.kjar.template.TemplateRuleLoader;

/**
 * Gradi jedinstvenu KieBase koja sadrzi:
 *  - staticka DRL pravila (diagnostics.drl, backward_chaining.drl)
 *  - template-generisana pravila (iz .drt + .xls)
 * Sve u jednoj bazi znanja -> jedna sesija pokrece sve.
 */
public final class KieBaseFactory {

    private static final String[] DRL_FILES = {
            "/rules/diagnostics.drl",
            "/rules/backward_chaining.drl"
    };

    private KieBaseFactory() {
    }

    public static KieBase create() {
        KieHelper helper = new KieHelper();

        // 1. staticka DRL pravila
        for (String path : DRL_FILES) {
            InputStream stream = KjarApplication.class.getResourceAsStream(path);
            if (stream == null) {
                throw new IllegalStateException("Ne mogu da nadjem: " + path);
            }
            helper.addResource(ResourceFactory.newInputStreamResource(stream), ResourceType.DRL);
        }

        // 2. template-generisana pravila (.drt + .xls -> DRL)
        String templateDrl = TemplateRuleLoader.compileTemplate(
                "/rules/alert_thresholds.drt",
                "/rules/alert_thresholds.xls");
        helper.addContent(templateDrl, ResourceType.DRL);

        // 3. verifikuj
        Results results = helper.verify();
        if (results.hasMessages(Message.Level.ERROR)) {
            throw new IllegalStateException("DRL build failed:\n" + results.getMessages(Message.Level.ERROR));
        }

        return helper.build();
    }
}
