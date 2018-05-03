package net.jingbo.x5springboot.baas.data.sql;


import net.jingbo.x5springboot.baas.data.sql.token.Token;
import net.jingbo.x5springboot.baas.data.sql.token.TokenKind;
import net.jingbo.x5springboot.baas.data.sql.token.TokenManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SQLStruct {
	private String SQL;
	private List<Token> tokens;
	private boolean isMatch = false;
	
	public SQLStruct(String sql){
		this.SQL = sql;
	}
	
	public List<Object> getBinds(Map<String, Object> varsMap){
		if(null!=varsMap){
			List<Object> ret = new ArrayList<Object>();
			List<Token> tokens = getTokens();
			for(Token token : tokens){
				if(TokenKind.VARIABLE == token.getKind()){
					String name = token.getImage();
					if(varsMap.containsKey(name)) ret.add(varsMap.get(name));
					else ret.add(null);//没有变量默认给null
				}
			}
			return ret;
		}
		return null;
	}
	
	public String getSQL(){
		return getSQL(null);
	}
	
	public String getSQL(Map<String,?> params){
		StringBuffer ret = new StringBuffer();
		List<Token> tokens = getTokens();
		for(Token token : tokens){
			TokenKind tokenKind = token.getKind();
			if(TokenKind.EOF != tokenKind){
				if(TokenKind.STRING == tokenKind){
					ret.append("'");
					ret.append(token.getImage()); 
					ret.append("' ");
				}else if(TokenKind.VARIABLE == tokenKind){
					ret.append("?");
					ret.append(" ");
				}else if(TokenKind.SQL_STRING_VARIABLE == tokenKind){
					if(null!=params){
						String wd = token.getImage();
						Object value = params.containsKey(wd)?params.get(wd):"";
						if(null!=value){
							ret.append(value.toString());
							ret.append(" ");
						}else ret.append("");
					}else ret.append("");
				}else if(TokenKind.L_BRACKET == tokenKind){
					ret.append(token.getImage());
				}else{
					ret.append(token.getImage());
					ret.append(" ");
				}
			}
		}
		return ret.toString();
	}
	
	private List<Token> getTokens(){
		if(!isMatch){
			tokens = TokenManager.match(SQL);
			isMatch = true;
		}
		return tokens;
	}

}
