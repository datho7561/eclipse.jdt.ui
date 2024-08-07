/*******************************************************************************
 * Copyright (c) 2000, 2019 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Mateusz Matela <mateusz.matela@gmail.com> - [formatter] Formatter does not format Java code correctly, especially when max line width is set
 *******************************************************************************/
package org.eclipse.jdt.testplugin;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;

import org.eclipse.jdt.internal.formatter.DefaultCodeFormatterOptions.Alignment;

import org.eclipse.jdt.ui.PreferenceConstants;

import org.eclipse.jdt.internal.ui.JavaPlugin;

public class TestOptions {

	public static Hashtable<String, String> getDefaultOptions() {
		Hashtable<String, String> result= JavaCore.getDefaultOptions();
		result.put(JavaCore.COMPILER_PB_LOCAL_VARIABLE_HIDING, JavaCore.IGNORE);
		result.put(JavaCore.COMPILER_PB_FIELD_HIDING, JavaCore.IGNORE);
		result.put(JavaCore.COMPILER_PB_UNUSED_PRIVATE_MEMBER, JavaCore.IGNORE);
		result.put(JavaCore.COMPILER_PB_UNUSED_LOCAL, JavaCore.IGNORE);
		result.put(JavaCore.COMPILER_PB_RAW_TYPE_REFERENCE, JavaCore.IGNORE);
		result.put(JavaCore.COMPILER_PB_UNUSED_WARNING_TOKEN, JavaCore.IGNORE);
		result.put(JavaCore.COMPILER_PB_DEAD_CODE, JavaCore.IGNORE);
		result.put(JavaCore.CODEASSIST_SUBWORD_MATCH, JavaCore.DISABLED);
		// should cover all compiler settings
		result.putAll(TestFormatterOptions.getSettings());
		return result;
	}

	public static void initializeCodeGenerationOptions() {
		IPreferenceStore store= JavaPlugin.getDefault().getPreferenceStore();
		store.setValue(PreferenceConstants.CODEGEN_KEYWORD_THIS, false);
		store.setValue(PreferenceConstants.CODEGEN_IS_FOR_GETTERS, true);
		store.setValue(PreferenceConstants.CODEGEN_EXCEPTION_VAR_NAME, "e"); //$NON-NLS-1$
		store.setValue(PreferenceConstants.CODEGEN_ADD_COMMENTS, true);
		store.setValue(PreferenceConstants.ORGIMPORTS_IMPORTORDER, "java;javax;org;com"); //$NON-NLS-1$
		store.setValue(PreferenceConstants.ORGIMPORTS_ONDEMANDTHRESHOLD, 99);
		store.setValue(PreferenceConstants.ORGIMPORTS_IGNORELOWERCASE, true);
	}

	public static void initializeProjectOptions(IJavaProject project) {
		Map<String, String> options= new HashMap<>();
		JavaProjectHelper.set18CompilerOptions(options);
		project.setOptions(options);
	}

	private TestOptions() {
	}

}

class TestFormatterOptions {

	public static Hashtable<String, String> getSettings() {
		TestFormatterOptions options = new TestFormatterOptions();
		options.setDefaultSettings();
		return options.getMap();
	}

	public int alignment_for_arguments_in_allocation_expression;
	public int alignment_for_arguments_in_explicit_constructor_call;
	public int alignment_for_arguments_in_method_invocation;
	public int alignment_for_arguments_in_qualified_allocation_expression;
	public int alignment_for_binary_expression;
	public int alignment_for_compact_if;
	public int alignment_for_conditional_expression;
	public int alignment_for_expressions_in_array_initializer;
	public int alignment_for_multiple_fields;
	public int alignment_for_parameters_in_constructor_declaration;
	public int alignment_for_parameters_in_method_declaration;
	public int alignment_for_selector_in_method_invocation;
	public int alignment_for_superclass_in_type_declaration;
	public int alignment_for_superinterfaces_in_type_declaration;
	public int alignment_for_throws_clause_in_constructor_declaration;
	public int alignment_for_throws_clause_in_method_declaration;

	public boolean align_type_members_on_columns;

	public String brace_position_for_anonymous_type_declaration;
	public String brace_position_for_array_initializer;
	public String brace_position_for_block;
	public String brace_position_for_block_in_case;
	public String brace_position_for_constructor_declaration;
	public String brace_position_for_method_declaration;
	public String brace_position_for_type_declaration;
	public String brace_position_for_switch;

	public int continuation_indentation;
	public int continuation_indentation_for_array_initializer;

	public int blank_lines_after_imports;
	public int blank_lines_after_package;
	public int blank_lines_before_field;
	public int blank_lines_before_first_class_body_declaration;
	public int blank_lines_before_imports;
	public int blank_lines_before_member_type;
	public int blank_lines_before_abstract_method;
	public int blank_lines_before_method;
	public int blank_lines_before_new_chunk;
	public int blank_lines_before_package;
	public int blank_lines_between_type_declarations;
	public int blank_lines_at_beginning_of_method_body;

	public boolean indent_statements_compare_to_block;
	public boolean indent_statements_compare_to_body;
	public boolean indent_body_declarations_compare_to_type_header;
	public boolean indent_breaks_compare_to_cases;
	public boolean indent_switchstatements_compare_to_cases;
	public boolean indent_switchstatements_compare_to_switch;

