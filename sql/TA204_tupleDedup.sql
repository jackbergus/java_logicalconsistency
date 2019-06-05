select "resolvedType" as type, array_agg(arrays) as arrays
from (
        select "resolvedType", json_build_object('nistType',"nistType", 'rn',array_agg(distinct "resolvedName")) as arrays
        from fact
        where not ("partialLabel" like '%Time') and not ("resolvedType" like '%Time') and not ("rNistName" like '%Time') and
              not ("partialLabel" like '%Location') and not ("resolvedType" like '%Location') and not ("rNistName" like '%Location') and
              not ("partialLabel" like '%Place') and not ("resolvedType" like '%Place') and not ("rNistName" like '%Place') and
              not ("partialLabel" like '%Origin') and not ("resolvedType" like '%Origin') and not ("rNistName" like '%Origin') and
              not ("partialLabel" like '%Destination') and not ("resolvedType" like '%Destination') and not ("rNistName" like '%Destination')
        group by "nistType", "resolvedType"
     ) as Tauple
group by "resolvedType"
