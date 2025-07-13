package com.eazybytes.accounts.customer;

import com.eazybytes.accounts.config.AccessConfig;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    private final CustomerRepository repository;
    private final AccessConfig config;
    private final ExpressionParser parser = new SpelExpressionParser();

    public CustomerService(CustomerRepository repository, AccessConfig config) {
        this.repository = repository;
        this.config = config;
    }

    public List<Customer> getAllCustomers() {
        return repository.findAll();
    }

    public Customer addCustomer(Customer customer) {
        return repository.save(customer);
    }

    public Optional<String> getSpecialAccessAction(String persona, String functionName, String targetPersona) {
        Optional<AccessConfig.PersonaRuleWrapper> personaRuleOpt = config.getPersonaAccess().stream()
                .filter(p -> p.getPersona().equals(persona))
                .findFirst();

        if (personaRuleOpt.isEmpty()) return Optional.empty();

        AccessConfig.PersonaRuleWrapper personaRule = personaRuleOpt.get();
        AccessConfig.Rule rule = personaRule.getSpecialRule() != null ? personaRule.getSpecialRule() : personaRule.getExistingRule();

        if (!functionName.equalsIgnoreCase(rule.getFunctionName())) {
            return Optional.empty();
        }

        // If there is no special condition
        List<String> conditions = rule.getConditions();
        if (conditions == null || conditions.isEmpty()) {
            return Optional.ofNullable(rule.getAction());
        }

        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("targetPersona", targetPersona);

        for (String cond : conditions) {
            Expression expr = parser.parseExpression(cond);
            Boolean result = expr.getValue(context, Boolean.class);
            if (Boolean.TRUE.equals(result)) {
                return Optional.ofNullable(rule.getAction());
            }
        }

        return Optional.empty();
    }
}
