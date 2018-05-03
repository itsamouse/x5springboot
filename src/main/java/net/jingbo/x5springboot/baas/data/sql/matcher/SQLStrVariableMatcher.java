package net.jingbo.x5springboot.baas.data.sql.matcher;

import net.jingbo.x5springboot.baas.data.sql.token.CharStream;
import net.jingbo.x5springboot.baas.data.sql.token.Token;
import net.jingbo.x5springboot.baas.data.sql.token.TokenKind;

public class SQLStrVariableMatcher extends TokenMatcher {

	@Override
	public Token match(CharStream stream) {
		if (stream.isEof())
			return null;
		if (stream.get() == ':') {
			int start = stream.position();
			if (!stream.next())
				return null;
			if (stream.get() == ':'){
				stream.next();
				String wd = stream.nextWord();
				if (wd != null) {
					return new Token(wd, TokenKind.SQL_STRING_VARIABLE, start, stream.position());
				}
			}
		}
		return null;
	}

}
