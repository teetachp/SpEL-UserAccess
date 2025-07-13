package com.eazybytes.accounts.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "access-config")
public class AccessConfig {
    private List<PersonaRuleWrapper> personaAccess;

    public List<PersonaRuleWrapper> getPersonaAccess() {
        return personaAccess;
    }
    public void setPersonaAccess(List<PersonaRuleWrapper> personaAccess) {
        this.personaAccess = personaAccess;
    }

    public static class PersonaRuleWrapper {
        private String persona;
        private List<Rule> existingRule;
        private List<Rule> specialRule;

        public String getPersona() { return persona; }
        public void setPersona(String persona) { this.persona = persona; }

        public List<Rule> getExistingRule() { return existingRule; }
        public void setExistingRule(List<Rule> existingRule) { this.existingRule = existingRule; }

        public List<Rule> getSpecialRule() { return specialRule; }
        public void setSpecialRule(List<Rule> specialRule) { this.specialRule = specialRule; }
    }

    public static class Rule {
        private String functionName;
        private String scope;
        private String action;
        private List<String> conditions;

        public String getFunctionName() { return functionName; }
        public void setFunctionName(String functionName) { this.functionName = functionName; }

        public String getScope() { return scope; }
        public void setScope(String scope) { this.scope = scope; }

        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }

        public List<String> getConditions() { return conditions; }
        public void setConditions(List<String> conditions) { this.conditions = conditions; }
    }
}