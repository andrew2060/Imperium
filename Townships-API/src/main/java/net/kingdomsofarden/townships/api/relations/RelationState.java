package net.kingdomsofarden.townships.api.relations;

public enum RelationState {
    PEACE(true),
    PEACE_OFFERED(false, RelationState.PEACE),
    WAR(true),
    WAR_DECLARED(false, RelationState.WAR),
    WAR_MUTUAL(false, RelationState.WAR),
    WAR_PENDING_PEACE(false, RelationState.PEACE),
    ALLIANCE(true),
    ALLIANCE_OFFERED(false, RelationState.ALLIANCE),
    ALLIANCE_PENDING(false, RelationState.ALLIANCE),
    ALLIANCE_PENDING_PEACE(false, RelationState.PEACE),
    SELF(false, RelationState.ALLIANCE);

    private final RelationState baseType;
    private boolean declarable;

    RelationState(boolean declare, RelationState type) {
        declarable = declare;
        baseType = type;
    }

    RelationState(boolean declare) {
        declarable = declare;
        baseType = this;
    }


    public boolean isDeclarable() {
        return declarable;
    }

    public RelationState getBaseType() {
        return baseType;
    }
}
