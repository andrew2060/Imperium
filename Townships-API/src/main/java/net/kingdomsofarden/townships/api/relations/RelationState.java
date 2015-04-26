package net.kingdomsofarden.townships.api.relations;

public enum RelationState {
    WAR(true, RelationState.WAR),
    WAR_DECLARED(false, RelationState.WAR),
    WAR_MUTUAL(false, RelationState.WAR),
    WAR_PENDING_PEACE(false, RelationState.PEACE),
    PEACE(true, RelationState.PEACE),
    PEACE_OFFERED(false, RelationState.PEACE),
    ALLIANCE(true, RelationState.ALLIANCE),
    ALLIANCE_OFFERED(false, RelationState.ALLIANCE),
    ALLIANCE_PENDING(false, RelationState.ALLIANCE),
    ALLIANCE_PENDING_PEACE(false, RelationState.PEACE);

    private final RelationState baseType;
    private boolean declarable;

    RelationState(boolean declare, RelationState type) {
        declarable = declare;
        baseType = type;
    }


    public boolean isDeclarable() {
        return declarable;
    }

    public RelationState getBaseType() {
        return baseType;
    }
}
