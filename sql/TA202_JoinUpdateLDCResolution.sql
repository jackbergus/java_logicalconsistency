UPDATE fact

SET "resolvedName" = mentions_for_update."resolved_string",
    "resolvedType" = mentions_for_update."resolved_type",
    score = mentions_for_update.resolved_score

FROM mentions_for_update
WHERE fact."argumentId" = mentions_for_update."amid"
 AND  fact."argumentNistType" = mentions_for_update."atype"
 AND  fact."argumentClusterId" = mentions_for_update."acid"