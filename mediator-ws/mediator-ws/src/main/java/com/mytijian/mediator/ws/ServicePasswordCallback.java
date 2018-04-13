package com.mytijian.mediator.ws;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.wss4j.common.ext.WSPasswordCallback;

public class ServicePasswordCallback implements CallbackHandler {

	@Override
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		// TODO Auto-generated method stub
		WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];
		if(pc.getIdentifier().equals("serverprivatekey")){
			pc.setPassword("MyTiJianHis2018!%");
		}
	}

}
