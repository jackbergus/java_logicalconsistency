package ref;

import com.google.common.collect.HashMultimap;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class FOL2Listener  {

    /**
     * Unescapes a string that contains standard Java escape sequences.
     *
     * @param st
     *            A string optionally containing standard java escape sequences.
     * @return The translated string.
     */
    public static String unescapeJavaString(String st) {

        StringBuilder sb = new StringBuilder(st.length());

        for (int i = 0; i < st.length(); i++) {
            char ch = st.charAt(i);
            if (ch == '\\') {
                char nextChar = (i == st.length() - 1) ? '\\' : st
                        .charAt(i + 1);
                // Octal escape?
                if (nextChar >= '0' && nextChar <= '7') {
                    String code = "" + nextChar;
                    i++;
                    if ((i < st.length() - 1) && st.charAt(i + 1) >= '0'
                            && st.charAt(i + 1) <= '7') {
                        code += st.charAt(i + 1);
                        i++;
                        if ((i < st.length() - 1) && st.charAt(i + 1) >= '0'
                                && st.charAt(i + 1) <= '7') {
                            code += st.charAt(i + 1);
                            i++;
                        }
                    }
                    sb.append((char) Integer.parseInt(code, 8));
                    continue;
                }
                switch (nextChar) {
                    case '\\':
                        ch = '\\';
                        break;
                    case 'b':
                        ch = '\b';
                        break;
                    case 'f':
                        ch = '\f';
                        break;
                    case 'n':
                        ch = '\n';
                        break;
                    case 'r':
                        ch = '\r';
                        break;
                    case 't':
                        ch = '\t';
                        break;
                    case '\"':
                        ch = '\"';
                        break;
                    case '\'':
                        ch = '\'';
                        break;
                    // Hex Unicode: u????
                    case 'u':
                        if (i >= st.length() - 5) {
                            ch = 'u';
                            break;
                        }
                        int code = Integer.parseInt(
                                "" + st.charAt(i + 2) + st.charAt(i + 3)
                                        + st.charAt(i + 4) + st.charAt(i + 5), 16);
                        sb.append(Character.toChars(code));
                        i += 5;
                        continue;
                }
                i++;
            }
            sb.append(ch);
        }

        String tmp = sb.toString();
        if (tmp.charAt(0) == '"' && tmp.charAt(tmp.length()-1) == '"') {
            tmp = tmp.substring(1, tmp.length()-1);
        }
        return tmp;
    }

    private static <X> List<List<X>> inject(Collection<X> ls) {
        List<List<X>> toret = new ArrayList<>(ls.size());
        ls.forEach(x -> toret.add(new ArrayList<X>(){{add(x);}}));
        return toret;
    }

    private static <X> List<List<X>> cross(List<List<X>> rec, Collection<X> ls) {
        for (X x : ls) {
            for (int i = 0, n = rec.size(); i<n; i++) {
                rec.get(i).add(x);
            }
        }
        return rec;
    }

    private static List<Map<String, String>> mapping(HashMultimap<String, String> map) {
        LinkedHashSet<String> keys = new LinkedHashSet<>(map.keySet());
        Iterator<String> iterator = keys.iterator();
        if (!iterator.hasNext()) {
            return new ArrayList<>();
        } else {
            String k = iterator.next();
            List<List<String>> rec = inject(map.get(k));
            for (; iterator.hasNext(); ) {
                rec = cross(rec, map.get(iterator.next()));
            }
            List<Map<String, String>> toRet  = new ArrayList<>();
            for (List<String> ls : rec) {
                int i = 0;
                Map<String, String> m = new HashMap<>();
                for (String x : keys) {
                    m.put(x, ls.get(i++));
                }
                toRet.add(m);
            }
            return toRet;
        }
    }

    public static void doexpansion(Map<String, String> aboveMapping, StringBuilder sb, fol2Parser.CommandsContext ctx) {
        HashMultimap<String, String> substitution_map = HashMultimap.create();
        for (fol2Parser.Fol_twoContext x : ctx.fol_two()) {
            String variable = x.variable.getText();
            for (TerminalNode y : x.stringlist().VALUE()) {
                substitution_map.put(variable, unescapeJavaString(y.getText()));
            }
        }
        for (Map<String, String> map: mapping(substitution_map)) {
            map.putAll(aboveMapping);
            for (fol2Parser.Expansion_optsContext child : ctx.expansion_opts()) {
                compose(sb, map, child);
            }
        }
    }

    public static void compose(StringBuilder sb, Map<String,String> composition, fol2Parser.Expansion_optsContext ctx) {
        if (ctx.VAR() != null) {
            // the variable corresponds to the actual value
            sb.append(composition.get(unescapeJavaString(ctx.VALUE().getText())));
        } else if (ctx.commands() == null ) {
            // there is just some text
            sb.append(unescapeJavaString(ctx.VALUE().getText()));
        } else {
            doexpansion(composition, sb, ctx.commands());
        }
    }

    public static void main(String args[]) throws IOException {
        System.out.println(Files.readAllLines(new File("schema_expansion.txt").toPath()));
        final org.antlr.v4.runtime.CharStream input = new ANTLRInputStream(new FileReader("schema_expansion.txt"));
        fol2Lexer lexer = new fol2Lexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        fol2Parser parser = new fol2Parser(tokens);
        fol2Parser.ExpansionContext tree = parser.expansion(); // parse a compilationUnit
        StringBuilder sb = new StringBuilder();
        Map<String, String> m = (Map<String, String>)Collections.EMPTY_MAP;
        for (fol2Parser.CommandsContext ctx : tree.commands()) {
            doexpansion(m, sb, ctx);
        }
        System.out.println(sb.toString());
    }

}
