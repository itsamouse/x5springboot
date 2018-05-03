package net.jingbo.x5springboot.baas.data.sql.matcher;


import net.jingbo.x5springboot.baas.data.sql.token.CharStream;
import net.jingbo.x5springboot.baas.data.sql.token.Token;

public abstract class TokenMatcher {

	public abstract Token match(CharStream stream);

}
