CREATE TABLE tuples as(

SELECT "nistType", mid, "scoreEvent" as score, "isEventNegated" as negated, "isEventHedged" as hedged, ARRAY[mid] as constituent, array_agg(json_build_object('fieldName', "partialLabel",
                                                    'fieldType',  COALESCE("resolvedType","rNistName"),
                                                    'fieldString', COALESCE("resolvedName","argumentRawString"),
                                                    'typeFromFuzzyMatch', "fromFuzzyMatching",
                                                    'mid', "argumentId",
						    'isNegated', "isNegated",
						    'isHedged', "isHedged",
						    'score', "score"))
                                                FROM      fact
                                                GROUP BY  mid, "nistType", "scoreEvent", "isEventNegated", "isEventHedged")
