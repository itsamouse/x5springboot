package net.jingbo.x5springboot.baas.data.sql.matcher;


import net.jingbo.x5springboot.baas.data.sql.token.CharStream;
import net.jingbo.x5springboot.baas.data.sql.token.Token;
import net.jingbo.x5springboot.baas.data.sql.token.TokenKind;

public class CommaMatcher extends TokenMatcher{

	@Override
	public Token match(CharStream stream) {
		if(stream.isEof()) return null;
		if(stream.get() == ','){
			stream.next();
			return new Token(",", TokenKind.COMMA, stream.position()-1, stream.position());
		}
		return null;
	}
	

}
