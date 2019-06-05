CREATE TABLE mentionwithslotsb AS
  (SELECT
      -- Basic elements identifying the current element
         mention.tree_id, mention.id, mention."mentionId", mention.type, mention.subtype,

      -- collapsing all the attributes together in one single array
         ARRAY(SELECT DISTINCT UNNEST(string_to_array(concat_ws(',',
                                                                CASE WHEN (mention.attribute = '') IS NOT FALSE
                                                                          THEN NULL
                                                                     ELSE mention.attribute
                                                                    END,
                                                                CASE WHEN (mention.attribute2 = '') IS NOT FALSE
                                                                          THEN NULL
                                                                     ELSE mention.attribute2
                                                                    END), ',')::text[])) AS attributes,

      -- textual informations collapsed in here
         jsonb_build_object('provenance', mention.provenance,
                            'startchar', mention.textoffset_startchar,
                            'endchar', mention.textoffset_endchar) AS txt_entrypoint,

      -- slot information
         '[]'::json AS slots,

      -- start temporal information
         CASE
           WHEN mention.start_date IS NULL
                   THEN NULL
           ELSE jsonb_build_object('type', mention.start_date_type,
                                   'val', mention.start_date) END AS start_date,

      -- end temporal information
         CASE
           WHEN mention.end_date IS NULL
                   THEN NULL
           ELSE jsonb_build_object('type', mention.end_date_type,
                                   'val', mention.end_date) END AS end_date,

      -- remaining fields from mention
         mention.text_string,
         mention.justification,
         "miniKB".topic_id,
         "miniKB".category,
         "miniKB".handle,
         "miniKB".description

  FROM "miniKB" NATURAL RIGHT JOIN mention
  WHERE mention."mentionId" not in (select mentionwithslots."mentionId" from mentionwithslots)
  GROUP BY mention.tree_id, mention.id, mention."mentionId", mention.type, mention.subtype, mention.attribute,
           mention.attribute2, mention.provenance, mention.textoffset_startchar, mention.textoffset_endchar,
           mention.start_date_type, mention.start_date, mention.end_date_type, mention.end_date,
           mention.text_string, mention.justification, "miniKB".topic_id, "miniKB".category, "miniKB".handle,
           "miniKB".description
  )
;