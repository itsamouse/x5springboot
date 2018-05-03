package net.jingbo.x5springboot.baas;

import org.apache.log4j.Logger;

public class BaasException extends RuntimeException {
	protected static Logger logger = Logger.getLogger(BaasException.class);
	private static final long serialVersionUID = 4788254265308389054L;

	public BaasException(String msg){
		super(msg);
		logger.error(msg);
	}

	public BaasException(String msg, Exception exception){
		super(msg, exception);
		logger.error(msg, exception);
	}
}
