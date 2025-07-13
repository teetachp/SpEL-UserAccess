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

    /**
     * Evaluates access control rules for a given persona, function, and targetPersona.
     *
     * This method loads rules defined via configuration, parses conditions written in
     * Spring Expression Language (SpEL), and evaluates them using the provided context.
     *
     * Workflow:
     * 1. Finds the rule for the input persona.
     * 2. Chooses specialRule if available; otherwise, falls back to existingRule.
     * 3. Compares the given function name to the rule's function.
     * 4. If there are SpEL conditions, evaluates them one by one using the context.
     * 5. If any condition is true, returns the associated action.
     *
     * @param persona        The identity attempting to access
     * @param functionName   The name of the function (e.g., USER_MANAGEMENT)
     * @param targetPersona  The target of the action being accessed
     * @return Optional containing the action (e.g., READ_WRITE) if access is granted
     */
    public Optional<String> getAccessAction(String persona, String functionName, String targetPersona) {
        Optional<AccessConfig.PersonaRuleWrapper> personaRuleOpt = config.getPersonaAccess().stream()
                .filter(p -> p.getPersona().equals(persona))
                .findFirst();

        if (personaRuleOpt.isEmpty()) return Optional.empty();

        AccessConfig.PersonaRuleWrapper personaRule = personaRuleOpt.get();
        List<AccessConfig.Rule> rules = personaRule.getSpecialRule() != null ? personaRule.getSpecialRule() : personaRule.getExistingRule();

        for (AccessConfig.Rule rule : rules) {
            if (!functionName.equalsIgnoreCase(rule.getFunctionName())) continue;

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
        }

        return Optional.empty();
    }

}
