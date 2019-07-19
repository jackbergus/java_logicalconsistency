package org.ufl.hypogator.jackb.traversers.conceptnet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jooq.*;
import org.ufl.hypogator.jackb.disambiguation.dimension.concept.ConceptNetDimensionDisambiguationOperations;
import org.ufl.hypogator.jackb.m9.configuration.Concept5ClientConfigurations;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.ConceptNet5Interface;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.EdgeVertex;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.model.Tables;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.model.tables.*;
import org.ufl.hypogator.jackb.scraper.ScraperSources;
import org.jooq.impl.DSL;
import org.jooq.util.postgres.PostgresDSL;
import org.postgresql.util.PGobject;
import org.ufl.aida.ldc.dbloader.tmpORM.withReflection.dbms.Database;
import org.ufl.aida.ldc.dbloader.tmpORM.withReflection.dbms.rdbms.DBMSInterface;
import org.ufl.aida.ldc.dbloader.tmpORM.withReflection.dbms.rdbms.DBMSInterfaceFactory;

import java.io.*;
import java.util.*;
import java.util.stream.Stream;

import static org.ufl.hypogator.jackb.disambiguation.dimension.concept.ConceptNetDimensionDisambiguationOperations.unrectify;

public class ConceptNet5Postgres implements ConceptNet5Interface {

    private Database db;
    private Nodes n1, n2, np1, np2;
    private NodePrefixes p1, p2;
    private Edges e;
    private Edges e1, e2;
    private Relations r;
    private EdgeSources es;
    private Sources s;
    private final static ObjectMapper unser = Concept5ClientConfigurations.instantiate().jsonSerializer;

