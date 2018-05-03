package net.jingbo.x5springboot.baas.data.sql.token;

import net.jingbo.x5springboot.baas.data.sql.SQLException;
import net.jingbo.x5springboot.baas.data.sql.matcher.*;

import java.util.*;

import static net.jingbo.x5springboot.baas.data.sql.token.TokenKind.*;

public class TokenManager {
	private static final TokenManager instance = new TokenManager();

	private HashMap<TokenKind, TokenMatcher> matchers = new LinkedHashMap<TokenKind, TokenMatcher>();

	private TokenManager() {
		matchers.put(EOF, new EOFMatcher());

		matchers.put(KWD_SELECT, new KeywordMatcher("SELECT"));
		matchers.put(KWD_INSERT, new KeywordMatcher("INSERT"));
		matchers.put(KWD_DELETE, new KeywordMatcher("DELETE"));
		matchers.put(KWD_UPDATE, new KeywordMatcher("UPDATE"));

		matchers.put(KWD_FROM, new KeywordMatcher("FROM"));
		matchers.put(KWD_INTO, new KeywordMatcher("INTO"));
		matchers.put(KWD_VALUES, new KeywordMatcher("VALUES"));
		matchers.put(KWD_WHERE, new KeywordMatcher("WHERE"));
		matchers.put(KWD_RETRACT, new KeywordMatcher("RETRACT"));
		matchers.put(KWD_OPTIONAL, new KeywordMatcher("OPTIONAL"));
		matchers.put(KWD_JOIN, new KeywordMatcher("JOIN"));
		matchers.put(KWD_SET, new KeywordMatcher("SET"));
		matchers.put(KWD_ON, new KeywordMatcher("ON"));
		matchers.put(KWD_GROUP, new KeywordMatcher("GROUP"));
		matchers.put(KWD_BY, new KeywordMatcher("BY"));
		matchers.put(KWD_HAVING, new KeywordMatcher("HAVING"));
		matchers.put(KWD_ORDER, new KeywordMatcher("ORDER"));
		matchers.put(KWD_ASC, new KeywordMatcher("ASC"));
		matchers.put(KWD_DESC, new KeywordMatcher("DESC"));
		matchers.put(KWD_AS, new KeywordMatcher("AS"));
		matchers.put(KWD_IS, new KeywordMatcher("IS"));
		matchers.put(KWD_NULL, new KeywordMatcher("NULL"));
		matchers.put(KWD_AND, new KeywordMatcher("AND"));
		matchers.put(KWD_OR, new KeywordMatcher("OR"));
		matchers.put(KWD_NOT, new KeywordMatcher("NOT"));
		matchers.put(KWD_EXISTS, new KeywordMatcher("EXISTS"));
		matchers.put(KWD_LIMIT, new KeywordMatcher("LIMIT"));
		matchers.put(KWD_DISTINCT, new KeywordMatcher("DISTINCT"));
		matchers.put(KWD_LIKE, new KeywordMatcher("LIKE"));
		matchers.put(KWD_IN, new KeywordMatcher("IN"));
		matchers.put(KWD_BETWEEN, new KeywordMatcher("BETWEEN"));
		matchers.put(KWD_UNION, new KeywordMatcher("UNION"));
		matchers.put(KWD_CASE, new KeywordMatcher("CASE"));
		matchers.put(KWD_WHEN, new KeywordMatcher("WHEN"));
		matchers.put(KWD_THEN, new KeywordMatcher("THEN"));
		matchers.put(KWD_ELSE, new KeywordMatcher("ELSE"));
		matchers.put(KWD_END, new KeywordMatcher("END"));
		matchers.put(KWD_TRUE, new KeywordMatcher("TRUE"));
		matchers.put(KWD_FALSE, new KeywordMatcher("FALSE"));
		matchers.put(KWD_CAST, new KeywordMatcher("CAST"));
		matchers.put(KWD_SQL, new KeywordMatcher("SQL"));

		// 符号
		matchers.put(COMMA, new CommaMatcher()); // ,
		matchers.put(L_BRACKET, BracketMatcher.createLMatcher());
		matchers.put(R_BRACKET, BracketMatcher.createRMatcher());
		matchers.put(L_F, BracketMatcher.createLFMatcher());
		matchers.put(R_F, BracketMatcher.createRFMatcher());

		// 算数操作符
		matchers.put(MATH_OP_ADD_MINUS, MathOpMatcher.createAddMinusMatcher());
		matchers.put(MATH_OP_MULI_DIVIDE, MathOpMatcher.createMuliDivMatcher());
		//		matchers.put(MATH_OP_ADD, new MathOpMatcher());
		//		matchers.put(MATH_OP_MINUS, new MathOpMatcher());
		//		matchers.put(MATH_OP_MULI, new MathOpMatcher());
		//		matchers.put(MATH_OP_DIVIDE, new MathOpMatcher());

		// 算数比较
		matchers.put(MATH_CMP, new MathCmpMatcher());
		matchers.put(MATH_CMP_EQ, MathCmpMatcher.createEqMatcher());
		//		matchers.put(MATH_CMP_GT, new MathCmpMacher());
		//		matchers.put(MATH_CMP_LT, new MathCmpMacher());
		//		matchers.put(MATH_CMP_GE, new MathCmpMacher());
		//		matchers.put(MATH_CMP_LE, new MathCmpMacher());
		//		matchers.put(MATH_CMP_NE, new MathCmpMacher());

		//function
		matchers.put(FUNCTION, new FunctionMatcher());
		// relation
		matchers.put(RELATION, new RelationMatcher());
		matchers.put(RELA_ONE, RelationMatcher.createOneRelationMatcher());
		matchers.put(RELA_ALL, RelationMatcher.createAllRelationMatcher());

		// constant
		matchers.put(IDENTIFIER, new IdentiferMatcher());
		matchers.put(NUMBER, new NumberMatcher());
		matchers.put(INTEGER, NumberMatcher.createInteterMatcher());
		//		matchers.put(FLOAT, new NumberMacher());
		matchers.put(STRING, new StringMatcher());
		matchers.put(SQL_STRING_VARIABLE, new SQLStrVariableMatcher());
		matchers.put(VARIABLE, new VariableMatcher());

		matchers.put(EXP_IDENTIFIER, new ExpIdentiferMatcher());
		matchers.put(EXP_STRING, new ExpStringMatcher());
		matchers.put(EXP_TRUE, new WordMatcher("true", EXP_TRUE));
		matchers.put(EXP_FALSE, new WordMatcher("false", EXP_FALSE));
		matchers.put(EXP_AND, new WordMatcher("and", EXP_AND));
		matchers.put(EXP_OR, new WordMatcher("or", EXP_OR));
		matchers.put(EXP_NOT, new WordMatcher("not", EXP_NOT));
		matchers.put(EXP_NULL, new WordMatcher("null", EXP_NULL));
	}

