package org.ufl.hypogator.mr;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.ufl.aida.ldc.dbloader.tmpORM.withReflection.model.SQLType;
import org.ufl.aida.ldc.dbloader.tmpORM.withReflection.model.Table;
import org.ufl.aida.ldc.dbloader.tmptables.SourceTabLoader;
import org.ufl.aida.ldc.dbloader.tmptables.mentions.EntMentions;
import org.ufl.hypogator.jackb.ontology.TypeSubtype;
import org.ufl.hypogator.jackb.streamutils.data.Tuple;
import org.ufl.hypogator.jackb.ontology.JsonOntologyLoader;

import java.io.File;

@Table(sqlTable = "fact")
public class Fact extends SourceTabLoader {

    private static final JsonOntologyLoader jol = JsonOntologyLoader.getInstance();

    @JsonProperty("mention_id")
    @SQLType(type = "varchar")
    public String mid;

    @JsonProperty("fact_id")
    @SQLType(type = "varchar")
    public String id;

    @JsonProperty("type")
    @SQLType(type = "varchar")
    public String nistTypeLeft;

    @JsonProperty("subtype")
    @SQLType(type = "varchar")
    public String nistTypeRight;

    @SQLType(type = "varchar")
    public String nistType;


    @JsonProperty("fact_provenance")
    @SQLType(type = "varchar")
    public String tree_id;

    //

    @JsonProperty("arg_role")
    @SQLType(type = "varchar")
    public String partialLabel;

    @SQLType(type = "varchar")
    public String nistFullLabel;

    //

    @JsonProperty("entity_id")
    @SQLType(type = "varchar")
    public String argumentId;

    @JsonProperty("entity_type")
    @SQLType(type = "varchar")
    public String argumentNistType;

    @JsonProperty("entity_string")
    @SQLType(type = "varchar")
    public String argumentRawString;

    @JsonProperty("kb_id")
    @SQLType(type = "varchar")
    public String argumentClusterId;

    @JsonProperty("entity_string_eng")
    @JsonAlias({"entity_string_eng", "entity_string_en"})
    @SQLType(type = "varchar")
    public String argumentBadlyTranslatedString;

    @JsonProperty("entity_string_lang")
    private String ignoredLanguageField;

    @SQLType(type = "varchar")
    public String rKind;

    @SQLType(type = "varchar")
    public String rNistName;

    @SQLType(type = "varchar")
    public String resolvedName;

    @SQLType(type = "varchar")
    public String resolvedType;

    @JsonProperty("score")
    @SQLType(type = "double precision")
    public double score = 1.0;

    @JsonProperty("scoreEvent")
    @SQLType(type = "double precision")
    public double scoreEvent = 1.0;

    @JsonProperty("isNegated")
    @SQLType(type = "bool")
    public boolean isNegated = false;

    @JsonProperty("isHedged")
    @SQLType(type = "bool")
    public boolean isHedged = false;

    @JsonProperty("isEventNegated")
    @SQLType(type = "bool")
    public boolean isEventNegated = false;

    @JsonProperty("isEventHedged")
    @SQLType(type = "bool")
    public boolean isEventHedged = false;

    @SQLType(type = "bool")
    public boolean fromFuzzyMatching;

    @JsonProperty("entity_start_offset")
    int s_offset;

    @JsonProperty("entity_end_offset")
    int e_offset;

    public Fact() {
        score = 1.0;
        resolvedName = null;
    }

    public Fact refactor() {
        nistType = nistTypeLeft+"."+nistTypeRight;
        nistFullLabel = nistType+"."+partialLabel;
        TypeSubtype typeInformation = jol.resolveSingleNISTType(argumentNistType);
        rKind = typeInformation.kind;
        rNistName = typeInformation.nistName;
        return this;
    }

    public Tuple asTuple() {
        Tuple backwardCompatibility = new Tuple();
        TypeSubtype typeInformation = jol.resolveSingleNISTType(nistType);
        return backwardCompatibility
                .putStream("mid2", mid)
                .putStream("id", id)
                // kbId is not now associated to events or relationships. The clustering is only on the events
                // start date and end date are not given.
                .putStream("type", typeInformation.kind)
                .putStream("subtype", typeInformation.nistName)
                .putStream("tree_id", tree_id)

                // Part of 'arguments'
                .putStream("arg_id", argumentId)
                .putStream("slot_type", argumentNistType)
                .putStream("fieldAttribute", nistFullLabel)
                .putStream("description", argumentRawString);
    }

    public Tuple asTrimmedMention() {

        /*// In this case, we want to also retrieve the information belonging to the arguments
        TypeSubtype typeInformation = OntologyLoader.getInstance().resolveNISTType(nistType);
        Tuple tuple =  new Tuple()
                .putStream("dimension", typeInformation.kind)
                .putStream("type", typeInformation.nistName)
                .putStream("id", mid);
                // Events and relationships have no textual representations*/

        // In this case, we want to also retrieve the information belonging to the arguments
        TypeSubtype typeInformation = jol.resolveSingleNISTType(argumentNistType);
        Tuple argument =  new Tuple()
                .putStream("kb_id2", argumentClusterId) // kbId is now associated to the argument
                .putStream("dimension", typeInformation.kind)
                .putStream("type", typeInformation.nistName)
                .putStream("id", argumentId)
                .putStream("description", argumentRawString) // assigning to the natural text the intermediate reliability
                .putStream("text_string", argumentBadlyTranslatedString); // assignign to the translation the lowest string value

        return argument;
    }


    @Override
    public void load(String fields[]) {
        if (fields.length <= 8) {
            setFailed();
            return;
        }
        this.mid = fields[0];
        this.id = fields[1] == null || fields[1].isEmpty() ? null : fields[1];
        this.nistTypeLeft = fields[2];
        this.nistTypeRight = fields[3];
        this.tree_id = fields[4];
        this.partialLabel = fields[5];
        this.argumentId = fields[6];
        this.argumentNistType = fields[7];
        this.argumentRawString = fields[8];
        try {
            this.argumentClusterId = fields[9];
        } catch (ArrayIndexOutOfBoundsException e) {
            this.argumentClusterId  = "";
        }
        try {
            if (fields[10] != null && !fields[10].isEmpty())
                this.argumentBadlyTranslatedString = fields[10];
            else
                this.argumentBadlyTranslatedString = "";
        } catch (ArrayIndexOutOfBoundsException e) {
            this.argumentBadlyTranslatedString  = "";
        }
        if (fields.length >= 15) {
            this.argumentRawString = argumentRawString + "|" + fields[14];
        }
        try {
            this.isNegated = fields[15].toLowerCase().startsWith("t");
            try {
                this.isHedged = fields[16].toLowerCase().startsWith("t");
                try {
                    this.scoreEvent = Double.valueOf(fields[17]);
                } catch (Exception e) {
                    this.scoreEvent = 1.0;
                }
                try {
                    this.isEventHedged = fields[18].toLowerCase().startsWith("t");
                    try {
                        this.isEventNegated = fields[19].toLowerCase().startsWith("t");
                    } catch (Exception e) {
                        this.isEventNegated = false;
                    }
                } catch (Exception e) {
                    this.isEventHedged = false;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                this.isHedged = false;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            this.isNegated = false;
        }
        refactor();
    }

    @Override
    public File getFile(File parentFolder) {
        return parentFolder;
    }

    @Override
    public Fact generateNew() {
        return new Fact();
    }

}
