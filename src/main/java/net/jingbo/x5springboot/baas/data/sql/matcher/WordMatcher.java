package net.jingbo.x5springboot.baas.data.sql.matcher;

import net.jingbo.x5springboot.baas.Utils;
import net.jingbo.x5springboot.baas.data.sql.token.CharStream;
import net.jingbo.x5springboot.baas.data.sql.token.Token;
import net.jingbo.x5springboot.baas.data.sql.token.TokenKind;

public class WordMatcher extends TokenMatcher {
	private String word = null;
	private TokenKind kind = null;

	public WordMatcher(String wd, TokenKind knd) {
		Utils.check(wd != null && knd != null, "参数wd, knd不允许为空");
		word = wd;
		kind = knd;
	}

	@Override
	public Token match(CharStream stream) {
		if (stream.isEof())
			return null;
		int start = stream.position();
		String wd = stream.nextWord();
		if (wd == null)
			return null;
		if (word.equals(wd))
			return new Token(word, kind, start, stream.position());

		return null;
	}

}
