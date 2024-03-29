package net.jingbo.x5springboot.baas.data.sql.matcher;


import net.jingbo.x5springboot.baas.data.sql.token.CharStream;
import net.jingbo.x5springboot.baas.data.sql.token.Token;
import net.jingbo.x5springboot.baas.data.sql.token.TokenKind;

public class EOFMatcher extends TokenMatcher {

	@Override
	public Token match(CharStream stream) {
		if(stream.isEof())
			return new Token(null, TokenKind.EOF,stream.position()+1, stream.position() +1);
		return null;
	}

}
