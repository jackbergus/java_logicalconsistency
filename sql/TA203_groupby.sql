SELECT "nistType",
       array_agg(json_build_object('tupleId', mid,
                                   'arguments',array_agg
                                  )
                 ) as tuples
FROM      tuples
GROUP BY "nistType"

