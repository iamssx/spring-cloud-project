package com.ssx.pay.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface PayService {

    String pay(Long oid, HttpServletRequest request, HttpServletResponse response) throws Exception;

    boolean callbackNotify(HttpServletRequest request, HttpServletResponse response) throws Exception;

}
