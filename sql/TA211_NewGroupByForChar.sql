CREATE TABLE mentions_for_update as (

  select "argumentId" as amid,  --argumentClusterId
        "argumentNistType" as atype,
        "argumentClusterId" as acid,
        array_agg(distinct "argumentRawString") as strings,
        array_agg("argumentBadlyTranslatedString") as enStrings,
        NULL as resolved_string,
        0.0 as resolved_score,
        false as fromFuzzyMatching,
        NULL as resolved_type
  from fact
  where NOT (("argumentRawString" = '') IS NOT FALSE) -- removing the arguments that have empty strings associated to that: TODO, this implies that variables should have ids
  group by "argumentId", "argumentNistType", "argumentClusterId"

)