	private Token matchToken(CharStream stream, boolean error, TokenKind kind) {
		Utilities.skipWhite(stream);
		stream.mark();
		Token tk = matchers.get(kind).match(stream);
		if (tk != null) {
			stream.unmark();
			return tk;
		}

		stream.back();
		if (error) {
			throw new SQLException("SQL语法错误, " + errMsg(stream) + "中未找到需要的元素, 位置" + stream.position() + "期望是" + kind);
		}
		return null;

	}

	private List<Token> matchToken(CharStream stream) {
		List<Token> ret = new ArrayList<Token>();
		Utilities.skipWhite(stream);
		Set<TokenKind> kindSet = matchers.keySet();
		boolean isHit = true;
		while (isHit && !stream.isEof()) {
			isHit = false;
			for (TokenKind kind : kindSet) {
				stream.mark();
				Token tk = matchers.get(kind).match(stream);
				if (tk != null) {
					stream.unmark();
					ret.add(tk);
					Utilities.skipWhite(stream);
					isHit = true;
					break;
				} else
					stream.back();
			}
		}
		if (!isHit) {
			throw new SQLException("SQL语法错误, 不支持的语法：" + stream.subString(stream.position(), stream.length()));
		}
		return ret;
	}

	public static String errMsg(CharStream stream) {
		int len = 15;
		int pos = stream.isEof() ? stream.length() : stream.position();
		int start = (pos > len) ? pos - len : 0;
		int end = pos + len;
		end = (end > stream.length()) ? stream.length() : end;
		return stream.subString(start, pos) + "^^" + stream.subString(pos, end);

	}

	/**
	 * 根据TokenKind，匹配一个Token。
	 * <p>
	 * <pre>
	 * 例子：
	 *     match(stream, false, KWD_LIKE);
	 * </pre>
	 * @param stream 字符流
	 * @param error  true表示匹配不到是报错，否则false。
	 * @param kinds  需要匹配的类型
	 * @return Token 匹配倒的Token对象。
	 */
	public static Token match(CharStream stream, boolean error, TokenKind kind) {
		return instance.matchToken(stream, error, kind);
	}

	public static boolean matched(CharStream stream, TokenKind kind) {
		return instance.matchToken(stream, false, kind) != null;
	}

	public static boolean matched(CharStream stream, boolean error, TokenKind kind) {
		return instance.matchToken(stream, error, kind) != null;
	}

	public static List<Token> match(CharStream stream) {
		return instance.matchToken(stream);
	}

	public static List<Token> match(String str) {
		if (null == str)
			str = "";
		CharStream stream = new CharStream(str);
		return instance.matchToken(stream);
	}

}
