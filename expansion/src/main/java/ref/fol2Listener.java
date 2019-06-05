// Generated from ref/fol2.g4 by ANTLR 4.7.1
package ref;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link fol2Parser}.
 */
public interface fol2Listener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link fol2Parser#expansion}.
	 * @param ctx the parse tree
	 */
	void enterExpansion(fol2Parser.ExpansionContext ctx);
	/**
	 * Exit a parse tree produced by {@link fol2Parser#expansion}.
	 * @param ctx the parse tree
	 */
	void exitExpansion(fol2Parser.ExpansionContext ctx);
	/**
	 * Enter a parse tree produced by {@link fol2Parser#commands}.
	 * @param ctx the parse tree
	 */
	void enterCommands(fol2Parser.CommandsContext ctx);
	/**
	 * Exit a parse tree produced by {@link fol2Parser#commands}.
	 * @param ctx the parse tree
	 */
	void exitCommands(fol2Parser.CommandsContext ctx);
	/**
	 * Enter a parse tree produced by {@link fol2Parser#fol_two}.
	 * @param ctx the parse tree
	 */
	void enterFol_two(fol2Parser.Fol_twoContext ctx);
	/**
	 * Exit a parse tree produced by {@link fol2Parser#fol_two}.
	 * @param ctx the parse tree
	 */
	void exitFol_two(fol2Parser.Fol_twoContext ctx);
	/**
	 * Enter a parse tree produced by {@link fol2Parser#stringlist}.
	 * @param ctx the parse tree
	 */
	void enterStringlist(fol2Parser.StringlistContext ctx);
	/**
	 * Exit a parse tree produced by {@link fol2Parser#stringlist}.
	 * @param ctx the parse tree
	 */
	void exitStringlist(fol2Parser.StringlistContext ctx);
	/**
	 * Enter a parse tree produced by {@link fol2Parser#expansion_opts}.
	 * @param ctx the parse tree
	 */
	void enterExpansion_opts(fol2Parser.Expansion_optsContext ctx);
	/**
	 * Exit a parse tree produced by {@link fol2Parser#expansion_opts}.
	 * @param ctx the parse tree
	 */
	void exitExpansion_opts(fol2Parser.Expansion_optsContext ctx);
}