// Generated from ref/schema.g4 by ANTLR 4.7.1
package ref;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link schemaParser}.
 */
public interface schemaListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link schemaParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(schemaParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link schemaParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(schemaParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by the {@code rel_delcare}
	 * labeled alternative in {@link schemaParser#commands}.
	 * @param ctx the parse tree
	 */
	void enterRel_delcare(schemaParser.Rel_delcareContext ctx);
	/**
	 * Exit a parse tree produced by the {@code rel_delcare}
	 * labeled alternative in {@link schemaParser#commands}.
	 * @param ctx the parse tree
	 */
	void exitRel_delcare(schemaParser.Rel_delcareContext ctx);
	/**
	 * Enter a parse tree produced by the {@code fdep_declare}
	 * labeled alternative in {@link schemaParser#commands}.
	 * @param ctx the parse tree
	 */
	void enterFdep_declare(schemaParser.Fdep_declareContext ctx);
	/**
	 * Exit a parse tree produced by the {@code fdep_declare}
	 * labeled alternative in {@link schemaParser#commands}.
	 * @param ctx the parse tree
	 */
	void exitFdep_declare(schemaParser.Fdep_declareContext ctx);
	/**
	 * Enter a parse tree produced by the {@code rule}
	 * labeled alternative in {@link schemaParser#commands}.
	 * @param ctx the parse tree
	 */
	void enterRule(schemaParser.RuleContext ctx);
	/**
	 * Exit a parse tree produced by the {@code rule}
	 * labeled alternative in {@link schemaParser#commands}.
	 * @param ctx the parse tree
	 */
	void exitRule(schemaParser.RuleContext ctx);
	/**
	 * Enter a parse tree produced by the {@code enexists}
	 * labeled alternative in {@link schemaParser#commands}.
	 * @param ctx the parse tree
	 */
	void enterEnexists(schemaParser.EnexistsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code enexists}
	 * labeled alternative in {@link schemaParser#commands}.
	 * @param ctx the parse tree
	 */
	void exitEnexists(schemaParser.EnexistsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code macro_definition}
	 * labeled alternative in {@link schemaParser#commands}.
	 * @param ctx the parse tree
	 */
	void enterMacro_definition(schemaParser.Macro_definitionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code macro_definition}
	 * labeled alternative in {@link schemaParser#commands}.
	 * @param ctx the parse tree
	 */
	void exitMacro_definition(schemaParser.Macro_definitionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code macro_expand}
	 * labeled alternative in {@link schemaParser#commands}.
	 * @param ctx the parse tree
	 */
	void enterMacro_expand(schemaParser.Macro_expandContext ctx);
	/**
	 * Exit a parse tree produced by the {@code macro_expand}
	 * labeled alternative in {@link schemaParser#commands}.
	 * @param ctx the parse tree
	 */
	void exitMacro_expand(schemaParser.Macro_expandContext ctx);
	/**
	 * Enter a parse tree produced by the {@code beginend_declare}
	 * labeled alternative in {@link schemaParser#commands}.
	 * @param ctx the parse tree
	 */
	void enterBeginend_declare(schemaParser.Beginend_declareContext ctx);
	/**
	 * Exit a parse tree produced by the {@code beginend_declare}
	 * labeled alternative in {@link schemaParser#commands}.
	 * @param ctx the parse tree
	 */
	void exitBeginend_declare(schemaParser.Beginend_declareContext ctx);
	/**
	 * Enter a parse tree produced by the {@code transfer_macro}
	 * labeled alternative in {@link schemaParser#commands}.
	 * @param ctx the parse tree
	 */
	void enterTransfer_macro(schemaParser.Transfer_macroContext ctx);
	/**
	 * Exit a parse tree produced by the {@code transfer_macro}
	 * labeled alternative in {@link schemaParser#commands}.
	 * @param ctx the parse tree
	 */
	void exitTransfer_macro(schemaParser.Transfer_macroContext ctx);
	/**
	 * Enter a parse tree produced by {@link schemaParser#except}.
	 * @param ctx the parse tree
	 */
	void enterExcept(schemaParser.ExceptContext ctx);
	/**
	 * Exit a parse tree produced by {@link schemaParser#except}.
	 * @param ctx the parse tree
	 */
	void exitExcept(schemaParser.ExceptContext ctx);
	/**
	 * Enter a parse tree produced by {@link schemaParser#intime}.
	 * @param ctx the parse tree
	 */
	void enterIntime(schemaParser.IntimeContext ctx);
	/**
	 * Exit a parse tree produced by {@link schemaParser#intime}.
	 * @param ctx the parse tree
	 */
	void exitIntime(schemaParser.IntimeContext ctx);
	/**
	 * Enter a parse tree produced by {@link schemaParser#inspace}.
	 * @param ctx the parse tree
	 */
	void enterInspace(schemaParser.InspaceContext ctx);
	/**
	 * Exit a parse tree produced by {@link schemaParser#inspace}.
	 * @param ctx the parse tree
	 */
	void exitInspace(schemaParser.InspaceContext ctx);
	/**
	 * Enter a parse tree produced by the {@code set_relation}
	 * labeled alternative in {@link schemaParser#setOptions}.
	 * @param ctx the parse tree
	 */
	void enterSet_relation(schemaParser.Set_relationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code set_relation}
	 * labeled alternative in {@link schemaParser#setOptions}.
	 * @param ctx the parse tree
	 */
	void exitSet_relation(schemaParser.Set_relationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code set_event}
	 * labeled alternative in {@link schemaParser#setOptions}.
	 * @param ctx the parse tree
	 */
	void enterSet_event(schemaParser.Set_eventContext ctx);
	/**
	 * Exit a parse tree produced by the {@code set_event}
	 * labeled alternative in {@link schemaParser#setOptions}.
	 * @param ctx the parse tree
	 */
	void exitSet_event(schemaParser.Set_eventContext ctx);
	/**
	 * Enter a parse tree produced by the {@code set_entity}
	 * labeled alternative in {@link schemaParser#setOptions}.
	 * @param ctx the parse tree
	 */
	void enterSet_entity(schemaParser.Set_entityContext ctx);
	/**
	 * Exit a parse tree produced by the {@code set_entity}
	 * labeled alternative in {@link schemaParser#setOptions}.
	 * @param ctx the parse tree
	 */
	void exitSet_entity(schemaParser.Set_entityContext ctx);
	/**
	 * Enter a parse tree produced by {@link schemaParser#paramDeclared}.
	 * @param ctx the parse tree
	 */
	void enterParamDeclared(schemaParser.ParamDeclaredContext ctx);
	/**
	 * Exit a parse tree produced by {@link schemaParser#paramDeclared}.
	 * @param ctx the parse tree
	 */
	void exitParamDeclared(schemaParser.ParamDeclaredContext ctx);
	/**
	 * Enter a parse tree produced by {@link schemaParser#stringlist}.
	 * @param ctx the parse tree
	 */
	void enterStringlist(schemaParser.StringlistContext ctx);
	/**
	 * Exit a parse tree produced by {@link schemaParser#stringlist}.
	 * @param ctx the parse tree
	 */
	void exitStringlist(schemaParser.StringlistContext ctx);
	/**
	 * Enter a parse tree produced by {@link schemaParser#moreClauses}.
	 * @param ctx the parse tree
	 */
	void enterMoreClauses(schemaParser.MoreClausesContext ctx);
	/**
	 * Exit a parse tree produced by {@link schemaParser#moreClauses}.
	 * @param ctx the parse tree
	 */
	void exitMoreClauses(schemaParser.MoreClausesContext ctx);
	/**
	 * Enter a parse tree produced by {@link schemaParser#clause}.
	 * @param ctx the parse tree
	 */
	void enterClause(schemaParser.ClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link schemaParser#clause}.
	 * @param ctx the parse tree
	 */
	void exitClause(schemaParser.ClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link schemaParser#forall}.
	 * @param ctx the parse tree
	 */
	void enterForall(schemaParser.ForallContext ctx);
	/**
	 * Exit a parse tree produced by {@link schemaParser#forall}.
	 * @param ctx the parse tree
	 */
	void exitForall(schemaParser.ForallContext ctx);
	/**
	 * Enter a parse tree produced by {@link schemaParser#exists}.
	 * @param ctx the parse tree
	 */
	void enterExists(schemaParser.ExistsContext ctx);
	/**
	 * Exit a parse tree produced by {@link schemaParser#exists}.
	 * @param ctx the parse tree
	 */
	void exitExists(schemaParser.ExistsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code var_notnull}
	 * labeled alternative in {@link schemaParser#predicates}.
	 * @param ctx the parse tree
	 */
	void enterVar_notnull(schemaParser.Var_notnullContext ctx);
	/**
	 * Exit a parse tree produced by the {@code var_notnull}
	 * labeled alternative in {@link schemaParser#predicates}.
	 * @param ctx the parse tree
	 */
	void exitVar_notnull(schemaParser.Var_notnullContext ctx);
	/**
	 * Enter a parse tree produced by the {@code quantification}
	 * labeled alternative in {@link schemaParser#predicates}.
	 * @param ctx the parse tree
	 */
	void enterQuantification(schemaParser.QuantificationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code quantification}
	 * labeled alternative in {@link schemaParser#predicates}.
	 * @param ctx the parse tree
	 */
	void exitQuantification(schemaParser.QuantificationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code isstring}
	 * labeled alternative in {@link schemaParser#angestrengend}.
	 * @param ctx the parse tree
	 */
	void enterIsstring(schemaParser.IsstringContext ctx);
	/**
	 * Exit a parse tree produced by the {@code isstring}
	 * labeled alternative in {@link schemaParser#angestrengend}.
	 * @param ctx the parse tree
	 */
	void exitIsstring(schemaParser.IsstringContext ctx);
	/**
	 * Enter a parse tree produced by the {@code isvalue}
	 * labeled alternative in {@link schemaParser#angestrengend}.
	 * @param ctx the parse tree
	 */
	void enterIsvalue(schemaParser.IsvalueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code isvalue}
	 * labeled alternative in {@link schemaParser#angestrengend}.
	 * @param ctx the parse tree
	 */
	void exitIsvalue(schemaParser.IsvalueContext ctx);
	/**
	 * Enter a parse tree produced by {@link schemaParser#lbound}.
	 * @param ctx the parse tree
	 */
	void enterLbound(schemaParser.LboundContext ctx);
	/**
	 * Exit a parse tree produced by {@link schemaParser#lbound}.
	 * @param ctx the parse tree
	 */
	void exitLbound(schemaParser.LboundContext ctx);
	/**
	 * Enter a parse tree produced by {@link schemaParser#ubound}.
	 * @param ctx the parse tree
	 */
	void enterUbound(schemaParser.UboundContext ctx);
	/**
	 * Exit a parse tree produced by {@link schemaParser#ubound}.
	 * @param ctx the parse tree
	 */
	void exitUbound(schemaParser.UboundContext ctx);
}