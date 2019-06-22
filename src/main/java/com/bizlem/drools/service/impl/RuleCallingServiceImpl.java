package com.bizlem.drools.service.impl;

import com.bizlem.drools.service.RuleCallingService;
import com.bizlem.drools.util.Helper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.gson.Gson;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static com.bizlem.drools.util.Helper.isLongTimeStampFormat;

@Service
public class RuleCallingServiceImpl implements RuleCallingService {
	private static Logger log = LoggerFactory.getLogger(RuleCallingServiceImpl.class);
	//final String drlPath = System.getProperty("com.bizlem.appDir") + File.separator;
	 @Value("${drools.drlFilePath}")
	    private String drlPath;
    final ObjectMapper objectMapper = new ObjectMapper().registerModule(new GuavaModule());
    final Gson gson = new Gson();

    @Override
    public Map<String, String> callRules(String drlName, Map<String, String> variablePOJO) throws IOException {
        log.info("Path of the folder that contains rules file: {}", drlPath);
        Map multimap = preProcessingData(variablePOJO);

        KieServices kieServices = KieServices.Factory.get();
        File file = new File(drlPath.concat(drlName));
        Resource resource = kieServices.getResources().newFileSystemResource(file).setResourceType(ResourceType.DRL);
        KieFileSystem kFileSystem = kieServices.newKieFileSystem().write(resource);
        kieServices.newKieBuilder(kFileSystem).buildAll();

        KieContainer kc = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());

        KieSession kSession = kc.newKieSession();
        kSession.insert(multimap);
        kSession.fireAllRules();

        log.info("fired all rules");
        kSession.dispose();
        return postHandle(multimap);
    }

    private Map<String, String> postHandle(Map<String, String> multimap) {
        multimap.entrySet().forEach(param -> {

            if (isLongTimeStampFormat(param.getValue())) {
                param.setValue(Helper.localDateFromLong(param.getValue()));
            }
        });
        return multimap;
    }

    private Map<String, String> preProcessingData(Map<String, String> variablePOJO) throws JsonProcessingException {

        log.info("Value of variablePOJO {}", gson.toJson(variablePOJO));
        Set<Map.Entry<String, String>> entrySet = variablePOJO.entrySet();

        entrySet.forEach(entry -> {
            if (entry.getValue() != null) {
                if (entry.getValue().endsWith("%")) {
                    entry.setValue(org.apache.commons.lang3.StringUtils.chop(entry.getValue()));
                }
                if (Helper.isDateFormat(entry.getValue())) {
                    entry.setValue(String.valueOf(Helper.longFromLocalDate(entry.getValue())));
                }
            }
        });

        log.info(" [After] Value of variablePOJO {}", gson.toJson(variablePOJO));
        return variablePOJO;
    }

}