	public boolean insert_new_line_after_opening_brace_in_array_initializer;
	public boolean insert_new_line_after_annotation_on_member;
	public boolean insert_new_line_after_annotation_on_parameter;
	public boolean insert_new_line_after_annotation_on_local_variable;
	public boolean insert_new_line_at_end_of_file_if_missing;
	public boolean insert_new_line_before_catch_in_try_statement;
	public boolean insert_new_line_before_closing_brace_in_array_initializer;
	public boolean insert_new_line_before_else_in_if_statement;
	public boolean insert_new_line_before_finally_in_try_statement;
	public boolean insert_new_line_before_while_in_do_statement;
	public boolean insert_new_line_in_empty_anonymous_type_declaration;
	public boolean insert_new_line_in_empty_block;
	public boolean insert_new_line_in_empty_method_body;
	public boolean insert_new_line_in_empty_type_declaration;
	public boolean insert_space_after_assignment_operator;
	public boolean insert_space_after_binary_operator;
	public boolean insert_space_after_closing_paren_in_cast;
	public boolean insert_space_after_closing_brace_in_block;
	public boolean insert_space_after_colon_in_assert;
	public boolean insert_space_after_colon_in_case;
	public boolean insert_space_after_colon_in_conditional;
	public boolean insert_space_after_colon_in_labeled_statement;
	public boolean insert_space_after_comma_in_allocation_expression;
	public boolean insert_space_after_comma_in_array_initializer;
	public boolean insert_space_after_comma_in_constructor_declaration_parameters;
	public boolean insert_space_after_comma_in_constructor_declaration_throws;
	public boolean insert_space_after_comma_in_explicit_constructor_call_arguments;
	public boolean insert_space_after_comma_in_for_increments;
	public boolean insert_space_after_comma_in_for_inits;
	public boolean insert_space_after_comma_in_method_invocation_arguments;
	public boolean insert_space_after_comma_in_method_declaration_parameters;
	public boolean insert_space_after_comma_in_method_declaration_throws;
	public boolean insert_space_after_comma_in_multiple_field_declarations;
	public boolean insert_space_after_comma_in_multiple_local_declarations;
	public boolean insert_space_after_comma_in_superinterfaces;
	public boolean insert_space_after_opening_bracket_in_array_allocation_expression;
	public boolean insert_space_after_opening_bracket_in_array_reference;
	public boolean insert_space_after_opening_brace_in_array_initializer;
	public boolean insert_space_after_opening_paren_in_cast;
	public boolean insert_space_after_opening_paren_in_catch;
	public boolean insert_space_after_opening_paren_in_constructor_declaration;
	public boolean insert_space_after_opening_paren_in_for;
	public boolean insert_space_after_opening_paren_in_if;
	public boolean insert_space_after_opening_paren_in_method_declaration;
	public boolean insert_space_after_opening_paren_in_method_invocation;
	public boolean insert_space_after_opening_paren_in_parenthesized_expression;
	public boolean insert_space_after_opening_paren_in_switch;
	public boolean insert_space_after_opening_paren_in_synchronized;
	public boolean insert_space_after_opening_paren_in_while;
	public boolean insert_space_after_postfix_operator;
	public boolean insert_space_after_prefix_operator;
	public boolean insert_space_after_question_in_conditional;
	public boolean insert_space_after_semicolon_in_for;
	public boolean insert_space_after_unary_operator;
	public boolean insert_space_before_assignment_operator;
	public boolean insert_space_before_binary_operator;
	public boolean insert_space_before_closing_brace_in_array_initializer;
	public boolean insert_space_before_closing_bracket_in_array_allocation_expression;
	public boolean insert_space_before_closing_bracket_in_array_reference;
	public boolean insert_space_before_closing_paren_in_cast;
	public boolean insert_space_before_closing_paren_in_catch;
	public boolean insert_space_before_closing_paren_in_constructor_declaration;
	public boolean insert_space_before_closing_paren_in_for;
	public boolean insert_space_before_closing_paren_in_if;
	public boolean insert_space_before_closing_paren_in_method_declaration;
	public boolean insert_space_before_closing_paren_in_method_invocation;
	public boolean insert_space_before_closing_paren_in_parenthesized_expression;
	public boolean insert_space_before_closing_paren_in_switch;
	public boolean insert_space_before_closing_paren_in_synchronized;
	public boolean insert_space_before_closing_paren_in_while;
	public boolean insert_space_before_colon_in_assert;
	public boolean insert_space_before_colon_in_case;
	public boolean insert_space_before_colon_in_conditional;
	public boolean insert_space_before_colon_in_default;
	public boolean insert_space_before_colon_in_labeled_statement;
	public boolean insert_space_before_comma_in_allocation_expression;
	public boolean insert_space_before_comma_in_array_initializer;
	public boolean insert_space_before_comma_in_constructor_declaration_parameters;
	public boolean insert_space_before_comma_in_constructor_declaration_throws;
	public boolean insert_space_before_comma_in_explicit_constructor_call_arguments;
	public boolean insert_space_before_comma_in_for_increments;
	public boolean insert_space_before_comma_in_for_inits;
	public boolean insert_space_before_comma_in_method_invocation_arguments;
	public boolean insert_space_before_comma_in_method_declaration_parameters;
	public boolean insert_space_before_comma_in_method_declaration_throws;
	public boolean insert_space_before_comma_in_multiple_field_declarations;
	public boolean insert_space_before_comma_in_multiple_local_declarations;
	public boolean insert_space_before_comma_in_superinterfaces;
	public boolean insert_space_before_opening_brace_in_anonymous_type_declaration;
	public boolean insert_space_before_opening_brace_in_array_initializer;
	public boolean insert_space_before_opening_brace_in_block;
	public boolean insert_space_before_opening_brace_in_constructor_declaration;
	public boolean insert_space_before_opening_brace_in_method_declaration;
	public boolean insert_space_before_opening_brace_in_type_declaration;
	public boolean insert_space_before_opening_bracket_in_array_allocation_expression;
	public boolean insert_space_before_opening_bracket_in_array_reference;
	public boolean insert_space_before_opening_bracket_in_array_type_reference;
	public boolean insert_space_before_opening_paren_in_catch;
	public boolean insert_space_before_opening_paren_in_constructor_declaration;
	public boolean insert_space_before_opening_paren_in_for;
	public boolean insert_space_before_opening_paren_in_if;
	public boolean insert_space_before_opening_paren_in_method_invocation;
	public boolean insert_space_before_opening_paren_in_method_declaration;
	public boolean insert_space_before_opening_paren_in_switch;
	public boolean insert_space_before_opening_brace_in_switch;
	public boolean insert_space_before_opening_paren_in_synchronized;
	public boolean insert_space_before_opening_paren_in_parenthesized_expression;
	public boolean insert_space_before_opening_paren_in_while;
	public boolean insert_space_before_postfix_operator;
	public boolean insert_space_before_prefix_operator;
	public boolean insert_space_before_question_in_conditional;
	public boolean insert_space_before_semicolon;
	public boolean insert_space_before_semicolon_in_for;
	public boolean insert_space_before_unary_operator;
	public boolean insert_space_between_brackets_in_array_type_reference;
	public boolean insert_space_between_empty_braces_in_array_initializer;
	public boolean insert_space_between_empty_brackets_in_array_allocation_expression;
	public boolean insert_space_between_empty_parens_in_constructor_declaration;
	public boolean insert_space_between_empty_parens_in_method_declaration;
	public boolean insert_space_between_empty_parens_in_method_invocation;
	public boolean compact_else_if;
	public boolean keep_guardian_clause_on_one_line;
	public boolean keep_else_statement_on_same_line;
	public boolean keep_empty_array_initializer_on_one_line;
	public boolean keep_simple_if_on_one_line;
	public boolean keep_then_statement_on_same_line;
	public boolean never_indent_block_comments_on_first_column;
	public boolean never_indent_line_comments_on_first_column;
	public int number_of_empty_lines_to_preserve;
	public boolean put_empty_statement_on_new_line;
	public int tab_size;
	public final char filling_space = ' ';
	public int page_width;
	public boolean use_tab;
	public boolean wrapBeforeBinaryOperator;

