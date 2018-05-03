package net.jingbo.x5springboot.baas.data.sql.matcher;

import net.jingbo.x5springboot.baas.data.sql.token.CharStream;
import net.jingbo.x5springboot.baas.data.sql.token.Token;
import net.jingbo.x5springboot.baas.data.sql.token.TokenKind;

public class IdentiferMatcher extends TokenMatcher{
	@Override
	public Token match(CharStream stream) {
		if(stream.isEof()) return null;
		int start = stream.position();
		String wd = stream.nextWord();
		
		if(wd != null){
			if(KeywordMatcher.keywords.containsKey(wd.toUpperCase()))
				return null;
			return new Token(wd,TokenKind.IDENTIFIER,start, stream.position());
		}
		return null;
	}

}
