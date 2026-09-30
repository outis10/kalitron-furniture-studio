package com.kalitron.studio.service.dto.croquis;

import java.util.EnumSet;
import java.util.Set;

/** Document a validation rule applies to (E12 #113). */
public enum RuleSet {
    /** Site survey consistency (E12). Runs whenever a measurement exists. */
    MEASUREMENT(EnumSet.of(RuleScope.WALL, RuleScope.ELEMENT, RuleScope.SITE, RuleScope.MEASUREMENT)),
    /** Distribution vs. site (E13). Runs only when a distribution exists. */
    DISTRIBUTION(EnumSet.of(RuleScope.RUN, RuleScope.ITEM, RuleScope.DISTRIBUTION));

    private final Set<RuleScope> allowedScopes;

    RuleSet(Set<RuleScope> allowedScopes) {
        this.allowedScopes = allowedScopes;
    }

    public boolean allows(RuleScope scope) {
        return allowedScopes.contains(scope);
    }
}
