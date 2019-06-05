CREATE MATERIALIZED VIEW tuples2 as 
SELECT mid, "nistType", "scoreEvent", negated, hedged, constituent, json_object_agg("key","val")
from (SELECT mid, "nistType", "scoreEvent", negated, hedged, constituent,
       "fieldName" as key,
       array_agg(json_build_object('fieldName', "fieldName",
                                                    'fieldType',  "fieldType",
                                                    'fieldString', "fieldString",
                                                    'typeFromFuzzyMatch', "typeFromFuzzyMatch",
                                                    'mid', "argumentId",
						    'isNegated', "isNegated",
						    'isHedged', "isHedged")) as val
FROM (SELECT distinct mid, "nistType",
                   "scoreEvent",
		   "isEventNegated" as negated,
                   "isEventHedged" as hedged,
		   ARRAY[mid] as constituent,
                   "partialLabel" as "fieldName", 
                   COALESCE("resolvedType","rNistName") as "fieldType",
		   COALESCE("resolvedName","argumentRawString") as "fieldString",
		   "fromFuzzyMatching" as "typeFromFuzzyMatch",
		   "argumentId" as "argumentId",
		   "isNegated", "isHedged", score

from fact) drop_colaesce
GROUP BY  mid, "nistType", "scoreEvent", negated, hedged, "fieldName", constituent) aggr_keys
GROUP BY  mid, "nistType", "scoreEvent", negated, hedged, constituent;