	public int initial_indentation_level;
	public String line_separator;

	private TestFormatterOptions() {
		// cannot be instantiated
	}

	private String getAlignment(int alignment) {
		return Integer.toString(alignment);
	}

	private Hashtable<String, String> getMap() {
		Hashtable<String, String> options = new Hashtable<>();
		options.put(DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_ARGUMENTS_IN_ALLOCATION_EXPRESSION, getAlignment(this.alignment_for_arguments_in_allocation_expression));
		options.put(DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_ARGUMENTS_IN_EXPLICIT_CONSTRUCTOR_CALL, getAlignment(this.alignment_for_arguments_in_explicit_constructor_call));
		options.put(DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_ARGUMENTS_IN_METHOD_INVOCATION, getAlignment(this.alignment_for_arguments_in_method_invocation));
		options.put(DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_ARGUMENTS_IN_QUALIFIED_ALLOCATION_EXPRESSION, getAlignment(this.alignment_for_arguments_in_qualified_allocation_expression));
		options.put(DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_BINARY_EXPRESSION, getAlignment(this.alignment_for_binary_expression));
		options.put(DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_COMPACT_IF, getAlignment(this.alignment_for_compact_if));
		options.put(DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_CONDITIONAL_EXPRESSION, getAlignment(this.alignment_for_conditional_expression));
		options.put(DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_EXPRESSIONS_IN_ARRAY_INITIALIZER, getAlignment(this.alignment_for_expressions_in_array_initializer));
		options.put(DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_MULTIPLE_FIELDS, getAlignment(this.alignment_for_multiple_fields));
		options.put(DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_PARAMETERS_IN_CONSTRUCTOR_DECLARATION, getAlignment(this.alignment_for_parameters_in_constructor_declaration));
		options.put(DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_PARAMETERS_IN_METHOD_DECLARATION, getAlignment(this.alignment_for_parameters_in_method_declaration));
		options.put(DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_SELECTOR_IN_METHOD_INVOCATION, getAlignment(this.alignment_for_selector_in_method_invocation));
		options.put(DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_SUPERCLASS_IN_TYPE_DECLARATION, getAlignment(this.alignment_for_superclass_in_type_declaration));
		options.put(DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_SUPERINTERFACES_IN_TYPE_DECLARATION, getAlignment(this.alignment_for_superinterfaces_in_type_declaration));
		options.put(DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_THROWS_CLAUSE_IN_CONSTRUCTOR_DECLARATION, getAlignment(this.alignment_for_throws_clause_in_constructor_declaration));
		options.put(DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_THROWS_CLAUSE_IN_METHOD_DECLARATION, getAlignment(this.alignment_for_throws_clause_in_method_declaration));
		options.put(DefaultCodeFormatterConstants.FORMATTER_ALIGN_TYPE_MEMBERS_ON_COLUMNS, this.align_type_members_on_columns ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_ANONYMOUS_TYPE_DECLARATION, this.brace_position_for_anonymous_type_declaration);
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_ARRAY_INITIALIZER, this.brace_position_for_array_initializer);
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_BLOCK, this.brace_position_for_block);
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_BLOCK_IN_CASE, this.brace_position_for_block_in_case);
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_CONSTRUCTOR_DECLARATION, this.brace_position_for_constructor_declaration);
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_METHOD_DECLARATION, this.brace_position_for_method_declaration);
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_TYPE_DECLARATION, this.brace_position_for_type_declaration);
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_SWITCH, this.brace_position_for_switch);
		options.put(DefaultCodeFormatterConstants.FORMATTER_CONTINUATION_INDENTATION, Integer.toString(this.continuation_indentation));
		options.put(DefaultCodeFormatterConstants.FORMATTER_CONTINUATION_INDENTATION_FOR_ARRAY_INITIALIZER, Integer.toString(this.continuation_indentation_for_array_initializer));
		options.put(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_AFTER_IMPORTS, Integer.toString(this.blank_lines_after_imports));
		options.put(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_AFTER_PACKAGE, Integer.toString(this.blank_lines_after_package));
		options.put(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_FIELD, Integer.toString(this.blank_lines_before_field));
		options.put(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_FIRST_CLASS_BODY_DECLARATION, Integer.toString(this.blank_lines_before_first_class_body_declaration));
		options.put(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_IMPORTS, Integer.toString(this.blank_lines_before_imports));
		options.put(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_MEMBER_TYPE, Integer.toString(this.blank_lines_before_member_type));
		options.put(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_ABSTRACT_METHOD, Integer.toString(this.blank_lines_before_abstract_method));
		options.put(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_METHOD, Integer.toString(this.blank_lines_before_method));
		options.put(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_NEW_CHUNK, Integer.toString(this.blank_lines_before_new_chunk));
		options.put(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_PACKAGE, Integer.toString(this.blank_lines_before_package));
		options.put(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_BETWEEN_TYPE_DECLARATIONS, Integer.toString(this.blank_lines_between_type_declarations));
		options.put(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_AT_BEGINNING_OF_METHOD_BODY, Integer.toString(this.blank_lines_at_beginning_of_method_body));
		options.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_STATEMENTS_COMPARE_TO_BLOCK, this.indent_statements_compare_to_block ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_STATEMENTS_COMPARE_TO_BODY, this.indent_statements_compare_to_body ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_BODY_DECLARATIONS_COMPARE_TO_TYPE_HEADER, this.indent_body_declarations_compare_to_type_header ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_BREAKS_COMPARE_TO_CASES, this.indent_breaks_compare_to_cases ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_SWITCHSTATEMENTS_COMPARE_TO_CASES, this.indent_switchstatements_compare_to_cases ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_SWITCHSTATEMENTS_COMPARE_TO_SWITCH, this.indent_switchstatements_compare_to_switch ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_AFTER_OPENING_BRACE_IN_ARRAY_INITIALIZER, this.insert_new_line_after_opening_brace_in_array_initializer? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_AFTER_ANNOTATION_ON_MEMBER, this.insert_new_line_after_annotation_on_member? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_AFTER_ANNOTATION_ON_PARAMETER, this.insert_new_line_after_annotation_on_parameter? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_AFTER_ANNOTATION_ON_LOCAL_VARIABLE, this.insert_new_line_after_annotation_on_local_variable? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_AT_END_OF_FILE_IF_MISSING, this.insert_new_line_at_end_of_file_if_missing? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_CATCH_IN_TRY_STATEMENT, this.insert_new_line_before_catch_in_try_statement? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_CLOSING_BRACE_IN_ARRAY_INITIALIZER, this.insert_new_line_before_closing_brace_in_array_initializer? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_ELSE_IN_IF_STATEMENT, this.insert_new_line_before_else_in_if_statement? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_FINALLY_IN_TRY_STATEMENT, this.insert_new_line_before_finally_in_try_statement? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_WHILE_IN_DO_STATEMENT, this.insert_new_line_before_while_in_do_statement? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_IN_EMPTY_ANONYMOUS_TYPE_DECLARATION, this.insert_new_line_in_empty_anonymous_type_declaration? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_IN_EMPTY_BLOCK, this.insert_new_line_in_empty_block? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_IN_EMPTY_METHOD_BODY, this.insert_new_line_in_empty_method_body? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_IN_EMPTY_TYPE_DECLARATION, this.insert_new_line_in_empty_type_declaration? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_ASSIGNMENT_OPERATOR, this.insert_space_after_assignment_operator? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_BINARY_OPERATOR, this.insert_space_after_binary_operator? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_CLOSING_PAREN_IN_CAST, this.insert_space_after_closing_paren_in_cast? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_CLOSING_BRACE_IN_BLOCK, this.insert_space_after_closing_brace_in_block? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COLON_IN_ASSERT, this.insert_space_after_colon_in_assert? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COLON_IN_CASE, this.insert_space_after_colon_in_case? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COLON_IN_CONDITIONAL, this.insert_space_after_colon_in_conditional? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COLON_IN_LABELED_STATEMENT, this.insert_space_after_colon_in_labeled_statement? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_ALLOCATION_EXPRESSION, this.insert_space_after_comma_in_allocation_expression? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_ARRAY_INITIALIZER, this.insert_space_after_comma_in_array_initializer? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_CONSTRUCTOR_DECLARATION_PARAMETERS, this.insert_space_after_comma_in_constructor_declaration_parameters? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_CONSTRUCTOR_DECLARATION_THROWS, this.insert_space_after_comma_in_constructor_declaration_throws? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_EXPLICIT_CONSTRUCTOR_CALL_ARGUMENTS, this.insert_space_after_comma_in_explicit_constructor_call_arguments? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_FOR_INCREMENTS, this.insert_space_after_comma_in_for_increments? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_FOR_INITS, this.insert_space_after_comma_in_for_inits? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_METHOD_INVOCATION_ARGUMENTS, this.insert_space_after_comma_in_method_invocation_arguments? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_METHOD_DECLARATION_PARAMETERS, this.insert_space_after_comma_in_method_declaration_parameters? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_METHOD_DECLARATION_THROWS, this.insert_space_after_comma_in_method_declaration_throws? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_MULTIPLE_FIELD_DECLARATIONS, this.insert_space_after_comma_in_multiple_field_declarations? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_MULTIPLE_LOCAL_DECLARATIONS, this.insert_space_after_comma_in_multiple_local_declarations? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_SUPERINTERFACES, this.insert_space_after_comma_in_superinterfaces? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_BRACKET_IN_ARRAY_ALLOCATION_EXPRESSION, this.insert_space_after_opening_bracket_in_array_allocation_expression? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_BRACKET_IN_ARRAY_REFERENCE, this.insert_space_after_opening_bracket_in_array_reference? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_BRACE_IN_ARRAY_INITIALIZER, this.insert_space_after_opening_brace_in_array_initializer? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_CAST, this.insert_space_after_opening_paren_in_cast? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_CATCH, this.insert_space_after_opening_paren_in_catch? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_CONSTRUCTOR_DECLARATION, this.insert_space_after_opening_paren_in_constructor_declaration? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_FOR, this.insert_space_after_opening_paren_in_for? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_IF, this.insert_space_after_opening_paren_in_if? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_METHOD_DECLARATION, this.insert_space_after_opening_paren_in_method_declaration? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_METHOD_INVOCATION, this.insert_space_after_opening_paren_in_method_invocation? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_PARENTHESIZED_EXPRESSION, this.insert_space_after_opening_paren_in_parenthesized_expression? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_SWITCH, this.insert_space_after_opening_paren_in_switch? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_SYNCHRONIZED, this.insert_space_after_opening_paren_in_synchronized? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_WHILE, this.insert_space_after_opening_paren_in_while? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_POSTFIX_OPERATOR, this.insert_space_after_postfix_operator? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_PREFIX_OPERATOR, this.insert_space_after_prefix_operator? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_QUESTION_IN_CONDITIONAL, this.insert_space_after_question_in_conditional? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_SEMICOLON_IN_FOR, this.insert_space_after_semicolon_in_for? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_UNARY_OPERATOR, this.insert_space_after_unary_operator? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_ASSIGNMENT_OPERATOR, this.insert_space_before_assignment_operator? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_BINARY_OPERATOR, this.insert_space_before_binary_operator? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_BRACE_IN_ARRAY_INITIALIZER, this.insert_space_before_closing_brace_in_array_initializer? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_BRACKET_IN_ARRAY_ALLOCATION_EXPRESSION, this.insert_space_before_closing_bracket_in_array_allocation_expression? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_BRACKET_IN_ARRAY_REFERENCE, this.insert_space_before_closing_bracket_in_array_reference? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_CAST, this.insert_space_before_closing_paren_in_cast? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_CATCH, this.insert_space_before_closing_paren_in_catch? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_CONSTRUCTOR_DECLARATION, this.insert_space_before_closing_paren_in_constructor_declaration? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_FOR, this.insert_space_before_closing_paren_in_for? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_IF, this.insert_space_before_closing_paren_in_if? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_METHOD_DECLARATION, this.insert_space_before_closing_paren_in_method_declaration? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_METHOD_INVOCATION, this.insert_space_before_closing_paren_in_method_invocation? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_PARENTHESIZED_EXPRESSION, this.insert_space_before_closing_paren_in_parenthesized_expression? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_SWITCH, this.insert_space_before_closing_paren_in_switch? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_SYNCHRONIZED, this.insert_space_before_closing_paren_in_synchronized? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_WHILE, this.insert_space_before_closing_paren_in_while? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COLON_IN_ASSERT, this.insert_space_before_colon_in_assert? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COLON_IN_CASE, this.insert_space_before_colon_in_case? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COLON_IN_CONDITIONAL, this.insert_space_before_colon_in_conditional? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COLON_IN_DEFAULT, this.insert_space_before_colon_in_default? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COLON_IN_LABELED_STATEMENT, this.insert_space_before_colon_in_labeled_statement? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_ALLOCATION_EXPRESSION, this.insert_space_before_comma_in_allocation_expression? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_ARRAY_INITIALIZER, this.insert_space_before_comma_in_array_initializer? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_CONSTRUCTOR_DECLARATION_PARAMETERS, this.insert_space_before_comma_in_constructor_declaration_parameters? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_CONSTRUCTOR_DECLARATION_THROWS, this.insert_space_before_comma_in_constructor_declaration_throws? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_EXPLICIT_CONSTRUCTOR_CALL_ARGUMENTS, this.insert_space_before_comma_in_explicit_constructor_call_arguments? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_FOR_INCREMENTS, this.insert_space_before_comma_in_for_increments? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_FOR_INITS, this.insert_space_before_comma_in_for_inits? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_METHOD_INVOCATION_ARGUMENTS, this.insert_space_before_comma_in_method_invocation_arguments? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_METHOD_DECLARATION_PARAMETERS, this.insert_space_before_comma_in_method_declaration_parameters? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_METHOD_DECLARATION_THROWS, this.insert_space_before_comma_in_method_declaration_throws? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_MULTIPLE_FIELD_DECLARATIONS, this.insert_space_before_comma_in_multiple_field_declarations? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_MULTIPLE_LOCAL_DECLARATIONS, this.insert_space_before_comma_in_multiple_local_declarations? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_SUPERINTERFACES, this.insert_space_before_comma_in_superinterfaces? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_ANONYMOUS_TYPE_DECLARATION, this.insert_space_before_opening_brace_in_anonymous_type_declaration? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_ARRAY_INITIALIZER, this.insert_space_before_opening_brace_in_array_initializer? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_BLOCK, this.insert_space_before_opening_brace_in_block? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_CONSTRUCTOR_DECLARATION, this.insert_space_before_opening_brace_in_constructor_declaration? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_METHOD_DECLARATION, this.insert_space_before_opening_brace_in_method_declaration? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_TYPE_DECLARATION, this.insert_space_before_opening_brace_in_type_declaration? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACKET_IN_ARRAY_ALLOCATION_EXPRESSION, this.insert_space_before_opening_bracket_in_array_allocation_expression ? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACKET_IN_ARRAY_REFERENCE, this.insert_space_before_opening_bracket_in_array_reference? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACKET_IN_ARRAY_TYPE_REFERENCE, this.insert_space_before_opening_bracket_in_array_type_reference? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_CATCH, this.insert_space_before_opening_paren_in_catch? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_CONSTRUCTOR_DECLARATION, this.insert_space_before_opening_paren_in_constructor_declaration? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FOR, this.insert_space_before_opening_paren_in_for? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_IF, this.insert_space_before_opening_paren_in_if? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_METHOD_INVOCATION, this.insert_space_before_opening_paren_in_method_invocation? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_METHOD_DECLARATION, this.insert_space_before_opening_paren_in_method_declaration? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_SWITCH, this.insert_space_before_opening_paren_in_switch? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_SWITCH, this.insert_space_before_opening_brace_in_switch? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_SYNCHRONIZED, this.insert_space_before_opening_paren_in_synchronized? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_PARENTHESIZED_EXPRESSION, this.insert_space_before_opening_paren_in_parenthesized_expression? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_WHILE, this.insert_space_before_opening_paren_in_while? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_POSTFIX_OPERATOR, this.insert_space_before_postfix_operator? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_PREFIX_OPERATOR, this.insert_space_before_prefix_operator? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_QUESTION_IN_CONDITIONAL, this.insert_space_before_question_in_conditional? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON, this.insert_space_before_semicolon? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON_IN_FOR, this.insert_space_before_semicolon_in_for? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_UNARY_OPERATOR, this.insert_space_before_unary_operator? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_BRACKETS_IN_ARRAY_TYPE_REFERENCE, this.insert_space_between_brackets_in_array_type_reference? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_BRACES_IN_ARRAY_INITIALIZER, this.insert_space_between_empty_braces_in_array_initializer? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_BRACKETS_IN_ARRAY_ALLOCATION_EXPRESSION, this.insert_space_between_empty_brackets_in_array_allocation_expression? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_CONSTRUCTOR_DECLARATION, this.insert_space_between_empty_parens_in_constructor_declaration? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_METHOD_DECLARATION, this.insert_space_between_empty_parens_in_method_declaration? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_METHOD_INVOCATION, this.insert_space_between_empty_parens_in_method_invocation? JavaCore.INSERT : JavaCore.DO_NOT_INSERT);
		options.put(DefaultCodeFormatterConstants.FORMATTER_COMPACT_ELSE_IF, this.compact_else_if ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_GUARDIAN_CLAUSE_ON_ONE_LINE, this.keep_guardian_clause_on_one_line ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_ELSE_STATEMENT_ON_SAME_LINE, this.keep_else_statement_on_same_line ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_EMPTY_ARRAY_INITIALIZER_ON_ONE_LINE, this.keep_empty_array_initializer_on_one_line ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_IF_ON_ONE_LINE, this.keep_simple_if_on_one_line ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_THEN_STATEMENT_ON_SAME_LINE, this.keep_then_statement_on_same_line ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_NUMBER_OF_EMPTY_LINES_TO_PRESERVE, Integer.toString(this.number_of_empty_lines_to_preserve));
		options.put(DefaultCodeFormatterConstants.FORMATTER_PUT_EMPTY_STATEMENT_ON_NEW_LINE, this.put_empty_statement_on_new_line ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_LINE_SPLIT, Integer.toString(this.page_width));
		options.put(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, this.use_tab ? JavaCore.TAB: JavaCore.SPACE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, Integer.toString(this.tab_size));
		options.put(DefaultCodeFormatterConstants.FORMATTER_WRAP_BEFORE_BINARY_OPERATOR, this.wrapBeforeBinaryOperator ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_NEVER_INDENT_BLOCK_COMMENTS_ON_FIRST_COLUMN, this.never_indent_block_comments_on_first_column ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_NEVER_INDENT_LINE_COMMENTS_ON_FIRST_COLUMN, this.never_indent_line_comments_on_first_column ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		return options;
	}

	private void setDefaultSettings() {
		this.alignment_for_arguments_in_allocation_expression = Alignment.M_COMPACT_SPLIT;
		this.alignment_for_arguments_in_explicit_constructor_call = Alignment.M_COMPACT_SPLIT;
		this.alignment_for_arguments_in_method_invocation = Alignment.M_COMPACT_SPLIT;
		this.alignment_for_arguments_in_qualified_allocation_expression = Alignment.M_COMPACT_SPLIT;
		this.alignment_for_binary_expression = Alignment.M_COMPACT_SPLIT;
		this.alignment_for_compact_if = Alignment.M_ONE_PER_LINE_SPLIT | Alignment.M_INDENT_BY_ONE;
		this.alignment_for_conditional_expression = Alignment.M_ONE_PER_LINE_SPLIT;
		this.alignment_for_expressions_in_array_initializer = Alignment.M_COMPACT_SPLIT;
		this.alignment_for_multiple_fields = Alignment.M_COMPACT_SPLIT;
		this.alignment_for_parameters_in_constructor_declaration = Alignment.M_COMPACT_SPLIT;
		this.alignment_for_parameters_in_method_declaration = Alignment.M_COMPACT_SPLIT;
		this.alignment_for_selector_in_method_invocation = Alignment.M_COMPACT_SPLIT;
		this.alignment_for_superclass_in_type_declaration = Alignment.M_NEXT_SHIFTED_SPLIT;
		this.alignment_for_superinterfaces_in_type_declaration = Alignment.M_NEXT_SHIFTED_SPLIT;
		this.alignment_for_throws_clause_in_constructor_declaration = Alignment.M_COMPACT_SPLIT;
		this.alignment_for_throws_clause_in_method_declaration = Alignment.M_COMPACT_SPLIT;
		this.align_type_members_on_columns = false;
		this.brace_position_for_anonymous_type_declaration = DefaultCodeFormatterConstants.END_OF_LINE;
		this.brace_position_for_array_initializer = DefaultCodeFormatterConstants.END_OF_LINE;
		this.brace_position_for_block = DefaultCodeFormatterConstants.END_OF_LINE;
		this.brace_position_for_block_in_case = DefaultCodeFormatterConstants.END_OF_LINE;
		this.brace_position_for_constructor_declaration = DefaultCodeFormatterConstants.END_OF_LINE;
		this.brace_position_for_method_declaration = DefaultCodeFormatterConstants.END_OF_LINE;
		this.brace_position_for_type_declaration = DefaultCodeFormatterConstants.END_OF_LINE;
		this.brace_position_for_switch = DefaultCodeFormatterConstants.END_OF_LINE;
		this.continuation_indentation = 2;
		this.continuation_indentation_for_array_initializer = 2;
		this.blank_lines_after_imports = 0;
		this.blank_lines_after_package = 0;
		this.blank_lines_before_field = 0;
		this.blank_lines_before_first_class_body_declaration = 0;
		this.blank_lines_before_imports = 0;
		this.blank_lines_before_member_type = 0;
		this.blank_lines_before_abstract_method = 0;
		this.blank_lines_before_method = 0;
		this.blank_lines_before_new_chunk = 0;
		this.blank_lines_before_package = 0;
		this.blank_lines_between_type_declarations = 0;
		this.blank_lines_at_beginning_of_method_body = 0;
		this.indent_statements_compare_to_block = true;
		this.indent_statements_compare_to_body = true;
		this.indent_body_declarations_compare_to_type_header = true;
		this.indent_breaks_compare_to_cases = true;
		this.indent_switchstatements_compare_to_cases = true;
		this.indent_switchstatements_compare_to_switch = true;
		this.insert_new_line_after_annotation_on_member= true;
		this.insert_new_line_after_annotation_on_parameter= false;
		this.insert_new_line_after_annotation_on_local_variable= true;
		this.insert_new_line_at_end_of_file_if_missing= false;
		this.insert_new_line_after_opening_brace_in_array_initializer = false;
		this.insert_new_line_before_catch_in_try_statement = false;
		this.insert_new_line_before_closing_brace_in_array_initializer = false;
		this.insert_new_line_before_else_in_if_statement = false;
		this.insert_new_line_before_finally_in_try_statement = false;
		this.insert_new_line_before_while_in_do_statement = false;
		this.insert_new_line_in_empty_anonymous_type_declaration = true;
		this.insert_new_line_in_empty_block = true;
		this.insert_new_line_in_empty_method_body = true;
		this.insert_new_line_in_empty_type_declaration = true;
		this.insert_space_after_assignment_operator = true;
		this.insert_space_after_binary_operator = true;
		this.insert_space_after_closing_paren_in_cast = true;
		this.insert_space_after_closing_brace_in_block = true;
		this.insert_space_after_colon_in_assert = true;
		this.insert_space_after_colon_in_case = true;
		this.insert_space_after_colon_in_conditional = true;
		this.insert_space_after_colon_in_labeled_statement = true;
		this.insert_space_after_comma_in_allocation_expression = true;
		this.insert_space_after_comma_in_array_initializer = true;
		this.insert_space_after_comma_in_constructor_declaration_parameters = true;
		this.insert_space_after_comma_in_constructor_declaration_throws = true;
		this.insert_space_after_comma_in_explicit_constructor_call_arguments = true;
		this.insert_space_after_comma_in_for_increments = true;
		this.insert_space_after_comma_in_for_inits = true;
		this.insert_space_after_comma_in_method_invocation_arguments = true;
		this.insert_space_after_comma_in_method_declaration_parameters = true;
		this.insert_space_after_comma_in_method_declaration_throws = true;
		this.insert_space_after_comma_in_multiple_field_declarations = true;
		this.insert_space_after_comma_in_multiple_local_declarations = true;
		this.insert_space_after_comma_in_superinterfaces = true;
		this.insert_space_after_opening_bracket_in_array_allocation_expression = false;
		this.insert_space_after_opening_bracket_in_array_reference = false;
		this.insert_space_after_opening_brace_in_array_initializer = false;
		this.insert_space_after_opening_paren_in_cast = false;
		this.insert_space_after_opening_paren_in_catch = false;
		this.insert_space_after_opening_paren_in_constructor_declaration = false;
		this.insert_space_after_opening_paren_in_for = false;
		this.insert_space_after_opening_paren_in_if = false;
		this.insert_space_after_opening_paren_in_method_declaration = false;
		this.insert_space_after_opening_paren_in_method_invocation = false;
		this.insert_space_after_opening_paren_in_parenthesized_expression = false;
		this.insert_space_after_opening_paren_in_switch = false;
		this.insert_space_after_opening_paren_in_synchronized = false;
		this.insert_space_after_opening_paren_in_while = false;
		this.insert_space_after_postfix_operator = false;
		this.insert_space_after_prefix_operator = false;
		this.insert_space_after_question_in_conditional = true;
		this.insert_space_after_semicolon_in_for = true;
		this.insert_space_after_unary_operator = false;
		this.insert_space_before_assignment_operator = true;
		this.insert_space_before_binary_operator = true;
		this.insert_space_before_closing_brace_in_array_initializer = false;
		this.insert_space_before_closing_bracket_in_array_allocation_expression = false;
		this.insert_space_before_closing_bracket_in_array_reference = false;
		this.insert_space_before_closing_paren_in_cast = false;
		this.insert_space_before_closing_paren_in_catch = false;
		this.insert_space_before_closing_paren_in_constructor_declaration = false;
		this.insert_space_before_closing_paren_in_for = false;
		this.insert_space_before_closing_paren_in_if = false;
		this.insert_space_before_closing_paren_in_method_declaration = false;
		this.insert_space_before_closing_paren_in_method_invocation = false;
		this.insert_space_before_closing_paren_in_parenthesized_expression = false;
		this.insert_space_before_closing_paren_in_switch = false;
		this.insert_space_before_closing_paren_in_synchronized = false;
		this.insert_space_before_closing_paren_in_while = false;
		this.insert_space_before_colon_in_assert = true;
		this.insert_space_before_colon_in_case = true;
		this.insert_space_before_colon_in_conditional = true;
		this.insert_space_before_colon_in_default = true;
		this.insert_space_before_colon_in_labeled_statement = true;
		this.insert_space_before_comma_in_allocation_expression = false;
		this.insert_space_before_comma_in_array_initializer = false;
		this.insert_space_before_comma_in_constructor_declaration_parameters = false;
		this.insert_space_before_comma_in_constructor_declaration_throws = false;
		this.insert_space_before_comma_in_explicit_constructor_call_arguments = false;
		this.insert_space_before_comma_in_for_increments = false;
		this.insert_space_before_comma_in_for_inits = false;
		this.insert_space_before_comma_in_method_invocation_arguments = false;
		this.insert_space_before_comma_in_method_declaration_parameters = false;
		this.insert_space_before_comma_in_method_declaration_throws = false;
		this.insert_space_before_comma_in_multiple_field_declarations = false;
		this.insert_space_before_comma_in_multiple_local_declarations = false;
		this.insert_space_before_comma_in_superinterfaces = false;
		this.insert_space_before_opening_brace_in_anonymous_type_declaration = true;
		this.insert_space_before_opening_brace_in_array_initializer = false;
		this.insert_space_before_opening_brace_in_block = true;
		this.insert_space_before_opening_brace_in_constructor_declaration = true;
		this.insert_space_before_opening_brace_in_method_declaration = true;
		this.insert_space_before_opening_brace_in_switch = true;
		this.insert_space_before_opening_brace_in_type_declaration = true;
		this.insert_space_before_opening_bracket_in_array_allocation_expression = false;
		this.insert_space_before_opening_bracket_in_array_reference = false;
		this.insert_space_before_opening_bracket_in_array_type_reference = false;
		this.insert_space_before_opening_paren_in_catch = true;
		this.insert_space_before_opening_paren_in_constructor_declaration = false;
		this.insert_space_before_opening_paren_in_for = true;
		this.insert_space_before_opening_paren_in_if = true;
		this.insert_space_before_opening_paren_in_method_invocation = false;
		this.insert_space_before_opening_paren_in_method_declaration = false;
		this.insert_space_before_opening_paren_in_switch = true;
		this.insert_space_before_opening_paren_in_synchronized = true;
		this.insert_space_before_opening_paren_in_parenthesized_expression = false;
		this.insert_space_before_opening_paren_in_while = true;
		this.insert_space_before_postfix_operator = false;
		this.insert_space_before_prefix_operator = false;
		this.insert_space_before_question_in_conditional = true;
		this.insert_space_before_semicolon = false;
		this.insert_space_before_semicolon_in_for = false;
		this.insert_space_before_unary_operator = false;
		this.insert_space_between_brackets_in_array_type_reference = false;
		this.insert_space_between_empty_braces_in_array_initializer = false;
		this.insert_space_between_empty_brackets_in_array_allocation_expression = false;
		this.insert_space_between_empty_parens_in_constructor_declaration = false;
		this.insert_space_between_empty_parens_in_method_declaration = false;
		this.insert_space_between_empty_parens_in_method_invocation = false;
		this.compact_else_if = true;
		this.keep_guardian_clause_on_one_line = false;
		this.keep_else_statement_on_same_line = false;
		this.keep_empty_array_initializer_on_one_line = true;
		this.keep_simple_if_on_one_line = false;
		this.keep_then_statement_on_same_line = false;
		this.number_of_empty_lines_to_preserve = 1;
		this.put_empty_statement_on_new_line = false;
		this.tab_size = 4;
		this.page_width = 80;
		this.use_tab = true; // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=49081
		this.wrapBeforeBinaryOperator = true;
		this.never_indent_block_comments_on_first_column = false;
		this.never_indent_line_comments_on_first_column = false;
	}
}