    private ConceptNet5Postgres() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream("conf/postgresql.properties"));

        DBMSInterface engine = DBMSInterfaceFactory.generate(properties.getProperty("engine", "PostgreSQL"));
        String dbname = "conceptnet5";
        String username = properties.getProperty("cusername", "root");
        String password = properties.getProperty("cpassword", "conceptnet5");

        Optional<Database> opt = Database.open(engine, dbname, username, password);
        if (opt.isPresent()) {
            db = opt.get();
                r = Tables.RELATIONS.as("r");
                e = Tables.EDGES.as("e");
                e1 = Tables.EDGES.as("e1");
                e2 = Tables.EDGES.as("e2");
                n1 = Tables.NODES.as("n1");
                n2 = Tables.NODES.as("n2");
                p1 = Tables.NODE_PREFIXES.as("p1");
                p2 = Tables.NODE_PREFIXES.as("p2");
                np1 = Tables.NODES.as("np1");
                np2 = Tables.NODES.as("np2");
        }
    }
    static ConceptNet5Postgres self;
    public static ConceptNet5Postgres getInstance() {
        if (self == null) {
            try {
                return ((self = new ConceptNet5Postgres()));
            } catch (IOException e) {
                return null;
            }
        } else return self;
    }

    private Integer dumpEdges(FileWriter file, int offset) throws IOException {
        SelectForUpdateStep<Record2<Integer, Object>> l = db.jooq().select(e.ID, e.DATA)
                .from(e)
                .where(PostgresDSL.not(PostgresDSL.field("e.data->>'start'").like("%/a").and(PostgresDSL.field("e.data->>'start'").like("%/v"))))
                .and(PostgresDSL.not(PostgresDSL.field("e.data->>'end'").like("%/a").and(PostgresDSL.field("e.data->>'end'").like("%/v"))))
                .and(e.WEIGHT.ge(0.10f))
                .limit(10000).offset(offset);

        Iterator<Record2<Integer, Object>> it = l.iterator();
        int count = 0;

        while (it.hasNext()) {
            Record2<Integer, Object> record = it.next();

            if (record.component2() != null) {
                file.write(((PGobject)record.component2()).getValue());
            }
            file.write("\n");
            count++;
        }
        file.flush();
        return count;
    }

    public boolean dumpEdgestoFile(File f) {
        try {
            FileWriter w = new FileWriter(f);
            Integer id = null;
            Integer offset = 0;
            do { System.out.println("dump from: "+offset); id = dumpEdges(w, offset); offset += id; } while (id>0);
            w.close();
        } catch (IOException e3) {
            e3.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Standard SQL query through which the database is accessed
     * @param sourceId
     * @param relationLabel
     * @param targetId
     * @return
     */
    private SelectSeekStep2<Record3<String, Float, Object>, Float, String> generateMainStatement(String sourceId, List<String> relationLabel, String targetId, boolean onlyEnglish) {
        SelectConditionStep<Record3<String, Float, Object>> sql =
                db.jooq().selectDistinct(e.URI, e.WEIGHT, e.DATA)
                .from(r, e, n1, n2, p1, p2, np1, np2)
                .where(e.RELATION_ID.equal(r.ID))
                .and(e.START_ID.equal(n1.ID))
                .and(e.END_ID.equal(n2.ID))
                .and(p1.PREFIX_ID.equal(np1.ID))
                .and(p1.NODE_ID.equal(n1.ID))
                .and(p2.PREFIX_ID.equal(np2.ID))
                .and(p2.NODE_ID.equal(n2.ID));

        if (onlyEnglish) {
            if (targetId == null)
                sql = sql.and(n2.URI.like("/c/en%")).and(PostgresDSL.not(n2.URI.like("%/a").and(n2.URI.like("%/v"))));
            if (sourceId == null)
                sql = sql.and(n1.URI.like("/c/en%")).and(PostgresDSL.not(n1.URI.like("%/a").and(n1.URI.like("%/v"))));
        }

        if (relationLabel != null && !relationLabel.isEmpty()) {
            Condition ll = null;
            for (String relationship : relationLabel) {
                if (ll == null)
                    ll = r.URI.equal(DSL.val(relationship));
                else
                    ll = ll.or(r.URI.equal(DSL.val(relationship)));
            }
            sql = sql.and(ll);
        }

        if (sourceId != null) {
            Condition ll = np1.URI.equal(DSL.val(sourceId));
            sql = sql.and(ll);
        }

        if (targetId != null) {
            sql = sql.and(np2.URI.equal(DSL.val(targetId)));
        }

        return sql.and(e.WEIGHT.ge(0.10f)).orderBy(e.WEIGHT.desc(), e.URI);
    }

    private Condition generateConditions(boolean like, String... nodes) {
        Condition condition = null;
        for (int i = 0, n=nodes.length; i<n; i++) {
                                   // Over like, the user will approximage
            Condition tmp = like ? n1.URI.like(nodes[i]).and(PostgresDSL.not(n1.URI.like("%/a").and(n1.URI.like("%/v")))) :
                    // I keep either a term with no noun specification, or any noun or verb or whatever
                    n1.URI.equal(nodes[i]).or(n1.URI.like(nodes[i].concat("/%")).and(PostgresDSL.not(n1.URI.like("%/a").and(n1.URI.like("%/v")))));
            if (condition == null || i == 0)
                condition = tmp;
            else
                condition = condition.or(tmp);
        }
        return condition;
    }


    private String dumpNodes(String id, FileWriter file) throws IOException {
        Condition condition = PostgresDSL.not(n1.URI.like("%/a")).and(PostgresDSL.not(n1.URI.like("%/v")));
        if (id != null)
            condition = condition.and(n1.URI.greaterThan(id));

        Iterator<Record2<String, Object[]>> it = db.jooq().select(n1.URI,
                PostgresDSL.array(
                        // Aggregating the arguments into one single elements, and also making them distinct
                        PostgresDSL
                                .selectDistinct(PostgresDSL.field("a"))
                                .from(PostgresDSL.unnest(PostgresDSL.arrayCat(PostgresDSL.arrayAgg(PostgresDSL.field("e1.data->>'surfaceStart'")), PostgresDSL.arrayAgg(PostgresDSL.field("e2.data->>'surfaceEnd'")))).as("a"))
                        .where(PostgresDSL.field("a").isNotNull())

                )).from(e1).rightJoin(n1).on(n1.ID.equal(e1.START_ID))
                .leftJoin(e2).on(n1.ID.equal(e2.END_ID))
                .where(condition)
                .groupBy(n1.URI)
                .orderBy(n1.URI.asc())
                .limit(10000)
                .iterator();

        String max = null;
        while (it.hasNext()) {
                Record2<String, Object[]> record = it.next();

                file.write(record.component1());
                if (record.component2() != null)
                for (Object o : record.component2()) {
                    if (o != null) {
                        String s = o.toString().trim();
                        if (!s.isEmpty()) {
                            file.write("\t"+s);
                        }
                    }
                }
                if (!it.hasNext())
                    max = record.component1();
                file.write("\n");
        }
        file.flush();
        return max;
    }

    public boolean dumpNodestoFile(File f) {
        try {
            FileWriter w = new FileWriter(f);
            String id = null;
            do { System.out.println("dump from: "+id); id = dumpNodes(id, w); } while (id != null);
            w.close();
        } catch (IOException e3) {
            e3.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Creates a node using the nodeId from babelnet
     * @param node
     * @return
     */
    @Override
    public EdgeVertex queryNode(boolean like, String node) {
        EdgeVertex ev = new EdgeVertex();
        node = Concept5ClientConfigurations.rectifyTerm(node);
        ev.setGeneratingSource(ScraperSources.CONCEPTNET);
        SelectOrderByStep<Record2<String, Object[]>> q = db.jooq().select(n1.URI, PostgresDSL.arrayAggDistinct(PostgresDSL.field("e.data->>'surfaceStart'")))
                .from(n1).leftJoin(e)
                .on(n1.ID.equal(e.START_ID))
                /*.where(n1.ID.equal(p1.NODE_ID))
                .and(p1.PREFIX_ID.equal(np1.ID))*/
                .where(generateConditions(like, node))
                //.and(PostgresDSL.field("e.data->>'surfaceStart'").isNotNull())
                .groupBy(n1.URI)
                .union(
                        PostgresDSL.select(n1.URI, PostgresDSL.arrayAggDistinct(PostgresDSL.field("e.data->>'surfaceEnd'")))
                                .from(n1, /*np1, p1,*/ e)
                                /*.where(n1.ID.equal(p1.NODE_ID))
                                .and(p1.PREFIX_ID.equal(np1.ID))*/
                                .where(e.END_ID.equal(n1.ID))
                                .and(generateConditions(like, node))
                                //.and(PostgresDSL.field("e.data->>'surfaceEnd'").isNotNull())
                                .groupBy(n1.URI)

                );

        q.forEach(ev::fromConceptNet);

        // Just in case that ConceptNet had no associated raw edges, we're going to express it not as a node, but via its relatiionships later on. For the moment, just return a bogus element
        if (ev.id == null) {
            ev.id = node;
            ev.term = ConceptNetDimensionDisambiguationOperations.unrectify(ev.id);
            ev.label = ev.term;
            String[] split = ev.id.split("/");
            ev.language = split[2];
            ev.sense_label = split.length == 5 ? split[4] : null;
            ev.setGeneratingSource(ScraperSources.AIDA);
        }
        return ev;
    }

    public static class RecordResultForSingleNode implements Serializable {
        public final String id;
        private EdgeVertex parent;
        public final String[] strings;
        public static final long serialVersionUID = 9061169524859839802L;

        public String[] getStrings() {
            return strings;
        }

        public EdgeVertex getParent() {
            if (parent == null) {
                this.parent = new EdgeVertex();
                this.parent.setGeneratingSource(ScraperSources.CONCEPTNET);
                this.parent.fromConceptNet(id, strings);
            }
            return parent;
        }

        public RecordResultForSingleNode(String id, String[] strings, EdgeVertex parent) {
            this.id = id;
            this.parent = parent;
            this.strings = strings == null ?
                    new String[]{unrectify(id)} :
                    Arrays.stream(strings).filter(s -> (s != null && s.length() > 0)).toArray(String[]::new);
        }

        public RecordResultForSingleNode(String id, Object[] strings) {
            this.id = id;
            List<String> array = new ArrayList<>(strings == null ? 0 : strings.length);
            if (strings != null) {
                for (int i = 0, stringsLength = strings.length; i < stringsLength; i++) {
                    String o = (String)strings[i];
                    if (o != null) {
                        o = o.trim();
                        if (o.length() > 0)
                            array.add(o);
                    }
                }
            }
            this.strings = array.isEmpty() ? new String[]{unrectify(id)} : array.toArray(new String[array.size()]);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RecordResultForSingleNode that = (RecordResultForSingleNode) o;
            return Objects.equals(id, that.id) &&
                    Arrays.equals(strings, that.strings);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(id);
            result = 31 * result + Arrays.hashCode(strings);
            return result;
        }

        @Override
        public String toString() {
            return "RecordResultForSingleNode{" +
                    "id='" + id + '\'' +
                    ", strings=" + Arrays.toString(strings) +
                    '}';
        }
    }

    @Deprecated
    public List<RecordResultForSingleNode> rawQueryNode(boolean like, String... node) {
        for (int i = 0, n = node.length; i<n; i++) {
            node[i] = Concept5ClientConfigurations.rectifyTerm(node[i]);
        }
        List<RecordResultForSingleNode> result = db.jooq().select(n1.URI, PostgresDSL.arrayAggDistinct(PostgresDSL.field("e.data->>'surfaceStart'")))
                .from(n1).leftJoin(e)
                          .on(n1.ID.equal(e.START_ID))
                .where(generateConditions(like, node))
                .groupBy(n1.URI)
                .union(
                        PostgresDSL.select(n1.URI, PostgresDSL.arrayAggDistinct(PostgresDSL.field("e.data->>'surfaceEnd'")))
                                .from(n1, /*np1, p1,*/ e)
                                .where(e.END_ID.equal(n1.ID))
                                .and(generateConditions(like, node))
                                .groupBy(n1.URI)

                ).fetchInto(RecordResultForSingleNode.class);

        // Just in case that ConceptNet had no associated raw edges, we're going to express it not as a node, but via its relatiionships later on. For the moment, just return a bogus element
        if (result.size() == 0) {
            return Collections.emptyList();
        }
        return result;
    }

    public Stream<APIRecord> execute(String sourceId, List<String> relationLabel, String targetId, boolean traverse, boolean onlyEnglish) {
        Stream<APIRecord> statement;
        if (!traverse) {
            statement = generateMainStatement(sourceId, relationLabel, targetId, onlyEnglish)
                    .fetchStream().map(ter -> new APIRecord(ter.value1(), ter.value2(), ter.value3()));
        } else {
            statement = generateMainStatement(sourceId, relationLabel, targetId, onlyEnglish)
                    .union(generateMainStatement(targetId, relationLabel, sourceId, onlyEnglish))
                    .fetchStream().map(ter -> new APIRecord(ter.value1(), ter.value2(), ter.value3()));
        }
        //System.out.println(statement);
        return statement;
    }


    public static class APIRecord  {
        private String uri;
        private Float edgeScore;
        private Object pgObject;
        private boolean isString;

        public APIRecord() { }

        public APIRecord(String uri, Float edgeScore, Object pgObject) {
            this.uri = uri;
            this.edgeScore = edgeScore;
            this.pgObject = pgObject;
            isString = false;
        }

        public APIRecord(String uri, String pgObject, Float edgeScore) {
            this.uri = uri;
            this.edgeScore = edgeScore;
            this.pgObject = pgObject;
            isString = false;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public Float getEdgeScore() {
            return edgeScore;
        }

        public void setEdgeScore(Float edgeScore) {
            this.edgeScore = edgeScore;
        }

        public String getPgObject() {
            return isString ? ((String) pgObject) : ((PGobject)pgObject).getValue();
        }

        public void setPgObject(Object pgObject) {
            this.pgObject = pgObject;
        }

        @Override
        public String toString() {
            return "APIRecord{" +
                    "uri='" + uri + '\'' +
                    ", edgeScore=" + edgeScore +
                    ", pgObject=" + pgObject +
                    '}';
        }
    }

}
